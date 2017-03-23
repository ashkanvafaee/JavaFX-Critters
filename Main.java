package assignment5;


import java.io.Console;

/* Main.java
* Implements Critters World and all User Interface functionality
*/

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import assignment5.Critter.TestCritter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

	static GridPane grid = new GridPane();
	static TextArea output = new TextArea();				// Text field used to output runStats
	static PrintStream ps;
	private ChoiceBox<String> box2 = new ChoiceBox<String>();


	@Override
	public void start(Stage primaryStage) {
		try {
			// Changes output stream to a text field
			ps = new PrintStream(new Console(output));
			System.setOut(ps);
			System.setErr(ps);
			
			primaryStage.setTitle("Critter World");

			// grid.setGridLinesVisible(true);

			// Sets dimensions of outer window
			Scene scene = new Scene(grid, Params.world_width * 10, Params.world_height * 10);
			grid.setStyle("-fx-background-color: white;");

			primaryStage.setScene(scene);
			primaryStage.show();

			// Second stage that users interact with
			Stage userInterface = new Stage();
			userInterface.setTitle("User Control");
			GridPane userGrid = new GridPane();
			Scene userScene = new Scene(userGrid, 500, 800);
			userGrid.setStyle("-fx-background-color: white;");
			userInterface.setScene(userScene);
			userInterface.show();

			// adds buffer around grids
			// grid.setPadding(new Insets(10, 10, 10, 10));
			userGrid.setPadding(new Insets(10, 10, 10, 10));
			
			/*Circle c = new Circle(3);
			System.out.println(c.getRadius());
			grid.add(c, 3, 3);
			grid.getChildren().remove(c);*/
			

			for (int i = 0; i < Params.world_width; i++) {
				ColumnConstraints column = new ColumnConstraints(10);
				grid.getColumnConstraints().add(column);
			}
			for (int i = 0; i < Params.world_height; i++) {
				RowConstraints row = new RowConstraints(10);
				grid.getRowConstraints().add(row);
			}

			for (int i = 0; i < Params.world_width; i++) {
				for (int j = 0; j < Params.world_height; j++) {
					Pane pane = new Pane();
					pane.setStyle("-fx-border-color: black;");
					grid.add(pane, i, j);
				}
			}

			// These  lines define the row and column sizes of the user Grid
			userGrid.getRowConstraints().add(new RowConstraints(90)); 	// Row 0 (Title)
			userGrid.getColumnConstraints().add(new ColumnConstraints(300));
			userGrid.getRowConstraints().add(new RowConstraints(50)); 	// Row 1 (Make Critters)
			userGrid.getRowConstraints().add(new RowConstraints(200)); 	// Row 2 (Time Step)
			userGrid.getRowConstraints().add(new RowConstraints(200)); 	// Row 3 (Set seed)
			userGrid.getRowConstraints().add(new RowConstraints(100)); 	// Row 4 (runStarts)
			userGrid.getRowConstraints().add(new RowConstraints(100));	// Row 5 (Animation)

			// Welcome Label
			Label welcome = new Label();
			welcome.setText("Welcome to Critters!");
			welcome.setStyle("-fx-font: 30 px;");
			welcome.setTranslateX(90);
			welcome.setTranslateY(-30);
			welcome.setMaxWidth(300);
			welcome.setMinWidth(300);
			userGrid.add(welcome, 0, 0);

			// Button for making Critters
			Button make = new Button("Make Critters!");
			userGrid.add(make, 2, 1);
			make.setMaxWidth(150);
			make.setMinWidth(150);
			make.setTranslateX(-160);
			make.setStyle("-fx-background-color: skyblue;");
			make.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent AE) {
					make.setStyle("-fx-background-color: lightblue;");
				}
			});
			make.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent AE) {
					make.setStyle("-fx-background-color: skyblue;");
				}
			});

			// TextBox for specifying number of Critters
			TextField t = new TextField();
			t.setPromptText("  #"); // grayed out text
			t.setMaxWidth(50);
			t.setMinWidth(50);
			userGrid.add(t, 1, 1);
			t.setTranslateX(-200);

			// Drop down menu listing choices of Critters
			ChoiceBox<String> box = new ChoiceBox<String>();
			String packageName = this.getClass().getCanonicalName().substring(0,11);

			// Parsing all valid Critter sub-types into the drop down menu
			File f = new File(packageName);
			String[] classList = f.list();
			for (int i = 0; i < classList.length; i++) {
				if (classList[i].endsWith(".class")) {
					try {
						Class<?> cType = Class
								.forName(packageName + "." + classList[i].substring(0, classList[i].length() - 6));
						Object cObject = cType.newInstance();

						if (Critter.class.isAssignableFrom(cObject.getClass())
								|| TestCritter.class.isAssignableFrom(cObject.getClass())) {
							// System.out.println(classList[i].substring(0,
							// classList[i].length() - 6));
							box.getItems().add(classList[i].substring(0, classList[i].length() - 6));
							box.setValue((classList[i].substring(0, classList[i].length() - 6)));
						}
					} catch (Exception e) {
						continue;
					}
				}
			}
			userGrid.add(box, 0, 1);
			
			// box.setTranslateX(10);
			box.setMaxWidth(90);
			box.setMinWidth(90);

			// make Critter handler
			make.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent AE) {
					try {
						for (int i = 0; i < Integer.parseInt(t.getText()); i++) {
							Critter.makeCritter(box.getValue());
						}
						update();
						Critter.displayWorld(grid);
					} catch (InvalidCritterException e) {
					}
				}
			});

			// TimeStep Slidebar
			Slider slider = new Slider();
			slider.setMaxWidth(200);
			slider.setMaxWidth(200);
			slider.setMin(1);
			slider.setMax(500);
			slider.setValue(0);
			slider.setShowTickLabels(true);
			slider.setShowTickMarks(true);
			slider.setMajorTickUnit(100);
			slider.setMinorTickCount(5);
			userGrid.add(slider, 0, 2);
			// slider.setTranslateX();

			// Textbox for slider
			TextField ts = new TextField();
			ts.setMaxWidth(50);
			ts.setMinWidth(50);
			ts.setTranslateX(-75);
			ts.setText("1");
			userGrid.add(ts, 1, 2);

			// slider handler
			slider.setOnMouseDragged(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent AE) {
					ts.setText(Integer.toString((int) slider.getValue()));
				}
			});

			// Button for Time Steps
			Button tStep = new Button("Perform Time Step");
			// tStep.setTranslateX(-10 * (Params.world_width) + 400);
			tStep.setTranslateY(50);
			tStep.setMaxWidth(280);
			tStep.setMinWidth(280);
			tStep.setStyle("-fx-background-color: skyblue;");
			userGrid.add(tStep, 0, 2);
			tStep.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent AE) {
					tStep.setStyle("-fx-background-color: lightblue;");
				}
			});
			tStep.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent AE) {
					tStep.setStyle("-fx-background-color: skyblue;");
				}
			});

			// Time Step handler
			tStep.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent AE) {
					try {
						for (int i = 0; i < Integer.parseInt(ts.getText()); i++) {
							Critter.worldTimeStep();
						}
						update();
						Critter.displayWorld(grid);
					} catch (Exception e) {
					}
				}
			});
			
			// Set Seed TextBox
			TextField seed = new TextField();
			seed.setPromptText("     #"); // grayed out text
			seed.setMaxWidth(70);
			seed.setMinWidth(70);
			userGrid.add(seed, 0, 3);
			
			// Set Seed Button
			Button setSeed = new Button("Set Seed");
			userGrid.add(setSeed, 0, 3);
			setSeed.setMaxWidth(100);
			setSeed.setMinWidth(100);
			setSeed.setTranslateX(100);
			setSeed.setStyle("-fx-background-color: skyblue;");
			setSeed.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent AE) {
					setSeed.setStyle("-fx-background-color: lightblue;");
				}
			});
			setSeed.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent AE) {
					setSeed.setStyle("-fx-background-color: skyblue;");
				}
			});
			
			// Set Seed handler
			setSeed.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent AE) {
					try {
						Critter.setSeed(Integer.parseInt(seed.getText()));;
					} catch (Exception e) {
					}
				}
			});
			
			
			// Label for runStats output
			output.setWrapText(true);
			output.setTranslateY(80);
			output.setMaxWidth(400);
			output.setMaxHeight(600);
			userGrid.add(output,0, 4);
			
			//choice box for runStats
			// Parsing all valid Critter sub-types into the drop down menu
			for (int i = 0; i < classList.length; i++) {
				if (classList[i].endsWith(".class")) {
					try {
						Class<?> cType = Class.forName(packageName + "." + classList[i].substring(0, classList[i].length() - 6));
						Object cObject = cType.newInstance();

						if (Critter.class.isAssignableFrom(cObject.getClass())
								|| TestCritter.class.isAssignableFrom(cObject.getClass())) {
							box2.getItems().add(classList[i].substring(0, classList[i].length() - 6));
							box2.setValue((classList[i].substring(0, classList[i].length() - 6)));
						}
					} catch (Exception e) {
						continue;
					}
				}
			}

			userGrid.add(box2, 0, 4);
			// box.setTranslateX(10);
			box2.setMaxWidth(90);
			box2.setMinWidth(90);
			
			// runStats button
			// Set Seed Button
			Button statsButton = new Button("Run Stats");
			userGrid.add(statsButton, 0, 4);
			statsButton.setMaxWidth(100);
			statsButton.setMinWidth(100);
			statsButton.setTranslateX(100);
			statsButton.setStyle("-fx-background-color: skyblue;");
			statsButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent AE) {
					statsButton.setStyle("-fx-background-color: lightblue;");
				}
			});
			statsButton.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent AE) {
					statsButton.setStyle("-fx-background-color: skyblue;");
				}
			});
			
			
			// runStats handler
			statsButton.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent AE) {
					update();
				}
			});
			
			
			
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	//Method that updates the runStats output
	private void update(){
		String packageName = this.getClass().getCanonicalName().substring(0,11);

		try {
			Class<?> cType = Class.forName(packageName + "." + box2.getValue());
			Object cObject = cType.newInstance();
			if (cObject instanceof Critter) {
				List<Critter> cList = new java.util.ArrayList<Critter>();
				cList = Critter.getInstances(box2.getValue());
				if (cList.size() >= 0) {
					Class<?> myCritter = null;

					myCritter = Class.forName(packageName + "." + box2.getValue());
					Method m1 = myCritter.getMethod("runStats", List.class);
					m1.invoke(null, Critter.getInstances(box2.getValue()));

				}

			}

		} catch (Exception e) {
		}
		
	}
	
	
    public class Console extends OutputStream {
        private TextArea console;

        public Console(TextArea console) {
            this.console = console;
        }

        public void appendText(String valueOf) {
            Platform.runLater(() -> console.appendText(valueOf));
        }

        public void write(int b) throws IOException {
            appendText(String.valueOf((char)b));
        }
    }
}



/*
 * Reference Code
 * 
 * 
 * 
 * /*for(int i=1; i<Params.world_height; i++){ Rectangle r = new Rectangle
 * (10,10); r.setFill(javafx.scene.paint.Color.WHITE);
 * r.setStyle("-fx-border-color: black;"); grid.addRow(i, r);
 * //GridPane.setHalignment(r, HPos.CENTER);
 * 
 * }
 * 
 * for(int j=1;j<Params.world_width;j++){ Rectangle r = new Rectangle (10,10);
 * r.setFill(javafx.scene.paint.Color.WHITE);
 * r.setStyle("-fx-border-color: black;"); grid.addColumn(j, r);
 * //GridPane.setValignment(r, VPos.CENTER); }
 */
