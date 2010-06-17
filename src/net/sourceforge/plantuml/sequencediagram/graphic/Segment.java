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
 * Revision $Revision: 4699 $
 *
 */
package net.sourceforge.plantuml.sequencediagram.graphic;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.SimpleContext2D;
import net.sourceforge.plantuml.ugraphic.UGraphic;

class Segment {

	final private double pos1;
	final private double pos2;
	final private HtmlColor backcolor;

	Segment(double pos1, double pos2, HtmlColor backcolor) {
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.backcolor = backcolor;
		if (pos2 < pos1) {
			throw new IllegalArgumentException("pos1=" + pos1 + " pos2=" + pos2);
		}
	}
	
	public HtmlColor getSpecificBackColor() {
		return backcolor;
	}

	@Override
	public boolean equals(Object obj) {
		final Segment this2 = (Segment) obj;
		return pos1 == this2.pos1 && pos2 == this2.pos2;
	}

	@Override
	public int hashCode() {
		return new Double(pos1).hashCode() + new Double(pos2).hashCode();
	}

	public boolean contains(double y) {
		return y >= pos1 && y <= pos2;
	}

	@Override
	public String toString() {
		return "" + pos1 + " - " + pos2;
	}

	public void drawU(UGraphic ug, Component comp, int level) {
		final double atX = ug.getTranslateX();
		final double atY = ug.getTranslateY();

		final StringBounder stringBounder = ug.getStringBounder();
		ug.translate((level - 1) * comp.getPreferredWidth(stringBounder) / 2, pos1);
		final Dimension2D dim = new Dimension2DDouble(comp.getPreferredWidth(stringBounder), pos2 - pos1);
		comp.drawU(ug, dim, new SimpleContext2D(false));
		ug.setTranslate(atX, atY);
	}

	public double getLength() {
		return pos2 - pos1;
	}

	public double getPos1() {
		return pos1;
	}

	public double getPos2() {
		return pos2;
	}

	public Segment merge(Segment this2) {
		return new Segment(Math.min(this.pos1, this2.pos1), Math.max(this.pos2, this2.pos2), backcolor);
	}

}
