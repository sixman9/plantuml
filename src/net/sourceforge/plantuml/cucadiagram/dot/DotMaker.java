/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009, Arnaud Roques
 *
 * Project Info:  http://plantuml.sourceforge.net
 * 
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * Original Author:  Arnaud Roques
 * 
 * Revision $Revision: 5334 $
 *
 */
package net.sourceforge.plantuml.cucadiagram.dot;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.SignatureUtils;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.cucadiagram.Member;
import net.sourceforge.plantuml.cucadiagram.Entity;
import net.sourceforge.plantuml.cucadiagram.EntityPortion;
import net.sourceforge.plantuml.cucadiagram.EntityType;
import net.sourceforge.plantuml.cucadiagram.Group;
import net.sourceforge.plantuml.cucadiagram.GroupType;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.Rankdir;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.skin.rose.Rose;

final public class DotMaker implements GraphvizMaker {

	private final DotData data;

	private static boolean isJunit = false;

	private final List<String> dotStrings;
	private boolean underline = false;
	private final Rose rose = new Rose();

	private static String lastDotSignature;

	private final boolean isEps;

	private final boolean isVisibilityModifierPresent;

	public static void goJunit() {
		isJunit = true;
	}

	public DotMaker(DotData data, List<String> dotStrings, boolean isEps) {
		this.data = data;
		this.dotStrings = dotStrings;
		this.isEps = isEps;
		if (data.getSkinParam().classAttributeIconSize() > 0) {
			this.isVisibilityModifierPresent = data.getVisibilityImages().size() > 0;
		} else {
			this.isVisibilityModifierPresent = false;
		}
	}

	public String createDotString() throws IOException {

		final StringBuilder sb = new StringBuilder();

		initPrintWriter(sb);
		printGroups(sb, null);
		printEntities(sb, getUnpackagedEntities());
		printLinks(sb, data.getLinks());
		sb.append("}");

		// System.err.println(sb);
		if (isJunit) {
			lastDotSignature = SignatureUtils.getSignatureWithoutImgSrc(sb.toString());
		}
		return sb.toString();
	}

	private void initPrintWriter(StringBuilder sb) {

		sb.append("digraph unix {");
		if (isJunit == false) {
			for (String s : dotStrings) {
				sb.append(s);
			}
		}
		sb.append("bgcolor=\"" + data.getSkinParam().getBackgroundColor().getAsHtml() + "\";");
		sb.append("ratio=auto;");
		sb.append("compound=true;");
		sb.append("remincross=true;");
		// sb.append("concentrate=true;");
		// pw.println("size=\"40,40;\"");
		sb.append("searchsize=500;");
		if (data.getRankdir() == Rankdir.LEFT_TO_RIGHT) {
			sb.append("rankdir=LR;");
		}
	}

	private Collection<IEntity> getUnpackagedEntities() {
		final List<IEntity> result = new ArrayList<IEntity>();
		for (IEntity ent : data.getEntities().values()) {
			if (ent.getParent() == data.getTopParent()) {
				result.add(ent);
			}
		}
		return result;
	}

	private void printGroups(StringBuilder sb, Group parent) throws IOException {
		for (Group g : data.getGroupHierarchy().getChildrenGroups(parent)) {
			if (data.isEmpty(g) && g.getType() == GroupType.PACKAGE) {
				final IEntity folder = new Entity(g.getUid(), g.getCode(), g.getDisplay(), EntityType.EMPTY_PACKAGE,
						null);
				printEntity(sb, folder);
			} else {
				printGroup(sb, g);
			}
		}
	}

	private void printGroup(StringBuilder sb, Group g) throws IOException {
		if (g.getType() == GroupType.CONCURRENT_STATE) {
			return;
		}

		if (isSpecialGroup(g)) {
			printGroupSpecial(sb, g);
		} else {
			printGroupNormal(sb, g);
		}
	}

	private void printGroupNormal(StringBuilder sb, Group g) throws IOException {

		sb.append("subgraph " + g.getUid() + " {");
		// sb.append("margin=10;");

		sb.append("fontsize=\"" + data.getSkinParam().getFontSize(getFontParamForGroup()) + "\";");
		final String fontFamily = data.getSkinParam().getFontFamily(getFontParamForGroup());
		if (fontFamily != null) {
			sb.append("fontname=\"" + fontFamily + "\";");
		}

		if (g.getDisplay() != null) {
			sb.append("label=<" + manageHtmlIB(g.getDisplay(), getFontParamForGroup()) + ">;");
		}
		final String fontColor = data.getSkinParam().getFontHtmlColor(getFontParamForGroup()).getAsHtml();
		sb.append("fontcolor=\"" + fontColor + "\";");
		if (getGroupBackColor(g) != null) {
			sb.append("fillcolor=\"" + getGroupBackColor(g).getAsHtml() + "\";");
		}
		if (g.getType() == GroupType.STATE) {
			sb.append("color=" + getColorString(ColorParam.stateBorder) + ";");
		} else {
			sb.append("color=" + getColorString(ColorParam.packageBorder) + ";");
		}
		sb.append("style=\"" + getStyle(g) + "\";");

		printGroups(sb, g);

		this.printEntities(sb, g.entities().values());
		for (Link link : data.getLinks()) {
			eventuallySameRank(sb, g, link);
		}
		sb.append("}");
	}

	private HtmlColor getGroupBackColor(Group g) {
		HtmlColor value = g.getBackColor();
		if (value == null) {
			value = data.getSkinParam().getHtmlColor(ColorParam.packageBackground);
			// value = rose.getHtmlColor(this.data.getSkinParam(),
			// ColorParam.packageBackground);
		}
		return value;
	}

