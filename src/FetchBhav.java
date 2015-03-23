import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Misaal on 17/03/2015.
 */
public class FetchBhav {

    private static final String BASE_LINK = "http://www.nseindia.com/content/historical/EQUITIES/";

    public static void main(String args[]){
        System.out.println("Please enter the first date in the format dd/mm/yyyy ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Date date1 = readDateInput(br);

        System.out.println("Please enter the second date in the format dd/mm/yyyy ");
        Date date2 = readDateInput(br);

        System.out.println("Please enter the stock symbol.");
        String stockSymbol = readStockSymbol(br);

        System.out.println("Downloading files ...");
        List<File> files = downloadAndUnzipFiles(date1, date2);
        CSVReader reader = new CSVReader();
        SQLiteHelper helper = new SQLiteHelper();

        System.out.println("Writing stock data to the database ...");
        for(File file : files){
            List<StockEntry> fileData = reader.read(file);
            helper.addStocks(fileData);
        }

        System.out.println("Fetching data for : " +stockSymbol);
        helper.printStockBySymbol(stockSymbol);
    }

    /**
     * Reads user input of date and returns a calendar object
     * @param br : BufferedReader
     * @return : Date
     */
    private static Date readDateInput(BufferedReader br) {
        String dateStr = null;
        try {
            dateStr = br.readLine();
        } catch (IOException e) {
            System.out.println("error in reading date input");
            e.printStackTrace();
        }
        Calendar cal = null;
        if(dateStr != null && !dateStr.isEmpty()){
            String[] dateParts = dateStr.split("/");
            cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, Integer.parseInt(dateParts[2]));
            cal.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[0]));
        }else{
            System.out.println("Please enter a date in the format dd/mm/yyyy");
        }

        return cal.getTime();
    }


    /**
     * Reads the stock symbol input by the user in the terminal
     * @param br : BufferedReader
     * @return : String
     */
    private static String readStockSymbol(BufferedReader br) {
        String stockSymbol = null;
        try {
            stockSymbol = br.readLine();
            br.close();
        } catch (IOException e) {
            System.out.println("error in reading date input");
            e.printStackTrace();
        }
        if(stockSymbol != null){
            stockSymbol.toUpperCase();
        }
        return stockSymbol;
    }


    /**
     * Uses the input dates to generate file names from all dates that lie between the 2 dates given. Uses these dynamically
     * generated file names to download bhav copy files from the NSE web site. The files are unzipped and those file are
     * referenced in the returned list
     * @param first : earlier date
     * @param second : later date
     * @return : List<File> which holds the .csv bhav copy files for th dates that lie between the input
     * @throws IOException
     */
    public static List<File> downloadAndUnzipFiles(Date first, Date second)  {
        // create a directory in the user home directory to hold all the files
        File downloadDirectory = createDownloadDirectory();

        // get all dates between the two input dates including the input dates
        List<Date> dates = iterateBetweenDates(first, second);
        List<File> files = new ArrayList<File>();
        for(Date date: dates){
            // format date into a string with format dd/MMM/yyyy and then split it by "/"
            String[] dateParts = getDateParts(date);
            // the file name is based on the date it corresponds to and so the file name can be dynamically
            // constructed for any date and then downloaded
            String fileName = createFileName(dateParts);
            String link = createLinkFromDate(dateParts, fileName);

            // the base link from where the different bhav copy files can be accessed
            String fullLink = BASE_LINK + link;

            File file = downloadAndUnzipFromUrl(fullLink, downloadDirectory, fileName);
            if(file != null){
                files.add(file);
            }
        }
        return files;
    }


    /**
     *
     * @param urlStr
     * @param directory
     * @param fileName
     * @return
     * @throws IOException
     */
    public static File downloadAndUnzipFromUrl(String urlStr, File directory, String fileName){
        File zippedFile = null;
        File unzippedFile = null;
        try{
            // the url to download the file from
            URL url = new URL(urlStr);
            // connect to url
            URLConnection uc = url.openConnection();
            // set this property to bypass the restriction set by the nse website that only allows downloads from browsers
            uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) \" +\n" +
                    "                \"Gecko/20100316 Firefox/3.6.2");

            String downloadFilePath = directory.getAbsolutePath() + "/"+fileName;
            zippedFile = new File(downloadFilePath);
            // copies the data from the input stream to the file
            Files.copy(uc.getInputStream(), zippedFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            MyZip unZipper= new MyZip();
            // unzip file
            unzippedFile = unZipper.unzipFile(zippedFile.getPath(), directory.getAbsolutePath());
        }catch (IOException e){
            // DO NOTHING
        }finally {
            if(zippedFile != null){
                zippedFile.delete();
            }
        }
        // delete downloaded zip file
        return unzippedFile;
    }


    /**
     * Creates a directory in the External Storage of the device which holds all the files downloaded
     * @return
     */
    private static File createDownloadDirectory() {
        String folderName = "Bhavcopy Files";
        File f = new File(System.getProperty("user.home"),
                folderName);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

    /**
     *
     * @param date
     * @return
     */
    private static String[] getDateParts(Date date){
        DateFormat fr = new SimpleDateFormat("dd/MMM/yyyy");
        // get the date formatted to string - 12/Mar/1990
        String dateStr = fr.format(date);
        // split the date string using '/' to get the dd, MMM, and yyyy parts separately
        String[] dateParts = dateStr.split("/");
        dateParts[1] = dateParts[1].toUpperCase();
        return dateParts;
    }


    /**
     *
     * @param dateParts
     * @param fileName
     * @return
     */
    private static String createLinkFromDate(String[] dateParts, String fileName){
        StringBuilder builder = new StringBuilder();
        builder.append(dateParts[2] + "/"); // add year - "2015/"
        builder.append(dateParts[1].toUpperCase()+ "/"); // add month - "MAR/"
        builder.append(fileName); //add file name
        return builder.toString();
    }


    /**
     * dynamically forms the name of the file to be downloaded. The file name looks like this -
     * cm23MAR2015bhav.csv.zip
     * @param dateParts
     * @return
     */
    private static String createFileName(String[] dateParts){
        StringBuilder builder = new StringBuilder();
        builder.append("cm");
        builder.append(dateParts[0]+dateParts[1]+dateParts[2]);
        builder.append("bhav.csv.zip");
        return builder.toString();
    }


    /**
     *
     * @param startDate
     * @param endDate
     * @return
     */
    private static List<Date> iterateBetweenDates(Date startDate, Date endDate) {
        List<Date> dates = new ArrayList<Date>();
        Calendar startCalender = Calendar.getInstance();
        startCalender.setTime(startDate);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);

        for(; startCalender.compareTo(endCalendar)<=0; startCalender.add(Calendar.DATE, 1)) {
            dates.add(startCalender.getTime());
        }
        return dates;
    }
}


// NEED TO STORE SYMBOL, TIMESTAMP AND CLOSE PRICE
