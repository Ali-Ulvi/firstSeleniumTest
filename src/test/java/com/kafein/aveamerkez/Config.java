package com.kafein.aveamerkez;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by AUT via kafein on 19.11.2016.
 */
public class Config {
     String  kayitSms, msisdn, day, iptalSMS, kalanSMS, yetersizBakiyemsisdn, yetersizBakiyeKayitSMS; //Config.txt icin degiskenler
     String NonNtyetersizBakiyeKayitSMS, NonNtyetersizBakiyeMsisdn, NonNT_msisdn, NonNT_Kayit_Mesaji, TCID,NT_Fiyat,NonNT_Fiyat,NT_servis,NonNT_servis,bakiyesizAlim;
     String ip,mrte1Pw,temizle,gecko,kanal,sleep,kayitSms2,NonNT_Kayit_Mesaji2,kwNonNt,kw,kn;
    static Config config=new Config();//singleton pattern

    public Config(){
        Properties prop = new Properties();
        InputStream input = null;
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));

        try {

            input = new FileInputStream("Config.txt");

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            kayitSms = prop.getProperty("NT_Kayit_Mesaji");
            kayitSms2 = prop.getProperty("NT_Kayit_Mesaji2");
            kanal = prop.getProperty("SMS_kanali_promo_mu_ir_mi");
            sleep = prop.getProperty("Sleep_seconds_after_sending_SMS_before_checking_logs");
            msisdn = prop.getProperty("NTmsisdn");
            kw = prop.getProperty("SMS_Keyword");
            kwNonNt = prop.getProperty("SMS_Keyword_NonNt");
            kn = prop.getProperty("KisaNumara");
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
            gecko = prop.getProperty("Firefox_Driver_Path");

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
}
