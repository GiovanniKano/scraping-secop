package scraping.secop.WebSecop;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import scraping.secop.SecopVO.Constantes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ScrapingWebSecop {

    private final static Logger LOG = Logger.getLogger(ScrapingWebSecop.class);
    private WebDriverWait wait;

    public void startScrapinWeb(){
        try{
            LOG.info("Iniciando driver de chrome");
            System.setProperty("webdriver.chrome.driver", "C:\\Users\\Giova\\Documents\\DriverChrome\\chromedriver.exe");
            WebDriver driver = new ChromeDriver();
            driver.manage().window().maximize();
            driver.get(Constantes.URL);
            this.loginSecop(driver);
            LOG.info("Navengando por la página principal.");
            this.goTo(driver);
            LOG.info("Navengando por busqueda de procesos.");
            this.busquedaAvanzada(driver);
        }
        catch (WebDriverException ex){
            LOG.error("Ocurrio un error inicializando el scraping" + ex.getMessage());
        }
    }

    private void loginSecop(WebDriver driver){
        try{
            LOG.info("Iniciando sesión...");
            driver.findElement(By.id("txtUserName")).sendKeys(Constantes.usuario);
            driver.findElement(By.id("txtPassword")).sendKeys(Constantes.cotrasena);
            driver.findElement(By.id("btnLoginButton")).click();
            LOG.info("Inicio de sesión exitoso.");
            wait = new WebDriverWait(driver, Constantes.Timeout);
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnAcknowledgeGen")));
            LOG.info(element);
            JavascriptExecutor jse2 = (JavascriptExecutor)driver;
            jse2.executeScript(Constantes.SUPERPOSICION_NO_PERMANENTE, element);
        }
        catch (WebDriverException ex){
            LOG.error("Ocurrio un error iniciado sesión en la página" + ex.getMessage());
        }
    }

    private void goTo(WebDriver driver){
        try{
            LOG.info("Llendo a...");
            wait = new WebDriverWait(driver, Constantes.Timeout);
            WebElement  element = wait.until(ExpectedConditions.elementToBeClickable(By.id("IWantToContainer")));
            Actions actions = new Actions(driver);
            actions.moveToElement(element).click().perform();
            driver.findElement(By.xpath("//*[contains(text(), 'Buscar procesos')]")).click();
            LOG.info("Ir a... Exitoso");
        }
        catch (WebDriverException ex){
            LOG.error("Ocurrio un error intentando ir a: " + ex.getMessage());
            driver.close();
        }
    }

    public void busquedaAvanzada(WebDriver driver){
        try {
            wait = new WebDriverWait(driver, Constantes.Timeout);
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("lnkAdvancedSearch")));
            JavascriptExecutor jse = (JavascriptExecutor)driver;
            jse.executeScript(Constantes.SUPERPOSICION_NO_PERMANENTE, element);
            LOG.info("Click sobre busqueda avanzada exitoso.");
            driver.findElement(By.id("txtMainCategoryText")).sendKeys(Constantes.CODUNSPC);
            wait = new WebDriverWait(driver,Constantes.Timeout);
            WebElement element1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '"+Constantes.CODUNSPC+"')]")));
            JavascriptExecutor java = (JavascriptExecutor)driver;
            java.executeScript(Constantes.SUPERPOSICION_NO_PERMANENTE, element1);
            driver.findElement(By.id("btnSearchButton")).click();
            LOG.info("Esperando respuesta");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tblMainTable_trRowMiddle_tdCell1_tblForm_trTitleRow_tdCell1_spnAdvancedSearchTitle")));
            this.getTable(driver);
            LOG.info("Busqueda exitosa.");
        }
        catch (WebDriverException ex){
            LOG.error("Ocurrio un error hacien click en busqueda avanzada: " + ex.getMessage());
            driver.close();
        }
    }

    private void getTable(WebDriver driver){
        try{
            WebDriverWait wait = new WebDriverWait(driver, Constantes.Timeout);
            WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tblMainTable_trRowMiddle_tdCell1_tblForm_trGridRow_tdCell1_grdResultList_tbl")));
            JavascriptExecutor execute = (JavascriptExecutor)driver;
            execute.executeScript("arguments[0].scrollIntoView();", table);
            List<WebElement> filas = table.findElements(By.xpath("//tr[contains(@class,'gridLineLight Bold') or contains(@class,'gridLineDark Bold')]"));
            LOG.info("Se obtuvieron la siguiente cantidad de filas: " + filas.size());
            this.getFecha(filas, driver);
        }
        catch (WebDriverException ex){
            LOG.error("Ocurrio un error trayendo la tabla: " + ex.getMessage());
            throw new WebDriverException(ex.getMessage());
        }
    }

    private void getFecha(List<WebElement> tabla, WebDriver driver){
        try{
            String[] campos;
            int contador = 0;
            boolean filaCompleta = true;
            List<WebElement> filas = new ArrayList<WebElement>();
            for(int x = 0; x < tabla.size(); x++){
                String fecha = driver.findElement(By.id("dtmbRequestOnlinePublishingDate_"+x)).getText();
                campos = fecha.split("\\(",3);
                LOG.info(fecha);
                LOG.info(campos[1]);
                filaCompleta = this.diasTranscurridos(campos[1]);
                if(filaCompleta){
                    LOG.info(filas.size());
                    LOG.info("Filas que pasaron: " + contador++);
                    filas.add(tabla.get(x));
                }
            }
            LOG.info(filaCompleta);
            if(filaCompleta){
                WebElement element = driver.findElement(By.id("tblMainTable_trRowMiddle_tdCell1_tblForm_trGridRow_tdCell1_grdResultList_Paginator_goToPage_MoreItems"));
                JavascriptExecutor java = (JavascriptExecutor)driver;
                java.executeScript("arguments[0].scrollIntoView();", element);
                java.executeScript(Constantes.SUPERPOSICION_NO_PERMANENTE, element);
                this.getTable(driver);
            }
            getDatos(filas, driver);
        }
        catch (WebDriverException ex){
            LOG.error("Ocurrio un error obteniendo fechas: " + ex.getMessage());
            throw new WebDriverException(ex.getMessage());
        }
    }

    private void getDatos(List<WebElement> tabla, WebDriver driver){
        try{
            JavascriptExecutor execute = (JavascriptExecutor)driver;
            WebElement table = driver.findElement(By.id("tblMainTable_trRowMiddle_tdCell1_tblForm_trGridRow_tdCell1_grdResultList_tbl"));
            execute.executeScript("arguments[0].scrollIntoView();", table);
            LOG.info("Llego a getDatos.");
            List<String> links = new ArrayList<String>();
            for (int x = 0; x < tabla.size(); x++) {
                WebDriverWait wait = new WebDriverWait(driver, Constantes.Timeout);
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("tblMainTable_trRowMiddle_tdCell1_tblForm_trGridRow_tdCell1_grdResultList_tdDetailCol_lnkDetailColLink_"+x+"")));
                String text = element.getAttribute("href");
                links.add(text);
                LOG.info(links.get(x));
            }
            new DatosTabla().goToLink(driver, links);
        }
        catch (WebDriverException ex){
            LOG.error("Ocurrio un error obteniendo detalle de la tabla: " + ex.getMessage());
            throw new WebDriverException(ex.getMessage());
        }
    }

    private Boolean diasTranscurridos(String fecha){
        try{
            SimpleDateFormat formatoinicio = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss a");
            String hoy = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss a").format(new Date());
            Date fechafin = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss a").parse(hoy);
            Date fechainicio = formatoinicio.parse(fecha);
            int diferencia = (int)(fechafin.getTime() - fechainicio.getTime());
            int horas = diferencia/(1000*60*60);
            LOG.info("Hora de diferencia: " + horas);
            if(horas <= 48){
                return true;
            }
            return false;
        }
        catch (Exception ex){
            LOG.error("Ocurrio un error comparando fechas" + ex.getMessage());
            return false;
        }
    }

    private void waitForJQueryLoad() {
        try {
            wait.until(driver -> (boolean)((JavascriptExecutor)driver).executeScript("return jQuery.active == 0"));
        }
        catch (WebDriverException ignored) {
            LOG.error("Ocurio un error esperando la carga de jquery: " + ignored.getMessage());
        }
    }
}
