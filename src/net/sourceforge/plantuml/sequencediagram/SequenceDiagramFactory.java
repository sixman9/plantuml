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
 * Revision $Revision: 4655 $
 *
 */
package net.sourceforge.plantuml.sequencediagram;

import net.sourceforge.plantuml.command.AbstractUmlSystemCommandFactory;
import net.sourceforge.plantuml.sequencediagram.command.CommandActivate;
import net.sourceforge.plantuml.sequencediagram.command.CommandActivate2;
import net.sourceforge.plantuml.sequencediagram.command.CommandArrow;
import net.sourceforge.plantuml.sequencediagram.command.CommandAutonumber;
import net.sourceforge.plantuml.sequencediagram.command.CommandDivider;
import net.sourceforge.plantuml.sequencediagram.command.CommandExoArrowLeft;
import net.sourceforge.plantuml.sequencediagram.command.CommandExoArrowRight;
import net.sourceforge.plantuml.sequencediagram.command.CommandFootbox;
import net.sourceforge.plantuml.sequencediagram.command.CommandGrouping;
import net.sourceforge.plantuml.sequencediagram.command.CommandMultilinesNote;
import net.sourceforge.plantuml.sequencediagram.command.CommandMultilinesNoteOnArrow;
import net.sourceforge.plantuml.sequencediagram.command.CommandMultilinesNoteOverSeveral;
import net.sourceforge.plantuml.sequencediagram.command.CommandNewpage;
import net.sourceforge.plantuml.sequencediagram.command.CommandNoteOnArrow;
import net.sourceforge.plantuml.sequencediagram.command.CommandNoteOverSeveral;
import net.sourceforge.plantuml.sequencediagram.command.CommandNoteSequence;
import net.sourceforge.plantuml.sequencediagram.command.CommandParticipant;
import net.sourceforge.plantuml.sequencediagram.command.CommandSkin;

public class SequenceDiagramFactory extends AbstractUmlSystemCommandFactory {

	private SequenceDiagram system;

	@Override
	protected void initCommands() {
		system = new SequenceDiagram();

		addCommonCommands(system);

		addCommand(new CommandParticipant(system));
		addCommand(new CommandArrow(system));
		addCommand(new CommandExoArrowLeft(system));
		addCommand(new CommandExoArrowRight(system));
		addCommand(new CommandNoteSequence(system));
		addCommand(new CommandNoteOverSeveral(system));
		addCommand(new CommandGrouping(system));
		addCommand(new CommandActivate(system));
		addCommand(new CommandActivate2(system));

		addCommand(new CommandNoteOnArrow(system));

		addCommand(new CommandMultilinesNote(system));
		addCommand(new CommandMultilinesNoteOverSeveral(system));
		addCommand(new CommandMultilinesNoteOnArrow(system));

		addCommand(new CommandNewpage(system));
		addCommand(new CommandDivider(system));
		addCommand(new CommandSkin(system));
		addCommand(new CommandAutonumber(system));
		addCommand(new CommandFootbox(system));
	}

	public SequenceDiagram getSystem() {
		return system;
	}

}
