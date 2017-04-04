package assignment5;

/* Critter1.java
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

public class Critter1 extends Critter {

	private int dir;

	public Critter1() {
		dir = Critter.getRandomInt(8);
	}

	/**
	 * This critter runs if it has at least 20 energy, otherwise it will stay
	 * still. It also only reproduces if it has at least twice the min reproduce
	 * energy
	 */
	@Override
	public void doTimeStep() {

		if (getEnergy() > 20) {
			run(dir);
		}

		if (look(dir, true) == null) {
			if (getEnergy() > 20) {
				run(dir);
			}
		} else {
			dir = Critter.getRandomInt(8);
			run(dir);
		}

		if (getEnergy() > Params.min_reproduce_energy * 2) {
			Critter1 child = new Critter1();
			reproduce(child, getRandomInt(8));
		}
		dir = Critter.getRandomInt(8);

	}

	/**
	 * This critter always fights
	 * 
	 * @param opponent
	 *            type of opponent specified by opponent's toString method
	 */
	@Override
	public boolean fight(String opponent) {
		return true;
	}

	@Override
	public String toString() {
		return "1";
	}

	@Override
	public CritterShape viewShape() {
		return CritterShape.TRIANGLE;
	}

	@Override
	public javafx.scene.paint.Color viewOutlineColor() {
		return javafx.scene.paint.Color.BLACK;
	}

	@Override
	public javafx.scene.paint.Color viewFillColor() {
		return javafx.scene.paint.Color.ORANGE;
	}
}
