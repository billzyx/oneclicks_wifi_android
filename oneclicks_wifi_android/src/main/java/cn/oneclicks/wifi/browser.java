package cn.oneclicks.wifi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cn.oneclicks.wifi_school.SchoolWifiManager;
import im.delight.android.webview.AdvancedWebView;


/**
 * Created by tianwai on 7/27/16.
 */

public class Browser extends Activity implements AdvancedWebView.Listener{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser);
        Bundle b = getIntent().getExtras();
        boolean requireComplete = false;
        if(b != null){
            infos = b.getStringArrayList("infos");
            requireComplete = b.getBoolean("requireComplete");
        }

        if(infos != null){
            RelativeLayout browser_message_layout = (RelativeLayout) findViewById(R.id.browser_message_layout);
            browser_message_layout.setVisibility(View.VISIBLE);
            nextInfoStep();
        }

        if(requireComplete){
            TextView browser_complete = (TextView) findViewById(R.id.browser_complete);
            browser_complete.setVisibility(View.VISIBLE);
        }
        init();


    }

    private List<String> infos;

    private int infoStep = 0;

    private AdvancedWebView mWebView;

    String sUrl = "http://wifi.oneclicks.cn/";

    void init()
    {
        mWebView = (AdvancedWebView) findViewById(R.id.webview);
        mWebView.setListener(this, this);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "MYOBJECT");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:window.MYOBJECT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");


                StringBuilder sb = new StringBuilder();
                sb.append("var forms = document.getElementsByTagName('form');");
                sb.append("for (var j = 0; j < forms.length; j++) {");
                sb.append("forms[j].onsubmit = function () {");
                sb.append("var str = '';");
                sb.append("var inputs = document.getElementsByTagName('input');");
                sb.append("for (var i = 0; i < inputs.length; i++) {");
                sb.append("str += inputs[i].name;");
                sb.append("str += ',';");
                sb.append("str += inputs[i].value;");
                sb.append("str += ';';");
                sb.append("}");
                sb.append("window.MYOBJECT.processHTML(str);");
                sb.append("return true;");
                sb.append("}");
                sb.append("};");

                view.loadUrl("javascript:" + sb.toString());
            }

        });

        Bundle b = getIntent().getExtras();
        if(b != null)
            sUrl = b.getString("sUrl");
        mWebView.loadUrl(sUrl);

        handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {//need twice layer login
                    Bundle b = new Bundle();
                    b.putBoolean("tiwceLayer",true);
                    getIntent().putExtras(b);
                }
            }
        };

    }

    protected void saveFile(String s){
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

    public void complete(View view){
        if(infos != null){
            if(infoStep < infos.size()){
                if(infoStep == 1){
                    checkNetwork();
                }
                nextInfoStep();
                return;
            }
        }
        saveFile(preSaveStringBuilder.toString());
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

    public void back(View view)
    {
        finish();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        Log.v("load",url);
        preSaveFile(url);
    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {
        Log.v("load2",url);
    }



    class MyJavaScriptInterface
    {
        @JavascriptInterface
        public void processHTML(String html)
        {
            Log.v("henrytest", html);
            preSaveFile(html);


        }
    }

    private StringBuilder preSaveStringBuilder = new StringBuilder();
    private Handler handler = null;

    private void preSaveFile(String html) {
        preSaveStringBuilder.append(html);
        preSaveStringBuilder.append("\n\n");
    }

    private void nextInfoStep(){
        if(infoStep < infos.size()){
            TextView browser_message_textview = (TextView) findViewById(R.id.browser_message_textview);
            String info = infos.get(infoStep);
            browser_message_textview.setText(info);
            preSaveFile(info);
            infoStep++;
        }
    }

    private void checkNetwork(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message m = new Message();
                try {
                    if(!SchoolWifiManager.isConnectedNet()){
                        m.what = 1;
                        handler.sendMessage(m);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
