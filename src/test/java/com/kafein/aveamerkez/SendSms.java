package com.kafein.aveamerkez;

import com.google.common.base.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.net.URL;
import java.util.concurrent.TimeUnit;



/**
 * Created by Ali Ulvi Talipoglu via Kafein on 19.11.2016.
 */
public class SendSms {
    static SendSms sendSms = new SendSms();//singleton detected
    static WebDriver driver;
    static Config c = Config.config;
    static WebElement msisdn;
    static WebElement tx;
    static WebElement bt;
    static WebElement kn;
    public void sendSms(String no, String KN, String sms) throws Exception {
        if (driver == null) {
            driver = new RemoteWebDriver(new URL("http://127.0.0.1:9515"), DesiredCapabilities.chrome());
            driver.navigate().to("http://10.248.68.40:7015/KafeinTestMaster/fsk/acme/SmsGonder.xhtml");
            if (c.kanal.equalsIgnoreCase("promo")) //promo or ir
                fluentWait(By.cssSelector("#j_idt20\\3a j_idt22\\3a 1")).click();
            else
                fluentWait(By.cssSelector("#j_idt20\\3a j_idt22\\3a 0")).click();
           //fields
            msisdn=driver.findElement(By.cssSelector("#j_idt20\\3a msisdn"));
            tx=driver.findElement(By.cssSelector("#j_idt20\\3a smsText"));
            bt=driver.findElement(By.cssSelector("#j_idt20\\3a j_idt33"));
            kn=driver.findElement(By.cssSelector("#j_idt20\\3a shortNumberw"));
        }
        //clear
        msisdn.clear();
        kn.clear();
        tx.clear();

        msisdn.sendKeys(no);
        kn.sendKeys(KN);
        tx.sendKeys(sms);

        bt.click( );
        Thread.sleep(Long.parseLong(c.sleep)*1000);

    }

    public WebElement fluentWait(final By locator) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(35, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class);

        WebElement foo = wait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(locator);
            }
        });

        return foo;
    }

}
