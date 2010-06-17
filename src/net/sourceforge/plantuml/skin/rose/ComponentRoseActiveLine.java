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
 * Revision $Revision: 4169 $
 *
 */
package net.sourceforge.plantuml.skin.rose;

import java.awt.Color;
import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.skin.AbstractComponent;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;

public class ComponentRoseActiveLine extends AbstractComponent {

	private final Color foregroundColor;
	private final Color lifeLineBackground;

	public ComponentRoseActiveLine(Color foregroundColor, Color lifeLineBackground) {
		this.foregroundColor = foregroundColor;
		this.lifeLineBackground = lifeLineBackground;
	}

	protected void drawInternalU(UGraphic ug, Dimension2D dimensionToUse) {
		ug.getParam().setBackcolor(lifeLineBackground);
		ug.getParam().setColor(foregroundColor);
		final StringBounder stringBounder = ug.getStringBounder();
		final int x = (int) (dimensionToUse.getWidth() - getPreferredWidth(stringBounder)) / 2;

		final URectangle rect = new URectangle(getPreferredWidth(stringBounder), dimensionToUse.getHeight());
		ug.draw(x, 0, rect);
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return 0;
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return 10;
	}

}
