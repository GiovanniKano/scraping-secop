package scraping.secop.WebSecop;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import scraping.secop.SecopVO.Constantes;
import scraping.secop.SecopVO.DatosTablaVO;
import scraping.secop.Util.ElementExist;
import scraping.secop.Util.FilesUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DatosTabla {

    private final static Logger LOG = Logger.getLogger(DatosTabla.class);
    private List<DatosTablaVO> listDatos = new ArrayList<>();
    private ElementExist exist = new ElementExist();

    public void goToLink(WebDriver driver, List<String> links, List<String> nombreEntidad, String path){
        try {
            int contador = 0;
            for (String x: links) {
                JavascriptExecutor execute = (JavascriptExecutor)driver;
                execute.executeScript("window.open('"+x+"')");
                Set<String> tab_handles = driver.getWindowHandles();
                int number_of_tabs = tab_handles.size();
                int new_tab_index = number_of_tabs-1;
                driver.switchTo().window(tab_handles.toArray()[new_tab_index].toString());
                descargarDocumentos(driver);
                File ruta = new FilesUtils().moveDocuments(path);
                fillData(driver, nombreEntidad, contador);
                new SendEmail().email(listDatos.get(contador), ruta.getAbsolutePath());
                ruta.delete();
                contador++;
                driver.close();
                driver.switchTo().window(tab_handles.toArray()[0].toString());
            }
            driver.close();
        }
        catch (WebDriverException ex){
            LOG.error("Ocurrio un error abriendo una nueva ventana: " + ex.getMessage());
            driver.close();
        }
    }

    private void fillData(WebDriver driver, List<String> nombresEntidad, int contador){
        try{
            LOG.info("Llego a {fillData}");
            DatosTablaVO datos = new DatosTablaVO();
            datos.setNombreEntidad(nombresEntidad.get(contador));
            datos.setEnlace(driver.getCurrentUrl());
            datos.setDescripcion(driver.findElement(By.id("divDescriptionDiv_spnDescription")).getText());
            if(exist.elementeExist("cbxPriceGen", driver)){
                String valor = driver.findElement(By.id("cbxPriceGen")).getText();
                datos.setValorEstimado(valor);
            }else{
                datos.setValorEstimado("");
            }
            List<String> codigos = new ArrayList<>();
            if(exist.elementeExist("gridListMultipleNodeRegion_0_tbl", driver)){
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
            if(exist.elementeExistXPath("//label[.='Presentación de Ofertas']", driver)){
                String id = driver.findElement(By.xpath("//label[.='Presentación de Ofertas']")).getAttribute("id");
                id = id.replaceAll("[^\\d]", "");
                datos.setFechaPresentacion(driver.findElement(By.id("dtmbScheduleDateTime_"+id+"_txt")).getText().split("\\(", 3)[1]);
            }else if(exist.elementeExistXPath("//label[.='Plazo para manifestación de interés de limitar la convocatoria a Mypes y/o Mipymes']", driver)){
                String id = driver.findElement(By.xpath("//label[.='Plazo para manifestación de interés de limitar la convocatoria a Mypes y/o Mipymes']")).getAttribute("id");
                id = id.replaceAll("[^\\d]", "");
                datos.setFechaPresentacion(driver.findElement(By.id("dtmbScheduleDateTime_"+id+"_txt")).getText().split("\\(", 3)[1]);
            }
            else{
                if(exist.elementeExist("dtmbScheduleDateTime_51_txt", driver)){
                    datos.setFechaPresentacion(driver.findElement(By.id("dtmbScheduleDateTime_51_txt")).getText());
                }
            }
            listDatos.add(datos);
        }
        catch (WebDriverException ex){
            LOG.error("Ocurrio un error llenando los datos: " + ex.getMessage());
            throw new WebDriverException(ex.getMessage());
        }
    }

    private  void descargarDocumentos(WebDriver driver){
        try{
            LOG.info("Llego a {descargar documentos}");
            JavascriptExecutor js = (JavascriptExecutor)driver;
            js.executeScript(Constantes.SUPERPOSICION_NO_PERMANENTE, driver.findElement(By.xpath("//*[@id='mprContractNoticeMapper']/a[4]")));
            WebDriverWait wait = new WebDriverWait(driver, 20);
            LOG.info("llego aquí.");
            if(exist.elementeExist("grdGridDocumentList_tbl", driver)){
                LOG.info("Llego a {Primer if documentos}");
                WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='grdGridDocumentList_tbl']")));
                List<WebElement> filas = table.findElements(By.xpath("//table[@id='grdGridDocumentList_tbl']/tbody/tr[contains(@class,'gridLineLight') or contains(@class,'gridLineDark')]"));
                LOG.info("Tamaño de filas" + filas.size());
                boolean pasoCinco = false;
                boolean pasoDiez = false;
                boolean pasoQuince = false;
                for(int x = 0; x < filas.size(); x++){
                    if(x >= 5 && pasoCinco == false){
                        pasoCinco = true;
                        WebDriverWait wait2 = new WebDriverWait(driver, Constantes.TimeoutShort);
                        WebElement element = wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("grdGridDocumentList_Paginator_goToPage_Next")));
                        JavascriptExecutor jExc = (JavascriptExecutor)driver;
                        jExc.executeScript(Constantes.SUPERPOSICION_NO_PERMANENTE, element);
                        WebElement fila = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tdColumnDocumentNameP2Gen_spnDocumentName_"+x)));
                        String nombreDocumento = fila.getText().toUpperCase();
                        this.clickMore(nombreDocumento, driver, x);
                    }else if(x >= 10 && pasoDiez == false){
                        pasoDiez = true;
                        WebDriverWait wait2 = new WebDriverWait(driver, Constantes.TimeoutShort);
                        WebElement element = wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("grdGridDocumentList_Paginator_goToPage_Next")));
                        JavascriptExecutor jExc = (JavascriptExecutor)driver;
                        jExc.executeScript(Constantes.SUPERPOSICION_NO_PERMANENTE, element);
                        WebDriverWait wait1 = new WebDriverWait(driver, Constantes.Timeout);
                        WebElement fila = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.id("tdColumnDocumentNameP2Gen_spnDocumentName_"+x)));
                        String nombreDocumento = fila.getText().toUpperCase();
                        this.clickMore(nombreDocumento, driver, x);
                    }else if(x >= 15 && pasoQuince == false){
                        pasoQuince = true;
                        WebDriverWait wait2 = new WebDriverWait(driver, Constantes.TimeoutShort);
                        WebElement element = wait2.until(ExpectedConditions.visibilityOfElementLocated(By.id("grdGridDocumentList_Paginator_goToPage_Next")));
                        JavascriptExecutor jExc = (JavascriptExecutor)driver;
                        jExc.executeScript(Constantes.SUPERPOSICION_NO_PERMANENTE, element);
                        WebDriverWait wait1 = new WebDriverWait(driver, Constantes.Timeout);
                        WebElement fila = wait1.until(ExpectedConditions.visibilityOfElementLocated(By.id("tdColumnDocumentNameP2Gen_spnDocumentName_"+x)));
                        String nombreDocumento = fila.getText().toUpperCase();
                        this.clickMore(nombreDocumento, driver, x);
                    }else {
                        LOG.info("Llego al else");
                        WebElement fila = driver.findElement(By.id("tdColumnDocumentNameP2Gen_spnDocumentName_"+x));
                        String nombreDocumento = fila.getText().toUpperCase();
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
        if(nombreDocumento.contains("PLIEGO") || nombreDocumento.contains("ESTUDIO")){
            WebElement element = new WebDriverWait(driver, 20).until(ExpectedConditions.visibilityOfElementLocated(By.id("lnkDetailLinkP3Gen_"+x)));
            JavascriptExecutor js = (JavascriptExecutor)driver;
            js.executeScript(Constantes.SUPERPOSICION_NO_PERMANENTE, element);
            Set<String> tab_handles = driver.getWindowHandles();
            int number_of_tabs = tab_handles.size();
            int new_tab_index = number_of_tabs-1;
            driver.switchTo().window(tab_handles.toArray()[new_tab_index].toString());
            if(exist.elementeExist("tdMTC1_tbToolBar_btnDownloadDocument", driver)){
                LOG.info("Llego al boton descargar");
                WebElement element1 = driver.findElement(By.id("tdMTC1_tbToolBar_btnDownloadDocument"));
                JavascriptExecutor jsExcu = (JavascriptExecutor)driver;
                jsExcu.executeScript(Constantes.SUPERPOSICION_NO_PERMANENTE, element1);
                if(!exist.elementeExist("esperaProvocada", driver)){
                    driver.close();
                    driver.switchTo().window(tab_handles.toArray()[1].toString());
                }
            }
        }
    }
}
