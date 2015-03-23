import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SQLiteHelper {

    private static final String SQLITE_CLASS_NAME = "org.sqlite.JDBC";
    private static final String DB_URL = "jdbc:sqlite:stocks.db";

    /**
     * Default Constructor
     */
    public SQLiteHelper(){
        initDB();
    }


    /**
     * Creates the database if it does not exist and also creates the table 'stocks' if it doesn't exist
     */
    public void initDB(){
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName(SQLITE_CLASS_NAME);
            connection = DriverManager.getConnection(DB_URL);
            statement = connection.createStatement();
            statement.executeUpdate(StockEntryContract.CREATE_TABLE_QUERY);
            statement.close();
            connection.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            e.printStackTrace();
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }


    /**
     * Uses the data from the input List<StockEntry> and inserts rows into the table 'stocks'
     * @param stockData : List<StockEntry> of data.
     */
    public void addStocks(List<StockEntry> stockData){
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName(SQLITE_CLASS_NAME);
            connection = DriverManager.getConnection(DB_URL);
            statement = connection.createStatement();
            for(StockEntry stock : stockData){
                String symbol = stock.getStockSymbol();
                double closePrice = stock.getClosePrice();
                String timeStamp = stock.getTimeStamp();
                String query = "INSERT INTO "+ StockEntryContract.TABLE_NAME +
                        " ("+ StockEntryContract.COLUMN_SYMBOL+","+ StockEntryContract.COLUMN_CLOSE_PRICE +","+ StockEntryContract.COLUMN_TIMESTAMP+
                        ") VALUES ('"+symbol+"',"+closePrice+",'"+timeStamp+"')";
                statement.executeUpdate(query);
            }
            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Reads the database for all entries that have 'symbol' equal to the input stock symbol
     * @param symbol :Stock symbol
     */
    public void printStockBySymbol(final String symbol){
        Connection c = null;
        Statement stmt = null;
        boolean isResult = false;
        try {
            Class.forName(SQLITE_CLASS_NAME);
            c = DriverManager.getConnection(DB_URL);
            c.setAutoCommit(false);

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM "+ StockEntryContract.TABLE_NAME+
                    " WHERE "+ StockEntryContract.COLUMN_SYMBOL+" = '"+symbol+"';" );
            System.out.println( "symbol = " + symbol.toUpperCase() );
            // a map that keeps a record of the value per timestamp (key)
            Map<String, Double> map = new HashMap<String, Double>();
            while ( rs.next() ) {
                double closePrice = rs.getDouble(StockEntryContract.COLUMN_CLOSE_PRICE);
                String  timeStamp = rs.getString(StockEntryContract.COLUMN_TIMESTAMP);
                map.put(timeStamp, closePrice);
                isResult = true;
            }
            printValues(map);
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        if(isResult){
            System.out.println("Operation done successfully");
        }else{
            System.out.println("Couldn't find any matches for the input stock symbol : "+symbol);
        }
    }

    /**
     *  Used a map to make sure that it doesn't print duplicate entries multiple times
     * @param map :
     */
    private void printValues(Map<String, Double> map) {
        Set<String> keySet = map.keySet();
        for(String timeStamp : keySet){
            System.out.print("timestamp = " + timeStamp +" ");
            double closePrice = map.get(timeStamp);
            System.out.print("close price = " + closePrice + " ");
            System.out.println();
        }
    }


    class StockEntryContract {
        private static final String TABLE_NAME = "stocks";
        private static final String COLUMN_ID = "id";
        private static final String COLUMN_SYMBOL ="stock_symbol";
        private static final String COLUMN_CLOSE_PRICE = "close_price";
        private static final String COLUMN_TIMESTAMP = "timestamp";

        private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME +" ("+
                COLUMN_ID+"        INTEGER     PRIMARY KEY AUTOINCREMENT NOT NULL," +
                COLUMN_SYMBOL+"    TEXT        NOT NULL, " +
                COLUMN_CLOSE_PRICE +"     REAL        NOT NULL, " +
                COLUMN_TIMESTAMP+" TEXT )";

    }
}