package soft2412.currencyconverter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

// Base Class for GUI Controllers
public class BaseController {
    protected SQLiteOperations db = new SQLiteOperations("jdbc:sqlite:db/rates.db");

    // Allows users to change between menu tabs on the left-hand side of gui
    @FXML
    protected void onMenuSelection(ActionEvent event) throws IOException {
        String fxmlFile = ((Button) event.getSource()).getText().toLowerCase().replace(" ", "") + ".fxml";
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setTitle("Currency Converter");
        stage.setScene(scene);
        stage.show();
    }
}
