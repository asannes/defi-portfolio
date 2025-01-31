package portfolio.views;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import portfolio.controllers.SettingsController;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class NoDataView implements Initializable {
    @FXML
    public ImageView img;
    @FXML
    public Label infoText;
    public Button btnClose;

    public void  btnWikiclicked(){
        if (SettingsController.getInstance().getPlatform().equals("linux")) {
            // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
            try {
                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://defichain-wiki.com/wiki/DeFiChain-Portfolio"});
                } else {
                    System.out.println("xdg-open is not supported!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Desktop.getDesktop().browse(new URL("https://defichain-wiki.com/wiki/DeFiChain-Portfolio").toURI());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.infoText.textProperty().set(SettingsController.getInstance().translationList.getValue().get("InfoNoData").toString());
    }

    public void Close(MouseEvent mouseEvent) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}


