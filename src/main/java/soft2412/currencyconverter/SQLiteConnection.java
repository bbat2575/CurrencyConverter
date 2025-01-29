package soft2412.currencyconverter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteConnection {
//    Source: https://www.javatpoint.com/java-sqlite
    private static String urlRates;

    public SQLiteConnection(String urlRates) {
        this.urlRates = urlRates;
    }

    public static void connect() {
        Connection conn1 = null;
        try {
            // Create a connection to the SQL database
            conn1 = DriverManager.getConnection(urlRates);

            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn1 != null) {
                    conn1.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static void initializeTables() {
        // SQL statement to create a rates table to store exchange rates between currencies
        String sqlTableRates = "CREATE TABLE IF NOT EXISTS rates (" +
                "fromCurrency CHAR(3), " +
                "toCurrency CHAR(3), " +
                "rate DOUBLE NOT NULL, " +
                "date DATE NOT NULL, " +
                "time INTEGER NOT NULL, " +
                "rateChange CHAR(1), " +
                "PRIMARY KEY(fromCurrency, date, time)" +
                ");";

        // SQL statement to create a popular table to store the 4 most popular currencies
        String sqlTablePopular = "CREATE TABLE IF NOT EXISTS popular (" +
                "rank INTEGER, " +
                "currency CHAR(3) NOT NULL, " +
                "PRIMARY KEY(rank)" +
                ");";

        // SQL statment to create a symbols table to store each currency and its symbol
        String sqlTableSymbols = "CREATE TABLE IF NOT EXISTS symbols (" +
                "currency CHAR(3), " +
                "symbol CHAR(3) NOT NULL, " +
                "PRIMARY KEY(currency)" +
                ");";

        try{
            Connection conn1 = DriverManager.getConnection(urlRates);
            Statement stmt1 = conn1.createStatement();
            stmt1.execute(sqlTableRates);
            stmt1.execute(sqlTablePopular);
            stmt1.execute(sqlTableSymbols);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
