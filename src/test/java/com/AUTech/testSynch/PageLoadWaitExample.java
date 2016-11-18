package com.AUTech.testSynch;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by Ali Ulvi Talipoglu on 18.11.2016.
 */

public class PageLoadWaitExample {

    static WebDriver driver;

    @Test
    public void testPageLoadSynchronization() throws Exception {
        System.setProperty("webdriver.gecko.driver", "C:\\geckodriver-v0.11.1-win64\\geckodriver.exe");
        driver = new FirefoxDriver();
        driver.navigate().to("https://en.wikipedia.org/wiki/Main_Page");

        By theLinkToNewPage = By.linkText("Log in");
        //Element of the link
        WebElement theLinkToNewPageElement = driver.findElement(theLinkToNewPage);
        //Click the link
        theLinkToNewPageElement.click();
        WebDriverWait wait = new WebDriverWait(driver, 60, 100);
        wait.until(ExpectedConditions.stalenessOf(theLinkToNewPageElement));
        wait.until(ExpectedConditions.presenceOfElementLocated(theLinkToNewPage));
        //Page is loaded
    }
}
