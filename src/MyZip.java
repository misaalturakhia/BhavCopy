import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MyZip {

    public File unzipFile(String zippedFilePath, String downloadDirectoryPath){
        File file;
        FileInputStream fis = null;
        ZipInputStream zipIs = null;
        ZipEntry zEntry = null;
        String opFilePath = null;
        try {
            fis = new FileInputStream(zippedFilePath);
            zipIs = new ZipInputStream(new BufferedInputStream(fis));
            while((zEntry = zipIs.getNextEntry()) != null){
                try{
                    byte[] tmp = new byte[4*1024];
                    FileOutputStream fos = null;
                    opFilePath = downloadDirectoryPath+"/"+zEntry.getName();
                    System.out.println("Extracting file to "+opFilePath);
                    fos = new FileOutputStream(opFilePath);
                    int size = 0;
                    while((size = zipIs.read(tmp)) != -1){
                        fos.write(tmp, 0 , size);
                    }
                    fos.flush();
                    fos.close();
                } catch(Exception ex){

                }
            }
            zipIs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new File(opFilePath);
    }

}
