package soft2412.currencyconverter;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.HashMap;
import java.util.Map;

// Handles GUI components for Converter stage
public class ConverterController extends BaseController {
    @FXML
    private ChoiceBox inputCurrency1;
    @FXML
    private ChoiceBox inputCurrency2;
    @FXML
    private TextField inputAmount;
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
    private void swapCurrencies() {
        if(!inputCurrency1.getValue().equals("Currency #1") && !inputCurrency2.getValue().equals("Currency #2")) {
            Object temp1 = inputCurrency1.getValue();
            Object temp2 = inputCurrency2.getValue();
            inputCurrency1.setValue(temp2);
            inputCurrency2.setValue(temp1);
        } else if(!inputCurrency1.getValue().equals("Currency #1")) {
            inputCurrency2.setValue(inputCurrency1.getValue());
            inputCurrency1.setValue("Currency #1");
        } else if(!inputCurrency2.getValue().equals("Currency #2")) {
            inputCurrency1.setValue(inputCurrency2.getValue());
            inputCurrency2.setValue("Currency #2");
        }
    }

    // Functionality for converting currencies
    @FXML
    private void onCurrencyConversion() {
        if(inputCurrency1.getValue().toString().equals(inputCurrency2.getValue().toString())) {
            outputResult.setText(" Currency #1 Cannot Equal Currency #2...");
        } else if(inputCurrency1.getValue().equals("Currency #1")) {
            outputResult.setText(" Currency #1 Not Specified...");
        } else if(inputCurrency2.getValue().equals("Currency #2")) {
            outputResult.setText(" Currency #2 Not Specified...");
        } else if(inputAmount.getText().isEmpty()) {
            outputResult.setText(" Amount Not Specified...");
        } else {
            double fromAmount = 0.00;
            double toAmount = 0.00;

            try {
                fromAmount = Double.parseDouble(inputAmount.getText());
            } catch(Exception e) {
                outputResult.setText(" Amount Has Incorrect Format...");
                return;
            }

            toAmount = fromAmount * db.findRate(inputCurrency1.getValue().toString(), inputCurrency2.getValue().toString());

            outputResult.setWrapText(true);

            outputResult.setText(db.getCurrencySymbol(inputCurrency1.getValue().toString()) + String.format("%.2f", fromAmount)
                    + " = " + db.getCurrencySymbol(inputCurrency2.getValue().toString()) + String.format("%.2f", toAmount));
        }
    }
}
