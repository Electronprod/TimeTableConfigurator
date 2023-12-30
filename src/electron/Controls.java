package electron;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONObject;

import com.sun.tools.javac.util.StringUtils;

import electron.csv.CSV;
import electron.data.FileOptions;
import electron.data.outFile;
import electron.preview.PreviewLauncher;
import electron.utils.DayMethods;
import electron.utils.Other;
import electron.utils.logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.scene.control.Alert.AlertType;

public class Controls {
	/*
	 * Variables
	 */
	private int CurrentDayID = 0;
	private String CurrentClass = null;
	@FXML
	private Label statusbar;
	@FXML
	private CheckMenuItem toggleapibtn;
	@FXML
	private CheckMenuItem togglesitebtn;
	@FXML
	private ListView<String> list;
	@FXML
	private ComboBox studentbox;
	@FXML
	private ComboBox<String> daybox;
	@FXML
	private ComboBox classbox;
	@FXML
	private TextField lessonnamefieldobj;
	@FXML
	private TextField teacherfieldobj;
	@FXML
	private TextField lessontimefieldobj;
	
	 /*
     * Add/Delete events
     */
    @FXML
    private void addclass() {
    	logger.log("[EVENT]: triggered addClass.");
    	TextInputDialog inputdialog = new TextInputDialog();
    	inputdialog.setContentText("Class name: ");
    	inputdialog.setHeaderText("Enter class you want to add.");
    	inputdialog.showAndWait();
    	boolean result = outFile.createClass(inputdialog.getEditor().getText());
    	if(!result) {
    		new Alert(Alert.AlertType.ERROR, "This class already exists!").show();
    	}else {
    		new modAlert(Alert.AlertType.INFORMATION, "Added new class successfully").Show();
    		statusbar.setText("Added new class successfully.");
    	}
    }
    @FXML
    private void delclass() {
    	logger.log("[EVENT]: triggered delClass.");
		String cl = String.valueOf(classbox.getValue());
    	if(cl==null) {
    		new Alert(AlertType.ERROR,"Selected value equals null!\n Please, select class you want to delete.").show();;
    		return;
    	}
    	if(showConfirmDialog("Confirmation","You sure?","Are you sure you want to delete class "+cl+"?")) {
    		boolean result = outFile.removeClass(cl);
    		if(!result) {
    			new Alert(Alert.AlertType.ERROR, "Class not found.").show();
    			statusbar.setText("Error deleting class: not found.");
    		}else {
    			statusbar.setText("Deleted class sucessfully");
    			new modAlert(Alert.AlertType.INFORMATION, "Deleted class successfully").Show();
    		}
    	}else {
    		statusbar.setText("Action 'deleteClass' canceled.");
    	}
    }
    private boolean showConfirmDialog(String title,String header,String contenttext) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(contenttext);
        Optional<ButtonType> buttonType = alert.showAndWait();
        if(buttonType.isPresent() && buttonType.get().equals(ButtonType.OK)) {
            return true;
        }
        return false;
    }
    @FXML
    private void addlesson() {
    	String lesson = lessonnamefieldobj.getText();
    	String teacher = teacherfieldobj.getText();
    	String time = lessontimefieldobj.getText();
    	boolean result = outFile.addLesson(teacher, lesson, "ALL", time, CurrentDayID, CurrentClass);
    	if(!result) {
    		new Alert(AlertType.ERROR,"Error adding lesson.");
    		statusbar.setText("Adding lesson failed.");
    		return;
    	}
    	updateList();
    	lessonnamefieldobj.setText("");
    	teacherfieldobj.setText("");
    	lessontimefieldobj.setText("");
    	statusbar.setText("Added lesson successfully.");
    }
    @FXML
    private void dellesson() {
    	if(!list.getSelectionModel().getSelectedItem().toString().contains(" | ")) {
    		new Alert(AlertType.ERROR,"Incorrect input. \nMake sure, that you selected lesson to delete");
    		return;
    	}
    	String[] selected = list.getSelectionModel().getSelectedItem().toString().replace(" | ", "/").split("/");
    	String time = selected[0];
    	String lesson = selected[1];
    	logger.debug(time+" | "+lesson);
    	if(outFile.removeLesson(CurrentDayID, CurrentClass, time, lesson)) {
    		new modAlert(Alert.AlertType.INFORMATION, "Deleted lesson successfully").Show();
    	}else {
    		new Alert(AlertType.ERROR,"Error finding lesson in database.");
    		return;
    	}
    	updateList();
    	statusbar.setText("Deleted lesson successfully.");
    }
    @FXML
    private void selectDay() {
    	if(daybox.getValue() == null) {return;}
    	int dayID = DayMethods.getDayByName(daybox.getValue());
    	logger.log("[EVENT]: selectDay event triggered. DayID: "+dayID);
    	statusbar.setText("Selected dayID: "+dayID);
    	CurrentDayID = dayID;
    	updateList();
    }
    @FXML
    private void daySelectorUpdate() {
    	logger.log("[EVENT]: daySelectorUpdate event triggered.");
    	daybox.setItems(DayMethods.generateDays());
    }
    @FXML
    private void selectClass() {
    	if(classbox.getValue() == null) {return;}
    	String classname = classbox.getValue().toString().toLowerCase();
    	logger.log("[EVENT]: selectClass event triggered. Class: "+classname);
    	statusbar.setText("Selected class: "+classname);
    	CurrentClass = classname;
    	updateList();
    }
    @FXML
    private void classSelectorUpdate() {
    	logger.log("[EVENT]: classSelectorUpdate event triggered.");
    	classbox.setItems(outFile.getClasses());
    }
    /**
     * Update ListView items
     */
    public void updateList() {
    	if(CurrentClass == null | CurrentDayID == 0) {
    		ObservableList<String> items = FXCollections.observableArrayList();
    		items.add("Select second option please");
    		list.setItems(items);
    		return;
    	}
    	ObservableList<String> items = outFile.getItems(CurrentClass, CurrentDayID);
    	if(items.size()==0) {
    		items.add("Nothing");
    		list.setItems(items);
    	}else {
    		list.setItems(items);
    	}
    	logger.log("[ListView]: updated items.");
    }
    @FXML
    private void selectStudent() {}
    @FXML
    private void studentsUpdate() {
    	logger.log("[EVENT]: studentsUpdate event triggered.");
    	//daybox.setItems();
    }
    
    /*
     * Top menu events
     */
    @FXML
    private void menuOpened() {
    	logger.log("[EVENT]: settingopened event triggered.");
		togglesitebtn.setSelected(!outFile.getSiteState());
		toggleapibtn.setSelected(!outFile.getApiState());
    }
    @FXML
    private void togglesite() {
    	logger.log("[EVENT]: togglesite event triggered.");
    	JSONObject siteobj = (JSONObject) outFile.getConfig().get("site");
    	outFile.toggleSiteState();
    	siteobj.remove("enabled");
    	siteobj.put("enabled", outFile.getSiteState());
    	logger.log("[EVENT]: toggled state to "+outFile.getSiteState());
    	statusbar.setText("Toggled state to "+outFile.getSiteState());
    }
    @FXML
    private void toggleapi() {
    	logger.log("[EVENT]: toggleapi event triggered.");
    	JSONObject siteobj = (JSONObject) outFile.getConfig().get("api");
    	outFile.toggleApiState();
    	siteobj.remove("enabled");
    	siteobj.put("enabled", outFile.getApiState());
    	logger.log("[EVENT]: toggled state to "+outFile.getApiState());
    	statusbar.setText("Toggled state to "+outFile.getApiState());
    }
    @FXML
    private void closebtn() {
    	statusbar.setText("Thank you. Bye.");
    	logger.log("Thank you. Bye.");
    	System.exit(0);
    }
    @FXML
    private void about() throws IOException {
    	InputStream url = this.getClass().getClassLoader().getResourceAsStream("electron/resources/ABOUT.txt");
    	Alert alert = new Alert(Alert.AlertType.INFORMATION,FileOptions.getInternalFileLineWithSeparator(url,"\n"));
    	alert.setTitle("About");
    	alert.setHeaderText("About TimeTableConfigurator");
    	alert.show();
    	statusbar.setText("Showed about info");
    }
    @FXML
    private void licenseinfo() throws IOException {
    	InputStream url = this.getClass().getClassLoader().getResourceAsStream("electron/resources/LICENSE.txt");
    	Alert alert = new Alert(Alert.AlertType.INFORMATION,FileOptions.getInternalFileLineWithSeparator(url,"\n"));
    	alert.setTitle("License");
    	alert.setHeaderText("MIT License");
    	alert.show();
    	statusbar.setText("Showed license info");
    }
    @FXML
    private void changeApiPort() {
    	TextInputDialog inputdialog = new TextInputDialog(outFile.getApiPort()+"");
    	inputdialog.setContentText("PORT: ");
    	inputdialog.setHeaderText("API port changer");
    	inputdialog.showAndWait();
    	if(!Other.isNum(inputdialog.getEditor().getText())) {
    		Alert alert = new Alert(Alert.AlertType.ERROR,"Incorrect input. Try again.\nYou can enter only numbers");
    		alert.show();
    		return;
    	}
    	int apiport = Integer.parseInt(inputdialog.getEditor().getText());
    	outFile.setApiPort(apiport);
    	statusbar.setText("Changed API port to: "+ apiport);	
    }
    @FXML
    private void changeSitePort() {
    	TextInputDialog inputdialog = new TextInputDialog(outFile.getSitePort()+"");
    	inputdialog.setContentText("PORT: ");
    	inputdialog.setHeaderText("Site port changer");
    	inputdialog.showAndWait();
    	if(!Other.isNum(inputdialog.getEditor().getText())) {
    		Alert alert = new Alert(Alert.AlertType.ERROR,"Incorrect input. Try again.\nYou can enter only numbers");
    		alert.show();
    		return;
    	}
    	int siteport = Integer.parseInt(inputdialog.getEditor().getText());
    	outFile.setSitePort(siteport);
    	statusbar.setText("Changed Site port to: "+ siteport);	
    }
    
    /*
     * Others
     */
    @FXML
    private void showPreview() {
    	try {
			new PreviewLauncher();
			statusbar.setText("Showed preview");
		} catch (IOException e) {
			e.printStackTrace();
			new Alert(Alert.AlertType.ERROR,"Error showing preview: "+e.getMessage());
		}
    }
    @FXML
    private void importAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Document");
        FileChooser.ExtensionFilter extFilter = 
        new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(TimeTableConfigurator.st);
        if (file != null) {
            statusbar.setText("Opening file: "+file.getName());
            CSV.parse(file,this);
        }else {
        	statusbar.setText("You canceled action 'import'");
        }
    }
}


class modAlert extends Alert {
	 
    private Thread thread;
    private javafx.stage.Window window;

    public modAlert(AlertType alertType, String contentText) {
        super(alertType, contentText);
        setHeaderText("Alert");
        window = getDialogPane().getScene().getWindow();
        thread = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            }
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 1000) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {
                }
                Platform.runLater(() -> window.setOpacity(Math.max(0, window.getOpacity() - .05)));
            }
            Platform.runLater(this::close);
        });

    }

    void Show() {
        show();
        thread.start();
    }
}