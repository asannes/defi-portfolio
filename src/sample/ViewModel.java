package sample;

import javafx.animation.PauseTransition;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;

public class ViewModel {

    public StringProperty strCurrentBlockLocally = new SimpleStringProperty("Current Block locally: 0");
    public StringProperty strCurrentBlockOnBlockchain = new SimpleStringProperty("Current Block on Blockchain: No connection");
    public ObjectProperty<javafx.scene.image.Image> imgStatus = new SimpleObjectProperty<>();
    public StringProperty strUpToDate = new SimpleStringProperty("Database not up to date");
    public SimpleDoubleProperty progress = new SimpleDoubleProperty(.0);
    public StringProperty strProgressbar = new SimpleStringProperty("");
    public LineChart<Number, Number> plotRewards, plotCommissions2, plotCommissions;
    public List<TransactionModel> transactionModelList = new ArrayList<>();
    public List<PoolPairModel> poolPairModelList = new ArrayList<>();
    public ObservableList<TransactionModel> transactionList;
    public ObservableList<PoolPairModel> poolPairList;

    public String[] cryptoCurrencies = new String[]{"BTC-DFI",  "ETH-DFI", "USDT-DFI", "LTC-DFI", "BCH-DFI", "DOGE-DFI"};
    public String[] plotCurrency = new String[]{"Coin","Fiat"};
    public String[] plotType = new String[]{"Individual", "Cumulated"};

    public String strCookiePath = System.getenv("APPDATA") + "\\DeFi Blockchain\\.cookie";
    public String strPathAppData = System.getenv("APPDATA") + "\\defi-portfolio\\";
    public String strPathDefid = System.getenv("LOCALAPPDATA") + "\\Programs\\defi-app\\resources\\binary\\win\\defid.exe";
    public String strPathDefiCli = System.getProperty("user.dir") + "\\src\\sample\\defichain-1.3.17-x86_64-w64-mingw32\\defichain-1.3.17\\bin\\defi-cli.exe";
    public String strTransactionData = "transactionData.portfolio";
    public String strCoinPriceData = "coinPriceData.portfolio";
    public String strSettingsData = "settings.portfolio";

    public CoinPriceController coinPriceController = new CoinPriceController(this.strPathAppData + strCoinPriceData);
    public SettingsController settingsController = SettingsController.getInstance();
    public TransactionController transactionController = new TransactionController(this.strPathAppData + this.strTransactionData, this.settingsController, this.coinPriceController, this.strPathDefiCli, this.strCookiePath);

    public ExportService expService;

    public ViewModel() {
        // generate folder %appData%//defiPortfolio if no one exists
        File directory = new File(strPathAppData);
        if (!directory.exists()) {
            directory.mkdir();
        }

        //if(checkIfDeFiAppIsRunning())   JOptionPane.showMessageDialog(null,"Please close the defi-app to connect to node","Update information", JOptionPane.INFORMATION_MESSAGE);

        /*Process p;
        StringBuilder processOutput = new StringBuilder();
        try {
            p = Runtime.getRuntime().exec(strPathDefid);

        } catch (IOException e) {
            e.printStackTrace();
        }*/

        this.transactionList = FXCollections.observableArrayList(this.transactionController.transactionList);
        this.transactionModelList = this.transactionController.transactionList;
        this.poolPairList = FXCollections.observableArrayList(this.poolPairModelList);
        this.expService = new ExportService(this.coinPriceController, this.transactionController, this.settingsController);

        this.strCurrentBlockOnBlockchain.set("Current Block on Blockchain: " + transactionController.getBlockCountRpc());
        this.strCurrentBlockLocally.set("Current Block locally: " + transactionController.getLocalBlockCount());

        // Init gui elements
        File file = new File(System.getProperty("user.dir") + "\\src\\icons\\warning.png");
        Image image = new Image(file.toURI().toString());
        this.imgStatus.setValue(image);
    }

