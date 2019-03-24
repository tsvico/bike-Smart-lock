package com.tsvico.mobike.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.andexert.library.RippleView;
import com.tsvico.mobike.MyApplication;
import com.tsvico.mobike.R;
import com.tsvico.mobike.activity.login.LoginActivity;
import com.tsvico.mobike.activity.usercenter.UserActivity;
import com.tsvico.mobike.api.Shared;
import com.tsvico.mobike.api.newland;
import com.tsvico.mobike.bean.BikeInfo;
import com.tsvico.mobike.bean.MyUser;
import com.tsvico.mobike.qrcode.Qrcode;
import com.tsvico.mobike.utils.ToastUtils;
import com.tsvico.mobike.utils.Utils;
import com.tsvico.mobike.utils.sparedCook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tsvico.mobike.R.id.refresh;

public class MainActivity extends AppCompatActivity{
    private static final String TAG           = "MainActivity";
    private static final int    REQS_LOCATION = 1;
    private static final int    REQS_UNLOCK   = 2;
    private int mSecretNumber = 0;
    private static final long MIN_CLICK_INTERVAL = 600;
    private long mLastClickTime;
    @BindView(R.id.ic_menu)
    ImageView      mIcMenu;//菜单按钮
    @BindView(R.id.ic_search)
    ImageView      mIcSearch;//搜索按钮
    @BindView(R.id.tv_allmobike)
    TextView       mTvAllmobike;//全部单车
    //@BindView(R.id.tv_mobikelite)
    //TextView       mTvMobikelite;//Mobikelite单车
    @BindView(R.id.layout_selectmobike)
    LinearLayout   mLayoutSelectmobike;//单车类型布局
    @BindView(R.id.id_bt_login)
    Button         mLogin;//login
    @BindView(R.id.dingwei)
    ImageView      mDingwei;//定位按钮
    @BindView(R.id.title)
    TextView     mtitle; //标题
    @BindView(R.id.refreshAll)
    RelativeLayout mRefreshAll;//刷新按钮
    @BindView(R.id.scan_qrcode)
    LinearLayout   mScan_qrcode;//扫描按钮
    @BindView(R.id.tv_location_info)
    TextView       mTvLocationInfo;//当前定位地址
    @BindView(R.id.bike_info_board)
    LinearLayout   mBikeInfoBoard;//骑行信息布局
    @BindView(R.id.bike_order_layout)
    LinearLayout   mBikeOrderBoard;//骑行信息布局
    @BindView(R.id.refresh)
    ImageView      mRefresh;//刷新
    @BindView(R.id.bt_loginOrorder)
    Button         mBtLoginOrorder;//登陆或者预定按钮
    @BindView(R.id.book_countdown)
    TextView       book_countdown;//倒计时
    @BindView(R.id.bike_code)
    TextView       bike_code;//倒计时
    @BindView(R.id.cancel_book)
    TextView       mTvCancleBook;//
    @BindView(R.id.bike_sound)
    TextView       mTvBikeSound;//
    @BindView(R.id.id_lo_bike_distance)
    LinearLayout   mLoBikeInfo;
    @BindView(R.id.confirm_cancel_layout)
    LinearLayout   mConfirm_cancle;
    @BindView(R.id.rv_ic_menu)
    RippleView     mRvicNemu;
    @BindView(R.id.rv_ic_search)
    RippleView     mRvicSearch;
    @BindView(R.id.rv_ic_message)
    RippleView     mRvicMessage;
    @BindView(R.id.tv_prices1)
    TextView       mTvPrices1;//价格
    @BindView(R.id.tv_distance1)
    TextView       mTvDistance1;//距离
    @BindView(R.id.minute1)
    TextView       mMinute1;//时间
    //   @BindView(R.id.tv_prices)
    @SuppressLint("StaticFieldLeak")
    public static TextView mTvPrices;//价格
    //    @BindView(R.id.tv_distance)
    @SuppressLint("StaticFieldLeak")
    public static TextView mTvDistance;//距离
    //   @BindView(R.id.minute)
    @SuppressLint("StaticFieldLeak")
    public static TextView mMinute;//时间
    private boolean isNeedLogin = true;//是否已登录

