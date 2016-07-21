package cn.oneclicks.wifi;

import cn.oneclicks.wifi_school.SchoolWifiManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Setting_User extends Activity  {
	
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_user);
		init();
		
		
	}
	
	RelativeLayout rl1;
	RelativeLayout rl2;
	
	void init()
	{
		
		loginUsername = (EditText) findViewById(R.id.loginUsername);
		loginPassword = (EditText) findViewById(R.id.loginPassword);
		loginUsername2 = (EditText) findViewById(R.id.loginUsername2);
		loginPassword2 = (EditText) findViewById(R.id.loginPassword2);
		
		SharedPreferences setting_user = getSharedPreferences("setting_user", 0);
		String Username = setting_user.getString("Username","");
		String Password = setting_user.getString("Password","");
		String Username2 = setting_user.getString("Username2","");
		String Password2 = setting_user.getString("Password2","");
		loginUsername.setText(Username);
		loginPassword.setText(Password);
		loginUsername2.setText(Username2);
		loginPassword2.setText(Password2);
		
		rl1 = (RelativeLayout) findViewById(R.id.SettingUser_layout1);
		rl2 = (RelativeLayout) findViewById(R.id.SettingUser_layout2);
		rl1.setVisibility(View.VISIBLE);
		rl2.setVisibility(View.VISIBLE);
		String type = SchoolWifiManager.getSchoolWifiType(SchoolWifiManager.getSchool_user(),SchoolWifiManager.getWifiName());
		if(type==null)
			return;
		Log.v("acc_type",type);
		
		if(type.equalsIgnoreCase("1"))
			rl2.setVisibility(View.GONE);
		else if(type.equalsIgnoreCase("2"))
			rl1.setVisibility(View.GONE);
		
	}
	
	
	EditText loginUsername;
	EditText loginPassword;
	EditText loginUsername2;
	EditText loginPassword2;
	
	
	public void loginBtn_Click(View v)
	{
		SharedPreferences setting_user = getSharedPreferences("setting_user", 0);
		SharedPreferences.Editor setting_user_editor = setting_user.edit();
		setting_user_editor.putString("Username",loginUsername.getText().toString());
		setting_user_editor.putString("Password",loginPassword.getText().toString());
		setting_user_editor.putString("Username2",loginUsername2.getText().toString());
		setting_user_editor.putString("Password2",loginPassword2.getText().toString());
		setting_user_editor.commit();
		Toast.makeText(getApplicationContext(), "保存成功！",
			     Toast.LENGTH_SHORT).show();
	}
	
	public void back(View view)
	{
		finish();
	}
	

}
