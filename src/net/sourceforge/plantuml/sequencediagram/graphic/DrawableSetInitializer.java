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
 * Revision $Revision: 5216 $
 *
 */
package net.sourceforge.plantuml.sequencediagram.graphic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParamBackcolored;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.sequencediagram.Divider;
import net.sourceforge.plantuml.sequencediagram.Event;
import net.sourceforge.plantuml.sequencediagram.GroupingLeaf;
import net.sourceforge.plantuml.sequencediagram.GroupingStart;
import net.sourceforge.plantuml.sequencediagram.GroupingType;
import net.sourceforge.plantuml.sequencediagram.InGroupableList;
import net.sourceforge.plantuml.sequencediagram.LifeEvent;
import net.sourceforge.plantuml.sequencediagram.LifeEventType;
import net.sourceforge.plantuml.sequencediagram.Message;
import net.sourceforge.plantuml.sequencediagram.MessageExo;
import net.sourceforge.plantuml.sequencediagram.Newpage;
import net.sourceforge.plantuml.sequencediagram.Note;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.sequencediagram.ParticipantEnglober;
import net.sourceforge.plantuml.sequencediagram.ParticipantType;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.ComponentType;
import net.sourceforge.plantuml.skin.Skin;

class DrawableSetInitializer {

	private final DrawableSet drawableSet;
	private final boolean showTail;

	private double freeX = 0;
	private double freeY = 0;

	private final double groupingMargin = 10;
	private final double autonewpage;

	private int maxGrouping = 0;

	private ConstraintSet constraintSet;
	private ConstraintSetHBar constraintSetHBar;

	public DrawableSetInitializer(Skin skin, ISkinParam skinParam, boolean showTail, double autonewpage) {
		this.drawableSet = new DrawableSet(skin, skinParam);
		this.showTail = showTail;
		this.autonewpage = autonewpage;

	}

	private double lastFreeY = 0;

	public DrawableSet createDrawableSet(StringBounder stringBounder) {
		if (freeY != 0) {
			throw new IllegalStateException();
		}

		for (Participant p : drawableSet.getAllParticipants()) {
			prepareParticipant(stringBounder, p);
		}

		this.freeY = drawableSet.getHeadHeight(stringBounder);
		//this.freeY += drawableSet.getOffsetForEnglobers(stringBounder);
		this.lastFreeY = this.freeY;
		
		drawableSet.setTopStartingY(this.freeY);

//		for (LivingParticipantBox p : drawableSet.getAllLivingParticipantBox()) {
//			p.getParticipantBox().setTopStartingY(this.freeY);
//		}

		for (Participant p : drawableSet.getAllParticipants()) {
			final LivingParticipantBox living = drawableSet.getLivingParticipantBox(p);
			for (int i = 0; i < p.getInitialLife(); i++) {
				living.getLifeLine().addSegmentVariation(LifeSegmentVariation.LARGER, this.freeY,
						p.getLiveSpecificBackColor());
			}
		}

		final Collection<ParticipantBox> col = new ArrayList<ParticipantBox>();
		for (LivingParticipantBox livingParticipantBox : drawableSet.getAllLivingParticipantBox()) {
			col.add(livingParticipantBox.getParticipantBox());
		}

		constraintSet = new ConstraintSet(col, freeX);
		constraintSetHBar = new ConstraintSetHBar(col);

		for (Event ev : new ArrayList<Event>(drawableSet.getAllEvents())) {
			final double diffY = freeY - lastFreeY;
			if (autonewpage > 0 && diffY > 0 && diffY + getTotalHeight(0, stringBounder) > autonewpage) {
				prepareNewpageSpecial(stringBounder, new Newpage(null), ev);
			}
			if (ev instanceof MessageExo) {
				prepareMessageExo(stringBounder, (MessageExo) ev);
			} else if (ev instanceof Message) {
				prepareMessage(stringBounder, (Message) ev);
			} else if (ev instanceof Note) {
				prepareNote(stringBounder, (Note) ev);
			} else if (ev instanceof LifeEvent) {
				prepareLiveEvent(stringBounder, (LifeEvent) ev);
			} else if (ev instanceof GroupingLeaf) {
				prepareGroupingLeaf(stringBounder, (GroupingLeaf) ev);
			} else if (ev instanceof GroupingStart) {
				prepareGroupingStart(stringBounder, (GroupingStart) ev);
			} else if (ev instanceof Newpage) {
				prepareNewpage(stringBounder, (Newpage) ev);
			} else if (ev instanceof Divider) {
				prepareDivider(stringBounder, (Divider) ev);
			} else {
				throw new IllegalStateException();
			}
		}

		constraintSet.takeConstraintIntoAccount(stringBounder);

		prepareMissingSpace(stringBounder);

		final double diagramWidth = constraintSetHBar.takeConstraintIntoAccount(stringBounder, freeX) + 1;

		drawableSet.setDimension(new Dimension2DDouble(diagramWidth, getTotalHeight(freeY, stringBounder)));
		return drawableSet;
	}

