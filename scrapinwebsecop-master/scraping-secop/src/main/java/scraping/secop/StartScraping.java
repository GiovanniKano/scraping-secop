package scraping.secop;

import org.apache.log4j.Logger;
import scraping.secop.TaskSchedular.Task;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StartScraping {

    private final static Logger LOG = Logger.getLogger(StartScraping.class);

    public static void main(String[] args) {
        try{
            LOG.info("Iniciando scraping:" + new Date());
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(new Task(), 0, 30, TimeUnit.MINUTES);
        }
        catch (Exception ex){
            LOG.error("Ha ocurrido un error iniciando scraping" + ex.getMessage());
        }
    }

}
