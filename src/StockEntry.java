/**
 * Created by Misaal on 24/03/2015.
 */
public class StockEntry {


    private final String stockSymbol;
    private final double closePrice;
    private final String timeStamp;


    /**
     * constructor
     * @param symbol
     * @param closePrice
     * @param timestamp
     */
    public StockEntry(String symbol, double closePrice, String timestamp){
        this.stockSymbol = symbol;
        this.closePrice = closePrice;
        this.timeStamp = timestamp;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public double getClosePrice() {
        return closePrice;
    }

    public String getTimeStamp() {
        return timeStamp;
    }


}
