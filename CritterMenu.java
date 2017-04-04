/*
 * EE 422C Spring 2017, Quiz 6
 * Name: Ashkan Vafaee
 * UT EID: av28837
 * Unique: 16238
 */

/*
 * This program is correctly working.
 */
package assignment5;

import java.io.File;

import assignment5.Critter.TestCritter;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CritterMenu extends Application {

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		GridPane grid = new GridPane();
		Scene scene = new Scene(grid, 300, 250);
		primaryStage.setScene(scene);
		primaryStage.show();

		// Drop down menu listing choices of Critters
		ChoiceBox<String> box = new ChoiceBox<String>();
		String packageName = this.getClass().getCanonicalName().substring(0,11);
		System.out.println(packageName);

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
						box.getItems().add(classList[i].substring(0, classList[i].length() - 6));
						box.setValue((classList[i].substring(0, classList[i].length() - 6)));
					}
				} catch (Exception e) {
					continue;
				}
			}
		}
		grid.add(box, 0, 1);
		
		//Button to add Critter
		Button b = new Button("Add Critter");
		grid.add(b, 0, 2);
		
		//Textfield to specify number of Critters
		TextField t = new TextField();
		t.setPromptText("  #"); // grayed out text
		t.setMaxWidth(50);
		t.setMinWidth(50);
		grid.add(t, 2, 1);
		
		// make Critter handler
		b.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent AE) {
				try {
					for (int i = 0; i < Integer.parseInt(t.getText()); i++) {
						Critter.makeCritter(box.getValue());
					}
					Critter.displayWorld(grid);
				} catch (InvalidCritterException e) {
				}
			}
		});
		

		
		

	}

}
