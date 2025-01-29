package soft2412.currencyconverter;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

// Handles GUI components for Add New Rates stage
public class AddRatesController extends BaseController {
    @FXML
    private ImageView usernameVerified;
    @FXML
    private ImageView passwordVerified;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private TextField inputCurrency1;
    @FXML
    private TextField inputCurrency2;
    @FXML
    private TextField exchangeRate;
    @FXML
    private TextField date;
    @FXML
    private Label outputResult;

    @FXML
    private void onAddRate() {
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
        if(inputCurrency1.getText().isEmpty()) {
            outputResult.setText(" Currency #1 Not Specified...");
        } else if(inputCurrency2.getText().isEmpty()) {
            outputResult.setText(" Currency #2 Not Specified...");
        } else if(exchangeRate.getText().isEmpty()) {
            outputResult.setText(" Exchange Rate Not Specified...");
        } else if(date.getText().isEmpty()) {
            outputResult.setText(" Date Not Specified...");
        } else if(inputCurrency1.getText().equalsIgnoreCase(inputCurrency2.getText())) {
            outputResult.setText(" Currency #1 Cannot Equal Currency #2...");
        } else if(!DateChecker.isValidDate(date.getText())) {
            outputResult.setText(" Date Has Incorrect Format...");
        }else {
            double exchangeRateDouble = 0.00;

            try {
                exchangeRateDouble = Double.parseDouble(exchangeRate.getText());

                if(exchangeRateDouble <= 0) {
                    throw new IllegalArgumentException(" Exchange Rate Cannot Be A Negative Number...");
                }
            } catch (NumberFormatException e) {
                outputResult.setText(e.getMessage());
                return;
            } catch(Exception e) {
                outputResult.setText(" Exchange Rate Has Incorrect Format...");
                return;
            }

            db.insertRate(inputCurrency1.getText(), inputCurrency2.getText(), exchangeRateDouble,
                    date.getText(), 1);

            outputResult.setText(" Rate Successfully Added!");
        }
    }
}
