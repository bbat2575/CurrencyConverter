package soft2412.currencyconverter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

// The main GUI application class
public class CurrencyConverter extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CurrencyConverter.class.getResource("converter.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 743, 445);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setTitle("Currency Converter");
        stage.setScene(scene);

        stage.setMinWidth(743);
        stage.setMinHeight(484);
        stage.setMaxWidth(743);
        stage.setMaxHeight(484);
        stage.setWidth(743);
        stage.setHeight(484);

        stage.show();
    }

    public static void main(String[] args) {
        SQLiteConnection connect = new SQLiteConnection("jdbc:sqlite:db/rates.db");
        connect.connect();
        connect.initializeTables();

        launch();
    }
}