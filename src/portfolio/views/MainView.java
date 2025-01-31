package portfolio.views;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.*;
import java.time.LocalDate;
import java.util.*;

import javafx.stage.StageStyle;
import javafx.util.Callback;
import portfolio.controllers.CheckConnection;
import portfolio.controllers.SettingsController;
import portfolio.controllers.TransactionController;
import portfolio.models.PoolPairModel;
import portfolio.models.TransactionModel;
import portfolio.controllers.MainViewController;

public class MainView implements Initializable {
    @FXML
    public NumberAxis yAxis;
    @FXML
    public AnchorPane mainAnchorPane;
    @FXML
    public AnchorPane leftAnchorPane;
    @FXML
    public Button btnRawData;
    @FXML
    public Button btnAnalyse;
    @FXML
    public Button btnUpdateDatabase;
    @FXML
    public Pane anchorPanelAnalyse, anchorPanelRawData;
    @FXML
    public Label strCurrentBlockLocally, strCurrentBlockOnBlockchain, strLastUpdate;
    @FXML
    public ComboBox<String> cmbCoins, cmbIntervall, cmbFiat, cmbPlotCurrency, cmbCoinsCom, cmbIntervallCom, cmbFiatCom, cmbPlotCurrencyCom, cmbIntervallOver;
    @FXML
    public ImageView coinImageRewards, coinImageCommissions;
    @FXML
    public DatePicker dateFrom = new DatePicker();
    @FXML
    public DatePicker dateTo = new DatePicker();
    @FXML
    public DatePicker dateFromCom = new DatePicker();
    @FXML
    public DatePicker dateToCom = new DatePicker();
    @FXML
    public DatePicker dateFromOver = new DatePicker();
    @FXML
    public DatePicker dateToOver = new DatePicker();
    @FXML
    public TabPane tabPane = new TabPane();
    @FXML
    public LineChart<Number, Number> plotRewards, plotCommissions1, plotCommissions2;
    @FXML
    public PieChart plotPortfolio1;
    @FXML
    public StackedAreaChart<Number, Number> plotOverview;
    @FXML
    public TableView<TransactionModel> rawDataTable;
    @FXML
    public TableView<PoolPairModel> plotTable;
    @FXML
    public TableColumn<TransactionModel, Long> blockTimeColumn;
    @FXML
    public TableColumn<TransactionModel, String> typeColumn;
    @FXML
    public TableColumn<TransactionModel, Double> cryptoValueColumn;
    @FXML
    public TableColumn<TransactionModel, String> cryptoCurrencyColumn;
    @FXML
    public TableColumn<TransactionModel, String> blockHashColumn;
    @FXML
    public TableColumn<TransactionModel, Integer> blockHeightColumn;
    @FXML
    public TableColumn<TransactionModel, String> poolIDColumn;
    @FXML
    public TableColumn<TransactionModel, String> ownerColumn;
    @FXML
    public TableColumn<TransactionModel, Double> fiatValueColumn;
    @FXML
    public TableColumn<TransactionModel, String> fiatCurrencyColumn;
    @FXML
    public TableColumn<TransactionModel, String> transactionColumn;
    @FXML
    public TableColumn<PoolPairModel, String> timeStampColumn;
    @FXML
    public TableColumn<PoolPairModel, Double> crypto1Column;
    @FXML
    public TableColumn<PoolPairModel, Double> crypto1FiatColumn;
    @FXML
    public TableColumn<PoolPairModel, Double> Commission2OverviewColumn;
    @FXML
    public TableColumn<PoolPairModel, Double> Commission2OverviewFiatColumn;
    @FXML
    public TableColumn<PoolPairModel, Double> crypto2Column;
    @FXML
    public TableColumn<PoolPairModel, Double> crypto2FiatColumn;
    @FXML
    public TableColumn<PoolPairModel, Double> fiatColumn;
    @FXML
    public TableColumn<PoolPairModel, String> poolPairColumn;
    @FXML
    public TableColumn<PoolPairModel, String> balanceFiatColumn;
    public Label CurrentBlock;
    public Label CurrentBlockChain;
    public Label LastUpdate;
    public Tab Rewards;
    public Tab Commissions;
    public Tab Overview;
    public Label StartDate;
    public Label EndDate;
    public Label EndDateCom;
    public Label StartDateCom;
    public Label StartDateOver;
    public Label EndDateOver;

    public MenuItem menuItemCopySelected = new MenuItem("Copy");
    public MenuItem menuItemCopyHeaderSelected = new MenuItem("Copy with header");
    public MenuItem menuItemExportSelected = new MenuItem("Export selected to CSV");
    public MenuItem menuItemExportAllSelected = new MenuItem("Export all to CSV");
    public MenuItem menuItemExportAllDailySelected = new MenuItem("Export all to CSV (Daily cumulated)");
    public MenuItem menuItemCopySelectedPlot = new MenuItem("Copy");
    public MenuItem menuItemCopyHeaderSelectedPlot = new MenuItem("Copy with header");
    public MenuItem menuItemExportSelectedPlot = new MenuItem("Export selected to CSV");
    public MenuItem menuItemExportAllSelectedPlot = new MenuItem("Export all to CSV");


    public Stage settingsStage, helpStage, donateStage;
    public boolean init = true;
    public Button btnSettings;
    public Button btnHelp;
    public Button btnDonate;
    public Label connectionLabel;
    public Button btnConnect;
    public Tab Portfolio;
    public Label UpdateText;
    public PieChart plotPortfolio11;
    public Label fieldTotal,fieldTotalYield,fieldTotalYieldRewards,fieldTotalYieldCommissions, tokenLabel,tokenLabelLM;
    MainViewController mainViewController = MainViewController.getInstance();

    public MainView() {
    }

    public void updateHeader() {

        timeStampColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Date").toString());
        poolPairColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("PoolPair").toString());