	private double getTotalHeight(double y, StringBounder stringBounder) {
		final double signature = 0;
		return y + drawableSet.getTailHeight(stringBounder, showTail) + signature;
	}

	public double getYposition(StringBounder stringBounder, Newpage newpage) {
		if (newpage == null) {
			throw new IllegalArgumentException();
		}
		final GraphicalNewpage graphicalNewpage = (GraphicalNewpage) drawableSet.getEvent(newpage);
		return graphicalNewpage.getStartingY();
	}

	private void prepareMissingSpace(StringBounder stringBounder) {
		freeX = constraintSet.getMaxX();

		double missingSpace1 = 0;
		double missingSpace2 = 0;

		for (GraphicalElement ev : drawableSet.getAllGraphicalElements()) {
			final double startX = ev.getStartingX(stringBounder);
			final double delta1 = -startX;
			if (delta1 > missingSpace1) {
				missingSpace1 = delta1;
			}
			if (ev instanceof Arrow) {
				final Arrow a = (Arrow) ev;
				a.setMaxX(freeX);
			}
			double width = ev.getPreferredWidth(stringBounder);
			if (ev instanceof Arrow) {
				final Arrow a = (Arrow) ev;
				if (width < a.getActualWidth(stringBounder)) {
					width = a.getActualWidth(stringBounder);
				}
			}
			final double endX = startX + width;
			final double delta2 = endX - freeX;
			if (delta2 > missingSpace2) {
				missingSpace2 = delta2;
			}
		}
		if (missingSpace1 > 0) {
			constraintSet.pushToLeft(missingSpace1);
		}
		freeX = constraintSet.getMaxX() + missingSpace2;
	}

	private void prepareNewpage(StringBounder stringBounder, Newpage newpage) {
		final GraphicalNewpage graphicalNewpage = new GraphicalNewpage(freeY, drawableSet.getSkin().createComponent(
				ComponentType.NEWPAGE, drawableSet.getSkinParam(), null));
		this.lastFreeY = freeY;
		freeY += graphicalNewpage.getPreferredHeight(stringBounder);
		drawableSet.addEvent(newpage, graphicalNewpage);
	}

	private void prepareNewpageSpecial(StringBounder stringBounder, Newpage newpage, Event justBefore) {
		final GraphicalNewpage graphicalNewpage = new GraphicalNewpage(freeY, drawableSet.getSkin().createComponent(
				ComponentType.NEWPAGE, drawableSet.getSkinParam(), null));
		this.lastFreeY = freeY;
		freeY += graphicalNewpage.getPreferredHeight(stringBounder);
		drawableSet.addEvent(newpage, graphicalNewpage, justBefore);
	}

	private void prepareDivider(StringBounder stringBounder, Divider divider) {
		final GraphicalDivider graphicalDivider = new GraphicalDivider(freeY, drawableSet.getSkin().createComponent(
				ComponentType.DIVIDER, drawableSet.getSkinParam(), divider.getText()));
		freeY += graphicalDivider.getPreferredHeight(stringBounder);
		drawableSet.addEvent(divider, graphicalDivider);
	}

	private List<InGroupableList> inGroupableLists = new ArrayList<InGroupableList>();

