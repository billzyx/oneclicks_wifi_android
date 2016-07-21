package cn.oneclicks.wifi;

import java.util.List;

import cn.oneclicks.wifi_school.SchoolWifiManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 *
 */
public class Setting_School extends Activity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_school);
		init();
		
		
	}
	
	ListView school_listview;
	List<String> schools ;
	
	void init()
	{
		schools = new SchoolWifiManager().getSchoolList();
		school_listview = (ListView) findViewById(R.id.school_listview);//得到ListView对象的引用 /*为ListView设置Adapter来绑定数据*/
		school_listview.setAdapter(new ArrayAdapter<String>(this,
		                android.R.layout.simple_list_item_1, schools));
		school_listview.setOnItemClickListener(itemClickListener);
	}
	
	private  OnItemClickListener itemClickListener =new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			String s= schools.toArray()[position].toString();
			SchoolWifiManager.setSchool_user(s);
    		Toast.makeText(getApplicationContext(), "设置成功！",
    			     Toast.LENGTH_SHORT).show();
    		finish();
		}
	};
	
	public void back(View view)
	{
		finish();
	}
}
