package cn.oneclicks.wifi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by tianwai on 7/30/16.
 */

public class Auxiliary extends Activity {



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auxiliary);
        init();


    }



    void init()
    {
        findViewById(R.id.id_LinearLayout_auxiliary_push)
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(Auxiliary.this,CaptureInfo.class);
                        startActivity(intent);

                    }
                });
    }







    public void back(View view)
    {
        finish();
    }


}
