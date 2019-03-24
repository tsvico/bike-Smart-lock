package com.tsvico.mobike.activity;
/*******************
 *
 * 计费
 *
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tsvico.mobike.R;
import com.tsvico.mobike.api.Shared;
import com.tsvico.mobike.api.newland;
import com.tsvico.mobike.utils.sparedCook;

import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class money extends AppCompatActivity {
    private TextView total_time;
    private TextView total_pricce;
    private long baseTimer;
    WebView mWebview;
    private Button Buy;
    private boolean type=false;
    newland open = new newland();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        total_time = this.findViewById(R.id.total_time);
        total_pricce = findViewById(R.id.total_pricce);
        baseTimer = SystemClock.elapsedRealtime();
        mWebview = findViewById(R.id.mapview);
        Buy = findViewById(R.id.buy);
        setButtonGray(Buy);
        @SuppressLint("HandlerLeak")
        final Handler startTime = new Handler(){
            @SuppressLint({"SetTextI18n", "ShowToast"})
            public void handleMessage(android.os.Message msg) {
                if (null != total_time) {
                    total_time.setText("骑行时长："+ msg.obj);
                    int money = msg.arg1;
                    double m = (money%30) * 0.5;
                    total_pricce.setText("费用合计："+m + "元");
                    int yun = msg.arg2;
                    if(yun>5&&yun%10==0){
                        //检查是否已经开启
                        String accessToken = sparedCook.getCookie(money.this,"accessToken");
                        String value = open.getDate("nl_lamp",accessToken);
                        if(value.equals("0")){
                            Toast.makeText(money.this,"车锁已关闭",Toast.LENGTH_SHORT);
                            setButtonRed(Buy);
                            type = true;
                        }
                    }
                }
            }
        };
       new Timer("计时器").scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int time = (int)((SystemClock.elapsedRealtime() - baseTimer) / 1000);
                String mm = new DecimalFormat("00").format(time % 3600 / 60);
                String ss = new DecimalFormat("00").format(time % 60);
                String timeFormat = mm + "分" + ss + "秒";
                int timemoney = Integer.parseInt(mm);
                int yun = Integer.parseInt(ss);
                Message msg = new Message();
                msg.obj = timeFormat;
                msg.arg1 = timemoney;
                msg.arg2 = yun;
                startTime.sendMessage(msg);
                if(type){
                    System.gc();
                    cancel();
                }

            }

        }, 0, 1000L);
        initMap();
        Buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("button","diainji");
                String intentFullUrl = "alipays://platformapi/startapp?appId=20000067&__open_alipay__=YES&url=" +
                        "https%3A%2F%2Frender.alipay.com%2Fp%2Ff%2Ffd-j6lzqrgm%2Fguiderofmklvtvw.html" +
                        "%3Fchannel%3DqrCode%26shareId%3D2088022098239795%26sign%3Dppbq9NUaZitxJZg0GIu2Oh45g9vo%252Fm2MF2ZIYgTaZKE%253D%26scene%3" +
                        "DofflinePaymentNewSns%26campStr%3Dp1j%252BdzkZl018zOczaHT4Z5CLdPVCgrEXq89JsWOx1gdt05SIDMPg3PTxZbdPw9dL%26token%3Dc1x06972jmqa95dekqagwa7";
                Intent intent = null;
                try {
                    intent = Intent.parseUri(intentFullUrl, Intent.URI_INTENT_SCHEME );
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
    }
    private void initMap() {
        mWebview.getSettings().setJavaScriptEnabled(true);
        //mWebview.getSettings().setLoadsImagesAutomatically(true);//图片自动加载
        //优先使用缓存:
        mWebview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebview.addJavascriptInterface(new money.Js(this), "android");
        mWebview.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) { //结束时调用
                String x= Shared.getSettingNote(money.this, "gps", "startLng");
                String y= Shared.getSettingNote(money.this, "gps", "startLat");
                String a= Shared.getSettingNote(money.this, "gps", "endLng");
                String b= Shared.getSettingNote(money.this, "gps", "endLat");
                Log.e("MainAcctivity--->",x +"  "+ y + " "+a+" "+b);
                mWebview.loadUrl("javascript:search([" + x + "," + y +"],["+ a+","+b+"])"); //获取定位点
                //super.onPageFinished(view, url);
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });

        mWebview.setWebChromeClient(new WebChromeClient() {
            // 处理javascript中的alert
            public boolean onJsAlert(WebView view, String url, String message,
                                     final JsResult result) {
                return true;
            }

            // 处理javascript中的confirm
            public boolean onJsConfirm(WebView view, String url,
                                       String message, final JsResult result) {
                return true;
            }

            // 处理定位权限请求
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
            // 设置应用程序的标题title
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
        mWebview.loadUrl("file:///android_asset/map.html");

    }
    public class Js {
        Context context;
        public Js(Context c) {
            context= c;
        }
        /**
         * 与js交互时用到的方法，在js里直接调用的
         */
        @JavascriptInterface
        public void showToast(String ssss) {
            Toast.makeText(money.this, ssss, Toast.LENGTH_LONG).show();
        }
    }
    /**
     * 改变bt颜色red设置可点击
     *
     * @param bt
     */
    private void setButtonRed(Button bt) {
        bt.setClickable(true);
        bt.setBackgroundResource(R.color.red);
    }

    /**
     * 改变bt颜色gray设置不可点击
     *
     * @param bt
     */
    private void setButtonGray(Button bt) {
        bt.setClickable(false);
        bt.setBackgroundResource(R.color.gray);
    }
}
