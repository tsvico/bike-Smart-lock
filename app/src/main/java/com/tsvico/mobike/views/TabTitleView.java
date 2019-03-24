package com.tsvico.mobike.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.tsvico.mobike.R;

/**
 */

public class TabTitleView extends RelativeLayout {
    private LayoutInflater mInflater;
    private TextView       mTitleTextView;
    private ImageView      mLeftButton;
    private TextView       mRightTextView;

    private RippleView mLRippleView, mRRippleView;

    public TabTitleView(Context context) {
        this(context, null);
    }

    public TabTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TabTitleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mInflater = LayoutInflater.from(context);
        initView();

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabTitleView, defStyleAttr, defStyleRes);
            Drawable left_ImagViewDrawble = a.getDrawable(R.styleable.TabTitleView_leftImageIcon);
            if (left_ImagViewDrawble == null) {
            } else {
                setleft_ImagViewDrawable(left_ImagViewDrawble);
            }
            boolean showRightTextView = a.getBoolean(R.styleable.TabTitleView_showRightTextView, false);
            if (showRightTextView) {
                setRigtTvVisiable(View.VISIBLE);
            } else {
                setRigtTvVisiable(View.GONE);
            }

            int rightTvcolor = a.getColor(R.styleable.TabTitleView_rightTvColor, getResources()
                    .getColor(R.color.white));
            setRigtTvColor(rightTvcolor);

            String title = a.getString(R.styleable.TabTitleView_titleText);
            String rightTvText = a.getString(R.styleable.TabTitleView_rightText);

            setTitleText(title);

            setRigtTvText(rightTvText);

            a.recycle();

        }
        initListener();

    }

    private void initListener() {
        //                mLeftButton.setOnClickListener(new OnClickListener() {
        //                    @Override
        //                    public void onClick(View v) {
        //                        if (onLeftButtonClickListener != null) {
        //                            onLeftButtonClickListener.onClick();
        //                        }
        //                    }
        //                });
        //
        //                mRightTextView.setOnClickListener(new OnClickListener() {
        //                    @Override
        //                    public void onClick(View v) {
        //                        if (onRightTextViewClickListener != null) {
        //                            onRightTextViewClickListener.onClick();
        //                        }
        //                    }
        //                });
        mLRippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (onLeftButtonClickListener != null) {
                    onLeftButtonClickListener.onClick();
                }
            }
        });
        mRRippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (onRightTextViewClickListener != null) {
                    onRightTextViewClickListener.onClick();
                }
            }
        });

    }

    public void setleft_ImagViewDrawable(Drawable left_imagViewDrawble) {
        if (mLeftButton != null)
            mLeftButton.setBackground(left_imagViewDrawble);
    }

    public void setRigtTvText(String rightTvText) {
        if (mRightTextView != null)
            mRightTextView.setText(rightTvText);
    }

    public void setTitleText(String titletext) {
        if (mTitleTextView != null)
            mTitleTextView.setText(titletext);//CharSequence
    }

    public void setRigtTvVisiable(int visiable) {
        if (mRightTextView != null)
            mRightTextView.setVisibility(visiable);
    }


    public void setRigtTvColor(int rightTvcolor) {
        if (mRightTextView != null)
            mRightTextView.setTextColor(rightTvcolor);
    }

    private void initView() {
        View view = mInflater.inflate(R.layout.tab_title, this, true);
        mTitleTextView = view.findViewById(R.id.tv_tab_title);
        mLeftButton = view.findViewById(R.id.iv_tab_left);
        mRightTextView = view.findViewById(R.id.tv_tab_ringht);
        mLRippleView = view.findViewById(R.id.more);
        mRRippleView = view.findViewById(R.id.more1);
    }

    public interface OnLeftButtonClickListener {
        void onClick();
    }

    public interface OnRightButtonClickListener {
        void onClick();

    }

    private OnLeftButtonClickListener  onLeftButtonClickListener;
    private OnRightButtonClickListener onRightTextViewClickListener;

    public void setOnLeftButtonClickListener(OnLeftButtonClickListener listener) {
        onLeftButtonClickListener = listener;
    }

    public void setOnRightTextViewClickListener(OnRightButtonClickListener listener) {
        onRightTextViewClickListener = listener;
    }
}
