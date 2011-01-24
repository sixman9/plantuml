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
 * Revision $Revision: 3916 $
 *
 */
package net.sourceforge.plantuml.sequencediagram.graphic;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.ugraphic.UGraphic;

class GraphicalDelayText extends GraphicalElement {

	private final Component compText;

	public GraphicalDelayText(double startingY, Component compText) {
		super(startingY);
		this.compText = compText;
	}

	@Override
	protected void drawInternalU(UGraphic ug, double maxX, Context2D context) {
		ug.translate(0, getStartingY());
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dim = new Dimension2DDouble(maxX, compText.getPreferredHeight(stringBounder));
		compText.drawU(ug, dim, context);
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return compText.getPreferredHeight(stringBounder);
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return compText.getPreferredWidth(stringBounder);
	}

	@Override
	public double getStartingX(StringBounder stringBounder) {
		return 0;
	}

	public double getEndingY(StringBounder stringBounder) {
		return getStartingY() + compText.getPreferredHeight(stringBounder);
	}

}