	private void printGroupSpecial(StringBuilder sb, Group g) throws IOException {

		sb.append("subgraph " + g.getUid() + "a {");
		if (OptionFlags.getInstance().isDebugDot()) {
			sb.append("style=dotted;");
			sb.append("label=\"a\";");
		} else {
			sb.append("style=invis;");
			sb.append("label=\"\";");
		}

		sb.append("subgraph " + g.getUid() + "v {");
		sb.append("style=solid;");
		// sb.append("margin=10;");

		final List<Link> autolinks = data.getAutoLinks(g);
		final List<Link> toEdgeLinks = data.getToEdgeLinks(g);
		final List<Link> fromEdgeLinks = data.getFromEdgeLinks(g);
		final boolean autoLabel = autolinks.size() == 1;

		final List<Link> nodesHiddenUidOut = getNodesHiddenUidOut(g);
		final List<Link> nodesHiddenUidIn = getNodesHiddenUidIn(g);
		final List<Link> nodesHiddenUid = new ArrayList<Link>(nodesHiddenUidOut);
		nodesHiddenUid.addAll(nodesHiddenUidIn);
		for (Link link : nodesHiddenUid) {
			final String uid = getHiddenNodeUid(g, link);
			// sb.append("subgraph " + g.getUid() + "k" + uid + " {");
			if (OptionFlags.getInstance().isDebugDot()) {
				sb.append("style=dotted;");
				sb.append("label=\"k" + uid + "\";");
			} else {
				sb.append("style=invis;");
				sb.append("label=\"\";");
			}
			if (OptionFlags.getInstance().isDebugDot()) {
				sb.append(uid + ";");
			} else {
				sb.append(uid + " [shape=point,width=.01,style=invis,label=\"\"];");
			}
			// sb.append("}"); // end of k
		}

		for (int j = 1; j < nodesHiddenUidOut.size(); j++) {
			for (int i = 0; i < j; i++) {
				final Link linki = nodesHiddenUidOut.get(i);
				final Link linkj = nodesHiddenUidOut.get(j);
				if (linki.getEntity2() != linkj.getEntity2()) {
					continue;
				}
				final String uidi = getHiddenNodeUid(g, linki);
				final String uidj = getHiddenNodeUid(g, linkj);
				if (OptionFlags.getInstance().isDebugDot()) {
					sb.append(uidi + "->" + uidj + ";");
				} else {
					sb.append(uidi + "->" + uidj + " [style=invis,arrowtail=none,arrowhead=none];");
				}

			}
		}

		if (autoLabel /* || toEdgeLinks.size() > 0 || fromEdgeLinks.size() > 0 */) {
			if (OptionFlags.getInstance().isDebugDot()) {
				sb.append(g.getUid() + "lmin;");
				sb.append(g.getUid() + "lmax;");
				sb.append(g.getUid() + "lmin->" + g.getUid() + "lmax [minlen=2]; ");
			} else {
				sb.append(g.getUid() + "lmin [shape=point,width=.01,style=invis,label=\"\"];");
				sb.append(g.getUid() + "lmax [shape=point,width=.01,style=invis,label=\"\"];");
				sb.append(g.getUid() + "lmin->" + g.getUid()
						+ "lmax [minlen=2,style=invis,arrowtail=none,arrowhead=none]; ");
			}
		}
		// sb.append(g.getUid() + "min->" + g.getUid() + "max;");

		sb.append("fontsize=\"" + data.getSkinParam().getFontSize(getFontParamForGroup()) + "\";");
		final String fontFamily = data.getSkinParam().getFontFamily(getFontParamForGroup());
		if (fontFamily != null) {
			sb.append("fontname=\"" + fontFamily + "\";");
		}

		if (g.getDisplay() != null) {
			final StringBuilder label = new StringBuilder(manageHtmlIB(g.getDisplay(), getFontParamForGroup()));
			if (g.getEntityCluster().fields2().size() > 0) {
				label.append("<BR ALIGN=\"LEFT\"/>");
				for (Member att : g.getEntityCluster().fields2()) {
					label.append(manageHtmlIB("  " + att.getDisplayWithVisibilityChar() + "  ",
							FontParam.STATE_ATTRIBUTE));
					label.append("<BR ALIGN=\"LEFT\"/>");
				}
			}
			sb.append("label=<" + label + ">;");
		}

		final String fontColor = data.getSkinParam().getFontHtmlColor(getFontParamForGroup()).getAsHtml();
		sb.append("fontcolor=\"" + fontColor + "\";");
		if (getGroupBackColor(g) != null) {
			sb.append("fillcolor=\"" + getGroupBackColor(g).getAsHtml() + "\";");
		}
		if (g.getType() == GroupType.STATE) {
			sb.append("color=" + getColorString(ColorParam.stateBorder) + ";");
		} else {
			sb.append("color=" + getColorString(ColorParam.packageBorder) + ";");
		}
		sb.append("style=\"" + getStyle(g) + "\";");

		sb.append("subgraph " + g.getUid() + "i {");
		sb.append("label=\"i\";");
		if (OptionFlags.getInstance().isDebugDot()) {
			sb.append("style=dotted;");
			sb.append("label=\"i\";");
		} else {
			sb.append("style=invis;");
			sb.append("label=\"\";");
		}

		printGroups(sb, g);

		this.printEntities(sb, g.entities().values());
		for (Link link : data.getLinks()) {
			eventuallySameRank(sb, g, link);
		}

		for (int i = 0; i < fromEdgeLinks.size(); i++) {
			if (OptionFlags.getInstance().isDebugDot()) {
				sb.append("eds" + i + ";");
			} else {
				sb.append("eds" + i + " [shape=point,width=.01,style=invis,label=\"\"];");
			}
			sb.append("eds" + i + " ->" + fromEdgeLinks.get(i).getEntity2().getUid()
					+ " [minlen=2,style=invis,arrowtail=none,arrowhead=none]; ");

		}

		sb.append("}"); // end of i
		sb.append("}"); // end of v

		if (autoLabel) {
			sb.append("subgraph " + g.getUid() + "l {");
			if (OptionFlags.getInstance().isDebugDot()) {
				sb.append("style=dotted;");
				sb.append("label=\"l\";");
			} else {
				sb.append("style=invis;");
				sb.append("label=\"\";");
			}
			final String decorationColor = ",color=" + getColorString(getArrowColorParam());

			sb.append(g.getUid() + "lab0 [shape=point,width=.01,label=\"\"" + decorationColor + "]");
			String autolabel = autolinks.get(0).getLabel();
			if (autolabel == null) {
				autolabel = "";
			}
			sb.append(g.getUid() + "lab1 [label=<" + manageHtmlIB(autolabel, getArrowFontParam())
					+ ">,shape=plaintext,margin=0];");
			sb.append(g.getUid() + "lab0 -> " + g.getUid() + "lab1 [minlen=0,style=invis];");
			sb.append("}"); // end of l

			sb.append(g.getUid() + "lmin -> " + g.getUid() + "lab0 [ltail=" + g.getUid()
					+ "v,arrowtail=none,arrowhead=none" + decorationColor + "];");
			sb.append(g.getUid() + "lab0 -> " + g.getUid() + "lmax [lhead=" + g.getUid()
					+ "v,arrowtail=none,arrowhead=open" + decorationColor + "];");
		}

		for (int i = 0; i < fromEdgeLinks.size(); i++) {
			sb.append("subgraph " + g.getUid() + "ed" + i + " {");
			if (OptionFlags.getInstance().isDebugDot()) {
				sb.append("style=dotted;");
				sb.append("label=\"ed" + i + "\";");
			} else {
				sb.append("style=invis;");
				sb.append("label=\"\";");
			}
			final String decorationColor = ",color=" + getColorString(getArrowColorParam());
			String label = fromEdgeLinks.get(i).getLabel();
			if (label == null) {
				label = "";
			}

			sb.append(g.getUid() + "fedge" + i + " [shape=point,width=.01,label=\"\"" + decorationColor + "]");
			sb.append("}"); // end of ed
			sb.append("eds" + i + " -> " + g.getUid() + "fedge" + i + " [ltail=" + g.getUid()
					+ "v,arrowtail=none,arrowhead=none" + decorationColor + "];");
			sb.append(g.getUid() + "fedge" + i + " -> " + fromEdgeLinks.get(i).getEntity2().getUid()
					+ "[arrowtail=none,arrowhead=open" + decorationColor);
			sb.append(",label=<" + manageHtmlIB(label, getArrowFontParam()) + ">];");

		}
		sb.append("}"); // end of a
	}

	private FontParam getFontParamForGroup() {
		if (data.getUmlDiagramType() == UmlDiagramType.STATE) {
			return FontParam.STATE;
		}
		return FontParam.PACKAGE;
	}

	private String getStyle(Group g) {
		final StringBuilder sb = new StringBuilder();
		if (g.isBold()) {
			sb.append("bold");
		} else if (g.isDashed()) {
			sb.append("dashed");
		} else {
			sb.append("solid");

		}
		if (getGroupBackColor(g) != null) {
			sb.append(",filled");
		}
		if (g.isRounded()) {
			sb.append(",rounded");
		}
		return sb.toString();
	}

