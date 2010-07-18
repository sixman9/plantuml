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
 * Revision $Revision: 4639 $
 * 
 */
package net.sourceforge.plantuml.oregon;

import java.util.Random;

public class OregonBasicGame implements BasicGame {

	private Screen screen;
	private SmartKeyboard kb;
	private Random rnd;

	private final String da[] = new String[] { "March 29", "April 12", "April 26", "May 10", "May 24", "June 7",
			"June 21", "July 5", "July 19", "August 2", "August 16", "August 31", "September 13", "September 27",
			"October 11", "October 25", "November 8", "November 22", "December 6", "December 20" };
	private final int ep[] = new int[] { 6, 11, 13, 15, 17, 22, 32, 35, 37, 42, 44, 54, 64, 69, 95 };

	public Screen getScreen() {
		return screen;
	}

	private void print(String s) {
		screen.print(s);
	}

	private void printb(String s) {
		screen.print("<b>** " + s + " **</b>");
	}

	private void print() {
		screen.print();
	}

	public void run(Keyboard keyboard) throws NoInputException {
		if (screen != null) {
			throw new IllegalStateException();
		}
		screen = new Screen();
		kb = new SmartKeyboard(keyboard);
		init();
	}

	private double rnd() {
		if (this.rnd == null) {
			this.rnd = new Random(kb.getHistory().hashCode());
		}
		return rnd.nextDouble();
	}

	private void init() throws NoInputException {
		printInitialScenario490();
		initialPurchasesOfPlayer690();
		initialShootingRanking920();
		screen.clear();
		print("<i>** Your trip is about to begin... **</i>");
		print();
		for (int j = 0; j < 20; j++) {
			if (m > 2039) {
				madeIt3190(j);
				break;
			}
			print("<b>Monday, " + da[j] + ", 1847</b>. You are " + whereAreWe());
			print();
			if (f < 6) {
				print("<b>** You're low on food. Better buy some or go hunting soon. **");
				print();
			}
			if (ks == 1 || kh == 1) {
				t = t - 10;
				if (t < 0) {
					needDoctorBadly3010(j);
				}
				print("Doctor charged <b>$10</b> for his services");
				print("to treat your " + (ks == 1 ? "illness." : "injuries."));
			}
			// MP flag to be done?

			m = (int) (m + .5);
			print("Total mileage to date is: <b>" + ((int) m) + "</b>");
			m += 200 + (a - 110) / 2.5 + 10 * rnd();
			print();
			// Calculate how far we travel in 2 weeks
			print("Here's what you now have (no. of bullets, $ worth of other items) :");
			printInventory3350();
			question1000(j);
			eating1310(j);
			screen.clear();
			riders1390(j);
			// print();
			events1800(j);
			// print();
			montains2640();
			if (kb.hasMore()) {
				screen.clear();
			}
		}
	}

	private void events1800(int j) throws NoInputException {
		final int rn = (int) (100.0 * rnd());
		for (int i = 0; i < ep.length; i++) {
			if (rn <= ep[i]) {
				execEvent(i, j);
				return;
			}
		}
		execEvent(ep.length, j);
	}

