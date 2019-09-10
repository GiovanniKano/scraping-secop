package scraping.secop;

import org.apache.log4j.Logger;
import scraping.secop.WebSecop.FilesUtils;
import scraping.secop.WebSecop.ScrapingWebSecop;

import java.util.List;

public class StartScraping {

    private final static Logger LOG = Logger.getLogger(StartScraping.class);

    public static void main(String[] args) {
        try{
            ScrapingWebSecop web = new ScrapingWebSecop();
            List<String> codigos = new FilesUtils().leerArchivo();
            for(int i = 0; i < codigos.size(); i++){
                LOG.info(codigos.get(i));
                web.startScrapinWeb(codigos.get(i));
            }
        }
        catch (Exception ex){
            LOG.error("Ha ocurrido un error iniciando scraping" + ex.getMessage());
        }
    }

}
