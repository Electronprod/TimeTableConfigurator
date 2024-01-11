package electron;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import electron.data.outFile;
import electron.utils.logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import library.electron.updatelib.ActionListener;
import library.electron.updatelib.UpdateLib;

public class TimeTableConfigurator extends Application{
	static final Double version = 1.1;
	public static Stage st;
	public static void main(String[] args) throws MalformedURLException {
		//Parsing arguments
        Map<String, String> arguments = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-")) {
                if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                    arguments.put(args[i].substring(1), args[i + 1]);
                    i++;
                } else {
                    arguments.put(args[i].substring(1), null);
                }
            }
        }
        if(arguments.containsKey("debug")) {
        	logger.enDebug=true;
		}
		logger.log("Starting TimeTableConfigurator...");
		outFile.load();
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
        checkUpdates();
        st=stage;
    }
	private static void checkUpdates() throws MalformedURLException {
		UpdateLib updater = new UpdateLib("https://api.github.com/repos/Electronprod/TimeTableConfigurator/releases");
		updater.setActionListener(new ActionListener() {
            @Override
            public void reveivedUpdates() {
            	String versionobj = updater.getLastVersionJSON();
            	String lastversion = updater.getTagName(versionobj);
            	double newversion = Double.parseDouble(lastversion.replace("v", ""));
            	if(newversion>version) {
            		logger.error("-----------------[Update]-----------------");
            		logger.error("New version available: "+lastversion);
            		logger.error("Release notes:");
            		logger.error(updater.getBody(versionobj));
            		logger.error("");
            		logger.error("Publish date: "+updater.getPublishDate(versionobj));
            		logger.error("");
            		logger.error("Download it:");
            		logger.error(updater.getReleaseUrl(versionobj));
            		logger.error("-----------------[Update]-----------------");
            		JOptionPane.showMessageDialog(new JFrame(), "New version available: "+lastversion+"\nRelease notes:\n"+updater.getBody(versionobj)+"\n\nPublish date: "+updater.getPublishDate(versionobj), "New version available", JOptionPane.WARNING_MESSAGE);
            	}else {
            		logger.log("[Updater] it's latest version.");
            	}
            }

			@Override
			public void updateFailed() {
				System.err.println("[Updater] error checking updates.");
			}
        });
	}
}