	private void execEvent(int i, int j) throws NoInputException {
		switch (i) {
		case 0:
			printb("Your wagon breaks down. It costs you time and supplies to fix it.");
			m = m - 15 - 5 * rnd();
			r = r - 4;
			break;
		case 1:
			printb("An ox gores your leg. That slows you down for the rest of the trip.");
			m = m - 25;
			a = a - 10;
			break;
		case 2:
			printb("Bad luck�your daughter breaks her arm. You must stop and");
			printb("make a splint and sling with some of your medical supplies.");
			m = m - 5 - 4 * rnd();
			r = r - 1 - 2 * rnd();
			break;
		case 3:
			printb("An ox wanders off and you have to spend time looking for it.");
			m = m - 17;
			break;
		case 4:
			printb("Your son gets lost and you spend half a day searching for him.");
			m = m - 10;
			break;
		case 5:
			printb("Nothing but contaminated and stagnant water near the trail.");
			printb("You lose time looking for a clean spring or creek.");
			m = m - 2 - 10 * rnd();
			break;

		case 6:
			if (m > 950) {
				int c1 = 0;
				if (c < 11 + 2 * rnd()) {
					c1 = 1;
				}
				printb("Cold weather�Brrrrrrr!�You " + (c1 == 1 ? "dont't " : "")
						+ "have enough clothing to keep warm.");
				if (c1 == 1) {
					dealWithIllness2880(j);
				}
			} else {
				printb("Heavy rains. Traveling is slow in the mud and you break your spare");
				printb("ox yoke using it to pry your wagon out of the mud. Worse yet, some");
				printb("of your ammo is damaged by the water.");
				m = m - 5 - 10 * rnd();
				r = r - 7;
				b = b - 400;
				f = f - 5;
			}
			break;

		case 7:
			printb("Bandits attacking!");
			int br = shoot3870();
			b = b - 20 * br;
			if (b > 0) {
				if (br <= 1) {
					print("That was the quickest draw outside of Dodge City.");
					print("You got at least one and drove 'em off.");
					return;
				}
			} else {
				t = t / 3;
				print("You try to drive them off but you run out of bullets.");
				print("They grab as much cash as they can find.");

			}
			print("You get shot in the leg -");
			kh = 1;
			print("and they grab one of your oxen.");
			a = a - 10;
			r = r - 2;
			print("Better have a doc look at your leg... and soon!");
			break;

		case 8:
			printb("You have a fire in your wagon. Food and supplies are damaged.");
			m = m - 15;
			f = f - 20;
			b = b - 400;
			r = r - 2 * 6 * rnd();
			break;

		case 9:
			printb("You lose your way in heavy fog. Time lost regaining the trail.");
			m = m - 10 - 5 * rnd();
			break;

		case 10:
			printb("You come upon a rattlesnake and before you are able to get your gun");
			printb("out, it bites you.");
			b = b - 10;
			r = r - 2;
			if (r < 0) {
				printb("You have no medical supplies left, and you die of poison.");
				die3060(j);

			}
			print("Fortunately, you acted quickly, sucked out the poison, and");
			print("treated the wound. It is painful, but you'll survive.");
			break;

		case 11:
			print("Your wagon gets swamped fording a river; you lose food and clothes.");
			m = m - 20 - 20 * rnd();
			f = f - 15;
			c = c - 10;
			break;

		case 12:
			printb("You're sound asleep and you hear a noise�get up to investigate.");
			printb("It's wild animals! They attack you!");
			br = shoot3870();
			if (b <= 39) {
				print("You're almost out of ammo; can't reach more.");
				print("The wolves come at you biting and clawing.");
				kh = 1;
				die3030(j);
			}
			if (br <= 2) {
				print("Nice shooting, pardner... They didn't get much.");
			} else {
				print("Kind of slow on the draw. The wolves got at your food and clothes.");
				b = b - 20 * br;
				c = c - 2 * br;
				f = f - 4 * br;
			}
			break;

		case 13:
			printb("You're caught in a fierce hailstorm; ammo and supplies are damaged.");
			m = m - 5 - 10 * rnd();
			b = b - 150;
			r = r - 2 - 2 * rnd();
			break;

		case 14:
			if (e == 1) {
				dealWithIllness2880(j);
			} else if (e == 2 && rnd() > .25) {
				dealWithIllness2880(j);
			} else if (e == 3 && rnd() > .5) {
				dealWithIllness2880(j);
			}
			break;

		case 15:
			printb("Helpful Indians show you where to find more food.");
			f = f + 7;
			break;

		default:
			printb("EVENT " + i);
		}
		print();

	}

	private void sickness2880() {
		// TODO Auto-generated method stub

	}

	private double ma;

	private void madeIt3190(int j) throws NoInputException {
		final double ml = (2040 - ma) / (m - ma);
		f = f + (1 - ml) * (8 + 5 * e);
		print("You finally arrived at Oregon City after 2040 long miles.");
		print("You're exhausted and haggard, but you made it! A real pioneer!");
		final int d = (int) (14 * (j + ml));
		final int dm = (int) (d / 30.5);
		final int dd = (int) (d - 30.5 * dm);
		print("You've been on the trail for " + dm + " months and " + dd + " days.");
		print("You have few supplies remaining :");
		printInventory3350();
		print();
		print("President James A. Polk sends you his heartiest");

		print("congratulations and wishes you a prosperous life in your new home.");
		throw new NoInputException();
	}

