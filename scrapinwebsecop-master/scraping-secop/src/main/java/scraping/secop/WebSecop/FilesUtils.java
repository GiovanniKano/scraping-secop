package scraping.secop.WebSecop;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FilesUtils {

    private static final Logger LOG =  Logger.getLogger(FilesUtils.class);

    public void unzip(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to "+newFile.getAbsolutePath());
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> leerArchivo(){
        List<String> codigos = new ArrayList<>();
        try {
            File archivoRelacion = new File("C:\\Users\\Ingenian Sotware\\Documents\\Codigos\\codigos.txt");
            BufferedReader lectura = new BufferedReader(new FileReader(archivoRelacion));
            String linea;
            while((linea = lectura.readLine()) != null) {
                codigos.add(linea);
            }
            lectura.close();
            return codigos;
        }
        catch(IOException ex) {
            LOG.error("Ocurrio un error abriendo el domingo: " + ex.getMessage());
            return null;
        }
    }

}
