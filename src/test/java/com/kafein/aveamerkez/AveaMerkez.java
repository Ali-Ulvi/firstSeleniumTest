package com.kafein.aveamerkez;

import com.google.common.base.Function;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.*;

import java.io.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;


/**
 * Created by Ali Ulvi Talipoglu 071428 via Kafein on 26.10.2016.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AveaMerkez {
    static String user, pw, url, pack, kayitSms, gecko, tur, msisdn, day, iptalSMS, kalanSMS, yetersizBakiyemsisdn, yetersizBakiyeKayitSMS; //Config.txt icin degiskenler
    static String master, NonNtyetersizBakiyeKayitSMS, NonNtyetersizBakiyeMsisdn, NonNT_msisdn, NonNT_Kayit_Mesaji, TCID,NT_Fiyat,NonNT_Fiyat,NT_servis,NonNT_servis,bakiyesizAlim;
    static String ip,mrte1Pw,temizle,NonNT_Kayit_Mesaji2,kayitSms2;
    static WebDriver driver;
    static Sil sil,silNonNT;

 @BeforeClass
    public static void loadProps() {

        System.out.println("@BeforeClass: onceExecutedBeforeAll to read config");
        Properties prop = new Properties();
        InputStream input = null;
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));

        try {
            try {
                FileReader reader = new FileReader("Config.txt");
                BufferedReader bufferedReader = new BufferedReader(reader);

                String line;
                FileWriter writer = new FileWriter("AUTO-Generated-File_Config_ingilizce_karakterli.txt", false);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                while ((line = bufferedReader.readLine()) != null) {
                    bufferedWriter.write(StringUtils.replaceChars(line,"ı’üşöçİ;ğÜŞÖÇĞ","i'usocI,gUSOCG"));
                    bufferedWriter.newLine();
                }
                reader.close();
                bufferedWriter.close();
                input = new FileInputStream("AUTO-Generated-File_Config_ingilizce_karakterli.txt");

                System.out.println( "File_Config_ingilizce_karakterliye cevrildi");
            } catch (IOException e) {
                e.printStackTrace();
                input = new FileInputStream("Config.txt");
                System.out.println( "File_Config_ingilizceye cevirme sirasinda hata");

            }

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            user = prop.getProperty("user");
            pw = prop.getProperty("sifre");
            url = prop.getProperty("url");
            pack = prop.getProperty("Paket_Gorunen_ismi");
            kayitSms = prop.getProperty("NT_Kayit_Mesaji");
            kayitSms2 = prop.getProperty("NT_Kayit_Mesaji2");
            gecko = prop.getProperty("Firefox_Driver_Path");
            tur = prop.getProperty("Paket_Turu");
            msisdn = prop.getProperty("NTmsisdn");
            day = prop.getProperty("paket_kac_gunluk");
            iptalSMS = prop.getProperty("iptalSMS");
            kalanSMS = prop.getProperty("kalanSMS");
            yetersizBakiyemsisdn = prop.getProperty("NT_yetersizBakiye_Msisdn");
            yetersizBakiyeKayitSMS = prop.getProperty("NT_yetersizBakiye_KayitSMS");
            NonNtyetersizBakiyeKayitSMS = prop.getProperty("NONNT_yetersizBakiye_KayitSMS");
            NonNtyetersizBakiyeMsisdn = prop.getProperty("NONNT_yetersizBakiye_Msisdn");
            NonNT_msisdn = prop.getProperty("NonNT_msisdn");
            NonNT_Kayit_Mesaji = prop.getProperty("NonNT_Kayit_Mesaji");
            NonNT_Kayit_Mesaji2 = prop.getProperty("NonNT_Kayit_Mesaji2");
            TCID = prop.getProperty("TCID");
            NonNT_Fiyat = prop.getProperty("NonNT_Fiyat");
            NT_Fiyat = prop.getProperty("NT_Fiyat");
            NT_servis = prop.getProperty("yenilemeli_ise_NT_servis_ismi");
            NonNT_servis = prop.getProperty("yenilemeli_ise_NonNT_servis_ismi");
            bakiyesizAlim = prop.getProperty("Yenilemeli_ise_Bakiyesiz_Alim_var_mi");
            ip = prop.getProperty("mrte1_IP");
            mrte1Pw = prop.getProperty("mrte1_Sifre");
            temizle = prop.getProperty("Aboneler_Once_Temizlensin_mi");
            Enumeration<?> e = prop.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String value = prop.getProperty(key);
                if (!key.contentEquals("sifre"))
                    System.out.println( key + " : " + value);
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
    
    @BeforeClass
    public static void eraser(){
        sil=new Sil(msisdn);
        silNonNT=new Sil(NonNT_msisdn);
                sil.start();
                silNonNT.start();
    }
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
        driver.navigate().to(url);

    }

    @AfterClass
    public static void clean() throws IOException {

       // driver.close();
        //driver.quit();

        try {
            Runtime.getRuntime().exec("taskkill /F /IM geckodriver.exe ");
        } catch (IOException e) {
            System.out.println("kill driver exception");
        }
    }

    //We should add @Test annotation that JUnit will run below method
    @Test
    //Start to write our test method. It should ends with "Test"
    public void test1paketAlim() throws Exception {


        //  driver = new RemoteWebDriver(new URL("http://127.0.0.1:9515"), DesiredCapabilities.chrome());

        //Step 2- Navigation: Open a website


        //Step 3- Assertion: Check its title is correct
        //assertEquals method Parameters: Message, Expected Value, Actual Value
        // Assert.assertEquals("AveaMerkez", driver.getTitle());

        try {
            driver.findElement(By.name("userName")).sendKeys(user);
            driver.findElement(By.name("password")).sendKeys(pw);
            driver.findElement(By.name("sendButton")).click();
        } catch (Exception e) {
            System.err.println("pw girme hatasi ");
        }
        WebElement logout = fluentWait(By.id("logout"));
        driver.navigate().to(url);
        WebElement txtBox = fluentWait(By.cssSelector("#searchmsisdn"));
        // WebElement txtBox = driver.findElement(By.cssSelector("#searchmsisdn"));
        WebElement guncelle = driver.findElement(By.cssSelector("input[value='Paketleri Guncelle']"));
        guncelle.click();
        //waitForJStoLoad();
        WebDriverWait wait = new WebDriverWait(driver, 89,200);
       // wait.until(new page_loaded("#searchmsisdn", 1));
        wait.until(ExpectedConditions.stalenessOf(guncelle));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[value='Paketleri Guncelle']")));
        sil.join(4*60*1000);
        txtBox = fluentWait(By.cssSelector("#searchmsisdn"));
        //try {

        WebElement paketListesi = null;

        txtBox.sendKeys(msisdn);
        Thread.sleep(1000);

        WebElement tamam = driver.findElement(By.cssSelector("input[value=Tamam]"));
        tamam.click();
        Thread.sleep(2000);

        if (driver.findElements(By.className("clsInputLabel")).size() > 0 && fluentWait(By.className("clsInputLabel")).getText().contains("Hata")) {
            tamam = driver.findElement(By.cssSelector("input[value=Tamam]"));
            tamam.click();
            Thread.sleep(2000);
        }
        BigDecimal money=getMoney(msisdn);
        Select types = new Select(fluentWait(By.cssSelector("select#types")));
        if (tur.compareToIgnoreCase("ses") == 0) {

            types.selectByVisibleText("Ses Paketleri");
        } else if (tur.compareToIgnoreCase("karma") == 0) {
            types.selectByVisibleText("Karma Paketler");

        } else if (tur.compareToIgnoreCase("data") == 0) {
            types.selectByVisibleText("Data Paketleri");

        } else {
            types.selectByVisibleText(tur);
        }

        // WebElement pktName = fluentWait(By.cssSelector("option[value='newOnnetPack.vas']"));//just to wait page load
        //Thread.sleep(2000);
        Select ses1 = new Select(fluentWait(By.cssSelector("#populated")));
        System.out.println("paket list size before" + ses1.getOptions().size());

        //WebDriverWait wait = new WebDriverWait(driver, 66);

        WebDriverWait wait2 = new WebDriverWait(driver, 90,3000);
        wait2.until(new ElementPopulatedByFilter("#populated", ses1.getOptions().size()-1));

        paketListesi = fluentWait(By.cssSelector("#populated"));

        Select ses = new Select(paketListesi);
        Thread.sleep(1700);
        System.out.println("paket list size after" + ses.getOptions().size());


        ses.selectByVisibleText(pack);
        Thread.sleep(3000);
        try {

            WebElement tcID = driver.findElement(By.cssSelector("#recordoperations"));
            Select dropdown = new Select(tcID);
            dropdown.selectByIndex(1);
            if (!TCID.isEmpty()) {
                dropdown.selectByIndex(2);
                Thread.sleep(2000);
                driver.findElement(By.cssSelector("#TC\\20 Kimlik\\20 NO")).sendKeys(TCID);
            }
        } catch (Exception e) {
            System.out.println("TC ID not needed or exceptioned.");
            Assert.assertTrue("TC ID girilirken hata. TC ID kutusu cikmiyor olabilir", TCID.isEmpty());
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
            Assert.assertFalse("Hata mesaji bulundu:" + ms.getText(), ms.getText().contains("Hata:"));
        }
        BigDecimal moneyAfter=getMoney(msisdn);
        BigDecimal fiyat=money.subtract(moneyAfter) ;
        Assert.assertEquals(NT_Fiyat+" TL dusmesi gerekirken "+fiyat+" TL dustu. Servis veya fiyati yanlis..",Double.parseDouble(new BigDecimal(NT_Fiyat).toString()) ,Double.parseDouble(fiyat.toString()),0);

        String message1 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[1]/td/font")).getText();
        Assert.assertTrue("Kayit Mesaji yanlis:" + message1, message1.contentEquals("  * Paket satışı başarıyla yapıldı."));
        String message2 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[2]/td/font")).getText();
        checkService(msisdn,NT_servis,"ACTIVE/STD/STD");
        String gun = addDay();
        kayitSms=kayitSms+kayitSms2;
        kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);
        kayitSms = kayitSms.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
        kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
        kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

        System.out.println("Beklenen kayit SMSi: \n" + kayitSms);
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
        int cnt = 0;
        while (driver.getWindowHandles().size() == 1) {
            Thread.sleep(250);
            if (cnt > 44) break;
        }
        System.out.println(driver.getWindowHandles().size());
        Set<String> windowId = driver.getWindowHandles();    // get  window id of current window
        Iterator<String> itererator = windowId.iterator();
        String mainWinID = itererator.next();
        String newAdwinID = itererator.next();
        driver.switchTo().window(newAdwinID);
        //System.out.println(fluentWait(By.cssSelector("body > table > tbody > tr:nth-child(3) > td:nth-child(1) > b")).getText());
        String sms=fluentWait(By.xpath("/html/body/table/tbody/tr[3]/td[3]/kalan")).getText();
        Assert.assertEquals(kalanSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy", addDay()), sms);
        driver.close();
        driver.switchTo().window(master);  // switch back to parent window
        System.out.println(sms+"\n test2 paket kalan bitti");

    }


    @Test
    public void test3_iptal() throws Exception {

        assumeTrue(!iptalSMS.isEmpty());
        Set<String> windowId = driver.getWindowHandles();    // get  window id of current window
        Iterator<String> itererator = windowId.iterator();
        String mainWinID = itererator.next();
        driver.switchTo().window(mainWinID);
        WebElement chck = fluentWait(By.xpath(".//td[text()=\"" + pack + "\"]/preceding-sibling::td/input"));
        if (chck.getAttribute("checked") == null) chck.click();
        driver.findElement(By.cssSelector("input[onclick='deactivatePackets();']")).click();
        Thread.sleep(2000);
        driver.switchTo().alert().accept();
        new WebDriverWait(driver, 30).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(".//td[text()=\"" + pack + "\"]/preceding-sibling::td/input")));
        Thread.sleep(3000);
        fluentWait(By.className("clsInputLabel"));
        checkServiceNotExist(msisdn,NT_servis);
        List<WebElement> messages = driver.findElements(By.className("clsInputLabel"));
        for (WebElement ms : messages) {
            System.out.println(ms.getText());
            Assert.assertFalse("Hata mesaji bulundu:" + ms.getText(), ms.getText().contains("Hata:"));
        }
        String message1 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[1]/td/font")).getText();
        Assert.assertEquals("Iptal Mesaji Yanlis", message1, "  * " + pack + " paketinin iptali başarıyla yapıldı.");

        String message2 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[2]/td/font")).getText();
        String gun = addDay();
        iptalSMS = iptalSMS.replace("{sysdate+30}", gun);
        iptalSMS = iptalSMS.replace(" kadar ", " kadar \\s?");
        iptalSMS = iptalSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy|<BONUS_END_DATE>", gun);

        System.out.println("INFO: Beklenen iptal SMSi : " + iptalSMS);
        String pattern = "\\s*Islem için gerekli bütün kontroller basari ile yapilmistir. " + "\\s*" + iptalSMS + "\\.?\\.?\\.?\\s*";
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);
        // Now create matcher object.
        Matcher m = r.matcher(message2);
        System.out.println("INFO: beklenen regex: " + pattern);
        Assert.assertTrue("Iptal Mesaji yanlis..:" + message2, m.matches());
        System.out.println("\n test3 paket iptal bitti");
    }

    @Test
    public void test4_yetersizBakiye_paketAlim() throws Exception {

        assumeTrue(!yetersizBakiyeKayitSMS.isEmpty() && !yetersizBakiyemsisdn.isEmpty());
        try {
            driver.findElement(By.name("userName")).sendKeys(user);
            driver.findElement(By.name("password")).sendKeys(pw);
            driver.findElement(By.name("sendButton")).click();
            WebElement logout = fluentWait(By.id("logout"));
            driver.navigate().to(url);
            WebElement txtBox = fluentWait(By.cssSelector("#searchmsisdn"));
        } catch (Exception e) {
            System.err.println("pw girme hatasi ");
            // driver.close();
            //driver.quit();
        }



        Set<String> windowId = driver.getWindowHandles();    // get  window id of current window
        Iterator<String> itererator = windowId.iterator();
        String mainWinID = itererator.next();
        driver.switchTo().window(mainWinID);
        //Step 2- Navigation: Open a website
        //driver.navigate().to(url);

        WebElement txtBox;
        // WebElement txtBox = driver.findElement(By.cssSelector("#searchmsisdn"));
        txtBox = fluentWait(By.cssSelector("#searchmsisdn"));
        //try {

        WebElement paketListesi;
        txtBox.clear();
        txtBox.sendKeys(yetersizBakiyemsisdn);
        Thread.sleep(2000);
        WebElement tamam = driver.findElement(By.cssSelector("input[value=Tamam]"));
        tamam.click();
        Thread.sleep(1000);
        fluentWait(By.cssSelector("#loader[style='']"));
        Thread.sleep(2000);
        fluentWait(By.cssSelector("#loader[style='display: none;']"));

        Select types = new Select(fluentWait(By.cssSelector("select#types")));
        if (tur.compareToIgnoreCase("ses") == 0) {

            types.selectByVisibleText("Ses Paketleri");
        } else if (tur.compareToIgnoreCase("karma") == 0) {
            types.selectByVisibleText("Karma Paketler");

        } else if (tur.compareToIgnoreCase("data") == 0) {
            types.selectByVisibleText("Data Paketleri");

        } else {
            types.selectByVisibleText(tur);
        }
        Thread.sleep(2000);
        WebDriverWait wait = new WebDriverWait(driver, 66);


        // WebElement pktName = fluentWait(By.cssSelector("option[value='newOnnetPack.vas']"));//just to wait page load
        //Thread.sleep(2000);


        Select ses1 = new Select(fluentWait(By.cssSelector("#populated")));
        System.out.println("paket list size before" + ses1.getOptions().size());
        WebDriverWait wait2 = new WebDriverWait(driver, 90,3000);
        wait2.until(new ElementPopulatedByFilter("#populated", ses1.getOptions().size()-1));

        paketListesi = fluentWait(By.cssSelector("#populated"));

        Select ses = new Select(paketListesi);
        Thread.sleep(1500);
        System.out.println("paket list size after" + ses.getOptions().size());


        ses.selectByVisibleText(pack);
        Thread.sleep(3000);
        try {

            WebElement tcID = driver.findElement(By.cssSelector("#recordoperations"));
            Select dropdown = new Select(tcID);
            dropdown.selectByIndex(1);
            if (!TCID.isEmpty()) {
                dropdown.selectByIndex(2);
                Thread.sleep(2000);
                driver.findElement(By.cssSelector("#TC\\20 Kimlik\\20 NO")).sendKeys(TCID);
            }
        } catch (Exception e) {
            System.out.println("TC ID not needed or catched exception.");
            Assert.assertTrue("TC ID girilirken hata. TC ID kutusu cikmiyor olabilir", TCID.isEmpty());
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
        //List<WebElement> messages = driver.findElements(By.className("clsInputLabel"));
        /*for (WebElement ms : messages) {
            System.out.println(ms.getText());
            Assert.assertFalse("Hata mesaji bulundu:" + ms.getText(), ms.getText().contains("Hata:"));
        }*/
        // String message1 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[1]/td/font")).getText();
        //  Assert.assertTrue("Kayit Mesaji yanlis:" + message1, message1.contentEquals("  * Paket satışı başarıyla yapıldı."));
        String message2 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[2]/td/font")).getText();
        if (bakiyesizAlim.equalsIgnoreCase("evet"))
        checkService(yetersizBakiyemsisdn,NT_servis,"PASSIVE/STD/FOLLOW");
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

        assumeTrue(!NonNT_Kayit_Mesaji.isEmpty() && !NonNT_msisdn.isEmpty());

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
        WebDriverWait wait = new WebDriverWait(driver, 66,200);
        wait.until(new page_loaded("#searchmsisdn", 1));
        silNonNT.join(4*60*1000);
        txtBox = fluentWait(By.cssSelector("#searchmsisdn"));
        //try {

        WebElement paketListesi = null;
        txtBox.sendKeys(NonNT_msisdn);
        Thread.sleep(2000);

        WebElement tamam = driver.findElement(By.cssSelector("input[value=Tamam]"));
        tamam.click();
        Thread.sleep(2000);
        BigDecimal money=getMoney(NonNT_msisdn);
        if (driver.findElements(By.className("clsInputLabel")).size() > 0 && fluentWait(By.className("clsInputLabel")).getText().contains("Hata")) {
            tamam = driver.findElement(By.cssSelector("input[value=Tamam]"));
            tamam.click();
            Thread.sleep(2000);
        }
        Select types = new Select(fluentWait(By.cssSelector("select#types")));
        if (tur.compareToIgnoreCase("ses") == 0) {

            types.selectByVisibleText("Ses Paketleri");
        } else if (tur.compareToIgnoreCase("karma") == 0) {
            types.selectByVisibleText("Karma Paketler");

        } else if (tur.compareToIgnoreCase("data") == 0) {
            types.selectByVisibleText("Data Paketleri");

        } else {
            types.selectByVisibleText(tur);
        }


        // WebElement pktName = fluentWait(By.cssSelector("option[value='newOnnetPack.vas']"));//just to wait page load
        //Thread.sleep(2000);


        Select ses1 = new Select(fluentWait(By.cssSelector("#populated")));
        System.out.println("paket list size before" + ses1.getOptions().size());
         wait = new WebDriverWait(driver, 66);
        WebDriverWait wait2 = new WebDriverWait(driver, 90,3000);
        wait2.until(new ElementPopulatedByFilter("#populated", ses1.getOptions().size()-1));
        paketListesi = fluentWait(By.cssSelector("#populated"));
        Select ses = new Select(paketListesi);
        Thread.sleep(1500);
        System.out.println("paket list size after" + ses.getOptions().size());
        ses.selectByVisibleText(pack);
        Thread.sleep(3000);
        try {

            WebElement tcID = driver.findElement(By.cssSelector("#recordoperations"));
            Select dropdown = new Select(tcID);
            dropdown.selectByIndex(1);
            if (!TCID.isEmpty()) {
                dropdown.selectByIndex(2);
                Thread.sleep(2000);
                driver.findElement(By.cssSelector("#TC\\20 Kimlik\\20 NO")).sendKeys(TCID);
            }
        } catch (Exception e) {
            System.out.println("TC ID not needed or catched exception.");
            Assert.assertTrue("TC ID girilirken hata. TC ID kutusu cikmiyor olabilir", TCID.isEmpty());
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
            Assert.assertFalse("Hata mesaji bulundu:" + ms.getText(), ms.getText().contains("Hata:"));
        }
        BigDecimal moneyAfter=getMoney(NonNT_msisdn);
        BigDecimal fiyat=money.subtract(moneyAfter);

        Assert.assertEquals(NonNT_Fiyat+" TL dusmesi gerekirken "+fiyat+" TL dustu. Servis veya fiyati yanlis..",Double.parseDouble(new BigDecimal(NonNT_Fiyat).toString()) ,Double.parseDouble(fiyat.toString()),0);
        String message1 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[1]/td/font")).getText();
        Assert.assertTrue("Kayit Mesaji yanlis:" + message1, message1.contentEquals("  * Paket satışı başarıyla yapıldı."));
        String message2 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[2]/td/font")).getText();
        checkService(NonNT_msisdn,NonNT_servis,"ACTIVE/STD/STD");

        String gun = addDay();
        NonNT_Kayit_Mesaji = NonNT_Kayit_Mesaji+NonNT_Kayit_Mesaji2;
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
        fluentWait(By.xpath(".//td[text()=\"" + pack + "\"]/preceding-sibling::td/input")).click();
        master = driver.getWindowHandle();
        fluentWait(By.xpath(".//td[text()=\"" + pack + "\"]/following-sibling::td/input")).click();
        int cnt = 0;
        while (driver.getWindowHandles().size() == 1) {
            Thread.sleep(250);
            if (cnt > 44) break;
        }
        System.out.println("Window Count:"+driver.getWindowHandles().size());
        Set<String> windowId = driver.getWindowHandles();    // get  window id of current window
        Iterator<String> itererator = windowId.iterator();
        String mainWinID = itererator.next();
        String newAdwinID = itererator.next();
        driver.switchTo().window(newAdwinID);
        //System.out.println(fluentWait(By.cssSelector("body > table > tbody > tr:nth-child(3) > td:nth-child(1) > b")).getText());
        String sms=fluentWait(By.xpath("/html/body/table/tbody/tr[3]/td[3]/kalan")).getText();
        Assert.assertEquals(kalanSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy", addDay()), sms);
        driver.switchTo().window(master);  // switch back to parent window
        System.out.println(sms+"\n test6 NonNt paket kalan bitti");

    }

    @Test
    public void test7_NonNT_iptal() throws Exception {

        assumeTrue(!iptalSMS.isEmpty());
        Set<String> windowId = driver.getWindowHandles();    // get  window id of current window
        Iterator<String> itererator = windowId.iterator();
        String mainWinID = itererator.next();
        driver.switchTo().window(mainWinID);
        WebElement chck = fluentWait(By.xpath(".//td[text()=\"" + pack + "\"]/preceding-sibling::td/input"));
        if (chck.getAttribute("checked") == null) chck.click();
        driver.findElement(By.cssSelector("input[onclick='deactivatePackets();']")).click();
        Thread.sleep(2000);
        driver.switchTo().alert().accept();
        new WebDriverWait(driver, 66).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(".//td[text()=\"" + pack + "\"]/preceding-sibling::td/input")));
        Thread.sleep(3000);
        fluentWait(By.className("clsInputLabel"));

        checkServiceNotExist(NonNT_msisdn,NonNT_servis);
        List<WebElement> messages = driver.findElements(By.className("clsInputLabel"));
        for (WebElement ms : messages) {
            System.out.println(ms.getText());
            Assert.assertFalse("Hata mesaji bulundu:" + ms.getText(), ms.getText().contains("Hata:"));
        }
        String message1 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[1]/td/font")).getText();
        Assert.assertEquals("Iptal Mesaji Yanlis", message1, "  * " + pack + " paketinin iptali başarıyla yapıldı.");

        String message2 = driver.findElement(By.xpath(".//*[@id='leftPane']/table/tbody/tr[2]/td/font")).getText();
        String gun = addDay();
        iptalSMS = iptalSMS.replace("{sysdate+30}", gun);
        iptalSMS = iptalSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy|<BONUS_END_DATE>", gun);

        System.out.println("INFO: Beklenen iptal SMSi : " + iptalSMS);
        String pattern = "\\s*Islem için gerekli bütün kontroller basari ile yapilmistir. " + "\\s*" + iptalSMS + "\\.?\\.?\\.?\\s*";
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);
        // Now create matcher object.
        Matcher m = r.matcher(message2);
        System.out.println("INFO: beklenen regex: " + pattern);
        Assert.assertTrue("NonNT Iptal Mesaji yanlis..:" + message2, m.matches());
        System.out.println("\n test7 NonNT paket iptal bitti");
    }

    @Test
    public void test8_NonNT_yetersizBakiye_paketAlim() throws Exception {

        assumeTrue(!NonNtyetersizBakiyeKayitSMS.isEmpty() && !NonNtyetersizBakiyeMsisdn.isEmpty());
        try {
            driver.findElement(By.name("userName")).sendKeys(user);
            driver.findElement(By.name("password")).sendKeys(pw);
            driver.findElement(By.name("sendButton")).click();
            WebElement logout = fluentWait(By.id("logout"));
            driver.navigate().to(url);
            WebElement txtBox = fluentWait(By.cssSelector("#searchmsisdn"));
        } catch (Exception e) {
            System.err.println("pw girme hatasi.. geciliyor "  );

            // driver.close();
            //driver.quit();
        }
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
        //loading screen shown (waiting)
        fluentWait(By.cssSelector("#loader[style='']"));
        Thread.sleep(2000);
        //loading screen closed (waiting)
        fluentWait(By.cssSelector("#loader[style='display: none;']"));
        Thread.sleep(2000);
        Select types = new Select(fluentWait(By.cssSelector("select#types")));
        if (tur.compareToIgnoreCase("ses") == 0) {

            types.selectByVisibleText("Ses Paketleri");
        } else if (tur.compareToIgnoreCase("karma") == 0) {
            types.selectByVisibleText("Karma Paketler");

        } else if (tur.compareToIgnoreCase("data") == 0) {
            types.selectByVisibleText("Data Paketleri");

        } else {
            types.selectByVisibleText(tur);
        }
        Thread.sleep(2000);
        WebDriverWait wait = new WebDriverWait(driver, 86);
        Select ses1 = new Select(fluentWait(By.cssSelector("#populated")));
        System.out.println("paket list size before" + ses1.getOptions().size());
        WebDriverWait wait2 = new WebDriverWait(driver, 40,2000);
        try {
            wait2.until(new ElementPopulatedByFilter("#populated", ses1.getOptions().size() - 1));
        }
        catch (Exception e){
            //aveamerkez or connection errors may have happened. trying again
              wait2 = new WebDriverWait(driver, 80,4000);
            wait2.until(new ElementPopulatedByFilter("#populated", ses1.getOptions().size() - 1));
        }
        paketListesi = fluentWait(By.cssSelector("#populated"));

        Select ses = new Select(paketListesi);
        Thread.sleep(1500);
        System.out.println("paket list size after" + ses.getOptions().size());


        ses.selectByVisibleText(pack);
        Thread.sleep(3000);
        try {

            WebElement tcID = driver.findElement(By.cssSelector("#recordoperations"));
            Select dropdown = new Select(tcID);
            dropdown.selectByIndex(1);
            if (!TCID.isEmpty()) {
                dropdown.selectByIndex(2);
                Thread.sleep(2000);
                driver.findElement(By.cssSelector("#TC\\20 Kimlik\\20 NO")).sendKeys(TCID);
            }
        } catch (Exception e) {
            System.out.println("TC ID not needed or catched exception.");
            Assert.assertTrue("TC ID girilirken hata. TC ID kutusu cikmiyor olabilir", TCID.isEmpty());
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
        if (bakiyesizAlim.equalsIgnoreCase("evet"))
        checkService(NonNtyetersizBakiyeMsisdn,NonNT_servis,"PASSIVE/STD/FOLLOW");

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

    //notused
    public boolean waitForJStoLoad() {

        WebDriverWait wait = new WebDriverWait(driver, 30);
        final JavascriptExecutor js = (JavascriptExecutor) driver;
        // wait for jQuery to load
        ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {

                    return ((Long) js.executeScript("return jQuery.active") == 0);
                } catch (Exception e) {
                    return true;
                }
            }
        };

        // wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return js.executeScript("return document.readyState").toString().equals("complete");
            }
        };

        return wait.until(jQueryLoad) && wait.until(jsLoad);
    }


    //waiting function. may need java 1.8+.
    public WebElement fluentWait(final By locator) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
                .withTimeout(35, TimeUnit.SECONDS)
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

    //@Test
    public void regTest() {
      /*  BigDecimal fiyat=new BigDecimal("30.000000") ;
        Assert.assertEquals(NonNT_Fiyat+" TL dusmesi gerekirken "+fiyat+" TL dustu. Servis veya fiyati yanlis..",Double.parseDouble(new BigDecimal(NonNT_Fiyat).toString()) ,Double.parseDouble(fiyat.toString()),0);

        System.out.println(kalanSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy", addDay()));
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


        */String kayitSMS = "Paketiniz hattiniza basariyla tanimlanmistir. Bal 300 Paketi ile 1 ay boyunca yurtici her yone 300 dakika konusabilirsiniz.Kullanim suresi sonunda hattinizda yeterli bakiye olmasi halinde paketiniz otomatik olarak yenilenecektir.Paketinizi iptal etmek icin IPTAL yazip 2070e gonderin";
        String pattern = "  Islem için gerekli bütün kontroller basari ile yapilmistir. " + "\\s*" + kayitSMS + "\\.?\\.?\\.?\\s*";
        String testStr = "  Islem için gerekli bütün kontroller basari ile yapilmistir. Paketiniz hattiniza basariyla tanimlanmistir. Bal 300 Paketi ile 1 ay boyunca yurtici her yone 300 dakika konusabilirsiniz.Kullanim suresi sonunda hattinizda yeterli bakiye olmasi halinde paketiniz otomatik olarak yenilenecektir.Paketinizi iptal etmek icin IPTAL yazip 2070e gonderin";
        // Create a Pattern object
        //Pattern r = Pattern.compile(pattern);
        Pattern r = Pattern.compile("[0-9]{2}+", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
testStr="500 dk";
        // Now create matcher object.
        Matcher m = r.matcher(testStr);
        if (m.matches()) {
            System.out.println("Found value: " + m.group(0));
        } else {
            System.out.println("NO MATCH");
        }

    }

    private class ElementContainsText implements ExpectedCondition<Boolean> {
        private String textToFind;
        private By findBy;

        //Constructor (Set the given values)
        public ElementContainsText(final By elementFindBy, final String textToFind) {
            this.findBy = elementFindBy;
            this.textToFind = textToFind;
        }


        //Override the apply method with your own functionality
        @Override
        public Boolean apply(WebDriver webDriver) {
            //Find the element with given By method (By CSS, XPaht, Name, etc.)
            WebElement element = webDriver.findElement(this.findBy);

            //Check that the element contains given text?
            if (element.getText().contains(this.textToFind)) {
                return true;
            } else {
                return false;
            }


        }

        //This is for log message. I override it because when test fails, it will give us a meaningful message.
        @Override
        public String toString() {
            return ": \"Does " + this.findBy + " contain " + this.textToFind + "?\"";
        }
    }

    private class ElementPopulatedByFilter implements ExpectedCondition<Boolean> {
        private int size;
        // private Select findBy;
        private String css;

        //Constructor (Set the given values)
        public ElementPopulatedByFilter(final String css, final int size) {
            this.css = css;
            this.size = size;
        }

        //Override the apply method with your own functionality
        @Override
        public Boolean apply(WebDriver webDriver) {

            try {
                //Check that the element size filtered
                if (new Select(fluentWait(By.cssSelector(css))).getOptions().size() <= this.size) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }

        }

        //This is for log message. I override it because when test fails, it will give us a meaningful message.
        @Override
        public String toString() {
            return "Paket listesi Yuklemesinde Sorun var?";
        }
    }

    private class page_loaded implements ExpectedCondition<Boolean> {
        private int size;
        // private Select findBy;
        private String css;

        //Constructor (Set the given values)
        public page_loaded(final String css, final int size) {
            this.css = css;
            this.size = size;
        }

        //Override the apply method with your own functionality
        @Override
        public Boolean apply(WebDriver webDriver) {

            try {
                //Check that the element size filtered
                driver.findElement(By.cssSelector(css));
                    return false;

            } catch (Exception e) {
                return true;
            }

        }

        //This is for log message. I override it because when test fails, it will give us a meaningful message.
        @Override
        public String toString() {
            return "Page Load beklerken hata";
        }
    }


    public String postSOAPXML(String msisdn) {
        String resp = null;
        try {

            String soapBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:avea=\"http://oslee.orga.com/bsg/ra/ws/avea\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <avea:GetQueryDataRequest>\n" +
                    "         <MiddlewareTransactionId>123456AUT</MiddlewareTransactionId>\n" +
                    "         <opChannel>BPM_WLI</opChannel>\n" +
                    "         <keyName>MSISDN</keyName>\n" +
                    "         <keyValue>0" + msisdn + "</keyValue>\n" +
                    "         <returnRealtimeData>true</returnRealtimeData>\n" +
                    "      </avea:GetQueryDataRequest>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";
            // Get target URL
            HttpClient httpclient = new DefaultHttpClient();

            StringEntity strEntity = new StringEntity(soapBody, "text/xml", "UTF-8");
            HttpPost post = new HttpPost("http://10.248.68.129:10003/getQueryData");
            post.setHeader("SOAPAction", "GetQueryData");
            post.setEntity(strEntity);

            // Execute request
            HttpResponse response = httpclient.execute(post);
            HttpEntity respEntity = response.getEntity();

            if (respEntity != null) {
                resp = EntityUtils.toString(respEntity);

                System.out.println(resp);
                Matcher m = Pattern.compile("PP_(?!NWS_|Tax)[^<]*")
                        .matcher(resp);
                System.out.println("uzerindeki paket(ler):");
                while (m.find()) {
                    System.out.println(m.group());
                }
                System.out.print("Bakiyesi: ");
                m = Pattern.compile("<balanceName>BLP_Main</balanceName><measure>TRY</measure><unitType>CUR</unitType><currency>TRY</currency><balance>([^<]*)")
                        .matcher(resp);
                while (m.find()) {
                    System.out.println("\n"+m.group(1));
                }
            } else {
                System.err.println("No Response");
            }

        } catch (Exception e) {
            System.err.println("WS SOAP exception = " + e.toString());
        }
        return resp;
    }

    public BigDecimal getMoney(String msisdn) {
        BigDecimal money = null;
        String resp = postSOAPXML(msisdn);
        Matcher m = Pattern.compile("<balanceName>BLP_Main</balanceName><measure>TRY</measure><unitType>CUR</unitType><currency>TRY</currency><balance>([^<]*)")
                .matcher(resp);
        while (m.find()) {
            money =new BigDecimal(m.group(1));
        }
        return money;
    }

    public void checkService(String msisdn, String servis, String state) {
        if (servis.isEmpty()) {
            System.out.println("INFO: Servis bos birakildigindan kontrol edilmiyor.");
            return;
        }
        String resp = postSOAPXML(msisdn);
        Matcher m = Pattern.compile("<soldProducts><productName>"+servis+"</productName><state>"+state+"</state><type>Package</type>")
                .matcher(resp);

            assumeTrue(servis+" "+state+" bulunamadi",m.find());

    }


    public void checkServiceNotExist(String msisdn, String servis) {
        if (servis.isEmpty()) {
            System.out.println("INFO: Servis bos birakildigindan kontrol edilmiyor.");
            return;
        }
        String resp = postSOAPXML(msisdn);
        Matcher m = Pattern.compile("<soldProducts><productName>"+servis+"</productName>").matcher(resp);
        assumeFalse(servis+" hala abone uzerinde",m.find());

        m = Pattern.compile("<Message>BSG_10000:SUCCESS</Message>").matcher(resp);
        assumeTrue("BSG_10000:SUCCESS alinamadi",m.find());

    }


}


//Hatalıysam Mail At: ulvital@gmail.com