	private void prepareGroupingStart(StringBounder stringBounder, GroupingStart m) {
		if (m.getType() != GroupingType.START) {
			throw new IllegalStateException();
		}
		final ISkinParam skinParam = new SkinParamBackcolored(drawableSet.getSkinParam(), m.getBackColorElement(), m
				.getBackColorGeneral());
		this.maxGrouping++;
		final Component header = drawableSet.getSkin().createComponent(ComponentType.GROUPING_HEADER, skinParam,
				Arrays.asList(m.getTitle(), m.getComment()));
		final InGroupableList inGroupableList = new InGroupableList(m, freeY);
		for (InGroupableList other : inGroupableLists) {
			other.addInGroupable(inGroupableList);
		}
		inGroupableLists.add(inGroupableList);
		constraintSetHBar.add(inGroupableList);

		final GraphicalElement element = new GroupingHeader(freeY, header, (m.getLevel() + 1) * groupingMargin,
				inGroupableList);
		inGroupableList.setMinWidth(element.getPreferredWidth(stringBounder));
		freeY += element.getPreferredHeight(stringBounder);
		drawableSet.addEvent(m, element);
	}

	private void prepareGroupingLeaf(StringBounder stringBounder, GroupingLeaf m) {
		final GraphicalElement element;
		if (m.getType() == GroupingType.ELSE) {
			final ISkinParam skinParam = new SkinParamBackcolored(drawableSet.getSkinParam(), null, m.getJustBefore()
					.getBackColorGeneral());
			final GraphicalElement before = drawableSet.getEvent(m.getJustBefore());
			final Component comp = drawableSet.getSkin().createComponent(ComponentType.GROUPING_ELSE, skinParam,
					Arrays.asList(m.getComment()));
			final Component body = drawableSet.getSkin().createComponent(ComponentType.GROUPING_BODY, skinParam, null);
			double initY = before.getStartingY();
			if (before instanceof GroupingHeader) {
				initY += before.getPreferredHeight(stringBounder);
			}
			element = new GroupingElse(freeY, initY, body, comp, (m.getLevel() + 1) * groupingMargin,
					getTopGroupingStructure());
			freeY += element.getPreferredHeight(stringBounder);
		} else if (m.getType() == GroupingType.END) {
			final ISkinParam skinParam = new SkinParamBackcolored(drawableSet.getSkinParam(), null, m.getJustBefore()
					.getBackColorGeneral());
			final GraphicalElement justBefore = drawableSet.getEvent(m.getJustBefore());
			final Component body = drawableSet.getSkin().createComponent(ComponentType.GROUPING_BODY, skinParam, null);
			final Component tail = drawableSet.getSkin().createComponent(ComponentType.GROUPING_TAIL, skinParam, null);
			double initY = justBefore.getStartingY();
			if (justBefore instanceof GroupingHeader) {
				// initY += before.getPreferredHeight(stringBounder);
				// A cause de ComponentRoseGroupingHeader::getPaddingY() a
				// supprimer
				initY += 7;
			}
			element = new GroupingTail(freeY, initY, (m.getLevel() + 1) * groupingMargin, body, tail,
					getTopGroupingStructure());
			freeY += tail.getPreferredHeight(stringBounder);
			final int idx = inGroupableLists.size() - 1;
			inGroupableLists.get(idx).setEndingY(freeY);
			inGroupableLists.remove(idx);
		} else {
			throw new IllegalStateException();
		}
		drawableSet.addEvent(m, element);
	}

	private void prepareNote(StringBounder stringBounder, Note n) {
		LivingParticipantBox p1 = drawableSet.getLivingParticipantBox(n.getParticipant());
		LivingParticipantBox p2;
		if (n.getParticipant2() == null) {
			p2 = null;
		} else {
			p2 = drawableSet.getLivingParticipantBox(n.getParticipant2());
			if (p1.getParticipantBox().getCenterX(stringBounder) > p2.getParticipantBox().getCenterX(stringBounder)) {
				final LivingParticipantBox tmp = p1;
				p1 = p2;
				p2 = tmp;
			}
		}
		final ISkinParam skinParam = new SkinParamBackcolored(drawableSet.getSkinParam(), n.getSpecificBackColor());
		final NoteBox noteBox = new NoteBox(freeY, drawableSet.getSkin().createComponent(ComponentType.NOTE, skinParam,
				n.getStrings()), p1, p2, n.getPosition());

		for (InGroupableList groupingStructure : inGroupableLists) {
			groupingStructure.addInGroupable(noteBox);
		}

		drawableSet.addEvent(n, noteBox);
		freeY += noteBox.getPreferredHeight(stringBounder);
	}

