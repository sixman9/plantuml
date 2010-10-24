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
 * Revision $Revision: 5441 $
 *
 */
package net.sourceforge.plantuml.statediagram.command;

import java.util.Map;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexPartialMatch;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.LinkDecor;
import net.sourceforge.plantuml.cucadiagram.LinkType;
import net.sourceforge.plantuml.statediagram.StateDiagram;

public class CommandLinkState2 extends SingleLineCommand2<StateDiagram> {

	public CommandLinkState2(StateDiagram diagram) {
		super(diagram, getRegex());
		// "(?i)^([\\p{L}0-9_.]+|\\[\\*\\])\\s*" +
		// "(-+(?:left|right|up|down|le?|ri?|up?|do?)?-*[\\]>])"
		// + "\\s*([\\p{L}0-9_.]+|\\[\\*\\])\\s*(?::\\s*([^\"]+))?$");
	}

	static RegexConcat getRegex() {
		return new RegexConcat(new RegexLeaf("^"), getStatePattern("ENT1"), new RegexLeaf("\\s*"), new RegexLeaf(
				"ARROW", "((-+)(left|right|up|down|le?|ri?|up?|do?)?(-*)([\\]>]))"), new RegexLeaf("\\s*"),
				getStatePattern("ENT2"), new RegexLeaf("\\s*"), new RegexLeaf("LABEL", "(?::\\s*([^\"]+))?"),
				new RegexLeaf("$"));
	}

	private static RegexLeaf getStatePattern(String name) {
		return new RegexLeaf(name, "([\\p{L}0-9_.]+|\\[\\*\\])");
	}

	@Override
	protected CommandExecutionResult executeArg(Map<String, RegexPartialMatch> arg) {
		final String ent1 = arg.get("ENT1").get(0);
		final String ent2 = arg.get("ENT2").get(0);

		final IEntity cl1 = getEntityStart(ent1);
		final IEntity cl2 = getEntityEnd(ent2);

		String queue = arg.get("ARROW").get(1) + arg.get("ARROW").get(3);
		final Direction dir = getDirection(arg);

		if (dir == Direction.LEFT || dir == Direction.RIGHT) {
			queue = "-";
		}

		final int lenght = queue.length();

		Link link = new Link(cl1, cl2, new LinkType(LinkDecor.ARROW, LinkDecor.NONE), arg.get("LABEL").get(0), lenght);
		if (dir == Direction.LEFT || dir == Direction.UP) {
			link = link.getInv();
		}
		getSystem().addLink(link);

		return CommandExecutionResult.ok();
	}

	private Direction getDirection(Map<String, RegexPartialMatch> arg) {
		if (arg.get("ARROW").get(2) != null) {
			return StringUtils.getQueueDirection(arg.get("ARROW").get(2));
		}
		return null;
	}

	private IEntity getEntityStart(String code) {
		if (code.startsWith("[*]")) {
			return getSystem().getStart();
		}
		return getSystem().getOrCreateClass(code);
	}

	private IEntity getEntityEnd(String code) {
		if (code.startsWith("[*]")) {
			return getSystem().getEnd();
		}
		return getSystem().getOrCreateClass(code);
	}

}
