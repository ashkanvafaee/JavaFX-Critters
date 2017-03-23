package assignment5;

/* Critter.java
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
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;

public abstract class Critter {
	/* NEW FOR PROJECT 5 */
	public enum CritterShape {
		CIRCLE, SQUARE, TRIANGLE, DIAMOND, STAR
	}

	/*
	 * the default color is white, which I hope makes critters invisible by
	 * default If you change the background color of your View component, then
	 * update the default color to be the same as you background
	 * 
	 * critters must override at least one of the following three methods, it is
	 * not proper for critters to remain invisible in the view
	 * 
	 * If a critter only overrides the outline color, then it will look like a
	 * non-filled shape, at least, that's the intent. You can edit these default
	 * methods however you need to, but please preserve that intent as you
	 * implement them.
	 */
	public javafx.scene.paint.Color viewColor() {
		return javafx.scene.paint.Color.WHITE;
	}

	public javafx.scene.paint.Color viewOutlineColor() {
		return viewColor();
	}

	public javafx.scene.paint.Color viewFillColor() {
		return viewColor();
	}

	public abstract CritterShape viewShape();

	private boolean moved;
	private static String myPackage;
	private static List<Critter> population = new java.util.ArrayList<Critter>();
	private static List<Critter> babies = new java.util.ArrayList<Critter>();
	private static HashMap<Critter, Polygon> shapes = new HashMap<Critter, Polygon>();


	// Gets the package name. This assumes that Critter and its subclasses are
	// all in the same package.
	static {
		myPackage = Critter.class.getPackage().toString().split(" ")[1];
	}

	protected final String look(int direction, boolean steps) {
		return "";
	}

	/* rest is unchanged from Project 4 */

	private static java.util.Random rand = new java.util.Random();

	public static int getRandomInt(int max) {
		return rand.nextInt(max);
	}

	public static void setSeed(long new_seed) {
		rand = new java.util.Random(new_seed);
	}

	/*
	 * a one-character long string that visually depicts your critter in the
	 * ASCII interface
	 */
	public String toString() {
		return "";
	}

	private int energy = 0;

	protected int getEnergy() {
		return energy;
	}

	private int x_coord;
	private int y_coord;

	/**
	 * Moves the critter 1 unit specified by direction if it has not moved and
	 * it always deducts the cost to walk specified by Params
	 * 
	 * @param direction
	 *            between 0-7 for each cardinal direction
	 */
	protected final void walk(int direction) {
		if (!moved) {
			move(1, direction);
		}
		this.energy = getEnergy() - Params.walk_energy_cost;
		setMoved(true);
	}

	/**
	 * Moves the critter 2 unit specified by direction if it has not moved and
	 * it always deducts the cost to run specified by Params
	 * 
	 * @param direction
	 *            between 0-7 for each cardinal direction
	 */
	protected final void run(int direction) {
		if (!moved) {
			move(2, direction);
		}
		this.energy = getEnergy() - Params.run_energy_cost;
		setMoved(true);
	}

	protected final void reproduce(Critter offspring, int direction) {
		offspring.x_coord = this.x_coord;
		offspring.y_coord = this.y_coord;
		offspring.energy = this.energy / 2;
		offspring.move(1, direction);
		offspring.energy += Params.walk_energy_cost;
		babies.add(offspring);

		// Round up for parent's energy
		this.energy = (this.energy + 1) / 2;
	}

	/**
	 * Moves the critter in the world by distance specified by direction
	 * 
	 * @param distance
	 *            either 1 or 2 depending on walk or run
	 * @param direction
	 *            between 0-7 for each cardinal direction
	 */
	private void move(int distance, int direction) {
		switch (direction) {

		// Move right
		case 0:
			this.x_coord = (this.x_coord + distance) % Params.world_width;
			break;

		// Move right and up
		case 1:
			this.x_coord = (this.x_coord + distance) % Params.world_width;
			this.y_coord = this.y_coord - distance;
			if (this.y_coord < 0)
				this.y_coord += Params.world_height;
			break;

		// Move up
		case 2:
			this.y_coord = this.y_coord - distance;
			if (this.y_coord < 0)
				this.y_coord += Params.world_height;
			break;

		// Move left and up
		case 3:
			this.x_coord = this.x_coord - distance;
			if (this.x_coord < 0)
				this.x_coord += Params.world_width;
			this.y_coord = this.y_coord - distance;
			if (this.y_coord < 0)
				this.y_coord += Params.world_height;
			break;

		// Move left
		case 4:
			this.x_coord = this.x_coord - distance;
			if (this.x_coord < 0)
				this.x_coord += Params.world_width;
			break;

		// Move left and down
		case 5:
			this.x_coord = this.x_coord - distance;
			if (this.x_coord < 0)
				this.x_coord += Params.world_width;
			this.y_coord = (this.y_coord + distance) % Params.world_height;
			break;

		// Move down
		case 6:
			this.y_coord = (this.y_coord + distance) % Params.world_height;
			break;

		// Move down and right
		case 7:
			this.x_coord = (this.x_coord + distance) % Params.world_width;
			this.y_coord = (this.y_coord + distance) % Params.world_height;
			break;
		}
	}

	/**
	 * Sets whether the critter has moved or not by flag
	 * 
	 * @param flag
	 *            true or false for if the critter has moved or not
	 */
	private void setMoved(boolean flag) {
		moved = flag;
	}

	/**
	 * Do the fight interactions for all the critters on the map that share the
	 * same space in the world
	 */
	private static void doEncounters() {
		for (Critter cr1 : population) {
			for (Critter cr2 : population) {

				/*
				 * If two critters that are alive, not references to itself, and
				 * on same coordinates then call their fight methods
				 */
				if (cr1.getEnergy() > 0 && cr2.getEnergy() > 0 && cr1 != cr2 && cr1.x_coord == cr2.x_coord
						&& cr1.y_coord == cr2.y_coord) {

					boolean cr1Fight = cr1.fight(cr2.toString());

					if (!cr1Fight) {
						for (Critter cr3 : population) {

							// If moving causes critter to move into occupied
							// spot, move back
							if (cr1 != cr3 && cr1.x_coord == cr3.x_coord && cr1.y_coord == cr3.y_coord) {
								cr1.x_coord = cr2.x_coord;
								cr1.y_coord = cr2.y_coord;
							}
						}
					}

					boolean cr2Fight = cr2.fight(cr1.toString());

					if (!cr2Fight) {
						for (Critter cr3 : population) {

							// If moving causes critter to move into occupied
							// spot, move back
							if (cr2 != cr3 && cr2.x_coord == cr3.x_coord && cr1.y_coord == cr3.y_coord) {
								cr2.x_coord = cr1.x_coord;
								cr2.y_coord = cr1.y_coord;
							}
						}
					}

					// Both alive and same position
					if (cr1.getEnergy() > 0 && cr2.getEnergy() > 0 && cr1.x_coord == cr2.x_coord
							&& cr1.y_coord == cr2.y_coord) {

						// Calculate fighting power
						int cr1Power = cr1Fight ? getRandomInt(cr1.getEnergy()) : 0;
						int cr2Power = cr2Fight ? getRandomInt(cr2.getEnergy()) : 0;

						// Critter 1 wins
						if (cr1Power > cr2Power) {
							cr1.energy += cr2.energy / 2;
							cr2.energy = 0;
						}

						// Critter 2 wins
						else if (cr1Power < cr2Power) {
							cr2.energy += cr1.energy / 2;
							cr1.energy = 0;
						}

						// Tie case, choose critter 1 wins
						else {
							cr1.energy += cr2.energy / 2;
							cr2.energy = 0;
						}
					}
				}
			}
		}
	}

	public abstract void doTimeStep();

	public abstract boolean fight(String oponent);

	/**
	 * Moves the world by one time step. Does the following: 1. Increments
	 * timestep 2. Calls each critter's timestep 3. Does all the fighting in the
	 * world 4. Updates the rest energy for all critters 5. Generates new algae
	 * for the world 6. Moves the babies to the general population 7. Removes
	 * all dead critters in the world
	 */
	public static void worldTimeStep() {

		// 1. Increment timestep
		int timestep = 0;
		timestep++;

		// 2. Do time steps for all critters
		java.util.Iterator<Critter> it = population.iterator();
		while (it.hasNext()) {
			Critter temp = it.next();
			temp.setMoved(false);
			temp.doTimeStep();

			// Remove dead critters
			if (temp.energy <= 0) {
				Main.grid.getChildren().remove(temp);				// removes dead critters from the grid
				it.remove();
			}

		}

		// 3. Do fights/encounters
		doEncounters();

		// 4. Update rest energy
		for (Critter cr : population) {
			cr.energy -= Params.rest_energy_cost;
		}

		// 5. Generate algae
		for (int i = 0; i < Params.refresh_algae_count; i++) {
			Algae alg = new Algae();
			alg.setEnergy(Params.start_energy);
			alg.setX_coord(getRandomInt(Params.world_width));
			alg.setY_coord(getRandomInt(Params.world_height));
			population.add(alg);
		}

		// 6. Move babies to gen pop.
		population.addAll(babies);
		babies.clear();

		// 7. Remove all dead critters
		java.util.Iterator<Critter> i = population.iterator();
		while (i.hasNext()) {
			Critter temp = i.next();
			if (temp.energy <= 0) {
				Main.grid.getChildren().remove(temp);				// removes dead critters from the grid
				i.remove();
			}
		}

	}

	public static void displayWorld(Object pane) {
	
		GridPane grid = (GridPane) pane;	
			for (int i = 0; i < population.size(); i++) {
				grid.getChildren().remove(shapes.get(population.get(i)));

				if (population.get(i).viewShape().equals(CritterShape.CIRCLE)) {
					Shape s = new Circle(2.8);
					s.setFill(population.get(i).viewFillColor());
					s.setStroke(population.get(i).viewOutlineColor());
					s.setStyle("-fx-border-color: black;");
					grid.add(s, population.get(i).x_coord, population.get(i).y_coord);
					shapes.put(population.get(i), (Polygon) s);
					GridPane.setHalignment(s, HPos.CENTER);
					GridPane.setValignment(s, VPos.CENTER);
				}

				else if (population.get(i).viewShape().equals(CritterShape.SQUARE)) {
					Shape s = new Rectangle(3, 3);
					s.setFill(population.get(i).viewFillColor());
					s.setStroke(population.get(i).viewOutlineColor());
					s.setStyle("-fx-border-color: black;");
					grid.add(s, population.get(i).x_coord, population.get(i).y_coord);
					shapes.put(population.get(i), (Polygon) s);
					GridPane.setHalignment(s, HPos.CENTER);
					GridPane.setValignment(s, VPos.CENTER);
				}

				else if (population.get(i).viewShape().equals(CritterShape.TRIANGLE)) {
					Polygon s = new Polygon();
					s.getPoints().addAll(
							(double)population.get(i).x_coord     , (double)population.get(i).y_coord,			// vertex 1
							(double)population.get(i).x_coord +  5, (double)population.get(i).y_coord + 5,		// vertex 2
							(double)population.get(i).x_coord + 10, (double)population.get(i).y_coord);			// vertex 3
					s.setFill(population.get(i).viewFillColor());
					s.setStroke(population.get(i).viewOutlineColor());
					s.setStyle("-fx-border-color: black;");
					grid.add(s, population.get(i).x_coord, population.get(i).y_coord);
					shapes.put(population.get(i), (Polygon) s);
					GridPane.setHalignment(s, HPos.CENTER);
					GridPane.setValignment(s, VPos.CENTER);
					s.setTranslateY(1);
				}
				
				else if (population.get(i).viewShape().equals(CritterShape.DIAMOND)) {
					Polygon s = new Polygon();
					s.getPoints().addAll(
							(double)population.get(i).x_coord     , (double)population.get(i).y_coord,			// vertex 1
							(double)population.get(i).x_coord +  4, (double)population.get(i).y_coord + 4,		// vertex 2
							(double)population.get(i).x_coord +  8, (double)population.get(i).y_coord,			// vertex 3
							(double)population.get(i).x_coord +  4, (double)population.get(i).y_coord - 4);		// vertex 4
					s.setFill(population.get(i).viewFillColor());
					s.setStroke(population.get(i).viewOutlineColor());
					s.setStyle("-fx-border-color: black;");
					grid.add(s, population.get(i).x_coord, population.get(i).y_coord);
					shapes.put(population.get(i), (Polygon) s);
					GridPane.setHalignment(s, HPos.CENTER);
					GridPane.setValignment(s, VPos.CENTER);
				}
				
				else if (population.get(i).viewShape().equals(CritterShape.DIAMOND)) {
					Polygon s = new Polygon();
					s.getPoints().addAll(
							(double)population.get(i).x_coord +  5, (double)population.get(i).y_coord,			// vertex 1
							(double)population.get(i).x_coord +  5, (double)population.get(i).y_coord + 5,		// vertex 2
							(double)population.get(i).x_coord + 10, (double)population.get(i).y_coord,			// vertex 3
							(double)population.get(i).x_coord +  5, (double)population.get(i).y_coord - 5);		// vertex 4
					s.setFill(population.get(i).viewFillColor());
					s.setStroke(population.get(i).viewOutlineColor());
					shapes.put(population.get(i), (Polygon) s);
					s.setStyle("-fx-border-color: black;");
					grid.add(s, population.get(i).x_coord, population.get(i).y_coord);
					GridPane.setHalignment(s, HPos.CENTER);
					GridPane.setValignment(s, VPos.CENTER);
				
			}
		}
		

	}
	/*
	 * Alternate displayWorld, where you use Main.<pane> to reach into your
	 * display component. // public static void displayWorld() {}
	 */

	/**
	 * create and initialize a Critter subclass. critter_class_name must be the
	 * unqualified name of a concrete subclass of Critter, if not, an
	 * InvalidCritterException must be thrown. (Java weirdness: Exception
	 * throwing does not work properly if the parameter has lower-case instead
	 * of upper. For example, if craig is supplied instead of Craig, an error is
	 * thrown instead of an Exception.)
	 * 
	 * @param critter_class_name
	 *            name of the Critter to be created
	 * @throws InvalidCritterException
	 *             if an invalid critter name is given
	 */
	public static void makeCritter(String critter_class_name) throws InvalidCritterException {

		Class<?> myCritter = null;
		Constructor<?> constructor = null;
		Object instanceOfMyCritter = null;

		try {
			myCritter = Class.forName(myPackage + "." + critter_class_name);
		} catch (ClassNotFoundException e) {
			throw new InvalidCritterException(critter_class_name);
		}

		try {

			// No-parameter constructor object
			constructor = myCritter.getConstructor();

			// Create new object using constructor
			instanceOfMyCritter = constructor.newInstance();

			Critter me = (Critter) instanceOfMyCritter; // Cast to Critter

			// Set initial parameters for critter
			me.x_coord = getRandomInt(Params.world_width);
			me.y_coord = getRandomInt(Params.world_height);
			me.energy = Params.start_energy;

			population.add(me);

		} catch (Exception e) {
			// Do whatever is needed to handle the various exceptions here --
			// e.g. rethrow Exception
			throw new InvalidCritterException(critter_class_name);
		}

	}

	/**
	 * Gets a list of critters of a specific type.
	 * 
	 * @param critter_class_name
	 *            What kind of Critter is to be listed. Unqualified class name.
	 * @return List of Critters.
	 * @throws InvalidCritterException
	 *             if an invalid critter name is given
	 */
	public static List<Critter> getInstances(String critter_class_name) throws InvalidCritterException {
		List<Critter> result = new java.util.ArrayList<Critter>();

		try {
			Class<?> cType = Class.forName(myPackage + "." + critter_class_name);
			for (Critter c : population) {
				if (cType.isInstance(c)) {
					result.add(c);
				}
			}
			return result;
		} catch (Exception e) {
			return (result);
		}
	}

	/**
	 * Prints out how many Critters of each type there are on the board.
	 * 
	 * @param critters
	 *            List of Critters.
	 */
	public static void runStats(List<Critter> critters) {
		System.out.print("" + critters.size() + " critters as follows -- ");
		java.util.Map<String, Integer> critter_count = new java.util.HashMap<String, Integer>();

		for (Critter crit : critters) {
			String crit_string = crit.toString();
			Integer old_count = critter_count.get(crit_string);
			if (old_count == null) {
				critter_count.put(crit_string, 1);
			} else {
				critter_count.put(crit_string, old_count.intValue() + 1);
			}
		}

		String prefix = "";
		for (String s : critter_count.keySet()) {
			System.out.print(prefix + s + ":" + critter_count.get(s));
			prefix = ", ";
		}
		System.out.println();
	}

	/*
	 * the TestCritter class allows some critters to "cheat". If you want to
	 * create tests of your Critter model, you can create subclasses of this
	 * class and then use the setter functions contained here.
	 * 
	 * NOTE: you must make sure thath the setter functions work with your
	 * implementation of Critter. That means, if you're recording the positions
	 * of your critters using some sort of external grid or some other data
	 * structure in addition to the x_coord and y_coord functions, then you MUST
	 * update these setter functions so that they correctup update your
	 * grid/data structure.
	 */
	static abstract class TestCritter extends Critter {
		protected void setEnergy(int new_energy_value) {
			super.energy = new_energy_value;
		}

		protected void setX_coord(int new_x_coord) {
			super.x_coord = new_x_coord;
		}

		protected void setY_coord(int new_y_coord) {
			super.y_coord = new_y_coord;
		}

		protected int getX_coord() {
			return super.x_coord;
		}

		protected int getY_coord() {
			return super.y_coord;
		}

		/*
		 * This method getPopulation has to be modified by you if you are not
		 * using the population ArrayList that has been provided in the starter
		 * code. In any case, it has to be implemented for grading tests to
		 * work.
		 */
		protected static List<Critter> getPopulation() {
			return population;
		}

		/*
		 * This method getBabies has to be modified by you if you are not using
		 * the babies ArrayList that has been provided in the starter code. In
		 * any case, it has to be implemented for grading tests to work. Babies
		 * should be added to the general population at either the beginning OR
		 * the end of every timestep.
		 */
		protected static List<Critter> getBabies() {
			return babies;
		}
	}

	/**
	 * Clear the world of all critters, dead and alive
	 */
	public static void clearWorld() {
		population.clear();
	}

}
