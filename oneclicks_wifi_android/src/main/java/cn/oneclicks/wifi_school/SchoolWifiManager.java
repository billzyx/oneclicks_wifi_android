package cn.oneclicks.wifi_school;

import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.util.Log;
import cn.oneclicks.wifi.AssetsDatabaseManager;
import cn.oneclicks.wifi.MyApplication;
import cn.oneclicks.wifi.WifiAdmin;
import cn.oneclicks.wifi_school.connection_ways.*;

public class SchoolWifiManager {




	public List<String> getProvinceList(){
		List<String> s = new ArrayList<String>();

		Cursor cursor = schoolDB.rawQuery("select province_name from province",null);
		while(cursor.moveToNext())
		{
			s.add(cursor.getString(cursor.getColumnIndex("province_name")));
		}
		cursor.close();
		return s;
	}
	
	public List<String> getSchoolList()
	{
		List<String> s = new ArrayList<String>();

		
		Cursor cursor = schoolDB.rawQuery("select school_name from school",null);
		while(cursor.moveToNext())
		{
			s.add(cursor.getString(cursor.getColumnIndex("school_name")));
		}
		cursor.close();
		return s;
	}

	public List<String> getProvinceSchoolList(String province)
	{
		List<String> s = new ArrayList<String>();


		Cursor cursor = schoolDB.rawQuery("select school_name from school where province = ?",new String[]{province});
		while(cursor.moveToNext())
		{
			s.add(cursor.getString(cursor.getColumnIndex("school_name")));
		}
		cursor.close();
		return s;
	}
	
	
	public List<String> getSchoolWifiList(String school)
	{
		if(school.equalsIgnoreCase(""))
			return null;
		//HashMap<String, String[]> map = this.getSchoolWifiHashMap(school);
		List<String> list = new ArrayList<String>();
//		Iterator iter = map.entrySet().iterator();
//		while (iter.hasNext()) {
//				Map.Entry entry = (Map.Entry) iter.next();
//				list.add((String) entry.getKey());
//				}
		Cursor cursor = schoolDB.rawQuery("select SSID_name from SSID where school = ?",new String[]{school});
		while(cursor.moveToNext())
		{
			list.add(cursor.getString(cursor.getColumnIndex("SSID_name")));
		}
		cursor.close();
		
		return list;
	}
	
	private String[] getSchoolWifiPortal(String school,String WifiName)
	{
		if(school.equalsIgnoreCase(""))
			return null;
		if(WifiName.equalsIgnoreCase(""))
			return null;
		//return getSchoolWifiHashMap(school).get(WifiName);
		Cursor cursor = schoolDB.rawQuery("select portal1,portal2 from SSID where school = ? and SSID_name = ?",new String[]{school,WifiName});
		
		if(cursor!=null)
			cursor.moveToNext();
		else
			return null;
		String s[] = null;
		if(cursor.getString(cursor.getColumnIndex("portal1"))!=null)
			s = new String[]{cursor.getString(cursor.getColumnIndex("portal1")),cursor.getString(cursor.getColumnIndex("portal2"))};

		cursor.close();
		
		return s;
	}
	
	public static String getSchoolWifiType(String school,String WifiName)
	{
		if(school.equalsIgnoreCase(""))
			return null;
		if(WifiName.equalsIgnoreCase(""))
			return null;
		
		Cursor cursor = schoolDB.rawQuery("select accounts_type from SSID where school = ? and SSID_name = ?",new String[]{school,WifiName});
		
		if(cursor!=null)
			cursor.moveToNext();
		else
			return null;
		String s = null;
		Log.v("sql",school+WifiName);
		s = cursor.getString(cursor.getColumnIndex("accounts_type"));
			

		cursor.close();
		
		return s;
	}
	
	public SchoolWifiManager(String school)
	{
		this.school = school;
		//init();
	}
	
	public SchoolWifiManager()
	{
		SharedPreferences setting_user = MyApplication.getContext().getSharedPreferences("setting_school_wifi", 0);
		String school = setting_user.getString("school_name","");
		this.school = school;
		//init();
	}
	
	String school = null;
	static SQLiteDatabase schoolDB = null;
	
	static
	{
		// 初始化，只需要调用一次  
		AssetsDatabaseManager.initManager(MyApplication.getContext());  
		// 获取管理对象，因为数据库需要通过管理对象才能够获取  
		AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();  
		// 通过管理对象获取数据库  
		schoolDB = mg.getDatabase("school.db");
		Log.v("DB","get succeed");
	}
	