        if (tabPane.getSelectionModel().getSelectedItem().getText().equals("Portfolio")) {
            timeStampColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Token").toString());
            poolPairColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("CryptoValue").toString());
            balanceFiatColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("FIATValue").toString()+" (" + mainViewController.settingsController.selectedFiatCurrency.getValue()+")");
            crypto1Column.setVisible(false);
            crypto1FiatColumn.setVisible(false);
            crypto2Column.setVisible(false);
            crypto2FiatColumn.setVisible(false);
            Commission2OverviewColumn.setVisible(false);
            Commission2OverviewFiatColumn.setVisible(false);
            fiatColumn.setVisible(false);
            balanceFiatColumn.setVisible(true);
        }
        if (tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Overview"))) {
            crypto1Column.setText(this.mainViewController.settingsController.translationList.getValue().get("Rewards").toString());
            crypto1FiatColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Rewards") + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
            crypto2Column.setText(this.mainViewController.settingsController.translationList.getValue().get("Commissions") + " DFI");
            crypto2FiatColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Commissions") + " DFI(" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
            Commission2OverviewColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Commissions") + " 2");
            Commission2OverviewFiatColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Commissions") + " 2(" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");

            balanceFiatColumn.setVisible(false);
            crypto1Column.setVisible(true);
            crypto1FiatColumn.setVisible(true);
            crypto2Column.setVisible(true);
            crypto2FiatColumn.setVisible(true);
            Commission2OverviewColumn.setVisible(true);
            Commission2OverviewFiatColumn.setVisible(true);
            fiatColumn.setVisible(true);
        }
        if (tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Rewards"))) {
            crypto1Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[1]);
            crypto1FiatColumn.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[1] + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
            crypto1Column.setVisible(true);
            balanceFiatColumn.setVisible(false);
            crypto1FiatColumn.setVisible(true);
            crypto2Column.setVisible(false);
            crypto2FiatColumn.setVisible(false);
            Commission2OverviewColumn.setVisible(false);
            Commission2OverviewFiatColumn.setVisible(false);
            fiatColumn.setVisible(false);
        }
        if (tabPane.getSelectionModel().getSelectedItem().getText().equals(this.mainViewController.settingsController.translationList.getValue().get("Commissions"))) {
            crypto1Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[1]);
            crypto1FiatColumn.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[1] + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
            crypto2Column.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[0]);
            crypto2FiatColumn.setText(mainViewController.settingsController.selectedCoin.getValue().split("-")[0] + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");

            crypto1Column.setVisible(true);
            crypto1FiatColumn.setVisible(true);
            crypto2Column.setVisible(true);
            crypto2FiatColumn.setVisible(true);
            balanceFiatColumn.setVisible(false);
            Commission2OverviewColumn.setVisible(false);
            Commission2OverviewFiatColumn.setVisible(false);
            fiatColumn.setVisible(true);
        }
    }

    public void btnAnalysePressed() {
        this.anchorPanelAnalyse.toFront();
          if (!this.init) {
            this.mainViewController.plotUpdate(this.tabPane.getSelectionModel().getSelectedItem().getId());
            this.updateHeader();
        }
    }

    public void btnRawDataPressed() {
        this.anchorPanelRawData.toFront();
    }

    public void helpPressed() throws IOException {

        if (helpStage != null) helpStage.close();
        final Delta dragDelta = new Delta();
        Parent root = FXMLLoader.load(getClass().getResource("HelpView.fxml"));
        Scene scene = new Scene(root);

        helpStage = new Stage();
        helpStage.initStyle(StageStyle.UNDECORATED);
        scene.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = helpStage.getX() - mouseEvent.getScreenX();
            dragDelta.y = helpStage.getY() - mouseEvent.getScreenY();
        });
        scene.setOnMouseDragged(mouseEvent -> {
            helpStage.setX(mouseEvent.getScreenX() + dragDelta.x);
            helpStage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
        helpStage.getIcons().add(new Image(new File(System.getProperty("user.dir") + "/defi-portfolio/src/icons/help.png").toURI().toString()));
        helpStage.setTitle((this.mainViewController.settingsController.translationList.getValue().get("HelpTitle").toString()));
        helpStage.setScene(scene);
        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
            double stageWidth = newValue.doubleValue();
            helpStage.setX(mainAnchorPane.getScene().getWindow().getX() + mainAnchorPane.getScene().getWindow().getWidth() / 2 - stageWidth / 2);
        };
        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
            double stageHeight = newValue.doubleValue();
            helpStage.setY(mainAnchorPane.getScene().getWindow().getY() + mainAnchorPane.getScene().getWindow().getHeight() / 2 - stageHeight / 2);
        };

        helpStage.widthProperty().addListener(widthListener);
        helpStage.heightProperty().addListener(heightListener);

        helpStage.setOnShown(e -> {
            helpStage.widthProperty().removeListener(widthListener);
            helpStage.heightProperty().removeListener(heightListener);
        });
        helpStage.show();

        java.io.File darkMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/darkMode.css");
        java.io.File lightMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/lightMode.css");
        if (this.mainViewController.settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            helpStage.getScene().getStylesheets().add(darkMode.toURI().toString());
        } else {
            helpStage.getScene().getStylesheets().add(lightMode.toURI().toString());
        }
    }

    public void openAccountInformation() throws IOException {
        if (donateStage != null) donateStage.close();
        final Delta dragDelta = new Delta();
        Parent root = FXMLLoader.load(getClass().getResource("DonateView.fxml"));
        Scene scene = new Scene(root);
        donateStage = new Stage();
        donateStage.initStyle(StageStyle.UNDECORATED);
        scene.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = donateStage.getX() - mouseEvent.getScreenX();
            dragDelta.y = donateStage.getY() - mouseEvent.getScreenY();
        });
        scene.setOnMouseDragged(mouseEvent -> {
            donateStage.setX(mouseEvent.getScreenX() + dragDelta.x);
            donateStage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
        donateStage.getIcons().add(new Image(new File(System.getProperty("user.dir") + "/defi-portfolio/src/icons/donate.png").toURI().toString()));
        donateStage.setTitle(this.mainViewController.settingsController.translationList.getValue().get("Donate").toString());
        donateStage.setScene(scene);
        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
            double stageWidth = newValue.doubleValue();
            donateStage.setX(mainAnchorPane.getScene().getWindow().getX() + mainAnchorPane.getScene().getWindow().getWidth() / 2 - stageWidth / 2);
        };
        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
            double stageHeight = newValue.doubleValue();
            donateStage.setY(mainAnchorPane.getScene().getWindow().getY() + mainAnchorPane.getScene().getWindow().getHeight() / 2 - stageHeight / 2);
        };

        donateStage.widthProperty().addListener(widthListener);
        donateStage.heightProperty().addListener(heightListener);

        donateStage.setOnShown(e -> {
            donateStage.widthProperty().removeListener(widthListener);
            donateStage.heightProperty().removeListener(heightListener);
        });
        donateStage.show();

        java.io.File darkMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/darkMode.css");
        java.io.File lightMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/lightMode.css");
        if (this.mainViewController.settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            donateStage.getScene().getStylesheets().add(darkMode.toURI().toString());
        } else {
            donateStage.getScene().getStylesheets().add(lightMode.toURI().toString());
        }
    }

    public void openSettingPressed() throws IOException {

        if (settingsStage != null) settingsStage.close();
        final Delta dragDelta = new Delta();
        Parent root = FXMLLoader.load(getClass().getResource("SettingsView.fxml"));
        Scene scene = new Scene(root);
        settingsStage = new Stage();
        settingsStage.initStyle(StageStyle.UNDECORATED);
        scene.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = settingsStage.getX() - mouseEvent.getScreenX();
            dragDelta.y = settingsStage.getY() - mouseEvent.getScreenY();
        });
        scene.setOnMouseDragged(mouseEvent -> {
            settingsStage.setX(mouseEvent.getScreenX() + dragDelta.x);
            settingsStage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
        settingsStage.getIcons().add(new Image(new File(System.getProperty("user.dir") + "/defi-portfolio/src/icons/settings.png").toURI().toString()));
        settingsStage.setTitle(this.mainViewController.settingsController.translationList.getValue().get("Settings").toString());
        settingsStage.setScene(scene);

        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
            double stageWidth = newValue.doubleValue();
            settingsStage.setX(mainAnchorPane.getScene().getWindow().getX() + mainAnchorPane.getScene().getWindow().getWidth() / 2 - stageWidth / 2);
        };
        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
            double stageHeight = newValue.doubleValue();
            settingsStage.setY(mainAnchorPane.getScene().getWindow().getY() + mainAnchorPane.getScene().getWindow().getHeight() / 2 - stageHeight / 2);
        };

        settingsStage.widthProperty().addListener(widthListener);
        settingsStage.heightProperty().addListener(heightListener);

        settingsStage.setOnShown(e -> {
            settingsStage.widthProperty().removeListener(widthListener);
            settingsStage.heightProperty().removeListener(heightListener);
        });

        settingsStage.show();

        java.io.File darkMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/darkMode.css");
        java.io.File lightMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/lightMode.css");
        if (this.mainViewController.settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            settingsStage.getScene().getStylesheets().add(darkMode.toURI().toString());
        } else {
            settingsStage.getScene().getStylesheets().add(lightMode.toURI().toString());
        }
    }

    public void connectDefid(ActionEvent actionEvent) {
        if (!mainViewController.transactionController.checkRpc()) {
            this.mainViewController.transactionController.startServer();
        }
    }


    static class Delta {
        double x, y;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.mainViewController.mainView = this;
        updateLanguage();

        coinImageRewards.setImage(new Image(new File(System.getProperty("user.dir") + "/defi-portfolio/src/icons/" + mainViewController.settingsController.selectedCoin.getValue().split("-")[0].toLowerCase() + "-icon.png").toURI().toString()));
        coinImageCommissions.setImage(new Image(new File(System.getProperty("user.dir") + "/defi-portfolio/src/icons/" + mainViewController.settingsController.selectedCoin.getValue().split("-")[0].toLowerCase() + "-icon.png").toURI().toString()));
        updateStylesheet();

        this.mainViewController.settingsController.selectedStyleMode.addListener(style -> updateStylesheet());
        final Delta dragDelta = new Delta();

        this.btnConnect.disableProperty().bind(this.mainViewController.bDataBase.not());
        this.tokenLabel.textProperty().bindBidirectional(this.mainViewController.settingsController.tokenBalance);
        this.tokenLabelLM.textProperty().bindBidirectional(this.mainViewController.settingsController.tokenBalanceLM);

        this.fieldTotal.textProperty().bindBidirectional(this.mainViewController.settingsController.tokenAmount);
        this.fieldTotalYield.textProperty().bindBidirectional(this.mainViewController.settingsController.tokenYield);
        this.fieldTotalYieldRewards.textProperty().bindBidirectional(this.mainViewController.settingsController.tokenYieldRewards);
        this.fieldTotalYieldCommissions.textProperty().bindBidirectional(this.mainViewController.settingsController.tokenYieldCommissions);
        this.strCurrentBlockLocally.textProperty().bindBidirectional(this.mainViewController.strCurrentBlockLocally);
        this.strCurrentBlockOnBlockchain.textProperty().bindBidirectional(this.mainViewController.strCurrentBlockOnBlockchain);
        this.strLastUpdate.textProperty().bindBidirectional(this.mainViewController.settingsController.lastUpdate);
        this.btnUpdateDatabase.setOnAction(e -> {
            this.mainViewController.settingsController.selectedLaunchSync = true;
            this.mainViewController.transactionController.startServer();
            this.mainViewController.settingsController.runCheckTimer = true;
            Timer checkTimer = new Timer("");
            if (SettingsController.getInstance().getPlatform().equals("mac")) {
                try {
                    FileWriter myWriter = new FileWriter(System.getProperty("user.dir") + "/PortfolioData/" + "update.portfolio");
                    myWriter.write(this.mainViewController.settingsController.translationList.getValue().get("ConnectNode").toString());
                    myWriter.close();
                    try {
                        Process ps = null;
                        ps = Runtime.getRuntime().exec("./jre/bin/java -Xdock:icon=icons.icns -jar UpdateData.jar " + this.mainViewController.settingsController.selectedStyleMode.getValue().replace(" ", ""));
                    } catch (IOException r) {
                        SettingsController.getInstance().logger.warning("Exception occured: " + r.toString());
                    }
                } catch (IOException h) {
                    SettingsController.getInstance().logger.warning("Could not write to update.portfolio.");
                }
            } else {
                this.mainViewController.transactionController.updateJFrame();
                this.mainViewController.transactionController.jl.setText(this.mainViewController.settingsController.translationList.getValue().get("ConnectNode").toString());
            }
            checkTimer.scheduleAtFixedRate(new CheckConnection(this.mainViewController), 0, 30000);
        });

        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) ->
                {
                    if (!this.init)
                        mainViewController.plotUpdate(tabPane.getSelectionModel().getSelectedItem().getId());
                    cmbCoins.setVisible(true);
                    cmbFiat.setVisible(true);
                    cmbPlotCurrency.setVisible(true);
                    cmbCoinsCom.setVisible(true);
                    cmbFiatCom.setVisible(true);
                    cmbPlotCurrencyCom.setVisible(true);

                    this.updateHeader();
                }
        );

        this.cmbIntervall.valueProperty().bindBidirectional(this.mainViewController.settingsController.selectedIntervall);
        this.cmbIntervall.valueProperty().addListener((ov, oldValue, newValue) ->
        {
            if (newValue != null) {
                this.mainViewController.settingsController.selectedIntervallInt = "Daily";
                if(this.mainViewController.settingsController.translationList.getValue().get("Daily").equals(newValue)){
                    this.mainViewController.settingsController.selectedIntervallInt = "Daily";
                }

                if(this.mainViewController.settingsController.translationList.getValue().get("Weekly").equals(newValue)){
                    this.mainViewController.settingsController.selectedIntervallInt = "Weekly";
                }

                if(this.mainViewController.settingsController.translationList.getValue().get("Monthly").equals(newValue)){
                    this.mainViewController.settingsController.selectedIntervallInt = "Monthly";
                }

                if(this.mainViewController.settingsController.translationList.getValue().get("Yearly").equals(newValue)){
                    this.mainViewController.settingsController.selectedIntervallInt = "Yearly";
                }
            }
            if (!this.init)
                mainViewController.plotUpdate(tabPane.getSelectionModel().getSelectedItem().getId());
            this.mainViewController.settingsController.saveSettings();
        });

        this.cmbIntervallCom.valueProperty().bindBidirectional(this.mainViewController.settingsController.selectedIntervall);
        this.cmbIntervallOver.valueProperty().bindBidirectional(this.mainViewController.settingsController.selectedIntervall);

        this.cmbCoins.getItems().addAll(this.mainViewController.settingsController.cryptoCurrencies);
        this.cmbCoins.valueProperty().bindBidirectional(this.mainViewController.settingsController.selectedCoin);
        this.cmbCoins.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (!this.init)
                mainViewController.plotUpdate(tabPane.getSelectionModel().getSelectedItem().getId());

            this.updateHeader();
            this.mainViewController.settingsController.saveSettings();
            coinImageRewards.setImage(new Image(new File(System.getProperty("user.dir") + "/defi-portfolio/src/icons/" + mainViewController.settingsController.selectedCoin.getValue().split("-")[0].toLowerCase() + "-icon.png").toURI().toString()));
            coinImageCommissions.setImage(new Image(new File(System.getProperty("user.dir") + "/defi-portfolio/src/icons/" + mainViewController.settingsController.selectedCoin.getValue().split("-")[0].toLowerCase() + "-icon.png").toURI().toString()));
        });

        this.cmbCoinsCom.getItems().addAll(this.mainViewController.settingsController.cryptoCurrencies);
        this.cmbCoinsCom.valueProperty().bindBidirectional(this.mainViewController.settingsController.selectedCoin);

