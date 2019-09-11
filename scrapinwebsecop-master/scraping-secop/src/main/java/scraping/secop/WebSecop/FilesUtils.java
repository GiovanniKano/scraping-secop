package scraping.secop.WebSecop;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FilesUtils {

    private static final Logger LOG =  Logger.getLogger(FilesUtils.class);

    public List<String> leerArchivo(){
        List<String> codigos = new ArrayList<>();
        try {
            File archivoRelacion = new File("C:\\Users\\Giova\\Documents\\Codigos\\codigos.txt");
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
