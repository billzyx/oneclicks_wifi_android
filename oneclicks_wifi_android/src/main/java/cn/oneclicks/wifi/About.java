package cn.oneclicks.wifi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.net.Uri;






/**
 * Created by tianwai on 7/27/16.
 */
public class About extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        init();


    }


    void init()
    {

    }


    public void officialSite(View view)
    {
        Intent intent= new Intent(Intent.ACTION_VIEW,Uri.parse("http://wifi.oneclicks.cn"));
        startActivity(intent);
    }

    public void back(View view)
    {
        finish();
    }
}

