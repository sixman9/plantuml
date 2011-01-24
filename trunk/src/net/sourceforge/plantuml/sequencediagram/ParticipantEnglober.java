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
 * Revision $Revision: 4836 $
 *
 */
package net.sourceforge.plantuml.sequencediagram;

import java.util.List;

import net.sourceforge.plantuml.graphic.HtmlColor;

public class ParticipantEnglober {

	final private List<String> title;
	final private Participant first;
	final private Participant last;
	final private HtmlColor boxColor;

	public ParticipantEnglober(Participant first, Participant last, List<String> title, HtmlColor boxColor) {
		if (first == null || last == null) {
			throw new IllegalArgumentException();
		}
		this.first = first;
		this.last = last;
		this.title = title;
		this.boxColor = boxColor;
	}

	public final Participant getFirst() {
		return first;
	}

	public final Participant getLast() {
		return last;
	}

	public final List<String> getTitle() {
		return title;
	}

	public final HtmlColor getBoxColor() {
		return boxColor;
	}

}