	private void printLinks(StringBuilder sb, List<Link> links) throws IOException {
		for (Link link : appendPhantomLink(links)) {
			final IEntity entity1 = link.getEntity1();
			final IEntity entity2 = link.getEntity2();
			if (entity1 == entity2 && entity1.getType() == EntityType.GROUP) {
				continue;
			}
			if (entity1.getType() == EntityType.GROUP && entity2.getParent() == entity1.getParent()) {
				continue;
			}
			if (entity2.getType() == EntityType.GROUP && entity1.getParent() == entity2.getParent()) {
				continue;
			}
			if (entity1.getType() == EntityType.LOLLIPOP || entity2.getType() == EntityType.LOLLIPOP) {
				continue;
			}
			// System.err.println("outing " + link);
			printLink(sb, link);
		}
	}

	private void printLink(StringBuilder sb, Link link) throws IOException {
		final StringBuilder decoration = getLinkDecoration();

		if (link.getWeight() > 1) {
			decoration.append("weight=" + link.getWeight() + ",");
		}
		if (link.getLabeldistance() != null) {
			decoration.append("labeldistance=" + link.getLabeldistance() + ",");
		}
		if (link.getLabelangle() != null) {
			decoration.append("labelangle=" + link.getLabelangle() + ",");
		}

		final DrawFile noteLink = link.getImageFile();

		if (link.getLabel() != null) {
			decoration.append("label=<" + manageHtmlIB(link.getLabel(), getArrowFontParam()) + ">,");
		} else if (noteLink != null) {
			decoration.append("label=<" + getHtmlForLinkNote(noteLink.getPngOrEps(isEps)) + ">,");
		}

		if (link.getQualifier1() != null) {
			decoration.append("taillabel=<" + manageHtmlIB(link.getQualifier1(), getArrowFontParam()) + ">,");
		}
		if (link.getQualifier2() != null) {
			decoration.append("headlabel=<" + manageHtmlIB(link.getQualifier2(), getArrowFontParam()) + ">,");
		}
		decoration.append(link.getType().getSpecificDecoration());
		if (link.isInvis()) {
			decoration.append(",style=invis");
		}

		final int len = link.getLength();
		final String lenString = len >= 3 ? ",minlen=" + (len - 1) : "";

		String uid1 = link.getEntity1().getUid();
		String uid2 = link.getEntity2().getUid();
		if (link.getEntity1().getType() == EntityType.GROUP) {
			uid1 = getHiddenNodeUid(link.getEntity1().getParent(), link);
			decoration.append(",ltail=" + link.getEntity1().getParent().getUid() + "v");
		}
		if (link.getEntity2().getType() == EntityType.GROUP) {
			uid2 = getHiddenNodeUid(link.getEntity2().getParent(), link);
			decoration.append(",lhead=" + link.getEntity2().getParent().getUid() + "v");
		}

		sb.append(uid1 + " -> " + uid2);
		sb.append(decoration);
		sb.append(lenString + "];");
		eventuallySameRank(sb, data.getTopParent(), link);
	}

	private List<Link> getNodesHiddenUidOut(Group g) {
		final List<Link> result = new ArrayList<Link>();
		for (Link link : data.getLinks()) {
			if (link.getEntity1().getParent() == link.getEntity2().getParent()) {
				continue;
			}
			if (link.getEntity1().getType() == EntityType.GROUP && link.getEntity1().getParent() == g) {
				result.add(link);
			}
		}
		return Collections.unmodifiableList(result);
	}

	private List<Link> getNodesHiddenUidIn(Group g) {
		final List<Link> result = new ArrayList<Link>();
		for (Link link : data.getLinks()) {
			if (link.getEntity1().getParent() == link.getEntity2().getParent()) {
				continue;
			}
			if (link.getEntity2().getType() == EntityType.GROUP && link.getEntity2().getParent() == g) {
				result.add(link);
			}
		}
		return Collections.unmodifiableList(result);
	}

	private String getHiddenNodeUid(Group g, Link link) {
		if (data.isEmpty(g) && g.getType() == GroupType.PACKAGE) {
			return g.getUid();
		}
		return g.getUid() + "_" + link.getUid();
	}

	private StringBuilder getLinkDecoration() {
		final StringBuilder decoration = new StringBuilder("[color=" + getColorString(getArrowColorParam()) + ",");

		decoration.append("fontcolor=" + getFontColorString(getArrowFontParam()) + ",");
		decoration.append("fontsize=\"" + data.getSkinParam().getFontSize(getArrowFontParam()) + "\",");

		final String fontName = data.getSkinParam().getFontFamily(getArrowFontParam());
		if (fontName != null) {
			decoration.append("fontname=\"" + fontName + "\",");
		}
		return decoration;
	}

	private List<Link> appendPhantomLink(List<Link> links) {
		final List<Link> result = new ArrayList<Link>(links);
		for (Link link : links) {
			if (link.getLength() != 1) {
				continue;
			}
			final DrawFile noteLink = link.getImageFile();
			if (noteLink == null) {
				continue;
			}
			final Link phantom = new Link(link.getEntity1(), link.getEntity2(), link.getType(), "", link.getLength());
			phantom.setInvis(true);
			result.add(phantom);
		}
		return result;
	}

	private String getHtmlForLinkNote(File image) {
		final String circleInterfaceAbsolutePath = StringUtils.getPlateformDependentAbsolutePath(image);
		final StringBuilder sb = new StringBuilder("<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"0\">");
		sb.append("<TR><TD><IMG SRC=\"" + circleInterfaceAbsolutePath + "\"/></TD></TR>");
		sb.append("</TABLE>");
		return sb.toString();

	}

	private FontParam getArrowFontParam() {
		if (data.getUmlDiagramType() == UmlDiagramType.CLASS) {
			return FontParam.CLASS_ARROW;
		} else if (data.getUmlDiagramType() == UmlDiagramType.OBJECT) {
			return FontParam.OBJECT_ARROW;
		} else if (data.getUmlDiagramType() == UmlDiagramType.USECASE) {
			return FontParam.USECASE_ARROW;
		} else if (data.getUmlDiagramType() == UmlDiagramType.ACTIVITY) {
			return FontParam.ACTIVITY_ARROW;
		} else if (data.getUmlDiagramType() == UmlDiagramType.COMPONENT) {
			return FontParam.COMPONENT_ARROW;
		} else if (data.getUmlDiagramType() == UmlDiagramType.STATE) {
			return FontParam.STATE_ARROW;
		}
		throw new IllegalStateException();
	}

	private ColorParam getArrowColorParam() {
		if (data.getUmlDiagramType() == UmlDiagramType.CLASS) {
			return ColorParam.classArrow;
		} else if (data.getUmlDiagramType() == UmlDiagramType.OBJECT) {
			return ColorParam.objectArrow;
		} else if (data.getUmlDiagramType() == UmlDiagramType.USECASE) {
			return ColorParam.usecaseArrow;
		} else if (data.getUmlDiagramType() == UmlDiagramType.ACTIVITY) {
			return ColorParam.activityArrow;
		} else if (data.getUmlDiagramType() == UmlDiagramType.COMPONENT) {
			return ColorParam.componentArrow;
		} else if (data.getUmlDiagramType() == UmlDiagramType.STATE) {
			return ColorParam.stateArrow;
		}
		throw new IllegalStateException();
	}

	private String getColorString(ColorParam colorParam) {
		return "\"" + rose.getHtmlColor(data.getSkinParam(), colorParam).getAsHtml() + "\"";
	}

	private String getFontColorString(FontParam fontParam) {
		return "\"" + getFontHtmlColor(fontParam).getAsHtml() + "\"";
	}

	private HtmlColor getFontHtmlColor(FontParam fontParam) {
		return data.getSkinParam().getFontHtmlColor(fontParam);
	}

