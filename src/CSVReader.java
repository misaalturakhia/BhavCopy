import java.io.*;
import java.util.ArrayList;
import java.util.List;


/** Provides a method that reads the input CSV file, extracts the required data and stores it in a List<StockEntry> which is
 * returned
 * Created by Misaal on 24/03/2015.
 */
public class CSVReader {

    public static final String SEPARATOR = ",";
    public static final int NO_OF_COLUMNS = 3;
    public static final int SYMBOL_COLUMN_INDEX = 0;
    public static final int CLOSE_PRICE_COLUMN_INDEX = 5;
    public static final int TIMESTAMP_COLUMN_INDEX = 10;


    /**
     * Default Constructor
     */
    public CSVReader(){}


    /**
     * Extracts stock data from the csv file
     * @param file
     * @return
     */
    public List<StockEntry> read(File file){
        if(!isCorrectFormat(file)){
            throw new IllegalArgumentException("File :"+file.getName()+", is not a csv file! ");
        }
        List<StockEntry> stockData = new ArrayList<StockEntry>();
        BufferedReader br = null;
        String line = "";
        try {

            br = new BufferedReader(new FileReader(file));
            br.readLine(); // skip first line which contains column headers
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] lineArray = line.split(SEPARATOR);
                // initialize array that will hold details of one stock
                int i = 0;
                // put data into stockArray
                String symbol = lineArray[SYMBOL_COLUMN_INDEX];
                String closePrice = lineArray[CLOSE_PRICE_COLUMN_INDEX];
                String timeStamp = lineArray[TIMESTAMP_COLUMN_INDEX];
                // put stockArray into stockData
                stockData.add(new StockEntry(symbol, Double.parseDouble(closePrice), timeStamp));
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found ! : " +file.getAbsolutePath());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return stockData;
    }


    /** Checks if the file extension is .csv
     *
     * @param file
     * @return
     */
    private boolean isCorrectFormat(File file) {
        String fileName = file.getName();
        String extension = extractExtension(fileName);
        return extension.equals("csv");
    }


    /** Extracts the extension from the input fileName
     *
     * @param fileName
     * @return : extension string. eg: 'txt' from fileName 'abc.txt'
     */
    private String extractExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        String extension = fileName.substring(lastIndexOf+1);
        return extension;
    }

}
