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
 * Revision $Revision: 5847 $
 *
 */
package net.sourceforge.plantuml.activitydiagram2;

import net.sourceforge.plantuml.activitydiagram2.command.CommandIf2;
import net.sourceforge.plantuml.activitydiagram2.command.CommandNewActivity;
import net.sourceforge.plantuml.activitydiagram2.command.CommandStart;
import net.sourceforge.plantuml.command.AbstractUmlSystemCommandFactory;

public class ActivityDiagramFactory2 extends AbstractUmlSystemCommandFactory {

	private ActivityDiagram2 system;

	public ActivityDiagram2 getSystem() {
		return system;
	}

	@Override
	protected void initCommands() {
		system = new ActivityDiagram2();

		addCommonCommands(system);
		addCommand(new CommandStart(system));
		addCommand(new CommandNewActivity(system));
		addCommand(new CommandIf2(system));

//		addCommand(new CommandLinkActivity(system));
//		addCommand(new CommandPartition(system));
//		addCommand(new CommandEndPartition(system));
//		addCommand(new CommandLinkLongActivity(system));
//
//		addCommand(new CommandNoteActivity(system));
//		addCommand(new CommandMultilinesNoteActivity(system));
//
//		addCommand(new CommandNoteOnActivityLink(system));
//		addCommand(new CommandMultilinesNoteActivityLink(system));
//		
//		addCommand(new CommandIf(system));
//		addCommand(new CommandElse(system));
//		addCommand(new CommandEndif(system));
		// addCommand(new CommandInnerConcurrent(system));
	}

}
