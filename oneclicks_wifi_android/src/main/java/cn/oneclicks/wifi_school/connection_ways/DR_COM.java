package cn.oneclicks.wifi_school.connection_ways;

import android.content.SharedPreferences;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import cn.oneclicks.wifi.MyApplication;
import cn.oneclicks.wifi_school.HttpUtil;

/**
 * Created by tianwai on 23/09/2016.
 */

public class DR_COM implements ConnectionWays {
    @Override
    public boolean login(String WifiName, String username, String password) {
        String urlStr = "http://www.baidu.com";
        URL url1;
        String urlUI = "";
        try
        {
            url1 = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.getResponseCode();
            urlUI = conn.getURL().toString();
        }
        catch (Exception e) {

            e.printStackTrace();
            return false;
        }

        server = "http://" + urlUI.split("//")[1].split("/")[0];

        String url= server + "/0.htm";

        HashMap<String, String> postmap = new HashMap<String, String>();
        postmap.put("DDDDD", username);
        postmap.put("upass", password);
        postmap.put("0MKKey","");
        HttpUtil hp = new HttpUtil();
        String re = hp.post(url, postmap);
        Log.v("postback", re);
        SaveServer();
        if(!re.contains("成功"))
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    @Override
    public boolean logout() {
        if(HttpUtil.post( server + "/F.htm", null) != null)
            return true;
        else
            return false;
    }

    void SaveServer()
    {
        SharedPreferences setting_user = MyApplication.getContext().getSharedPreferences("DR_COM", 0);
        SharedPreferences.Editor setting_user_editor = setting_user.edit();
        setting_user_editor.putString("server",server);
        setting_user_editor.commit();
    }

    public DR_COM()
    {
        SharedPreferences setting_user = MyApplication.getContext().getSharedPreferences("DR_COM", 0);
        server = setting_user.getString("server","");
    }

    String server = "";
}
