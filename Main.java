package assignment5;



/* Main.java
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
import javafx.concurrent.*;
import javafx.scene.shape.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import assignment5.Critter.TestCritter;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class Main extends Application {

	// Main grid where critters are located
	static GridPane grid = new GridPane(); 
										
	// Text field used to output runStats
	private static TextArea output = new TextArea(); 
											
	// Lists the available critters
	private ChoiceBox<String> box2 = new ChoiceBox<String>(); 
															
	// Used to alternate functionality of run/stop button
	private static boolean flag = false; 
										
	private static Timer timer = new Timer();
	private static int timerSteps; // Specifies steps/sec
	public static boolean timerFlag = false;

	@Override
	public void start(Stage primaryStage) {
		try {

			primaryStage.setTitle("Critter World");

			// Sets dimensions of outer window based on Params
			int displayWidth = 0;
			int displayHeight = 0;
			int colConstraint = 0;

			// Small world
			if (Params.world_width < 30 && Params.world_height < 30) {
				displayWidth = Params.world_width * 30;
				displayHeight = Params.world_height * 30;
				colConstraint = 30;
			}
			// Medium world
			else if (Params.world_width < 60 && Params.world_height < 60) {
				displayWidth = Params.world_width * 16;
				displayHeight = Params.world_height * 16;
				colConstraint = 16;
			}
			// Big world
			else {
				displayWidth = Params.world_width * 10;
				displayHeight = Params.world_height * 10;
				colConstraint = 10;
			}

			Scene scene = new Scene(grid, displayWidth, displayHeight);
			grid.setStyle("-fx-background-color: white;");

			primaryStage.setScene(scene);
			primaryStage.show();

			// Second stage that users interact with
			Stage userInterface = new Stage();
			userInterface.setTitle("User Control");
			GridPane userGrid = new GridPane();
			Scene userScene = new Scene(userGrid, 500, 900);
			userGrid.setStyle("-fx-background-color: white;");
			userInterface.setScene(userScene);
			userInterface.show();

			// adds buffer around grids
			userGrid.setPadding(new Insets(10, 10, 10, 10));

			for (int i = 0; i < Params.world_width; i++) {
				ColumnConstraints column = new ColumnConstraints(colConstraint);
				grid.getColumnConstraints().add(column);
			}
			for (int i = 0; i < Params.world_height; i++) {
				RowConstraints row = new RowConstraints(colConstraint);
				grid.getRowConstraints().add(row);
			}

			for (int i = 0; i < Params.world_width; i++) {
				for (int j = 0; j < Params.world_height; j++) {
					Pane pane = new Pane();
					pane.setStyle("-fx-border-color: black;");
					grid.add(pane, i, j);
				}
			}

			// These lines define the row and column sizes of the user Grid

			// Row 0 (Title)
			userGrid.getRowConstraints().add(new RowConstraints(90));

			userGrid.getColumnConstraints().add(new ColumnConstraints(300));

			// Row 1 Make Critters
			userGrid.getRowConstraints().add(new RowConstraints(50));

			// Row 2 Time Step
			userGrid.getRowConstraints().add(new RowConstraints(150));

			// Row 3 Set Seed
			userGrid.getRowConstraints().add(new RowConstraints(150));

			// Row 4 runStats
			userGrid.getRowConstraints().add(new RowConstraints(100));

			// Row 5 Animation
			userGrid.getRowConstraints().add(new RowConstraints(300));

			// Row 6 Exit
			userGrid.getRowConstraints().add(new RowConstraints(260));

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
			// t.setPromptText(" #"); // grayed out text
			t.setText("1");
			t.setMaxWidth(50);
			t.setMinWidth(50);
			userGrid.add(t, 1, 1);
			t.setTranslateX(-200);

			// Drop down menu listing choices of Critters
			ChoiceBox<String> box = new ChoiceBox<String>();
			String packageName = this.getClass().getCanonicalName().substring(0, 11);

			// Parsing all valid Critter sub-types into the drop down menu
			File f = new File(packageName);
			String[] classList = f.list();

			// means that .class files are located in separate bin folder
			if (classList == null) { 
				File f2 = new File(System.getProperty("user.dir") + "\\bin\\assignment5");
				classList = f2.list();
			}

			// if .class files are located in same folder as .java files
			if (classList != null) {
				for (int i = 0; i < classList.length; i++) {
					if (classList[i].endsWith(".class")) {
						try {
							Class<?> cType = Class
									.forName(packageName + "." + classList[i].substring(0, classList[i].length() - 6));
							Object cObject = cType.newInstance();

							if (Critter.class.isAssignableFrom(cObject.getClass())
									|| TestCritter.class.isAssignableFrom(cObject.getClass())) {
								box.getItems().add(classList[i].substring(0, classList[i].length() - 6));
								box.setValue((classList[i].substring(0, classList[i].length() - 6)));
							}
						} catch (Exception e) {
							continue;
						}
					}
				}
			}

			userGrid.add(box, 0, 1);

			box.setMaxWidth(90);
			box.setMinWidth(90);

			// make Critter handler
			make.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent AE) {
					try {
						int val = Integer.parseInt(t.getText());
						if (val < 0)
							throw new Exception();
						for (int i = 0; i < val; i++) {
							Critter.makeCritter(box.getValue());
						}
						update();
						Critter.displayWorld(grid);
					} catch (Exception e) {
						Alert a = new Alert(AlertType.ERROR);
						a.setHeaderText("Invalid Input");
						a.setResizable(true);
						a.setContentText("Could not process " + t.getText());
						a.showAndWait();
					}
				}
			});

			// TimeStep Slidebar
			Slider slider = new Slider();
			slider.setMaxWidth(200);
			slider.setMaxWidth(200);
			slider.setMin(1);
			slider.setMax(1000);
			slider.setValue(0);
			slider.setShowTickLabels(true);
			slider.setShowTickMarks(true);
			slider.setMajorTickUnit(100);
			slider.setMinorTickCount(5);
			userGrid.add(slider, 0, 2);

			// Textbox for slider
			TextField ts = new TextField();
			ts.setMaxWidth(60);
			ts.setMinWidth(60);
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

			// Time Step
			tStep.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent AE) {
					if (isInt(ts.getText())) {
						for (int i = 0; i < Integer.parseInt(ts.getText()); i++) {
							Critter.worldTimeStep();
						}
						update();
						Critter.displayWorld(grid);
					} else {
						Alert a = new Alert(AlertType.ERROR);
						a.setHeaderText("Invalid Input");
						a.setResizable(true);
						a.setContentText("Could not process " + ts.getText());
						a.showAndWait();
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
						int val = Integer.parseInt(seed.getText());
						if (val < 0)
							throw new Exception();
						Critter.setSeed(val);
						Alert a = new Alert(AlertType.INFORMATION);
						a.setTitle("Seed Set");
						a.setResizable(false);
						a.setHeaderText("Seed set to: " + seed.getText());
						a.showAndWait();
					} catch (Exception e) {
						Alert a = new Alert(AlertType.ERROR);
						a.setHeaderText("Invalid Input");
						a.setResizable(true);
						a.setContentText("Could not process " + seed.getText());
						a.showAndWait();
					}
				}
			});

			// Label for runStats output
			output.setWrapText(true);
			output.setTranslateY(80);
			output.setMaxWidth(900);
			output.setMaxHeight(1000);
			userGrid.add(output, 0, 4);

			// choice box for runStats
			// Parsing all valid Critter sub-types into the drop down menu
			if (classList != null) {
				for (int i = 0; i < classList.length; i++) {
					if (classList[i].endsWith(".class")) {
						try {
							Class<?> cType = Class
									.forName(packageName + "." + classList[i].substring(0, classList[i].length() - 6));
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
			}

			userGrid.add(box2, 0, 4);
			// box.setTranslateX(10);
			box2.setMaxWidth(90);
			box2.setMinWidth(90);

			// runStats button
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

			// Exit Button
			Button exit = new Button("Quit");
			userGrid.add(exit, 0, 6);
			exit.setMaxWidth(300);
			exit.setMinWidth(300);
			exit.setTranslateX(80);
			exit.setTranslateY(-130);
			exit.setStyle("-fx-background-color: skyblue;");
			exit.setOnMouseEntered(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent AE) {
					exit.setStyle("-fx-background-color: lightblue;");
				}
			});
			exit.setOnMouseExited(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent AE) {
					exit.setStyle("-fx-background-color: skyblue;");
				}
			});

			// Exit handler
			exit.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent AE) {
					try {
						System.exit(0);
					} catch (Exception e) {
					}
				}
			});
			// Slider for Animation steps/frame
			Slider sliderAn = new Slider();
			sliderAn.setMaxWidth(200);
			sliderAn.setMinWidth(200);
			sliderAn.setMin(1);
			sliderAn.setMax(100);
			sliderAn.setValue(0);
			sliderAn.setShowTickLabels(true);
			sliderAn.setShowTickMarks(true);
			sliderAn.setMajorTickUnit(100);
			sliderAn.setMinorTickCount(5);
			userGrid.add(sliderAn, 0, 5);

			// Textbox for slider
			TextField tsAn = new TextField();
			tsAn.setMaxWidth(60);
			tsAn.setMinWidth(60);
			tsAn.setTranslateX(-75);
			tsAn.setText("1");
			tsAn.setTranslateY(-5);
			userGrid.add(tsAn, 1, 5);

			// slider handler
			sliderAn.setOnMouseDragged(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent AE) {
					tsAn.setText(Integer.toString((int) sliderAn.getValue()));
				}
			});

			// Button for Animation
			Button run = new Button("Run");
			userGrid.add(run, 1, 5);
			run.setMaxWidth(100);
			run.setMinWidth(100);
			run.setTranslateX(10);
			run.setTranslateY(-5);
			run.setStyle("-fx-background-color: skyblue;");

			// Label unit for slider
			Label unit1 = new Label("steps/frame:");
			unit1.setMaxWidth(100);
			unit1.setMinWidth(100);
			unit1.setTranslateY(-30);
			userGrid.add(unit1, 0, 5);

			// Slider for Animation frames/sec
			Slider sliderAn2 = new Slider();
			sliderAn2.setMaxWidth(200);
			sliderAn2.setMaxWidth(200);
			sliderAn2.setMin(1);
			sliderAn2.setMax(10);
			sliderAn2.setValue(0);
			sliderAn2.setShowTickLabels(true);
			sliderAn2.setShowTickMarks(true);
			sliderAn2.setMajorTickUnit(1);
			sliderAn2.setMinorTickCount(0);
			sliderAn2.setTranslateY(70);
			sliderAn2.setSnapToTicks(true);
			userGrid.add(sliderAn2, 0, 5);

			// Label unit for slider
			Label unit2 = new Label("frames/sec:");
			unit2.setMaxWidth(100);
			unit2.setMinWidth(100);
			unit2.setTranslateY(40);
			userGrid.add(unit2, 0, 5);

			AnimationTimer timer1 = new repeat();

			// Animation button handler
			run.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent AE) {

					if (isInt(tsAn.getText())) {

						if (flag) {
							run.setStyle("-fx-background-color: skyblue;");
							run.setText("Run");
							flag = !flag;
							timer.cancel();
							timer1.stop();

							// Enabling All Buttons and bottom 2 sliders
							make.setDisable(false);
							make.setStyle("-fx-background-color: skyblue;");
							tStep.setDisable(false);
							tStep.setStyle("-fx-background-color: skyblue;");
							setSeed.setDisable(false);
							setSeed.setStyle("-fx-background-color: skyblue;");
							statsButton.setDisable(false);
							statsButton.setStyle("-fx-background-color: skyblue;");
							sliderAn.setDisable(false);
							sliderAn2.setDisable(false);
							box2.setDisable(false);
							box.setDisable(false);
							t.setDisable(false);
							slider.setDisable(false);
							ts.setDisable(false);
							seed.setDisable(false);
							exit.setDisable(false);

						} else {
							// Disabling All Buttons and bottom 2 sliders
							make.setDisable(true);
							make.setStyle("-fx-background-color: gray;");
							tStep.setDisable(true);
							tStep.setStyle("-fx-background-color: gray;");
							setSeed.setDisable(true);
							setSeed.setStyle("-fx-background-color: gray;");
							statsButton.setDisable(true);
							statsButton.setStyle("-fx-background-color: gray;");
							sliderAn.setDisable(true);
							sliderAn2.setDisable(true);
							box2.setDisable(true);
							box.setDisable(true);
							t.setDisable(true);
							slider.setDisable(true);
							ts.setDisable(true);
							seed.setDisable(true);
							exit.setDisable(true);

							// sets steps/frame
							timerSteps = Integer.parseInt(tsAn.getText());

							timer = new Timer();

							timer.scheduleAtFixedRate(new TimerTask() {

								@Override
								public void run() {
									timer1.start();
								}

							}, 0, 1000 / (int) sliderAn2.getValue());

							run.setStyle("-fx-background-color: orange;");
							run.setText("Stop");
							flag = !flag;
						}

					} else {
						Alert a = new Alert(AlertType.ERROR);
						a.setHeaderText("Invalid Input");
						a.setResizable(true);
						a.setContentText("Could not process " + tsAn.getText());
						a.showAndWait();
					}
				}
			});

		} catch (Exception e) {
		}
	}

	private boolean isInt(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	// Method that updates the runStats output
	private void update() {
		String packageName = this.getClass().getCanonicalName().substring(0, 11);

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
					Object value = m1.invoke(null, Critter.getInstances(box2.getValue()));
					output.setText((String) value);
				}

			}
		} catch (Exception e) {
		}
	}

	// Overrides the timer handler
	class repeat extends AnimationTimer {

		@Override
		public void handle(long now) {
			for (int i = 0; i < timerSteps; i++) {
				Critter.worldTimeStep();
			}
			
			Runnable r = new UpdateWorldThread();
			grid.getChildren().removeAll(Critter.shapes.values());
			Thread t = new Thread(r);
			//t.setDaemon(true);
			t.start();
			update();
			
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			t.stop();

			Critter.optimizedDisplay(grid);
			

	

			
			this.stop();
		}
	}
	
	Task updateWorld = new Task<Void>(){
		@Override public Void call(){
			Critter.displayWorld(grid);
			timerFlag=false;
			return null;
		}
	};

	
	
}