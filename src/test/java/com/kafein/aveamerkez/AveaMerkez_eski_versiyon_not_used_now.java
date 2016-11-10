package com.kafein.aveamerkez;

import com.google.common.base.Function;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assume.assumeTrue;


/**
 * Created by Ali Ulvi Talipoglu 071428 via Kafein on 26.10.2016.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AveaMerkez_eski_versiyon_not_used_now {
    static String user, pw, url, pack, kayitSms, gecko, tur, msisdn, day, ucretliMi, yetersizSMS, iptalSMS, kalanSMS,yetersizBakiyemsisdn,yetersizBakiyeKayitSMS; //Config.txt icin degiskenler
    static String master,NonNtyetersizBakiyeKayitSMS,NonNtyetersizBakiyeMsisdn,NonNT_msisdn,NonNT_Kayit_Mesaji,TCID;
    static WebDriver driver;

    @BeforeClass
    public static void drive() throws MalformedURLException {
        //Step 1- Driver Instantiation: Instantiate driver object as FirefoxDriver
         System.setProperty("webdriver.gecko.driver", gecko);
        // System.setProperty("webdriver.chrome.driver", "C:\\geckodriver-v0.11.1-win64\\chromedriver.exe");

         driver = new FirefoxDriver();

        //  driver = new ChromeDriver();
      /*  ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
      */
     // driver = new RemoteWebDriver(new URL("http://127.0.0.1:4444"), DesiredCapabilities.firefox());
    }

    //@AfterClass
    public static void clean()   {

         driver.close();
        driver.quit();
    }

    @BeforeClass
    public static void loadProps() {

        System.out.println("@BeforeClass: onceExecutedBeforeAll to read config");
        Properties prop = new Properties();
        InputStream input = null;
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));

        try {

            input = new FileInputStream("Config.txt");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            user = prop.getProperty("user");
            pw = prop.getProperty("sifre");
            url = prop.getProperty("url");
            pack = prop.getProperty("Paket_Gorunen_ismi");
            kayitSms = prop.getProperty("NT_Kayit_Mesaji");
            gecko = prop.getProperty("Firefox_Driver_Path");
            tur = prop.getProperty("Paket_Turu");
            msisdn = prop.getProperty("NTmsisdn");
            day = prop.getProperty("paket_kac_gunluk");
            //ucretliMi = prop.getProperty("paketUcretliMi?");
            //yetersizSMS = prop.getProperty("yetersizBakiyeSMS");
            iptalSMS = prop.getProperty("iptalSMS");
            kalanSMS = prop.getProperty("kalanSMS");
            yetersizBakiyemsisdn = prop.getProperty("NT_yetersizBakiye_Msisdn");
            yetersizBakiyeKayitSMS = prop.getProperty("NT_yetersizBakiye_KayitSMS");
            NonNtyetersizBakiyeKayitSMS = prop.getProperty("NONNT_yetersizBakiye_KayitSMS");
            NonNtyetersizBakiyeMsisdn = prop.getProperty("NONNT_yetersizBakiye_Msisdn");
            NonNT_msisdn = prop.getProperty("NonNT_msisdn");
            NonNT_Kayit_Mesaji = prop.getProperty("NonNT_Kayit_Mesaji");
            TCID = prop.getProperty("TCID");
            Enumeration<?> e = prop.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String value = prop.getProperty(key);
                if (!key.contentEquals("sifre"))
                    System.out.println("Key : " + key + "; Value : " + value);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


    }


    //We should add @Test annotation that JUnit will run below method
    @Test
    //Start to write our test method. It should ends with "Test"
    public void test1paketAlim() throws Exception {


        //  driver = new RemoteWebDriver(new URL("http://127.0.0.1:9515"), DesiredCapabilities.chrome());

        //Step 2- Navigation: Open a website
        driver.navigate().to(url);

        //Step 3- Assertion: Check its title is correct
        //assertEquals method Parameters: Message, Expected Value, Actual Value
        // Assert.assertEquals("AveaMerkez", driver.getTitle());
        //input.inputButton:nth-child(3)
        try {
            driver.findElement(By.name("userName")).sendKeys(user);
            driver.findElement(By.name("password")).sendKeys(pw);
            driver.findElement(By.name("sendButton")).click();
        } catch (Exception e) {
            System.err.println("pw girme hatasi " + e.getLocalizedMessage() + e.getMessage());
            e.printStackTrace();
            // driver.close();
            //driver.quit();
        }
        WebElement logout = fluentWait(By.id("logout"));
        driver.navigate().to(url);
        WebElement txtBox = fluentWait(By.cssSelector("#searchmsisdn"));
        // WebElement txtBox = driver.findElement(By.cssSelector("#searchmsisdn"));
        WebElement guncelle = driver.findElement(By.cssSelector("input[value='Paketleri Guncelle']"));
        guncelle.click();
        txtBox = fluentWait(By.cssSelector("#searchmsisdn"));
        //try {

        WebElement paketListesi = null;
        txtBox.sendKeys(msisdn);
        Thread.sleep(2000);
        WebElement tamam = driver.findElement(By.cssSelector("input[value=Tamam]"));
        tamam.click();
        Thread.sleep(2000);

        if (driver.findElements(By.className("clsInputLabel")).size() > 0 && fluentWait(By.className("clsInputLabel")).getText().contains("Hata")) {
            tamam.click();
        }
        if (tur.compareToIgnoreCase("ses") == 0) {
            paketListesi = fluentWait(By.cssSelector("select.inputBox2"));
        } else if (tur.compareToIgnoreCase("karma") == 0) {
            paketListesi = fluentWait(By.cssSelector("select#mix"));

        } else if (tur.compareToIgnoreCase("data") == 0) {
            paketListesi = fluentWait(By.xpath(".//*[@id='salePackageId']/table/tbody/tr[3]/td[text()=\"Data Paketleri\"]/following-sibling::td/div/select"));

        } else {
            paketListesi = fluentWait(By.xpath(".//*[@id='salePackageId']/table/tbody/tr/td[text()='" + tur + "']/following-sibling::td/div/select"));

        }
        WebElement pktName = fluentWait(By.cssSelector("option[value='newOnnetPack.vas']"));//just to wait page load
        Thread.sleep(2000);
        Select ses = new Select(paketListesi);
        ses.selectByVisibleText(pack);
        Thread.sleep(2500);

        try {

            WebElement tcID = driver.findElement(By.cssSelector("#recordoperations"));
            Select dropdown = new Select(tcID);
            dropdown.selectByIndex(1);
            if (!TCID.isEmpty()){
                dropdown.selectByIndex(2);
                Thread.sleep(2000);
                driver.findElement(By.cssSelector("#TC\\20 Kimlik\\20 NO")).sendKeys(TCID);
            }
        } catch (Exception e) {
            System.out.println("TC ID not needed or catched exception. skipping");
            //System.err.println("TC id girme hatasi " + e.getLocalizedMessage() + e.getMessage());
            //e.printStackTrace();
        }
        WebElement aktifBtn = driver.findElement(By.cssSelector("input[value='Aktif Et']"));
        Thread.sleep(2000);
        aktifBtn.click();
        Thread.sleep(2000);
        driver.switchTo().alert().accept();
        fluentWait(By.className("clsInputLabel"));
        Thread.sleep(2000);
        List<WebElement> messages = driver.findElements(By.className("clsInputLabel"));
        for (WebElement ms : messages) {
            System.out.println(ms.getText());
        }
        for (WebElement ms : messages) {
            System.out.println(ms.getText());
            Assert.assertFalse("Hata mesaji bulundu:" + ms.getText(), ms.getText().contains("Hata:"));
        }
        String message1 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[1]/td/font")).getText();
        Assert.assertTrue("Kayit Mesaji yanlis:" + message1, message1.contentEquals("  * Paket satışı başarıyla yapıldı."));
        String message2 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[2]/td/font")).getText();

        String gun = addDay();
        kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);
        kayitSms = kayitSms.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
        kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
        kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

        System.out.println("Beklenen kayit SMSi: " + kayitSms);
        //String kayitSMS="Paketiniz hattiniza basariyla tanimlanmistir. Bal 300 Paketi ile 1 ay boyunca yurtici her yone 300 dakika konusabilirsiniz.Kullanim suresi sonunda hattinizda yeterli bakiye olmasi halinde paketiniz otomatik olarak yenilenecektir.Paketinizi iptal etmek icin IPTAL yazip 2070e gonderin";
        String pattern = "  Islem için gerekli bütün kontroller basari ile yapilmistir. " + "\\s*" + kayitSms + "\\.?\\.?\\.?\\s*";
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(message2);

        Assert.assertTrue("Kayit Mesaji yanlis..:" + message2, m.matches());

        //Thread.sleep(30000);
        // driver.close();
        //driver.quit();
        System.out.println("\n test1 paket alim bitti");

       /* } catch (Exception e) {
            System.err.println(e.getLocalizedMessage() + e.getMessage());
            e.printStackTrace();
            // driver.close();
            //  driver.quit();
        }*/
    }


    @Test
    public void test2_kalan() throws Exception {

        assumeTrue(!kalanSMS.isEmpty());
        fluentWait(By.xpath(".//td[text()=\"" + pack + "\"]/preceding-sibling::td/input")).click();
         master = driver.getWindowHandle();
        fluentWait(By.xpath(".//td[text()=\"" + pack + "\"]/following-sibling::td/input")).click();
        int cnt=0;
        while(driver.getWindowHandles().size() == 1) {
            Thread.sleep(250);
            if (cnt > 44) break;
        }
        System.out.println(driver.getWindowHandles().size());
        Set<String> windowId = driver.getWindowHandles();    // get  window id of current window
        Iterator<String> itererator = windowId.iterator();
        String mainWinID = itererator.next();
        String  newAdwinID = itererator.next();
        driver.switchTo().window(newAdwinID);
        //System.out.println(fluentWait(By.cssSelector("body > table > tbody > tr:nth-child(3) > td:nth-child(1) > b")).getText());
        Assert.assertEquals (kalanSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy",addDay()),fluentWait(By.xpath("/html/body/table/tbody/tr[3]/td[3]/kalan")).getText());
        driver.switchTo().window(master);  // switch back to parent window
        System.out.println("\n test2 paket kalan bitti");

    }


    @Test
    public void test3_iptal() throws Exception {

        assumeTrue(!iptalSMS.isEmpty());
        Set<String> windowId = driver.getWindowHandles();    // get  window id of current window
        Iterator<String> itererator = windowId.iterator();
        String mainWinID = itererator.next();
        driver.switchTo().window(mainWinID);
        WebElement chck=fluentWait(By.xpath(".//td[text()=\"" + pack + "\"]/preceding-sibling::td/input"));
        if (chck.getAttribute("checked") == null) chck.click();
        driver.findElement(By.cssSelector("input[onclick='deactivatePackets();']")).click();
        Thread.sleep(2000);
        driver.switchTo().alert().accept();
        new WebDriverWait(driver, 30).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(".//td[text()=\"" + pack + "\"]/preceding-sibling::td/input")));
        Thread.sleep(3000);
        fluentWait(By.className("clsInputLabel"));
        List<WebElement> messages = driver.findElements(By.className("clsInputLabel"));
        for (WebElement ms : messages) {
            System.out.println(ms.getText());
            Assert.assertFalse("Hata mesaji bulundu:" + ms.getText(), ms.getText().contains("Hata:"));
        }
        String message1 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[1]/td/font")).getText();
        Assert.assertEquals("Iptal Mesaji Yanlis",message1,"  * "+pack+" paketinin iptali başarıyla yapıldı.");

        String message2 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[2]/td/font")).getText();
        String gun = addDay();
        iptalSMS = iptalSMS.replace("{sysdate+30}",gun);
        iptalSMS = iptalSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy|<BONUS_END_DATE>",gun);

        System.out.println("INFO: Beklenen iptal SMSi : " + iptalSMS);
        String pattern = "\\s*Islem için gerekli bütün kontroller basari ile yapilmistir. " + "\\s*" + iptalSMS + "\\.?\\.?\\.?\\s*";
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);
        // Now create matcher object.
        Matcher m = r.matcher(message2);
        System.out.println("INFO: beklenen regex: "+pattern);
        Assert.assertTrue("Iptal Mesaji yanlis..:" + message2, m.matches());
        System.out.println("\n test3 paket iptal bitti");
    }

    @Test
    public void test4_yetersizBakiye_paketAlim() throws Exception {

        assumeTrue(!yetersizBakiyeKayitSMS.isEmpty()&& !yetersizBakiyemsisdn.isEmpty());
        Set<String> windowId = driver.getWindowHandles();    // get  window id of current window
        Iterator<String> itererator = windowId.iterator();
        String mainWinID = itererator.next();
        driver.switchTo().window(mainWinID);
        //Step 2- Navigation: Open a website
        //driver.navigate().to(url);

        WebElement txtBox = null;
        // WebElement txtBox = driver.findElement(By.cssSelector("#searchmsisdn"));
        txtBox = fluentWait(By.cssSelector("#searchmsisdn"));
        //try {

        WebElement paketListesi = null;
        txtBox.clear();
        txtBox.sendKeys(yetersizBakiyemsisdn);
        Thread.sleep(2000);
        WebElement tamam = driver.findElement(By.cssSelector("input[value=Tamam]"));
        tamam.click();
        Thread.sleep(2000);
        fluentWait(By.cssSelector("#loader[style='']"));
        Thread.sleep(2000);
        fluentWait(By.cssSelector("#loader[style='display: none;']"));



        if (tur.compareToIgnoreCase("ses") == 0) {
            paketListesi = fluentWait(By.cssSelector("select.inputBox2"));
        } else if (tur.compareToIgnoreCase("karma") == 0) {
            paketListesi = fluentWait(By.cssSelector("select#mix"));

        } else if (tur.compareToIgnoreCase("data") == 0) {
            paketListesi = fluentWait(By.xpath(".//*[@id='salePackageId']/table/tbody/tr[3]/td[text()=\"Data Paketleri\"]/following-sibling::td/div/select"));

        } else {
            paketListesi = fluentWait(By.xpath(".//*[@id='salePackageId']/table/tbody/tr/td[text()='" + tur + "']/following-sibling::td/div/select"));

        }
        WebElement pktName = fluentWait(By.cssSelector("option[value='newOnnetPack.vas']"));//just to wait page load
        Thread.sleep(2000);
        Select ses = new Select(paketListesi);
        ses.selectByVisibleText(pack);
        try {

            WebElement tcID = driver.findElement(By.cssSelector("#recordoperations"));
            Select dropdown = new Select(tcID);
            dropdown.selectByIndex(1);
            if (!TCID.isEmpty()){
                dropdown.selectByIndex(2);
                Thread.sleep(2000);
                driver.findElement(By.cssSelector("#TC\\20 Kimlik\\20 NO")).sendKeys(TCID);
            }
        } catch (Exception e) {
            System.out.println("TC ID not needed or catched exception. skipping");
            //System.err.println("TC id girme hatasi " + e.getLocalizedMessage() + e.getMessage());
            //e.printStackTrace();
        }
        WebElement aktifBtn = driver.findElement(By.cssSelector("input[value='Aktif Et']"));
        Thread.sleep(2000);
        aktifBtn.click();
        Thread.sleep(2000);
        driver.switchTo().alert().accept();
        fluentWait(By.className("clsInputLabel"));
        Thread.sleep(2000);
        List<WebElement> messages = driver.findElements(By.className("clsInputLabel"));
        /*for (WebElement ms : messages) {
            System.out.println(ms.getText());
            Assert.assertFalse("Hata mesaji bulundu:" + ms.getText(), ms.getText().contains("Hata:"));
        }*/
       // String message1 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[1]/td/font")).getText();
      //  Assert.assertTrue("Kayit Mesaji yanlis:" + message1, message1.contentEquals("  * Paket satışı başarıyla yapıldı."));
        String message2 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[2]/td/font")).getText();

        String gun = addDay();
        yetersizBakiyeKayitSMS = yetersizBakiyeKayitSMS.replaceAll("<BONUS_END_DATE>", gun);
        yetersizBakiyeKayitSMS = yetersizBakiyeKayitSMS.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
        yetersizBakiyeKayitSMS = yetersizBakiyeKayitSMS.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
        yetersizBakiyeKayitSMS = yetersizBakiyeKayitSMS.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

        System.out.println("Beklenen yetersizBakiyeKayitSMS i: " + yetersizBakiyeKayitSMS);
        String pattern = ".*" + yetersizBakiyeKayitSMS + "\\.?\\.?\\.?\\s*";
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(message2);
        System.out.println("Beklenen regex Patterni:" + pattern);

        Assert.assertTrue("yetersizBakiyeKayit Mesaji yanlis..:" + message2, m.matches());

        //Thread.sleep(30000);
        // driver.close();
        //driver.quit();
        System.out.println("\n test4 yetersizBakiyeKayit bitti");

       /* } catch (Exception e) {
            System.err.println(e.getLocalizedMessage() + e.getMessage());
            e.printStackTrace();
            // driver.close();
            //  driver.quit();
        }*/
    }

    @Test
    public void test5_NonNT_paketAlim() throws Exception {

        assumeTrue(!NonNT_Kayit_Mesaji.isEmpty()&& !NonNT_msisdn.isEmpty());

        //  driver = new RemoteWebDriver(new URL("http://127.0.0.1:9515"), DesiredCapabilities.chrome());

        //Step 2- Navigation: Open a website
        driver.navigate().to(url);

        //Step 3- Assertion: Check its title is correct
        //assertEquals method Parameters: Message, Expected Value, Actual Value
        // Assert.assertEquals("AveaMerkez", driver.getTitle());
        //input.inputButton:nth-child(3)
        try {
            driver.findElement(By.name("userName")).sendKeys(user);
            driver.findElement(By.name("password")).sendKeys(pw);
            driver.findElement(By.name("sendButton")).click();
        } catch (Exception e) {
            System.out.println("no pw needed. skipping.");

            //System.err.println("pw girme hatasi " + e.getLocalizedMessage() + e.getMessage());
            //e.printStackTrace();
            // driver.close();
            //driver.quit();
        }
        WebElement logout = fluentWait(By.id("logout"));
        driver.navigate().to(url);
        WebElement txtBox = fluentWait(By.cssSelector("#searchmsisdn"));
        // WebElement txtBox = driver.findElement(By.cssSelector("#searchmsisdn"));
        WebElement guncelle = driver.findElement(By.cssSelector("input[value='Paketleri Guncelle']"));
        guncelle.click();
        txtBox = fluentWait(By.cssSelector("#searchmsisdn"));
        //try {

        WebElement paketListesi = null;
        txtBox.sendKeys(NonNT_msisdn);
        Thread.sleep(2000);
        WebElement tamam = driver.findElement(By.cssSelector("input[value=Tamam]"));
        tamam.click();
        Thread.sleep(2000);

        if (driver.findElements(By.className("clsInputLabel")).size() > 0 && fluentWait(By.className("clsInputLabel")).getText().contains("Hata")) {
            tamam.click();
        }
        if (tur.compareToIgnoreCase("ses") == 0) {
            paketListesi = fluentWait(By.cssSelector("select.inputBox2"));
        } else if (tur.compareToIgnoreCase("karma") == 0) {
            paketListesi = fluentWait(By.cssSelector("select#mix"));

        } else if (tur.compareToIgnoreCase("data") == 0) {
            paketListesi = fluentWait(By.xpath(".//*[@id='salePackageId']/table/tbody/tr[3]/td[text()=\"Data Paketleri\"]/following-sibling::td/div/select"));

        } else {
            paketListesi = fluentWait(By.xpath(".//*[@id='salePackageId']/table/tbody/tr/td[text()='" + tur + "']/following-sibling::td/div/select"));

        }
        WebElement pktName = fluentWait(By.cssSelector("option[value='newOnnetPack.vas']"));//just to wait page load
        Thread.sleep(2000);
        Select ses = new Select(paketListesi);
        ses.selectByVisibleText(pack);
        try {

            WebElement tcID = driver.findElement(By.cssSelector("#recordoperations"));
            Select dropdown = new Select(tcID);
            dropdown.selectByIndex(1);
            if (!TCID.isEmpty()){
                dropdown.selectByIndex(2);
                Thread.sleep(2000);
                driver.findElement(By.cssSelector("#TC\\20 Kimlik\\20 NO")).sendKeys(TCID);
            }
        } catch (Exception e) {
            System.out.println("TC ID not needed or catched exception. skipping");
            //System.err.println("TC id girme hatasi " + e.getLocalizedMessage() + e.getMessage());
            //e.printStackTrace();
        }
        WebElement aktifBtn = driver.findElement(By.cssSelector("input[value='Aktif Et']"));
        Thread.sleep(2000);
        aktifBtn.click();
        Thread.sleep(2000);
        driver.switchTo().alert().accept();
        fluentWait(By.className("clsInputLabel"));
        Thread.sleep(2000);
        List<WebElement> messages = driver.findElements(By.className("clsInputLabel"));
        for (WebElement ms : messages) {
            System.out.println(ms.getText());
        }
        for (WebElement ms : messages) {
            System.out.println(ms.getText());
            Assert.assertFalse("Hata mesaji bulundu:" + ms.getText(), ms.getText().contains("Hata:"));
        }
        String message1 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[1]/td/font")).getText();
        Assert.assertTrue("Kayit Mesaji yanlis:" + message1, message1.contentEquals("  * Paket satışı başarıyla yapıldı."));
        String message2 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[2]/td/font")).getText();

        String gun = addDay();
        NonNT_Kayit_Mesaji = NonNT_Kayit_Mesaji.replaceAll("<BONUS_END_DATE>", gun);
        NonNT_Kayit_Mesaji = NonNT_Kayit_Mesaji.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
        NonNT_Kayit_Mesaji = NonNT_Kayit_Mesaji.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
        NonNT_Kayit_Mesaji = NonNT_Kayit_Mesaji.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

        System.out.println("Beklenen NonNt kayit SMSi: " + NonNT_Kayit_Mesaji);
        //String kayitSMS="Paketiniz hattiniza basariyla tanimlanmistir. Bal 300 Paketi ile 1 ay boyunca yurtici her yone 300 dakika konusabilirsiniz.Kullanim suresi sonunda hattinizda yeterli bakiye olmasi halinde paketiniz otomatik olarak yenilenecektir.Paketinizi iptal etmek icin IPTAL yazip 2070e gonderin";
        String pattern = "  Islem için gerekli bütün kontroller basari ile yapilmistir. " + "\\s*" + NonNT_Kayit_Mesaji + "\\.?\\.?\\.?\\s*";
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(message2);

        Assert.assertTrue("NonNT Kayit Mesaji yanlis..:" + message2, m.matches());

        //Thread.sleep(30000);
        // driver.close();
        //driver.quit();
        System.out.println("\n test5 NonNT paket alim bitti");

       /* } catch (Exception e) {
            System.err.println(e.getLocalizedMessage() + e.getMessage());
            e.printStackTrace();
            // driver.close();
            //  driver.quit();
        }*/
    }

    @Test
    public void test6_NonNT_kalan() throws Exception {

        assumeTrue(!kalanSMS.isEmpty());
        assumeTrue(!yetersizBakiyeKayitSMS.isEmpty()&& !yetersizBakiyemsisdn.isEmpty());
        fluentWait(By.xpath(".//td[text()=\"" + pack + "\"]/preceding-sibling::td/input")).click();
        master = driver.getWindowHandle();
        fluentWait(By.xpath(".//td[text()=\"" + pack + "\"]/following-sibling::td/input")).click();
        int cnt=0;
        while(driver.getWindowHandles().size() == 1) {
            Thread.sleep(250);
            if (cnt > 44) break;
        }
        System.out.println(driver.getWindowHandles().size());
        Set<String> windowId = driver.getWindowHandles();    // get  window id of current window
        Iterator<String> itererator = windowId.iterator();
        String mainWinID = itererator.next();
        String  newAdwinID = itererator.next();
        driver.switchTo().window(newAdwinID);
        //System.out.println(fluentWait(By.cssSelector("body > table > tbody > tr:nth-child(3) > td:nth-child(1) > b")).getText());
        Assert.assertEquals (kalanSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy",addDay()),fluentWait(By.xpath("/html/body/table/tbody/tr[3]/td[3]/kalan")).getText());
        driver.switchTo().window(master);  // switch back to parent window
        System.out.println("\n test6 NonNt paket kalan bitti");

    }

    @Test
    public void test7_NonNT_iptal() throws Exception {

        assumeTrue(!iptalSMS.isEmpty());
        assumeTrue(!yetersizBakiyeKayitSMS.isEmpty()&& !yetersizBakiyemsisdn.isEmpty());
        Set<String> windowId = driver.getWindowHandles();    // get  window id of current window
        Iterator<String> itererator = windowId.iterator();
        String mainWinID = itererator.next();
        driver.switchTo().window(mainWinID);
        WebElement chck=fluentWait(By.xpath(".//td[text()=\"" + pack + "\"]/preceding-sibling::td/input"));
        if (chck.getAttribute("checked") == null) chck.click();
        driver.findElement(By.cssSelector("input[onclick='deactivatePackets();']")).click();
        Thread.sleep(2000);
        driver.switchTo().alert().accept();
        new WebDriverWait(driver, 30).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(".//td[text()=\"" + pack + "\"]/preceding-sibling::td/input")));
        Thread.sleep(3000);
        fluentWait(By.className("clsInputLabel"));
        List<WebElement> messages = driver.findElements(By.className("clsInputLabel"));
        for (WebElement ms : messages) {
            System.out.println(ms.getText());
            Assert.assertFalse("Hata mesaji bulundu:" + ms.getText(), ms.getText().contains("Hata:"));
        }
        String message1 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[1]/td/font")).getText();
        Assert.assertEquals("Iptal Mesaji Yanlis",message1,"  * "+pack+" paketinin iptali başarıyla yapıldı.");

        String message2 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[2]/td/font")).getText();
        String gun = addDay();
        iptalSMS = iptalSMS.replace("{sysdate+30}",gun);
        iptalSMS = iptalSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy|<BONUS_END_DATE>",gun);

        System.out.println("INFO: Beklenen iptal SMSi : " + iptalSMS);
        String pattern = "\\s*Islem için gerekli bütün kontroller basari ile yapilmistir. " + "\\s*" + iptalSMS + "\\.?\\.?\\.?\\s*";
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);
        // Now create matcher object.
        Matcher m = r.matcher(message2);
        System.out.println("INFO: beklenen regex: "+pattern);
        Assert.assertTrue("NonNT Iptal Mesaji yanlis..:" + message2, m.matches());
        System.out.println("\n test7 NonNT paket iptal bitti");
    }

    @Test
    public void test8_NonNT_yetersizBakiye_paketAlim() throws Exception {

        assumeTrue(!NonNtyetersizBakiyeKayitSMS.isEmpty()&& !NonNtyetersizBakiyeMsisdn.isEmpty());
        Set<String> windowId = driver.getWindowHandles();    // get  window id of current window
        Iterator<String> itererator = windowId.iterator();
        String mainWinID = itererator.next();
        driver.switchTo().window(mainWinID);
        //Step 2- Navigation: Open a website
        //driver.navigate().to(url);

        WebElement txtBox = null;
        // WebElement txtBox = driver.findElement(By.cssSelector("#searchmsisdn"));
        txtBox = fluentWait(By.cssSelector("#searchmsisdn"));
        //try {

        WebElement paketListesi = null;
        txtBox.clear();
        txtBox.sendKeys(NonNtyetersizBakiyeMsisdn);
        Thread.sleep(2000);
        WebElement tamam = driver.findElement(By.cssSelector("input[value=Tamam]"));
        tamam.click();
        Thread.sleep(2000);
        fluentWait(By.cssSelector("#loader[style='']"));
        Thread.sleep(2000);
        fluentWait(By.cssSelector("#loader[style='display: none;']"));



        if (tur.compareToIgnoreCase("ses") == 0) {
            paketListesi = fluentWait(By.cssSelector("select.inputBox2"));
        } else if (tur.compareToIgnoreCase("karma") == 0) {
            paketListesi = fluentWait(By.cssSelector("select#mix"));

        } else if (tur.compareToIgnoreCase("data") == 0) {
            paketListesi = fluentWait(By.xpath(".//*[@id='salePackageId']/table/tbody/tr[3]/td[text()=\"Data Paketleri\"]/following-sibling::td/div/select"));

        } else {
            paketListesi = fluentWait(By.xpath(".//*[@id='salePackageId']/table/tbody/tr/td[text()='" + tur + "']/following-sibling::td/div/select"));

        }
        WebElement pktName = fluentWait(By.cssSelector("option[value='newOnnetPack.vas']"));//just to wait page load
        Thread.sleep(2000);
        Select ses = new Select(paketListesi);
        ses.selectByVisibleText(pack);
        try {

            WebElement tcID = driver.findElement(By.cssSelector("#recordoperations"));
            Select dropdown = new Select(tcID);
            dropdown.selectByIndex(1);
            if (!TCID.isEmpty()){
                dropdown.selectByIndex(2);
                Thread.sleep(2000);
                driver.findElement(By.cssSelector("#TC\\20 Kimlik\\20 NO")).sendKeys(TCID);
            }
        } catch (Exception e) {
            System.out.println("TC ID not needed or catched exception. skipping");
            //System.err.println("TC id girme hatasi " + e.getLocalizedMessage() + e.getMessage());
            //e.printStackTrace();
        }
        WebElement aktifBtn = driver.findElement(By.cssSelector("input[value='Aktif Et']"));
        Thread.sleep(2000);
        aktifBtn.click();
        Thread.sleep(2000);
        driver.switchTo().alert().accept();
        fluentWait(By.className("clsInputLabel"));
        Thread.sleep(2000);
        List<WebElement> messages = driver.findElements(By.className("clsInputLabel"));
        /*for (WebElement ms : messages) {
            System.out.println(ms.getText());
            Assert.assertFalse("Hata mesaji bulundu:" + ms.getText(), ms.getText().contains("Hata:"));
        }*/
        // String message1 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[1]/td/font")).getText();
        //  Assert.assertTrue("Kayit Mesaji yanlis:" + message1, message1.contentEquals("  * Paket satışı başarıyla yapıldı."));
        String message2 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[2]/td/font")).getText();

        String gun = addDay();
        NonNtyetersizBakiyeKayitSMS = NonNtyetersizBakiyeKayitSMS.replaceAll("<BONUS_END_DATE>", gun);
        NonNtyetersizBakiyeKayitSMS = NonNtyetersizBakiyeKayitSMS.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
        NonNtyetersizBakiyeKayitSMS = NonNtyetersizBakiyeKayitSMS.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
        NonNtyetersizBakiyeKayitSMS = NonNtyetersizBakiyeKayitSMS.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

        System.out.println("Beklenen NonNT yetersizBakiyeKayitSMS i: " + NonNtyetersizBakiyeKayitSMS);
        String pattern = ".*" + NonNtyetersizBakiyeKayitSMS + "\\.?\\.?\\.?\\s*";
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(message2);
        System.out.println("Beklenen regex Patterni:" + pattern);

        Assert.assertTrue("NonNT_yetersizBakiyeKayit Mesaji yanlis..:" + message2, m.matches());

        //Thread.sleep(30000);
        // driver.close();
        //driver.quit();
        System.out.println("\n test6 NonNT yetersizBakiyeKayit bitti");

       /* } catch (Exception e) {
            System.err.println(e.getLocalizedMessage() + e.getMessage());
            e.printStackTrace();
            // driver.close();
            //  driver.quit();
        }*/
    }

       //waiting function. may need java 1.8+.
    public WebElement fluentWait(final By locator) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(25, TimeUnit.SECONDS)
                .pollingEvery(2, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class);

        WebElement foo = wait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(locator);
            }
        });

        return foo;
    }

    //@Test
    public String addDay() {
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DAY_OF_YEAR, Integer.parseInt(day));
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = today.getTime();
        String r = dateFormat.format(date);
        //System.out.println(r);


        return r;
    }

    public void regTest() {
        System.out.println(kalanSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy",addDay()));
        System.out.println(kalanSMS);
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0); // same for minutes and seconds
        System.out.println("today: " + today.getTime());
        today.add(Calendar.DAY_OF_YEAR, 30);
        System.out.println("today2: " + today.getTime());

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = today.getTime();
        System.out.println(dateFormat.format(date));


        String gun = dateFormat.format(date);

        System.out.println("gun: " + gun);


        String kayitSMS = "Paketiniz hattiniza basariyla tanimlanmistir. Bal 300 Paketi ile 1 ay boyunca yurtici her yone 300 dakika konusabilirsiniz.Kullanim suresi sonunda hattinizda yeterli bakiye olmasi halinde paketiniz otomatik olarak yenilenecektir.Paketinizi iptal etmek icin IPTAL yazip 2070e gonderin";
        String pattern = "  Islem için gerekli bütün kontroller basari ile yapilmistir. " + "\\s*" + kayitSMS + "\\.?\\.?\\.?\\s*";
        String testStr = "  Islem için gerekli bütün kontroller basari ile yapilmistir. Paketiniz hattiniza basariyla tanimlanmistir. Bal 300 Paketi ile 1 ay boyunca yurtici her yone 300 dakika konusabilirsiniz.Kullanim suresi sonunda hattinizda yeterli bakiye olmasi halinde paketiniz otomatik olarak yenilenecektir.Paketinizi iptal etmek icin IPTAL yazip 2070e gonderin";
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(testStr);
        if (m.matches()) {
            System.out.println("Found value: " + m.group(0));
        } else {
            System.out.println("NO MATCH");
        }

    }
}


