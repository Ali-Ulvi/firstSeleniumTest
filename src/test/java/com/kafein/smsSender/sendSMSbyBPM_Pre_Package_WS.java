package com.kafein.smsSender;


import com.kafein.aveamerkez.Config;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by AUT via kafein on 19.07.2017.
 */
public class sendSMSbyBPM_Pre_Package_WS {
    static Config c = Config.config;
    public void sendSms(String no, String TP, String KW) throws Exception {
        sendSms( no,  TP,  KW, Long.parseLong(c.sleep));

    }
    public void sendSms(String no, String TP, String KW, long sleepSeconds) throws Exception {
        //send WS
        postSOAPXML(  no,  TP,   KW );
        Thread.sleep(sleepSeconds * 1000);
    }

    public String postSOAPXML(String msisdn,String TP, String KW ) {
        String resp = null;
        try {

            String soapBody = "<IWIS_IN_DATA_UNIT>\n" +
                    "   <IWIS_IN_HEADER version=\"1.0\">\n" +
                    "      <SYSTEM>VAS_PROMO</SYSTEM>\n" +
                    "      <SERVICE>BPM_PRE_PACKAGE_ASSIGNMENT</SERVICE>\n" +
                    "      <TID>GO8EKBEKmmQZWsPyrcnYJ47Q</TID>\n" +
                    "   </IWIS_IN_HEADER>\n" +
                    "   <IWIS_SERVICE_PARAMS>\n" +
                    "      <XML>\n" +
                    "         <sch:PrepaidPackageAssignmentProcessRequest xmlns:sch=\"avea/bpm/schemas\">\n" +
                    "            <MSISDN>"+msisdn+"</MSISDN>\n" +
                    "            <CHANNEL>2</CHANNEL>\n" +
                    "            <KEYWORD>"+KW+"</KEYWORD>\n" +
                    "            <RULE_ID>"+TP+"</RULE_ID>\n" +
                    "         </sch:PrepaidPackageAssignmentProcessRequest>\n" +
                    "      </XML>\n" +
                    "   </IWIS_SERVICE_PARAMS>\n" +
                    "</IWIS_IN_DATA_UNIT>";
            // Get target URL
            HttpClient httpclient = new DefaultHttpClient();

            StringEntity strEntity = new StringEntity(soapBody, "text/xml", "UTF-8");
            HttpPost post = new HttpPost("http://10.248.66.212/WEB/IWIS");
            post.setHeader("SOAPAction", "BPM_Services");
            post.setEntity(strEntity);

            // Execute request
            HttpResponse response = httpclient.execute(post);
            HttpEntity respEntity = response.getEntity();

            if (respEntity != null) {
                resp = EntityUtils.toString(respEntity);

                System.out.println(resp);

                assertTrue("BPM Patlak. Calisma/kesinti maili/rezervasyonu yoksa Test Support'la gorusunuz.", !(resp.toLowerCase().contains("exception"))&&resp.contains("PrepaidPackageAssignmentProcessResponse"));

            } else {
                System.err.println("No Response from BPM");
            }

        } catch (Exception e) {
            System.err.println("WS SOAP exception = " + e.toString());
        }
        return resp;
    }

}
