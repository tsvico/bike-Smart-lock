package com.tsvico.mobike.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.tsvico.mobike.MyApplication;
import com.tsvico.mobike.R;
import com.tsvico.mobike.activity.login.LoginActivity;
import com.tsvico.mobike.bean.MyUser;
import com.tsvico.mobike.utils.MyConstains;
import com.tsvico.mobike.utils.PreferencesUtils;

import static com.tsvico.mobike.utils.CommonUtils.isNetworkAvailable;

public class SplashActivity extends AppCompatActivity {
    private boolean isNeedSetting;
    private boolean isNeedLogin = true;//
    private boolean isFirstRun  = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        initData();

        initConnect();

        //  initGPS();

    }

    private void initData() {
        MyApplication application = MyApplication.getInstance();
        isFirstRun = PreferencesUtils.getBoolean(this, MyConstains.IS_FIRST_RUN, false);
        MyUser mUser = application.getUser();
        if (mUser != null) {
            isNeedLogin = false;
        }
    }

    private void initConnect() {
        if (!isNetworkAvailable(this)) {
            //提示对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
            builder.setMessage("网络无法访问，请检查网络").setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = null;
                            //判断手机系统的版本  即API大于10 就是3.0或以上版本
                            if (android.os.Build.VERSION.SDK_INT > 21) {
                                intent = new Intent(Settings.ACTION_HOME_SETTINGS);
                            } else {
                                intent = new Intent();
                                ComponentName component = new ComponentName("com.android.settings",
                                        "com.android.settings.WirelessSettings");
                                intent.setComponent(component);
                                intent.setAction("android.intent.action.VIEW");
                            }
                            startActivityForResult(intent, 1);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            }).show();
        } else {
            checkFirstRun();
        }
    }


    private void initGPS() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则跳转至设置开启界面，设置完毕后返回到首页
        if (!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
            builder.setTitle("GPS提示：");
            builder.setMessage("请打开GPS开关，以便您更准确的找到自行车");
            builder.setCancelable(false);

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
    protected void onRestart() {
        super.onRestart();
        if (isNeedSetting) {
            if (isNetworkAvailable(this)) {
                checkFirstRun();
            } else {
                initConnect();
            }
        }
    }

    private void checkFirstRun() {
        if (isFirstRun) {
            PreferencesUtils.putBoolean(this, MyConstains.IS_FIRST_RUN, false);
            Go2Guide();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isNeedLogin) {
                        Go2Login();
                    } else {
                        Go2Main();
                    }
                }
            }, 200);
        }

    }

    private void Go2Login() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void Go2Main() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void Go2Guide() {
        Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
            case 2:
                isNeedSetting = true;
                break;

            default:
                break;
        }
    }
}