	private void eventuallySameRank(StringBuilder sb, Group entityPackage, Link link) {
		if (workAroundDotBug()) {
			return;
		}
		final int len = link.getLength();
		if (len == 1 && link.getEntity1().getParent() == entityPackage
				&& link.getEntity2().getParent() == entityPackage) {
			if (link.getEntity1().getType() == EntityType.GROUP) {
				throw new IllegalArgumentException();
			}
			if (link.getEntity2().getType() == EntityType.GROUP) {
				throw new IllegalArgumentException();
			}
			sb.append("{rank=same; " + link.getEntity1().getUid() + "; " + link.getEntity2().getUid() + "}");
		}
	}

	private void printEntities(StringBuilder sb, Collection<? extends IEntity> entities) throws IOException {
		final Set<IEntity> lollipops = new HashSet<IEntity>();
		final Set<IEntity> lollipopsFriends = new HashSet<IEntity>();
		for (IEntity entity : entities) {
			if (entity.getType() == EntityType.LOLLIPOP) {
				lollipops.add(entity);
				lollipopsFriends.add(getConnectedToLollipop(entity));
			}
		}
		for (IEntity entity : entities) {
			if (lollipops.contains(entity) || lollipopsFriends.contains(entity)) {
				continue;
			}
			printEntity(sb, entity);
		}

		for (IEntity ent : lollipopsFriends) {
			sb.append("subgraph cluster" + ent.getUid() + "lol {");
			sb.append("style=invis;");
			sb.append("label=\"\";");
			printEntity(sb, ent);
			for (IEntity lollipop : getAllLollipop(ent)) {
				final Link link = getLinkLollipop(lollipop, ent);
				final String headOrTail = getHeadOrTail(lollipop, link);
				printEntity(sb, lollipop, headOrTail);
				printLink(sb, link);
			}
			sb.append("}");
		}

	}

	private Collection<IEntity> getAllLollipop(IEntity entity) {
		final Collection<IEntity> result = new ArrayList<IEntity>();
		for (IEntity lollipop : data.getAllLinkedDirectedTo(entity)) {
			if (lollipop.getType() == EntityType.LOLLIPOP) {
				result.add(lollipop);
			}
		}
		return result;
	}

	private IEntity getConnectedToLollipop(IEntity lollipop) {
		assert lollipop.getType() == EntityType.LOLLIPOP;
		final Collection<IEntity> linked = data.getAllLinkedDirectedTo(lollipop);
		if (linked.size() != 1) {
			throw new IllegalStateException("size=" + linked.size());
		}
		return linked.iterator().next();
	}

	private Link getLinkLollipop(IEntity lollipop, IEntity ent) {
		assert lollipop.getType() == EntityType.LOLLIPOP;
		for (Link link : data.getLinks()) {
			if (link.isBetween(lollipop, ent)) {
				return link;
			}
		}
		throw new IllegalArgumentException();
	}

	private void printEntity(StringBuilder sb, IEntity entity, String headOrTail) throws IOException {
		final EntityType type = entity.getType();
		if (type == EntityType.LOLLIPOP) {
			final String color1 = getColorString(ColorParam.classBackground);
			final String color2 = getColorString(ColorParam.classBorder);
			final String colorBack = getColorString(ColorParam.background);
			final String labelLo = manageHtmlIB(entity.getDisplay(), FontParam.CLASS_ATTRIBUTE);
			sb.append(entity.getUid() + " [fillcolor=" + color1 + ",color=" + color2 + ",style=\"filled\","
					+ "shape=circle,width=0.12,height=0.12,label=\"\"];");
			sb.append(entity.getUid() + " -> " + entity.getUid() + "[color=" + colorBack
					+ ",arrowtail=none,arrowhead=none," + headOrTail + "=<" + labelLo + ">];");
		} else {
			throw new IllegalStateException(type.toString() + " " + data.getUmlDiagramType());
		}

	}

	private void printEntity(StringBuilder sb, IEntity entity) throws IOException {
		final EntityType type = entity.getType();
		final String label = getLabel(entity);
		if (type == EntityType.GROUP) {
			return;
		}
		if (type == EntityType.ABSTRACT_CLASS || type == EntityType.CLASS || type == EntityType.INTERFACE
				|| type == EntityType.ENUM) {
			String dec = " [fontcolor=" + getFontColorString(FontParam.CLASS) + ",margin=0,fillcolor="
					+ getColorString(ColorParam.classBackground) + ",color=" + getColorString(ColorParam.classBorder)
					+ ",style=filled,shape=box," + label;
			if (this.data.hasUrl() && entity.getUrl() != null) {
				dec += ",URL=\"" + entity.getUrl() + "\"";
			}
			dec += "];";
			sb.append(entity.getUid() + dec);
		} else if (type == EntityType.OBJECT) {
			sb.append(entity.getUid() + " [fontcolor=" + getFontColorString(FontParam.CLASS) + ",margin=0,fillcolor="
					+ getColorString(ColorParam.classBackground) + ",color=" + getColorString(ColorParam.classBorder)
					+ ",style=filled,shape=record," + label + "];");
		} else if (type == EntityType.USECASE) {
			sb.append(entity.getUid() + " [fontcolor=" + getFontColorString(FontParam.USECASE) + ",fillcolor="
					+ getColorString(ColorParam.usecaseBackground) + ",color="
					+ getColorString(ColorParam.usecaseBorder) + ",style=filled," + label + "];");
		} else if (type == EntityType.ACTOR) {
			sb.append(entity.getUid() + " [fontcolor=" + getFontColorString(FontParam.USECASE_ACTOR)
					+ ",margin=0,shape=plaintext," + label + "];");
		} else if (type == EntityType.CIRCLE_INTERFACE) {
			sb.append(entity.getUid() + " [margin=0,shape=plaintext," + label + "];");
		} else if (type == EntityType.COMPONENT) {
			sb.append(entity.getUid() + " [margin=0.2,fontcolor=" + getFontColorString(FontParam.COMPONENT)
					+ ",fillcolor=" + getColorString(ColorParam.componentBackground) + ",color="
					+ getColorString(ColorParam.componentBorder) + ",style=filled,shape=component," + label + "];");
		} else if (type == EntityType.NOTE) {
			final DrawFile file = entity.getImageFile();
			if (file == null) {
				// sb.append(entity.getUid() + ";");
				// Log.error("Warning : no file for NOTE");
				// return;
				throw new IllegalStateException("No file for NOTE");
			}
			if (file.getPngOrEps(isEps).exists() == false) {
				throw new IllegalStateException();
			}
			final String absolutePath = StringUtils.getPlateformDependentAbsolutePath(file.getPngOrEps(isEps));
			sb.append(entity.getUid() + " [margin=0,pad=0," + label + ",shape=none,image=\"" + absolutePath + "\"];");
		} else if (type == EntityType.ACTIVITY) {
			String shape = "octagon";
			if (entity.getImageFile() != null) {
				shape = "rect";
			}
			sb.append(entity.getUid() + " [fontcolor=" + getFontColorString(FontParam.ACTIVITY) + ",fillcolor="
					+ getColorString(ColorParam.activityBackground) + ",color="
					+ getColorString(ColorParam.activityBorder) + ",style=\"rounded,filled\",shape=" + shape + ","
					+ label + "];");
		} else if (type == EntityType.BRANCH) {
			sb.append(entity.getUid() + " [fillcolor=" + getColorString(ColorParam.activityBackground) + ",color="
					+ getColorString(ColorParam.activityBorder)
					+ ",style=\"filled\",shape=diamond,height=.25,width=.25,label=\"\"];");
			// if (StringUtils.isNotEmpty(entity.getDisplay())) {
			// sb.append(entity.getUid() + "->" + entity.getUid() +
			// "[taillabel=\"" + entity.getDisplay()
			// + "\",arrowtail=none,arrowhead=none,color=\"white\"];");
			// }
		} else if (type == EntityType.SYNCHRO_BAR) {
			final String color = getColorString(ColorParam.activityBar);
			sb.append(entity.getUid() + " [fillcolor=" + color + ",color=" + color + ",style=\"filled\","
					+ "shape=rect,height=.08,width=1.30,label=\"\"];");
		} else if (type == EntityType.CIRCLE_START) {
			final String color = getColorString(ColorParam.activityStart);
			sb.append(entity.getUid() + " [fillcolor=" + color + ",color=" + color + ",style=\"filled\","
					+ "shape=circle,width=.20,label=\"\"];");
		} else if (type == EntityType.CIRCLE_END) {
			final String color = getColorString(ColorParam.activityEnd);
			sb.append(entity.getUid() + " [fillcolor=" + color + ",color=" + color + ",style=\"filled\","
					+ "shape=doublecircle,width=.13,label=\"\"];");
		} else if (type == EntityType.POINT_FOR_ASSOCIATION) {
			sb.append(entity.getUid() + " [width=.05,shape=point,color=" + getColorString(ColorParam.classBorder)
					+ "];");
		} else if (type == EntityType.STATE) {
			sb.append(entity.getUid() + " [color=" + getColorString(ColorParam.stateBorder)
					+ ",shape=record,style=\"rounded,filled\",color=" + getColorString(ColorParam.stateBorder));
			if (entity.getImageFile() == null) {
				sb.append(",fillcolor=" + getColorString(ColorParam.stateBackground));
			} else {
				sb.append(",fillcolor=" + getColorString(ColorParam.stateBackground));
				// sb.append(",fillcolor=\"" +
				// data.getSkinParam().getBackgroundColor().getAsHtml() + "\"");
			}
			sb.append("," + label + "];");
		} else if (type == EntityType.STATE_CONCURRENT) {
			final DrawFile file = entity.getImageFile();
			if (file == null) {
				throw new IllegalStateException();
			}
			if (file.getPng().exists() == false) {
				throw new IllegalStateException();
			}
			final String absolutePath = StringUtils.getPlateformDependentAbsolutePath(file.getPng());
			sb.append(entity.getUid() + " [margin=1,pad=1," + label + ",style=dashed,shape=box,image=\"" + absolutePath
					+ "\"];");
		} else if (type == EntityType.ACTIVITY_CONCURRENT) {
			final DrawFile file = entity.getImageFile();
			if (file == null) {
				throw new IllegalStateException();
			}
			if (file.getPng().exists() == false) {
				throw new IllegalStateException();
			}
			final String absolutePath = StringUtils.getPlateformDependentAbsolutePath(file.getPng());
			sb.append(entity.getUid() + " [margin=0,pad=0," + label + ",style=dashed,shape=box,image=\"" + absolutePath
					+ "\"];");
		} else if (type == EntityType.EMPTY_PACKAGE) {
			sb.append(entity.getUid() + " [margin=0.2,fontcolor=" + getFontColorString(FontParam.PACKAGE)
					+ ",fillcolor=" + getColorString(ColorParam.packageBackground) + ",color="
					+ getColorString(ColorParam.packageBorder) + ",style=filled,shape=tab," + label + "];");
		} else {
			throw new IllegalStateException(type.toString() + " " + data.getUmlDiagramType());
		}
	}

