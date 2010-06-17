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
 * Revision $Revision: 4604 $
 *
 */
package net.sourceforge.plantuml.cucadiagram;

public class LinkType {

	private final LinkDecor decor1;
	private final LinkStyle style;
	private final LinkDecor decor2;

	public LinkType(LinkDecor decor1, LinkDecor decor2) {
		this(decor1, LinkStyle.NORMAL, decor2);
	}

	public boolean contains(LinkDecor decors) {
		return decor1 == decors || decor2 == decors;
	}

	@Override
	public String toString() {
		return decor1 + "-" + style + "-" + decor2;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		final LinkType other = (LinkType) obj;
		return this.decor1 == other.decor1 && this.decor2 == other.decor2 && this.style == other.style;
	}

	private LinkType(LinkDecor decor1, LinkStyle style, LinkDecor decor2) {
		this.decor1 = decor1;
		this.style = style;
		this.decor2 = decor2;
	}

	public boolean isDashed() {
		return style == LinkStyle.DASHED;
	}

	public LinkType getDashed() {
		return new LinkType(decor1, LinkStyle.DASHED, decor2);
	}

	public LinkType getInterfaceProvider() {
		return new LinkType(decor1, LinkStyle.INTERFACE_PROVIDER, decor2);
	}

	public LinkType getInterfaceUser() {
		return new LinkType(decor1, LinkStyle.INTERFACE_USER, decor2);
	}

	public LinkType getInv() {
		return new LinkType(decor2, style, decor1);
	}

	public String getSpecificDecoration() {
		final StringBuilder sb = new StringBuilder();

		if (decor1 == LinkDecor.NONE && decor2 != LinkDecor.NONE) {
			sb.append("dir=back,");
		}
		if (decor1 != LinkDecor.NONE && decor2 != LinkDecor.NONE) {
			sb.append("dir=both,");
		}

		sb.append("arrowtail=");
		sb.append(decor2.getArrowDot());
		sb.append(",arrowhead=");
		sb.append(decor1.getArrowDot());

		if (decor1 == LinkDecor.EXTENDS || decor2 == LinkDecor.EXTENDS) {
			sb.append(",arrowsize=2");
		}
		if (decor1 == LinkDecor.PLUS || decor2 == LinkDecor.PLUS) {
			sb.append(",arrowsize=1.5");
		}

		if (style == LinkStyle.DASHED) {
			sb.append(",style=dashed");
		}

		return sb.toString();
	}

	public final LinkDecor getDecor1() {
		return decor1;
	}

	public final LinkStyle getStyle() {
		return style;
	}

	public final LinkDecor getDecor2() {
		return decor2;
	}

}
