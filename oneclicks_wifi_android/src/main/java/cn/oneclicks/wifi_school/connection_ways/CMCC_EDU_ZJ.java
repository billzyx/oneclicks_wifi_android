package cn.oneclicks.wifi_school.connection_ways;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.SharedPreferences;


import cn.oneclicks.wifi.MyApplication;
import cn.oneclicks.wifi_school.HttpUtil;

public class CMCC_EDU_ZJ implements ConnectionWays{
	
	 String server = "";
	 String wlanuserip = "";
	 String wlanacname = "";
	 String wlanacip = "";
	 String logincode = "";
	 String username = "";
	
	public boolean login(String WifiName,String username,String password)
	{
		this.username = username;
		String urlStr = "http://www.baidu.com";
		URL url;
		String urlUI = "";
		try
		{
			url = new URL(urlStr);
		    HttpURLConnection conn = (HttpURLConnection) url.openConnection();   
		    conn.getResponseCode();
		    urlUI = conn.getURL().toString();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} 
        
		if(urlUI.equalsIgnoreCase(""))
			return false;
		
		server = "http://" + urlUI.split("//")[1].split("/")[0];
        
        String s[] =urlUI.split("[?]")[1].split("&");
        
        for(String arg : s)
        {
        	if(arg.split("=")[0].equalsIgnoreCase("wlanuserip"))
                wlanuserip = arg.split("=")[1];
            else if(arg.split("=")[0].equalsIgnoreCase("wlanacname"))
                wlanacname = arg.split("=")[1];
            else if(arg.split("=")[0].equalsIgnoreCase("wlanacip"))
                wlanacip = arg.split("=")[1];
        }
        
        
 		String re = HttpUtil.post(urlUI, null);
 		
 		String pstr = "<form id='Wlan_Login' name='login' method='post' action=\"([^\"]+)\""; 
        Pattern p = Pattern.compile(pstr);  
        Matcher m = p.matcher(re); 
        //提交网址
        String posturl;
        if(m.find())
        	posturl = m.group(1) ;
        else
        	return false;
        
        logincode = posturl.split("[?]")[1];
	        
		
		HashMap<String, String> postmap = new HashMap<String, String>();
		postmap.put("wlanUserIp", wlanuserip);
		postmap.put("wlanAcName", wlanacname);
		postmap.put("wlanAcIp", wlanacip);
		postmap.put("userName", username);
		postmap.put("userPwd", password);
		HttpUtil hp2 = new HttpUtil();
		String re2 = hp2.post(posturl, postmap);
		
		SaveServer();
		
		if(!re2.contains("成功"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void logout()
	{
		HashMap<String, String> postmap = new HashMap<String, String>();
		postmap.put("wlanUserIp", wlanuserip);
		postmap.put("wlanAcName", wlanacname);
		postmap.put("wlanAcIp", wlanacip);
		postmap.put("passType", "1");
		postmap.put("isLocalUser", "1");
		postmap.put("userName", username);
		HttpUtil.post(server + "/zmcc/portalLogout.wlan?isCloseWindow=N&" + logincode, postmap);
	}
	
	void SaveServer()
	{
		SharedPreferences setting_user = MyApplication.getContext().getSharedPreferences("CMCC_EDU_ZJ", 0);
		SharedPreferences.Editor setting_user_editor = setting_user.edit();
		setting_user_editor.putString("server",server);
		setting_user_editor.putString("wlanuserip",wlanuserip);
		setting_user_editor.putString("wlanacname",wlanacname);
		setting_user_editor.putString("wlanacip",wlanacip);
		setting_user_editor.putString("username",username);
		setting_user_editor.putString("logincode",logincode);
		setting_user_editor.commit();
	}
	
	public CMCC_EDU_ZJ()
	{
		SharedPreferences setting_user = MyApplication.getContext().getSharedPreferences("CMCC_EDU_ZJ", 0);
		wlanuserip = setting_user.getString("wlanuserip","");
		wlanacname = setting_user.getString("wlanacname","");
		wlanacip = setting_user.getString("wlanacip","");
		username = setting_user.getString("username","");
		logincode = setting_user.getString("logincode","");
		server = setting_user.getString("server","");
	}

}
