package com.kafein.aveamerkez;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

/**
 * Created by AUT via kafein on 19.11.2016.
 * Kısa numaraya SMS gönderip, müşteriye giden SMS'i loglardan kontrol ediyor.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AveaSMSKanali {
    static Sil2 sil, silNonNT;
    // static WebDriver driver;
    static SendSms sms = SendSms.sendSms;
    static Config c = Config.config;
    static Log log = Log.log;

    @BeforeClass
    public static void drive() throws MalformedURLException {
        //Step 1- Driver Instantiation: Instantiate driver object as FirefoxDriver


        sil = new Sil2(c.msisdn);
        silNonNT = new Sil2(c.NonNT_msisdn);
        sil.start();
        silNonNT.start();
        //System.setProperty("webdriver.gecko.driver", c.gecko);
        //System.setProperty("webdriver.chrome.driver", "C:\\geckodriver-v0.11.1-win64\\chromedriver.exe");

        //driver = new FirefoxDriver();

        //driver = new ChromeDriver();
      /*  ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);
      */

    }

    // @AfterClass
    public static void clean() {
        sms.driver.close();
        sms.driver.quit();
    }


    @Test
    public void Test01_KayitSMS() throws Exception {
        BigDecimal money = getMoney(c.msisdn);

        sil.join(4 * 60 * 1000);
        //silNonNT.join(4 * 60 * 1000);

        Log.log.startAsbLog(c.msisdn);
        if (!c.TCID.isEmpty())
            sms.sendSms(c.msisdn, c.kn, c.kw + " " + c.TCID);
        else
            sms.sendSms(c.msisdn, c.kn, c.kw);

        checkService(c.msisdn, c.NT_servis, "ACTIVE/STD/STD");


        BigDecimal moneyAfter = getMoney(c.msisdn);
        BigDecimal fiyat = money.subtract(moneyAfter);
        Assert.assertEquals(c.NT_Fiyat + " TL dusmesi gerekirken " + fiyat + " TL dustu. Servis veya fiyati yanlis.", Double.parseDouble(new BigDecimal(c.NT_Fiyat).toString()), Double.parseDouble(fiyat.toString()), 0);

        List<String> smsler = log.getAsbLog(c.msisdn);
        Iterator<String> iterator = smsler.iterator();

        String gun = addDay();
        String kayitSms = c.kayitSms;
        kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);
        kayitSms = kayitSms.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
        kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
        kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

        System.out.println("Beklenen kayit SMSi: \n" + kayitSms);
        //String kayitSMS="Paketiniz hattiniza basariyla tanimlanmistir. Bal 300 Paketi ile 1 ay boyunca yurtici her yone 300 dakika konusabilirsiniz.Kullanim suresi sonunda hattinizda yeterli bakiye olmasi halinde paketiniz otomatik olarak yenilenecektir.Paketinizi iptal etmek icin IPTAL yazip 2070e gonderin";
        String pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);
        String gidenSms1 = iterator.next();
        // Now create matcher object.
        Matcher m = r.matcher(gidenSms1);

        Assert.assertTrue("Kayit SMSi yanlis: " + gidenSms1, m.matches());

        if (iterator.hasNext()) {
            kayitSms = c.kayitSms2;
            kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);
            kayitSms = kayitSms.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
            kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
            kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

            System.out.println("Beklenen kayit SMSi2: \n" + kayitSms);
            //String kayitSMS="Paketiniz hattiniza basariyla tanimlanmistir. Bal 300 Paketi ile 1 ay boyunca yurtici her yone 300 dakika konusabilirsiniz.Kullanim suresi sonunda hattinizda yeterli bakiye olmasi halinde paketiniz otomatik olarak yenilenecektir.Paketinizi iptal etmek icin IPTAL yazip 2070e gonderin";
            pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";
            // Create a Pattern object
            r = Pattern.compile(pattern);
            String gidenSms2 = iterator.next();
            // Now create matcher object.
            m = r.matcher(gidenSms2);

            Assert.assertTrue("2. Kayit SMSi yanlis..:" + gidenSms2, m.matches());

        }else{
            Assert.assertEquals("2. Kayit SMSi gelmedi","",c.kayitSms2);
        }
        System.out.println("\n test1 Kayit SMS bitti");

    }

    @Test
    public void Test02_KalanSMS() throws Exception {
        assumeTrue(!c.kalanSMS.isEmpty());
        log.startPromoLog(c.msisdn);
        sms.sendSms(c.msisdn, c.kn, "KALAN " + c.kw);
        List<String> smsler = log.getPromoLog(c.msisdn);
        Iterator<String> iterator = smsler.iterator();
        String gidenSms1 = iterator.next();
        Assert.assertEquals(c.kalanSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy", addDay()), gidenSms1);

        if (iterator.hasNext()) {
            String gidenSms2 = iterator.next();
            System.err.println("Giden 2. SMS: " + gidenSms2);
            assumeTrue("Kalan sorgusunda Promo'dan birden fazla SMS gidiyor. Promo loguna bakiniz", false);

        }
        System.out.println(sms + "\n test2 paket kalan SMS bitti");

    }

    @Test
    public void Test03_iptalSMS() throws Exception {

        assumeTrue(!c.iptalSMS.isEmpty());
        Log.log.startAsbLog(c.msisdn);

        sms.sendSms(c.msisdn, c.kn, "IPTAL " + c.kw);
        String gun = addDay();
        String iptalSMS = c.iptalSMS;
        iptalSMS = iptalSMS.replace("{sysdate+30}", gun);
        iptalSMS = iptalSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy|<BONUS_END_DATE>", gun);

        System.out.println("Beklenen iptal SMSi: \n" + iptalSMS);
        //String kayitSMS="Paketiniz hattiniza basariyla tanimlanmistir. Bal 300 Paketi ile 1 ay boyunca yurtici her yone 300 dakika konusabilirsiniz.Kullanim suresi sonunda hattinizda yeterli bakiye olmasi halinde paketiniz otomatik olarak yenilenecektir.Paketinizi iptal etmek icin IPTAL yazip 2070e gonderin";
        String pattern = "\\s?\\s?\\s?" + iptalSMS + "\\.?\\.?\\.?\\s?\\s?\\s?";
        checkServiceNotExist(c.msisdn, c.NT_servis);

        List<String> smsler = log.getAsbLog(c.msisdn);
        Iterator<String> iterator = smsler.iterator();
        assertThat(iterator.next(), RegexMatcher.matchesRegex(pattern));

        if (iterator.hasNext()) {

            String gidenSms2 = iterator.next();
            System.err.println("iptalde giden 2. SMS: " + gidenSms2);
            assumeTrue("iptalde ASB'den birden fazla SMS gidiyor. ASB loguna bakiniz", false);

        }
        System.out.println("\n test3 iptal SMS bitti");

    }

    @Test
    public void Test04_iptal_edilecek_paketi_yoksa_SMSi() throws Exception {

        assumeTrue(!c.iptal_edilecek_paketi_yoksa_SMSi.isEmpty());
        Log.log.startAsbLog(c.msisdn);

        sms.sendSms(c.msisdn, c.kn, "IPTAL " + c.kw);
        checkServiceNotExist(c.msisdn, c.NT_servis);
        String gun = addDay();
        String iptalSMS = c.iptal_edilecek_paketi_yoksa_SMSi;
        iptalSMS = iptalSMS.replace("{sysdate+30}", gun);
        iptalSMS = iptalSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy|<BONUS_END_DATE>", gun);

        System.out.println("Beklenen iptal_edilecek_paketi_yoksa_SMSi: \n" + iptalSMS);
        String pattern = "\\s?\\s?\\s?" + iptalSMS + "\\.?\\.?\\.?\\s?\\s?\\s?";

        List<String> smsler = log.getAsbLog(c.msisdn);
        Iterator<String> iterator = smsler.iterator();
        assertThat(iterator.next(), RegexMatcher.matchesRegex(pattern));

        if (iterator.hasNext()) {

            String gidenSms2 = iterator.next();
            System.err.println("iptalde giden 2. SMS: " + gidenSms2);
            assumeTrue("iptalde ASB'den birden fazla SMS gidiyor. ASB loguna bakiniz", false);

        }
        System.out.println("\n test4 iptal_edilecek_paketi_yoksa_SMSi bitti");

    }

    @Test
    public void Test05_Pakete_birden_fazla_kez_KayitSMS() throws Exception {
        if (c.Paketi_zaten_var_SMSi.isEmpty()) {
            BigDecimal money = getMoney(c.msisdn);
            Log.log.startAsbLog(c.msisdn);
            if (!c.TCID.isEmpty())
                sms.sendSms(c.msisdn, c.kn, c.kw + " " + c.TCID);
            else
                sms.sendSms(c.msisdn, c.kn, c.kw);

            checkService(c.msisdn, c.NT_servis, "ACTIVE/STD/STD");
            BigDecimal moneyAfter = getMoney(c.msisdn);
            BigDecimal fiyat = money.subtract(moneyAfter);
            Assert.assertEquals(c.NT_Fiyat + " TL dusmesi gerekirken " + fiyat + " TL dustu. Servis veya fiyati yanlis.", Double.parseDouble(new BigDecimal(c.NT_Fiyat).toString()), Double.parseDouble(fiyat.toString()), 0);

            List<String> smsler = log.getAsbLog(c.msisdn);
            Iterator<String> iterator = smsler.iterator();

            String gun = addDay();
            String kayitSms = c.kayitSms;
            kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);
            kayitSms = kayitSms.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
            kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
            kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

            System.out.println("Beklenen kayit SMSi: \n" + kayitSms);
            String pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";
            // Create a Pattern object
            Pattern r = Pattern.compile(pattern);
            String gidenSms1 = iterator.next();
            // Now create matcher object.
            Matcher m = r.matcher(gidenSms1);
            Assert.assertTrue("Kayit SMSi yanlis: " + gidenSms1, m.matches());

            if (iterator.hasNext()) {
                kayitSms = c.kayitSms2;
                kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);
                kayitSms = kayitSms.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
                kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
                kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

                System.out.println("Beklenen kayit SMSi2: \n" + kayitSms);
                //String kayitSMS="Paketiniz hattiniza basariyla tanimlanmistir. Bal 300 Paketi ile 1 ay boyunca yurtici her yone 300 dakika konusabilirsiniz.Kullanim suresi sonunda hattinizda yeterli bakiye olmasi halinde paketiniz otomatik olarak yenilenecektir.Paketinizi iptal etmek icin IPTAL yazip 2070e gonderin";
                pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";
                // Create a Pattern object
                r = Pattern.compile(pattern);
                String gidenSms2 = iterator.next();
                // Now create matcher object.
                m = r.matcher(gidenSms2);

                Assert.assertTrue("2. Kayit SMSi yanlis..:" + gidenSms2, m.matches());

            }
            System.out.println("\n test5 birden fazla Kayit SMS bitti");

        } else {//1den fazla kez alamaz,
            BigDecimal money = getMoney(c.msisdn);


            Log.log.startAsbLog(c.msisdn);
            if (!c.TCID.isEmpty())
                sms.sendSms(c.msisdn, c.kn, c.kw + " " + c.TCID);
            else
                sms.sendSms(c.msisdn, c.kn, c.kw);

            checkService(c.msisdn, c.NT_servis, "ACTIVE/STD/STD");


            BigDecimal moneyAfter = getMoney(c.msisdn);
            BigDecimal fiyat = money.subtract(moneyAfter);
            Assert.assertEquals(0 + " TL dusmesi gerekirken " + fiyat + " TL dustu. Servis veya fiyati yanlis.", Double.parseDouble(new BigDecimal(0).toString()), Double.parseDouble(fiyat.toString()), 0);

            List<String> smsler = log.getAsbLog(c.msisdn);
            Iterator<String> iterator = smsler.iterator();

            String gun = addDay();
            String kayitSms = c.Paketi_zaten_var_SMSi;
            kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);
            kayitSms = kayitSms.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
            kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
            kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

            System.out.println("Beklenen kayit SMSi: \n" + kayitSms);
            String pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";

            // Create a Pattern object
            Pattern r = Pattern.compile(pattern);
            String gidenSms1 = iterator.next();
            // Now create matcher object.
            Matcher m = r.matcher(gidenSms1);

            Assert.assertTrue("Kayit SMSi yanlis: " + gidenSms1, m.matches());

            if (iterator.hasNext()) {
                kayitSms = c.kayitSms2;
                kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);
                kayitSms = kayitSms.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
                kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
                kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

                System.out.println("Beklenen kayit SMSi2: \n" + kayitSms);
                pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";
                // Create a Pattern object
                r = Pattern.compile(pattern);
                String gidenSms2 = iterator.next();
                // Now create matcher object.
                m = r.matcher(gidenSms2);

                Assert.assertTrue("2. Kayit SMSi yanlis..:" + gidenSms2, m.matches());

            }
            System.out.println("\n test5 birden fazla alamaz Kayit SMSi bitti");

        }
    }

    @Test
    public void Test06_Yetersiz_BakiyeKayitSMS() throws Exception {
        BigDecimal money = getMoney(c.yetersizBakiyemsisdn);

        Log.log.startAsbLog(c.yetersizBakiyemsisdn);
        if (!c.TCID.isEmpty())
            sms.sendSms(c.yetersizBakiyemsisdn, c.kn, c.kw + " " + c.TCID);
        else
            sms.sendSms(c.yetersizBakiyemsisdn, c.kn, c.kw);

        BigDecimal moneyAfter = getMoney(c.yetersizBakiyemsisdn);
        BigDecimal fiyat = money.subtract(moneyAfter);
        Assert.assertEquals(0 + " TL dusmesi gerekirken " + fiyat + " TL dustu. campaigns_rules_map_tab fiyati yanlis olabilir.", Double.parseDouble(new BigDecimal(0).toString()), Double.parseDouble(fiyat.toString()), 0);

        List<String> smsler = log.getAsbLog(c.yetersizBakiyemsisdn);
        Iterator<String> iterator = smsler.iterator();

        String gun = addDay();
        String kayitSms = c.yetersizBakiyeKayitSMS;
        kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);
        kayitSms = kayitSms.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
        kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
        kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

        System.out.println("Beklenen kayit SMSi: \n" + kayitSms);

        String pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);
        String gidenSms1 = iterator.next();
        // Now create matcher object.
        Matcher m = r.matcher(gidenSms1);

        Assert.assertTrue("Kayit SMSi yanlis: " + gidenSms1, m.matches());

        if (iterator.hasNext()) {
            kayitSms = c.kayitSms2;
            kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);
            kayitSms = kayitSms.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
            kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
            kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

            System.out.println("Beklenen kayit SMSi2: \n" + kayitSms);
            //String kayitSMS="Paketiniz hattiniza basariyla tanimlanmistir. Bal 300 Paketi ile 1 ay boyunca yurtici her yone 300 dakika konusabilirsiniz.Kullanim suresi sonunda hattinizda yeterli bakiye olmasi halinde paketiniz otomatik olarak yenilenecektir.Paketinizi iptal etmek icin IPTAL yazip 2070e gonderin";
            pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";
            // Create a Pattern object
            r = Pattern.compile(pattern);
            String gidenSms2 = iterator.next();
            // Now create matcher object.
            m = r.matcher(gidenSms2);

            Assert.assertTrue("2. Kayit SMSi yanlis..:" + gidenSms2, m.matches());

        }
        System.out.println("\n test6 yetersiz bakiye Kayit SMSi bitti");

    }

    @Test
    public void Test07_NonNT_KayitSMS() throws Exception {
        BigDecimal money = getMoney(c.NonNT_msisdn);

        silNonNT.join(4 * 60 * 1000);

        Log.log.startAsbLog(c.NonNT_msisdn);
        if (!c.TCID.isEmpty())
            sms.sendSms(c.NonNT_msisdn, c.kn, c.kwNonNt + " " + c.TCID);
        else
            sms.sendSms(c.NonNT_msisdn, c.kn, c.kwNonNt);

        checkService(c.NonNT_msisdn, c.NonNT_servis, "ACTIVE/STD/STD");


        BigDecimal moneyAfter = getMoney(c.NonNT_msisdn);
        BigDecimal fiyat = money.subtract(moneyAfter);
        Assert.assertEquals(c.NonNT_Fiyat + " TL dusmesi gerekirken " + fiyat + " TL dustu. Servis veya fiyati yanlis.", Double.parseDouble(new BigDecimal(c.NonNT_Fiyat).toString()), Double.parseDouble(fiyat.toString()), 0);

        List<String> smsler = log.getAsbLog(c.NonNT_msisdn);
        Iterator<String> iterator = smsler.iterator();

        String gun = addDay();
        String kayitSms = c.NonNT_Kayit_Mesaji;
        kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);
        kayitSms = kayitSms.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
        kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
        kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

        System.out.println("Beklenen kayit SMSi: \n" + kayitSms);
        String pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);
        String gidenSms1 = iterator.next();
        // Now create matcher object.
        Matcher m = r.matcher(gidenSms1);

        Assert.assertTrue("Kayit SMSi yanlis: " + gidenSms1, m.matches());

        if (iterator.hasNext()) {
            kayitSms = c.NonNT_Kayit_Mesaji2;
            kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);
            kayitSms = kayitSms.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
            kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
            kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

            System.out.println("Beklenen kayit SMSi2: \n" + kayitSms);
            //String kayitSMS="Paketiniz hattiniza basariyla tanimlanmistir. Bal 300 Paketi ile 1 ay boyunca yurtici her yone 300 dakika konusabilirsiniz.Kullanim suresi sonunda hattinizda yeterli bakiye olmasi halinde paketiniz otomatik olarak yenilenecektir.Paketinizi iptal etmek icin IPTAL yazip 2070e gonderin";
            pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";
            // Create a Pattern object
            r = Pattern.compile(pattern);
            String gidenSms2 = iterator.next();
            // Now create matcher object.
            m = r.matcher(gidenSms2);

            Assert.assertTrue("2. Kayit SMSi yanlis..:" + gidenSms2, m.matches());

        }
        System.out.println("\n test7 NonNt Kayit SMS bitti");

    }

    @Test
    public void Test08_NonNT_KalanSMS() throws Exception {
        assumeTrue(!c.kalanSMS.isEmpty());
        log.startPromoLog(c.NonNT_msisdn);
        sms.sendSms(c.NonNT_msisdn, c.kn, "KALAN " + c.kwNonNt);
        List<String> smsler = log.getPromoLog(c.NonNT_msisdn);
        Iterator<String> iterator = smsler.iterator();
        String gidenSms1 = iterator.next();
        Assert.assertEquals(c.kalanSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy", addDay()), gidenSms1);

        if (iterator.hasNext()) {
            String gidenSms2 = iterator.next();
            System.err.println("Giden 2. SMS: " + gidenSms2);
            assumeTrue("Kalan sorgusunda Promo'dan birden fazla SMS gidiyor. Promo loguna bakiniz", false);

        }
        System.out.println(sms + "\n test8 NonNt paket kalan SMS bitti");

    }

    @Test
    public void Test09_NonNT_Yetersiz_BakiyeKayitSMS() throws Exception {
        BigDecimal money = getMoney(c.NonNtyetersizBakiyeMsisdn);

        Log.log.startAsbLog(c.NonNtyetersizBakiyeMsisdn);
        if (!c.TCID.isEmpty())
            sms.sendSms(c.NonNtyetersizBakiyeMsisdn, c.kn, c.kw + " " + c.TCID);
        else
            sms.sendSms(c.NonNtyetersizBakiyeMsisdn, c.kn, c.kw);


        BigDecimal moneyAfter = getMoney(c.NonNtyetersizBakiyeMsisdn);
        BigDecimal fiyat = money.subtract(moneyAfter);
        Assert.assertEquals(0 + " TL dusmesi gerekirken " + fiyat + " TL dustu. campaigns_rules_map_tab fiyati yanlis olabilir.", Double.parseDouble(new BigDecimal(0).toString()), Double.parseDouble(fiyat.toString()), 0);

        List<String> smsler = log.getAsbLog(c.NonNtyetersizBakiyeMsisdn);
        Iterator<String> iterator = smsler.iterator();

        String gun = addDay();
        String kayitSms = c.NonNtyetersizBakiyeKayitSMS;
        kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);
        kayitSms = kayitSms.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
        kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
        kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

        System.out.println("Beklenen kayit SMSi: \n" + kayitSms);

        String pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);
        String gidenSms1 = iterator.next();
        // Now create matcher object.
        Matcher m = r.matcher(gidenSms1);

        Assert.assertTrue("Kayit SMSi yanlis: " + gidenSms1, m.matches());

        if (iterator.hasNext()) {
            kayitSms = c.kayitSms2;
            kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);
            kayitSms = kayitSms.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
            kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
            kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

            System.out.println("Beklenen kayit SMSi2: \n" + kayitSms);
            //String kayitSMS="Paketiniz hattiniza basariyla tanimlanmistir. Bal 300 Paketi ile 1 ay boyunca yurtici her yone 300 dakika konusabilirsiniz.Kullanim suresi sonunda hattinizda yeterli bakiye olmasi halinde paketiniz otomatik olarak yenilenecektir.Paketinizi iptal etmek icin IPTAL yazip 2070e gonderin";
            pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";
            // Create a Pattern object
            r = Pattern.compile(pattern);
            String gidenSms2 = iterator.next();
            // Now create matcher object.
            m = r.matcher(gidenSms2);

            Assert.assertTrue("2. Kayit SMSi yanlis..:" + gidenSms2, m.matches());

        }
        System.out.println("\n test9 NonNt yetersiz bakiye Kayit SMSi bitti");

    }

    public String addDay() {
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DAY_OF_YEAR, Integer.parseInt(c.day));
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = today.getTime();
        String r = dateFormat.format(date);
        //System.out.println(r);


        return r;
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
                //System.out.print("Bakiyesi: ");
                //m = Pattern.compile("<balanceName>BLP_Main</balanceName><measure>TRY</measure><unitType>CUR</unitType><currency>TRY</currency><balance>([^<]*)")
                //      .matcher(resp);
                //while (m.find()) {
                //  System.out.println(  m.group(1));
                //}
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
            money = new BigDecimal(m.group(1));
            System.out.println("Bakiyesi: " + money);
        }
        m = Pattern.compile("<Message>BSG_10000:SUCCESS</Message>").matcher(resp);
        assumeTrue("BSG_10000:SUCCESS alinamadi", m.find());

        if (money==null)
            return new BigDecimal(0);
        return money;
    }

    public void checkService(String msisdn, String servis, String state) {
        if (servis.isEmpty()) {
            System.out.println("INFO: Servis bos birakildigindan kontrol edilmiyor.");
            return;
        }
        String resp = postSOAPXML(msisdn);
        Matcher m = Pattern.compile("<soldProducts><productName>" + servis + "</productName><state>" + state + "</state><type>Package</type>")
                .matcher(resp);

        assumeTrue(servis + " " + state + " bulunamadi", m.find());

    }

    public void checkServiceNotExist(String msisdn, String servis) {
        if (servis.isEmpty()) {
            System.out.println("INFO: Servis bos birakildigindan kontrol edilmiyor.");
            return;
        }
        String resp = postSOAPXML(msisdn);
        Matcher m = Pattern.compile("<soldProducts><productName>" + servis + "</productName>").matcher(resp);
        assumeFalse(servis + " hala abone uzerinde", m.find());

        m = Pattern.compile("<Message>BSG_10000:SUCCESS</Message>").matcher(resp);
        assumeTrue("BSG_10000:SUCCESS alinamadi", m.find());

    }
}

