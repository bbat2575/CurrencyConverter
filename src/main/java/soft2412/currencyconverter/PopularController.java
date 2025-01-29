package soft2412.currencyconverter;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

// Handles GUI components for Popular stage
public class PopularController extends BaseController {

    @FXML
    private ImageView usernameVerified;
    @FXML
    private ImageView passwordVerified;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private ChoiceBox inputCurrency1;
    @FXML
    private ChoiceBox inputCurrency2;
    @FXML
    private ChoiceBox inputCurrency3;
    @FXML
    private ChoiceBox inputCurrency4;
    @FXML
    private Label outputResult;
    @FXML
    private Label gridCurrencyC1;
    @FXML
    private Label gridCurrencyC2;
    @FXML
    private Label gridCurrencyC3;
    @FXML
    private Label gridCurrencyC4;
    @FXML
    private Label gridCurrencyR1;
    @FXML
    private Label gridCurrencyR2;
    @FXML
    private Label gridCurrencyR3;
    @FXML
    private Label gridCurrencyR4;
    @FXML
    private Label gridCurrency2t1;
    @FXML
    private Label gridCurrency3t1;
    @FXML
    private Label gridCurrency4t1;
    @FXML
    private Label gridCurrency1t2;
    @FXML
    private Label gridCurrency3t2;
    @FXML
    private Label gridCurrency4t2;
    @FXML
    private Label gridCurrency1t3;
    @FXML
    private Label gridCurrency2t3;
    @FXML
    private Label gridCurrency4t3;
    @FXML
    private Label gridCurrency1t4;
    @FXML
    private Label gridCurrency2t4;
    @FXML
    private Label gridCurrency3t4;
    @FXML
    private ImageView gridArrow2t1;
    @FXML
    private ImageView gridArrow3t1;
    @FXML
    private ImageView gridArrow4t1;
    @FXML
    private ImageView gridArrow1t2;
    @FXML
    private ImageView gridArrow3t2;
    @FXML
    private ImageView gridArrow4t2;
    @FXML
    private ImageView gridArrow1t3;
    @FXML
    private ImageView gridArrow2t3;
    @FXML
    private ImageView gridArrow4t3;
    @FXML
    private ImageView gridArrow1t4;
    @FXML
    private ImageView gridArrow2t4;
    @FXML
    private ImageView gridArrow3t4;

    @FXML
    private void initialize() {
        // Get currencies from database to populate ChoiceBoxes
        inputCurrency1.setItems(db.getCurrencies());
        inputCurrency1.setValue("Currency #1");
        inputCurrency2.setItems(db.getCurrencies());
        inputCurrency2.setValue("Currency #2");
        inputCurrency3.setItems(db.getCurrencies());
        inputCurrency3.setValue("Currency #3");
        inputCurrency4.setItems(db.getCurrencies());
        inputCurrency4.setValue("Currency #4");

        updatePopularTable();
    }

    // Update the popular table
    private void updatePopularTable() {
        gridCurrencyC1.setText(db.findPopular().get(0));
        gridCurrencyC2.setText(db.findPopular().get(1));
        gridCurrencyC3.setText(db.findPopular().get(2));
        gridCurrencyC4.setText(db.findPopular().get(3));
        gridCurrencyR1.setText(db.findPopular().get(0));
        gridCurrencyR2.setText(db.findPopular().get(1));
        gridCurrencyR3.setText(db.findPopular().get(2));
        gridCurrencyR4.setText(db.findPopular().get(3));

        updateGrid(gridCurrency2t1, gridCurrencyC2, gridCurrencyC1, gridArrow2t1);
        updateGrid(gridCurrency3t1, gridCurrencyC3, gridCurrencyC1, gridArrow3t1);
        updateGrid(gridCurrency4t1, gridCurrencyC4, gridCurrencyC1, gridArrow4t1);
        updateGrid(gridCurrency1t2, gridCurrencyC1, gridCurrencyC2, gridArrow1t2);
        updateGrid(gridCurrency3t2, gridCurrencyC3, gridCurrencyC2, gridArrow3t2);
        updateGrid(gridCurrency4t2, gridCurrencyC4, gridCurrencyC2, gridArrow4t2);
        updateGrid(gridCurrency1t3, gridCurrencyC1, gridCurrencyC3, gridArrow1t3);
        updateGrid(gridCurrency2t3, gridCurrencyC2, gridCurrencyC3, gridArrow2t3);
        updateGrid(gridCurrency4t3, gridCurrencyC4, gridCurrencyC3, gridArrow4t3);
        updateGrid(gridCurrency1t4, gridCurrencyC1, gridCurrencyC4, gridArrow1t4);
        updateGrid(gridCurrency2t4, gridCurrencyC2, gridCurrencyC4, gridArrow2t4);
        updateGrid(gridCurrency3t4, gridCurrencyC3, gridCurrencyC4, gridArrow3t4);


    }

