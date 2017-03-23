package assignment5;

/* Critter3.java
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

public class Critter3 extends Critter {

	private int dir;

	public Critter3() {
		dir = Critter.getRandomInt(8);
	}

	/**
	 * This critter only walks in the northeast direction. It only reproduces if
	 * it has 10 under double energy.
	 */
	@Override
	public void doTimeStep() {

		if (dir == 1) {
			walk(dir);
		}

		if (getEnergy() > Params.min_reproduce_energy && getEnergy() > Params.min_reproduce_energy * 2 - 10) {
			Critter3 child = new Critter3();
			reproduce(child, Critter.getRandomInt(8));
		}
	}

	/**
	 * This critter only fights if it has at least its starting energy. If it
	 * has more than 10, it will run away, otherwise it will walk away.
	 * @param opponent type of opponent specified by opponent's toString method
	 */
	@Override
	public boolean fight(String opponent) {

		if (getEnergy() >= Params.start_energy) {
			return true;
		} else if (getEnergy() > 10) {
			run(getRandomInt(8));
			return false;
		} else {
			walk(getRandomInt(8));
			return false;
		}
	}

	@Override
	public String toString() {
		return "3";
	}

	@Override
	public CritterShape viewShape() {
		return CritterShape.STAR;
	}
	
	@Override
	public javafx.scene.paint.Color viewOutlineColor() { 
		return javafx.scene.paint.Color.GREEN; 
		}
	
	@Override
	public javafx.scene.paint.Color viewFillColor(){
		return javafx.scene.paint.Color.LIGHTBLUE;
	}
}
