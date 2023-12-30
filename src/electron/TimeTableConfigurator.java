package electron;

import org.json.simple.JSONObject;

import electron.data.outFile;
import electron.utils.DayMethods;
import electron.utils.logger;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TimeTableConfigurator extends Application{
	public static Stage st;
	public static void main(String[] args) {
		logger.enDebug=true;
		logger.log("Starting TimeTableConfigurator...");
		outFile.load();
		logger.loadConsole();
		launch(args);
	}
    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(FXMLLoader.load(this.getClass().getClassLoader().getResource("electron/resources/mainstage.fxml")));
        stage.setTitle("TimeTableConfigurator");
        stage.setScene(scene);
        Image img = new Image(this.getClass().getClassLoader().getResourceAsStream("electron/resources/icon.png"));
        stage.getIcons().add(img);
        stage.setOnCloseRequest( event -> {logger.log("Closing proram..."); System.exit(0);} );
        stage.show();
        st=stage;
    }
}
