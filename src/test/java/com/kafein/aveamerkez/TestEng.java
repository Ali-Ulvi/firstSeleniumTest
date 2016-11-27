package com.kafein.aveamerkez;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by AUT via kafein on 26.11.2016.
 */
public class TestEng {
    @org.junit.Test
    public void regTest() {
        Pattern r = Pattern.compile("(?<=(paket|kapsam).* )([0-9]+)( +(dakika|dk))", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        String testStr = "DOLU 400 MB Paketi kapsaminda DD/MM/YYYY tarihine kadar kullanabileceginiz yurt ici her yone 750 dk, avea ici 300 dakika her yone 1000 SMS ve 3072 MB cepten internet kullanim hakkiniz bulunmaktadir.";
        // Now create matcher object.
        Matcher m = r.matcher(testStr);
        Map<String, Long> dk = new HashMap<String, Long>(), sms = new HashMap<String, Long>(), mb = new HashMap<String, Long>(), mms = new HashMap<String, Long>();
        while (m.find()) {
            System.out.println("Found value: " + m.group(2));
            String amnt = m.group(2);
            testStr = testStr.replaceAll(amnt + "(" + m.group(3) + ")", String.valueOf(Long.parseLong(amnt) * 2) + "$1");
            dk.put(amnt + m.group(3), Long.parseLong(amnt) * 2);
        }
        r = Pattern.compile("(paket|kapsam).* ([0-9]+)( +(sms|kisa mesaj))", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = r.matcher(testStr);
        while (m.find()) {
            System.out.println("Found value: " + m.group(2));
            String amnt = m.group(2);

            sms.put(amnt + m.group(3), Long.parseLong(amnt) * 2);
            testStr = testStr.replaceAll(amnt + "(" + m.group(3) + ")", String.valueOf(Long.parseLong(amnt) * 2) + "$1");
        }

        r = Pattern.compile("(paket|kapsam).* ([0-9]+)( +(mb|mega ?byte))", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = r.matcher(testStr);
        while (m.find()) {
            System.out.println("Found value: " + m.group(2));
            String amnt = m.group(2);

            mb.put(amnt + m.group(3), Long.parseLong(amnt) * 2);
            testStr = testStr.replaceAll(amnt + "(" + m.group(3) + ")", String.valueOf(Long.parseLong(amnt) * 2) + "$1");
        }

        r = Pattern.compile("(paket|kapsam).* ([0-9]+)( +mms|(multi ?med(i|y)a ?mesaj))", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        m = r.matcher(testStr);
        while (m.find()) {
            System.out.println("Found value: " + m.group(2));
            String amnt = m.group(2);

            mms.put(amnt + m.group(3), Long.parseLong(amnt) * 2);
            testStr = testStr.replaceAll(amnt + "(" + m.group(3) + ")", String.valueOf(Long.parseLong(amnt) * 2) + "$1");
        }

        for (Map.Entry<String, Long> entry : mb.entrySet()) {
            System.out.println(testStr + entry.getKey() + "/" + entry.getValue());


        }
        for (Map.Entry<String, Long> entry : mb.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }

        for (Map.Entry<String, Long> entry : sms.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }

        for (Map.Entry<String, Long> entry : mms.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }
    }


    @Test
    public void replace() {

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

