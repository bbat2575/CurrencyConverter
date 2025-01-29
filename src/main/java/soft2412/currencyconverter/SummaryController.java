package soft2412.currencyconverter;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;

// Handles GUI components for Summary stage
public class SummaryController extends BaseController {
    @FXML
    private ChoiceBox inputCurrency1;
    @FXML
    private ChoiceBox inputCurrency2;
    @FXML
    private TextField fromDate;
    @FXML
    private TextField toDate;
    @FXML
    private Label averageField;
    @FXML
    private Label medianField;
    @FXML
    private Label sdField;
    @FXML
    private Label minField;
    @FXML
    private Label maxField;
    @FXML
    private Label allRatesField;
    @FXML
    private Label outputResult;

    @FXML
    private void initialize() {
        // Get currencies from database to populate ChoiceBoxes
        inputCurrency1.setItems(db.getCurrencies());
        inputCurrency1.setValue("Currency #1");
        inputCurrency2.setItems(db.getCurrencies());
        inputCurrency2.setValue("Currency #2");
    }

    @FXML
    private void onLoadSummary() {
        if(inputCurrency1.getValue().toString().equals(inputCurrency2.getValue().toString())) {
            outputResult.setText(" Currency #1 Cannot Equal Currency #2...");
        } else if(inputCurrency1.getValue().equals("Currency #1")) {
            outputResult.setText(" Currency #1 Not Specified...");
        } else if(inputCurrency2.getValue().equals("Currency #2")) {
            outputResult.setText(" Currency #2 Not Specified...");
        } else if(fromDate.getText().isEmpty()) {
            outputResult.setText(" From Date Not Specified...");
        } else if(toDate.getText().isEmpty()) {
            outputResult.setText(" To Date Not Specified...");
        } else if(!DateChecker.isValidDate(fromDate.getText())) {
            outputResult.setText(" From Date Has Incorrect Format...");
        } else if(!DateChecker.isValidDate(toDate.getText())) {
            outputResult.setText(" To Date Has Incorrect Format...");
        } else {
            outputResult.setText("");
            averageField.setText(String.format("%.2f", db.findAverageRateWithinDates(inputCurrency1.getValue().toString(),
                    inputCurrency2.getValue().toString(), fromDate.getText(), toDate.getText())));

            medianField.setText(String.format("%.2f", db.findMedian(inputCurrency1.getValue().toString(),
                    inputCurrency2.getValue().toString(), fromDate.getText(), toDate.getText())));

            sdField.setText(String.format("%.2f", db.findSD(inputCurrency1.getValue().toString(),
                    inputCurrency2.getValue().toString(), fromDate.getText(), toDate.getText())));

            minField.setText(String.format("%.2f", db.findMinimum(inputCurrency1.getValue().toString(),
                    inputCurrency2.getValue().toString(), fromDate.getText(), toDate.getText())));

            maxField.setText(String.format("%.2f", db.findMaximum(inputCurrency1.getValue().toString(),
                    inputCurrency2.getValue().toString(), fromDate.getText(), toDate.getText())));

            // Create a list to store the Double ArrayList of exchange rates
            ArrayList<String> ratesList = new ArrayList<>();

            // Convert the list to String
            for (Double d : db.findAllConversionRates(inputCurrency1.getValue().toString(),
                    inputCurrency2.getValue().toString(), fromDate.getText(), toDate.getText())) {
                ratesList.add(d.toString());
            }

            // Join into a single string with each rate on a new line
            allRatesField.setText(String.join(System.lineSeparator(), ratesList));
        }
    }
}
