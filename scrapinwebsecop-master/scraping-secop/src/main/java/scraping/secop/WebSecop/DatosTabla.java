package scraping.secop.WebSecop;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import scraping.secop.SecopVO.DatosTablaVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DatosTabla {

    private final static Logger LOG = Logger.getLogger(DatosTabla.class);
    private List<DatosTablaVO> listDatos = new ArrayList<DatosTablaVO>();
    private DatosTablaVO datos = new DatosTablaVO();

    public void goToLink(WebDriver driver, List<String> links){
        try {
            for (String x: links) {
                JavascriptExecutor execute = (JavascriptExecutor)driver;
                execute.executeScript("window.open('"+x+"')");
                Set<String> tab_handles = driver.getWindowHandles();
                int number_of_tabs = tab_handles.size();
                int new_tab_index = number_of_tabs-1;
                LOG.info(new_tab_index);
                driver.switchTo().window(tab_handles.toArray()[new_tab_index].toString());
                fillData(driver);
                driver.close();
                driver.switchTo().window(tab_handles.toArray()[0].toString());
            }
            LOG.info("Datos llenos son: " + listDatos.size());
        }
        catch (WebDriverException ex){
            LOG.error("Ocurrio un error abriendo una nueva ventana: " + ex.getMessage());
            driver.close();
        }
    }

    private void fillData(WebDriver driver){
        try{
            datos.setEnlace(driver.getCurrentUrl());
            datos.setDescripcion(driver.findElement(By.id("divDescriptionDiv_spnDescription")).getText());
            datos.setValorEstimado(driver.findElement(By.id("cbxPriceGen")).getText());
            listDatos.add(datos);
        }
        catch (WebDriverException ex){
            LOG.error("Ocurrio un error llenando los datos: " + ex.getMessage());
            throw new WebDriverException(ex.getMessage());
        }
    }
}
