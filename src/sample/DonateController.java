package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class DonateController {
    @FXML
    private Button btnCopy;

    public void  btnCopyPressed(){
        String myString = "dMswTqWd43S9Yu1m4LiX3QYPL2BAs7d37V";
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

}