//        this.fiatColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Total") + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
//        this.crypto1Column.setText(this.mainViewController.settingsController.translationList.getValue().get("Rewards") + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
//        this.crypto2Column.setText(this.mainViewController.settingsController.translationList.getValue().get("Commissions") + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
//        this.crypto1FiatColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Rewards") + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");
//        this.crypto2FiatColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Commissions") + " (" + mainViewController.settingsController.selectedFiatCurrency.getValue() + ")");


        this.mainViewController.settingsController.selectedFiatCurrency.addListener((ov, oldValue, newValue) ->

        {
            if (!oldValue.equals(newValue) & this.plotRewards != null) {
                if (!this.init) mainViewController.plotUpdate(tabPane.getSelectionModel().getSelectedItem().getId());
                this.mainViewController.settingsController.saveSettings();
                this.fiatColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Total") + " (" + newValue + ")");
                this.updateHeader();

            }

        });


        this.cmbFiatCom.getItems().addAll(this.mainViewController.settingsController.plotCurrency);
        this.cmbFiatCom.valueProperty().bindBidirectional(this.mainViewController.settingsController.selectedPlotCurrency);

        this.cmbFiat.getItems().addAll(this.mainViewController.settingsController.plotCurrency);
        this.cmbFiat.valueProperty().bindBidirectional(this.mainViewController.settingsController.selectedPlotCurrency);
        this.cmbFiat.valueProperty().addListener((ov, oldValue, newValue) ->
        {
            if (!this.init)
                mainViewController.plotUpdate(tabPane.getSelectionModel().getSelectedItem().getId());
            this.mainViewController.settingsController.saveSettings();

            this.updateHeader();

        });

        this.mainViewController.settingsController.selectedDecimal.addListener((ov, oldValue, newValue) ->
        {
            if (!oldValue.equals(newValue) & this.plotRewards != null) {
                mainViewController.plotUpdate(tabPane.getSelectionModel().getSelectedItem().getId());
            }
        });

        this.cmbPlotCurrency.valueProperty().bindBidirectional(this.mainViewController.settingsController.selectedPlotType);
        this.cmbPlotCurrency.valueProperty().addListener((ov, oldValue, newValue) ->
        {
            if (!this.init)
                mainViewController.plotUpdate(tabPane.getSelectionModel().getSelectedItem().getId());
        });

        this.cmbPlotCurrencyCom.valueProperty().bindBidirectional(this.mainViewController.settingsController.selectedPlotType);

        this.dateFrom.valueProperty().bindBidirectional(this.mainViewController.settingsController.dateFrom);
        this.dateFrom.valueProperty().addListener((ov, oldValue, newValue) ->
        {
            if (!this.init)
                mainViewController.plotUpdate(tabPane.getSelectionModel().getSelectedItem().getId());
        });

        this.dateFromCom.valueProperty().bindBidirectional(this.mainViewController.settingsController.dateFrom);
        this.dateFromOver.valueProperty().bindBidirectional(this.mainViewController.settingsController.dateFrom);

        this.dateFrom.setDayCellFactory(picker -> new
                DateCell() {
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        LocalDate today = LocalDate.now();
                        setDisable(empty || date.compareTo(today) > 0);
                    }
                });

        this.dateFromCom.setDayCellFactory(picker -> new
                DateCell() {
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        LocalDate today = LocalDate.now();
                        setDisable(empty || date.compareTo(today) > 0);
                    }
                });

        this.dateFromOver.setDayCellFactory(picker -> new
                DateCell() {
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        LocalDate today = LocalDate.now();
                        setDisable(empty || date.compareTo(today) > 0);
                    }
                });

        this.mainViewController.settingsController.selectedLanguage.addListener((ov, oldValue, newValue) -> this.

                updateLanguage());

        this.dateTo.valueProperty().

                bindBidirectional(this.mainViewController.settingsController.dateTo);
        this.dateTo.valueProperty().

                addListener((ov, oldValue, newValue) ->

                {
                    if (!this.init)
                        mainViewController.plotUpdate(tabPane.getSelectionModel().getSelectedItem().getId());
                });
        this.dateTo.setValue(LocalDate.now());
        this.dateTo.setDayCellFactory(picker -> new

                DateCell() {

                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        LocalDate today = LocalDate.now();
                        setDisable(empty || date.compareTo(today) > 0);
                    }
                });

        this.dateToOver.valueProperty().

                bindBidirectional(this.mainViewController.settingsController.dateTo);
        this.dateToOver.setValue(LocalDate.now());
        this.dateToOver.setDayCellFactory(picker -> new

                DateCell() {

                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        LocalDate today = LocalDate.now();
                        setDisable(empty || date.compareTo(today) > 0);
                    }
                });

        this.dateToCom.valueProperty().

                bindBidirectional(this.mainViewController.settingsController.dateTo);
        this.dateToCom.setValue(LocalDate.now());
        this.dateToCom.setDayCellFactory(picker -> new
                DateCell() {

                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        LocalDate today = LocalDate.now();
                        setDisable(empty || date.compareTo(today) > 0);
                    }
                });

        initializeTableViewContextMenu();

        initPlotTableContextMenu();

        rawDataTable.itemsProperty().

                set(this.mainViewController.getTransactionTable());

        rawDataTable.getSelectionModel().

                setSelectionMode(
                        SelectionMode.MULTIPLE
                );

        plotTable.itemsProperty().

                set(this.mainViewController.getPlotData());
        plotTable.getSelectionModel().

                setSelectionMode(
                        SelectionMode.MULTIPLE
                );
        timeStampColumn.setCellValueFactory(param -> param.getValue().

                getBlockTime());
        crypto1Column.setCellValueFactory(param -> param.getValue().

                getCryptoValue1().

                asObject());
        crypto2Column.setCellValueFactory(param -> param.getValue().

                getCryptoValue2().

                asObject());
        crypto1FiatColumn.setCellValueFactory(param -> param.getValue().

                getCryptoFiatValue1().

                asObject());
        crypto2FiatColumn.setCellValueFactory(param -> param.getValue().

                getCryptoFiatValue2().

                asObject());

        Commission2OverviewColumn.setCellValueFactory(param -> param.getValue().

                getcryptoCommission2Overview().

                asObject());
        Commission2OverviewFiatColumn.setCellValueFactory(param -> param.getValue().

                getcryptoCommission2FiaOtverview().

                asObject());
        fiatColumn.setCellValueFactory(param -> param.getValue().

                getFiatValue().

                asObject());
        poolPairColumn.setCellValueFactory(param -> param.getValue().
                getPoolPair());
        balanceFiatColumn.setCellValueFactory(param -> param.getValue().
                getBalanceFiat());
        ownerColumn.setCellValueFactory(param -> param.getValue().

                ownerProperty);
        blockTimeColumn.setCellValueFactory(param -> param.getValue().

                blockTimeProperty.

                asObject());
        typeColumn.setCellValueFactory(param -> param.getValue().

                typeProperty);
        cryptoCurrencyColumn.setCellValueFactory(param -> param.getValue().

                cryptoCurrencyProperty);
        cryptoValueColumn.setCellValueFactory(param -> param.getValue().

                cryptoValueProperty.

                asObject());
        blockHashColumn.setCellValueFactory(param -> param.getValue().

                blockHashProperty);
        blockHeightColumn.setCellValueFactory(param -> param.getValue().

                blockHeightProperty.

                asObject());
        poolIDColumn.setCellValueFactory(param -> param.getValue().

                poolIDProperty);
        fiatValueColumn.setCellValueFactory(param -> param.getValue().

                fiatValueProperty.

                asObject());
        fiatCurrencyColumn.setCellValueFactory(param -> param.getValue().

                fiatCurrencyProperty);
        transactionColumn.setCellValueFactory(param -> param.getValue().

                txIDProperty);


        Callback<TableColumn<TransactionModel, String>, TableCell<TransactionModel, String>> cellFactory0
                = (final TableColumn<TransactionModel, String> entry) -> new TableCell<TransactionModel, String>() {

            final Hyperlink hyperlink = new Hyperlink();

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    TransactionModel tempParam = rawDataTable.getItems().get(getIndex());
                    hyperlink.setText(item);
                    hyperlink.setOnAction((event) -> {
                        try {
                            if (mainViewController.settingsController.getPlatform().equals("linux")) {
                                // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
                                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://mainnet.defichain.io/#/DFI/mainnet/block/" + tempParam.blockHashProperty.getValue()});
                                } else {
                                    System.out.println("xdg-open is not supported!");
                                }
                            } else {
                                Desktop.getDesktop().browse(new URL("https://mainnet.defichain.io/#/DFI/mainnet/block/" + tempParam.blockHashProperty.getValue()).toURI());
                            }
                        } catch (IOException | URISyntaxException e) {
                            SettingsController.getInstance().logger.warning("Exception occured: " + e.toString());
                        }
                    });
                    setGraphic(hyperlink);
                }
                setText(null);
            }
        };
        blockHashColumn.setCellFactory(cellFactory0);


        Callback<TableColumn<TransactionModel, String>, TableCell<TransactionModel, String>> cellFactory1
                = (final TableColumn<TransactionModel, String> entry) -> new TableCell<TransactionModel, String>() {

            final Hyperlink hyperlink = new Hyperlink();

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    TransactionModel tempParam = rawDataTable.getItems().get(getIndex());
                    hyperlink.setText(item);
                    hyperlink.setOnAction((event) -> {
                        try {
                            if (mainViewController.settingsController.getPlatform().equals("linux")) {
                                // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
                                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://mainnet.defichain.io/#/DFI/mainnet/address/" + tempParam.ownerProperty.getValue()});
                                } else {
                                    System.out.println("xdg-open is not supported!");
                                }
                            } else {
                                Desktop.getDesktop().browse(new URL("https://mainnet.defichain.io/#/DFI/mainnet/address/" + tempParam.ownerProperty.getValue()).toURI());
                            }
                        } catch (IOException | URISyntaxException e) {
                            SettingsController.getInstance().logger.warning("Exception occured: " + e.toString());
                        }
                    });
                    setGraphic(hyperlink);
                }
                setText(null);
            }
        };
        ownerColumn.setCellFactory(cellFactory1);

        Callback<TableColumn<TransactionModel, Integer>, TableCell<TransactionModel, Integer>> cellFactory2
                = (final TableColumn<TransactionModel, Integer> entry) -> new TableCell<TransactionModel, Integer>() {

            final Hyperlink hyperlink = new Hyperlink();

            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    TransactionModel tempParam = rawDataTable.getItems().get(getIndex());
                    hyperlink.setText(item.toString());
                    hyperlink.setOnAction((event) -> {
                        try {
                            if (mainViewController.settingsController.getPlatform().equals("linux")) {
                                // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
                                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://mainnet.defichain.io/#/DFI/mainnet/block/" + tempParam.blockHeightProperty.getValue()});
                                } else {
                                    System.out.println("xdg-open is not supported!");
                                }
                            } else {
                                Desktop.getDesktop().browse(new URL("https://mainnet.defichain.io/#/DFI/mainnet/block/" + tempParam.blockHeightProperty.getValue()).toURI());
                            }
                        } catch (IOException | URISyntaxException e) {
                            SettingsController.getInstance().logger.warning("Exception occured: " + e.toString());
                        }
                    });
                    setGraphic(hyperlink);
                }
                setText(null);
            }
        };
        blockHeightColumn.setCellFactory(cellFactory2);

        Callback<TableColumn<TransactionModel, String>, TableCell<TransactionModel, String>> cellFactory3
                = (final TableColumn<TransactionModel, String> entry) -> new TableCell<TransactionModel, String>() {

            final Hyperlink hyperlink = new Hyperlink();

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    TransactionModel tempParam = rawDataTable.getItems().get(getIndex());

                    if (tempParam.txIDProperty.getValue().equals("\"\"")) {
                        setText("-");
                        setGraphic(null);
                    } else {

                        hyperlink.setText(item);
                        hyperlink.setOnAction((event) -> {
                            try {
                                if (mainViewController.settingsController.getPlatform().equals("linux")) {
                                    // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
                                    if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                                        Runtime.getRuntime().exec(new String[]{"xdg-open", "https://mainnet.defichain.io/#/DFI/mainnet/tx/" + tempParam.txIDProperty.getValue()});
                                    } else {
                                        System.out.println("xdg-open is not supported!");
                                    }
                                } else {
                                    Desktop.getDesktop().browse(new URL("https://mainnet.defichain.io/#/DFI/mainnet/tx/" + tempParam.txIDProperty.getValue()).toURI());
                                }
                            } catch (IOException | URISyntaxException e) {
                                SettingsController.getInstance().logger.warning("Exception occured: " + e.toString());
                            }
                        });

                        setText(null);
                        setGraphic(hyperlink);
                    }
                }
            }
        };
        transactionColumn.setCellFactory(cellFactory3);


        poolIDColumn.setCellFactory(tc -> new TableCell<TransactionModel, String>() {
            @Override
            protected void updateItem(String poolID, boolean empty) {
                super.updateItem(poolID, empty);
                if (empty) {
                    setText(null);
                } else {

                    String pool = TransactionController.getInstance().getPoolPairFromId(poolID);
                    setText(pool);
                }
            }
        });

        fiatCurrencyColumn.setCellFactory(tc -> new TableCell<TransactionModel, String>() {
            @Override
            protected void updateItem(String fiatCurrency, boolean empty) {
                super.updateItem(fiatCurrency, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(mainViewController.settingsController.selectedFiatCurrency.getValue());
                }
            }
        });

        fiatColumn.setCellFactory(tc -> new TableCell<PoolPairModel, Double>() {
            @Override
            protected void updateItem(Double fiatValue, boolean empty) {
                super.updateItem(fiatValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (mainViewController.settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", fiatValue));
                }
            }
        });

        fiatValueColumn.setCellFactory(tc -> new TableCell<TransactionModel, Double>() {
            @Override
            protected void updateItem(Double fiatValue, boolean empty) {
                super.updateItem(fiatValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (mainViewController.settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", fiatValue));
                }
            }
        });

        crypto1Column.setCellFactory(tc -> new TableCell<PoolPairModel, Double>() {
            @Override
            protected void updateItem(Double cryptoValue, boolean empty) {
                super.updateItem(cryptoValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (mainViewController.settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", cryptoValue));
                }
            }
        });

        crypto1FiatColumn.setCellFactory(tc -> new TableCell<PoolPairModel, Double>() {
            @Override
            protected void updateItem(Double cryptoValue, boolean empty) {
                super.updateItem(cryptoValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (mainViewController.settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", cryptoValue));
                }
            }
        });

//        Commission1OverviewColumn.setCellFactory(tc -> new TableCell<PoolPairModel, Double>() {
//            @Override
//            protected void updateItem(Double cryptoValue, boolean empty) {
//                super.updateItem(cryptoValue, empty);
//                if (empty) {
//                    setText(null);
//                } else {
//
//                    Locale localeDecimal = Locale.GERMAN;
//                    if (mainViewController.settingsController.selectedDecimal.getValue().equals(".")) {
//                        localeDecimal = Locale.US;
//                    }
//                    setText(String.format(localeDecimal, "%.8f", cryptoValue));
//                }
//            }
//        });
        Commission2OverviewColumn.setCellFactory(tc -> new TableCell<PoolPairModel, Double>() {
            @Override
            protected void updateItem(Double cryptoValue, boolean empty) {
                super.updateItem(cryptoValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (mainViewController.settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", cryptoValue));
                }
            }
        });
//        Commission1OverviewFiatColumn.setCellFactory(tc -> new TableCell<PoolPairModel, Double>() {
//            @Override
//            protected void updateItem(Double cryptoValue, boolean empty) {
//                super.updateItem(cryptoValue, empty);
//                if (empty) {
//                    setText(null);
//                } else {
//
//                    Locale localeDecimal = Locale.GERMAN;
//                    if (mainViewController.settingsController.selectedDecimal.getValue().equals(".")) {
//                        localeDecimal = Locale.US;
//                    }
//                    setText(String.format(localeDecimal, "%.8f", cryptoValue));
//                }
//            }
//        });
        Commission2OverviewFiatColumn.setCellFactory(tc -> new TableCell<PoolPairModel, Double>() {
            @Override
            protected void updateItem(Double cryptoValue, boolean empty) {
                super.updateItem(cryptoValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (mainViewController.settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", cryptoValue));
                }
            }
        });

        crypto2Column.setCellFactory(tc -> new TableCell<PoolPairModel, Double>() {
            @Override
            protected void updateItem(Double cryptoValue, boolean empty) {
                super.updateItem(cryptoValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (mainViewController.settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", cryptoValue));
                }
            }
        });

        crypto2FiatColumn.setCellFactory(tc -> new TableCell<PoolPairModel, Double>() {
            @Override
            protected void updateItem(Double cryptoValue, boolean empty) {
                super.updateItem(cryptoValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (mainViewController.settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", cryptoValue));
                }
            }
        });

        cryptoValueColumn.setCellFactory(tc -> new TableCell<TransactionModel, Double>() {
            @Override
            protected void updateItem(Double cryptoValue, boolean empty) {
                super.updateItem(cryptoValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (mainViewController.settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", cryptoValue));
                }
            }
        });

        blockTimeColumn.setCellFactory(tc -> new TableCell<TransactionModel, Long>() {
            @Override
            protected void updateItem(Long blockTime, boolean empty) {
                super.updateItem(blockTime, empty);
                if (empty) {
                    setText(null);
                } else {
                    Date date = new Date(blockTime * 1000L);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    setText(dateFormat.format(date));
                }
            }
        });
        this.init = false;
        this.btnAnalyse.fire();
    }

    private void initializeTableViewContextMenu() {


        ContextMenu contextMenuRawData = new ContextMenu();

        //Init context menu of raw data table
        menuItemCopySelected.setOnAction(event -> mainViewController.copySelectedRawDataToClipboard(rawDataTable.selectionModelProperty().get().getSelectedItems(), false));
        menuItemCopyHeaderSelected.setOnAction(event -> mainViewController.copySelectedRawDataToClipboard(rawDataTable.selectionModelProperty().get().getSelectedItems(), true));
        menuItemExportSelected.setOnAction(event -> mainViewController.exportTransactionToExcel(rawDataTable.selectionModelProperty().get().getSelectedItems(), false));
        menuItemExportAllSelected.setOnAction(event -> mainViewController.exportTransactionToExcel(rawDataTable.getItems(), false));
        menuItemExportAllDailySelected.setOnAction(event -> mainViewController.exportTransactionToExcel(rawDataTable.getItems(), true));

        contextMenuRawData.getItems().add(menuItemCopySelected);
        contextMenuRawData.getItems().add(menuItemCopyHeaderSelected);
        contextMenuRawData.getItems().add(menuItemExportSelected);
        contextMenuRawData.getItems().add(menuItemExportAllSelected);
        contextMenuRawData.getItems().add(menuItemExportAllDailySelected);


        this.rawDataTable.contextMenuProperty().set(contextMenuRawData);
    }

    private void initPlotTableContextMenu() {

        //Init context menu of plot table

        ContextMenu contextMenuPlotData = new ContextMenu();
        menuItemCopySelectedPlot.setOnAction(event -> mainViewController.copySelectedDataToClipboard(plotTable.selectionModelProperty().get().getSelectedItems(), false));
        menuItemCopyHeaderSelectedPlot.setOnAction(event -> mainViewController.copySelectedDataToClipboard(plotTable.selectionModelProperty().get().getSelectedItems(), true));
        menuItemExportSelectedPlot.setOnAction(event -> mainViewController.exportPoolPairToExcel(plotTable.selectionModelProperty().get().getSelectedItems(), this.tabPane.getSelectionModel().getSelectedItem().getId()));
        menuItemExportAllSelectedPlot.setOnAction(event -> mainViewController.exportPoolPairToExcel(plotTable.getItems(), this.tabPane.getSelectionModel().getSelectedItem().getId()));

        contextMenuPlotData.getItems().add(menuItemCopySelectedPlot);
        contextMenuPlotData.getItems().add(menuItemCopyHeaderSelectedPlot);
        contextMenuPlotData.getItems().add(menuItemExportSelectedPlot);
        contextMenuPlotData.getItems().add(menuItemExportAllSelectedPlot);

        this.plotTable.contextMenuProperty().set(contextMenuPlotData);
    }

    private void updateStylesheet() {

        java.io.File darkMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/darkMode.css");
        java.io.File lightMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/lightMode.css");
        this.mainAnchorPane.getStylesheets().clear();
        if (this.helpStage != null) this.helpStage.getScene().getStylesheets().clear();
        if (this.settingsStage != null) this.settingsStage.getScene().getStylesheets().clear();
        if (this.donateStage != null) this.donateStage.getScene().getStylesheets().clear();

        if (this.mainViewController.settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            this.mainAnchorPane.getStylesheets().add(darkMode.toURI().toString());
            if (this.helpStage != null)
                this.helpStage.getScene().getStylesheets().add(darkMode.toURI().toString());
            if (this.settingsStage != null)
                this.settingsStage.getScene().getStylesheets().add(darkMode.toURI().toString());
            if (this.donateStage != null)
                this.donateStage.getScene().getStylesheets().add(darkMode.toURI().toString());
        } else {
            this.mainAnchorPane.getStylesheets().add(lightMode.toURI().toString());
            if (this.helpStage != null)
                this.helpStage.getScene().getStylesheets().add(lightMode.toURI().toString());
            if (this.settingsStage != null)
                this.settingsStage.getScene().getStylesheets().add(lightMode.toURI().toString());
            if (this.donateStage != null)
                this.donateStage.getScene().getStylesheets().add(lightMode.toURI().toString());
        }
    }

    private void updateLanguage() {
        this.mainViewController.settingsController.updateLanguage();
        this.btnRawData.textProperty().setValue(this.mainViewController.settingsController.translationList.getValue().get("RawData").toString());
        this.menuItemCopySelected.setText(this.mainViewController.settingsController.translationList.getValue().get("Copy").toString());
        this.menuItemCopyHeaderSelected.setText(this.mainViewController.settingsController.translationList.getValue().get("CopyHeader").toString());
        this.menuItemExportSelected.setText(this.mainViewController.settingsController.translationList.getValue().get("ExportSelected").toString());
        this.menuItemExportAllSelected.setText(this.mainViewController.settingsController.translationList.getValue().get("ExportAll").toString());
        this.menuItemExportAllDailySelected.setText(this.mainViewController.settingsController.translationList.getValue().get("ExportAllDaily").toString());
        this.menuItemCopySelectedPlot.setText(this.mainViewController.settingsController.translationList.getValue().get("Copy").toString());
        this.menuItemCopyHeaderSelectedPlot.setText(this.mainViewController.settingsController.translationList.getValue().get("CopyHeader").toString());
        this.menuItemExportSelectedPlot.setText(this.mainViewController.settingsController.translationList.getValue().get("ExportSelected").toString());
        this.menuItemExportAllSelectedPlot.setText(this.mainViewController.settingsController.translationList.getValue().get("ExportAll").toString());
        this.CurrentBlock.setText(this.mainViewController.settingsController.translationList.getValue().get("CurrentBlock").toString());
        this.CurrentBlockChain.setText(this.mainViewController.settingsController.translationList.getValue().get("CurrentBlockBC").toString());
        this.LastUpdate.setText(this.mainViewController.settingsController.translationList.getValue().get("LastUpdate").toString());
        this.btnSettings.setText(this.mainViewController.settingsController.translationList.getValue().get("Settings").toString());
        this.btnDonate.setText(this.mainViewController.settingsController.translationList.getValue().get("Donate").toString());
        this.Rewards.setText(this.mainViewController.settingsController.translationList.getValue().get("Rewards").toString());
        this.Commissions.setText(this.mainViewController.settingsController.translationList.getValue().get("Commissions").toString());
        this.Overview.setText(this.mainViewController.settingsController.translationList.getValue().get("Overview").toString());
        this.StartDate.setText(this.mainViewController.settingsController.translationList.getValue().get("StartDate").toString());
        this.EndDate.setText(this.mainViewController.settingsController.translationList.getValue().get("EndDate").toString());
        this.StartDateCom.setText(this.mainViewController.settingsController.translationList.getValue().get("StartDate").toString());
        this.EndDateCom.setText(this.mainViewController.settingsController.translationList.getValue().get("EndDate").toString());
        this.StartDateOver.setText(this.mainViewController.settingsController.translationList.getValue().get("StartDate").toString());
        this.EndDateOver.setText(this.mainViewController.settingsController.translationList.getValue().get("EndDate").toString());
        this.btnAnalyse.setText(this.mainViewController.settingsController.translationList.getValue().get("AnalyseData").toString());
        this.btnUpdateDatabase.setText(this.mainViewController.settingsController.translationList.getValue().get("UpdateData").toString());
        if (this.cmbIntervall.getItems().size() > 0) {

            this.cmbIntervallCom.getItems().set(0, this.mainViewController.settingsController.translationList.getValue().get("Daily").toString());
            this.cmbIntervallCom.getItems().set(1, this.mainViewController.settingsController.translationList.getValue().get("Weekly").toString());
            this.cmbIntervallCom.getItems().set(2, this.mainViewController.settingsController.translationList.getValue().get("Monthly").toString());
            this.cmbIntervallCom.getItems().set(3, this.mainViewController.settingsController.translationList.getValue().get("Yearly").toString());
            this.cmbIntervallOver.getItems().set(0, this.mainViewController.settingsController.translationList.getValue().get("Daily").toString());
            this.cmbIntervallOver.getItems().set(1, this.mainViewController.settingsController.translationList.getValue().get("Weekly").toString());
            this.cmbIntervallOver.getItems().set(2, this.mainViewController.settingsController.translationList.getValue().get("Monthly").toString());
            this.cmbIntervallOver.getItems().set(3, this.mainViewController.settingsController.translationList.getValue().get("Yearly").toString());
            this.cmbIntervall.getItems().set(0, this.mainViewController.settingsController.translationList.getValue().get("Daily").toString());
            this.cmbIntervall.getItems().set(1, this.mainViewController.settingsController.translationList.getValue().get("Weekly").toString());
            this.cmbIntervall.getItems().set(2, this.mainViewController.settingsController.translationList.getValue().get("Monthly").toString());
            this.cmbIntervall.getItems().set(3, this.mainViewController.settingsController.translationList.getValue().get("Yearly").toString());
            this.cmbPlotCurrency.getItems().set(0, this.mainViewController.settingsController.translationList.getValue().get("Individual").toString());
            this.cmbPlotCurrency.getItems().set(1, this.mainViewController.settingsController.translationList.getValue().get("Cumulated").toString());
            this.cmbPlotCurrencyCom.getItems().add(0, this.mainViewController.settingsController.translationList.getValue().get("Individual").toString());
            this.cmbPlotCurrencyCom.getItems().add(1, this.mainViewController.settingsController.translationList.getValue().get("Cumulated").toString());

        } else {

            this.cmbIntervallCom.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Daily").toString());
            this.cmbIntervallCom.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Weekly").toString());
            this.cmbIntervallCom.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Monthly").toString());
            this.cmbIntervallCom.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Yearly").toString());
            this.cmbIntervallOver.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Daily").toString());
            this.cmbIntervallOver.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Weekly").toString());
            this.cmbIntervallOver.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Monthly").toString());
            this.cmbIntervallOver.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Yearly").toString());
            this.cmbIntervall.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Daily").toString());
            this.cmbIntervall.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Weekly").toString());
            this.cmbIntervall.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Monthly").toString());
            this.cmbIntervall.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Yearly").toString());
            this.cmbPlotCurrency.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Individual").toString());
            this.cmbPlotCurrency.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Cumulated").toString());
            this.cmbPlotCurrencyCom.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Individual").toString());
            this.cmbPlotCurrencyCom.getItems().add(this.mainViewController.settingsController.translationList.getValue().get("Cumulated").toString());
        }
        this.btnConnect.setText(this.mainViewController.settingsController.translationList.getValue().get("ConnectNode").toString());
//        this.connectionLabel.getTooltip().setText(this.mainViewController.settingsController.translationList.getValue().get("UpdateTooltip").toString());
        this.blockTimeColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Date").toString());
        this.timeStampColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Date").toString());
        this.typeColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Operation").toString());
        this.cryptoValueColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("CryptoValue").toString());
        this.cryptoCurrencyColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("CryptoCurrency").toString());
        this.fiatValueColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("FIATValue").toString());
        this.fiatCurrencyColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("FIATCurrency").toString());
        this.poolIDColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("PoolPair").toString());
        this.poolPairColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("PoolPair").toString());
        this.blockHeightColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("BlockHeight").toString());
        this.blockHashColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("BlockHash").toString());
        this.ownerColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Owner").toString());
        this.transactionColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("TransactionHash").toString());
        this.fiatColumn.setText(this.mainViewController.settingsController.translationList.getValue().get("Total").toString());
        this.mainViewController.donateController.strDonateText.setValue(this.mainViewController.settingsController.translationList.getValue().get("DonateLabel").toString());
        this.mainViewController.settingsController.selectedPlotType.setValue(this.mainViewController.settingsController.translationList.getValue().get("Individual").toString());
        this.mainViewController.settingsController.selectedIntervall.setValue(this.mainViewController.settingsController.translationList.getValue().get("Daily").toString());


        if (!this.init)
            this.updateHeader();
    }

}
