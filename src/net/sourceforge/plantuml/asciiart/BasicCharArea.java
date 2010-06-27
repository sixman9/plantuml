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
 * Revision $Revision: 3826 $
 *
 */
package net.sourceforge.plantuml.asciiart;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicCharArea {

	private int charSize1 = 500;
	private int charSize2 = 500;

	private int width;
	private int height;

	private char chars[][];

	public BasicCharArea() {
		chars = new char[charSize1][charSize2];
		for (int i = 0; i < charSize1; i++) {
			for (int j = 0; j < charSize2; j++) {
				chars[i][j] = ' ';
			}
		}
	}

	public final int getWidth() {
		return width;
	}

	public final int getHeight() {
		return height;
	}

	public void drawChar(char c, int x, int y) {
		chars[x][y] = c;
		if (x >= width) {
			width = x + 1;
		}
		if (y >= height) {
			height = y + 1;
		}
	}

	public void drawStringLR(String string, int x, int y) {
		for (int i = 0; i < string.length(); i++) {
			drawChar(string.charAt(i), x + i, y);
		}
	}

	public void drawStringTB(String string, int x, int y) {
		for (int i = 0; i < string.length(); i++) {
			drawChar(string.charAt(i), x, y + i);
		}
	}

	public String getLine(int line) {
		final StringBuilder sb = new StringBuilder(charSize1);
		for (int x = 0; x < width; x++) {
			sb.append(chars[x][line]);
		}
		return sb.toString();
	}

	public void print(PrintStream ps) {
		for (String s : getLines()) {
			ps.println(s);
		}
	}

	public List<String> getLines() {
		final List<String> result = new ArrayList<String>(height);
		for (int y = 0; y < height; y++) {
			result.add(getLine(y));
		}
		return Collections.unmodifiableList(result);
	}

	public void drawHLine(char c, int line, int col1, int col2) {
		for (int x = col1; x < col2; x++) {
			this.drawChar(c, x, line);
		}
	}

	public void drawVLine(char c, int col, int line1, int line2) {
		for (int y = line1; y < line2; y++) {
			this.drawChar(c, col, y);
		}
	}

	@Override
	public String toString() {
		return getLines().toString();
	}

}