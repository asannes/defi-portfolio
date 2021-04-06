package portfolio.services;

import javafx.scene.chart.PieChart;
import javafx.scene.control.TableColumn;
import portfolio.controllers.MainViewController;
import portfolio.controllers.SettingsController;
import portfolio.controllers.TransactionController;
import portfolio.models.PortfolioModel;
import portfolio.views.MainView;
import portfolio.models.PoolPairModel;
import portfolio.models.TransactionModel;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class ExportService {

    MainViewController mainViewController;

    public ExportService(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public boolean exportTransactionToExcel(List<TransactionModel> transactions, String exportPath, Locale localeDecimal, String exportSplitter) {
        File exportFile = new File(exportPath);
        this.mainViewController.settingsController.lastExportPath = exportFile.getParent();
        this.mainViewController.settingsController.saveSettings();
        if(exportFile.exists()) exportFile.delete();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(exportPath, true));
        } catch (IOException e) {
            SettingsController.getInstance().logger.warning("Exception occured: " + e.toString());
        }
        StringBuilder sb = new StringBuilder();

        for (TableColumn column : this.mainViewController.mainView.rawDataTable.getColumns()
        ) {
            sb.append(column.getText()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
        }

        sb.setLength(sb.length() - 1);
        sb.append("\n");
        writer.write(sb.toString());

        for (TransactionModel transaction : transactions) {
            sb = new StringBuilder();
            sb.append(this.mainViewController.transactionController.convertTimeStampToString(transaction.blockTimeProperty.getValue())).append(exportSplitter);
            sb.append(transaction.typeProperty.getValue()).append(exportSplitter);
            sb.append(String.format(localeDecimal, "%.8f", transaction.cryptoValueProperty.getValue())).append(exportSplitter);
            sb.append(transaction.cryptoCurrencyProperty.getValue()).append(exportSplitter);
            sb.append(String.format(localeDecimal, "%.8f", transaction.fiatValueProperty.getValue())).append(exportSplitter);
            sb.append(transaction.fiatCurrencyProperty.getValue()).append(exportSplitter);
            sb.append(transaction.poolIDProperty.getValue()).append(exportSplitter);
            sb.append(transaction.blockHeightProperty.getValue()).append(exportSplitter);
            sb.append(transaction.blockHashProperty.getValue()).append(exportSplitter);
            sb.append(transaction.ownerProperty.getValue()).append(exportSplitter);
            sb.append(transaction.txIDProperty.getValue());
            sb.append("\n");
            writer.write(sb.toString());
            sb = null;
        }
        writer.close();
        return true;

    }

    public boolean exportTransactionToExcelDaily(List<TransactionModel> transactions, String exportPath, Locale localeDecimal, String exportSplitter) {
        File exportFile = new File(exportPath);
        this.mainViewController.settingsController.lastExportPath = exportFile.getParent();
        this.mainViewController.settingsController.saveSettings();
        if (exportFile.exists()) exportFile.delete();

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(exportPath, true));
        } catch (IOException e) {
            SettingsController.getInstance().logger.warning("Exception occured: " + e.toString());
        }
        StringBuilder sb = new StringBuilder();

        for (TableColumn column : this.mainViewController.mainView.rawDataTable.getColumns()
        ) {
            sb.append(column.getText()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
        }

        sb.setLength(sb.length() - 1);
        sb.append("\n");
        writer.write(sb.toString());
        TreeMap<String, TransactionModel> exportList = new TreeMap<>();
        String oldDate = "";
        for (TransactionModel transaction : transactions) {
            String newDate = this.mainViewController.transactionController.convertTimeStampWithoutTimeToString(transaction.blockTimeProperty.getValue());

            if(transaction.typeProperty.getValue().equals("Commission") || transaction.typeProperty.getValue().equals("Rewards")){

            if ((oldDate.equals("") || oldDate.equals(newDate)) ) {
                String key = this.mainViewController.transactionController.getPoolPairFromId(transaction.poolIDProperty.getValue()) + transaction.cryptoCurrencyProperty.getValue() + transaction.typeProperty.getValue();
                if (!exportList.containsKey(key)) {
                     exportList.put(key,new TransactionModel(transaction.blockTimeProperty.getValue(),transaction.ownerProperty.getValue(),transaction.typeProperty.getValue(),transaction.amountProperty.getValue(),transaction.blockHashProperty.getValue(),transaction.blockHeightProperty.getValue(),transaction.poolIDProperty.getValue(),transaction.txIDProperty.getValue(),this.mainViewController.transactionController));
                } else {
                    exportList.get(key).cryptoValueProperty.set(exportList.get(key).cryptoValueProperty.getValue() + transaction.cryptoValueProperty.getValue());
                    exportList.get(key).fiatValueProperty.set(exportList.get(key).fiatValueProperty.getValue() + transaction.fiatValueProperty.getValue());
                }
            } else {
                for (HashMap.Entry<String, TransactionModel> entry : exportList.entrySet()) {

                sb = new StringBuilder();
                sb.append(this.mainViewController.transactionController.convertTimeStampWithoutTimeToString(entry.getValue().blockTimeProperty.getValue())).append(exportSplitter);
                sb.append(entry.getValue().typeProperty.getValue()).append(exportSplitter);
                sb.append(String.format(localeDecimal, "%.8f", entry.getValue().cryptoValueProperty.getValue())).append(exportSplitter);
                sb.append(entry.getValue().cryptoCurrencyProperty.getValue()).append(exportSplitter);
                sb.append(String.format(localeDecimal, "%.8f", entry.getValue().fiatValueProperty.getValue())).append(exportSplitter);
                sb.append(entry.getValue().fiatCurrencyProperty.getValue()).append(exportSplitter);
                sb.append(entry.getValue().poolIDProperty.getValue()).append(exportSplitter);
                sb.append(entry.getValue().blockHeightProperty.getValue()).append(exportSplitter);
                sb.append(entry.getValue().blockHashProperty.getValue()).append(exportSplitter);
                sb.append(entry.getValue().ownerProperty.getValue()).append(exportSplitter);
                sb.append(entry.getValue().txIDProperty.getValue());
                sb.append("\n");
                writer.write(sb.toString());
                sb = null;

                }
                exportList = new TreeMap<>();

                String key = this.mainViewController.transactionController.getPoolPairFromId(transaction.poolIDProperty.getValue()) + transaction.cryptoCurrencyProperty.getValue() + transaction.typeProperty.getValue();
                exportList.put(key,new TransactionModel(transaction.blockTimeProperty.getValue(),transaction.ownerProperty.getValue(),transaction.typeProperty.getValue(),transaction.amountProperty.getValue(),transaction.blockHashProperty.getValue(),transaction.blockHeightProperty.getValue(),transaction.poolIDProperty.getValue(),transaction.txIDProperty.getValue(),this.mainViewController.transactionController));

            }

            }else{
                sb = new StringBuilder();
                sb.append(this.mainViewController.transactionController.convertTimeStampToString(transaction.blockTimeProperty.getValue())).append(exportSplitter);
                sb.append(transaction.typeProperty.getValue()).append(exportSplitter);
                sb.append(String.format(localeDecimal, "%.8f", transaction.cryptoValueProperty.getValue())).append(exportSplitter);
                sb.append(transaction.cryptoCurrencyProperty.getValue()).append(exportSplitter);
                sb.append(String.format(localeDecimal, "%.8f", transaction.fiatValueProperty.getValue())).append(exportSplitter);
                sb.append(transaction.fiatCurrencyProperty.getValue()).append(exportSplitter);
                sb.append(transaction.poolIDProperty.getValue()).append(exportSplitter);
                sb.append(transaction.blockHeightProperty.getValue()).append(exportSplitter);
                sb.append(transaction.blockHashProperty.getValue()).append(exportSplitter);
                sb.append(transaction.ownerProperty.getValue()).append(exportSplitter);
                sb.append(transaction.txIDProperty.getValue());
                sb.append("\n");
                writer.write(sb.toString());
                sb = null;
            }

            oldDate = newDate;
        }

        for (HashMap.Entry<String, TransactionModel> entry : exportList.entrySet()) {

            sb = new StringBuilder();
            sb.append(this.mainViewController.transactionController.convertTimeStampWithoutTimeToString(entry.getValue().blockTimeProperty.getValue())).append(exportSplitter);
            sb.append(entry.getValue().typeProperty.getValue()).append(exportSplitter);
            sb.append(String.format(localeDecimal, "%.8f", entry.getValue().cryptoValueProperty.getValue())).append(exportSplitter);
            sb.append(entry.getValue().cryptoCurrencyProperty.getValue()).append(exportSplitter);
            sb.append(String.format(localeDecimal, "%.8f", entry.getValue().fiatValueProperty.getValue())).append(exportSplitter);
            sb.append(entry.getValue().fiatCurrencyProperty.getValue()).append(exportSplitter);
            sb.append(entry.getValue().poolIDProperty.getValue()).append(exportSplitter);
            sb.append(entry.getValue().blockHeightProperty.getValue()).append(exportSplitter);
            sb.append(entry.getValue().blockHashProperty.getValue()).append(exportSplitter);
            sb.append(entry.getValue().ownerProperty.getValue()).append(exportSplitter);
            sb.append(entry.getValue().txIDProperty.getValue());
            sb.append("\n");
            writer.write(sb.toString());
            sb = null;

        }
        writer.close();
        exportList.clear();
        return true;

    }

    public boolean exportPoolPairToExcel(List<PoolPairModel> poolPairModelList, String exportPath, String source, MainView mainView) {
        try {
            PrintWriter writer = new PrintWriter(exportPath);
            StringBuilder sb = new StringBuilder();

            Locale localeDecimal = Locale.GERMAN;
            if (this.mainViewController.settingsController.selectedDecimal.getValue().equals(".")) {
                localeDecimal = Locale.US;
            }
            switch (mainView.tabPane.getSelectionModel().getSelectedItem().getText()) {
                case "Portfolio":
                    sb.append((mainView.plotTable.getColumns().get(0).getText() + "," + mainView.plotTable.getColumns().get(2).getText() + "," + mainView.plotTable.getColumns().get(2).getText() + "," + mainView.plotTable.getColumns().get(9).getText()).replace(",", this.mainViewController.settingsController.selectedSeperator.getValue())).append("\n");
                    for (PoolPairModel poolPairModel : poolPairModelList) {
                        sb.append(poolPairModel.getBlockTime().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getPoolPair().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getBalanceFiatValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append("\n");
                    }
                    break;
                case "Overview":
                case "Übersicht":
                    sb.append((mainView.plotTable.getColumns().get(0).getText() + "," + mainView.plotTable.getColumns().get(1).getText() + "," + mainView.plotTable.getColumns().get(2).getText() + "," + mainView.plotTable.getColumns().get(3).getText() + "," + mainView.plotTable.getColumns().get(4).getText()+ "," + mainView.plotTable.getColumns().get(5).getText()+ "," + mainView.plotTable.getColumns().get(6).getText()+ "," + mainView.plotTable.getColumns().get(7).getText()+ "," + mainView.plotTable.getColumns().get(8).getText()).replace(",", this.mainViewController.settingsController.selectedSeperator.getValue())).append("\n");
                    for (PoolPairModel poolPairModel : poolPairModelList) {
                        sb.append(poolPairModel.getBlockTime().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getPoolPair().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValue1().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoFiatValue1().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValue2().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoFiatValue2().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getcryptoCommission2Overviewvalue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getcryptoCommission2FiatOverviewvalue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getFiatValue().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append("\n");
                    }
                    break;
                case "Kommissionen":
                case "Commissions":
                    sb.append((mainView.plotTable.getColumns().get(0).getText() + "," + mainView.plotTable.getColumns().get(1).getText() + "," + mainView.plotTable.getColumns().get(2).getText() + "," + mainView.plotTable.getColumns().get(3).getText() + "," + mainView.plotTable.getColumns().get(4).getText()+ "," + mainView.plotTable.getColumns().get(5).getText()+ "," + mainView.plotTable.getColumns().get(8).getText()).replace(",", this.mainViewController.settingsController.selectedSeperator.getValue())).append("\n");
                    for (PoolPairModel poolPairModel : poolPairModelList) {
                        sb.append(poolPairModel.getBlockTime().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getPoolPair().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValue1().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoFiatValue1().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValue2().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoFiatValue2().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getFiatValue().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append("\n");
                    }
                    break;
                case "Rewards":
                case "Belohnungen":
                    sb.append((mainView.plotTable.getColumns().get(0).getText() + "," + mainView.plotTable.getColumns().get(1).getText() + "," + mainView.plotTable.getColumns().get(2).getText() + "," + mainView.plotTable.getColumns().get(3).getText()).replace(",", this.mainViewController.settingsController.selectedSeperator.getValue())).append("\n");
                    for (PoolPairModel poolPairModel : poolPairModelList) {
                        sb.append(poolPairModel.getBlockTime().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(poolPairModel.getPoolPair().getValue()).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoValue1().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append(String.format(localeDecimal, "%.8f", poolPairModel.getCryptoFiatValue1().getValue())).append(this.mainViewController.settingsController.selectedSeperator.getValue());
                        sb.append("\n");
                    }
                    break;
                default:
                    break;
            }
            writer.write(sb.toString());
            writer.close();
            return true;
        } catch (FileNotFoundException e) {
            SettingsController.getInstance().logger.warning("Exception occured: " + e.toString());
            return false;
        }
    }

    public static String getIdFromPoolPair(String poolID) {
        String pool;
        switch (poolID) {
            case "DFI":
                pool = "0";
                break;
            case "ETH":
                pool = "1";
                break;
            case "BTC":
                pool = "2";
                break;
            case "USDT":
                pool = "3";
                break;
            case "ETH-DFI":
                pool = "4";
                break;
            case "BTC-DFI":
                pool = "5";
                break;
            case "USDT-DFI":
                pool = "6";
                break;
            case "DOGE":
                pool = "7";
                break;
            case "DOGE-DFI":
                pool = "8";
                break;
            case "LTC":
                pool = "9";
                break;
            case "LTC-DFI":
                pool = "10";
                break;
            case "BCH":
                pool = "11";
                break;
            case "BCH-DFI":
                pool = "12";
                break;
            default:
                pool = "-";
                break;
        }
        return pool;
    }


}