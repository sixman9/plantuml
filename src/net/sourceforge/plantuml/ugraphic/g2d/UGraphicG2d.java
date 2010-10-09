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
 * Revision $Revision: 5326 $
 *
 */
package net.sourceforge.plantuml.ugraphic.g2d;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import net.sourceforge.plantuml.cucadiagram.dot.CucaDiagramFileMaker;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.StringBounderUtils;
import net.sourceforge.plantuml.graphic.UnusedSpace;
import net.sourceforge.plantuml.posimo.DotPath;
import net.sourceforge.plantuml.skin.UDrawable;
import net.sourceforge.plantuml.ugraphic.AbstractUGraphic;
import net.sourceforge.plantuml.ugraphic.UClip;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UText;
import net.sourceforge.plantuml.ugraphic.svg.UGraphicSvg;

public class UGraphicG2d extends AbstractUGraphic<Graphics2D> {

	private final BufferedImage bufferedImage;

	public UGraphicG2d(Graphics2D g2d, BufferedImage bufferedImage) {
		super(g2d);
		this.bufferedImage = bufferedImage;
		registerDriver(URectangle.class, new DriverRectangleG2d());
		registerDriver(UText.class, new DriverTextG2d());
		registerDriver(ULine.class, new DriverLineG2d());
		registerDriver(UPolygon.class, new DriverPolygonG2d());
		registerDriver(UEllipse.class, new DriverEllipseG2d());
		registerDriver(UImage.class, new DriverImageG2d());
		registerDriver(DotPath.class, new DriverDotPathG2d());
	}

	public StringBounder getStringBounder() {
		return StringBounderUtils.asStringBounder(getGraphicObject());
	}

	public final BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	public void setClip(UClip uclip) {
		if (uclip == null) {
			getGraphicObject().setClip(null);
		} else {
			final Shape clip = new Rectangle2D.Double(uclip.getX() + getTranslateX(), uclip.getY() + getTranslateY(),
					uclip.getWidth(), uclip.getHeight());
			getGraphicObject().setClip(clip);
		}
	}

	public void centerChar(double x, double y, char c, Font font) {
		final UnusedSpace unusedSpace = UnusedSpace.getUnusedSpace(font, c);

		getGraphicObject().setColor(getParam().getColor());
		final double xpos = x - unusedSpace.getCenterX();
		final double ypos = y - unusedSpace.getCenterY() - 0.5;

		getGraphicObject().setFont(font);
		getGraphicObject().drawString("" + c, (float) (xpos + getTranslateX()), (float) (ypos + getTranslateY()));
		//getGraphicObject().drawString("" + c, Math.round(xpos + getTranslateX()), Math.round(ypos + getTranslateY()));
	}
	
	static public String getSvgString(UDrawable udrawable) throws IOException {
		final UGraphicSvg ug = new UGraphicSvg(false);
		udrawable.drawU(ug);
		return CucaDiagramFileMaker.getSvg(ug);
	}


}
