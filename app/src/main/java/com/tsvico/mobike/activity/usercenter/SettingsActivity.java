package com.tsvico.mobike.activity.usercenter;
/**********
 * 设置
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.orhanobut.logger.Logger;
import com.tsvico.mobike.MyApplication;
import com.tsvico.mobike.activity.MainActivity;
import com.tsvico.mobike.bean.VersionInfo;
import com.tsvico.mobike.utils.ToastUtils;
import com.tsvico.mobike.views.MySettingView;
import com.tsvico.mobike.views.TabTitleView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import dmax.dialog.SpotsDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity {

    @BindView(com.tsvico.mobike.R.id.title_use_detail)
    TabTitleView mTitleUseDetail;
    @BindView(com.tsvico.mobike.R.id.tv_useaddress)
    MySettingView mTvUseaddress;

    @BindView(com.tsvico.mobike.R.id.tv_aboutus)
    MySettingView mTvAboutus;
    @BindView(com.tsvico.mobike.R.id.bt_logout)
    Button        mBtLogout;

    private SpotsDialog mDialog;

    private PackageManager mPackageManager;
    private int            mCurrentVersionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.tsvico.mobike.R.layout.activity_settings);
        ButterKnife.bind(this);
        initToolbar();
        initData();
        mDialog = new SpotsDialog(this);
    }

    private void initData() {
        mPackageManager = getPackageManager();
        try {
            PackageInfo packageInfo = mPackageManager.getPackageInfo(getPackageName(), 0);
            // 版本号
            mCurrentVersionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initToolbar() {
        mTitleUseDetail.setOnLeftButtonClickListener(new TabTitleView.OnLeftButtonClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }

    private void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @OnClick({com.tsvico.mobike.R.id.tv_useaddress,  com.tsvico.mobike.R.id.tv_aboutus, com.tsvico.mobike.R.id.bt_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case com.tsvico.mobike.R.id.tv_useaddress:
                startActivity(SettingAddressActivity.getMyIntent(SettingsActivity.this));
                break;
            case com.tsvico.mobike.R.id.tv_aboutus:
//                startActivity(CustomerServiceWebActivity
//                        .getCustomerServiceIntent(SettingsActivity.this, "关于我们",
//                                BuidUrl.getAboutus()));
                break;

            case com.tsvico.mobike.R.id.bt_logout:
                toLogout();
                break;
        }
    }

    private void toLogout() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);//context Activity类型
        //设置不可以取消
        //ab.setCancelable(false);
        //  ab.setTitle("提醒");
        ab.setMessage("确认是否要退出登录？");
        ab.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                BmobUser.logOut();
                MyApplication.getInstance().clearUser();

                ToastUtils.show(SettingsActivity.this, "您已退出，可重新登录");
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                finish();

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ab.show();

    }

    private void findNew() {
        mDialog.show();
        BmobQuery<VersionInfo> query = new BmobQuery<>("VersionInfo");
        query.order("-updatedAt");
        query.setLimit(1);//最新1条
        query.findObjects(new FindListener<VersionInfo>() {
            @Override
            public void done(List<VersionInfo> list, BmobException e) {
                if (e == null && !list.isEmpty()) {
                    dismissDialog();
                    chackVersion(list.get(0));
                } else {
                    ToastUtils.show(SettingsActivity.this, "检查新版本失败");
                    Logger.d(e);
                }
                dismissDialog();
            }
        });
    }

    private void chackVersion(VersionInfo versionInfo) {
        if (mCurrentVersionCode == Integer.parseInt(versionInfo.getVersionCode().trim())) {
            ToastUtils.show(SettingsActivity.this, "当前为最新版本");
        } else {
            showIsDownLoadDialog(versionInfo);
        }
    }

    private void showIsDownLoadDialog(final VersionInfo versionInfo) {
        //getApplicationContext() Context类型
        AlertDialog.Builder ab = new AlertDialog.Builder(this);//context Activity类型
        //设置不可以取消
        //ab.setCancelable(false);
        ab.setTitle("提醒");
        ab.setMessage("有新版本是否下载：\n新版本功能：" + versionInfo.getVersionDesc());
        ab.setPositiveButton("下载", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 下载
                downloadNewApk(versionInfo);

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ab.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // 取消
            }
        });
        ab.show();//显示对
    }

    private void downloadNewApk(VersionInfo versionInfo) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request b = new Request.Builder().url(versionInfo.getDownloadUrl()).build();
        okHttpClient.newCall(b).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.show(SettingsActivity.this, "下载失败");
            }

            @Override
            public void onResponse(Call call, Response response) {
                Logger.d(response);
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                File file = new File(Environment.getExternalStorageDirectory(), "newms.apk");
                //数据和mimeType同时制定
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                startActivityForResult(intent, 1);
            }
        });
    }

}
