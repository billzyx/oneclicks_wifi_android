package cn.oneclicks.wifi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by tianwai on 8/3/16.
 */

public class CaptureInfo extends Activity {



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.captureinfo);
        init();


    }

    private TextView captureinfo_textView_maintext ;
    private TextView captureinfo_textView_warning ;

    private int step = 0;
    private int recordStep = 0;

    void init()
    {
        captureinfo_textView_maintext = (TextView)findViewById(R.id.captureinfo_textView_maintext);
        captureinfo_textView_warning = (TextView)findViewById(R.id.captureinfo_textView_warning);
        nextStep();
    }

    public void button_nextstep(View view){
        if(step == 1) {
            nextStep();
            newRecordFile();
        }
        else if(step == 2){
            nextRecordStep();
        }else if(step >= 3){
            nextStep();
        }
    }

    private void nextStep(){
        step++;
        switch (step){
            case 1 :{
                captureinfo_textView_maintext.setText("一点wifi将通过记录并上传您的登录信息，适配您所在学校的wifi登录方式。上传将通过邮件发送进行，请确保您的设备上已装有并登录了邮件客户端。请您按界面上的指示操作完成记录和上传，这将耗费您几分钟的时间。");
                captureinfo_textView_warning.setText("警告：您将授权一点wifi使用您的校园网帐号用于开发测试用途。建议您在一点wifi完成您所在学校的适配后修改密码。");
            };break;
            case 2 :{
                captureinfo_textView_maintext.setText("请确保您已经连上校园网热点且您的校园网帐号处于下线状态。点击下一步继续。");
                captureinfo_textView_warning.setText("");
            };break;
            case 3 :{
                captureinfo_textView_maintext.setText("就要完成了！请输入您的学校和省份。");
                captureinfo_textView_warning.setVisibility(View.GONE);
                RelativeLayout captureinfo_layout_school_province = (RelativeLayout) findViewById(R.id.captureinfo_layout_school_province);
                captureinfo_layout_school_province.setVisibility(View.VISIBLE);
            };break;
            case 4 :{
                EditText schoolEditText = (EditText) findViewById(R.id.captureinfo_school);
                String school = schoolEditText.getText().toString();
                EditText provinceEditText = (EditText) findViewById(R.id.captureinfo_province);
                String province = provinceEditText.getText().toString();
                saveFileProvinceAndSchool(school,province);

                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/oneclickswifi");
                dir.mkdirs();
                File file = new File(dir, "package.txt");

                try {
                    copyFile(new File(getApplicationContext().getFilesDir() + File.separator + "package.txt"),file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }



                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"wifi@oneclicks.cn"});
                i.putExtra(Intent.EXTRA_SUBJECT, school + "请求适配");
                i.putExtra(Intent.EXTRA_TEXT   , "本人请求一点wifi适配" + school + "(所在省份:" + province + ")" + "的连接方式。授权一点wifi使用我的校园网帐号用于开发测试用途。");
                i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(CaptureInfo.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }

                Button button_resend = (Button) findViewById(R.id.captureinfo_button_resend);
                button_resend.setVisibility(View.VISIBLE);

                TextView captureinfo_ok = (TextView) findViewById(R.id.captureinfo_ok);
                captureinfo_ok.setText("完成");
            };break;
            case 5 :{
                finish();
            };break;
        }

    }

    private void nextRecordStep(){
        recordStep++;
        if(recordStep == 1){
            Intent intent = new Intent(CaptureInfo.this,Browser.class);
            Bundle b = new Bundle();
            b.putStringArrayList("infos",new ArrayList<String>
                    (Arrays.asList("请完成校园网登录。",
                            "请点击下线按钮",
                            "请正常输入帐号但输入错误的密码并点击登录")));
            b.putBoolean("requireComplete",true);
            b.putString("sUrl","http://wifi.oneclicks.cn/");
            intent.putExtras(b);
            startActivityForResult(intent,0);
        }else if(recordStep == 2){
            Intent intent = new Intent(CaptureInfo.this,Browser.class);
            Bundle b = new Bundle();
            b.putStringArrayList("infos",new ArrayList<String>
                    (Arrays.asList("请继续完成登录（直至第二层登录完成）",
                            "请点击下线按钮",
                            "请正常输入帐号但输入错误的密码并点击登录")));
            b.putBoolean("requireComplete",true);
            b.putString("sUrl","http://wifi.oneclicks.cn/");
            intent.putExtras(b);
            startActivityForResult(intent,0);
        }
    }

    private void newRecordFile(){
        FileOutputStream fOut = null;
        try {
            fOut = openFileOutput("package.txt", Context.MODE_PRIVATE);
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFileProvinceAndSchool(String school,String province){
        String s = school + "," + province + "\n\n";
        FileOutputStream fOut = null;
        try {
            fOut = openFileOutput("package.txt", Context.MODE_APPEND);
            fOut.write(s.getBytes());
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copyFile(File src, File dst) throws IOException
    {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try
        {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
        finally
        {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if(data.getBooleanExtra("tiwceLayer",false)){
                    nextRecordStep();
                }else {
                    nextStep();
                }
            }
            if(resultCode == 0){
                recordStep--;
            }
        } catch (Exception ex) {
        }

    }

    public void button_resend(View view){
        step--;
        nextStep();
    }

    public void back(View view)
    {
        finish();
    }


}
