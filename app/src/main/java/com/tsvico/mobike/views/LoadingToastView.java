package com.tsvico.mobike.views;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsvico.mobike.R;

public class LoadingToastView extends FrameLayout {
    private ImageView a;
    private TextView  b;
    private Animation c;
    public LoadingToastView(@NonNull Context context) {
        super(context);
    }

    public LoadingToastView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingToastView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void show()
    {
        if (this.a != null)
            this.a.startAnimation(this.c);
        setVisibility(VISIBLE);
    }

    public void hind()
    {
        if (this.a != null)
            this.a.clearAnimation();
        setVisibility(GONE);
    }

    protected void onFinishInflate()
    {
        super.onFinishInflate();
        this.a = findViewById(R.id.id_iv_loading_toast);
        this.b = findViewById(R.id.id_tv_loading_toast);
        this.c = AnimationUtils.loadAnimation(getContext(), R.anim.loading);
    }

}
