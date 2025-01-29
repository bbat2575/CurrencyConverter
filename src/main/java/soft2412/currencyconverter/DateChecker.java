package soft2412.currencyconverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// A class to check date formats
public class DateChecker {
    public static boolean isValidDate(String date) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            LocalDate.parse(date, df);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
