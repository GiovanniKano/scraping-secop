package scraping.secop.WebSecop;

import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import scraping.secop.SecopVO.Constantes;
import scraping.secop.Util.ElementExist;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class ScrapingWebSecop {

    private final static Logger LOG = Logger.getLogger(ScrapingWebSecop.class);
    private WebDriverWait wait;
    private File folder;
    private ElementExist exist;

    public void startScrapinWeb(String codigo){
        try{
            LOG.info("Iniciando driver de chrome");
            folder = new File(UUID.randomUUID().toString());
            folder.mkdirs();
            LOG.info(folder.getAbsolutePath());
            HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
            chromePrefs.put("profile.default_content_settings.popups", 0);
            chromePrefs.put("download.default_directory", folder.getAbsolutePath());
            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("prefs", chromePrefs);
            options.addArguments("--disable-notifications");
            DesiredCapabilities cap = DesiredCapabilities.chrome();
            cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            cap.setCapability(ChromeOptions.CAPABILITY, options);
            System.setProperty("webdriver.chrome.driver", "D:\\SeleniumDrive\\chromedriver.exe");
            WebDriver driver = new ChromeDriver(options);
            driver.manage().window().maximize();
            driver.get(Constantes.URL);
            this.loginSecop(driver);
            LOG.info("Navengando por la página principal.");
            this.goTo(driver);
            LOG.info("Navengando por busqueda de procesos.");
            this.busquedaAvanzada(driver, codigo);
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
            JavascriptExecutor java = (JavascriptExecutor)driver;
            java.executeScript(Constantes.SUPERPOSICION_NO_PERMANENTE, element);
            wait = new WebDriverWait(driver, Constantes.TimeoutShort);
            WebElement click = driver.findElement(By.xpath("//*[contains(text(), 'Buscar procesos')]"));
            java.executeScript(Constantes.SUPERPOSICION_NO_PERMANENTE, click);
            LOG.info("Ir a... Exitoso");
        }
        catch (WebDriverException ex){
            LOG.error("Ocurrio un error intentando ir a: " + ex.getMessage());
            driver.close();
        }
    }

    public void busquedaAvanzada(WebDriver driver, String codigo){
        try {
            wait = new WebDriverWait(driver, Constantes.Timeout);
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("lnkAdvancedSearch")));
            JavascriptExecutor jse = (JavascriptExecutor)driver;
            jse.executeScript(Constantes.SUPERPOSICION_NO_PERMANENTE, element);
            LOG.info("Click sobre busqueda avanzada exitoso.");
            driver.findElement(By.id("txtMainCategoryText")).sendKeys(codigo);
            wait = new WebDriverWait(driver,Constantes.Timeout);
            WebElement element1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(), '"+codigo+"')]")));
            JavascriptExecutor java = (JavascriptExecutor)driver;
            java.executeScript(Constantes.SUPERPOSICION_NO_PERMANENTE, element1);
            driver.findElement(By.id("btnSearchButton")).click();
            LOG.info("Esperando respuesta");
            wait = new WebDriverWait(driver, Constantes.Timeout);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("loadingCursor")));
            if(wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loadingCursor")))) {
                getTable(driver);
            }
        }
        catch (WebDriverException ex){
            LOG.error("Ocurrio un error hacien click en busqueda avanzada: " + ex.getMessage());
            driver.close();
        }
    }

    private void getTable(WebDriver driver){
        try{
            wait = new WebDriverWait(driver, Constantes.Timeout);
            WebElement espera = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tblMainTable_trRowMiddle_tdCell1_tblForm_trTitleRow_tdCell1_spnFilteringOver")));
            if(espera != null){
                List<WebElement> filas = new ArrayList<>();
                WebDriverWait wait = new WebDriverWait(driver, Constantes.Timeout);
                WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tblMainTable_trRowMiddle_tdCell1_tblForm_trGridRow_tdCell1_grdResultList_tbl")));
                JavascriptExecutor execute = (JavascriptExecutor)driver;
                execute.executeScript("arguments[0].scrollIntoView();", table);
                filas = table.findElements(By.xpath("//tr[@class='gridLineLight' or @class='gridLineDark']"));
                getFecha(filas, driver);
            }
        }
        catch (WebDriverException ex){
            LOG.error("Ocurrio un error trayendo la tabla: " + ex.getMessage());
            throw new WebDriverException(ex.getMessage());
        }
    }

    private void getFecha(List<WebElement> tabla, WebDriver driver){
        try{
            String[] campos;
            boolean filaCompleta = true;
            List<WebElement> filas = new ArrayList<>();
            for(int x = 0; x < tabla.size(); x++){
                WebDriverWait wait = new WebDriverWait(driver, Constantes.Timeout);
                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dtmbRequestOnlinePublishingDate_"+x)));
                String fecha = element.getText();
                campos = fecha.split("\\(",3);
                LOG.info(fecha);
                LOG.info(campos[1]);
                filaCompleta = this.diasTranscurridos(campos[1]);
                if(filaCompleta){
                    filas.add(tabla.get(x));
                    LOG.info("Filas agregadas: " + filas.size());
                }
            }
            LOG.info(filaCompleta);
            if(filaCompleta){
                clickMoreTable(driver);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("loadingCursor")));
                if(wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("loadingCursor")))){
                    this.getTable(driver);
                }
                if(!exist.elementeExist("esperaProvocada", driver)){
                    this.getTable(driver);
                }
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
            LOG.info("Filas a consultar: " + tabla.size());
            if(tabla.size() == 0){
                driver.close();
            }
            JavascriptExecutor execute = (JavascriptExecutor)driver;
            WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tblMainTable_trRowMiddle_tdCell1_tblForm_trGridRow_tdCell1_grdResultList_tbl")));
            execute.executeScript("arguments[0].scrollIntoView();", table);
            LOG.info("Llego a getDatos.");
            List<String> links = new ArrayList<>();
            List<String> entidad = new ArrayList<>();
            for (int x = 0; x < tabla.size(); x++) {
                WebDriverWait wait = new WebDriverWait(driver, Constantes.Timeout);
                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tblMainTable_trRowMiddle_tdCell1_tblForm_trGridRow_tdCell1_grdResultList_tdDetailCol_lnkDetailColLink_"+x+"")));
                String text = element.getAttribute("href");
                entidad.add(driver.findElement(By.id("tblMainTable_trRowMiddle_tdCell1_tblForm_trGridRow_tdCell1_grdResultList_tdManagingAuthorityCompanyCode_spnBuyerNameSpan_"+x+"")).getText());
                LOG.info("Nombre entidad: " + entidad.get(x));
                links.add(text);
                LOG.info(links.get(x));
            }
            new DatosTabla().goToLink(driver, links, entidad, folder.getAbsolutePath());
        }
        catch (WebDriverException ex){
            LOG.error("Ocurrio un error obteniendo detalle de la tabla: " + ex.getMessage());
            throw new WebDriverException(ex.getMessage());
        }
    }

    private Boolean diasTranscurridos(String fecha){
        try{
            String hoy = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss a").format(new Date());
            Date fechafin = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss a").parse(hoy);
            SimpleDateFormat formatoinicio = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss a");
            Date fechainicio = formatoinicio.parse(fecha);
            int diferencia = (int)(fechafin.getTime() - fechainicio.getTime());
            int horas = diferencia/(1000*60*60);
            LOG.info("Hora de diferencia: " + horas);
            if(horas <= 48 && horas >= 0){
                return true;
            }
            return false;
        }
        catch (Exception ex){
            LOG.error("Ocurrio un error comparando fechas" + ex.getMessage());
            return false;
        }
    }

    private void clickMoreTable(WebDriver driver){
        WebElement element = driver.findElement(By.id("tblMainTable_trRowMiddle_tdCell1_tblForm_trGridRow_tdCell1_grdResultList_Paginator_goToPage_MoreItems"));
        JavascriptExecutor java = (JavascriptExecutor)driver;
        java.executeScript("arguments[0].scrollIntoView();", element);
        java.executeScript(Constantes.SUPERPOSICION_NO_PERMANENTE, element);
    }
}
