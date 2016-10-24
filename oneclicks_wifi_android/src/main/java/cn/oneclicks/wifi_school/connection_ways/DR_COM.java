package cn.oneclicks.wifi_school.connection_ways;

import android.content.SharedPreferences;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import cn.oneclicks.wifi.MyApplication;
import cn.oneclicks.wifi_school.HttpUtil;
import cn.oneclicks.wifi_school.StreamTool;

/**
 * Created by tianwai on 23/09/2016.
 */

public class DR_COM extends CommonConnectionUtil implements ConnectionWays {
    @Override
    public boolean login(String WifiName, String username, String password) {

        String urlUI = super.checkPortalState();
        if(urlUI.equals("-1"))
            return false;
        if(urlUI.equals(""))
            return true;

        server = "http://" + urlUI.split("//")[1].split("/")[0];

        String url= server + "/0.htm";

        HashMap<String, String> postmap = new HashMap<String, String>();
        postmap.put("DDDDD", username);
        postmap.put("upass", password);
        postmap.put("0MKKey","");
        HttpUtil hp = new HttpUtil();
        String re = hp.post(url, postmap);
//        try {
//            re = new String(re.getBytes("gb2312"),"utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        Log.v("postback", re);
        SaveServer();
        if(re.contains("Msg=01")){
            return false;
        }
        else if(re.contains(username))
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
//        Log.v("logout", "start啊");
//
//        String re = HttpUtil.post( server + "/F.htm", null) ;
//        //Log.v("postback", re);
//        if(re != null){
//            try {
//                re = new String(re.getBytes("gb2312"),"utf-8");
//                Log.v("postback", re);
//                return true;
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            return false;
//        }
//        else
//            return false;
        try {
            URL url = new URL( server + "/F.htm");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            InputStream inStream = conn.getInputStream();
            byte[] data = StreamTool.inputStream2Byte(inStream);
            String result = new String(data, "UTF-8");
            Log.v("postback", result);
            return result.contains("Msg=14");
        }catch (Exception e){
            return  false;
        }
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