    public void copySelectedRawDataToClipboard(List<TransactionModel> list, boolean withHeaders) {
        var sb = new StringBuilder();

        Locale localeDecimal = Locale.GERMAN;
        if (settingsController.selectedDecimal.getValue().equals(".")) {
            localeDecimal = Locale.US;
        }

        if (withHeaders) {
            sb.append("Date,Owner,Operation,Amount,Cryptocurrency,FIAT value,FIAT currency,Block Hash,Block Height,Pool ID".replace(",", this.settingsController.selectedSeperator.getValue())).append("\n");
        }

        for (TransactionModel transaction : list
        ) {
            sb.append(this.transactionController.convertTimeStampToString(transaction.getBlockTime().getValue())).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getOwner().getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getType().getValue()).append(this.settingsController.selectedSeperator.getValue());
            String[] CoinsAndAmounts = this.transactionController.splitCoinsAndAmounts(transaction.getAmount().getValue()[0]);
            sb.append(String.format(localeDecimal, "%.8f", Double.parseDouble(CoinsAndAmounts[0]))).append(this.settingsController.selectedSeperator.getValue());
            sb.append(CoinsAndAmounts[1]).append(this.settingsController.selectedSeperator.getValue());

            var price = this.coinPriceController.getPriceFromTimeStamp(CoinsAndAmounts[1] + this.settingsController.selectedFiatCurrency.getValue(), transaction.getBlockTimeValue() * 1000L);
            sb.append(String.format(localeDecimal, "%.8f", Double.parseDouble(CoinsAndAmounts[0]) * price)).append(this.settingsController.selectedSeperator.getValue());
            sb.append(this.settingsController.selectedFiatCurrency.getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getBlockHash().getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getBlockHeight().getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(transaction.getPoolID().getValue());
            sb.append("\n");
        }
        StringSelection stringSelection = new StringSelection(sb.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public void copySelectedDataToClipboard(List<PoolPairModel> list, boolean withHeaders) {
        var sb = new StringBuilder();

        Locale localeDecimal = Locale.GERMAN;
        if (settingsController.selectedDecimal.getValue().equals(".")) {
            localeDecimal = Locale.US;
        }

        if (withHeaders) {
            sb.append("Date,Total in Fiat,Rewards,Crypto 1,Crypto 2".replace(",", this.settingsController.selectedSeperator.getValue())).append("\n");
        }

        for (PoolPairModel poolPair : list
        ) {
            sb.append(poolPair.getBlockTime().getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(poolPair.getFiatValue().getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(poolPair.getType().getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(poolPair.getCryptoValue1().getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append(poolPair.getCryptoValue2().getValue()).append(this.settingsController.selectedSeperator.getValue());
            sb.append("\n");
        }
        StringSelection stringSelection = new StringSelection(sb.toString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public boolean updateTransactionData() {
        if (new File(strPathAppData + strTransactionData).exists()) {
            int depth = this.transactionController.getBlockCountRpc() - this.transactionController.getLocalBlockCount();
            return this.transactionController.updateTransactionData(depth);
        } else {
            return this.transactionController.updateTransactionData(this.transactionController.getAccountHistoryCountRpc());
        }
    }

    public boolean checkIfDeFiAppIsRunning() {
        String line;
        String pidInfo = "";
        Process p;

        try {
            p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");


            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((line = input.readLine()) != null) {
                pidInfo += line;
            }

            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (pidInfo.contains("defi-app")) {
            return true;
        } else {
            return false;
        }
    }

    public void btnUpdateDatabasePressed() throws InterruptedException {

        if (updateTransactionData()) {

            this.strUpToDate.setValue("Database up to date");
            this.strProgressbar.setValue("Updating database finished");

            this.strCurrentBlockLocally.set("Current Block locally: " + this.transactionController.getLocalBlockCount());
            this.strCurrentBlockOnBlockchain.set("Current Block on Blockchain: " + this.transactionController.getBlockCountRpc());

            transactionList.clear();
            transactionList.addAll(this.transactionController.transactionList);
            this.transactionModelList = this.transactionController.transactionList;

        } else {

        }

        File file = new File(System.getProperty("user.dir") + "\\src\\icons\\accept.png");
        Image image = new Image(file.toURI().toString());
        this.imgStatus.setValue(image);
        this.strUpToDate.setValue("Database up to date");
    }

    public void plotUpdate(String openedTab){
        switch (openedTab) {
            case "Overview":
                break;
            case "Rewards":
                updateRewards();
                break;
            case "Commissions":
                updateCommissions();
                break;
            default:
                break;
        }
    }

    public void updateRewards() {

        XYChart.Series<Number, Number> rewardsSeries = new XYChart.Series();

        long TimeStampStart = Timestamp.valueOf(this.settingsController.dateFrom.getValue() + " 00:00:00").getTime() / 1000L;
        long TimeStampEnd = Timestamp.valueOf(this.settingsController.dateTo.getValue() + " 23:59:59").getTime() / 1000L;

        List<TransactionModel> transactionsInTime = this.transactionController.getTransactionsInTime(this.transactionList, TimeStampStart, TimeStampEnd);
        TreeMap<String, Double> joinedRewards = this.transactionController.getCryptoMap(transactionsInTime, this.settingsController.cmbIntervall.getValue(),1, this.settingsController.selectedCoin.getValue(), "Rewards", this.settingsController.selectedPlotCurrency.getValue());

        this.poolPairModelList.clear();
        this.plotRewards.setLegendVisible(false);

        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
            this.plotRewards.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[1]);
        } else {
            this.plotRewards.getYAxis().setLabel(this.settingsController.selectedFiatCurrency.getValue());
        }

        if(this.settingsController.selectedPlotType.getValue().equals("Individual")){

        // Plot timeSeries
        for (HashMap.Entry<String, Double> entry : joinedRewards.entrySet()) {
             rewardsSeries.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
            this.poolPairModelList.add(new PoolPairModel(entry.getKey(), "Rewards", entry.getValue(), entry.getValue() , 1, "BTC-DFI"));
        }

        this.poolPairList.clear();
        this.poolPairList.addAll(this.poolPairModelList);

        this.plotRewards.getData().clear();

        if (this.plotRewards.getData().size() == 1) {
            this.plotRewards.getData().remove(0);
        }

        this.plotRewards.getData().add(rewardsSeries);;

        for (XYChart.Series<Number, Number> s : this.plotRewards.getData()) {
            for (XYChart.Data d : s.getData()) {
                Tooltip t = new Tooltip(d.getYValue().toString());
                t.setShowDelay(Duration.seconds(0));
                Tooltip.install(d.getNode(), t);
                d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
            }
        }


        }else{

        // Plot Kumuliert
        Collection<Double> values = joinedRewards.values();
        ArrayList<Double> valueList = new ArrayList<>(values);
        XYChart.Series<Number, Number> series2 = new XYChart.Series();

        for (int i = 0; i < valueList.size() - 1; i++) {
            valueList.set(i + 1, valueList.get(i) + valueList.get(i + 1));
        }

        int iterator = 0;
        for (HashMap.Entry<String, Double> entry : joinedRewards.entrySet()) {
            entry.setValue(valueList.get(iterator));
            iterator++;
        }
        series2.setName("Rewards kumuliert");

        for (HashMap.Entry<String, Double> entry : joinedRewards.entrySet()) {
            series2.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
        }
        if (this.plotRewards.getData().size() == 1) {
            this.plotRewards.getData().remove(0);
        }

        this.plotRewards.getData().add(series2);
        for (XYChart.Series<Number, Number> s : this.plotRewards.getData()) {
            for (XYChart.Data d : s.getData()) {
                Tooltip t = new Tooltip(d.getYValue().toString());
                t.setShowDelay(Duration.seconds(0));
                Tooltip.install(d.getNode(), t);
                d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
            }
        }

        }

    }

    public void updateCommissions() {

        XYChart.Series<Number, Number> commissionsSeries1 = new XYChart.Series();
        XYChart.Series<Number, Number> commissionsSeries2 = new XYChart.Series();

        long TimeStampStart = Timestamp.valueOf(this.settingsController.dateFrom.getValue() + " 00:00:00").getTime() / 1000L;
        long TimeStampEnd = Timestamp.valueOf(this.settingsController.dateTo.getValue() + " 23:59:59").getTime() / 1000L;

        List<TransactionModel> transactionsInTime = this.transactionController.getTransactionsInTime(this.transactionList, TimeStampStart, TimeStampEnd);

        TreeMap<String, Double> joinedCommissionsCoin1 = this.transactionController.getCryptoMap(transactionsInTime, this.settingsController.cmbIntervall.getValue(),1, this.settingsController.selectedCoin.getValue(), "Commission", "Coin");
        TreeMap<String, Double> joinedCommissionsCoin2 = this.transactionController.getCryptoMap(transactionsInTime, this.settingsController.cmbIntervall.getValue(),0, this.settingsController.selectedCoin.getValue(), "Commission", "Fiat");
        TreeMap<String, Double> joinedCommissionsFiat1 = this.transactionController.getCryptoMap(transactionsInTime, this.settingsController.cmbIntervall.getValue(),1, this.settingsController.selectedCoin.getValue(), "Commission", "Coin");
        TreeMap<String, Double> joinedCommissionsFiat2 = this.transactionController.getCryptoMap(transactionsInTime, this.settingsController.cmbIntervall.getValue(),0, this.settingsController.selectedCoin.getValue(), "Commission", "Fiat");

        this.poolPairModelList.clear();
        this.poolPairList.clear();
        this.plotCommissions.setLegendVisible(false);
        this.plotCommissions2.setLegendVisible(false);
        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
            this.plotCommissions.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[1]);
            this.plotCommissions2.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[0]);
        } else {
            this.plotCommissions.getYAxis().setLabel(this.settingsController.selectedFiatCurrency.getValue());
            this.plotCommissions2.getYAxis().setLabel(this.settingsController.selectedFiatCurrency.getValue());
        }
        if(this.settingsController.selectedPlotType.getValue().equals("Individual")){

            // Plot commsion 1
            for (HashMap.Entry<String, Double> entry : joinedCommissionsCoin1.entrySet()) {
                double coinPrice = this.coinPriceController.getPriceFromTimeStamp(this.settingsController.selectedCoin.getValue().split("-")[1] + this.settingsController.selectedFiatCurrency.getValue(), Timestamp.valueOf(entry.getKey()+ " 12:00:00").getTime()*1000L);
                if(this.settingsController.selectedPlotCurrency.getValue().equals("Coin")){
                    commissionsSeries1.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
                }else{
                    commissionsSeries1.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()* coinPrice));
                }
               // this.poolPairModelList.add(new PoolPairModel(entry.getKey(), "Rewards", entry.getValue()* coinPrice, entry.getValue() , 1, "BTC-DFI"));
            }

            // Plot commsion 1
            for (HashMap.Entry<String, Double> entry : joinedCommissionsCoin2.entrySet()) {
                double coinPrice = this.coinPriceController.getPriceFromTimeStamp(this.settingsController.selectedCoin.getValue().split("-")[1] + this.settingsController.selectedFiatCurrency.getValue(), Timestamp.valueOf(entry.getKey()+ " 12:00:00").getTime()*1000L);
                if(this.settingsController.selectedPlotCurrency.getValue().equals("Coin")){
                    commissionsSeries2.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
                }else{
                    commissionsSeries2.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()* coinPrice));
                }
                //this.poolPairModelList.add(new PoolPairModel(entry.getKey(), "Rewards", entry.getValue()* coinPrice, entry.getValue() , 1, "BTC-DFI"));
            }

            this.poolPairList.addAll(this.poolPairModelList);

            this.plotRewards.getData().clear();

            if (this.plotCommissions.getData().size() == 1) {
                this.plotCommissions.getData().remove(0);
            }

            if (this.plotCommissions2.getData().size() == 1) {
                this.plotCommissions2.getData().remove(0);
            }

            this.plotCommissions.getData().add(commissionsSeries1);
            this.plotCommissions2.getData().add(commissionsSeries2);

            for (XYChart.Series<Number, Number> s : this.plotCommissions.getData()) {
                for (XYChart.Data d : s.getData()) {
                    Tooltip t = new Tooltip(d.getYValue().toString());
                    t.setShowDelay(Duration.seconds(0));
                    Tooltip.install(d.getNode(), t);
                    d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                    d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                }
            }

            for (XYChart.Series<Number, Number> s : this.plotCommissions2.getData()) {
                for (XYChart.Data d : s.getData()) {
                    Tooltip t = new Tooltip(d.getYValue().toString());
                    t.setShowDelay(Duration.seconds(0));
                    Tooltip.install(d.getNode(), t);
                    d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                    d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                }
            }


        }else{

            // Plot Kumuliert
            Collection<Double> values = joinedCommissionsCoin1.values();
            ArrayList<Double> valueList = new ArrayList<>(values);
            XYChart.Series<Number, Number> series = new XYChart.Series();

            for (int i = 0; i < valueList.size() - 1; i++) {
                valueList.set(i + 1, valueList.get(i) + valueList.get(i + 1));
            }

            int iterator = 0;
            for (HashMap.Entry<String, Double> entry : joinedCommissionsCoin1.entrySet()) {
                entry.setValue(valueList.get(iterator));
                iterator++;
            }
            series.setName("Rewards kumuliert");

            for (HashMap.Entry<String, Double> entry : joinedCommissionsCoin1.entrySet()) {
                series.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
            }
            if (this.plotCommissions.getData().size() == 1) {
                this.plotCommissions.getData().remove(0);
            }

            this.plotCommissions.getData().add(series);
            for (XYChart.Series<Number, Number> s : this.plotCommissions.getData()) {
                for (XYChart.Data d : s.getData()) {
                    Tooltip t = new Tooltip(d.getYValue().toString());
                    t.setShowDelay(Duration.seconds(0));
                    Tooltip.install(d.getNode(), t);
                    d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                    d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                }
            }

            // Plot Kumuliert
             values = joinedCommissionsCoin2.values();
            valueList = new ArrayList<>(values);
            XYChart.Series<Number, Number> series2 = new XYChart.Series();

            for (int i = 0; i < valueList.size() - 1; i++) {
                valueList.set(i + 1, valueList.get(i) + valueList.get(i + 1));
            }

             iterator = 0;
            for (HashMap.Entry<String, Double> entry : joinedCommissionsCoin2.entrySet()) {
                entry.setValue(valueList.get(iterator));
                iterator++;
            }
            series2.setName("Rewards kumuliert");

            for (HashMap.Entry<String, Double> entry : joinedCommissionsCoin2.entrySet()) {
                series2.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
            }
            if (this.plotCommissions2.getData().size() == 1) {
                this.plotCommissions2.getData().remove(0);
            }

            this.plotCommissions2.getData().add(series2);
            for (XYChart.Series<Number, Number> s : this.plotCommissions2.getData()) {
                for (XYChart.Data d : s.getData()) {
                    Tooltip t = new Tooltip(d.getYValue().toString());
                    t.setShowDelay(Duration.seconds(0));
                    Tooltip.install(d.getNode(), t);
                    d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
                    d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
                }
            }

        }

    }

