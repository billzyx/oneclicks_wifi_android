package cn.oneclicks.wifi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;




@SuppressLint("Registered")
public class ActSplashScreen extends Activity{




@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.shan);
// 闪屏的核心代码
new Handler().postDelayed(new Runnable() {
@Override
public void run() {
Intent intent = new Intent(ActSplashScreen.this,MainActivity.class); //从启动动画ui跳转到主ui
ActSplashScreen.this.startActivity(intent);
ActSplashScreen.this.finish(); // 结束启动动画界面
}
}, 3000); //启动动画持续3秒钟
}

}

