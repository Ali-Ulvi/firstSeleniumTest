package com.kafein.aveamerkez;

/**
 * Created by AUT via kafein on 20.11.2016.
 */

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Log {
    static Log log=new Log();
    public Log(){
        Security.insertProviderAt(new BouncyCastleProvider(),1);
    }
    void startAsbLog(String no) throws Exception{

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
                channel.setCommand("bash startLog.sh2  " + no );
                channel.connect();

                String msg = null;
                while ((msg = in.readLine()) != null) {
                    System.out.println(msg);
                }

                channel.disconnect();
                session.disconnect();


        }

    List<String> getAsbLog(String no) throws Exception{
List<String> smsList=new ArrayList<String>() ;

        JSch jsch = new JSch();
        Session session = jsch.getSession("asbtest", "10.248.67.110", 22);
        session.setPassword("asbtest123");
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        session.setConfig(config);

        session.connect();

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        channel.setCommand("bash stopAndShowlog.sh2  " + no );
        channel.connect();

        String msg = null;
        while ((msg = in.readLine()) != null) {
            System.out.println("Giden SMS:\n"+msg);
            smsList.add(msg);
        }

        channel.disconnect();
        session.disconnect();
return smsList;

    }

    void startPromoLog(String no) throws Exception{

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
        channel.setCommand("cd /javappl/promotionTest;sh startLog.sh2  " + no );
        channel.connect();

        String msg = null;
        while ((msg = in.readLine()) != null) {
            System.out.println(msg);
        }

        channel.disconnect();
        session.disconnect();


    }
    List<String> getPromoLog(String no) throws Exception{
        List<String> smsList=new ArrayList<String>() ;

        JSch jsch = new JSch();

        Session session = jsch.getSession("weblogic", "10.248.127.28", 22);
        session.setPassword("promo123");
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        session.setConfig(config);

        session.connect();

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
        channel.setCommand("cd /javappl/promotionTest; sh stopAndShowlog.sh2  " + no );
        channel.connect();

        String msg = null;
        while ((msg = in.readLine()) != null) {
            System.out.println("Giden SMS: \n"+msg);
            smsList.add(msg);
        }

        channel.disconnect();
        session.disconnect();
        return smsList;

    }

}





