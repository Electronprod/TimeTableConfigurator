package electron.students;

import java.io.IOException;

import electron.utils.logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class addStudentGui {
	@FXML
	private TextField tfield;
	public addStudentGui() throws IOException {
        Scene scene = new Scene(FXMLLoader.load(this.getClass().getClassLoader().getResource("electron/resources/studentstage.fxml")));
		Stage studentgui = new Stage();
		studentgui.setTitle("Add students");
		Image img = new Image(this.getClass().getClassLoader().getResourceAsStream("electron/resources/icon.png"));
		studentgui.getIcons().add(img);
		studentgui.setOnCloseRequest( event -> {logger.log("Closing proram..."); System.exit(0);} );
		studentgui.showAndWait();
	}
	@FXML
	private void save() {
		logger.log("[EVENT]: studentgui: triggered SAVE event.");
	}
	@FXML
	private void cancel() {
		logger.log("[EVENT]: studentgui: triggered CANCEL event.");
	}
	
}
