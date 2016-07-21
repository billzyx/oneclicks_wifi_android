package cn.oneclicks.wifi_school.connection_ways;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import cn.oneclicks.wifi.MyApplication;
import cn.oneclicks.wifi_school.HttpUtil;

import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;



public class H3C_2014 implements ConnectionWays{
	
	
	
	String server = "http://10.130.10.3:8080";
	
	public boolean login(String WifiName,String username,String password)
	{
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} 
		
		server = "http://" + urlUI.split("//")[1].split("/")[0];
		
		String url= server + "/portal/pws?t=li";
		String pwd64=null ;
        pwd64=Base64.encodeToString(password.getBytes(),Base64.DEFAULT);
		String line_wrong="JTdCJTIycG9ydFNlcnZJbmNsdWRlRmFpbGVkQ29kZSUyMiUzQSUyMjY0MDAzJTIyJTJDJTIycG9ydFNlcnZJbmNsdWRlRmFpbGVkUmVhc29uJTIyJTNBJTIyRTY0MDAzJTNBJUU3JTk0JUE4JUU2JTg4JUI3JUU1JTkwJThEJUU2JTg4JTk2JUU1JUFGJTg2JUU3JUEwJTgxJUU5JTk0JTk5JUU4JUFGJUFGJUUzJTgwJTgyJTIyJTJDJTIyZV9jJTIyJTNBJTIycG9ydFNlcnZJbmNsdWRlRmFpbGVkQ29kZSUyMiUyQyUyMmVfZCUyMiUzQSUyMnBvcnRTZXJ2SW5jbHVkZUZhaWxlZFJlYXNvbiUyMiUyQyUyMmVycm9yTnVtYmVyJTIyJTNBJTIyNyUyMiU3RA";
		HashMap<String, String> postmap = new HashMap<String, String>();
		postmap.put("userName", username);
		postmap.put("userPwd", pwd64);
		HttpUtil hp = new HttpUtil();
		String re = hp.post(url, postmap);
		Log.v("postback", re); 
		SaveServer();
		if(!re.equalsIgnoreCase(line_wrong)&&!re.equalsIgnoreCase(""))
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
		HttpUtil.post( server + "/portal/pws?t=lo", null);
		Log.v("logout","1");
	}
	
	void SaveServer()
	{
		SharedPreferences setting_user = MyApplication.getContext().getSharedPreferences("H3C_2014", 0);
		SharedPreferences.Editor setting_user_editor = setting_user.edit();
		setting_user_editor.putString("server",server);
		setting_user_editor.commit();
	}
	
	public H3C_2014()
	{
		SharedPreferences setting_user = MyApplication.getContext().getSharedPreferences("H3C_2014", 0);
		server = setting_user.getString("server","");
	}

}
