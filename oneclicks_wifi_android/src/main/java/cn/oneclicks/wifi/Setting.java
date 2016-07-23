package cn.oneclicks.wifi;



import java.util.List;

import cn.oneclicks.wifi_school.SchoolWifiManager;
import android.R.layout;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;


import android.util.Log;
import android.view.View;
import android.widget.Toast;




public class Setting extends Activity {
	
	

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
		init();
		
		
		
		
		}
	
	
	void init()
	{
		findViewById(R.id.id_LinearLayout_setting_school)
		.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Setting.this,Setting_Province.class);
				startActivity(intent);
				
		  }
		 });

		findViewById(R.id.id_LinearLayout_setting_user)
		.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Setting.this,Setting_User.class); 
				startActivity(intent);
		  }
		 });
		
		findViewById(R.id.id_LinearLayout_setting_wifi)
		.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String school = SchoolWifiManager.getSchool_user();
				if(school.equalsIgnoreCase(""))
				{
					Toast.makeText(getApplicationContext(), "请先设置学校！",
           			     Toast.LENGTH_SHORT).show();
					return;
				}
				List<String> wifi_list = new SchoolWifiManager().getSchoolWifiList(school); 
				final int size =  wifi_list.size();
				if(size==0)
				{
					Toast.makeText(getApplicationContext(), "暂不支持该学校！",
	           			     Toast.LENGTH_SHORT).show();
					return;
				}
				 final String[] items =   (String[])wifi_list.toArray(new String[size]);
						 
				 Log.v("school_wifi",items[0]);
	                new AlertDialog.Builder(Setting.this)  
	                        .setTitle("请点击选择")  
	                        .setItems(items, new DialogInterface.OnClickListener() {  
	  
	                            public void onClick(DialogInterface dialog,  
	                                    int which) {  
	                            	SharedPreferences setting_user = getSharedPreferences("setting_school_wifi", 0);
	                        		SharedPreferences.Editor setting_user_editor = setting_user.edit();
	                        		setting_user_editor.putString("wifi_name",items[which]);
	                        		setting_user_editor.commit();
	                        		Toast.makeText(getApplicationContext(), "设置成功！",
	                        			     Toast.LENGTH_SHORT).show();
	                             
	                                         
	                            }  
	                        }).show();  
		  }
		 });
	}

	


	
	public void back(View view)
	{
		finish();
	}

	

}
