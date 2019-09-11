package scraping.secop.Util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import scraping.secop.SecopVO.Constantes;

public class ElementExist {

    public boolean elementeExist(String element, WebDriver driver){
        try{
            WebDriverWait wait = new WebDriverWait(driver, Constantes.TimeoutShort);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(element)));
            return true;
        }
        catch (WebDriverException ex){
            return false;
        }
    }

    public boolean elementeExistXPath(String element, WebDriver driver){
        try{
            WebDriverWait wait = new WebDriverWait(driver, Constantes.TimeoutShort);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(element)));
            return true;
        }
        catch (WebDriverException ex){
            return false;
        }
    }
}
