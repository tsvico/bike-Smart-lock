package com.tsvico.mobike;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tsvico.mobike.bean.MyUser;
import com.tsvico.mobike.utils.UserLocalData;

import java.io.File;

import cn.bmob.v3.Bmob;

/***************
 * tsvico
 * 泰山学院
 */
public class MyApplication extends Application {
    private static MyApplication sInstance;
    public static String mSDCardPath;
    public static final String APP_FOLDER_NAME = "MoBike";

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        initMyApplication();
       //newland v = new newland();
        //v.Login();
    }

    private void initMyApplication() {
        Bmob.initialize(this, "609393c5620131f922cd5e75edc4ac1c");
        Logger.addLogAdapter(new AndroidLogAdapter(){
            @Override
            public boolean isLoggable(int priority, String tag) {
                return true;
            }
        });
        initUser();
    }

    public static MyApplication getInstance() {
        return sInstance;
    }

    private MyUser mUser;

    private void initUser() {
        this.mUser = UserLocalData.getUser(this);
    }

    public MyUser getUser() {
        return mUser;
    }
    public void upDataUser(MyUser localuser,MyUser newUser) {
        this.mUser = localuser;
        UserLocalData.upDataUser(this, localuser,newUser);
    }

    public void putUser(MyUser user) {
        this.mUser = user;
        UserLocalData.putUser(this, user);
    }

    public void clearUser() {
        this.mUser = null;
        UserLocalData.clearUser(this);
    }

    private Intent intent;

    public void putIntent(Intent intent) {
        this.intent = intent;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public void jumpToTargetActivity(Context context) {
        context.startActivity(intent);
        this.intent = null;
    }

    private boolean initDirs() {
        mSDCardPath = Environment.getExternalStorageDirectory().toString();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

}
