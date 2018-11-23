package com.xw.sample.dashboardviewdemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * 信用分刻度表
 * Created by BoQin on 2018/11/22.
 * Modified by BoQin
 *
 * @Version
 */
public class QtsDashboardView extends View {

    /** 最大点数 */
    private static final int DOT_MAX_COUNT = 24;
    private static final int MAX_POINT = 100;

    private Paint mPaint;
    private RectF mRectF, mAlphaRectF;
    private int mPadding, mCenterX, mCenterY;

    private LinearGradient mLinearGradient;
    private int mColorStart;
    private int mColorEnd;

    /** 信用分 */
    private int mCreditValue;
    private int mLightCount;

    public QtsDashboardView(Context context) {
        this(context, null);
    }

    public QtsDashboardView(Context context,
            @Nullable
                    AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QtsDashboardView(Context context,
            @Nullable
                    AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        mRectF = new RectF();
        mAlphaRectF = new RectF();
        mPaint = new Paint();

        mColorStart = getResources().getColor(R.color.color_g_s);
        mColorEnd = getResources().getColor(R.color.color_g_e);

        mCreditValue = 0;

        mLightCount = 0;
    }

    /**
     * 测量
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mPadding = Math.max(
                Math.max(getPaddingLeft(), getPaddingTop()),
                Math.max(getPaddingRight(), getPaddingBottom())
        );
        setPadding(mPadding, mPadding, mPadding, mPadding);
        //设置成正方形
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());

        mCenterX = mCenterY = getMeasuredWidth()/2;
        //画圆球
        mAlphaRectF.set(mPadding+dp2px(20), mPadding+dp2px(20), getMeasuredWidth()-mPadding-dp2px(20), getMeasuredWidth()-mPadding-dp2px(20));
        mRectF.set(mPadding+dp2px(25), mPadding+dp2px(25), getMeasuredWidth()-mPadding-dp2px(25), getMeasuredWidth()-mPadding-dp2px(25));
        mLinearGradient = new LinearGradient(mPadding+dp2px(20),mPadding+dp2px(20), mPadding+dp2px(20), getMeasuredWidth()-mPadding-dp2px(15), new int[]{mColorStart, mColorEnd}, null, Shader.TileMode.CLAMP);
        //        mPath.
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dp2px(10));
        mPaint.setShader(mLinearGradient);
        mPaint.setAlpha(40);
        canvas.drawArc(mAlphaRectF, 0,360, true, mPaint);
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setShader(mLinearGradient);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawArc(mRectF, 0,360, true, mPaint);

        //刻度点
        mPaint.reset();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mLightCount==0?Color.GRAY:getResources().getColor(R.color.color_g_e));
        canvas.save();
        canvas.drawCircle(mCenterX, 0+dp2px(5), dp2px(4), mPaint);
        int degrees = 360/DOT_MAX_COUNT;
        for (int i=0; i<DOT_MAX_COUNT-1; i++){
            canvas.rotate(degrees, mCenterX, mCenterY);
            mPaint.setColor(i>=mLightCount?Color.GRAY:getResources().getColor(R.color.color_g_e));
            canvas.drawCircle(mCenterX, 0+dp2px(5), dp2px(4), mPaint);
        }
        canvas.restore();
//        canvas.draw

        //写字
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(getResources().getColor(R.color.color_light));

        mPaint.setTextSize(sp2px(10));
        canvas.drawText("信用分", mCenterX, mCenterY - dp2px(48) -dp2px(5), mPaint);


        mPaint.setTextSize(sp2px(48));
        canvas.drawText(String.valueOf(mCreditValue), mCenterX, mCenterY + dp2px(5), mPaint);

        mPaint.setTextSize(sp2px(10));
        canvas.drawText("信用评级", mCenterX, mCenterY+dp2px(15)+sp2px(10), mPaint);

        mPaint.setTextSize(sp2px(14));
        canvas.drawText(calculateCreditDescription(), mCenterX, mCenterY+dp2px(20)+sp2px(10)+sp2px(14), mPaint);
    }

    public void setCreditValueWithAnim(int value) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, value);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mCreditValue = value;
                mLightCount = mCreditValue *DOT_MAX_COUNT/MAX_POINT;
                postInvalidate();
            }
        });

        valueAnimator.setDuration(2000);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.start();

    }

    /**
     * 信用分对应信用描述
     */
    private String calculateCreditDescription() {
        if (mCreditValue >= 90) {
            return "信用极好";
        } else if (mCreditValue >= 80) {
            return "信用优秀";
        } else if (mCreditValue >= 70) {
            return "信用良好";
        } else if (mCreditValue >= 60) {
            return "信用中等";
        }
        return "信用较差";
    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }
}