	private boolean riders1390(int j) throws NoInputException {
		final double value = (Math.pow(m / 100 - 4, 2) + 72) / (Math.pow(m / 100 - 4, 2) + 12) - 1;
		final double random = 10.0 * rnd();
		if (random > value) {
			return false;
		}
		int gh = 0;
		if (rnd() > .2) {
			gh = 1;
		}
		print();
		print("Riders ahead! They " + (gh == 1 ? "don't " : "") + "look hostile.");
		int gt;
		do {
			print("You can <b>(1)</b> run, <b>(2)</b> attack, <b>(3)</b> ignore them, or <b>(4)</b> circle wagons.");
			gt = kb.inputInt(screen);
		} while (gt < 0 || gt > 4);
		if (rnd() < .2) {
			gh = 1 - gh;
		}
		if (gh == 1) {
			if (gt == 1) {
				m = m + 15;
				a = a - 5;
			} else if (gt == 2) {
				m = m - 5;
				b = b - 100;
			} else if (gt == 4) {
				m = m - 20;
			}
			print("Riders were friendly, but check for possible losses.");
			return true;
		}
		if (gt == 1) {
			m = m + 20;
			r = r - 7;
			b = b - 150;
			a = a - 20;
		} else if (gt == 2) {
			final int br = shoot3870();
			b = b - br * 40 - 80;
			riderShoot(br);
		} else if (gt == 3) {
			if (rnd() > .8) {
				print("They did not attack. Whew!");
				return true;
			}
			b = b - 150;
			r = r - 7;
		} else {
			assert gt == 4;
			final int br = shoot3870();
			b = b - br * 30 - 80;
			m = m - 25;
			riderShoot(br);
		}
		print("Riders were hostile. Better check for losses!");
		if (b >= 0) {
			return true;
		}
		print();
		print("<b>Oh, my gosh!</b>");
		print("They're coming back and you're out of ammo! Your dreams turn to");
		print("dust as you and your family are massacred on the prairie.");
		print3110(j);
		return true;

	}

	private void riderShoot(final int br) {
		if (br <= 1) {
			print("Nice shooting � you drove them off.");
		} else if (br <= 4) {
			print("Kind of slow with your Colt .45.");
		} else {
			print("Pretty slow on the draw, partner. You got a nasty flesh wound.");
			kh = 1;
			print("You'll have to see the doc soon as you can.");
		}
	}

	private int kp;
	private int kq;

	private void montains2640() {
		if (m <= 975) {
			return;
		}
		final double mm = (m / 100.0 - 15);
		if (10 * rnd() > 9 - (mm * mm + 72) / (mm * mm + 12)) {

		}
		// 2670 PRINT "You're in rugged mountain country." : IF RND(1) > .1 THEN
		// 2700
		// 2680 PRINT "You get lost and lose valuable time trying to find the
		// trail."
		// 2690 M = M - 60 : GOTO 2750
		// 2700 IF RND(1) > .11 THEN 2730
		// 2710 PRINT "Trail cave in damages your wagon. You lose time and
		// supplies."
		// 2720 M = M - 20 - 30 * RND(1) : B = B - 200 : R = R - 3 : GOTO 2750
		// 2730 PRINT "The going is really slow; oxen are very tired." : M = M -
		// 45 - 50 * RND(1)

	}

	private void southPass2750() {
		if (kp == 1) {

		}
		kp = 1;
		if (rnd() < .8) {

		}

	}

	// 2750 'South Pass routine
	// 2760 IF KP = 1 THEN 2790 : 'Is the South Pass clear?
	// 2770 KP = 1 : IF RND(1) < .8 THEN 2840 : '80% chance of blizzard
	// 2780 PRINT "You made it safely through the South Pass....no snow!"
	// 2790 IF M < 1700 THEN 2810
	// 2800 IF KM = 1 THEN 2810 : 'Through Blue Mts yet?
	// 2810 KM = 1 : IF RND(1) < .7 THEN 2840 ELSE RETURN : 'Get through without
	// mishap?
	// 2820 MP = 1 : RETURN : 'Set South Pass flag
	// 2830 '
	// 2840 PRINT "Blizzard in the mountain pass. Going is slow; supplies are
	// lost."
	// 2850 KB = 1 : M = M - 30 - 40 * RND(1) : F = F - 12 : B = B - 200 : R = R
	// - 5
	// 2860 IF C < 18 + 2 * RND(1) THEN GOTO 2880 ELSE RETURN : 'Enough clothes?
	// 2870 '

	private int ks;
	private int kh;

