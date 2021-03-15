package portfolio.views;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

import portfolio.controllers.MainViewController;
import portfolio.controllers.SettingsController;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import portfolio.controllers.TransactionController;

public class SettingsView implements Initializable {
    public Button btnSaveAndApply;
    public Label labelLanguage;
    public Label CSV;
    public Label prefferedCurrency;
    public Label prefferedStyle;
    public Label labelDec;
    public Label lblLaunchDefid;
    public Label lblLaunchSync;
    public Label lblDeleteData;
    public AnchorPane anchorPane;
    @FXML
    public StackPane stack;

    @FXML
    public StackPane stackSync;
    @FXML
    public Button switchButton;
    @FXML
    public Button switchButtonSync;
    @FXML
    public Button btnDeleteData;
    @FXML
    private ComboBox<String> cmbLanguage, cmbPrefCurrency, cmbDecSeperator, cmbCSVSeperator, cmbPrefferedStyle;
    SettingsController settingsController = SettingsController.getInstance();

    public void btnSaveAndApplyPressed() {
        this.settingsController.saveSettings();
        Stage stage = (Stage) btnSaveAndApply.getScene().getWindow();
        stage.close();
    }

    public void btnDeletePressed() {
        boolean result = false;
        try {
            result = Files.deleteIfExists(new File(SettingsController.getInstance().DEFI_PORTFOLIO_HOME + "/transactionData.portfolio").toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(result == true){
            TransactionController.getInstance().clearTransactionList();
            TransactionController.getInstance().clearPortfolioList();
            MainViewController.getInstance().plotUpdate(MainViewController.getInstance().mainView.tabPane.getSelectionModel().getSelectedItem().getText());
            MainViewController.getInstance().strCurrentBlockLocally.set("0");
        }else{
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.labelLanguage.setText(this.settingsController.translationList.getValue().get("LanguageLabel").toString());
      //  this.CSV.setText(this.settingsController.translationList.getValue().get("CSV").toString());
        this.prefferedCurrency.setText(this.settingsController.translationList.getValue().get("PrefferedCurrency").toString());
      //  this.labelDec.setText(this.settingsController.translationList.getValue().get("Decimal").toString());
        this.lblLaunchDefid.setText(this.settingsController.translationList.getValue().get("LaunchDefid").toString());
        this.lblLaunchSync.setText(this.settingsController.translationList.getValue().get("LaunchSync").toString());
        this.cmbLanguage.getItems().addAll(this.settingsController.languages);
        this.cmbLanguage.valueProperty().bindBidirectional(this.settingsController.selectedLanguage);
        this.lblDeleteData.setText(this.settingsController.translationList.getValue().get("DeleteLabel").toString());
        this.btnDeleteData.setText(this.settingsController.translationList.getValue().get("DeleteButton").toString());

        this.cmbPrefCurrency.getItems().addAll(this.settingsController.currencies);
        this.cmbPrefCurrency.valueProperty().bindBidirectional(this.settingsController.selectedFiatCurrency);

//        this.cmbDecSeperator.getItems().addAll(this.settingsController.decSeperators);
//        this.cmbDecSeperator.valueProperty().bindBidirectional(this.settingsController.selectedDecimal);
//
//        this.cmbCSVSeperator.getItems().addAll(this.settingsController.csvSeperators);
//        this.cmbCSVSeperator.valueProperty().bindBidirectional(this.settingsController.selectedSeperator);

        this.cmbPrefferedStyle.getItems().addAll(this.settingsController.styleModes);
        this.cmbPrefferedStyle.valueProperty().bindBidirectional(this.settingsController.selectedStyleMode);


        this.SwitchButton();
        this.SwitchButtonSync();
    }

    public void changeLanguage() {
        this.labelLanguage.setText(this.settingsController.translationList.getValue().get("LanguageLabel").toString());
   //     this.CSV.setText(this.settingsController.translationList.getValue().get("CSV").toString());
        this.prefferedCurrency.setText(this.settingsController.translationList.getValue().get("PrefferedCurrency").toString());
  //      this.labelDec.setText(this.settingsController.translationList.getValue().get("Decimal").toString());
        this.lblLaunchDefid.setText(this.settingsController.translationList.get().get("LaunchDefid").toString());
        this.lblLaunchSync.setText(this.settingsController.translationList.getValue().get("LaunchSync").toString());
        this.lblDeleteData.setText(this.settingsController.translationList.getValue().get("DeleteLabel").toString());
        this.btnDeleteData.setText(this.settingsController.translationList.getValue().get("DeleteButton").toString());
    }

    private final Rectangle back = new Rectangle(35, 15, Color.RED);
    private final Rectangle backSync = new Rectangle(35, 15, Color.RED);
    private final String buttonStyleOff = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 0.2, 0.0, 0.0, 2); -fx-background-color: #d6cecc;";
    private final String buttonStyleOn = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 0.2, 0.0, 0.0, 2); -fx-background-color: #FF00AF;"; //00893d

    private void init() {
        stack.getChildren().addAll(back, switchButton);
        stack.setMinSize(35, 15);
        back.maxWidth(35);
        back.minWidth(30);
        back.maxHeight(15);
        back.minHeight(10);
        back.setArcHeight(back.getHeight());
        back.setArcWidth(back.getHeight());
        back.setFill(Color.valueOf("#d6cecc"));//Grau
        Double r = 3.0;
        switchButton.setShape(new Circle(r));

        switchButton.setMaxSize(20, 20);
        switchButton.setMinSize(20, 20);

        if (this.settingsController.selectedLaunchDefid) {
            switchButton.setStyle(buttonStyleOn);
            back.setFill(Color.valueOf("#FF00AF"));//Weiß
            StackPane.setAlignment(switchButton, Pos.CENTER_RIGHT);
        } else {
            switchButton.setStyle(buttonStyleOff);
            back.setFill(Color.valueOf("#d6cecc"));//Rosa
            StackPane.setAlignment(switchButton, Pos.CENTER_LEFT);

        }
    }

    private void initSync() {
        stackSync.getChildren().addAll(backSync, switchButtonSync);
        stackSync.setMinSize(35, 15);
        backSync.maxWidth(35);
        backSync.minWidth(30);
        backSync.maxHeight(15);
        backSync.minHeight(10);
        backSync.setArcHeight(backSync.getHeight());
        backSync.setArcWidth(backSync.getHeight());
        backSync.setFill(Color.valueOf("#d6cecc"));//Grau
        Double r = 3.0;
        switchButtonSync.setShape(new Circle(r));

        switchButtonSync.setMaxSize(20, 20);
        switchButtonSync.setMinSize(20, 20);

        if (this.settingsController.selectedLaunchSync) {
            switchButtonSync.setStyle(buttonStyleOn);
            backSync.setFill(Color.valueOf("#FF00AF"));//Weiß
            StackPane.setAlignment(switchButtonSync, Pos.CENTER_RIGHT);
        } else {
            switchButtonSync.setStyle(buttonStyleOff);
            backSync.setFill(Color.valueOf("#d6cecc"));//Rosa
            StackPane.setAlignment(switchButtonSync, Pos.CENTER_LEFT);

        }
    }

    public void updateSwitchButton() {

        if (this.settingsController.selectedLaunchDefid) {
            switchButton.setStyle(buttonStyleOff);
            back.setFill(Color.valueOf("#d6cecc"));//Weiß
            StackPane.setAlignment(switchButton, Pos.CENTER_LEFT);
            this.settingsController.selectedLaunchDefid = false;
        } else {
            switchButton.setStyle(buttonStyleOn);
            back.setFill(Color.valueOf("#FF00AF"));//Rosa
            StackPane.setAlignment(switchButton, Pos.CENTER_RIGHT);
            this.settingsController.selectedLaunchDefid = true;
        }

    }

    public void updateSwitchButtonSync() {

        if (this.settingsController.selectedLaunchSync) {
            switchButtonSync.setStyle(buttonStyleOff);
            backSync.setFill(Color.valueOf("#d6cecc"));//Weiß
            StackPane.setAlignment(switchButtonSync, Pos.CENTER_LEFT);
            this.settingsController.selectedLaunchSync = false;
        } else {
            switchButtonSync.setStyle(buttonStyleOn);
            backSync.setFill(Color.valueOf("#FF00AF"));//Rosa
            StackPane.setAlignment(switchButtonSync, Pos.CENTER_RIGHT);
            this.settingsController.selectedLaunchSync = true;
        }

    }

    public void SwitchButton() {
        init();
        EventHandler<Event> click = new EventHandler<Event>() {
            @Override
            public void handle(Event e) {
                updateSwitchButton();
            }
        };
        switchButton.setFocusTraversable(false);
        switchButton.setOnMouseClicked(click);
        stack.setOnMouseClicked(click);
    }


    private void SwitchButtonSync() {
        initSync();
        EventHandler<Event> click = new EventHandler<Event>() {
            @Override
            public void handle(Event e) {
                updateSwitchButtonSync();
            }
        };
        switchButtonSync.setFocusTraversable(false);
        switchButtonSync.setOnMouseClicked(click);
        stackSync.setOnMouseClicked(click);
    }

}