    private BikeInfo bInfo;
    private boolean isServiceLive = false;
    //自定义图标
    private long exitTime = 0;
    private MyUser      myUser;
    WebView mWebview;
    newland open = new newland();
    String token;
    Map<String, String> map = new HashMap<String, String>(); //本地保存数据
    //声明AMapLocationClient类对象
    AMapLocationClient mLocationClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mWebview = findViewById(R.id.bmapview1); //地图
        myUser = MyApplication.getInstance().getUser();  //是否登录
        isNeedLogin = myUser == null;
        Log.d(TAG, "initData: " + isNeedLogin);
        token = open.Login();
        Log.e("当前accessToken----->","accessToken"+token);
        sparedCook.saveCookie(MainActivity.this,"accessToken",token);
        //声明AMapLocationClient类对象
        AMapLocationClient mLocationClient = null;
        //声明定位回调监听器

        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        //mLocationClient.setLocationListener(mLocationListener);
        mLocationClient.startAssistantLocation(mWebview);

        //声明AMapLocationClientOption对象
        //AMapLocationClientOption mLocationOption = null;
        //初始化AMapLocationClientOption对象
        //mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        //mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        //mLocationOption.setInterval(1000);
        //mLocationClient.startLocation();

        initMap();
        requesPemission();
        initViews();
        isServiceLive = Utils.isServiceWork(this, "com.tsvico.mobike.service.RouteService");
        if (isServiceLive)
            beginService();
        String value = open.getDate("nl_lamp",token);
        Log.e("当前单车锁状态",value);

