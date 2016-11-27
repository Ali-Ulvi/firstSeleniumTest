package com.kafein.aveamerkez;

/**
 * Created by AUT via kafein on 15.11.2016.
 * Aboneyi Temizler (paket ve bonuslarini siler)
 */

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

public class Sil2 extends Thread {
    String no, ip, pw;

    public Sil2(String no) {
        this.no = no;
        Config c=Config.config;
        this.pw = c.mrte1Pw;
        this.ip = c.ip;
    }

    public void run() {
        if (Config.config.temizle.equalsIgnoreCase("evet") && ! no.isEmpty()) {
            no="0"+no;
            System.out.println("Cleaning Subscriber's Packs and bonus: " + no);
            JSch jsch = new JSch();

            try {
                Session session = jsch.getSession("mrte1", ip, 22);
                session.setPassword(pw);
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);

                session.connect();


                ChannelExec channel = (ChannelExec) session.openChannel("exec");
                BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
                channel.setCommand("source .profile;tumBonusSil " + no + ";tumPaketSilFast " + no);
                channel.connect();

                String msg = null;
                while ((msg = in.readLine()) != null) {
                    System.out.println(msg);
                }

                channel.disconnect();
                session.disconnect();
                System.out.println("Completed Cleaning : "  + no);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
