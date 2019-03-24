package com.tsvico.mobike.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsvico.mobike.MyApplication;
import com.tsvico.mobike.R;
import com.tsvico.mobike.activity.login.LoginActivity;
import com.tsvico.mobike.bean.MyUser;



public class BaseActivity extends AppCompatActivity {
    private TextView  toolbarTitle;
    private ImageView toolbarTitleImg;

    public int getStatusBarHeight() {
        int i = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int j = 0;
        if (i > 0)
            j = getResources().getDimensionPixelSize(i);
        return j;
    }

    void initToolbar() {
//               View localView = findViewById(R.id.toolbar);
//                if (localView != null)
//                {
//                    setSupportActionBar((Toolbar)localView);
//                  //  getSupportActionBar().setBackgroundDrawable(R.drawable.up_arrow);
//                    setStatusBar();
//                 //   this.toolbarTitle = ((TextView)localView.findViewById(R.id.toolbar_title_text));
//                    if (this.toolbarTitle != null)
//                    {   getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//                        getSupportActionBar().setDisplayShowTitleEnabled(true);
//                    }
//                //   this.toolbarTitleImg = ((ImageView)localView.findViewById(R.id.toolbar_title_image));
//                }
    }

    protected boolean isSetStateTranslate() {
        return true;
    }


    protected void onCreate(@Nullable Bundle paramBundle) {
        super.onCreate(paramBundle);
        if (!isTaskRoot()) {
            Intent localIntent = getIntent();
            String str = localIntent.getAction();
            if ((localIntent.hasCategory("android.intent.category.LAUNCHER")) && (str.equals("android.intent.action.MAIN")))
                finish();
        }
    }


    // @Instrumented
    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        //  VdsAgent.onOptionsItemSelected(this, paramMenuItem);
        switch (paramMenuItem.getItemId()) {
            case 16908332:
                boolean bool = super.onOptionsItemSelected(paramMenuItem);
                // VdsAgent.handleClickResult(new Boolean(bool));
                return bool;
             default:
        }
        finish();
        // VdsAgent.handleClickResult(new Boolean(true));
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // MobclickAgent.getUrl(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // MobclickAgent.b(this);
    }


    public void setContentView(int paramInt) {
        super.setContentView(paramInt);
        initToolbar();
    }

    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            if (isSetStateTranslate())
                getWindow().getDecorView().setSystemUiVisibility(1280);
        } else
            return;
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.gray));
    }

    public void startActivity(Intent intent, boolean isNeedLogin) {
        if (isNeedLogin) {
            MyUser user = MyApplication.getInstance().getUser();
            if (user != null) {
                super.startActivity(intent);
            } else {
                MyApplication.getInstance().putIntent(intent);
                Intent i = new Intent(this, LoginActivity.class);
                super.startActivity(intent);
            }
        } else {
            super.startActivity(intent);
        }
    }

    public void startActivityForResult(Intent intent, int requestCode, boolean isNeedLogin) {
        if (isNeedLogin) {
            MyUser user = MyApplication.getInstance().getUser();
            if (user != null) {
                super.startActivityForResult(intent, requestCode);
            } else {
                MyApplication.getInstance().putIntent(intent);
                Intent i = new Intent(this, LoginActivity.class);
                super.startActivity(intent);
            }
        } else {
            super.startActivityForResult(intent, requestCode);
        }
    }
}
