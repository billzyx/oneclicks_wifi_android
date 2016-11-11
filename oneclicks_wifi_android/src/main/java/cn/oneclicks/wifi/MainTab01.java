package cn.oneclicks.wifi;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import cn.oneclicks.wifi_school.SchoolWifiManager;

public class MainTab01 extends Fragment
{

	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.main_tab_01, container, false);
		
		mybar1 = (ProgressBar) view.findViewById(R.id.progressBar1);
		button_logout = (Button) view.findViewById(R.id.button_logout);
		
		Log.v("version", MyApplication.getVersion());
		
		imageButton_connect_wifi =  (ImageButton) view.findViewById(R.id.imageButton_connect_wifi);
		imageButton_connect_wifi.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				
				imageButton_connect_wifi();
				}});
		
		button_logout.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				button_logout();
				}});
		
		if(SchoolWifiManager.isConnectedSchoolWifi())
		{
			new Thread(new Runnable() {  
	            @Override  
	            public void run() {  
	            	Message m = new Message();   
	                try {  
	                	if(SchoolWifiManager.isConnectedNet())
	                	{
	                		m.what = 4;  //已连接校园网并已登录
		                    handler.sendMessage(m); 
	                	}
	        			 
	                } catch (Exception e) {  
	                    e.printStackTrace(); 
	                    
	                }  
	                
	            }  
	        }).start(); 
			
			
		}
		
		handler = new Handler() {  
            public void handleMessage(Message msg) {  
                super.handleMessage(msg);  
                if (msg.what == 0) {  
                	Toast.makeText(getActivity().getApplicationContext(), "连接失败！",
   					     Toast.LENGTH_SHORT).show();
                	mybar1.setVisibility(View.GONE);
                } 
                if (msg.what == 1) //连接成功
                {  
                	Toast.makeText(getActivity().getApplicationContext(), "连接成功！",
   					     Toast.LENGTH_SHORT).show();
                	mVibrator01 = ( Vibrator ) getActivity().getApplication().getSystemService(Service.VIBRATOR_SERVICE); 
                    mVibrator01.vibrate( new long[]{100,10,100},-1);
                    mybar1.setVisibility(View.GONE);
                    button_logout.setVisibility(View.VISIBLE);
                } 
                if(msg.what == 3)//下线消息
                {
                	button_logout.setVisibility(View.GONE);
                	Toast.makeText(getActivity().getApplicationContext(), "下线成功！",
      					     Toast.LENGTH_SHORT).show();
                }
                if(msg.what == 4)//启动检测是否连接校园网并已登录
                {
            		button_logout.setVisibility(View.VISIBLE);
                }
                if(msg.what == 5)//下线失败
                {
                    Toast.makeText(getActivity().getApplicationContext(), "下线失败！",
                            Toast.LENGTH_SHORT).show();
                }
                if(msg.what == 6)//连接超时
                {
                    Toast.makeText(getActivity().getApplicationContext(), "连接超时！",
                            Toast.LENGTH_SHORT).show();
                    mybar1.setVisibility(View.GONE);
                }
            }  
        }; 
		
		return view;
	
	}
	
	ImageButton imageButton_connect_wifi;
	private Handler handler = null; 
	private Vibrator mVibrator01;
	private ProgressBar mybar1;
	private Button button_logout;
	
	
	
	public void button_logout()
	{
		 new Thread(new Runnable() {  
	            @Override  
	            public void run() {  
	            	Message m = new Message();   
	                try {  
	                	SchoolWifiManager sc = new SchoolWifiManager();
	        			if(sc.logout())
	        			    m.what = 3;
                        else
                            m.what = 5;
	                    handler.sendMessage(m);  
	                } catch (Exception e) {  
	                    e.printStackTrace();
                        m.what = 5;
                        handler.sendMessage(m);
                    }
	                
	            }  
	        }).start(); 
	}


	
    void imageButton_connect_wifi()
	{
    	if(button_logout.getVisibility()==View.VISIBLE)
    	{
    		Toast.makeText(getActivity().getApplicationContext(), "你已经在线！",
					     Toast.LENGTH_SHORT).show();
    		return;
    	}
		int WifiState = getWifiState();
		if(WifiState==-1)
		{
			Toast.makeText(getActivity().getApplicationContext(), "wifi异常！",
				     Toast.LENGTH_SHORT).show();
		}
		else
		{
			WifiAdmin wifi = new WifiAdmin(getActivity());
			if(WifiState<2)
			{
				wifi.openWifi();
			}
			
			wifi.startScan();
			if(getWifiName()=="")
			{
				Toast.makeText(getActivity().getApplicationContext(), "请先设置热点！",
					     Toast.LENGTH_SHORT).show();
				return;
			}
			List<ScanResult> wifiList = wifi.getWifiList();
			ScanResult result = null ;
			for (int i = 0; i < wifiList.size(); i++) {
				ScanResult result1 = wifiList.get(i);
                if(result1.SSID.equalsIgnoreCase(getWifiName()))
                {
                	result=result1;
                	break;
                }
            }
			if(result==null)
			{
				Toast.makeText(getActivity().getApplicationContext(), getWifiName()+"无信号！",
					     Toast.LENGTH_SHORT).show();
				return;
			}
			
			wifi.connect(result.SSID);
			if(getUserName()=="")
			{
				Toast.makeText(getActivity().getApplicationContext(), " 请先设置帐号！",
					     Toast.LENGTH_SHORT).show();
				return;
			}
			mybar1.setVisibility(View.VISIBLE);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    WifiAdmin wifi = new WifiAdmin(getActivity());
                    int i = 0;
                    Message m = new Message();
                    while(!wifi.getSSID().equalsIgnoreCase("\""+getWifiName()+"\"")){
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }finally {
                            i++;
                        }
                        if(i>=20){
                            m.what = 6;  //连接超时
                            handler.sendMessage(m);
                            break;
                        }
                    }
                    if(i<20)
                        login();
                }
            }).start();

			
		}
		
	}
    
    protected void login() {  
    	  
        new Thread(new Runnable() {  
            @Override  
            public void run() {  
            	Message m = new Message();   
                try {  
                	SchoolWifiManager sc = new SchoolWifiManager();
        			if(sc.login())
        			{
        				m.what = 1;  //连接成功
                        handler.sendMessage(m);  
        			}
        			else
        			{
        				m.what = 0;  //连接失败
                        handler.sendMessage(m);  
        			}
                } catch (Exception e) {  
                    e.printStackTrace(); 
                    m.what = 0;  //连接失败
                    handler.sendMessage(m);  
                }  
                
            }  
        }).start();  
  
    }  
    
    
    String getWifiName()
    {
    	SharedPreferences setting_user = getActivity().getSharedPreferences("setting_school_wifi", 0);
		String WifiName = setting_user.getString("wifi_name","");
		return WifiName;
    }
    
    String getUserName()
    {
    	SharedPreferences setting_user = getActivity().getSharedPreferences("setting_user", 0);
		return setting_user.getString("Username","");
		
    }
    
    String getPassword()
    {
    	SharedPreferences setting_user = getActivity().getSharedPreferences("setting_user", 0);
		return setting_user.getString("Password","");
    }
	
	
	void openWifi()
	{
		WifiManager wifiManager = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(true);
	}
	
	int getWifiState()
	{
		WifiManager wifiManager = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);

			if(wifiManager != null){
				int wifiState = wifiManager.getWifiState();
				return wifiState;
				}
			else
				return -1;
	}

}