	private String getHeadOrTail(IEntity lollipop, Link link) {
		assert lollipop.getType() == EntityType.LOLLIPOP;
		if (link.getLength() > 1 && link.getEntity1() == lollipop) {
			return "taillabel";
		}
		return "headlabel";
	}

	private String getLabel(IEntity entity) throws IOException {
		if (entity.getType() == EntityType.ABSTRACT_CLASS || entity.getType() == EntityType.CLASS
				|| entity.getType() == EntityType.INTERFACE || entity.getType() == EntityType.ENUM) {
			return "label=" + getLabelForClassOrInterfaceOrEnum(entity);
		} else if (entity.getType() == EntityType.LOLLIPOP) {
			return "label=" + getLabelForLollipop(entity);
		} else if (entity.getType() == EntityType.OBJECT) {
			return "label=" + getLabelForObject(entity);
		} else if (entity.getType() == EntityType.ACTOR) {
			return "label=" + getLabelForActor(entity);
		} else if (entity.getType() == EntityType.CIRCLE_INTERFACE) {
			return "label=" + getLabelForCircleInterface(entity);
		} else if (entity.getType() == EntityType.NOTE) {
			return "label=\"\"";
		} else if (entity.getType() == EntityType.STATE_CONCURRENT) {
			return "label=\"\"";
		} else if (entity.getType() == EntityType.ACTIVITY_CONCURRENT) {
			return "label=\"\"";
		} else if (entity.getType() == EntityType.COMPONENT) {
			return "label=" + getLabelForComponent(entity);
		} else if (entity.getType() == EntityType.ACTIVITY) {
			final DrawFile drawFile = entity.getImageFile();
			if (drawFile != null) {
				final String path = StringUtils.getPlateformDependentAbsolutePath(drawFile.getPng());
				final String bgcolor = "\"" + data.getSkinParam().getBackgroundColor().getAsHtml() + "\"";
				final StringBuilder sb = new StringBuilder("label=<");
				sb.append("<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"0\">");
				sb.append("<TR>");
				sb.append("<TD BGCOLOR=" + bgcolor
						+ " BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"2\">");
				sb.append("<IMG SRC=\"" + path + "\"/></TD></TR>");
				sb.append("</TABLE>");
				sb.append(">");
				return sb.toString();
			}
			return "label=" + getSimpleLabelAsHtml(entity, FontParam.ACTIVITY);
		} else if (entity.getType() == EntityType.EMPTY_PACKAGE) {
			return "label=" + getSimpleLabelAsHtml(entity, getFontParamForGroup());
		} else if (entity.getType() == EntityType.USECASE) {
			return "label=" + getLabelForUsecase(entity);
		} else if (entity.getType() == EntityType.STATE) {
			return "label=" + getLabelForState(entity);
		}
		return "label=\"" + entity.getDisplay() + "\"";
	}

	private String getSimpleLabelAsHtml(IEntity entity, FontParam param) {
		return "<" + manageHtmlIB(entity.getDisplay(), param) + ">";
	}

	private String getLabelForState(IEntity entity) throws IOException {
		final DrawFile cFile = entity.getImageFile();
		final String stateBgcolor = getColorString(ColorParam.stateBackground);

		final StringBuilder sb = new StringBuilder("<{<TABLE BGCOLOR=" + stateBgcolor
				+ " BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"0\">");
		sb.append("<TR><TD>" + manageHtmlIB(entity.getDisplay(), FontParam.STATE) + "</TD></TR>");
		sb.append("</TABLE>");

		if (entity.fields2().size() > 0) {
			sb.append("|");
			for (Member att : entity.fields2()) {
				sb.append(manageHtmlIB(att.getDisplayWithVisibilityChar(), FontParam.STATE_ATTRIBUTE));
				sb.append("<BR ALIGN=\"LEFT\"/>");
			}
		}

		if (cFile != null) {
			sb.append("|");
			final String path = StringUtils.getPlateformDependentAbsolutePath(cFile.getPng());
			final String bgcolor = "\"" + data.getSkinParam().getBackgroundColor().getAsHtml() + "\"";

			sb.append("<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"0\">");
			sb.append("<TR>");
			sb.append("<TD BGCOLOR=" + bgcolor + " BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"2\">");
			sb.append("<IMG SRC=\"" + path + "\"/></TD></TR>");
			sb.append("</TABLE>");
		}

		if (entity.fields2().size() == 0 && cFile == null) {
			sb.append("|");
		}

		sb.append("}>");

		return sb.toString();
	}