    // Used to update a single grid in the popular table
    private void updateGrid(Label gridCurrency, Label gridCurrencyFrom, Label gridCurrencyTo, ImageView gridArrow) {
        gridCurrency.setText(String.format("%.2f", db.findRate(gridCurrencyFrom.getText(),
                gridCurrencyTo.getText())));
        if(db.findRateChange(gridCurrencyFrom.getText(), gridCurrencyTo.getText()).equals("I"))
            gridArrow.setImage(new Image("file:src/main/resources/soft2412/currencyconverter/icons/uarrow.png"));
        else if(db.findRateChange(gridCurrencyFrom.getText(), gridCurrencyTo.getText()).equals("D"))
            gridArrow.setImage(new Image("file:src/main/resources/soft2412/currencyconverter/icons/darrow.png"));
        else
            gridArrow.setImage(null);
    }

    // Functionality for setting the popular currencies
    @FXML
    private void setPopularCurrencies() {
        // Verify username and password
        if(!username.getText().equals("admin") && !password.getText().equals("admin")) {
            outputResult.setText(" Incorrect username and password!");
            usernameVerified.setImage(new Image("file:src/main/resources/soft2412/currencyconverter/icons/cross.png"));
            passwordVerified.setImage(new Image("file:src/main/resources/soft2412/currencyconverter/icons/cross.png"));
            return;
        } else if(!username.getText().equals("admin")) {
            outputResult.setText(" Incorrect username!");
            usernameVerified.setImage(new Image("file:src/main/resources/soft2412/currencyconverter/icons/cross.png"));
            passwordVerified.setImage(new Image("file:src/main/resources/soft2412/currencyconverter/icons/check.png"));
            return;
        } else if(!password.getText().equals("admin")) {
            outputResult.setText(" Incorrect password!");
            usernameVerified.setImage(new Image("file:src/main/resources/soft2412/currencyconverter/icons/check.png"));
            passwordVerified.setImage(new Image("file:src/main/resources/soft2412/currencyconverter/icons/cross.png"));
            return;
        } else {
            usernameVerified.setImage(new Image("file:src/main/resources/soft2412/currencyconverter/icons/check.png"));
            passwordVerified.setImage(new Image("file:src/main/resources/soft2412/currencyconverter/icons/check.png"));
        }

        // Verify other fields
        if(inputCurrency1.getValue().equals("Currency #1")) {
            outputResult.setText(" Currency #1 Not Specified...");
        } else if(inputCurrency2.getValue().equals("Currency #2")) {
            outputResult.setText(" Currency #2 Not Specified...");
        } else if(inputCurrency3.getValue().equals("Currency #3")) {
            outputResult.setText(" Currency #3 Not Specified...");
        } else if(inputCurrency4.getValue().equals("Currency #4")) {
            outputResult.setText(" Currency #4 Not Specified...");
        } else if(inputCurrency1.getValue().equals(inputCurrency2.getValue())) {
            outputResult.setText(" Currency #1 Cannot Equal Currency #2...");
        } else if(inputCurrency1.getValue().equals(inputCurrency3.getValue())) {
            outputResult.setText(" Currency #1 Cannot Equal Currency #3...");
        } else if(inputCurrency1.getValue().equals(inputCurrency4.getValue())) {
            outputResult.setText(" Currency #1 Cannot Equal Currency #4...");
        } else if(inputCurrency2.getValue().equals(inputCurrency1.getValue())) {
            outputResult.setText(" Currency #2 Cannot Equal Currency #1...");
        } else if(inputCurrency2.getValue().equals(inputCurrency3.getValue())) {
            outputResult.setText(" Currency #2 Cannot Equal Currency #3...");
        } else if(inputCurrency2.getValue().equals(inputCurrency4.getValue())) {
            outputResult.setText(" Currency #2 Cannot Equal Currency #4...");
        } else if(inputCurrency3.getValue().equals(inputCurrency1.getValue())) {
            outputResult.setText(" Currency #3 Cannot Equal Currency #1...");
        } else if(inputCurrency3.getValue().equals(inputCurrency2.getValue())) {
            outputResult.setText(" Currency #3 Cannot Equal Currency #2...");
        } else if(inputCurrency3.getValue().equals(inputCurrency4.getValue())) {
            outputResult.setText(" Currency #3 Cannot Equal Currency #4...");
        } else if(inputCurrency4.getValue().equals(inputCurrency1.getValue())) {
            outputResult.setText(" Currency #4 Cannot Equal Currency #1...");
        } else if(inputCurrency4.getValue().equals(inputCurrency2.getValue())) {
            outputResult.setText(" Currency #4 Cannot Equal Currency #2...");
        } else if(inputCurrency4.getValue().equals(inputCurrency3.getValue())) {
            outputResult.setText(" Currency #4 Cannot Equal Currency #3...");
        } else {
            db.setPopular(inputCurrency1.getValue().toString(), inputCurrency2.getValue().toString(),
                    inputCurrency3.getValue().toString(), inputCurrency4.getValue().toString());

            outputResult.setText(" Popular Currencies Successfully Updated!");

            updatePopularTable();
        }
    }
}
