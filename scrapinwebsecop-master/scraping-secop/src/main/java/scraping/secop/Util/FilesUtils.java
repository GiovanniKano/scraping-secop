package scraping.secop.Util;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import scraping.secop.SecopVO.ConfigPropertiesVO;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FilesUtils {

    private static final Logger LOG =  Logger.getLogger(FilesUtils.class);

    public List<String> leerArchivo(ConfigPropertiesVO config){
        List<String> codigos = new ArrayList<>();
        try {
            File archivoRelacion = new File(config.getCodePath());
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

    public File moveDocuments(String path){
        try{
            List<File> documents = this.getDocumentsFolder(path);
            File afile = null;
            File file = new File(UUID.randomUUID().toString());
            file.mkdirs();
            if (!file.exists()) {
                if (file.mkdir()) {
                    LOG.info("Directorio creado.");
                } else {
                    LOG.info("No se pudo crear el directorio.!");
                }
            }
            for (File archivo:documents) {
                 afile = new File(archivo.getAbsolutePath());
                if (afile.renameTo(new File(file.getAbsolutePath() + "\\" + afile.getName()))) {
                    LOG.info("Archivo movido");
                } else {
                    LOG.info("El archivo no se pudo mover");
                }
            }
            LOG.info("Ruta a la que se movió " + file.getAbsolutePath());
            return file;
        }catch(Exception ex){
            LOG.error("Oucrrio un error moviendo los documentos:" + ex.getMessage());
            return null;
        }
    }

    public List<File> getDocumentsFolder(String path){
        try{
            List<File> folder = Files.walk(Paths.get(path))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            return folder;
        }
        catch (IOException ex){
            LOG.error("Ocurrio un error abriendo carpeta" + ex.getMessage());
            return null;
        }
    }

    public long getSizeOfDirectory(String path){
        long size = FileUtils.sizeOfDirectory(new File(path));
        LOG.info("Tamaño de directorio = " + size);
        return size;
    }

}