	private void dealWithIllness2880(int j) throws NoInputException {
		if (100 * rnd() < 10 + 35 * (e - 1)) {
			print("Mild illness. Your own medicine will cure it.");
			m -= 5;
			r -= 1;
		} else if (100 * rnd() < 100.0 - 40.0 / Math.pow(4.0, e - 1)) {
			print("The whole family is sick. Your medicine will probably work okay.");
			m -= 5;
			r -= 2.5;
		} else {
			print("Serious illness in the family. You'll have to stop and see a doctor");
			print("soon. For now, your medicine will work.");
			r -= 5;
			ks = 1;
		}
		if (r <= 0) {
			print("...if only you had enough.");
			outOfMedicalSupplies3020(j);
		}

	}

	private void eating1310(int j) throws NoInputException {
		if (f < 5) {
			die3000(j);
			return;
		}
		do {
			print("Do you want to eat <b>(1)</b> poorly, <b>(2)</b> moderately or <b>(3)</b> well ?");
			e = kb.inputInt(screen);
			if (e < 1 || e > 3) {
				print("Enter 1, 2, or 3, please.");
				break;
			}
			final int ee = (int) (4 + 2.5 * e);
			if (e == 1 && ee > f) {
				f = 0;
				return;
			}
			if (ee > f) {
				print("You don't have enough to eat that well.");
				break;
			}
			f -= ee;
			return;
		} while (true);

	}

	private void needDoctorBadly3010(int j) throws NoInputException {
		print("<b>You need a doctor badly but can't afford one.</b>");
		die3030(j);
	}

	private void outOfMedicalSupplies3020(int j) throws NoInputException {
		print("<b>You have run out of all medical supplies.</b>");
		print();
		die3030(j);
	}

	private void die3000(int j) throws NoInputException {
		screen.clear();
		print("<b>You run out of food and starve to death.</b>");
		print();
		print3110(j);
	}

	private void die3030(int j) throws NoInputException {
		print("The wilderness is unforgiving and you die of " + (kh == 1 ? "your injuries" : "pneumonia"));
		die3060(j);
	}

	private void die3060(int j) throws NoInputException {
		print("Your family tries to push on, but finds the going too rough");
		print(" without you.");
		print3110(j);
	}

	private void print3110(int j) throws NoInputException {
		print("Some travelers find the bodies of you and your");
		print("family the following spring. They give you a decent");
		print("burial and notify your next of kin.");
		print();
		print("At the time of your unfortunate demise, you had been on the trail");
		final int d = 14 * j;
		final int dm = (int) (d / 30.5);
		final int dd = (int) (d - 30.5 * dm);
		print("for " + dm + " months and " + dd + " days and had covered " + (int) ((m + 70)) + " miles.");
		print();
		print("You had a few supplies left :");
		printInventory3350();
		throw new NoInputException();
	}

	private void question1000(int j) throws NoInputException {
		int x;
		if (j % 2 == 1) {
			do {
				print("Want to <b>(1)</b> stop at the next fort, <b>(2)</b> hunt, or <b>(3)</b> push on ?");
				x = kb.inputInt(screen);
				if (x == 3) {
					return;
				}
				if (x == 1) {
					stopAtFort1100(j);
					return;
				}
				if (x == 2) {
					hunt1200(j);
					if (kq == 0) {
						return;
					}
				}
			} while (true);
		} else {
			do {
				print("Would you like to <b>(1)</b> hunt or <b>(2)</b> continue on ?");
				x = kb.inputInt(screen);
				if (x == 2) {
					return;
				}
			} while (x < 1 || x > 2);
			if (x == 1) {
				hunt1200(j);
			}
		}

	}

	private void hunt1200(int j) throws NoInputException {
		kq = 0;
		if (b <= 39) {
			print("Tough luck. You don't have enough ammo to hunt.");
			kq = 1;
			return;
		}
		m = m - 45;
		final int br = shoot3870();
		if (br <= 1) {
			print("Right between the eyes... you got a big one!");
			print("Full bellies tonight!");
			b = b - 10 - 4 * rnd();
			f = f + 26 + 3 * rnd();
			return;
		}
		if (100.0 * rnd() < 13 * br) {
			print("You missed completely... and your dinner got away.");
			return;
		}
		print("Nice shot... right on target... good eatin' tonight!");
		f = f + 24 - 2 * br;
		b = b - 10 - 3 * br;
		return;
	}

