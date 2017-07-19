package com.kafein.aveamerkez;

import com.google.common.base.Function;
import com.kafein.smsSender.sendSMSbyBPM_Pre_Package_WS;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
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

    //Overload without sleep param
    public void sendSms(String no, String KN, String sms) throws Exception {
        if (c.BPM.equalsIgnoreCase("evet")){
            //shortcut branching for BPM support option which cancels using this class. should have been an SMSsend controller class (SMSSenderFactory) for this for better design if this was a multiple-coder project
            String myKw;
            if (sms.contains("IPTAL"))
                myKw="IPTAL " + c.KW;
            else
                myKw="KAYIT " + c.KW;
            new sendSMSbyBPM_Pre_Package_WS().sendSms(no,c.TP,myKw);
            return; //exit
        }
        sendSms( no,  KN,  sms, Long.parseLong(c.sleep));
    }

    public void sendSms(String no, String KN, String sms, long sleepSeconds) throws Exception {

        if (c.SMS_Gonderimi_icin_ikinci_yolu_kullan.equalsIgnoreCase("evet")) {

            if (c.kanal.equalsIgnoreCase("promo")) {

                ProcessBuilder pb = new ProcessBuilder(
                        System.getProperty("user.dir") + "/sendSms/send",
                        "promotion.sms.incoming2",
                        "90" + no + "|" + KN + "|" + sms);
                pb.directory(new File(System.getProperty("user.dir") + "/sendSms/"));
                pb.redirectErrorStream(true);
                Process pp = pb.start();
                pp.waitFor();
                System.err.println("sent to Promo");
            } else {

                ProcessBuilder pb = new ProcessBuilder(
                        System.getProperty("user.dir") + "/sendSms/send",
                        "intelligentRouter.incoming2",
                        "90" + no + "][" + KN + "][" + sms);
                pb.directory(new File(System.getProperty("user.dir") + "/sendSms/"));
                pb.redirectErrorStream(true);
                Process pp = pb.start();
                pp.waitFor();
                System.err.println("sent to IR");

            }
            Thread.sleep(sleepSeconds * 1000);

            return;
        }
        if (driver == null) {

            // System.setProperty("webdriver.chrome.driver", "C:\\firstSeleniumTest\\chromedriver.exe");
            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\chromedriver.exe");
            driver = new ChromeDriver();
//            driver = new RemoteWebDriver(new URL("http://127.0.0.1:9515"), DesiredCapabilities.chrome());
            try {
                //create new thread for interaction with windows authentication window
                driver.manage().timeouts().pageLoadTimeout(35, TimeUnit.SECONDS);

                driver.navigate().to("http://10.248.68.40:7015/KafeinTestMaster/fsk/acme/SmsGonder.xhtml");
                driver.findElement(By.cssSelector("#j_idt20\\3a j_idt22\\3a 1"));
            } catch (Exception e) {
                Thread t = new Thread(new LoginWindow(1));
                t.start();
                t.join();
                // System.out.println("Sifre pop-up'i kapatildi");
                try {
                    driver.navigate().to("http://10.248.68.40:7015/KafeinTestMaster/fsk/acme/SmsGonder.xhtml");
                    driver.findElement(By.cssSelector("#j_idt20\\3a j_idt22\\3a 1"));

                } catch (Exception ee) {
                    t = new Thread(new LoginWindow(1));
                    t.start();
                    t.join();
                }
            }
            if (c.kanal.equalsIgnoreCase("promo")) //promo or ir
                fluentWait(By.cssSelector("#j_idt20\\3a j_idt22\\3a 1")).click();
            else
                fluentWait(By.cssSelector("#j_idt20\\3a j_idt22\\3a 0")).click();
            //fields
            msisdn = driver.findElement(By.cssSelector("#j_idt20\\3a msisdn"));
            tx = driver.findElement(By.cssSelector("#j_idt20\\3a smsText"));
            bt = driver.findElement(By.cssSelector("#j_idt20\\3a j_idt33"));
            kn = driver.findElement(By.cssSelector("#j_idt20\\3a shortNumberw"));
        }
        //clear
        msisdn.clear();
        kn.clear();
        tx.clear();

        msisdn.sendKeys(no);
        kn.sendKeys(KN);
        tx.sendKeys(sms);

        bt.click();
        if (!tx.getText().isEmpty()) {
            System.err.println("sifre soruyor galiba");
            Thread t = new Thread(new LoginWindow(1));
            t.start();
            t.join();
            msisdn.clear();
            kn.clear();
            tx.clear();

            msisdn.sendKeys(no);
            kn.sendKeys(KN);
            tx.sendKeys(sms);

            bt.click();

        }
        driver.manage().window().setSize(new Dimension(66, 549));
        driver.manage().window().setPosition(new Point(-29, -33));
        Thread.sleep(sleepSeconds * 1000);


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

    //inner class for Login thread
    public class LoginWindow implements Runnable {
        long sec = 9;

        public LoginWindow(long secondsToSleep) {
            sec = secondsToSleep;
        }

        public LoginWindow() {

        }

        @Override
        public void run() {
            try {
                login();
            } catch (Exception ex) {
                System.out.println("Error in Login Thread: " + ex.getMessage());
            }
        }

        public void login() throws Exception {

            //wait - increase this wait period if required
            Thread.sleep(sec * 1000);
            //create robot for keyboard operations
            Robot rb = new Robot();
            System.out.println("Pressing Enter  ");

            //  rb.keyPress(KeyEvent.VK_ESCAPE);
            // rb.keyRelease(KeyEvent.VK_ESCAPE);
            rb.keyPress(KeyEvent.VK_ENTER);
            rb.keyRelease(KeyEvent.VK_ENTER);
            //wait
            Thread.sleep(sec * 1000);
        }
    }


}
