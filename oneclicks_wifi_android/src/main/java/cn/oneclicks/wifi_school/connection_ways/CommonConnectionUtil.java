package cn.oneclicks.wifi_school.connection_ways;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by tianwai on 24/10/2016.
 */

public class CommonConnectionUtil {

    String checkPortalState (){
        String urlStr = "http://www.baidu.com";
        URL url1;
        String urlUI = "";
        try
        {
            url1 = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.getResponseCode();
            urlUI = conn.getURL().toString();
            if(urlUI.contains("baidu"))
                return "";
        }
        catch (Exception e) {

            e.printStackTrace();
            return "-1";
        }
        return urlUI;
    }

}
