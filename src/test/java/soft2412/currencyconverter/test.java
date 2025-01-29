package soft2412.currencyconverter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import static org.mockito.Mockito.*;
import java.util.List;


public class test {

    private SQLiteOperations db_test;
    private String test_url = "jdbc:sqlite:db/rates_test.db";

    // This sets up the SQL tables and database before each test to ensure that each test runs independantly from each other and does not affect the actual tables and database.
    // Initial values are also inputted into the table for testing.
    @BeforeEach
    void setup_test() {
        SQLiteConnection connect = new SQLiteConnection(test_url);
        connect.connect();
        connect.initializeTables();

        db_test = new SQLiteOperations(test_url);

        symbols_table();
        rates_table();
        popular_table();
    }

    // This resets the table and database by clearing it to ensure that the changes made by the test before will not affect future tests.
    @AfterEach
    void reset_test() {
        reset_tables();
    }

    void rates_table() {
        String insert_rates = "INSERT INTO rates (fromCurrency, toCurrency, rate, date, time, rateChange) VALUES " +
                "('AUD', 'USD', 0.67, '2024-09-06', 2676, '-')," +
                "('USD', 'AUD', 1.49, '2024-09-06', 2676, '-');";

        try{
            Connection conn1 = DriverManager.getConnection(test_url);
            Statement stmt1 = conn1.createStatement();
            stmt1.executeUpdate(insert_rates);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    void popular_table() {
        String insert_popular = "INSERT INTO popular (rank, currency) VALUES " +
                "(1, 'AUD')," +
                "(2, 'CAD')," +
                "(3, 'CNY')," +
                "(4, 'EUR');";

        try{
            Connection conn1 = DriverManager.getConnection(test_url);
            Statement stmt1 = conn1.createStatement();
            stmt1.execute(insert_popular);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    void symbols_table() {
        String insert_symbols = "INSERT INTO symbols (currency, symbol) VALUES " +
                "('USD', '$')," +
                "('EUR', '€')," +
                "('GBP', '£')," +
                "('JPY', '¥')," +
                "('AUD', 'A$')," +
                "('CNY', 'CNY')," +
                "('INR', '₹')";

        try{
            Connection conn1 = DriverManager.getConnection(test_url);
            Statement stmt1 = conn1.createStatement();
            stmt1.execute(insert_symbols);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void reset_tables() {
        String reset_rates = "DROP TABLE IF EXISTS rates;";
        String reset_popular = "DROP TABLE IF EXISTS popular;";
        String reset_symbols = "DROP TABLE IF EXISTS symbols;";

        try {
            Connection conn1 = DriverManager.getConnection(test_url);
            Statement stmt1 = conn1.createStatement();
            stmt1.execute(reset_rates);
            stmt1.execute(reset_popular);
            stmt1.execute(reset_symbols);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    //
    @Test
    void test_date_converter() {
        String input_date = "06/09/2024";
        Date expectedDate = Date.valueOf("2024-09-06");

        Date date_test_result = db_test.convertJavaDateToSQL(input_date);

        assertEquals(expectedDate, date_test_result, "The converted date should match the expected date.");
    }

    @Test
    void test_findRate() {
        String from_test = "AUD";
        String to_test = "USD";
        double expected_rate = 0.67;
        double result = db_test.findRate(from_test, to_test);

        assertNotNull(result);
        assertEquals(result, expected_rate);
    }

    @Test
    void test_insert() {
        String from_test = "AUD";
        String to_test = "CAD";
        double expected_rate = 0;
        double result = db_test.findRate(from_test, to_test);

        assertNotNull(result);
        assertEquals(result, expected_rate);
        db_test.insertRate("AUD", "CAD", 0.91, "07/09/2024", 1);
        expected_rate = 0.91;
        result = db_test.findRate(from_test, to_test);

        db_test.insertRate("AUD", "CAD", 0.94, "08/09/2024", 1);
        expected_rate = 0.94;
        result = db_test.findRate(from_test, to_test);

        assertEquals(expected_rate, result);
    }

    @Test
    void test_getCurrencies() {
        ObservableList<String> result = db_test.getCurrencies();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList("AUD", "USD")));
    }

    @Test
    void test_getCurrencies_change() {
        ObservableList<String> result = db_test.getCurrencies();

        List<String> expected_rates = Arrays.asList("AUD", "USD");
        assertEquals(2, result.size());
        assertEquals(expected_rates, result);


        db_test.insertRate("AUD", "EUR", 0.61, "07/09/2024", 1);
        db_test.insertRate("USD", "CNY", 4.77, "07/09/2024", 1);
        db_test.insertRate("CAD", "GBP", 0.56, "07/09/2024", 1);
        result = db_test.getCurrencies();

        expected_rates = Arrays.asList("AUD", "CAD", "CNY", "EUR", "GBP", "USD");
        assertEquals(6, result.size());
        assertEquals(expected_rates, result);
    }


    @Test
    void test_findRate_missing() {
        String from_test = "AOD";
        String to_test = "USD";
        double expected_rate = 0;
        double result = db_test.findRate(from_test, to_test);

        assertNotNull(result);
        assertEquals(result, 0);
    }

    @Test
    void test_getCurrencySymbol() {
        String currency_test1 = "AUD";
        String currency_test2 = "CNY";
        String currency_test3 = "INR";
        String currency_test4 = "EUR";
        String currency_test5 = "JPY";
        String currency_fail_test1 = "COD";
        String expected_currency1 = "A$";
        String expected_currency2 = "CNY";
        String expected_currency3 = "₹";
        String expected_currency4 = "€";
        String expected_currency5 = "¥";

        String result1 = db_test.getCurrencySymbol(currency_test1);
        String result2 = db_test.getCurrencySymbol(currency_test2);
        String result3 = db_test.getCurrencySymbol(currency_test3);
        String result4 = db_test.getCurrencySymbol(currency_test4);
        String result5 = db_test.getCurrencySymbol(currency_test5);
        String fail_result1 = db_test.getCurrencySymbol(currency_fail_test1);

        assertNull(fail_result1);
        assertEquals(result1, expected_currency1);
        assertEquals(result2, expected_currency2);
        assertEquals(result3, expected_currency3);
        assertEquals(result4, expected_currency4);
        assertEquals(result5, expected_currency5);
    }

    // Testing whether the set popular function functions properly by first checking the current popular currencies through find popular and then changing them to new currencies
    @Test
    void test_popular() {
        ArrayList<String> current_popular = db_test.findPopular();
        List<String> expected_current = Arrays.asList("AUD", "CAD", "CNY", "EUR");
        assertEquals(expected_current, current_popular);

        db_test.setPopular("USD", "AUD", "GBP", "CAD");
        ArrayList<String> new_popular = db_test.findPopular();
        List<String> expected_new = Arrays.asList("USD", "AUD", "GBP", "CAD");
        assertEquals(new_popular, expected_new);

        db_test.setPopular("AUD", "CAD", "CNY", "EUR");
        current_popular = db_test.findPopular();
        assertEquals(current_popular, expected_current);
    }

    @Test
    void test_summary() {
        db_test.insertRate("AUD", "CAD", 0.91, "07/09/2024", 1);
        db_test.insertRate("AUD", "CAD", 0.92, "08/09/2024", 0);
        double expected_median = 0.915;
        double expected_maximum = 0.92;
        double expected_minimum = 0.91;
        double expected_sd = 0.01;
        double expected_average = 0.92;
        List<Double> expected_all_conversions = Arrays.asList(0.91, 0.92);

        double actual_median = db_test.findMedian("AUD", "CAD", "07/09/2024", "08/09/2024");
        double actual_maximum = db_test.findMaximum("AUD", "CAD", "07/09/2024", "08/09/2024");
        double actual_minimum = db_test.findMinimum("AUD", "CAD", "07/09/2024", "08/09/2024");
        double actual_sd = db_test.findSD("AUD", "CAD", "07/09/2024", "08/09/2024");
        double actual_average = db_test.findAverageRateWithinDates("AUD", "CAD", "07/09/2024", "08/09/2024");
        ArrayList<Double> actual_all_conversions = db_test.findAllConversionRates("AUD", "CAD", "07/09/2024", "08/09/2024");

        assertEquals(expected_median, actual_median);
        assertEquals(expected_maximum, actual_maximum);
        assertEquals(expected_minimum, actual_minimum);
        assertEquals(expected_sd, actual_sd);
        assertEquals(expected_average, actual_average);
        assertEquals(expected_all_conversions, actual_all_conversions);
    }

    @Test
    void test_summary_same_date() {
        db_test.insertRate("AUD", "CAD", 0.91, "08/09/2024", 1);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        db_test.insertRate("AUD", "CAD", 0.92, "08/09/2024", 0);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double expected_median = 0.915;
        double expected_maximum = 0.92;
        double expected_minimum = 0.91;
        double expected_sd = 0.01;
        double expected_average = 0.92;
        List<Double> expected_all_conversions = Arrays.asList(0.91, 0.92);

        double actual_median = db_test.findMedian("AUD", "CAD", "08/09/2024", "08/09/2024");
        double actual_maximum = db_test.findMaximum("AUD", "CAD", "08/09/2024", "08/09/2024");
        double actual_minimum = db_test.findMinimum("AUD", "CAD", "08/09/2024", "08/09/2024");
        double actual_sd = db_test.findSD("AUD", "CAD", "08/09/2024", "08/09/2024");
        double actual_average = db_test.findAverageRateWithinDates("AUD", "CAD", "08/09/2024", "08/09/2024");
        ArrayList<Double> actual_all_conversions = db_test.findAllConversionRates("AUD", "CAD", "08/09/2024", "08/09/2024");

        assertEquals(expected_median, actual_median);
        assertEquals(expected_maximum, actual_maximum);
        assertEquals(expected_minimum, actual_minimum);
        assertEquals(expected_sd, actual_sd);
        assertEquals(expected_average, actual_average);
        assertEquals(expected_all_conversions, actual_all_conversions);
    }

    @Test
    void test_summary_oustide_date() {
        db_test.insertRate("AUD", "GBP", 0.53, "07/09/2024", 1);
        db_test.insertRate("AUD", "GBP", 0.51, "08/09/2024", 0);
        db_test.insertRate("AUD", "GBP", 0.56, "09/09/2024", 0);
        double expected_median = 0.52;
        double expected_maximum = 0.53;
        double expected_minimum = 0.51;
        double expected_sd = 0.01;
        double expected_average = 0.52;
        List<Double> expected_all_conversions = Arrays.asList(0.53, 0.51);

        double actual_median = db_test.findMedian("AUD", "GBP", "07/09/2024", "08/09/2024");
        double actual_maximum = db_test.findMaximum("AUD", "GBP", "07/09/2024", "08/09/2024");
        double actual_minimum = db_test.findMinimum("AUD", "GBP", "07/09/2024", "08/09/2024");
        double actual_sd = db_test.findSD("AUD", "GBP", "07/09/2024", "08/09/2024");
        double actual_average = db_test.findAverageRateWithinDates("AUD", "GBP", "07/09/2024", "08/09/2024");
        ArrayList<Double> actual_all_conversions = db_test.findAllConversionRates("AUD", "GBP", "07/09/2024", "08/09/2024");

        assertEquals(expected_median, actual_median);
        assertEquals(expected_maximum, actual_maximum);
        assertEquals(expected_minimum, actual_minimum);
        assertEquals(expected_sd, actual_sd);
        assertEquals(expected_average, actual_average);
        assertEquals(expected_all_conversions, actual_all_conversions);
    }

    @Test
    void test_summary_after_change() {
        db_test.insertRate("AUD", "GBP", 0.53, "07/09/2024", 1);
        db_test.insertRate("AUD", "GBP", 0.51, "08/09/2024", 0);
        double expected_median = 0.52;
        double expected_maximum = 0.53;
        double expected_minimum = 0.51;
        double expected_sd = 0.01;
        double expected_average = 0.52;
        List<Double> expected_all_conversions = Arrays.asList(0.53, 0.51);

        double actual_median = db_test.findMedian("AUD", "GBP", "07/09/2024", "08/09/2024");
        double actual_maximum = db_test.findMaximum("AUD", "GBP", "07/09/2024", "08/09/2024");
        double actual_minimum = db_test.findMinimum("AUD", "GBP", "07/09/2024", "08/09/2024");
        double actual_sd = db_test.findSD("AUD", "GBP", "07/09/2024", "08/09/2024");
        double actual_average = db_test.findAverageRateWithinDates("AUD", "GBP", "07/09/2024", "08/09/2024");
        ArrayList<Double> actual_all_conversions = db_test.findAllConversionRates("AUD", "GBP", "07/09/2024", "08/09/2024");

        assertEquals(expected_median, actual_median);
        assertEquals(expected_maximum, actual_maximum);
        assertEquals(expected_minimum, actual_minimum);
        assertEquals(expected_sd, actual_sd);
        assertEquals(expected_average, actual_average);
        assertEquals(expected_all_conversions, actual_all_conversions);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        db_test.insertRate("AUD", "GBP", 0.56, "08/09/2024", 0);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        expected_median = 0.53;
        expected_maximum = 0.56;
        expected_minimum = 0.51;
        expected_sd = 0.03;
        expected_average = 0.53;
        expected_all_conversions = Arrays.asList(0.53, 0.51, 0.56);

        actual_median = db_test.findMedian("AUD", "GBP", "07/09/2024", "08/09/2024");
        actual_maximum = db_test.findMaximum("AUD", "GBP", "07/09/2024", "08/09/2024");
        actual_minimum = db_test.findMinimum("AUD", "GBP", "07/09/2024", "08/09/2024");
        actual_sd = db_test.findSD("AUD", "GBP", "07/09/2024", "08/09/2024");
        actual_average = db_test.findAverageRateWithinDates("AUD", "GBP", "07/09/2024", "08/09/2024");
        actual_all_conversions = db_test.findAllConversionRates("AUD", "GBP", "07/09/2024", "08/09/2024");

        assertEquals(expected_median, actual_median);
        assertEquals(expected_maximum, actual_maximum);
        assertEquals(expected_minimum, actual_minimum);
        assertEquals(expected_sd, actual_sd);
        assertEquals(expected_average, actual_average);
        assertEquals(expected_all_conversions, actual_all_conversions);
    }

    @Test
    void test_summary_no_currency() {
        double expected_median = 0;
        double expected_maximum = 0;
        double expected_minimum = 0;
        double expected_sd = 0;
        double expected_average = 0;
        List<Double> expected_all_conversions = new ArrayList<>();

        double actual_median = db_test.findMedian("AUD", "GBP", "07/09/2024", "08/09/2024");
        double actual_maximum = db_test.findMaximum("AUD", "GBP", "07/09/2024", "08/09/2024");
        double actual_minimum = db_test.findMinimum("AUD", "GBP", "07/09/2024", "08/09/2024");
        double actual_sd = db_test.findSD("AUD", "GBP", "07/09/2024", "08/09/2024");
        double actual_average = db_test.findAverageRateWithinDates("AUD", "GBP", "07/09/2024", "08/09/2024");
        ArrayList<Double> actual_all_conversions = db_test.findAllConversionRates("AUD", "GBP", "07/09/2024", "08/09/2024");

        assertEquals(expected_median, actual_median);
        assertEquals(expected_maximum, actual_maximum);
        assertEquals(expected_minimum, actual_minimum);
        assertEquals(expected_sd, actual_sd);
        assertEquals(expected_average, actual_average);
        assertEquals(expected_all_conversions, actual_all_conversions);
    }

    @Test
    void test_rate_change() {
        String expected_current = "-";
        String rate_current = db_test.findRateChange("AUD", "USD");
        assertEquals(expected_current, rate_current);

        db_test.insertRate("AUD", "USD", 1.1, "07/09/2024", 0);
        String expected_new = "I";
        String rate_new = db_test.findRateChange("AUD", "USD");
        assertEquals(expected_new, rate_new);

    }

    @Test
    void test_rate_change_up_down() {
        String expected_current = "-";
        String rate_current = db_test.findRateChange("AUD", "USD");
        assertEquals(expected_current, rate_current);

        db_test.insertRate("AUD", "USD", 1.1, "07/09/2024", 1);
        String expected_new = "I";
        String rate_new = db_test.findRateChange("AUD", "USD");
        assertEquals(expected_new, rate_new);


        db_test.insertRate("AUD", "USD", 0.69, "08/09/2024", 1);
        expected_new = "D";
        rate_new = db_test.findRateChange("AUD", "USD");
        System.out.println(db_test.findRateChange("AUD", "USD"));
        assertEquals(expected_new, rate_new);
    }

    @Test
    void test_rate_change_no_change() {
        String expected_current = "-";
        String rate_current = db_test.findRateChange("AUD", "USD");
        assertEquals(expected_current, rate_current);

        db_test.insertRate("AUD", "USD", 1.1, "07/09/2024", 0);
        String expected_new = "I";
        String rate_new = db_test.findRateChange("AUD", "USD");
        assertEquals(expected_new, rate_new);

        db_test.insertRate("AUD", "USD", 1.1, "09/09/2024", 0);
        expected_new = "-";
        rate_new = db_test.findRateChange("AUD", "USD");
        assertEquals(expected_new, rate_new);
    }

    @Test
    void test_fail_get_currency() {
        reset_tables();
        ObservableList<String> result = db_test.getCurrencies();
        assertNull(result);
    }

    @Test
    void test_fail_find_rate() {
        reset_tables();
        String from_test = "AUD";
        String to_test = "CAD";
        double result = db_test.findRate(from_test, to_test);
        assertEquals(0, result);
    }

    @Test
    void test_fail_find_rate_change() {
        reset_tables();
        String result = db_test.findRateChange("AUD", "USD");
        assertNull(result);
    }

    @Test
    void test_fail_summary() {
        reset_tables();
        double result = db_test.findAverageRateWithinDates("AUD", "USD", "06/09/2024", "06/09/2024");
        assertEquals(0, result);
        result = db_test.findMedian("AUD", "USD", "06/09/2024", "06/09/2024");
        assertEquals(0, result);
        result = db_test.findMinimum("AUD", "USD", "06/09/2024", "06/09/2024");
        assertEquals(0, result);
        result = db_test.findMaximum("AUD", "USD", "06/09/2024", "06/09/2024");
        assertEquals(0, result);
        result = db_test.findSD("AUD", "USD", "06/09/2024", "06/09/2024");
        assertEquals(0, result);
        List<Double> fail_conversion = db_test.findAllConversionRates("AUD", "USD", "06/09/2024", "06/09/2024");
        assertNull(fail_conversion);
    }

    @Test
    void test_fail_popular() {
        reset_tables();
        List<String> result = db_test.findPopular();
        assertNull(result);
    }

    @Test
    void test_fail_find_symbol() {
        reset_tables();
        String result = db_test.getCurrencySymbol("AUD");
        assertNull(result);
    }

    @Test
    void test_date_checker() {
        DateChecker test_date = new DateChecker();
        boolean expected = true;
        boolean actual  = test_date.isValidDate("12/01/2024");
        assertEquals(expected, actual);
    }

    @Test
    void test_date_checker_wrong_format() {
        DateChecker test_date = new DateChecker();
        boolean expected = false;
        boolean actual  = test_date.isValidDate("111/12/2024");
        assertEquals(expected, actual);
    }

    @Test
    void test_date_checker_13th_month() {
        DateChecker test_date = new DateChecker();
        boolean expected = false;
        boolean actual  = test_date.isValidDate("11/13/2024");
        assertEquals(expected, actual);
    }


    @Test
    void test_unable_connect() {
        SQLiteConnection con = new SQLiteConnection("jdbc:");
        con.connect();
        con.initializeTables();
    }

    @Test
    void test_initialise_tables_again() {
        SQLiteConnection con = new SQLiteConnection(test_url);
        con.initializeTables();

        String from_test = "AUD";
        String to_test = "USD";
        double expected_rate = 0.67;
        double result = db_test.findRate(from_test, to_test);

        assertNotNull(result);
        assertEquals(result, expected_rate);
    }

}
