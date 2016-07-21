package cn.oneclicks.wifi.test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import cn.oneclicks.wifi_school.HttpUtil;
import cn.oneclicks.wifi_school.SchoolWifiManager;
import android.test.AndroidTestCase;
import android.util.Log;

public class test extends AndroidTestCase{
	
	 public void testadd() throws Exception{
//		 SchoolWifiManager s = new SchoolWifiManager();
//		 //s.getSchoolWifiPortal("浙江传媒学院","CMCC-EDU");
//		 s.login();
		 
		 HttpUtil.post("http://www.baidu.com", null);
		 
		 
	 }

}
