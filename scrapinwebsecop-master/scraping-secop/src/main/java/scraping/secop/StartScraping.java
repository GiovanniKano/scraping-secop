package scraping.secop;

import org.apache.log4j.Logger;
import scraping.secop.WebSecop.ScrapingWebSecop;

public class StartScraping {

    private final static Logger LOG = Logger.getLogger(StartScraping.class);

    public static void main(String[] args) {
        try{
            ScrapingWebSecop web = new ScrapingWebSecop();
            web.startScrapinWeb();
        }
        catch (Exception ex){
            LOG.error("Ha ocurrido un error iniciando scraping" + ex.getMessage());
        }
    }

}
