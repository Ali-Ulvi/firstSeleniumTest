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

public class Sil extends Thread {
    String no, ip, pw;

    //not used constructor
    public Sil() {
        this.no = AveaMerkez.msisdn;
        this.pw = "mrte1123";
        this.ip = "10.248.68.127";
    }

    public Sil(String no) {
        this.no = no;
        this.pw = AveaMerkez.mrte1Pw;
        this.ip = AveaMerkez.ip;
    }

    public void run() {
        if (AveaMerkez.temizle.equalsIgnoreCase("evet") && ! no.isEmpty()) {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Complete: Cleaning Subscriber's Packs and bonus: "  + no);
        }
    }


}
