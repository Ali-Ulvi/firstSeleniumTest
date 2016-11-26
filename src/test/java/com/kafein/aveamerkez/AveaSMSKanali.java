package com.kafein.aveamerkez;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;
//import org.testng.asserts.SoftAssert;

/**
 * Created by Ali Ulvi Talipoglu via Kafein on 19.11.2016.
 * Kısa numaraya SMS gönderip, müşteriye giden SMS'i loglardan kontrol ediyor.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AveaSMSKanali {
    static Sil2 sil, silNonNT, silNTFLAGAbone;
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
        silNTFLAGAbone = new Sil2(c.Tarifesi_uygun_bir_msisdn);
        silNTFLAGAbone.start();
        String strDirectoy = "C:\\Logs";
        try {
            boolean success = (new File(strDirectoy)).mkdir();
            if (success) {
                System.out.println("Directory: " + strDirectoy + " created");
            }
        } catch (Exception e) {//Catch exception if any
            System.err.println("c:\\Logs klasoru yaratilamadi. manuel yaratiniz ");
        }
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
        checkBonus(c.msisdn,c.Paket_Kayit_Bonusu);

        BigDecimal moneyAfter = getMoney(c.msisdn);
        BigDecimal fiyat = money.subtract(moneyAfter);
        Assert.assertEquals(c.NT_Fiyat + " TL dusmesi gerekirken " + fiyat + " TL dustu. Servis veya fiyati yanlis.", Double.parseDouble(new BigDecimal(c.NT_Fiyat).toString()), Double.parseDouble(fiyat.toString()), 0);

        List<String> smsler = log.getAsbLog(c.msisdn, "Test01_KayitSMS");
        Iterator<String> iterator = smsler.iterator();

        String gun = addDay();
        String kayitSms = c.kayitSms;
        kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);kayitSms = kayitSms.replaceAll("\\.$","");kayitSms = kayitSms.replaceAll("\\*","\\\\*");kayitSms = kayitSms.replaceAll("\\.","\\\\.");kayitSms = kayitSms.replaceAll("ayabilirsin J$","ayabilirsin :)");kayitSms = kayitSms.replaceAll("\\(","\\\\(");kayitSms = kayitSms.replaceAll("\\)","\\\\)");kayitSms = kayitSms.replaceAll("\\]","\\\\]");kayitSms = kayitSms.replaceAll("\\[","\\\\[");
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
            kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);kayitSms = kayitSms.replaceAll("\\.$","");kayitSms = kayitSms.replaceAll("\\*","\\\\*");kayitSms = kayitSms.replaceAll("\\.","\\\\.");kayitSms = kayitSms.replaceAll("ayabilirsin J$","ayabilirsin :)");kayitSms = kayitSms.replaceAll("\\(","\\\\(");kayitSms = kayitSms.replaceAll("\\)","\\\\)");kayitSms = kayitSms.replaceAll("\\]","\\\\]");kayitSms = kayitSms.replaceAll("\\[","\\\\[");
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

        } else {
            Assert.assertEquals("2. Kayit SMSi gelmedi", "", c.kayitSms2);
        }
        System.out.println("\n test1 Kayit SMS bitti");

    }

    @Test
    public void Test02_KalanSMS() throws Exception {
        assumeTrue(!c.kalanSMS.isEmpty());
        log.startPromoLog(c.msisdn);
        sms.sendSms(c.msisdn, c.kn, "KALAN " + c.kw);
        List<String> smsler = log.getPromoLog(c.msisdn, "Test02_KalanSMS");
        Iterator<String> iterator = smsler.iterator();
        String gidenSms1 = iterator.next();
        Assert.assertEquals(c.kalanSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy", addDay()), gidenSms1);

        if (iterator.hasNext()) {
            String gidenSms2 = iterator.next();
            System.err.println("Giden 2. SMS: " + gidenSms2);
            assumeTrue("Kalan sorgusunda Promo'dan birden fazla SMS gidiyor. Promo loguna bakiniz", false);

        }
        System.out.println("\n test2 paket kalan SMS bitti");

    }

    @Test
    public void Test03_iptalSMS() throws Exception {

        assumeTrue(!c.iptalSMS.isEmpty());
        Log.log.startAsbLog(c.msisdn);

        sms.sendSms(c.msisdn, c.kn, "IPTAL " + c.kw);
        String gun = addDay();
        String iptalSMS = c.iptalSMS;
        iptalSMS = iptalSMS.replace("{sysdate+30}", gun);
        iptalSMS = iptalSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy|<BONUS_END_DATE>", gun);iptalSMS = iptalSMS.replaceAll("\\.$","");iptalSMS = iptalSMS.replaceAll("\\*","\\\\*");iptalSMS = iptalSMS.replaceAll("\\.","\\\\.");iptalSMS = iptalSMS.replaceAll("ayabilirsin J$","ayabilirsin :)");iptalSMS = iptalSMS.replaceAll("\\(","\\\\(");iptalSMS = iptalSMS.replaceAll("\\)","\\\\)");iptalSMS = iptalSMS.replaceAll("\\]","\\\\]");iptalSMS = iptalSMS.replaceAll("\\[","\\\\["); 
        iptalSMS = iptalSMS.replace(" kadar ", " kadar \\s?");
        System.out.println("Beklenen iptal SMSi: \n" + iptalSMS);
        //String kayitSMS="Paketiniz hattiniza basariyla tanimlanmistir. Bal 300 Paketi ile 1 ay boyunca yurtici her yone 300 dakika konusabilirsiniz.Kullanim suresi sonunda hattinizda yeterli bakiye olmasi halinde paketiniz otomatik olarak yenilenecektir.Paketinizi iptal etmek icin IPTAL yazip 2070e gonderin";
        String pattern = "\\s?\\s?\\s?" + iptalSMS + "\\.?\\.?\\.?\\s?\\s?\\s?";
        checkServiceNotExist(c.msisdn, c.NT_servis);
        checkBonusNotExist(c.msisdn,c.Paket_Kayit_Bonusu);
        List<String> smsler = log.getAsbLog(c.msisdn, "Test03_iptalSMS");
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
    public void Test05_v2_iptalSonrasiKalanSMS() throws Exception {
        assumeTrue(!c.kalanSMS.isEmpty());
        //once iptal ediyoruz:
        Test03_iptalSMS();
        log.startPromoLog(c.msisdn);
        sms.sendSms(c.msisdn, c.kn, "KALAN " + c.kw);
        List<String> smsler = log.getPromoLog(c.msisdn, "Test02_KalanSMS");
        Iterator<String> iterator = smsler.iterator();
        String gidenSms1 = iterator.next();
        if (!c.Paketi_zaten_var_SMSi.isEmpty()) { //Paket 1 kere alinabilen bir paketse Bir onceki test casede 1 kere alabildi demekki

            Assert.assertEquals(c.kalanSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy", addDay()), gidenSms1);
        }
        else{
            //2 kere alinabilen paket. bu noktada 2 kere alinmis oluyor. 5 nolu test nedeniyle. Gercek miktarlar sorgulandigi test edilmis oluyor
            String KalanSMS_2KereAlmis = multiplyBonusAmountsBy2(c.kalanSMS);
            Assert.assertEquals(KalanSMS_2KereAlmis.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy", addDay()), gidenSms1);
        }
        if (iterator.hasNext()) {
            String gidenSms2 = iterator.next();
            System.err.println("Giden 2. SMS: " + gidenSms2);
            assumeTrue("Kalan sorgusunda Promo'dan birden fazla SMS gidiyor. Promo loguna bakiniz", false);

        }
        System.out.println("\n test2 paket kalan SMS bitti");

    }

    @Test
    public void Test04_iptal_edilecek_paketi_yoksa_SMSi() throws Exception {

        assumeTrue(!c.iptal_edilecek_paketi_yoksa_SMSi.isEmpty());
        Log.log.startAsbLog(c.msisdn);

        sms.sendSms(c.msisdn, c.kn, "IPTAL " + c.kw);
        checkServiceNotExist(c.msisdn, c.NT_servis);
        checkBonusNotExist(c.msisdn,c.Paket_Kayit_Bonusu);
        String gun = addDay();
        String iptalSMS = c.iptal_edilecek_paketi_yoksa_SMSi;
        iptalSMS = iptalSMS.replace("{sysdate+30}", gun);
        iptalSMS = iptalSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy|<BONUS_END_DATE>", gun);
        iptalSMS = iptalSMS.replaceAll("\\.$","");iptalSMS = iptalSMS.replaceAll("\\*","\\\\*");iptalSMS = iptalSMS.replaceAll("\\.","\\\\.");iptalSMS = iptalSMS.replaceAll("ayabilirsin J$","ayabilirsin :)");iptalSMS = iptalSMS.replaceAll("\\(","\\\\(");iptalSMS = iptalSMS.replaceAll("\\)","\\\\)");iptalSMS = iptalSMS.replaceAll("\\]","\\\\]");iptalSMS = iptalSMS.replaceAll("\\[","\\\\["); 
        System.out.println("Beklenen iptal_edilecek_paketi_yoksa_SMSi: \n" + iptalSMS);
        String pattern = "\\s?\\s?\\s?" + iptalSMS + "\\.?\\.?\\.?\\s?\\s?\\s?";

        List<String> smsler = log.getAsbLog(c.msisdn, "Test04_iptal_edilecek_paketi_yoksa_SMSi");
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
        //ilk kayit
        Test01_KayitSMS();
        if (c.Paketi_zaten_var_SMSi.isEmpty()) {
            BigDecimal money = getMoney(c.msisdn);
            Log.log.startAsbLog(c.msisdn);
            if (!c.TCID.isEmpty())
                sms.sendSms(c.msisdn, c.kn, c.kw + " " + c.TCID);
            else
                sms.sendSms(c.msisdn, c.kn, c.kw);

            checkService(c.msisdn, c.NT_servis, "ACTIVE/STD/STD");
            checkBonus(c.msisdn,c.Paket_Kayit_Bonusu);
            BigDecimal moneyAfter = getMoney(c.msisdn);
            BigDecimal fiyat = money.subtract(moneyAfter);
            Assert.assertEquals(c.NT_Fiyat + " TL dusmesi gerekirken " + fiyat + " TL dustu. Servis veya fiyati yanlis.", Double.parseDouble(new BigDecimal(c.NT_Fiyat).toString()), Double.parseDouble(fiyat.toString()), 0);

            List<String> smsler = log.getAsbLog(c.msisdn, "Test05_Pakete_birden_fazla_kez_KayitSMS");
            Iterator<String> iterator = smsler.iterator();

            String gun = addDay();
            String kayitSms = c.kayitSms;
            kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);kayitSms = kayitSms.replaceAll("\\.$","");kayitSms = kayitSms.replaceAll("\\*","\\\\*");kayitSms = kayitSms.replaceAll("\\.","\\\\.");kayitSms = kayitSms.replaceAll("ayabilirsin J$","ayabilirsin :)");kayitSms = kayitSms.replaceAll("\\(","\\\\(");kayitSms = kayitSms.replaceAll("\\)","\\\\)");kayitSms = kayitSms.replaceAll("\\]","\\\\]");kayitSms = kayitSms.replaceAll("\\[","\\\\[");
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
                kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);kayitSms = kayitSms.replaceAll("\\.$","");kayitSms = kayitSms.replaceAll("\\*","\\\\*");kayitSms = kayitSms.replaceAll("\\.","\\\\.");kayitSms = kayitSms.replaceAll("ayabilirsin J$","ayabilirsin :)");kayitSms = kayitSms.replaceAll("\\(","\\\\(");kayitSms = kayitSms.replaceAll("\\)","\\\\)");kayitSms = kayitSms.replaceAll("\\]","\\\\]");kayitSms = kayitSms.replaceAll("\\[","\\\\[");
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
            checkBonus(c.msisdn,c.Paket_Kayit_Bonusu);

            BigDecimal moneyAfter = getMoney(c.msisdn);
            BigDecimal fiyat = money.subtract(moneyAfter);
            Assert.assertEquals(0 + " TL dusmesi gerekirken " + fiyat + " TL dustu. Servis veya fiyati yanlis.", Double.parseDouble(new BigDecimal(0).toString()), Double.parseDouble(fiyat.toString()), 0);

            List<String> smsler = log.getAsbLog(c.msisdn, "Test05_Pakete_birden_fazla_kez_KayitSMS");
            Iterator<String> iterator = smsler.iterator();

            String gun = addDay();
            String kayitSms = c.Paketi_zaten_var_SMSi;
            kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);kayitSms = kayitSms.replaceAll("\\.$","");kayitSms = kayitSms.replaceAll("\\*","\\\\*");kayitSms = kayitSms.replaceAll("\\.","\\\\.");kayitSms = kayitSms.replaceAll("ayabilirsin J$","ayabilirsin :)");kayitSms = kayitSms.replaceAll("\\(","\\\\(");kayitSms = kayitSms.replaceAll("\\)","\\\\)");kayitSms = kayitSms.replaceAll("\\]","\\\\]");kayitSms = kayitSms.replaceAll("\\[","\\\\[");
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
                kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);kayitSms = kayitSms.replaceAll("\\.$","");kayitSms = kayitSms.replaceAll("\\*","\\\\*");kayitSms = kayitSms.replaceAll("\\.","\\\\.");kayitSms = kayitSms.replaceAll("ayabilirsin J$","ayabilirsin :)");kayitSms = kayitSms.replaceAll("\\(","\\\\(");kayitSms = kayitSms.replaceAll("\\)","\\\\)");kayitSms = kayitSms.replaceAll("\\]","\\\\]");kayitSms = kayitSms.replaceAll("\\[","\\\\[");
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

        List<String> smsler = log.getAsbLog(c.yetersizBakiyemsisdn, "Test06_Yetersiz_BakiyeKayitSMS");
        Iterator<String> iterator = smsler.iterator();
        if (c.bakiyesizAlim.equalsIgnoreCase("evet"))
            checkService(c.yetersizBakiyemsisdn,c.NT_servis,"PASSIVE/STD/FOLLOW");
        else
            checkServiceNotExist(c.yetersizBakiyemsisdn,c.NT_servis);
        checkBonusNotExist(c.yetersizBakiyemsisdn,c.Paket_Kayit_Bonusu);
        String gun = addDay();
        String kayitSms = c.yetersizBakiyeKayitSMS;
        kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);kayitSms = kayitSms.replaceAll("\\.$","");kayitSms = kayitSms.replaceAll("\\*","\\\\*");kayitSms = kayitSms.replaceAll("\\.","\\\\.");kayitSms = kayitSms.replaceAll("ayabilirsin J$","ayabilirsin :)");kayitSms = kayitSms.replaceAll("\\(","\\\\(");kayitSms = kayitSms.replaceAll("\\)","\\\\)");kayitSms = kayitSms.replaceAll("\\]","\\\\]");kayitSms = kayitSms.replaceAll("\\[","\\\\[");
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
            kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);kayitSms = kayitSms.replaceAll("\\.$","");kayitSms = kayitSms.replaceAll("\\*","\\\\*");kayitSms = kayitSms.replaceAll("\\.","\\\\.");kayitSms = kayitSms.replaceAll("ayabilirsin J$","ayabilirsin :)");kayitSms = kayitSms.replaceAll("\\(","\\\\(");kayitSms = kayitSms.replaceAll("\\)","\\\\)");kayitSms = kayitSms.replaceAll("\\]","\\\\]");kayitSms = kayitSms.replaceAll("\\[","\\\\[");
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
        checkBonus(c.NonNT_msisdn, c.Paket_Kayit_Bonusu);


        BigDecimal moneyAfter = getMoney(c.NonNT_msisdn);
        BigDecimal fiyat = money.subtract(moneyAfter);
        Assert.assertEquals(c.NonNT_Fiyat + " TL dusmesi gerekirken " + fiyat + " TL dustu. Servis veya fiyati yanlis.", Double.parseDouble(new BigDecimal(c.NonNT_Fiyat).toString()), Double.parseDouble(fiyat.toString()), 0);

        List<String> smsler = log.getAsbLog(c.NonNT_msisdn, "Test07_NonNT_KayitSMS");
        Iterator<String> iterator = smsler.iterator();

        String gun = addDay();
        String kayitSms = c.NonNT_Kayit_Mesaji;
        kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);kayitSms = kayitSms.replaceAll("\\.$","");kayitSms = kayitSms.replaceAll("\\*","\\\\*");kayitSms = kayitSms.replaceAll("\\.","\\\\.");kayitSms = kayitSms.replaceAll("ayabilirsin J$","ayabilirsin :)");kayitSms = kayitSms.replaceAll("\\(","\\\\(");kayitSms = kayitSms.replaceAll("\\)","\\\\)");kayitSms = kayitSms.replaceAll("\\]","\\\\]");kayitSms = kayitSms.replaceAll("\\[","\\\\[");
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
            kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);kayitSms = kayitSms.replaceAll("\\.$","");kayitSms = kayitSms.replaceAll("\\*","\\\\*");kayitSms = kayitSms.replaceAll("\\.","\\\\.");kayitSms = kayitSms.replaceAll("ayabilirsin J$","ayabilirsin :)");kayitSms = kayitSms.replaceAll("\\(","\\\\(");kayitSms = kayitSms.replaceAll("\\)","\\\\)");kayitSms = kayitSms.replaceAll("\\]","\\\\]");kayitSms = kayitSms.replaceAll("\\[","\\\\[");
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
        List<String> smsler = log.getPromoLog(c.NonNT_msisdn, "Test08_NonNT_KalanSMS");
        Iterator<String> iterator = smsler.iterator();
        String gidenSms1 = iterator.next();
        Assert.assertEquals(c.kalanSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy", addDay()), gidenSms1);

        if (iterator.hasNext()) {
            String gidenSms2 = iterator.next();
            System.err.println("Giden 2. SMS: " + gidenSms2);
            assumeTrue("Kalan sorgusunda Promo'dan birden fazla SMS gidiyor. Promo loguna bakiniz", false);

        }
        System.out.println("\n test8 NonNt paket kalan SMS bitti");

    }

    @Test
    public void Test08_v2_NonNt_iptalSMS() throws Exception {

        assumeTrue(!c.iptalSMS.isEmpty());
        Log.log.startAsbLog(c.NonNT_msisdn);

        sms.sendSms(c.NonNT_msisdn, c.kn, "IPTAL " + c.kw);
        String gun = addDay();
        String iptalSMS = c.iptalSMS;
        iptalSMS = iptalSMS.replace("{sysdate+30}", gun);
        iptalSMS = iptalSMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy|<BONUS_END_DATE>", gun);iptalSMS = iptalSMS.replaceAll("\\.$","");iptalSMS = iptalSMS.replaceAll("\\*","\\\\*");iptalSMS = iptalSMS.replaceAll("\\.","\\\\.");iptalSMS = iptalSMS.replaceAll("ayabilirsin J$","ayabilirsin :)");iptalSMS = iptalSMS.replaceAll("\\(","\\\\(");iptalSMS = iptalSMS.replaceAll("\\)","\\\\)");iptalSMS = iptalSMS.replaceAll("\\]","\\\\]");iptalSMS = iptalSMS.replaceAll("\\[","\\\\["); 
        iptalSMS = iptalSMS.replace(" kadar ", " kadar \\s?");
        System.out.println("Beklenen iptal SMSi: \n" + iptalSMS);
        //String kayitSMS="Paketiniz hattiniza basariyla tanimlanmistir. Bal 300 Paketi ile 1 ay boyunca yurtici her yone 300 dakika konusabilirsiniz.Kullanim suresi sonunda hattinizda yeterli bakiye olmasi halinde paketiniz otomatik olarak yenilenecektir.Paketinizi iptal etmek icin IPTAL yazip 2070e gonderin";
        String pattern = "\\s?\\s?\\s?" + iptalSMS + "\\.?\\.?\\.?\\s?\\s?\\s?";
        checkServiceNotExist(c.NonNT_msisdn, c.NonNT_servis);
        checkBonusNotExist(c.NonNT_msisdn,c.Paket_Kayit_Bonusu);
        List<String> smsler = log.getAsbLog(c.NonNT_msisdn, "Test08_v2_NonNt_iptalSMS");
        Iterator<String> iterator = smsler.iterator();
        assertThat(iterator.next(), RegexMatcher.matchesRegex(pattern));

        if (iterator.hasNext()) {

            String gidenSms2 = iterator.next();
            System.err.println("iptalde giden 2. SMS: " + gidenSms2);
            assumeTrue("iptalde ASB'den birden fazla SMS gidiyor. ASB loguna bakiniz", false);

        }
        System.out.println("\n Test08_v2_NonNt_iptalSMS bitti");

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

        List<String> smsler = log.getAsbLog(c.NonNtyetersizBakiyeMsisdn, "Test09_NonNT_Yetersiz_BakiyeKayitSMS");
        Iterator<String> iterator = smsler.iterator();

        if (c.bakiyesizAlim.equalsIgnoreCase("evet"))
            checkService(c.NonNtyetersizBakiyeMsisdn,c.NonNT_servis,"PASSIVE/STD/FOLLOW");
        else
            checkServiceNotExist(c.NonNtyetersizBakiyeMsisdn,c.NonNT_servis);
        checkBonusNotExist(c.NonNtyetersizBakiyeMsisdn, c.Paket_Kayit_Bonusu);

        String gun = addDay();
        String kayitSms = c.NonNtyetersizBakiyeKayitSMS;
        kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);kayitSms = kayitSms.replaceAll("\\.$","");kayitSms = kayitSms.replaceAll("\\*","\\\\*");kayitSms = kayitSms.replaceAll("\\.","\\\\.");kayitSms = kayitSms.replaceAll("ayabilirsin J$","ayabilirsin :)");kayitSms = kayitSms.replaceAll("\\(","\\\\(");kayitSms = kayitSms.replaceAll("\\)","\\\\)");kayitSms = kayitSms.replaceAll("\\]","\\\\]");kayitSms = kayitSms.replaceAll("\\[","\\\\[");
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
            kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);kayitSms = kayitSms.replaceAll("\\.$","");kayitSms = kayitSms.replaceAll("\\*","\\\\*");kayitSms = kayitSms.replaceAll("\\.","\\\\.");kayitSms = kayitSms.replaceAll("ayabilirsin J$","ayabilirsin :)");kayitSms = kayitSms.replaceAll("\\(","\\\\(");kayitSms = kayitSms.replaceAll("\\)","\\\\)");kayitSms = kayitSms.replaceAll("\\]","\\\\]");kayitSms = kayitSms.replaceAll("\\[","\\\\[");
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

    @Test
    public void Test00_Paketi_Yoksa_KalanSMSi() throws Exception {
        assumeTrue(!c.kalanSMS_paket_yoksa.isEmpty());
        log.startPromoLog(c.msisdn);
        sms.sendSms(c.msisdn, c.kn, "KALAN " + c.kw);
        List<String> smsler = log.getPromoLog(c.msisdn, "Test00_Paketi_Yoksa_KalanSMSi");
        Iterator<String> iterator = smsler.iterator();
        String gidenSms1 = iterator.next();
        Assert.assertEquals(c.kalanSMS_paket_yoksa.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy", addDay()), gidenSms1);

        if (iterator.hasNext()) {
            String gidenSms2 = iterator.next();
            System.err.println("Giden 2. SMS: " + gidenSms2);
            assumeTrue("kalanSMS_paket_yoksa sorgusunda Promo'dan birden fazla SMS gidiyor. Promo loguna bakiniz", false);

        }
        System.out.println("\n test00 kalanSMS_paket_yoksa bitti");

    }

    @Test
    public void Test02_v2_paketi_bitmis_ise_KalanSMS() throws Exception {
        assumeTrue(!c.kalanSMS_paket_bitmis_ise.isEmpty());
        SilBonus silBonus = new SilBonus(c.msisdn);
        silBonus.start();
        silBonus.join();
        log.startPromoLog(c.msisdn);
        sms.sendSms(c.msisdn, c.kn, "KALAN " + c.kw);
        List<String> smsler = log.getPromoLog(c.msisdn, "Test02_2_paketi_bitmis_ise_KalanSMS");
        Iterator<String> iterator = smsler.iterator();
        String gidenSms1 = iterator.next();
        Assert.assertEquals(c.kalanSMS_paket_bitmis_ise.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy", addDay()), gidenSms1);

        if (iterator.hasNext()) {
            String gidenSms2 = iterator.next();
            System.err.println("Giden 2. SMS: " + gidenSms2);
            assumeTrue("kalanSMS_paket_yoksa sorgusunda Promo'dan birden fazla SMS gidiyor. Promo loguna bakiniz", false);

        }
        System.out.println("\n test02.2 kalanSMS_paket_bitmis_ise bitti");

    }

    @Test
    public void Test10_YasakTarifeSMSleri() throws Exception {

        BigDecimal money = getMoney(c.TempMsisdn);
        //SoftAssert softAssert = new SoftAssert();

        changeTariff(c.TempMsisdn, c.YasakTarife1);
        Log.log.startAsbLog(c.TempMsisdn);

        Log.log.startPromoLog(c.TempMsisdn);
        if (!c.TCID.isEmpty())
            sms.sendSms(c.TempMsisdn, c.kn, c.kw + " " + c.TCID);
        else
            sms.sendSms(c.TempMsisdn, c.kn, c.kw);

        BigDecimal moneyAfter = getMoney(c.TempMsisdn);
        BigDecimal fiyat = money.subtract(moneyAfter);

        List<String> smsler = log.getPromoLog(c.TempMsisdn, "Test10_YasakTarifeSMSleri Tarife " + c.YasakTarife1);
        List<String> smslerAsb = log.getAsbLog(c.TempMsisdn, "Test10_YasakTarifeSMSleri Tarife " + c.YasakTarife1);
        Iterator<String> iterator = smsler.iterator();
        Iterator<String> asbIterator = smslerAsb.iterator();
        assumeFalse("Hem ASBden Hem Promodan SMS gonderildi.", iterator.hasNext() && asbIterator.hasNext());
        if (iterator.hasNext()) {

            String kayitSms = c.YasakTarife1SMSi;
            System.out.println("Beklenen YasakTarife1SMSi: \n" + kayitSms);

            String pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";

            // Create a Pattern object
            Pattern r = Pattern.compile(pattern);
            String gidenSms1 = iterator.next();
            // Now create matcher object.
            Matcher m = r.matcher(gidenSms1);
            Assert.assertTrue(c.YasakTarife1 + " YasakTarife1SMSi yanlis (Vas_Promo): " + gidenSms1, m.matches());

            assumeFalse("Promodan birden fazla SMS gidiyor", iterator.hasNext());

        }

        if (asbIterator.hasNext()) {

            String kayitSms = c.YasakTarife1SMSi;
            System.out.println("Beklenen YasakTarife1SMSi: \n" + kayitSms);
            String pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";
            // Create a Pattern object
            Pattern r = Pattern.compile(pattern);
            String gidenSms1 = asbIterator.next();
            Matcher m = r.matcher(gidenSms1);
            Assert.assertTrue(c.YasakTarife1 + " YasakTarife1SMSi yanlis (ASB): " + gidenSms1, m.matches());
            assumeFalse("ASBden birden fazla SMS gidiyor", iterator.hasNext());

        }
        Assert.assertEquals(0 + " TL dusmesi gerekirken " + fiyat + " TL dustu. Yasakli Tarife alabiliyor: " + c.YasakTarife1, Double.parseDouble(new BigDecimal(0).toString()), Double.parseDouble(fiyat.toString()), 0);

        if (c.YasakTarife2.isEmpty()) {
            System.err.println("yasak tarife2 bos oldugundan test edilmiyor.");
            return;
        }
        changeTariff(c.TempMsisdn, c.YasakTarife2);
        Log.log.startAsbLog(c.TempMsisdn);

        Log.log.startPromoLog(c.TempMsisdn);
        if (!c.TCID.isEmpty())
            sms.sendSms(c.TempMsisdn, c.kn, c.kw + " " + c.TCID);
        else
            sms.sendSms(c.TempMsisdn, c.kn, c.kw);

        moneyAfter = getMoney(c.TempMsisdn);
        fiyat = money.subtract(moneyAfter);

        smsler = log.getPromoLog(c.TempMsisdn, "Test10_YasakTarifeSMSleri Tarife " + c.YasakTarife2);
        smslerAsb = log.getAsbLog(c.TempMsisdn, "Test10_YasakTarifeSMSleri Tarife " + c.YasakTarife2);
        iterator = smsler.iterator();
        asbIterator = smslerAsb.iterator();
        assumeFalse("Hem ASBden Hem Promodan SMS gonderildi.", iterator.hasNext() && asbIterator.hasNext());
        if (iterator.hasNext()) {

            String kayitSms = c.YasakTarife2SMSi;
            System.out.println("Beklenen YasakTarife2SMSi: \n" + kayitSms);

            String pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";

            // Create a Pattern object
            Pattern r = Pattern.compile(pattern);
            String gidenSms1 = iterator.next();
            // Now create matcher object.
            Matcher m = r.matcher(gidenSms1);
            Assert.assertTrue(c.YasakTarife2 + " YasakTarife2SMSi yanlis (Vas_Promo): " + gidenSms1, m.matches());

            assumeFalse("Promodan birden fazla SMS gidiyor", iterator.hasNext());

        }

        if (asbIterator.hasNext()) {

            String kayitSms = c.YasakTarife2SMSi;
            System.out.println("Beklenen YasakTarife2SMSi: \n" + kayitSms);
            String pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";
            // Create a Pattern object
            Pattern r = Pattern.compile(pattern);
            String gidenSms1 = asbIterator.next();
            Matcher m = r.matcher(gidenSms1);
            Assert.assertTrue(c.YasakTarife2 + " YasakTarife2SMSi yanlis (ASB): " + gidenSms1, m.matches());
            assumeFalse("ASBden birden fazla SMS gidiyor", iterator.hasNext());

        }
        Assert.assertEquals(0 + " TL dusmesi gerekirken " + fiyat + " TL dustu. Yasakli Tarife alabiliyor: " + c.YasakTarife2, Double.parseDouble(new BigDecimal(0).toString()), Double.parseDouble(fiyat.toString()), 0);


        System.out.println("\n test10 Yasak Tarifeler SMSleri bitti");

    }


    @Test
    public void Test11_YasakTarifeSMSleri2() throws Exception {
        assertTrue("YasakliTarife3 girilmediğinden Test Edilmiyor", !c.YasakTarife3.isEmpty());
        BigDecimal money = getMoney(c.TempMsisdn);
        //SoftAssert softAssert = new SoftAssert();

        changeTariff(c.TempMsisdn, c.YasakTarife3);
        Log.log.startAsbLog(c.TempMsisdn);

        Log.log.startPromoLog(c.TempMsisdn);
        if (!c.TCID.isEmpty())
            sms.sendSms(c.TempMsisdn, c.kn, c.kw + " " + c.TCID);
        else
            sms.sendSms(c.TempMsisdn, c.kn, c.kw);

        BigDecimal moneyAfter = getMoney(c.TempMsisdn);
        BigDecimal fiyat = money.subtract(moneyAfter);

        List<String> smsler = log.getPromoLog(c.TempMsisdn, "Test11_YasakTarifeSMSleri2 Tarife " + c.YasakTarife3);
        List<String> smslerAsb = log.getAsbLog(c.TempMsisdn, "Test11_YasakTarifeSMSleri2 Tarife " + c.YasakTarife3);
        Iterator<String> iterator = smsler.iterator();
        Iterator<String> asbIterator = smslerAsb.iterator();
        assumeFalse("Hem ASBden Hem Promodan SMS gonderildi.", iterator.hasNext() && asbIterator.hasNext());
        if (iterator.hasNext()) {

            String kayitSms = c.YasakTarife3SMSi;
            System.out.println("Beklenen YasakTarife3SMSi: \n" + kayitSms);

            String pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";

            // Create a Pattern object
            Pattern r = Pattern.compile(pattern);
            String gidenSms1 = iterator.next();
            // Now create matcher object.
            Matcher m = r.matcher(gidenSms1);
            Assert.assertTrue(c.YasakTarife3 + " YasakTarife3SMSi yanlis (Vas_Promo): " + gidenSms1, m.matches());

            assumeFalse("Promodan birden fazla SMS gidiyor", iterator.hasNext());

        }

        if (asbIterator.hasNext()) {

            String kayitSms = c.YasakTarife3SMSi;
            System.out.println("Beklenen YasakTarife3SMSi: \n" + kayitSms);
            String pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";
            // Create a Pattern object
            Pattern r = Pattern.compile(pattern);
            String gidenSms1 = asbIterator.next();
            Matcher m = r.matcher(gidenSms1);
            Assert.assertTrue(c.YasakTarife3 + " YasakTarife3SMSi yanlis (ASB): " + gidenSms1, m.matches());
            assumeFalse("ASBden birden fazla SMS gidiyor", iterator.hasNext());

        }
        Assert.assertEquals(0 + " TL dusmesi gerekirken " + fiyat + " TL dustu. Yasakli Tarife alabiliyor: " + c.YasakTarife3, Double.parseDouble(new BigDecimal(0).toString()), Double.parseDouble(fiyat.toString()), 0);

        if (c.YasakTarife4.isEmpty()) {
            System.err.println("yasak tarife4 bos oldugundan test edilmiyor.");
            return;
        }
        changeTariff(c.TempMsisdn, c.YasakTarife4);
        Log.log.startAsbLog(c.TempMsisdn);

        Log.log.startPromoLog(c.TempMsisdn);
        if (!c.TCID.isEmpty())
            sms.sendSms(c.TempMsisdn, c.kn, c.kw + " " + c.TCID);
        else
            sms.sendSms(c.TempMsisdn, c.kn, c.kw);

        moneyAfter = getMoney(c.TempMsisdn);
        fiyat = money.subtract(moneyAfter);

        smsler = log.getPromoLog(c.TempMsisdn, "Test11_YasakTarifeSMSleri2 Tarife " + c.YasakTarife4);
        smslerAsb = log.getAsbLog(c.TempMsisdn, "Test11_YasakTarifeSMSleri2 Tarife " + c.YasakTarife4);
        iterator = smsler.iterator();
        asbIterator = smslerAsb.iterator();
        assumeFalse("Hem ASBden Hem Promodan SMS gonderildi.", iterator.hasNext() && asbIterator.hasNext());
        if (iterator.hasNext()) {

            String kayitSms = c.YasakTarife4SMSi;
            System.out.println("Beklenen YasakTarife4SMSi: \n" + kayitSms);

            String pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";

            // Create a Pattern object
            Pattern r = Pattern.compile(pattern);
            String gidenSms1 = iterator.next();
            // Now create matcher object.
            Matcher m = r.matcher(gidenSms1);
            Assert.assertTrue(c.YasakTarife4 + " YasakTarife4SMSi yanlis (Vas_Promo): " + gidenSms1, m.matches());

            assumeFalse("Promodan birden fazla SMS gidiyor", iterator.hasNext());

        }

        if (asbIterator.hasNext()) {

            String kayitSms = c.YasakTarife4SMSi;
            System.out.println("Beklenen YasakTarife4SMSi: \n" + kayitSms);
            String pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";
            // Create a Pattern object
            Pattern r = Pattern.compile(pattern);
            String gidenSms1 = asbIterator.next();
            Matcher m = r.matcher(gidenSms1);
            Assert.assertTrue(c.YasakTarife4 + " YasakTarife4SMSi yanlis (ASB): " + gidenSms1, m.matches());
            assumeFalse("ASBden birden fazla SMS gidiyor", iterator.hasNext());

        }
        Assert.assertEquals(0 + " TL dusmesi gerekirken " + fiyat + " TL dustu. Yasakli Tarife alabiliyor: " + c.YasakTarife4, Double.parseDouble(new BigDecimal(0).toString()), Double.parseDouble(fiyat.toString()), 0);


        System.out.println("\n test11 Yasak Tarifeler SMSleri2 bitti");

    }


    @Test
    public void Test12_FaturaliAboneSMS() throws Exception {
        assumeTrue(!c.Faturali_No.isEmpty());
        log.startPromoLog(c.Faturali_No);
        sms.sendSms(c.Faturali_No, c.kn, c.kw);
        List<String> smsler = log.getPromoLog(c.Faturali_No, "Test12_FaturaliAboneSMS");
        Iterator<String> iterator = smsler.iterator();
        String gidenSms1 = iterator.next();
        Assert.assertEquals(c.Faturali_SMSi.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy", addDay()), gidenSms1);

        if (iterator.hasNext()) {
            String gidenSms2 = iterator.next();
            System.err.println("Giden 2. SMS: " + gidenSms2);
            assumeTrue("Test12_FaturaliAboneSMS: Promo'dan birden fazla SMS gidiyor. Promo loguna bakiniz", false);

        }
        System.out.println("\n Test12_FaturaliAboneSMS bitti");

    }


    @Test
    public void Test13_HotlineAboneSMS() throws Exception {
        assumeTrue(!c.Hotline_No.isEmpty());
        log.startPromoLog(c.Hotline_No);
        log.startAsbLog(c.Hotline_No);
        sms.sendSms(c.Hotline_No, c.kn, c.kw);
        List<String> smsler = log.getPromoLog(c.Hotline_No, "Test13_HotlineAboneSMS");
        Iterator<String> iterator = smsler.iterator();
        String gidenSms1 = iterator.next();
        Assert.assertEquals(c.Hotline_SMS.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy", addDay()), gidenSms1);

        if (iterator.hasNext()) {
            String gidenSms2 = iterator.next();
            System.err.println("Giden 2. SMS: " + gidenSms2);
            assumeTrue("Test13_HotlineAboneSMS: Promo'dan birden fazla SMS gidiyor. Promo loguna bakiniz", false);

        }
        smsler = log.getAsbLog(c.Hotline_No, "Test13_HotlineAboneSMS");
        assumeFalse("Hotline Testinde Hem Promodan Hem ASB'den SMS gonderildi", smsler.iterator().hasNext());
        System.out.println("\n Test13_HotlineAboneSMS bitti");

    }

    @Test
    public void Test14_FirstCalldakiAboneSMSi() throws Exception {
        assumeTrue(!c.First_Calldaki_Abone.isEmpty());
        log.startPromoLog(c.First_Calldaki_Abone);
        log.startAsbLog(c.First_Calldaki_Abone);
        sms.sendSms(c.First_Calldaki_Abone, c.kn, c.kw);
        List<String> smsler = log.getPromoLog(c.First_Calldaki_Abone, "Test14_FirstCalldakiAboneSMS");

        Iterator<String> iterator = smsler.iterator();
        if (iterator.hasNext()) {
            String gidenSms1 = iterator.next();
            String kayitSms = c.First_Call_SMSi;
            String gun = addDay();
            kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);
            kayitSms = kayitSms.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
            kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
            kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

            Assert.assertEquals(kayitSms.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy", gun), gidenSms1);

            if (iterator.hasNext()) {
                String gidenSms2 = iterator.next();
                System.err.println("Giden 2. SMS: " + gidenSms2);
                assumeTrue("Test14_FirstCalldakiAboneSMS: Promo'dan birden fazla SMS gidiyor. Promo loguna bakiniz", false);

            }
        }
        List<String> asbSmsler = log.getAsbLog(c.First_Calldaki_Abone, "Test14_FirstCalldakiAboneSMS");
        iterator = asbSmsler.iterator();
        if (iterator.hasNext()) {
            String gidenSms1 = iterator.next();
            String kayitSms = c.First_Call_SMSi;
            String gun = addDay();
            kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);
            kayitSms = kayitSms.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
            kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
            kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

            Assert.assertEquals(kayitSms.replaceAll("DD/MM/YYYY|dd/mm/yyyy|dd.mm.yyyy", gun), gidenSms1);

            if (iterator.hasNext()) {
                String gidenSms2 = iterator.next();
                System.err.println("Giden 2. SMS: " + gidenSms2);
                assumeTrue("Test14_FirstCalldakiAboneSMS: ASB'den birden fazla SMS gidiyor. ASB loguna bakiniz. C:\\Logs klasorunde", false);

            }
        }
        assumeFalse("Test14_FirstCalldakiAboneSMS Testinde Hem Promodan Hem ASB'den SMS gonderildi", smsler.size() > 0 && asbSmsler.size() > 0);
        assumeFalse("Test14_FirstCalldakiAboneSMS Testinde SMS gonderilmedi", smsler.size() == 0 && asbSmsler.size() == 0);
        System.out.println("\n Test14_FirstCalldakiAboneSMS bitti");

    }


    @Test
    public void Test15_Abone_NonNT_ama_NT_Flagi_varsa_ucuz_almali() throws Exception {
        assumeTrue("Abone_NonNT_ama_NT_Flagi_var_testi_yapilsin_mi ayari evet olmadigindan test atlaniyor", c.Abone_NonNT_ama_NT_Flagi_var_testi_yapilsin_mi.equalsIgnoreCase("evet"));

        log.NTYap(c.Tarifesi_uygun_bir_msisdn);
        silNTFLAGAbone.join(4 * 60 * 1000);

        Log.log.startAsbLog(c.Tarifesi_uygun_bir_msisdn);
        if (!c.TCID.isEmpty())
            sms.sendSms(c.Tarifesi_uygun_bir_msisdn, c.kn, c.kw + " " + c.TCID);
        else
            sms.sendSms(c.Tarifesi_uygun_bir_msisdn, c.kn, c.kw);

        List<String> smsler = log.getAsbLog(c.Tarifesi_uygun_bir_msisdn, "Test15_Abone_NonNT_ama_NT_Flagi_varsa_ucuz_almali_nt_iken_alma_logu");
        Iterator<String> iterator = smsler.iterator();

        String gun = addDay();
        String kayitSms = c.kayitSms;
        kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);kayitSms = kayitSms.replaceAll("\\.$","");kayitSms = kayitSms.replaceAll("\\*","\\\\*");kayitSms = kayitSms.replaceAll("\\.","\\\\.");kayitSms = kayitSms.replaceAll("ayabilirsin J$","ayabilirsin :)");kayitSms = kayitSms.replaceAll("\\(","\\\\(");kayitSms = kayitSms.replaceAll("\\)","\\\\)");kayitSms = kayitSms.replaceAll("\\]","\\\\]");kayitSms = kayitSms.replaceAll("\\[","\\\\[");
        kayitSms = kayitSms.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
        kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
        kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

        System.out.println("NT iken alim.Beklenen kayit SMSi: \n" + kayitSms);

        String pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);
        String gidenSms1 = iterator.next();
        // Now create matcher object.
        Matcher m = r.matcher(gidenSms1);

        checkService(c.Tarifesi_uygun_bir_msisdn, c.NT_servis, "ACTIVE/STD/STD");
        checkBonus(c.Tarifesi_uygun_bir_msisdn, c.Paket_Kayit_Bonusu);

        Assert.assertTrue("Kayit SMSi yanlis: " + gidenSms1, m.matches());

        SilServisler silServisler = new SilServisler(c.Tarifesi_uygun_bir_msisdn);
        silServisler.start();
        if (iterator.hasNext()) {
            kayitSms = c.kayitSms2;
            kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);kayitSms = kayitSms.replaceAll("\\.$","");kayitSms = kayitSms.replaceAll("\\*","\\\\*");kayitSms = kayitSms.replaceAll("\\.","\\\\.");kayitSms = kayitSms.replaceAll("ayabilirsin J$","ayabilirsin :)");kayitSms = kayitSms.replaceAll("\\(","\\\\(");kayitSms = kayitSms.replaceAll("\\)","\\\\)");kayitSms = kayitSms.replaceAll("\\]","\\\\]");kayitSms = kayitSms.replaceAll("\\[","\\\\[");
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

        } else {
            Assert.assertEquals("2. Kayit SMSi gelmedi", "", c.kayitSms2);
        }
        //part2 nonnt olunca (30 gun gecince) bir daha almak ister
        log.NONNTYap(c.Tarifesi_uygun_bir_msisdn);
        BigDecimal money = getMoney(c.Tarifesi_uygun_bir_msisdn);
        Log.log.startAsbLog(c.Tarifesi_uygun_bir_msisdn);
        silServisler.join();
        if (!c.TCID.isEmpty())
            sms.sendSms(c.Tarifesi_uygun_bir_msisdn, c.kn, c.kw + " " + c.TCID);
        else
            sms.sendSms(c.Tarifesi_uygun_bir_msisdn, c.kn, c.kw);

        checkService(c.Tarifesi_uygun_bir_msisdn, c.NT_servis, "ACTIVE/STD/STD");//main point
        checkBonus(c.Tarifesi_uygun_bir_msisdn, c.Paket_Kayit_Bonusu);

        BigDecimal moneyAfter = getMoney(c.Tarifesi_uygun_bir_msisdn);
        BigDecimal fiyat = money.subtract(moneyAfter);
        Assert.assertEquals(c.NT_Fiyat + " TL dusmesi gerekirken " + fiyat + " TL dustu. Servis veya fiyati yanlis.", Double.parseDouble(new BigDecimal(c.NT_Fiyat).toString()), Double.parseDouble(fiyat.toString()), 0);

        smsler = log.getAsbLog(c.Tarifesi_uygun_bir_msisdn, "Test15_Abone_NonNT_ama_NT_Flagi_varsa_ucuz_almali.2.alim");
         iterator = smsler.iterator();

         gun = addDay();
         kayitSms = c.kayitSms;
        kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);kayitSms = kayitSms.replaceAll("\\.$","");kayitSms = kayitSms.replaceAll("\\*","\\\\*");kayitSms = kayitSms.replaceAll("\\.","\\\\.");kayitSms = kayitSms.replaceAll("ayabilirsin J$","ayabilirsin :)");kayitSms = kayitSms.replaceAll("\\(","\\\\(");kayitSms = kayitSms.replaceAll("\\)","\\\\)");kayitSms = kayitSms.replaceAll("\\]","\\\\]");kayitSms = kayitSms.replaceAll("\\[","\\\\[");
        kayitSms = kayitSms.replace("{sysdate+30}", gun.replace(".", "/") + " saat 23:59'a");
        kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI’a", gun.replace(".", "/") + " saat 23:59'a");//ters tirnak calismiyor ama ekledim
        kayitSms = kayitSms.replace("DD/MM/YYYY saat HH:MI'a", gun.replace(".", "/") + " saat 23:59'a");

        System.out.println("Beklenen kayit SMSi: \n" + kayitSms);
         pattern = "\\s?\\s?\\s?" + kayitSms + "\\.?\\.?\\.?\\s?\\s?\\s?";

        // Create a Pattern object
         r = Pattern.compile(pattern);
         gidenSms1 = iterator.next();
        // Now create matcher object.
         m = r.matcher(gidenSms1);

        Assert.assertTrue("Kayit SMSi yanlis: " + gidenSms1, m.matches());

        if (iterator.hasNext()) {
            kayitSms = c.kayitSms2;
            kayitSms = kayitSms.replaceAll("<BONUS_END_DATE>", gun);kayitSms = kayitSms.replaceAll("\\.$","");kayitSms = kayitSms.replaceAll("\\*","\\\\*");kayitSms = kayitSms.replaceAll("\\.","\\\\.");kayitSms = kayitSms.replaceAll("ayabilirsin J$","ayabilirsin :)");kayitSms = kayitSms.replaceAll("\\(","\\\\(");kayitSms = kayitSms.replaceAll("\\)","\\\\)");kayitSms = kayitSms.replaceAll("\\]","\\\\]");kayitSms = kayitSms.replaceAll("\\[","\\\\[");
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

        System.out.println("\n Test15_Abone_NonNT_ama_NT_Flagi_varsa_ucuz_almali bitti");

    }


    @Test
    public void _Loglar_C_Logs_Klasorunde_Zipleniyor() {
        byte[] buffer = new byte[102400];

        try {

            FileOutputStream fos = new FileOutputStream("C:\\Logs\\" + c.kw + "_Logs.zip");
            ZipOutputStream zos = new ZipOutputStream(fos);
            File dir = new File("C:\\Logs");
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".txt");
                }
            });

            for (File file : files) {
                ZipEntry ze = new ZipEntry(file.getName());
                zos.putNextEntry(ze);
                FileInputStream in = new FileInputStream("C:\\Logs\\" + file.getName());

                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                in.close();
                zos.closeEntry();

            }
            //remember close it
            zos.close();
            System.out.println("SMS Kanali loglarini iceren C:\\Logs\\" + c.kw + "_Logs.zip olusturuldu");
        } catch (Exception ex) {
            System.err.println("Loglar Arsivlenirken hata. UAC'yi kapatiniz. Bkz. Google.");
        }
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

        if (money == null)
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
            System.out.println("notExist.INFO: Servis bos birakildigindan kontrol edilmiyor.");
            return;
        }
        String resp = postSOAPXML(msisdn);
        Matcher m = Pattern.compile("<soldProducts><productName>" + servis + "</productName>").matcher(resp);
        assumeFalse(servis + " hala abone uzerinde", m.find());

        m = Pattern.compile("<Message>BSG_10000:SUCCESS</Message>").matcher(resp);
        assumeTrue("BSG_10000:SUCCESS alinamadi", m.find());

    }

    public String changeTariff(String msisdn, String tariff) {
        String resp = null;

        try {

            String soapBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:avea=\"http://oslee.orga.com/bsg/ra/ws/avea\">\n" +
                    "   <soapenv:Header/>\n" +
                    "   <soapenv:Body>\n" +
                    "      <avea:ChangeTariffPlanRequest>\n" +
                    "         <!--Optional:-->\n" +
                    "         <MiddlewareTransactionId>124151231212415123121233</MiddlewareTransactionId>\n" +
                    "         <opChannel>IVR</opChannel>\n" +
                    "         <!--Optional:-->\n" +
                    "         <keyName>MSISDN</keyName>\n" +
                    "         <!--Optional:-->\n" +
                    "         <keyValue>0" + msisdn + "</keyValue>\n" +
                    "         <!--Optional:-->\n" +
                    "         <opCost>0</opCost>\n" +
                    "         <!--Optional:-->\n" +
                    "         <newTariffId>" + tariff + "</newTariffId>\n" +
                    "     </avea:ChangeTariffPlanRequest>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";
            // Get target URL
            HttpClient httpclient = new DefaultHttpClient();

            StringEntity strEntity = new StringEntity(soapBody, "text/xml", "UTF-8");
            HttpPost post = new HttpPost("http://10.248.68.129:10003/changeTariffPlan");
            post.setHeader("SOAPAction", "ChangeTariffPlan");
            post.setEntity(strEntity);

            // Execute request
            HttpResponse response = httpclient.execute(post);
            HttpEntity respEntity = response.getEntity();

            if (respEntity != null) {
                resp = EntityUtils.toString(respEntity);

                Matcher m = Pattern.compile("<Message>BSG_10000:SUCCESS</Message>|BSG_10034:New tariff plan is equal to old tariff plan").matcher(resp);
                assertTrue("Yasakli Tarifeye gecerken hata alindi, tarife " + tariff + " yanlis veya sistemde calisma olabilir. BSG_10000:SUCCESS alinamadi:" + resp, m.find());
            } else {
                System.err.println("No Response");
            }

        } catch (Exception e) {
            System.err.println("WS SOAP exception = " + e.toString());
        }
        return resp;
    }

    public void checkBonus(String msisdn, String bonus) {
        if (bonus.isEmpty()) {
            System.out.println("INFO: Kayit Bonusu bos birakildigindan kontrol edilmiyor.");
            return;
        }
        String resp = postSOAPXML(msisdn);
        Matcher m = Pattern.compile("<balanceName>"+bonus+"</balanceName>")
                .matcher(resp);

        assumeTrue(bonus + " bulunamadi", m.find());

    }
    public void checkBonusNotExist(String msisdn, String bonus) {
        if (bonus.isEmpty()) {
            System.out.println("notExist.INFO: Kayit Bonusu bos birakildigindan kontrol edilmiyor.");
            return;
        }
        String resp = postSOAPXML(msisdn);
        Matcher m = Pattern.compile("<balanceName>"+bonus+"</balanceName>")
                .matcher(resp);

        assumeFalse(bonus + " hala uzerinde", m.find());
        m = Pattern.compile("<Message>BSG_10000:SUCCESS</Message>").matcher(resp);
        assumeTrue("BSG_10000:SUCCESS alinamadi", m.find());

    }

    public String multiplyBonusAmountsBy2(String testStr) {
        Pattern r = Pattern.compile("(?<=(paket|kapsam).* )([0-9]+)( +(dakika|dk))", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher m = r.matcher(testStr);
        Map<String, Long> dk = new HashMap<String, Long>(), sms = new HashMap<String, Long>(), mb = new HashMap<String, Long>(), mms = new HashMap<String, Long>();
        while (m.find()) {

            String amnt = m.group(2);
            testStr=testStr.replaceAll(amnt + "("+m.group(3)+")",String.valueOf(Long.parseLong(amnt) * 2)+"$1");

        }
        r = Pattern.compile("(?<=(paket|kapsam).* )([0-9]+)( +(sms|kisa mesaj))", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = r.matcher(testStr);
        while (m.find()) {
            String amnt = m.group(2);
            testStr=testStr.replaceAll(amnt + "("+m.group(3)+")",String.valueOf(Long.parseLong(amnt) * 2)+"$1");
        }

        r = Pattern.compile("(?<=(paket|kapsam).* )([0-9]+)( +(mb|mega ?byte))", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = r.matcher(testStr);
        while (m.find()) {
            String amnt = m.group(2);
            testStr=testStr.replaceAll(amnt + "("+m.group(3)+")",String.valueOf(Long.parseLong(amnt) * 2)+"$1");
        }

        r = Pattern.compile("(?<=(paket|kapsam).* )([0-9]+)( +mms|(multi ?med(i|y)a ?mesaj))", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = r.matcher(testStr);
        while (m.find()) {
            String amnt = m.group(2);
            testStr=testStr.replaceAll(amnt + "("+m.group(3)+")",String.valueOf(Long.parseLong(amnt) * 2)+"$1");
        }
        return testStr;

    }
}

