package com.tsvico.mobike.views;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.tsvico.mobike.R;

public class CountTimerView extends CountDownTimer {

    private static final int TIME_COUNT = 61000;//时间防止从59s开始显示（以倒计时60s为例子）
    private TextView btn;
    private int endStrRid;


    public CountTimerView(TextView btn) {
        super(TIME_COUNT, 1000);
        this.btn = btn;
        this.endStrRid = R.string.smssdk_resend_identify_code;
    }

    // 计时完毕时触发
    @Override
    public void onFinish() {
        btn.setText(endStrRid);
        btn.setEnabled(true);
    }

    // 计时过程显示
    @Override
    public void onTick(long millisUntilFinished) {
        btn.setEnabled(false);
        btn.setText(millisUntilFinished / 1000 + " 秒后可重新发送");
    }
}
