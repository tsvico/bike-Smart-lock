package com.tsvico.mobike.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.tsvico.mobike.R;
import com.tsvico.mobike.api.newland;
import com.tsvico.mobike.bean.mateid;
import com.tsvico.mobike.utils.sparedCook;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class nfc extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private TextView tvUid;
    private String qrcode;
    newland open = new newland();
    String accessToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = nfc.this.getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(getResources().getColor(R.color.nfc));
        setContentView(R.layout.nfc);
        tvUid = findViewById(R.id.tv_uid);
        //获取NfcAdapter实例
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //获取id
        Intent intent = getIntent();
        if (intent != null)
            qrcode = intent.getStringExtra("qrcode");
        //获取通知
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        if (nfcAdapter == null) {
            Toast.makeText(nfc.this,"当前设备不支持NFC",Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        accessToken = sparedCook.getCookie(nfc.this,"accessToken");
        if (nfcAdapter!=null&&!nfcAdapter.isEnabled()) {
            Toast.makeText(nfc.this,"请在系统设置中先启用NFC功能",Toast.LENGTH_LONG).show();
            startActivity(new Intent("android.settings.NFC_SETTINGS"));
            //startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)); //打开设置页面
            return;
        }
        //因为启动模式是singleTop，于是会调用onNewIntent方法
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        resolveIntent(intent);
    }

    void resolveIntent(Intent intent) {

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            processTag(intent);
        }
    }

    private String ByteArrayToHexString(byte[] inarray) {
        int i, j, in;
        String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
                "B", "C", "D", "E", "F" };
        String out = "";
        for (j = 0; j < inarray.length; ++j) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    private String flipHexStr(String s){
        StringBuilder  result = new StringBuilder();
        for (int i = 0; i <=s.length()-2; i=i+2) {
            result.append(new StringBuilder(s.substring(i,i+2)));
        }
        return result.toString();
    }


    public void processTag(Intent intent) {//处理tag
        //获取到卡对象
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //获取卡id这里即uid
        byte[] aa = tagFromIntent.getId();
        String str = ByteArrayToHexString(aa);
        str = flipHexStr(str);
        //从云端获取数据
        final String card = str;



        if(qrcode==null){
            /******
             *获取后端云数据 //
             * TODO:后端云id与车锁匹配
             */
            BmobQuery<mateid> query = new BmobQuery<mateid>();
            query.findObjects(new FindListener<mateid>() {
                @Override
                public void done(List<mateid> list, BmobException e) {
                    if (e == null) {
                        if ( list != null && list.size() > 0 ) {
                            //获取数据不为空
                            for(int i=0;i<list.size();i++){
                                Log.e("云端卡id", list.get(i).getDevces());
                                if(card.equals(list.get(i).getDevces())){
                                    open.setStatu(list.get(i).getId(),"nl_lamp","1",accessToken); //
                                    // TODO:开锁
                                    Toast.makeText(nfc.this,"编号"+list.get(i).getId()+"开锁成功",Toast.LENGTH_SHORT).show();
                                    startActivity(money.class,true);
                                    break;
                                }
                            }
                        }
                    } else {
                        Log.e("test", "done: " + e);
                    }
                }
            });
        }else{
            /*************
             * 车锁开的是固定的一个，待定解决，因为就一个锁
             */
            BmobQuery<mateid> query = new BmobQuery<mateid>();
            query.findObjects(new FindListener<mateid>() {
                @Override
                public void done(List<mateid> list, BmobException e) {
                    if (e == null) {
                        boolean m = true;
                        if (list.size() > 0 && list != null) {
                            //获取数据不为空
                            for(int i=0;i<list.size();i++){
                                if(qrcode.equals(list.get(i).getId())){ //车锁匹配
                                    if(card.equals(list.get(i).getDevces())) { //卡也匹配
                                        open.setStatu(qrcode, "nl_lamp", "1",accessToken); //
                                        // TODO:开锁
                                        Toast.makeText(nfc.this, "编号" + qrcode + "开锁成功", Toast.LENGTH_SHORT).show();
                                        m = false;
                                        startActivity(money.class, true);
                                    }
                                    break;
                                }
                            }
                        }
                        if(m){
                            Toast.makeText(nfc.this,"车锁信息不匹配",Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("test", "done: " + e);
                    }
                }
            });
            //Toast.makeText(nfc.this,"开锁成功",Toast.LENGTH_SHORT).show();
            //open.setStatu("15809","nl_lamp","1"); //TODO:开锁
        }
        tvUid.setText(str);

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null)
            //设置程序不优先处理
            nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null)
            //设置程序优先处理
            nfcAdapter.enableForegroundDispatch(this, pendingIntent,
                    null, null);
    }
    public void startActivity(Class clazz,boolean isFinish) {
        startActivity(new Intent(this,clazz));
        if (isFinish) {
            finish();
        }
    }
}
