package soft2412.currencyconverter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
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
import java.util.ArrayList;

public class SQLiteOperations {

    private String db_path;

    public SQLiteOperations(String db_path) {
        this.db_path = db_path;
    }

    private Connection connect() {
        // SQLite connection string
        String url = db_path;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // Converts Java Date to SQL date to insert into DB
    public Date convertJavaDateToSQL(String date) {
        // Define date formatter to match string format - dd/MM/yyyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);

        // convert to java.sql.Date
        return Date.valueOf(localDate);
    }

    // Set the timezone, get the local time and adjust the format
    public int getTimeInSeconds() {
        ZoneId zoneId = ZoneId.of("Australia/Sydney");
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        LocalTime localTime = zonedDateTime.toLocalTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        return localTime.toSecondOfDay();
    }

    // Returns a list of all available currencies
    public ObservableList<String> getCurrencies() {
        ObservableList<String> currencies = FXCollections.observableArrayList();

        String sql = "SELECT DISTINCT fromCurrency AS Currency FROM rates";

        // Use try-with-resources block to automatically close resources when done
        try (Connection conn = this.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet result = preparedStatement.executeQuery()) {

            // loop through results -> table
            while (result.next()) {
                currencies.add(result.getString(1));
            }

            return currencies;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Currencies not found");
        return null;
    }

    // Insert rate into DB at specified date
    public void insertRate(String from, String to, Double rate, String date, int firstAdd) {
        String sql = "INSERT INTO rates VALUES(?,?,?,?,?,?)";

        // Use try-with-resources block to automatically close resources when done
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, from);
            pstmt.setString(2, to);
            pstmt.setDouble(3, rate);

            // Convert Java date to SQL date
            Date sqlDate = convertJavaDateToSQL(date);
            pstmt.setDate(4, sqlDate);

            // Get current time and set it
            int sqlTime = getTimeInSeconds();
            pstmt.setInt(5, sqlTime);

            // Determine if rate increased or decreased
            if(findRate(from, to) == 0.0)
                pstmt.setString(6, "-");
            else if (findRate(from, to) < rate)
                pstmt.setString(6, "I");
            else if (findRate(from, to) > rate)
                pstmt.setString(6, "D");
            else
                pstmt.setString(6, "-");

            // Execute the insert
            pstmt.executeUpdate();

            // If it's the first add, insert the inverse rate after a delay
            if (firstAdd == 1) {
                try {
                    Thread.sleep(200); // Delay for composite primary key
                    insertRate(to, from, Double.parseDouble(String.format("%.2f", 1 / rate)), date, 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Get LATEST added rate for currency pair
    public double findRate(String from, String to) {
        String sql = "SELECT rate FROM rates WHERE fromCurrency = ? AND toCurrency = ? ORDER BY date + time DESC LIMIT 1";

        try (Connection conn = this.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, from);
            preparedStatement.setString(2, to);

            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                return result.getDouble("rate");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Rate not found");
        return 0;
    }

    // Get LATEST rate change for currency pair
    public String findRateChange(String from, String to) {
        String sql = "SELECT rateChange FROM rates WHERE fromCurrency = ? AND toCurrency = ? ORDER BY date + time DESC LIMIT 1";

        try (Connection conn = this.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, from);
            preparedStatement.setString(2, to);

            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                return result.getString("rateChange");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Rate Change not found");
        return null;
    }

    // Creates a list of rates for currency pair from start to end dates
    public ArrayList<Double> findAllConversionRates(String from, String to, String startDate, String endDate) {
        ArrayList<Double> ratesWithinDates = new ArrayList<>();

        String sql = "SELECT rate FROM rates WHERE fromCurrency = ? AND toCurrency = ? AND date BETWEEN ? AND ?";
        try (Connection conn = this.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, from);
            preparedStatement.setString(2, to);

            Date sqlDateStart = convertJavaDateToSQL(startDate);
            Date sqlDateEnd = convertJavaDateToSQL(endDate);
            preparedStatement.setDate(3, sqlDateStart);
            preparedStatement.setDate(4, sqlDateEnd);

            ResultSet result = preparedStatement.executeQuery();

            // loop through results -> table
            while (result.next()) {
                ratesWithinDates.add(result.getDouble("rate"));
            }

            return ratesWithinDates;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Rates not found");
        return null;
    }

    // Get the symbol for some currency
    public String getCurrencySymbol(String currency) {
        // SQL Query
        String sql = "SELECT symbol FROM symbols WHERE currency = ?";

        try (Connection conn = this.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, currency);

            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                return result.getString("symbol");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Currency not found");
        return null;
    }

    // Finds median rate of currency pair between start and end dates
    public double findMedian(String from, String to, String startDate, String endDate) {
        String sql = "SELECT AVG(rate) AS median FROM (" +
                " SELECT rate FROM rates" +
                " WHERE fromCurrency = ? AND toCurrency = ? AND date BETWEEN ? AND ?" +
                " ORDER BY rate" +
                " LIMIT 2 - (SELECT COUNT(*) FROM rates WHERE fromCurrency = ? AND toCurrency = ? AND date BETWEEN ? AND ?) % 2 " +
                " OFFSET (SELECT (COUNT(*) - 1) / 2 FROM rates WHERE fromCurrency = ? AND toCurrency = ? AND date BETWEEN ? AND ?)" +
                ")";
        try (Connection conn = this.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, from);
            preparedStatement.setString(2, to);

            Date sqlDateStart = convertJavaDateToSQL(startDate);
            Date sqlDateEnd = convertJavaDateToSQL(endDate);
            preparedStatement.setDate(3, sqlDateStart);
            preparedStatement.setDate(4, sqlDateEnd);

            preparedStatement.setString(5, from);
            preparedStatement.setString(6, to);
            preparedStatement.setDate(7, sqlDateStart);
            preparedStatement.setDate(8, sqlDateEnd);

            preparedStatement.setString(9, from);
            preparedStatement.setString(10, to);
            preparedStatement.setDate(11, sqlDateStart);
            preparedStatement.setDate(12, sqlDateEnd);

            ResultSet result = preparedStatement.executeQuery();

            return result.getDouble("median");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Rate not found");
        return 0;
    }

    // Finds maximum rate of currency pair between start and end dates
    public double findMaximum(String from, String to, String startDate, String endDate) {
        String sql = "SELECT round(MAX(rate), 2) AS rate FROM rates WHERE fromCurrency = ? AND toCurrency = ? AND date BETWEEN ? AND ?";
        try (Connection conn = this.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, from);
            preparedStatement.setString(2, to);

            Date sqlDateStart = convertJavaDateToSQL(startDate);
            Date sqlDateEnd = convertJavaDateToSQL(endDate);
            preparedStatement.setDate(3, sqlDateStart);
            preparedStatement.setDate(4, sqlDateEnd);

            ResultSet result = preparedStatement.executeQuery();

            return result.getDouble("rate");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Rate not found");
        return 0;
    }

    // Finds minimum rate of currency pair between start and end dates
    public double findMinimum(String from, String to, String startDate, String endDate) {
        String sql = "SELECT round(MIN(rate), 2) AS rate FROM rates WHERE fromCurrency = ? AND toCurrency = ? AND date BETWEEN ? AND ?";
        try (Connection conn = this.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, from);
            preparedStatement.setString(2, to);

            Date sqlDateStart = convertJavaDateToSQL(startDate);
            Date sqlDateEnd = convertJavaDateToSQL(endDate);
            preparedStatement.setDate(3, sqlDateStart);
            preparedStatement.setDate(4, sqlDateEnd);

            ResultSet result = preparedStatement.executeQuery();

            return result.getDouble("rate");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Rate not found");
        return 0;
    }

    // Finds standard deviation of rate of currency pair between start and end dates
    public double findSD(String from, String to, String startDate, String endDate) {
        String sql = "SELECT round(STDEV(rate), 2) AS rate FROM rates WHERE fromCurrency = ? AND toCurrency = ? AND date BETWEEN ? AND ?";
        try (Connection conn = this.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, from);
            preparedStatement.setString(2, to);

            Date sqlDateStart = convertJavaDateToSQL(startDate);
            Date sqlDateEnd = convertJavaDateToSQL(endDate);
            preparedStatement.setDate(3, sqlDateStart);
            preparedStatement.setDate(4, sqlDateEnd);

            ResultSet result = preparedStatement.executeQuery();

            return result.getDouble("rate");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Rate not found");
        return 0;
    }

    // Finds average rate of currency pair between start and end dates
    public double findAverageRateWithinDates(String from, String to, String startDate, String endDate) {
        String sql = "SELECT round(AVG(rate), 2) AS rate FROM rates WHERE fromCurrency = ? AND toCurrency = ? AND date BETWEEN ? AND ?";
        try (Connection conn = this.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, from);
            preparedStatement.setString(2, to);

            Date sqlDateStart = convertJavaDateToSQL(startDate);
            Date sqlDateEnd = convertJavaDateToSQL(endDate);
            preparedStatement.setDate(3, sqlDateStart);
            preparedStatement.setDate(4, sqlDateEnd);

            ResultSet result = preparedStatement.executeQuery();

            return result.getDouble("rate");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Rate not found");
        return 0;
    }

    // Set popular currencies
    public void setPopular(String currency1, String currency2, String currency3, String currency4) {
        // Clear the table first
        String sql = "DELETE FROM popular;";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Update popular currencies
        sql = "INSERT INTO popular VALUES(?,?)";
        String[] currencies = {currency1, currency2, currency3, currency4};

        for (int i = 1; i < 5; i++) {
            try (Connection conn = this.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, i);
                pstmt.setString(2, currencies[i - 1]);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // Find popular currencies
    public ArrayList<String> findPopular() {
        ArrayList<String> popularCurrencies = new ArrayList<>();
        String sql = "SELECT currency FROM popular";

        // Use try-with-resources block to automatically close resources when done
        try (Connection conn = this.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet result = preparedStatement.executeQuery()) {

            while (result.next()) {
                popularCurrencies.add(result.getString("currency"));
            }

            return popularCurrencies;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}