	private String getLabelForUsecase(IEntity entity) {
		final Stereotype stereotype = getStereotype(entity);
		if (stereotype == null) {
			return getSimpleLabelAsHtml(entity, FontParam.USECASE);
		}
		final StringBuilder sb = new StringBuilder("<<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"0\">");
		if (isThereLabel(stereotype)) {
			sb.append("<TR><TD>" + manageHtmlIB(stereotype.getLabel(), FontParam.USECASE_STEREOTYPE) + "</TD></TR>");
		}
		sb.append("<TR><TD>" + manageHtmlIB(entity.getDisplay(), FontParam.USECASE) + "</TD></TR>");
		sb.append("</TABLE>>");
		return sb.toString();
	}

	private String getLabelForComponent(IEntity entity) {
		final Stereotype stereotype = getStereotype(entity);
		if (stereotype == null) {
			return getSimpleLabelAsHtml(entity, FontParam.COMPONENT);
		}
		final StringBuilder sb = new StringBuilder("<<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"0\">");
		if (isThereLabel(stereotype)) {
			sb.append("<TR><TD>" + manageHtmlIB(stereotype.getLabel(), FontParam.COMPONENT_STEREOTYPE) + "</TD></TR>");
		}
		sb.append("<TR><TD>" + manageHtmlIB(entity.getDisplay(), FontParam.COMPONENT) + "</TD></TR>");
		sb.append("</TABLE>>");
		return sb.toString();
	}

	private String getLabelForActor(IEntity entity) throws IOException {
		final String actorAbsolutePath = StringUtils.getPlateformDependentAbsolutePath(data.getStaticImages()
				.get(EntityType.ACTOR).getPngOrEps(isEps));
		final Stereotype stereotype = getStereotype(entity);

		final StringBuilder sb = new StringBuilder("<<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"0\">");
		if (isThereLabel(stereotype)) {
			sb.append("<TR><TD>" + manageHtmlIB(stereotype.getLabel(), FontParam.USECASE_ACTOR_STEREOTYPE)
					+ "</TD></TR>");
		}
		sb.append("<TR><TD><IMG SRC=\"" + actorAbsolutePath + "\"/></TD></TR>");
		sb.append("<TR><TD>" + manageHtmlIB(entity.getDisplay(), FontParam.USECASE_ACTOR) + "</TD></TR>");
		sb.append("</TABLE>>");
		return sb.toString();

	}

	private String getLabelForCircleInterface(IEntity entity) throws IOException {
		final String circleInterfaceAbsolutePath = StringUtils.getPlateformDependentAbsolutePath(data.getStaticImages()
				.get(EntityType.CIRCLE_INTERFACE).getPngOrEps(isEps));
		final Stereotype stereotype = getStereotype(entity);

		final StringBuilder sb = new StringBuilder("<<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"0\">");
		if (isThereLabel(stereotype)) {
			sb.append("<TR><TD>" + manageHtmlIB(stereotype.getLabel(), FontParam.COMPONENT_STEREOTYPE) + "</TD></TR>");
		}
		sb.append("<TR><TD><IMG SRC=\"" + circleInterfaceAbsolutePath + "\"/></TD></TR>");
		sb.append("<TR><TD>" + manageHtmlIB(entity.getDisplay(), FontParam.COMPONENT) + "</TD></TR>");
		sb.append("</TABLE>>");
		return sb.toString();

	}

	private String getLabelForLollipop(IEntity entity) throws IOException {
		final String circleInterfaceAbsolutePath = StringUtils.getPlateformDependentAbsolutePath(data.getStaticImages()
				.get(EntityType.LOLLIPOP).getPngOrEps(isEps));
		final Stereotype stereotype = getStereotype(entity);

		final StringBuilder sb = new StringBuilder("<<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"0\">");
		if (isThereLabel(stereotype)) {
			sb.append("<TR><TD>" + manageHtmlIB(stereotype.getLabel(), FontParam.CLASS) + "</TD></TR>");
		}
		sb.append("<TR><TD><IMG SRC=\"" + circleInterfaceAbsolutePath + "\"/></TD></TR>");
		sb.append("<TR><TD>" + manageHtmlIB(entity.getDisplay(), FontParam.CLASS) + "</TD></TR>");
		sb.append("</TABLE>>");
		return sb.toString();

	}

	private String getLabelForClassOrInterfaceOrEnum(IEntity entity) throws IOException {
		if (isVisibilityModifierPresent) {
			return getLabelForClassOrInterfaceOrEnumWithVisibilityImage(entity);
		}
		return getLabelForClassOrInterfaceOrEnumOld(entity);

	}

