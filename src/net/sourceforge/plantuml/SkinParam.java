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
 * Revision $Revision: 5403 $
 *
 */
package net.sourceforge.plantuml;

import java.awt.Font;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.plantuml.graphic.HtmlColor;

public class SkinParam implements ISkinParam {

	private final Map<String, String> params = new HashMap<String, String>();

	public void setParam(String key, String value) {
		params.put(key.toLowerCase().trim(), value.trim());
	}

	public HtmlColor getBackgroundColor() {
		final HtmlColor result = getHtmlColor(ColorParam.background);
		if (result == null) {
			return new HtmlColor("white");
		}
		return result;
	}

	public String getValue(String key) {
		return params.get(key.toLowerCase().replaceAll("_", ""));
	}

	static String humanName(String key) {
		final StringBuilder sb = new StringBuilder();
		boolean upper = true;
		for (int i = 0; i < key.length(); i++) {
			final char c = key.charAt(i);
			if (c == '_') {
				upper = true;
			} else {
				sb.append(upper ? Character.toUpperCase(c) : Character.toLowerCase(c));
				upper = false;
			}
		}
		return sb.toString();
	}

	public HtmlColor getHtmlColor(ColorParam param) {
		final String value = getValue(param.name() + "color");
		if (value == null || HtmlColor.isValid(value) == false) {
			return null;
		}
		return new HtmlColor(value);
	}

	public int getFontSize(FontParam param) {
		String value = getValue(param.name() + "fontsize");
		if (value == null || value.matches("\\d+") == false) {
			value = getValue("defaultfontsize");
		}
		if (value == null || value.matches("\\d+") == false) {
			return param.getDefaultSize();
		}
		return Integer.parseInt(value);
	}

	public String getFontFamily(FontParam param) {
		// Times, Helvetica, Courier or Symbol
		String value = getValue(param.name() + "fontname");
		if (value != null) {
			return value;
		}
		if (param != FontParam.CIRCLED_CHARACTER) {
			value = getValue("defaultfontname");
			if (value != null) {
				return value;
			}
		}
		return param.getDefaultFamily();
	}

	public HtmlColor getFontHtmlColor(FontParam param) {
		String value = getValue(param.name() + "fontcolor");
		if (value == null) {
			value = getValue("defaultfontcolor");
		}
		if (value == null) {
			value = param.getDefaultColor();
		}
		return new HtmlColor(value);
	}

	public int getFontStyle(FontParam param) {
		String value = getValue(param.name() + "fontstyle");
		if (value == null) {
			value = getValue("defaultfontstyle");
		}
		if (value == null) {
			return param.getDefaultFontStyle();
		}
		int result = Font.PLAIN;
		if (value.toLowerCase().contains("bold")) {
			result = result | Font.BOLD;
		}
		if (value.toLowerCase().contains("italic")) {
			result = result | Font.ITALIC;
		}
		return result;
	}

	public Font getFont(FontParam fontParam) {
		return new Font(getFontFamily(fontParam), getFontStyle(fontParam), getFontSize(fontParam));
	}

	public int getCircledCharacterRadius() {
		final String value = getValue("circledcharacterradius");
		if (value != null && value.matches("\\d+")) {
			return Integer.parseInt(value);
		}
		// return 11;
		// System.err.println("SIZE1="+getFontSize(FontParam.CIRCLED_CHARACTER));
		// System.err.println("SIZE1="+getFontSize(FontParam.CIRCLED_CHARACTER)/3);
		return getFontSize(FontParam.CIRCLED_CHARACTER) / 3 + 6;
	}

	public boolean isClassCollapse() {
		return true;
	}

	public int classAttributeIconSize() {
		final String value = getValue("classAttributeIconSize");
		if (value != null && value.matches("\\d+")) {
			return Integer.parseInt(value);
		}
		return 10;
	}

	public boolean isMonochrome() {
		return "true".equals(getValue("monochrome"));
	}

	public static Collection<String> getPossibleValues() {
		final Set<String> result = new TreeSet<String>();
		result.add("Monochrome");
		result.add("BackgroundColor");
		result.add("CircledCharacterRadius");
		result.add("ClassAttributeIconSize");
		result.add("DefaultFontName");
		result.add("DefaultFontStyle");
		result.add("DefaultFontSize");
		result.add("DefaultFontColor");
		for (FontParam p : EnumSet.allOf(FontParam.class)) {
			final String h = humanName(p.name());
			result.add(h + "FontStyle");
			result.add(h + "FontName");
			result.add(h + "FontSize");
			result.add(h + "FontColor");
		}
		return Collections.unmodifiableSet(result);
	}

}
