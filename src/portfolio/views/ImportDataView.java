package portfolio.views;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import portfolio.controllers.CheckConnection;
import portfolio.controllers.MainViewController;
import portfolio.controllers.SettingsController;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;

public class ImportDataView {

    @FXML
    public Button btnUpdateData,btnCakeCSV,btnWalletCSV,btnClose;
    @FXML
    public CheckBox cmbSaveDefault;


    public void btnUpdateDataPressed(){


        MainViewController.getInstance().settingsController.selectedLaunchSync = true;
        MainViewController.getInstance().transactionController.startServer();
        MainViewController.getInstance().settingsController.runCheckTimer = true;
        Timer checkTimer = new Timer("");
        if (SettingsController.getInstance().getPlatform().equals("mac")) {
            try {
                FileWriter myWriter = new FileWriter(System.getProperty("user.dir") + "/PortfolioData/" + "update.portfolio");
                myWriter.write(MainViewController.getInstance().settingsController.translationList.getValue().get("ConnectNode").toString());
                myWriter.close();
                try {
                    Process ps = null;
                    ps = Runtime.getRuntime().exec("./jre/bin/java -Xdock:icon=icons.icns -jar UpdateData.jar " + MainViewController.getInstance().settingsController.selectedStyleMode.getValue().replace(" ", ""));
                } catch (IOException r) {
                    SettingsController.getInstance().logger.warning("Exception occured: " + r.toString());
                }
            } catch (IOException h) {
                SettingsController.getInstance().logger.warning("Could not write to update.portfolio.");
            }
        } else {
            MainViewController.getInstance().transactionController.updateJFrame();
            MainViewController.getInstance().transactionController.jl.setText(MainViewController.getInstance().settingsController.translationList.getValue().get("ConnectNode").toString());
        }
        checkTimer.scheduleAtFixedRate(new CheckConnection(MainViewController.getInstance()), 0, 30000);
        this.btnClosePressed();
    }
    public void btnCakeCSVPressed(){
        this.btnClosePressed();

    }
    public void btnWalletCSVPressed(){
        this.btnClosePressed();

    }
    public void btnClosePressed(){
        Stage stage = (Stage) btnUpdateData.getScene().getWindow();
        stage.close();
    }
    public void defaultChanged(){
        SettingsController.getInstance().saveSettings();
    }

}