	private void stopAtFort1100(int j) throws NoInputException {
		if (t <= 0) {
			print("You sing with the folks there and get a good");
			print("night's sleep, but you have no money to buy anything.");
			return;
		}

		while (true) {
			print("What would you like to spend on each of the following");
			print("Food?");
			final double p1 = kb.inputInt(screen);
			print("Ammunition?");
			final double p2 = kb.inputInt(screen);
			print("Clothing?");
			final double p3 = kb.inputInt(screen);
			print("Medicine and supplies?");
			final double p4 = kb.inputInt(screen);
			final double p = p1 + p2 + p3 + p4;
			print("The storekeeper tallies up your bill. It comes to $" + ((int) p));
			if (t >= p) {
				t = t - p;
				f = f + .67 * p1;
				b = b + 33 * p2;
				c = c + .67 * p3;
				r = r + .67 * p4;
				return;
			}
			print("Uh, oh. That's more than you have. Better start over.");
		}
	}

	private void printInventory3350() {
		// print("+------+------+------+---------+--------------------+");
		print();
		print("| <u>Cash</u> | <u>Food</u> | <u>Ammo</u> | <u>Clothes</u> | <u>Medicine/parts/...</u> |");
		print("+------+------+------+---------+--------------------+");
		if (f < 0) {
			f = 0;
		}
		if (b < 0) {
			b = 0;
		}
		if (c < 0) {
			c = 0;
		}
		if (r < 0) {
			r = 0;
		}
		print(String.format("|%5d |%5d |%5d | %5d   |     %5d          |", (int) t, (int) f, (int) b, (int) c, (int) r));
		print("+------+------+------+---------+--------------------+");
		print();
	}

	private String whereAreWe() {
		if (m < 5) {
			return "on the high prairie.";
		}
		if (m < 200) {
			return "near Independence Crossing on the Big Blue River.";
		}
		if (m < 350) {
			return "following the Platte River.";
		}
		if (m < 450) {
			return "near Fort Kearney.";
		}
		if (m < 600) {
			return "following the North Platte River.";
		}
		if (m < 750) {
			return "within sight of Chimney Rock.";
		}
		if (m < 850) {
			return "near Fort Laramie.";
		}
		if (m < 1000) {
			return "close upon Independence Rock.";
		}
		if (m < 1050) {
			return "in the Big Horn Mountains.";
		}
		if (m < 1150) {
			return "following the Green River.";
		}
		if (m < 1250) {
			return "not too far from Fort Hall.";
		}
		if (m < 1400) {
			return "following the Snake River.";
		}
		if (m < 1550) {
			return "not far from Fort Boise.";
		}
		if (m < 1850) {
			return "in the Blue Mountains.";
		}
		return "following the Columbia River";

	}

	private void printInitialScenario490() {
		print("	Your journey over the Oregon Trail takes place in 1847.");
		print();
		print("Starting in Independence, Missouri, you plan to take your family of");
		print("five over 2040 tough miles to Oregon City.");
		print();
		print("	Having saved <b>$420</b> for the trip, you bought a wagon for <b>$70</b> and");
		print("now have to purchase the following items :");
		print();
		print(" * <b>Oxen</b> (spending more will buy you a larger and better team which");
		print("    will be faster so you'll be on the trail for less time)");
		print(" * <b>Food</b> (you'll need ample food to keep up your strength and health)");
		print(" * <b>Ammunition</b> ($1 buys a belt of 50 bullets. You'll need ammo for");
		print("    hunting and for fighting off attacks by bandits and animals)");
		print(" * <b>Clothing</b> (you'll need warm clothes, especially when you hit the");
		print("    snow and freezing weather in the mountains)");
		print(" * <b>Other supplies</b> (includes medicine, first-aid supplies, tools, and");
		print("    wagon parts for unexpected emergencies)");
		print();
		print(" You can spend all your money at the start or save some to spend");
		print("at forts along the way. However, items cost more at the forts. You");
		print("can also hunt for food if you run low.");
		print();

	}

