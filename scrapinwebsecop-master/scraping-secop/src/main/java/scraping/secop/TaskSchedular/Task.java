package scraping.secop.TaskSchedular;

import org.apache.log4j.Logger;
import scraping.secop.Util.FilesUtils;
import scraping.secop.WebSecop.ScrapingWebSecop;
import java.util.List;

public class Task implements Runnable{

    private static final Logger LOG = Logger.getLogger(Task.class);

    @Override
    public void run() {
        try{
            ScrapingWebSecop web = new ScrapingWebSecop();
            List<String> codigos = new FilesUtils().leerArchivo();
            for(int i = 0; i < codigos.size(); i++){
                LOG.info(codigos.get(i));
                web.startScrapinWeb(codigos.get(i));
            }
        }
        catch (Exception ex){
            LOG.error("Ocurrio un error corriendo la tarea: " + ex.getMessage());
        }
    }
}