	private void prepareLiveEvent(StringBounder stringBounder, LifeEvent lifeEvent) {
		if (lifeEvent.getType() != LifeEventType.DESTROY && lifeEvent.getType() != LifeEventType.CREATE) {
			throw new IllegalStateException();
		}
		// if (lifeEvent.getType() == LifeEventType.CREATE) {
		// final Participant p = lifeEvent.getParticipant();
		// drawableSet.getLivingParticipantBox(p).create(freeY);
		// //
		// drawableSet.getLivingParticipantBox(p).getParticipantBox().setShowHead(false);
		// if (1 == 1)
		// throw new UnsupportedOperationException();
		// final Component head =
		// drawableSet.getSkin().createComponent(ComponentType.PARTICIPANT_HEAD,
		// drawableSet.getSkinParam(), p.getDisplay());
		// final LifeDestroy destroy = new LifeDestroy(freeY,
		// drawableSet.getLivingParticipantBox(p)
		// .getParticipantBox(), head);
		// drawableSet.addEvent(lifeEvent, destroy);
		// freeY += destroy.getPreferredHeight(stringBounder);
		// }
	}

	private void prepareMessage(StringBounder stringBounder, Message m) {
		final Step1Message step1Message = new Step1Message(stringBounder, m, drawableSet, freeY);
		freeY = step1Message.prepareMessage(constraintSet, inGroupableLists);
	}

	private void prepareMessageExo(StringBounder stringBounder, MessageExo m) {
		final Step1MessageExo step1Message = new Step1MessageExo(stringBounder, m, drawableSet, freeY);
		freeY = step1Message.prepareMessage(constraintSet, inGroupableLists);
	}

	private InGroupableList getTopGroupingStructure() {
		if (inGroupableLists.size() == 0) {
			return null;
		}
		return inGroupableLists.get(inGroupableLists.size() - 1);
	}

	private void prepareParticipant(StringBounder stringBounder, Participant p) {
		final ParticipantBox box;

		if (p.getType() == ParticipantType.PARTICIPANT) {
			final ISkinParam skinParam = new SkinParamBackcolored(drawableSet.getSkinParam(), p.getSpecificBackColor());
			final Component head = drawableSet.getSkin().createComponent(ComponentType.PARTICIPANT_HEAD, skinParam,
					p.getDisplay());
			final Component tail = drawableSet.getSkin().createComponent(ComponentType.PARTICIPANT_TAIL, skinParam,
					p.getDisplay());
			final Component line = drawableSet.getSkin().createComponent(ComponentType.PARTICIPANT_LINE,
					drawableSet.getSkinParam(), p.getDisplay());
			box = new ParticipantBox(head, line, tail, this.freeX);
		} else if (p.getType() == ParticipantType.ACTOR) {
			final ISkinParam skinParam = new SkinParamBackcolored(drawableSet.getSkinParam(), p.getSpecificBackColor());
			final Component head = drawableSet.getSkin().createComponent(ComponentType.ACTOR_HEAD, skinParam,
					p.getDisplay());
			final Component tail = drawableSet.getSkin().createComponent(ComponentType.ACTOR_TAIL, skinParam,
					p.getDisplay());
			final Component line = drawableSet.getSkin().createComponent(ComponentType.ACTOR_LINE,
					drawableSet.getSkinParam(), p.getDisplay());
			box = new ParticipantBox(head, line, tail, this.freeX);
		} else {
			throw new IllegalArgumentException();
		}

		final Component comp = drawableSet.getSkin().createComponent(ComponentType.ALIVE_LINE,
				drawableSet.getSkinParam(), null);

		final LifeLine lifeLine = new LifeLine(box, comp.getPreferredWidth(stringBounder));
		drawableSet.addParticipant(p, new LivingParticipantBox(box, lifeLine));

		this.freeX = box.getMaxX(stringBounder);
	}

	public void addParticipant(Participant p) {
		drawableSet.addParticipant(p, null);
	}

	public void addEvent(Event event) {
		drawableSet.addEvent(event, null);
	}

	public void addParticipantEnglober(ParticipantEnglober englober) {
		drawableSet.addParticipantEnglober(englober);
	}

}