        initEvent();
/*      向后端云添加数据//
        TODO:向后端云添加数据
        mateid p2 = new mateid();
        p2.setId("15809");
        p2.setDevces("B2E01C2B");
        p2.save(new SaveListener<String>() {
            @Override
            public void done(String objectId,BmobException e) {
                if(e==null){
                    Log.e("添加成功","ok");
                }else{
                    //toast("创建数据失败：" + e.getMessage());
                }
            }
        });*/

/*
        String userid= Shared.getSettingNote(MainActivity.this, "gps", "userid");
        String userpwd= Shared.getSettingNote(MainActivity.this, "gps", "userpwd");
        Log.e("MainAcctivity--->",userid + userpwd);
*/
    }

    private void beginService() {
        if (!Utils.isGpsOPen(this)) {
            Utils.showDialog(this);
            return;
        }
        mScan_qrcode.setVisibility(View.GONE);
        mTvLocationInfo.setVisibility(View.GONE);
        mBikeInfoBoard.setVisibility(View.VISIBLE);
        mLoBikeInfo.setVisibility(View.VISIBLE);
        mBikeOrderBoard.setVisibility(View.GONE);
        mBtLoginOrorder.setText("结束骑行");
        mBtLoginOrorder.setVisibility(View.VISIBLE);
        mMinute1.setText("骑行时长");
        mTvDistance1.setText("骑行距离");
        mTvPrices1.setText("费用计算");
        mRefreshAll.setVisibility(View.GONE);
        countDownTimer.cancel();

    }

    private void initMap() {
        mWebview.getSettings().setJavaScriptEnabled(true);
        //mWebview.getSettings().setLoadsImagesAutomatically(true);//图片自动加载
        //优先使用缓存:
        mWebview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebview.addJavascriptInterface(new Js(this), "android");
        mWebview.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) { //结束时调用
                String[] v = open.getGps("LX","AY",token);
                Log.e("当前GPS----->",v[0]+","+v[1]);
                String id = "15809";
                mWebview.loadUrl("javascript:androidAddMarker(" + v[0] + "," + v[1] +","+ id+")"); //获取定位点
            }
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });

        mWebview.setWebChromeClient(new WebChromeClient() {
            // 处理javascript中的alert
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
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
        mWebview.loadUrl("file:///android_asset/index.html");

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
            //Toast.makeText(MainActivity.this, ssss, Toast.LENGTH_LONG).show();
            Go2LoginOrScan(ssss);
        }
        //这一行必须加
        @JavascriptInterface
        public void setShared(String x,String y,String a,String b){
           // Toast.makeText(MainActivity.this, "setShared", Toast.LENGTH_LONG).show();
            map.put("startLng", x);
            map.put("startLat", y);
            map.put("endLng",a);
            map.put("endLat",b);
            Shared.saveSettingNote(MainActivity.this, "gps", map);//参数（上下文，userinfo为文件名，需要保存的数据）
        }
    }
    private void updateBikeInfo(BikeInfo bikeInfo) {
        boolean hasPlanRoute = false;
        if (true) {
            if (isNeedLogin) {
                mBtLoginOrorder.setText("请登录后骑车");
            } else {
                mBtLoginOrorder.setText("预约骑车");
            }
            mLogin.setVisibility(View.GONE);
            mTvLocationInfo.setVisibility(View.VISIBLE);
            mBtLoginOrorder.setVisibility(View.VISIBLE);
            mBikeInfoBoard.setVisibility(View.VISIBLE);
            mLoBikeInfo.setVisibility(View.VISIBLE);
            mMinute.setText(bikeInfo.getTime());
            mTvDistance.setText(bikeInfo.getDistance());
            bInfo = bikeInfo;
        }
    }

    /**
     * 刷新动画
     */
    private void intiAnimation() {
        Animation ra;//动画
        if (mRefreshAll.getVisibility() == View.GONE) {
            mRefreshAll.setVisibility(View.VISIBLE);
            mRefreshAll.setClickable(true);
        }
        ra = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setFillAfter(true);
        ra.setDuration(500);
        ra.setRepeatCount(2);

        mRefresh.startAnimation(ra);

        ra.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRefreshAll.setClickable(false);
                mRefreshAll.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public static Intent getMyIntent(Context context, boolean unlockSuccess) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("unlockSuccess", unlockSuccess);
        return intent;
    }

    private void requesPemission() {
        List<String> permissionlist = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionlist.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionlist.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionlist.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!permissionlist.isEmpty()) {
            String[] perssions = permissionlist.toArray(new String[0]);
            ActivityCompat.requestPermissions(MainActivity.this, perssions, 1);
        }
    }

    private void initEvent() {
        initGPS();//检测GPS开启
        mRvicNemu.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Go2otherActivityAndChackLogin(UserActivity.class);
            }
        });
        mRvicSearch.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Go2Seach();
            }
        });
    }

    private void initViews() {
        mTvPrices = findViewById(R.id.tv_prices);
        mTvDistance = findViewById(R.id.tv_distance);
        mMinute = findViewById(R.id.minute);
        if (isNeedLogin) {
            mLogin.setVisibility(View.VISIBLE);
        } else {
            mLogin.setVisibility(View.GONE);
        }
        mScan_qrcode.setVisibility(View.VISIBLE);
        mBikeInfoBoard.setVisibility(View.GONE);
        mConfirm_cancle.setVisibility(View.GONE);
        mBikeOrderBoard.setVisibility(View.GONE);
        mBtLoginOrorder.setVisibility(View.GONE);
        mBikeInfoBoard.setVisibility(View.GONE);
        setMyClickable(mTvAllmobike);
    }
    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha： 0~1
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    //获取中心点
    public void getMyLocation() {
        mWebview.post(new Runnable() {
            @Override
            public void run() {
                // 注意调用的JS方法名要对应上
                // 调用javascript的callJS()方法
                mWebview.loadUrl("javascript:geo.getCurrentPosition();");
            }
        });
    }
    //添加地图
    //获取定位点

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result == PackageManager.PERMISSION_DENIED) {
                            Toast.makeText(MainActivity.this, "请同意所申请权限", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    //      requestionLotion();
                } else {
                    Toast.makeText(MainActivity.this, "somthing hanppened", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mBikeInfoBoard.getVisibility() == View.VISIBLE) {
                if (!Utils.isServiceWork(this, "com.tsvico.mobike.service.RouteService"))
                    cancelBook();
                return true;
            }

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                //                finish();
                //                System.exit(0);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void cancelBook() {
        if (isNeedLogin) {
            mLogin.setVisibility(View.VISIBLE);
        } else {
            mLogin.setVisibility(View.GONE);
        }
        mBikeInfoBoard.setVisibility(View.GONE);
        countDownTimer.cancel();
        mBikeOrderBoard.setVisibility(View.GONE);
        mBtLoginOrorder.setVisibility(View.VISIBLE);
//        if (routeOverlay != null)
//            routeOverlay.removeFromMap();
        //地图缩放比设置为18
    }

    private CountDownTimer countDownTimer = new CountDownTimer(10 * 60 * 1000, 1000) {

        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {
            book_countdown.setText(millisUntilFinished / 60000 + "分" + ((millisUntilFinished / 1000) % 60) + "秒");
        }

        @Override
        public void onFinish() {
            book_countdown.setText("预约结束");
            Toast.makeText(MainActivity.this, getString(R.string.cancel_book_toast), Toast.LENGTH_SHORT).show();
        }
    };

    @OnClick({R.id.ic_menu, R.id.ic_search, R.id.tv_allmobike
            , R.id.id_bt_login, R.id.dingwei, refresh
            , R.id.scan_qrcode, R.id.bt_loginOrorder, R.id.cancel_book, R.id.bike_sound
            ,R.id.title})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_allmobike:
                selectAllMobike();
                break;
            case R.id.id_bt_login:
                Go2Login();
                break;
            case R.id.dingwei:
                Go2myLotionAndRefresh();
                break;
            case refresh:
                refreshData();
                break;
            case R.id.scan_qrcode:
                //Go2LoginOrScan();
                mWebview.post(new Runnable() {
                    @Override
                    public void run() {

                        // 注意调用的JS方法名要对应上
                        // 调用javascript的callJS()方法
                        mWebview.loadUrl("javascript:id();");
                    }
                });
                break;
            case R.id.bt_loginOrorder:
                Go2LoginOrorder();
                break;
            case R.id.cancel_book:
                cancelBook();
                break;
            case R.id.bike_sound:
                beginService();
                break;
            case R.id.ic_search:
                Toast.makeText(MainActivity.this,"这里没有添加功能",Toast.LENGTH_SHORT).show();
                break;
            case R.id.title:
                long currentClickTime = SystemClock.uptimeMillis();
                long elapsedTime = currentClickTime - mLastClickTime;
                mLastClickTime = currentClickTime;
                if (elapsedTime < MIN_CLICK_INTERVAL) {
                    ++mSecretNumber;
                    if (5 == mSecretNumber) {
                        try {
                            Toast.makeText(MainActivity.this,"彩蛋：2016071154",Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.i(TAG, e.toString());
                        }
                    }
                } else {
                    mSecretNumber = 0;
                }
                break;
        }
    }


    private void setMyClickable(TextView tv) {
        mTvAllmobike.setClickable(true);
        mTvAllmobike.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        tv.setClickable(false);
        tv.setBackground(getResources().getDrawable(R.drawable.top_tab));
    }

    private void selectAllMobike() {
        setMyClickable(mTvAllmobike);
        refreshData();
    }
