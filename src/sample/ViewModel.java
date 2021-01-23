package sample;

import javafx.animation.PauseTransition;
import javafx.beans.property.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ViewModel {

    public StringProperty strCurrentBlockLocally = new SimpleStringProperty("Current Block locally: 0");
    public StringProperty strCurrentBlockOnBlockchain = new SimpleStringProperty("Current Block on Blockchain: 1000");
    public ObjectProperty<javafx.scene.image.Image> imgStatus = new SimpleObjectProperty<>();
    public StringProperty strUpToDate = new SimpleStringProperty("Database not up to date");
    public SimpleDoubleProperty progress = new SimpleDoubleProperty(.0);
    public StringProperty strProgressbar = new SimpleStringProperty("");
    public StringProperty selectedCoin = new SimpleStringProperty("BTC");
    public StringProperty selectedFiatCurrency = new SimpleStringProperty("EUR");
    public StringProperty selectedDecimal = new SimpleStringProperty(",");
    public StringProperty selectedSeperator = new SimpleStringProperty(",");
    public ObjectProperty<java.time.LocalDate> dateExpStart = new SimpleObjectProperty();
    public ObjectProperty<java.time.LocalDate> dateExpEnd = new SimpleObjectProperty();
    public ObjectProperty<java.time.LocalDate> dateAnalyseStart = new SimpleObjectProperty();
    public ObjectProperty<java.time.LocalDate> dateAnalyseEnd = new SimpleObjectProperty();
    public LineChart<Number, Number> hPlot;

    public String strPathAppData = System.getenv("APPDATA") + "\\defi-portfolio\\";
    public String strPathDefid = System.getenv("LOCALAPPDATA") + "\\Programs\\defi-app\\resources\\binary\\win\\defid.exe";
    public String strPathDefiCli = System.getProperty("user.dir") + "\\src\\sample\\defichain-1.3.17-x86_64-w64-mingw32\\defichain-1.3.17\\bin\\defi-cli.exe";
    public String strTransactionData = "transactionData.portfolio";
    public String strCoinPriceData = "coinPriceData.portfolio";

    public TransactionController transactionController = new TransactionController(this.strPathAppData + this.strTransactionData);
    public List<TransactionModel> transactionList;
    public CoinPriceController coinPriceController = new CoinPriceController(this.strPathAppData + strCoinPriceData);
    public CoinPriceModel coinPriceHistory;

    public ViewModel() {
        // generate folder %appData%//defiPortfolio if no one exists
        File directory = new File(strPathAppData);
        if (!directory.exists()) {
            directory.mkdir();
        }

        this.transactionList = this.transactionController.transactionList;
        this.coinPriceHistory = this.coinPriceController.coinPriceModel;

        this.strCurrentBlockLocally.set("Current Block locally: " + transactionController.getLocalBlockCount());
        this.strCurrentBlockOnBlockchain.set("Current Block on Blockchain: " + transactionController.getBlockCountRpc());

        // Init gui elements
        File file = new File(System.getProperty("user.dir") + "\\src\\icons\\warning.png");
        Image image = new Image(file.toURI().toString());
        this.imgStatus.setValue(image);
    }

    public boolean updateTransactionData() {

        if(!this.coinPriceController.updateCoinPriceData()){
            //Logg error @ coin price update
        }

        if (new File(strPathAppData + strTransactionData).exists()) {
            int depth = this.transactionController.getBlockCountRpc() - this.transactionController.getLocalBlockCount();
            return this.transactionController.updateTransactionData(depth);
        } else {
            return this.transactionController.updateTransactionData(this.transactionController.getAccountHistoryCountRpc());
        }

    }

    public void btnUpdateDatabasePressed() throws InterruptedException {
        this.progress.setValue(1);
        File file = new File(System.getProperty("user.dir") + "\\src\\icons\\accept.png");
        Image image = new Image(file.toURI().toString());
        this.imgStatus.setValue(image);

        this.strCurrentBlockLocally.set("Current Block locally: " + this.transactionController.getLocalBlockCount());
        this.strCurrentBlockOnBlockchain.set("Current Block on Blockchain: " + this.transactionController.getBlockCountRpc());

        if (updateTransactionData()) {

            this.strUpToDate.setValue("Database up to date");
            this.strProgressbar.setValue("Updating database finished");

            this.strCurrentBlockLocally.set("Current Block locally: " + this.transactionController.getLocalBlockCount());
            this.strCurrentBlockOnBlockchain.set("Current Block on Blockchain: " + this.transactionController.getBlockCountRpc());

            this.transactionList = this.transactionController.transactionList;

        } else {

        }
        PauseTransition pause = new PauseTransition(Duration.seconds(10));
        pause.setOnFinished(e -> this.strProgressbar.setValue(null));
        pause.play();
    }

    public void plotPressed() {

        LocalDate startDate = this.dateAnalyseStart.getValue();
        LocalDate endDate = this.dateAnalyseStart.getValue();
        Timestamp aasdasd = Timestamp.valueOf("2021-01-02 00:00:00");

        XYChart.Series<Number, Number> series = new XYChart.Series();
        series.setName("My portfolio");

        series.getData().add(new XYChart.Data("2", 14));
        series.getData().add(new XYChart.Data("Mar", 15));
        series.getData().add(new XYChart.Data("3", 24));
        series.getData().add(new XYChart.Data("May", 34));
        series.getData().add(new XYChart.Data("4", 36));
        series.getData().add(new XYChart.Data("Jul", 22));
        series.getData().add(new XYChart.Data("Aug", 45));
        series.getData().add(new XYChart.Data("Sep", 43));
        series.getData().add(new XYChart.Data("Oct", 17));
        series.getData().add(new XYChart.Data("Nov", 29));
        series.getData().add(new XYChart.Data("Dec", 25));
        series.getData().add(new XYChart.Data("01", 30));
        series.getData().add(new XYChart.Data("10", 23));
        series.getData().add(new XYChart.Data("20", 14));
        series.getData().add(new XYChart.Data("0Mar", 15));
        series.getData().add(new XYChart.Data("03", 24));
        series.getData().add(new XYChart.Data("M0ay", 34));
        series.getData().add(new XYChart.Data("40", 36));
        series.getData().add(new XYChart.Data("J0ul", 22));
        series.getData().add(new XYChart.Data("Au0g", 45));
        series.getData().add(new XYChart.Data("Sep", 43));
        series.getData().add(new XYChart.Data("ct", 17));
        series.getData().add(new XYChart.Data("Nov", 29));
        series.getData().add(new XYChart.Data("Dec", 25));

        if (this.hPlot.getData().size() == 1) {
            this.hPlot.getData().remove(0);
        }

        this.hPlot.getData().add(series);
        for (XYChart.Series<Number, Number> s : this.hPlot.getData()) {
            for (XYChart.Data d : s.getData()) {
                Tooltip t = new Tooltip(d.getYValue().toString());
                t.setShowDelay(Duration.seconds(0));
                Tooltip.install(d.getNode(), t);
                d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
            }
        }
//        List<XYChart.Series> v = new ArrayList<XYChart.Series>();
//        Collections.addAll( v, series);
        //  new ZoomManager(this.stackPane, this.hPlot, v);
    }

    public void exportToExcel() {

        Locale localeDecimal = Locale.GERMAN;
        if(selectedDecimal.equals(".")){
            localeDecimal = Locale.US;
        }

        //TODO brows dialog export --> strPathAppData

        boolean success = ExportService.exportTransactionToExcel(this.transactionList, strPathAppData+"//test.csv",this.coinPriceHistory,this.selectedFiatCurrency.getValue(),localeDecimal,this.selectedSeperator.getValue());

        if (success) {
            this.strProgressbar.setValue("Excel successfully exported!");
            //   this.strProgressbar.setTextFill(Color.LIGHTGREEN);
            PauseTransition pause = new PauseTransition(Duration.seconds(10));
            pause.setOnFinished(e -> this.strProgressbar.setValue(null));
            pause.play();
        } else {
            this.strProgressbar.setValue("Error while exporting excel!");
            //  this.strProgressbar.setTextFill(Color.RED);
            PauseTransition pause = new PauseTransition(Duration.seconds(10));
            pause.setOnFinished(e -> this.strProgressbar.setValue(null));
            pause.play();
        }

    }

}