    public ObservableList<TransactionModel> getTransactionTable() {
        return this.transactionList;
    }

    public ObservableList<PoolPairModel> getPlotData() {
        return this.poolPairList;
    }

    public void exportTransactionToExcel(List<TransactionModel> list) {

        Locale localeDecimal = Locale.GERMAN;
        if (settingsController.selectedDecimal.getValue().equals(".")) {
            localeDecimal = Locale.US;
        }


        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV files", "*.csv)")
        );
        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile != null) {
            boolean success = this.expService.exportTransactionToExcel(list, selectedFile.getPath(), this.coinPriceController.coinPriceModel, this.settingsController.selectedFiatCurrency.getValue(), localeDecimal, this.settingsController.selectedSeperator.getValue());

            if (success) {
                this.strProgressbar.setValue("Excel successfully exported!");
                //  this.strProgressbar.setTextFill(Color.Green);
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

    public void exportPoolPairToExcel(List<PoolPairModel> list) {

        Locale localeDecimal = Locale.GERMAN;
        if (settingsController.selectedDecimal.getValue().equals(".")) {
            localeDecimal = Locale.US;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV files", "*.csv")
        );

        File selectedFile = fileChooser.showSaveDialog(new Stage());

        if (selectedFile != null) {
            boolean success = this.expService.exportPoolPairToExcel(list, selectedFile.getPath(), this.coinPriceController.coinPriceModel, this.settingsController.selectedFiatCurrency.getValue(), localeDecimal, this.settingsController.selectedSeperator.getValue());

            if (success) {
                this.strProgressbar.setValue("Excel successfully exported!");
                //  this.strProgressbar.setTextFill(Color.Green);
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

    public void openBlockChainExplorer(TransactionModel model){
        try {
            Desktop.getDesktop().browse(new URL("https://mainnet.defichain.io/#/DFI/mainnet/block/" + model.getBlockHashValue()).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
