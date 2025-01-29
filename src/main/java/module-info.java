module soft2412.currencyconverter {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    requires java.sql;

    opens soft2412.currencyconverter to javafx.fxml;
    exports soft2412.currencyconverter;
}