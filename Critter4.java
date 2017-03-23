package assignment5;

/* Critter4.java
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

public class Critter4 extends Critter {

	private int dir;

	public Critter4() {
		dir = 0;
	}

	/**
	 * This critter walks in a counter-clockwise direction. It reproduces
	 * whenever possible.
	 */
	@Override
	public void doTimeStep() {

		walk(dir);

		dir = (dir + 1) % 8;

		if (getEnergy() > Params.min_reproduce_energy) {
			Critter4 child = new Critter4();
			reproduce(child, getRandomInt(8));
		}
	}

	/**
	 * This critter only fights algae, otherwise it walks away.
	 * @param opponent type of opponent specified by opponent's toString method
	 */
	@Override
	public boolean fight(String opponent) {

		if (opponent.equals("@")) {
			return true;
		} else {
			walk(getRandomInt(8));
			return false;
		}
	}

	@Override
	public String toString() {
		return "4";
	}

	@Override
	public CritterShape viewShape() {
		return CritterShape.TRIANGLE;
	}
	
	@Override
	public javafx.scene.paint.Color viewOutlineColor() { 
		return javafx.scene.paint.Color.YELLOW; 
		}
	
	@Override
	public javafx.scene.paint.Color viewFillColor(){
		return javafx.scene.paint.Color.BLACK;
	}
}
