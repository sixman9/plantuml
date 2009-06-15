/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009, Arnaud Roques (for Atos Origin).
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
 * Original Author:  Arnaud Roques (for Atos Origin).
 *
 */
package net.sourceforge.plantuml.sequencediagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.cucadiagram.Stereotype;

public class Participant {

	private final String code;
	private final List<CharSequence> display;
	private final ParticipantType type;

	private Stereotype stereotype;

	public Participant(ParticipantType type, String code, List<String> display) {
		if (type == null) {
			throw new IllegalArgumentException();
		}
		if (code == null || code.length() == 0) {
			throw new IllegalArgumentException();
		}
		if (display == null || display.size() == 0) {
			throw new IllegalArgumentException();
		}
		this.code = code;
		this.type = type;
		this.display = new ArrayList<CharSequence>(display);
	}

	public String getCode() {
		return code;
	}

	public List<CharSequence> getDisplay() {
		return Collections.unmodifiableList(display);
	}

	public ParticipantType getType() {
		return type;
	}

	public final void setStereotype(Stereotype stereotype) {
		if (type == ParticipantType.ACTOR) {
			return;
		}
		if (this.stereotype != null) {
			throw new IllegalStateException();
		}
		if (stereotype == null) {
			throw new IllegalArgumentException();
		}
		this.stereotype = stereotype;
		display.add(0, stereotype);
	}

}
