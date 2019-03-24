package com.tsvico.mobike.qrcode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.tsvico.mobike.R;
import com.tsvico.mobike.activity.QRCodeInputActivity;
import com.tsvico.mobike.activity.money;
import com.tsvico.mobike.activity.nfc;
import com.tsvico.mobike.api.newland;
import com.tsvico.mobike.qrcode.zxing.encoding.EncodingHandler;
import com.tsvico.mobike.utils.sparedCook;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class Qrcode extends Activity{

    private static final String TAG = "Qrcode:";

    private static final int REQEST_CARNUB = 22;//tsvico add

    private MediaPlayer            mediaPlayer;
    private boolean                playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private boolean flashLightOpen = false;
    private ImageButton  flashIbtn;
    private ImageButton nfc;
    private ImageButton small; //响铃
    private ImageButton     bt_input;
    private ImageView imageView;
    private String qrcode;
    private IntentFilter intentFilter;

    private TimeChangeReceiver timeChangeReceiver;
    private Timer timer = null;//计时器
    private TimerTask timerTask = null;
    Handler mHandler;
    newland open_small = new newland();
    String accessToken ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initView(); //主页面
        //监听时间广播
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//设置了系统时区
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);//设置了系统时间

        timeChangeReceiver = new TimeChangeReceiver();
        accessToken = sparedCook.getCookie(Qrcode.this,"accessToken");
        registerReceiver(timeChangeReceiver, intentFilter);
        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg) {
                stopTime();
            }
        };


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "xxxxxxxxxxxxxxxxxxxonResume");


        playBeep = true;
        final AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timeChangeReceiver); //去除监听广播
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        handleResult(resultString);
    }

    protected void handleResult(String resultString) {
        if (resultString.equals("")) {
            Toast.makeText(Qrcode.this, R.string.scan_failed, Toast.LENGTH_SHORT).show();
        } else {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("result", resultString);
            resultIntent.putExtras(bundle);
            this.setResult(RESULT_OK, resultIntent);
        }
        finish();
    }

    protected void initView() {
        Intent intent = getIntent();
        qrcode = "";
        if (intent != null){
            qrcode = intent.getStringExtra("qrcode");
        }

        //隐藏标题栏
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.qr_camera);

        imageView = findViewById(R.id.Qrcode);
        flashIbtn = findViewById(R.id.flash_ibtn);
        bt_input = findViewById(R.id.bt_input);
        nfc = findViewById(R.id.nfc);
        small = findViewById(R.id.small);

        //加载二维码
        try {
            //获取输入的文本信息
            String str = qrcode;
            if(str != null && !"".equals(str.trim())){
                //根据输入的文本生成对应的二维码并且显示出来
                Bitmap mBitmap = EncodingHandler.createQRCode(str, 600);
                if(mBitmap != null){
                    Toast.makeText(this,"二维码生成成功！",Toast.LENGTH_SHORT).show();
                    imageView.setImageBitmap(mBitmap);
                }
            }else{
                Toast.makeText(this,"还没有选择车辆！",Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }


        //打开手电筒
        flashIbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashLightOpen) {
                    flashIbtn.setImageResource(R.drawable.scan_qrcode_flash_light_off);
                    flashLightOpen = false;
                } else {
                    flashIbtn.setImageResource(R.drawable.scan_qrcode_flash_light_on);
                    flashLightOpen = true;
                }
                toggleFlashLight();
            }
        });
        //输入单号
        bt_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // openGallery();
                startActivityForResult(QRCodeInputActivity.
                        getMyIntent(Qrcode.this, qrcode), REQEST_CARNUB);
            }
        });
        //响铃
        small.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                open_small.setStatu("15809","nl_buzzer","1",accessToken);
                Toast.makeText(Qrcode.this,"铃声已响",Toast.LENGTH_SHORT).show();
                startTime();
            }
        });
        nfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //读取nfc
                Intent intent = new Intent(Qrcode.this,nfc.class);
                intent.putExtra("qrcode",qrcode);
                startActivity(intent);
            }
        });
    }


    /**
     * 切换散光灯状态
     */
    public void toggleFlashLight() {
        if (flashLightOpen) {
            //设置屏幕亮度最大
            setWindowBrightness(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
        } else {
            //取消屏幕最亮
            setWindowBrightness(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE);
        }
    }

    /**
     * 设置当前窗口亮度
     * @param brightness
     */
    private void setWindowBrightness(float brightness) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness;
        window.setAttributes(lp);
    }



    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    public void startActivity(Class clazz,boolean isFinish) {
        startActivity(new Intent(this,clazz));
        if (isFinish) {
            finish();
        }
    }


    //监听时间变化
    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case Intent.ACTION_TIME_TICK:
                    //每过一分钟 触发
                    //Toast.makeText(context, "1 min passed", Toast.LENGTH_SHORT).show();
                    Log.e("Time","-1m");
                    Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
                    t.setToNow(); // 取得系统时间。
                    int hour = t.hour; // 0-23
                    int minute = t.minute;
                    Log.e("Time", hour+"-"+minute);
                    //加载二维码
                    try {
                        //获取输入的文本信息
                        String str = qrcode + hour + minute;
                        if(!"".equals(str.trim())){
                            //根据输入的文本生成对应的二维码并且显示出来
                            Bitmap mBitmap = EncodingHandler.createQRCode(str, 600);
                            if(mBitmap != null){
                                imageView.setImageBitmap(mBitmap);
                            }
                        }else{
                            finish();
                        }
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    //检查是否已经开启
                    String value = open_small.getDate("nl_lamp",accessToken);
                    if(value.equals("1")){
                        startActivity(money.class, true);
                    }
                    break;
                case Intent.ACTION_TIME_CHANGED:
                    //设置了系统时间
                    Toast.makeText(context, "system time changed", Toast.LENGTH_SHORT).show();
                    break;
                case Intent.ACTION_TIMEZONE_CHANGED:
                    //设置了系统时区的action
                    Toast.makeText(context, "system time zone changed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


    /**
     * 开始自动减时
     */
    private void startTime() {
        if(timer==null){
            timer = new Timer();
        }

        timerTask = new TimerTask() {

            @Override
            public void run() {
                newland open_small = new newland();
                open_small.setStatu("15809","nl_buzzer","0",accessToken);
                Log.e("停止响铃","stop");
                Message message = Message.obtain();
                message.arg1=5;
                mHandler.sendMessage(message);//发送消息
            }
        };
        timer.schedule(timerTask, 5000);//1000ms执行一次
    }
    /**
     * 停止自动减时
     */
    private void stopTime() {
        if(timer!=null) {
            timer.cancel();
            timer.purge();
            timer=null;
        }
    }
}