	public boolean login()
	{
		if(school==null||school.equalsIgnoreCase(""))
			return false;
		if(getWifiName().equalsIgnoreCase(""))
			return false;
		String[] WifiPortal = getSchoolWifiPortal(school,getWifiName());
		String type = getSchoolWifiType(school,getWifiName());
		if(WifiPortal==null)
			return false;
		int i = 0;
		for(String Portal : WifiPortal)
		{
			i++;
			if(Portal==null)
				break;
			ConnectionWays sc;
			try{
	            sc=(ConnectionWays)Class.forName("cn.oneclicks.wifi_school.connection_ways." + Portal).newInstance();
	            Method m = sc.getClass().getDeclaredMethod("login",new Class[]{String.class,String.class,String.class});
	            if(type.equalsIgnoreCase("1"))
	            	m.invoke(sc,new Object[] {getWifiName(),getUserName(),getPassword()});
	            else if(type.equalsIgnoreCase("2"))
	            	m.invoke(sc,new Object[] {getWifiName(),getUserName2(),getPassword2()});
	            else if(type.equalsIgnoreCase("3"))
	            {
	            	if(i==1)
	            		m.invoke(sc,new Object[] {getWifiName(),getUserName(),getPassword()});
	            	else if(i==2)
	            		m.invoke(sc,new Object[] {getWifiName(),getUserName2(),getPassword2()});
	            }
	        }catch(Exception e){
	            e.printStackTrace();
	            Log.v("login","0");
	            return false;
	        }
			
			
			//if(Portal.equalsIgnoreCase("H3C_2014")){sc = new H3C_2014(); if(!sc.login(getWifiName(),getUserName(),getPassword())) return false;}
			//else if(Portal.equalsIgnoreCase("CMCC_ZJ")){sc = new CMCC_ZJ(); if(!sc.login(getWifiName(),getUserName2(),getPassword2())) return false;}
			
			
			//else {return false;}		
		}
		return true;
	}
	
	public boolean logout()
	{
		if(school==null||school.equalsIgnoreCase(""))
			return false;
		if(getWifiName().equalsIgnoreCase(""))
			return false;
		String[] WifiPortal = getSchoolWifiPortal(school,getWifiName());
		if(WifiPortal==null)
			return false;
		swap(WifiPortal);
		for(String Portal : WifiPortal)
		{
			if(Portal==null)
				break;
			ConnectionWays sc;
			 try{
		            sc=(ConnectionWays)Class.forName("cn.oneclicks.wifi_school.connection_ways." + Portal).newInstance();
		            Method m = sc.getClass().getDeclaredMethod("logout");
		            m.invoke(sc);
		        }catch(Exception e){
		            e.printStackTrace();
		            Log.v("logout","0");
                    return false;
		        }
			
//			ConnectionWays sc;
//			if(Portal.equalsIgnoreCase("H3C_2014")){sc = new H3C_2014(); sc.logout(); }
//			else if(Portal.equalsIgnoreCase("CMCC_ZJ")){sc = new CMCC_ZJ(); sc.logout(); }
			
			
					
		}
        return true;
	}
	
	private void swap(String strs[]) {
        int len = strs.length;
        int as = len / 2;
        for (int i = 0; i < as; i++) {
            String tmp = strs[i];
            strs[i] = strs[len - 1 - i];
            strs[len - 1 - i] = tmp;
        }
	}
	
	public static boolean isConnectedSchoolWifi()
	{
		if(getWifiName().equalsIgnoreCase(""))
			return false;
		WifiAdmin wifi = new WifiAdmin(MyApplication.getContext());
		WifiInfo mWifiInfo = wifi.getWifiInfo();
		if((!mWifiInfo.getSSID().equalsIgnoreCase("\""+getWifiName()+"\""))&&getWifiName()!="")
				{
					Log.i("getSSID", mWifiInfo.getSSID());
					return false;
				}
		
			return true;
		
	}
	
	public static String getWifiName()
    {
    	SharedPreferences setting_user = MyApplication.getContext().getSharedPreferences("setting_school_wifi", 0);
		String WifiName = setting_user.getString("wifi_name","");
		//Log.i("savedSSID", WifiName);
		return WifiName;
    }
	
	public static boolean isConnectedNet()
	{
		 String urlStr = "http://www.baidu.com";
		 URL url;
		try {
			 url = new URL(urlStr);
		     HttpURLConnection conn = (HttpURLConnection) url.openConnection();   
	         conn.getResponseCode();
	         if(conn.getURL().toString().equalsIgnoreCase(urlStr))
	        	 return true;
	         else
	        	 return false;
	         
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}   
		 
	}
	
	public static String getUserName()
    {
    	SharedPreferences setting_user = MyApplication.getContext().getSharedPreferences("setting_user", 0);
		return setting_user.getString("Username","");
		
    }
    
	public static String getPassword()
    {
    	SharedPreferences setting_user = MyApplication.getContext().getSharedPreferences("setting_user", 0);
		return setting_user.getString("Password","");
    }
	
	public static String getUserName2()
    {
    	SharedPreferences setting_user = MyApplication.getContext().getSharedPreferences("setting_user", 0);
		return setting_user.getString("Username2","");
		
    }
    
	public static String getPassword2()
    {
    	SharedPreferences setting_user = MyApplication.getContext().getSharedPreferences("setting_user", 0);
		return setting_user.getString("Password2","");
    }
	
	public static void setSchool_user(String s)
	{
		SharedPreferences setting_user = MyApplication.getContext().getSharedPreferences("setting_school_wifi", 0);
		SharedPreferences.Editor setting_user_editor = setting_user.edit();
		setting_user_editor.putString("school_name",s);
		setting_user_editor.commit();
	}
	
	public static String getSchool_user()
	{
		SharedPreferences setting_user = MyApplication.getContext().getSharedPreferences("setting_school_wifi", 0);
		return setting_user.getString("school_name","");
	}

}