	private String getLabelForClassOrInterfaceOrEnumOld(IEntity entity) throws IOException {
		DrawFile cFile = entity.getImageFile();
		if (cFile == null) {
			cFile = data.getStaticImages().get(entity.getType());
		}
		if (cFile == null) {
			throw new IllegalStateException();
		}
		final String circleAbsolutePath;
		if (data.showPortion(EntityPortion.CIRCLED_CHARACTER, entity)) {
			circleAbsolutePath = StringUtils.getPlateformDependentAbsolutePath(cFile.getPngOrEps(isEps));
		} else {
			circleAbsolutePath = null;
		}

		final StringBuilder sb = new StringBuilder("<");

		final boolean showFields = data.showPortion(EntityPortion.FIELD, entity);
		final boolean showMethods = data.showPortion(EntityPortion.METHOD, entity);

		if (showFields == false && showMethods == false) {
			sb.append(getHtmlHeaderTableForObjectOrClassOrInterfaceOrEnum(entity, circleAbsolutePath, 1, true, 1));
		} else {
			sb.append("<TABLE BGCOLOR=" + getColorString(ColorParam.classBackground)
					+ " BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">");
			sb.append("<TR><TD>");
			final int longuestFieldOrAttribute = getLongestFieldOrAttribute(entity);
			final int longuestHeader = getLonguestHeader(entity);
			final int spring = computeSpring(longuestHeader, longuestFieldOrAttribute, 30);

			sb.append(getHtmlHeaderTableForObjectOrClassOrInterfaceOrEnum(entity, circleAbsolutePath, spring, true, 0));

			sb.append("</TD></TR>");

			if (showFields) {
				final boolean hasStatic = hasStatic(entity.fields2());
				sb.append("<TR ALIGN=\"LEFT\"><TD WIDTH=\"55\" ALIGN=\"LEFT\">");
				for (Member att : entity.fields2()) {
					sb.append(manageHtmlIB2(att, FontParam.CLASS_ATTRIBUTE, hasStatic,
							getColorString(ColorParam.classBackground)));
					sb.append("<BR ALIGN=\"LEFT\"/>");
				}
				sb.append("</TD></TR>");
			}
			if (showMethods) {
				final boolean hasStatic = hasStatic(entity.methods2());
				sb.append("<TR ALIGN=\"LEFT\"><TD ALIGN=\"LEFT\">");
				for (Member att : entity.methods2()) {
					sb.append(manageHtmlIB2(att, FontParam.CLASS_ATTRIBUTE, hasStatic,
							getColorString(ColorParam.classBackground)));
					sb.append("<BR ALIGN=\"LEFT\"/>");
				}
				sb.append("</TD></TR>");
			}

			sb.append("</TABLE>");
		}
		sb.append(">");

		return sb.toString();
	}

	private boolean hasStatic(Collection<Member> attributes) {
		for (Member att : attributes) {
			if (att.isStatic()) {
				return true;
			}
		}
		return false;
	}

	private String getLabelForClassOrInterfaceOrEnumWithVisibilityImage(IEntity entity) throws IOException {
		DrawFile cFile = entity.getImageFile();
		if (cFile == null) {
			cFile = data.getStaticImages().get(entity.getType());
		}
		if (cFile == null) {
			throw new IllegalStateException();
		}
		final String circleAbsolutePath;
		if (data.showPortion(EntityPortion.CIRCLED_CHARACTER, entity)) {
			circleAbsolutePath = StringUtils.getPlateformDependentAbsolutePath(cFile.getPngOrEps(isEps));
		} else {
			circleAbsolutePath = null;
		}

		final boolean showFields = data.showPortion(EntityPortion.FIELD, entity);
		final boolean showMethods = data.showPortion(EntityPortion.METHOD, entity);

		final StringBuilder sb = new StringBuilder("<");
		if (showFields == false && showMethods == false) {
			sb.append(getHtmlHeaderTableForObjectOrClassOrInterfaceOrEnum(entity, circleAbsolutePath, 1, true, 1));
		} else {
			final int longuestHeader = getLonguestHeader(entity);
			final int spring = computeSpring(longuestHeader, getLongestFieldOrAttribute(entity), 30);
			final int springField = computeSpring(getLongestField(entity),
					Math.max(longuestHeader, getLongestMethods(entity)), 30);
			final int springMethod = computeSpring(getLongestMethods(entity),
					Math.max(longuestHeader, getLongestField(entity)), 30);

			sb.append("<TABLE BGCOLOR=" + getColorString(ColorParam.classBackground)
					+ " BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">");
			sb.append("<TR><TD>");

			sb.append(getHtmlHeaderTableForObjectOrClassOrInterfaceOrEnum(entity, circleAbsolutePath, spring, true, 0));
			sb.append("</TD></TR>");

			if (showFields) {
				sb.append("<TR><TD WIDTH=\"55\">");
				if (entity.fields2().size() > 0) {
					buildTableVisibility(entity, true, sb, springField);
				}
				sb.append("</TD></TR>");
			}
			if (showMethods) {
				sb.append("<TR><TD>");
				if (entity.methods2().size() > 0) {
					buildTableVisibility(entity, false, sb, springMethod);
				}
				sb.append("</TD></TR>");
			}
			sb.append("</TABLE>");
		}
		sb.append(">");

		return sb.toString();

	}

	private int computeSpring(final int current, final int maximum, int maxSpring) {
		if (maximum <= current) {
			return 0;
		}
		final int spring = maximum - current;
		if (spring > maxSpring) {
			return maxSpring;
		}
		return spring;
	}

	private void buildTableVisibility(IEntity entity, boolean isField, final StringBuilder sb, int spring) throws IOException {
		sb.append("<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"0\">");

		final boolean hasStatic = hasStatic(entity.methods2());
		for (Member att : isField ? entity.fields2() : entity.methods2()) {
			sb.append("<TR><TD WIDTH=\"10\">");
			String s = att.getDisplayWithVisibilityChar();
			final VisibilityModifier visibilityModifier = VisibilityModifier
					.getVisibilityModifier(s.charAt(0), isField);
			if (visibilityModifier != null) {
				final String modifierFile = StringUtils.getPlateformDependentAbsolutePath(data.getVisibilityImages()
						.get(visibilityModifier).getPngOrEps(isEps));
				sb.append("<IMG SRC=\"" + modifierFile + "\"/>");
				s = s.substring(1);
			}
			sb.append("</TD><TD ALIGN=\"LEFT\">");
			sb.append(manageHtmlIB3(att, FontParam.CLASS_ATTRIBUTE, hasStatic,
					getColorString(ColorParam.classBackground)));
			// sb.append(manageHtmlIB(s, FontParam.CLASS_ATTRIBUTE));
			sb.append("</TD>");
			for (int i = 0; i < spring; i++) {
				sb.append("<TD></TD>");
			}
			sb.append("</TR>");
		}
		sb.append("</TABLE>");
	}

	private int getLonguestHeader(IEntity entity) {
		int result = entity.getDisplay().length();
		final Stereotype stereotype = getStereotype(entity);
		if (isThereLabel(stereotype)) {
			final int size = stereotype.getLabel().length();
			if (size > result) {
				result = size;
			}
		}
		return result;
	}

	private int getLongestFieldOrAttribute(IEntity entity) {
		return Math.max(getLongestField(entity), getLongestMethods(entity));
	}

	private int getLongestMethods(IEntity entity) {
		int result = 0;
		for (Member att : entity.methods2()) {
			final int size = att.getDisplayWithVisibilityChar().length();
			if (size > result) {
				result = size;
			}
		}
		return result;

	}

	private int getLongestField(IEntity entity) {
		int result = 0;
		for (Member att : entity.fields2()) {
			final int size = att.getDisplayWithVisibilityChar().length();
			if (size > result) {
				result = size;
			}
		}
		return result;
	}

	private String getLabelForObject(IEntity entity) throws IOException {
		if (isVisibilityModifierPresent) {
			return getLabelForObjectWithVisibilityImage(entity);
		}
		return getLabelForObjectOld(entity);
	}

	private String getLabelForObjectWithVisibilityImage(IEntity entity) throws IOException {

		final int longuestHeader = getLonguestHeader(entity);
		final int spring = computeSpring(longuestHeader, getLongestFieldOrAttribute(entity), 30);
		final int springField = computeSpring(getLongestField(entity),
				Math.max(longuestHeader, getLongestMethods(entity)), 30);

		final StringBuilder sb = new StringBuilder("<<TABLE BGCOLOR=" + getColorString(ColorParam.classBackground)
				+ " BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">");
		sb.append("<TR><TD>");

		sb.append(getHtmlHeaderTableForObjectOrClassOrInterfaceOrEnum(entity, null, spring, false, 0));

		sb.append("</TD></TR>");
		sb.append("<TR><TD WIDTH=\"55\">");

		if (entity.fields2().size() == 0) {
			sb.append(manageHtmlIB(" ", FontParam.OBJECT_ATTRIBUTE));
		} else {
			buildTableVisibility(entity, true, sb, springField);
		}

		sb.append("</TD></TR>");
		sb.append("</TABLE>>");

		return sb.toString();

	}

	private String getLabelForObjectOld(IEntity entity) throws IOException {
		final StringBuilder sb = new StringBuilder("<<TABLE BGCOLOR=" + getColorString(ColorParam.classBackground)
				+ " BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"4\">");
		sb.append("<TR><TD>");

		final int longuestFieldOrAttribute = getLongestFieldOrAttribute(entity);
		final int longuestHeader = getLonguestHeader(entity);
		final int spring = computeSpring(longuestHeader, longuestFieldOrAttribute, 30);

		sb.append(getHtmlHeaderTableForObjectOrClassOrInterfaceOrEnum(entity, null, spring, false, 0));

		sb.append("</TD></TR>");
		sb.append("<TR ALIGN=\"LEFT\"><TD WIDTH=\"55\" ALIGN=\"LEFT\">");

		if (entity.fields2().size() == 0) {
			sb.append(manageHtmlIB(" ", FontParam.OBJECT_ATTRIBUTE));
		} else {
			for (Member att : entity.fields2()) {
				sb.append(manageHtmlIB(att.getDisplayWithVisibilityChar(), FontParam.OBJECT_ATTRIBUTE));
				sb.append("<BR ALIGN=\"LEFT\"/>");
			}
		}

		sb.append("</TD></TR>");
		sb.append("</TABLE>>");

		return sb.toString();
	}

	private String manageHtmlIB(String s, FontParam param) {
		s = unicode(s);
		final int fontSize = data.getSkinParam().getFontSize(param);
		final int style = data.getSkinParam().getFontStyle(param);
		final String fontFamily = data.getSkinParam().getFontFamily(param);
		final DotExpression dotExpression = new DotExpression(s, fontSize, getFontHtmlColor(param), fontFamily, style);
		final String result = dotExpression.getDotHtml();
		if (dotExpression.isUnderline()) {
			underline = true;
		}
		return result;
	}

	private String manageHtmlIB2(Member att, FontParam param, boolean hasStatic, String backColor) {
		String prefix = "";
		if (hasStatic) {
			prefix = "<FONT COLOR=" + backColor + ">_</FONT>";
		}
		if (att.isAbstract()) {
			return prefix + manageHtmlIB("<i>" + att.getDisplayWithVisibilityChar(), param);
		}
		if (att.isStatic()) {
			return manageHtmlIB("<u>" + att.getDisplayWithVisibilityChar(), param);
		}
		return prefix + manageHtmlIB(att.getDisplayWithVisibilityChar(), param);
	}

	private String manageHtmlIB3(Member att, FontParam param, boolean hasStatic, String backColor) {
		String prefix = "";
		if (hasStatic) {
			prefix = "<FONT COLOR=" + backColor + ">_</FONT>";
		}
		if (att.isAbstract()) {
			return prefix + manageHtmlIB("<i>" + att.getDisplayWithoutVisibilityChar(), param);
		}
		if (att.isStatic()) {
			return manageHtmlIB("<u>" + att.getDisplayWithoutVisibilityChar(), param);
		}
		return prefix + manageHtmlIB(att.getDisplayWithoutVisibilityChar(), param);
	}

	private String manageSpace(int size) {
		final DotExpression dotExpression = new DotExpression(" ", size, new HtmlColor("white"), null, Font.PLAIN);
		final String result = dotExpression.getDotHtml();
		return result;
	}

	static String unicode(String s) {
		final StringBuilder result = new StringBuilder();
		for (char c : s.toCharArray()) {
			if (c > 127 || c == '&') {
				final int i = c;
				result.append("&#" + i + ";");
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

	private String getHtmlHeaderTableForObjectOrClassOrInterfaceOrEnumNoSpring(IEntity entity,
			final String circleAbsolutePath, int cellSpacing, boolean classes) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"" + cellSpacing + "\" CELLPADDING=\"0\">");
		sb.append("<TR>");
		if (circleAbsolutePath == null) {
			sb.append("<TD>");
		} else {
			sb.append("<TD ALIGN=\"RIGHT\">");
			sb.append("<IMG SRC=\"" + circleAbsolutePath + "\"/></TD>");
			sb.append("<TD ALIGN=\"LEFT\">");
		}

		appendLabelAndStereotype(entity, sb, classes);
		sb.append("</TD></TR></TABLE>");
		return sb.toString();
	}

	private String getHtmlHeaderTableForObjectOrClassOrInterfaceOrEnum(IEntity entity, final String circleAbsolutePath,
			int spring, boolean classes, int border) throws IOException {
		if (spring == 0) {
			return getHtmlHeaderTableForObjectOrClassOrInterfaceOrEnumNoSpring(entity, circleAbsolutePath, 0, classes);
		}
		final StringBuilder sb = new StringBuilder();

		sb.append("<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"" + border + "\" CELLPADDING=\"" + border + "\">");
		sb.append("<TR>");

		for (int i = 0; i < spring; i++) {
			sb.append("<TD></TD>");
		}

		if (circleAbsolutePath != null) {
			if (circleAbsolutePath.endsWith(".png")) {
				final BufferedImage im = ImageIO.read(new File(circleAbsolutePath));
				final int height = im.getHeight();
				final int width = im.getWidth();
				sb.append("<TD FIXEDSIZE=\"TRUE\" WIDTH=\"" + width + "\" HEIGHT=\"" + height + "\"><IMG SRC=\""
						+ circleAbsolutePath + "\"/></TD>");
			} else if (circleAbsolutePath.endsWith(".eps")) {
				sb.append("<TD><IMG SRC=\"" + circleAbsolutePath + "\"/></TD>");
			}
		}

		sb.append("<TD>");
		appendLabelAndStereotype(entity, sb, classes);
		sb.append("</TD>");

		for (int i = 0; i < spring; i++) {
			sb.append("<TD></TD>");
		}
		sb.append("</TR></TABLE>");
		return sb.toString();
	}

	private void appendLabelAndStereotype(IEntity entity, final StringBuilder sb, boolean classes) {
		final Stereotype stereotype = getStereotype(entity);
		if (isThereLabel(stereotype)) {
			sb.append("<BR ALIGN=\"LEFT\"/>");
			sb.append(manageHtmlIB(stereotype.getLabel(), classes ? FontParam.CLASS_STEREOTYPE
					: FontParam.OBJECT_STEREOTYPE));
			sb.append("<BR/>");
		}
		String display = entity.getDisplay();
		final boolean italic = entity.getType() == EntityType.ABSTRACT_CLASS
				|| entity.getType() == EntityType.INTERFACE;
		if (italic) {
			display = "<i>" + display;
		}
		sb.append(manageHtmlIB(display, classes ? FontParam.CLASS : FontParam.OBJECT));
	}

	private String getHtmlHeaderTableForClassOrInterfaceOrEnumNew(Entity entity, final String circleAbsolutePath) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<TABLE BORDER=\"0\" CELLBORDER=\"0\" CELLSPACING=\"5\" CELLPADDING=\"0\">");
		sb.append("<TR><TD ALIGN=\"RIGHT\"><IMG SRC=\"" + circleAbsolutePath + "\"/></TD><TD ALIGN=\"LEFT\">");

		appendLabelAndStereotype(entity, sb, true);
		sb.append("</TD></TR></TABLE>");
		return sb.toString();
	}

	private boolean isThereLabel(final Stereotype stereotype) {
		return stereotype != null && stereotype.getLabel() != null;
	}

	private Stereotype getStereotype(IEntity entity) {
		if (data.showPortion(EntityPortion.STEREOTYPE, entity) == false) {
			return null;
		}
		return entity.getStereotype();
	}

	public final boolean isUnderline() {
		return underline;
	}

	private boolean workAroundDotBug() {
		for (Link link : data.getLinks()) {
			if (link.getLength() != 1) {
				return false;
			}
		}
		if (data.getUmlDiagramType() == UmlDiagramType.CLASS && allEntitiesAreClasses(data.getEntities().values())) {
			return true;
		}
		for (IEntity ent : data.getEntities().values()) {
			if (data.getAllLinkedTo(ent).size() == 0) {
				return true;
			}
		}
		return false;
	}

	private boolean allEntitiesAreClasses(Collection<? extends IEntity> entities) {
		for (IEntity ent : entities) {
			if (ent.getType() != EntityType.CLASS && ent.getType() != EntityType.ABSTRACT_CLASS
					&& ent.getType() != EntityType.INTERFACE && ent.getType() != EntityType.ENUM) {
				return false;
			}
		}
		return true;
	}

	private boolean isSpecialGroup(Group g) {
		if (g.getType() == GroupType.STATE) {
			return true;
		}
		if (g.getType() == GroupType.CONCURRENT_STATE) {
			throw new IllegalStateException();
		}
		if (data.isThereLink(g)) {
			return true;
		}
		return false;
	}

	public static final String getLastDotSignature() {
		return lastDotSignature;
	}

	public static final void reset() {
		lastDotSignature = null;
	}

}
