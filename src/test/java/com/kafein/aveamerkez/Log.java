package com.kafein.aveamerkez;

/**
 * Created by AUT via kafein on 20.11.2016.
 */

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import java.io.*;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Log {
    static Log log = new Log();


    public Log() {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);

    }

    void startAsbLog(String no) throws Exception {

        System.out.println("AsbLog gathering start: " + no);
        JSch jsch = new JSch();


        Session session = jsch.getSession("asbtest", "10.248.67.110", 22);
        session.setPassword("asbtest123");
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        session.setConfig(config);

        session.connect();

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        channel.setCommand("bash startLog.sh2  " + no);
        channel.connect();

        String msg = null;
        while ((msg = in.readLine()) != null) {
            System.out.println(msg);
        }

        channel.disconnect();
        session.disconnect();


    }

    List<String> getAsbLog(String no,String testName) throws Exception {
        List<String> smsList = new ArrayList<String>();

        JSch jsch = new JSch();
        Session session = jsch.getSession("asbtest", "10.248.67.110", 22);
        session.setPassword("asbtest123");
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        session.setConfig(config);

        session.connect();

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        writeFile(no,testName);
        channel.setCommand("bash stopAndShowlog.sh2  " + no);
        channel.connect();

        String msg = null;
        while ((msg = in.readLine()) != null) {
            System.out.println("Giden SMS:\n" + msg);
            smsList.add(msg);
        }
        channel.disconnect();
        session.disconnect();

        return smsList;

    }

    void startPromoLog(String no) throws Exception {

        System.out.println("PromoLog gathering start: " + no);
        JSch jsch = new JSch();


        Session session = jsch.getSession("weblogic", "10.248.127.28", 22);
        session.setPassword("promo123");
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        session.setConfig(config);

        session.connect();

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        channel.setCommand("cd /javappl/promotionTest;sh startLog.sh2  " + no);
        channel.connect();

        String msg = null;
        while ((msg = in.readLine()) != null) {
            System.out.println(msg);
        }

        channel.disconnect();
        session.disconnect();


    }

    List<String> getPromoLog(String no,String testName) throws Exception {
        List<String> smsList = new ArrayList<String>();

        JSch jsch = new JSch();

        Session session = jsch.getSession("weblogic", "10.248.127.28", 22);
        session.setPassword("promo123");
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        session.setConfig(config);

        session.connect();

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        writeFilePromo(no,testName);
        channel.setCommand("cd /javappl/promotionTest; sh stopAndShowlog.sh2  " + no);
        channel.connect();

        String msg = null;
        while ((msg = in.readLine()) != null) {
            System.out.println("Giden SMS: \n" + msg);
            smsList.add(msg);
        }

        channel.disconnect();
        session.disconnect();

        return smsList;

    }

    public void writeFile(String no, String file) throws Exception {

        JSch jsch = new JSch();
        Session session = jsch.getSession("asbtest", "10.248.67.110", 22);
        session.setPassword("asbtest123");
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        session.setConfig(config);

        session.connect();

        BufferedReader in;

        ChannelExec channel2 = (ChannelExec) session.openChannel("exec");
        in = new BufferedReader(new InputStreamReader(channel2.getInputStream()));
        // System.err.println("cd /export/home/asbtest; lineNo=$(cat LastLineNumber" + no + ");echo $lineNo;lineNo=$(expr $lineNo + 1);cd /logs/asb_m1;pwd ;tid=$(tail +$lineNo  asb_m1_app.log|grep "+no+"|grep '<TID>'|head -1|sed  's/.*<TID>\\([^<]*\\).*/\\1/');echo $tid x $lineNo;tail +$lineNo  asb_m1_app.log|grep -v PHS_GET_PRODUCT_MAPPING|egrep $tid\\|"+no);
        channel2.setCommand("cd /export/home/asbtest; lineNo=$(cat LastLineNumber" + no + ");lineNo=$(expr $lineNo + 1);cd /logs/asb_m1;pwd ;tid=$(tail +$lineNo  asb_m1_app.log|grep "+no+"|grep '<TID>'|head -1|sed  's/.*<TID>\\([^<]*\\).*/\\1/');echo $tid;tail +$lineNo  asb_m1_app.log|egrep -v PHS_GET_PRODUCT_MAPPING\\|GetQueryDataResponse|egrep $tid\\|"+no);
        channel2.connect();
        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("C:/Logs/" + file + "__ASBLog.txt"), "utf-8"));

        String msg = null;
        writer.write(file + " " + no+" ");
        while ((msg = in.readLine()) != null) {
            writer.write(msg + "\n");
        }
        writer.close();
        channel2.disconnect();
        session.disconnect();


    }


    public void writeFilePromo(String no, String file) throws Exception {

        JSch jsch = new JSch();
        Session session = jsch.getSession("weblogic", "10.248.127.28", 22);
        session.setPassword("promo123");

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        session.setConfig(config);

        session.connect();

        BufferedReader in;

        ChannelExec channel2 = (ChannelExec) session.openChannel("exec");
        in = new BufferedReader(new InputStreamReader(channel2.getInputStream()));
        channel2.setCommand("cd /javappl/promotionTest;lineNo=$(cat LastLineNumber"+no+");lineNo=$(expr $lineNo + 1);cd /javappl/kanbanPromo/log/;pwd ;tail +$lineNo  promo.log|grep " + no);

        channel2.connect();
        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("C:/Logs/" + file + "__PromoLog.txt"), "utf-8"));

        String msg = null;
        writer.write(file + " " + no+" ");
        while ((msg = in.readLine()) != null) {
            writer.write(msg + "\n");
        }
        writer.close();
        channel2.disconnect();
        session.disconnect();


    }

    @Test
    public void writeFileTest() throws Exception {

        JSch jsch = new JSch();
        // Session session = jsch.getSession("asbtest", "10.248.67.110", 22);
        //session.setPassword("asbtest123");
        Session session = jsch.getSession("weblogic", "10.248.127.28", 22);
        session.setPassword("promo123");
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        session.setConfig(config);

        session.connect();

        BufferedReader in;

        ChannelExec channel2 = (ChannelExec) session.openChannel("exec");
        in = new BufferedReader(new InputStreamReader(channel2.getInputStream()));
        channel2.setCommand("cd /javappl/promotionTest;lineNo=$(cat LastLineNumber);echo $lineNo;lineNo=$(expr $lineNo + 1);echo $lineNo;cd /javappl/kanbanPromo/log/;pwd ;tail +$lineNo  promo.log|grep 05");
        channel2.connect();
        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("C:/" + "file" + ".txt"), "utf-8"));

        String msg = null;

        while ((msg = in.readLine()) != null) {
            writer.write(msg + "\n");
        }
        writer.close();
        channel2.disconnect();
        session.disconnect();


    }

    void NTYap(String no) throws Exception {

        System.out.println("NTYap start: " + no);
        JSch jsch = new JSch();


        Session session = jsch.getSession("mrte1", Config.config.ip, 22);
        session.setPassword(Config.config.mrte1Pw);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        session.setConfig(config);

        session.connect();

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        channel.setCommand("source .profile;30gun 0" + no);
        channel.connect();

        String msg = null;
        while ((msg = in.readLine()) != null) {
            System.out.println(msg);
        }

        channel.disconnect();
        session.disconnect();


    }
    void NONNTYap(String no) throws Exception {

        System.out.println("NonNT yapma start: " + no);
        JSch jsch = new JSch();


        Session session = jsch.getSession("mrte1", Config.config.ip, 22);
        session.setPassword(Config.config.mrte1Pw);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        session.setConfig(config);

        session.connect();

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        channel.setCommand("source .profile;31gun 0" + no);
        channel.connect();

        String msg = null;
        while ((msg = in.readLine()) != null) {
            System.out.println(msg);
        }

        channel.disconnect();
        session.disconnect();


    }

}





