package com.tsvico.mobike.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jungly.gridpasswordview.GridPasswordView;
import com.tsvico.mobike.R;
import com.tsvico.mobike.api.newland;
import com.tsvico.mobike.qrcode.zxing.camera.CameraManager;
import com.tsvico.mobike.utils.ToastUtils;
import com.tsvico.mobike.utils.sparedCook;
import com.tsvico.mobike.views.TabTitleView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class QRCodeInputActivity extends AppCompatActivity {
    public static boolean unlockSuccess = false;
    @BindView(R.id.title_wallet)
    TabTitleView     mTitleWallet;
    @BindView(R.id.id_bt_query)
    Button           mBtQuery;
    @BindView(R.id.pswView)
    GridPasswordView mPasswordView;
    private String carNub;
    private boolean flashLightOpen = false;
    //private boolean isOnlyNub      = false;
    private String qrcode="";
    newland open = new newland();
    String accessToken = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_input);
        ButterKnife.bind(this);
        CameraManager.init(getApplication());
        Intent intent = getIntent();
        if (intent != null)
            qrcode = intent.getStringExtra("qrcode");
        initView();
        accessToken = sparedCook.getCookie(QRCodeInputActivity.this,"accessToken");
    }

    private void initView() {
        mTitleWallet.setOnLeftButtonClickListener(new TabTitleView.OnLeftButtonClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
        mTitleWallet.setOnRightTextViewClickListener(new TabTitleView.OnRightButtonClickListener() {
            @Override
            public void onClick() {
                //Intent intent = new Intent(QRCodeInputActivity.this, HelpCardActivity.class);
                //startActivity(intent);
                Toast.makeText(QRCodeInputActivity.this,"这么简单的玩意要啥帮助，车上的编号输入一下",Toast.LENGTH_LONG).show();
            }
        });
        mPasswordView.setPasswordVisibility(true);
        mPasswordView.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            @Override
            public void onTextChanged(String psw) {
                mBtQuery.setBackgroundResource(R.color.smssdk_gray);
                mBtQuery.setClickable(false);
            }

            @Override
            public void onInputFinish(String psw) {
                carNub = psw;
                mBtQuery.setBackgroundResource(R.color.red);
                mBtQuery.setClickable(true);
            }
        });
    }


    /**
     * 切换散光灯状态
     */
    public void toggleFlashLight() {
        if (flashLightOpen) {
            setFlashLightOpen(false);
        } else {
            setFlashLightOpen(true);
        }
    }

    private void setFlashLightOpen(boolean open) {
        if (flashLightOpen == open)
            return;

        flashLightOpen = !flashLightOpen;
        CameraManager.get().setFlashLight(open);
    }


    @OnClick({ R.id.id_bt_query})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.id_bt_query:
                queryUnlock();
                break;
        }
    }

    public static Intent getMyIntent(Context context, String qrcode) {
        Intent intent = new Intent(context, QRCodeInputActivity.class);
        intent.putExtra("qrcode", qrcode);
        return intent;
    }

    private void queryUnlock() {
        if (TextUtils.isEmpty(mPasswordView.getPassWord())){
            ToastUtils.show(QRCodeInputActivity.this,"请输入单车号码");
            return;
        }
        if (mPasswordView.getPassWord().length()!=10){
            ToastUtils.show(QRCodeInputActivity.this,"请输入完整的单车号码");
            return;
        }

        Log.e("传过来的二维码数据是",qrcode);
        Log.e("输入的的二维码数据是", mPasswordView.getPassWord());
        if (mPasswordView.getPassWord().indexOf(qrcode) != -1) { //包含该字符串
            Intent intent = new Intent();
            intent.putExtra("result", mPasswordView.getPassWord());
            setResult(RESULT_OK, intent);

            open.setStatu("15809","nl_lamp","1",accessToken);
            String value = open.getDate("nl_lamp",accessToken);
            //TODO:车锁状态
            Log.e("当前单车锁状态",value);
            Toast.makeText(QRCodeInputActivity.this,"开锁成功",Toast.LENGTH_SHORT).show();
            startActivity(money.class, true);
            //finish();
        } else {
            //unlockSuccess = true;
            //startActivity(new Intent(QRCodeInputActivity.this, MainActivity.class));
            //finish();
            Toast.makeText(QRCodeInputActivity.this,"输入错误，请重新输入",Toast.LENGTH_SHORT).show();
        }

    }

    public void startActivity(Class clazz,boolean isFinish) {
        startActivity(new Intent(this,clazz));
        if (isFinish) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraManager.get().closeDriver();
    }

    public void onDestroy() {
        super.onDestroy();
        unlockSuccess = false;
    }

}
