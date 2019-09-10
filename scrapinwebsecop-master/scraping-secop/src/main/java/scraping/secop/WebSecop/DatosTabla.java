package scraping.secop.WebSecop;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import scraping.secop.SecopVO.Constantes;
import scraping.secop.SecopVO.DatosTablaVO;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DatosTabla {

    private final static Logger LOG = Logger.getLogger(DatosTabla.class);
    private List<DatosTablaVO> listDatos = new ArrayList<DatosTablaVO>();


    public void goToLink(WebDriver driver, List<String> links, List<String> nombreEntidad){
        try {
            int contador = 0;
            for (String x: links) {
                JavascriptExecutor execute = (JavascriptExecutor)driver;
                execute.executeScript("window.open('"+x+"')");
                Set<String> tab_handles = driver.getWindowHandles();
                int number_of_tabs = tab_handles.size();
                int new_tab_index = number_of_tabs-1;
                driver.switchTo().window(tab_handles.toArray()[new_tab_index].toString());
                fillData(driver, nombreEntidad, contador);
                contador++;
                descargarDocumentos(driver);
                driver.close();
                driver.switchTo().window(tab_handles.toArray()[0].toString());
            }
            new SendEmail().email(listDatos);
            driver.close();
        }
        catch (WebDriverException ex){
            LOG.error("Ocurrio un error abriendo una nueva ventana: " + ex.getMessage());
            driver.close();
        }
    }

    private void fillData(WebDriver driver, List<String> nombresEntidad, int contador){
        try{
            DatosTablaVO datos = new DatosTablaVO();
            datos.setNombreEntidad(nombresEntidad.get(contador));
            datos.setEnlace(driver.getCurrentUrl());
            if(elementeExist("cbxPriceGen", driver)){
                String valor = driver.findElement(By.id("cbxPriceGen")).getText();
                datos.setValorEstimado(valor);
            }else{
                datos.setValorEstimado("");
            }
            List<String> codigos = new ArrayList<>();
            if(elementeExist("gridListMultipleNodeRegion_0_tbl", driver)){
                WebElement tabla = driver.findElement(By.id("gridListMultipleNodeRegion_0_tbl"));
                List<WebElement> filas = tabla.findElements(By.xpath("//table[@id='gridListMultipleNodeRegion_0_tbl']/tbody/tr[@class='gridLineLight' or @class='gridLineDark']"));
                LOG.info("Filas traidas." + filas.size());
                for(int i = 0; i < filas.size(); i++){
                    codigos.add(filas.get(i).findElement(By.className("VortalTagSpan")).getText());
                }
                datos.setListaCodigosUBSPC(codigos);
            }else{
                codigos.add("");
                datos.setListaCodigosUBSPC(codigos);
            }

            datos.setDescripcion(driver.findElement(By.id("divDescriptionDiv_spnDescription")).getText());
            listDatos.add(datos);
        }
        catch (WebDriverException ex){
            LOG.error("Ocurrio un error llenando los datos: " + ex.getMessage());
            throw new WebDriverException(ex.getMessage());
        }
    }

    private void descargarDocumentos(WebDriver driver){
        try{
            JavascriptExecutor js = (JavascriptExecutor)driver;
            js.executeScript(Constantes.SUPERPOSICION_NO_PERMANENTE, driver.findElement(By.xpath("//*[@id='mprContractNoticeMapper']/a[4]")));
            WebDriverWait wait = new WebDriverWait(driver, 20);
            if(elementeExist("grdGridDocumentList_tbl", driver)){
                WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='grdGridDocumentList_tbl']")));
                List<WebElement> filas = table.findElements(By.xpath("//table[@id='grdGridDocumentList_tbl']/tbody/tr[contains(@class,'gridLineLight') or contains(@class,'gridLineDark')]"));
                LOG.info("Tamaño de filas" + filas.size());
                for(int x = 0; x < filas.size(); x++){
                    if(x > 5){
                        JavascriptExecutor jaExecu = (JavascriptExecutor) driver;
                        jaExecu.executeScript(Constantes.SUPERPOSICION_NO_PERMANENTE, driver.findElement(By.id("grdGridDocumentList_Paginator_goToPage_Next")));
                        WebDriverWait wait1 = new WebDriverWait(driver, Constantes.Timeout);
                        WebElement fila = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.id("tdColumnDocumentNameP2Gen_spnDocumentName_"+x)));
                        String nombreDocumento = fila.getText();
                        this.clickMore(nombreDocumento, driver, x);
                    }else if(x > 10){
                        JavascriptExecutor jaExecu = (JavascriptExecutor) driver;
                        jaExecu.executeScript(Constantes.SUPERPOSICION_NO_PERMANENTE, driver.findElement(By.id("grdGridDocumentList_Paginator_goToPage_Next")));
                        WebDriverWait wait1 = new WebDriverWait(driver, Constantes.Timeout);
                        WebElement fila = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.id("tdColumnDocumentNameP2Gen_spnDocumentName_"+x)));
                        String nombreDocumento = fila.getText();
                        this.clickMore(nombreDocumento, driver, x);
                    }else {
                        WebElement fila = driver.findElement(By.id("tdColumnDocumentNameP2Gen_spnDocumentName_"+x));
                        String nombreDocumento = fila.getText();
                        LOG.info("Nombre documento: " + nombreDocumento);
                        this.clickMore(nombreDocumento, driver, x);
                    }
                }
            }
        }
        catch (WebDriverException ex){
            LOG.info("Ocurrio un error descargando los documentos: " + ex.getMessage());
        }
    }

    private void clickMore(String nombreDocumento, WebDriver driver, int x){
        if(nombreDocumento.contains("ESTUDIOS PREVIOS") || nombreDocumento.contains("ANEXO OPERADOR COTIZACION")) {
            WebElement element = new WebDriverWait(driver, 20).until(ExpectedConditions.visibilityOfElementLocated(By.id("lnkDetailLinkP3Gen_"+x)));
            JavascriptExecutor js = (JavascriptExecutor)driver;
            js.executeScript(Constantes.SUPERPOSICION_NO_PERMANENTE, element);
            Set<String> tab_handles = driver.getWindowHandles();
            int number_of_tabs = tab_handles.size();
            int new_tab_index = number_of_tabs-1;
            driver.switchTo().window(tab_handles.toArray()[new_tab_index].toString());
            WebDriverWait wait = new WebDriverWait(driver, Constantes.TimeoutShort);
            WebElement element1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tdMTC1_tbToolBar_btnDownloadDocument")));
            element1.click();
            driver.switchTo().window(tab_handles.toArray()[1].toString());
        }
    }

    private boolean elementeExist(String element, WebDriver driver){
        try{
            WebDriverWait wait = new WebDriverWait(driver, Constantes.TimeoutShort);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(element)));
            return true;
        }
        catch (WebDriverException ex){
            return false;
        }
    }

}
