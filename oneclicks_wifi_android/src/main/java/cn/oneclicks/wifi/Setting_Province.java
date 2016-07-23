package cn.oneclicks.wifi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import cn.oneclicks.wifi_school.SchoolWifiManager;

/**
 * Created by tianwai on 7/23/16.
 */
public class Setting_Province extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_province);
        init();


    }

    ListView school_listview;
    List<String> proivnces ;

    void init()
    {
        proivnces = new SchoolWifiManager().getProvinceList();
        school_listview = (ListView) findViewById(R.id.school_listview);//得到ListView对象的引用 /*为ListView设置Adapter来绑定数据*/
        school_listview.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, proivnces));
        school_listview.setOnItemClickListener(itemClickListener);
    }

    private AdapterView.OnItemClickListener itemClickListener =new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            String s= proivnces.toArray()[position].toString();
            Intent intent = new Intent(Setting_Province.this,Setting_School.class);
            Bundle b = new Bundle();
            b.putString("province",s);
            intent.putExtras(b);
            startActivityForResult(intent,0);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                finish();
            }
        } catch (Exception ex) {
        }

    }

    public void back(View view)
    {
        finish();
    }
}