//定位按钮
    private void Go2myLotionAndRefresh() {
        getMyLocation();
        refreshData();
        String[] v = open.getGps("LX","AY",token);
        Log.e("当前GPS----->",v[0]+","+v[1]);
        String id = "15809";
        mWebview.loadUrl("javascript:androidAddMarker(" + v[0] + "," + v[1] +","+ id+")"); //获取定位点

    }

    private void Go2otherActivityAndChackLogin(Class c) {
        if (isNeedLogin) {
            Go2Login();
        } else {
            Intent intent = new Intent(MainActivity.this, c);
            startActivity(intent);
        }
    }

    private void Go2Seach() {
        Toast.makeText(this,"搜索",Toast.LENGTH_SHORT).show();
    }

    private void Go2LoginOrorder() {
        if (isNeedLogin) {
            Go2Login();
        } else {
            isServiceLive = Utils.isServiceWork(this, "com.tsvico.mobike.service.RouteService");
            if (isServiceLive) {
                toastDialog();
            } else {
                GoforOrorder();
            }

        }
    }


    private void GoforOrorder() {
        mBikeInfoBoard.setVisibility(View.VISIBLE);
        mBikeOrderBoard.setVisibility(View.VISIBLE);
        mConfirm_cancle.setVisibility(View.VISIBLE);
        mLoBikeInfo.setVisibility(View.GONE);
        mBtLoginOrorder.setVisibility(View.GONE);
        bike_code.setText(bInfo.getName());
        countDownTimer.start();
    }

    private void Go2LoginOrScan(String qrcode) {
        if (isNeedLogin) {
            Go2Login();
        } else {
            Intent intent = new Intent(MainActivity.this, Qrcode.class);
            intent.putExtra("qrcode",qrcode);
            startActivityForResult(intent, REQS_UNLOCK);
        }
    }


    private void Go2UserActivity() {
        if (isNeedLogin) {
            Go2Login();
        } else {
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            startActivity(intent);
        }
    }

    private void refreshData() {
        intiAnimation();
//        if (routeOverlay != null)
//            routeOverlay.removeFromMap();
//        Log.d(TAG, "changeLatitude-----btn_refresh--------" + changeLatitude);
        //addOverLayout(changeLatitude, changeLongitude);
    }

    private void Go2Login() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQS_LOCATION:
                if (RESULT_OK == resultCode) {
//                    if (routeOverlay != null)
//                        routeOverlay.removeFromMap();
//                    //addOverLayout(latLng.latitude, latLng.longitude);
                }
                break;
            case REQS_UNLOCK:
                if (RESULT_OK == resultCode) {
                    ToastUtils.show(MainActivity.this, data.getStringExtra("result"));
                    beginService();
                }

                break;
        }
    }


    protected void onRestart() {
        super.onRestart();
        myUser = MyApplication.getInstance().getUser();
        isNeedLogin = myUser == null;
       // baiduMap.setMyLocationEnabled(true);
        //mlocationClient.start();
        //myOrientationListener.start();
        //mlocationClient.requestLocation();
        isServiceLive = Utils.isServiceWork(this, "com.tsvico.mobike.service.RouteService");
        Log.d(TAG, "MainActivity------------onRestart------------------");
        if (QRCodeInputActivity.unlockSuccess || isServiceLive) {
            beginService();
            return;
        }
        // if (RouteDetailActivity.completeRoute)
        backFromRouteDetail();
    }

    private void backFromRouteDetail() {
        mBikeInfoBoard.setVisibility(View.GONE);
        mConfirm_cancle.setVisibility(View.GONE);
        mBikeOrderBoard.setVisibility(View.GONE);
        mBtLoginOrorder.setVisibility(View.GONE);
        mBikeInfoBoard.setVisibility(View.GONE);
        mScan_qrcode.setVisibility(View.VISIBLE);
        if (isNeedLogin) {
            mLogin.setVisibility(View.VISIBLE);
            mBtLoginOrorder.setText("请登录后骑车");
        } else {
            mBtLoginOrorder.setText("预约骑车");
            mLogin.setVisibility(View.GONE);
        }
        //mBaiduMap.showZoomControls(false);
        getMyLocation();
//        if (routeOverlay != null)
//            routeOverlay.removeFromMap();
        //addOverLayout(currentLatitude, currentLongitude);
    }


    // 供路线选择的Dialog



    /**
     * bd地图监听，接收当前位置
     */

    /**
     * des:地图跳到指定位置
     *
     * @param
     */

    public static class LocationReceiver extends BroadcastReceiver {
        public LocationReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utils.isTopActivity(context)) {
                String time = intent.getStringExtra("totalTime");
                String distance = intent.getStringExtra("totalDistance");
                String price = intent.getStringExtra("totalPrice");
                mMinute.setText(time);
                mTvDistance.setText(distance);
                mTvPrices.setText(price);
                Log.d(TAG, "MainActivity-------TopActivity---------true");
                Log.d(TAG, "MainActivity-------time:" + time);
                Log.d(TAG, "MainActivity-------distance:" + distance);
                Log.d(TAG, "MainActivity-------price:" + price);
            } else {
                Log.d(TAG, "MainActivity-------TopActivity---------false");
            }
        }
    }

    /**
     * 结束骑行
     */
    private void toastDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("确认要结束进程吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //Intent intent = new Intent(MainActivity.this, RouteService.class);
                //stopService(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * des:提示开启GPS
     */
    private void initGPS() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则跳转至设置开启界面，设置完毕后返回到首页
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("GPS提示：");
            builder.setMessage("请打开GPS开关，以便您更准确的找到自行车");
            builder.setCancelable(true);

            builder.setPositiveButton("确定",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 2); // 设置完成后返回到原来的界面

                        }
                    });
            builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mBaiduMap.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mBaiduMap.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mWebview != null) {
            mWebview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebview.clearHistory();

            ((ViewGroup) mWebview.getParent()).removeView(mWebview);
            mWebview.destroy();
            mWebview = null;
            if(mLocationClient!=null)
                mLocationClient.stopAssistantLocation();
        }

        super.onDestroy();
       // mBaiduMap.onDestroy();
       // baiduMap.setMyLocationEnabled(false);
        // 退出时销毁定位
        //mlocationClient.stop();
        // 关闭定位图层
        //mBaiduMap = null;
        countDownTimer.cancel();

    }
}
