package assignment5;

/* Critter2.java
 * EE422C Project 5 submission by
 * Replace <...> with your actual data.
 * Kevin Chau
 * kc28535
 * 18238
 * Ashkan Vafaee
 * av28837
 * 18238
 * Slip days used: <0>
 * Git URL: https://github.com/ashkanvafaee/assignment5
 * Spring 2017
*/
import assignment5.Critter.TestCritter;

public class Critter2 extends Critter {

	private int dir;

	public Critter2() {
		dir = Critter.getRandomInt(8);
	}

	/**
	 * This critter only walks in straight lines. It reproduces whenever it has
	 * enough energy.
	 */
	@Override
	public void doTimeStep() {

		if (dir % 2 == 0) {
			walk(dir);
		}

		dir = Critter.getRandomInt(8);

		if (getEnergy() > Params.min_reproduce_energy) {
			Critter2 child = new Critter2();
			reproduce(child, getRandomInt(8));
		}

	}

	/**
	 * This critter only fights algae, otherwise it walks away from the fight.
	 * 
	 * @param opponent
	 *            type of opponent specified by opponent's toString method
	 */
	@Override
	public boolean fight(String opponent) {

		if (opponent.equals("@")) {
			return true;
		} else {
			if (look(dir, false) == null) {
				walk(dir);
			} else {
				dir = getRandomInt(8);
				walk(dir);
			}
			return false;
		}
	}

	@Override
	public String toString() {
		return "2";
	}

	@Override
	public CritterShape viewShape() {
		return CritterShape.DIAMOND;
	}

	@Override
	public javafx.scene.paint.Color viewOutlineColor() {
		return javafx.scene.paint.Color.RED;
	}

	@Override
	public javafx.scene.paint.Color viewFillColor() {
		return javafx.scene.paint.Color.LIMEGREEN;
	}
}
