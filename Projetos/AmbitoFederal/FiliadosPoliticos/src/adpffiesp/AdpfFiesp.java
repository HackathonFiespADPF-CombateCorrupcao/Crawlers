package adpffiesp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import static jdk.nashorn.internal.objects.ArrayBufferView.buffer;

public class AdpfFiesp {

    private static final String OUTPUTZIP = "outputzip/aplic/sead/lista_filiados/uf/"; 
    
    public static void main(String[] args) {

        try {
            String url_ = "http://agencia.tse.jus.br/estatistica/sead/eleitorado/filiados/uf/filiados_pmdb_pr.zip";
            
            URL url = new URL(url_);
            
            byte[] data = download(url);
            
            unZipIt(data);
            
            String test = null;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

//        readCsv();
        readFiles();
        

    }
    
    // https://stackoverflow.com/questions/238824/getting-a-file-from-an-http-request-in-java
    public static byte[] download(URL url) throws IOException {
        URLConnection uc = url.openConnection();
        int len = uc.getContentLength();
        InputStream is = new BufferedInputStream(uc.getInputStream());
        try {
            byte[] data = new byte[len];
            int offset = 0;
            while (offset < len) {
                int read = is.read(data, offset, data.length - offset);
                if (read < 0) {
                    break;
                }
              offset += read;
            }
            if (offset < len) {
                throw new IOException(
                    String.format("Read %d bytes; expected %d", offset, len));
            }
            return data;
        } finally {
            is.close();
        }
    }

    
    public static void readFiles(){
//        String ROOT_FILE_PATH="/";
        File f = new File(OUTPUTZIP);
        File[] allSubFiles =f .listFiles();
        for (File file : allSubFiles) {
                if(file.isDirectory())
            {
                System.out.println(file.getAbsolutePath()+" is directory");
                //Steps for directory
            }
            else
            {
                System.out.println(file.getName());
                readCsv(file.getName());
                
                //steps for files
            }
        }
    }


    
    public static void readCsv(String filename) {

        String csvFile = OUTPUTZIP + filename;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] country = line.split(cvsSplitBy);

                System.out.println("Country [code= " + country[0] + " , name=" + country[1] + "]");

            }

        } catch (FileNotFoundException e) {
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

    }

    
    // https://www.mkyong.com/8787878java/how-to-decompress-files-from-a-zip-file/
     /**
     * Unzip it
     * @param zipFile input zip file
     * @param output zip file output folder
     */
    public static void unZipIt(byte[] bytes){
        String OUTPUT_FOLDER = "./outputzip";
        byte[] buffer = new byte[1024];

        try{

            //create output directory is not exists
            File folder = new File("zipfolder");
            if(!folder.exists()){
                    folder.mkdir();
            }

            //get the zip file content
            ZipInputStream zis =
                    new ZipInputStream(new ByteArrayInputStream(bytes));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while(ze!=null){

               String fileName = ze.getName();
               
               if (fileName.contains("filiados_")){
                    File newFile = new File(OUTPUT_FOLDER + File.separator + fileName);

                    System.out.println("file unzip : "+ newFile.getAbsoluteFile());

                     //create all non exists folders
                     //else you will hit FileNotFoundException for compressed folder
                     new File(newFile.getParent()).mkdirs();

                     FileOutputStream fos = new FileOutputStream(newFile);

                     int len;
                     while ((len = zis.read(buffer)) > 0) {
                         fos.write(buffer, 0, len);
                     }

                     fos.close();
                     
               }
               ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            System.out.println("Done");

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
}