	private void initialPurchasesOfPlayer690() throws NoInputException {
		if (kb.hasMore()) {
			screen.clear();
		}
		do {
			print("How much do you want to pay for a team of oxen ?");
			a = kb.inputInt(screen);
			if (a < 100) {
				print("No one in town has a team that cheap");
				continue;
			}
			break;
		} while (true);
		if (a >= 151) {
			print("You choose an honest dealer who tells you that $" + a + " is too much for");
			print("a team of oxen. He charges you $150 and gives you $" + (a - 150) + " change.");
			a = 150;
		}
		do {
			print();
			print("How much do you want to spend on food ?");
			f = kb.inputInt(screen);
			if (f <= 13) {
				print("That won't even get you to the Kansas River");
				print(" - better spend a bit more.");
				continue;
			}
			if (a + f > 300) {
				print("You wont't have any for ammo and clothes.");
				continue;
			}
			break;
		} while (true);
		do {
			print();
			print("How much do you want to spend on ammunition ?");
			b = kb.inputInt(screen);
			if (b < 2) {
				print("Better take a bit just for protection.");
				continue;
			}
			if (a + f + b > 320) {
				print("That won't leave any money for clothes.");
				continue;
			}
			break;
		} while (true);
		do {
			print();
			print("How much do you want to spend on clothes ?");
			c = kb.inputInt(screen);
			if (c <= 24) {
				print("Your family is going to be mighty cold in.");
				print("the montains.");
				print("Better spend a bit more.");
				continue;
			}
			if (a + f + b + c > 345) {
				print("That leaves nothing for medecine.");
				continue;
			}
			break;
		} while (true);
		do {
			print();
			screen.print("How much for medecine, bandage, repair parts, etc. ?");
			r = kb.inputInt(screen);
			if (r <= 5) {
				print("That's not at all wise.");
				continue;
			}
			if (a + f + b + c + r > 350) {
				print("You don't have that much money.");
				continue;
			}
			break;
		} while (true);
		t = 350 - a - f - b - c - r;
		print();
		print("You now have <b>$" + ((int) t) + " left.</b>");
		b = 50 * b;
	}

	private void initialShootingRanking920() throws NoInputException {
		print();
		print("Please rank your shooting (typing) ability as follows :");
		print(" (1) Ace marksman  (2) Good shot  (3) Fair to middlin'");
		print(" (4) Need more practice  (5) Shaky knees");
		do {
			print();
			print("How do you rank yourself ?");
			dr = kb.inputInt(screen);
			if (dr >= 1 && dr <= 6) {
				return;
			}
			print("Please enter 1, 2, 3, 4 or 5.");
		} while (true);
	}

	private int e;
	private int a;
	private double b;
	private double f;
	private double c;
	private double r;
	private double t;
	private int dr;
	private double m;

	private int getTime() {
		return (int) ((System.currentTimeMillis() / 1000L) % 83);
	}

	private String getRandomShootingWord() {
		int rn = (int) (rnd() * 4);
		switch (rn) {
		case 0:
			return "pow";
		case 1:
			return "bang";
		case 2:
			return "blam";
		}
		return "whop";
	}

	private int shoot3870() throws NoInputException {
		final String word1 = getRandomShootingWord() + getTime();
		print("Type: " + word1);
		final String typed1 = kb.input(screen);
		final int time = getDeltaTime(typed1);
		final String word2 = getRandomShootingWord() + time;
		print("Type: " + word2);
		final String typed2 = kb.input(screen);
		int duration = extractInt(typed2) - dr - 1;
		// 3870 'Subroutine to shoot gun
		// 3880 RN = 1 + INT(4 * RND(1)) : 'Pick a random shooting word
		// 3890 S1 = 60 * VAL(MID$(TIME$, 4, 2)) + VAL(RIGHT$(TIME$, 2)) :
		// 'Start timer
		// 3900 PRINT "Type " S$(RN); : INPUT X$
		// 3910 IF S$(RN)< >X$ AND S$(RN + 4)< >X$ THEN PRINT "Nope. Try again.
		// "; : GOTO 3900
		// 3920 S2 = 60 * VAL(MID$(TIME$, 4, 2)) + VAL(RIGHT$(TIME$, 2)) : 'End
		// timer
		// 3930 BR = S2 - S1 - DR - 1 : RETURN
		if (duration < 0) {
			return 0;
		}
		return duration;
	}

	private int extractInt(String typed) {
		final String s = typed.replaceAll("\\D", "");
		if (s.length() == 0) {
			return 0;
		}
		return Integer.parseInt(s);
	}

	private int getDeltaTime(String typed) {
		final int was = extractInt(typed);
		int diff = getTime() - was;
		if (diff < 0) {
			diff += 83;
		}
		return diff;
	}
}
