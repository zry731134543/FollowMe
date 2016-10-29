package com.winorout.followme.sports;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by Mr_Yan on 2016/10/12.
 */

/**
 * 圆形进度条
 *
 * @author zhangruyi
 *
 */
public class CircleBar extends View {

    private RectF mColorWheelRectangle = new RectF();// 圆圈的矩形范围
    private Paint mDefaultWheelPaint;// 绘制底部灰色圆圈的画
    private Paint mColorWheelPaint;// 绘制蓝色扇形的画
    private Paint textPaint;// 中间文字的画
    private Paint textPaint1;// 上下文字的画
    private float mColorWheelRadius;// 圆圈普状态下的半
    private float circleStrokeWidth;// 圆圈的线条粗
    private float pressExtraStrokeWidth;// 按下状下增加的圆圈线条增加的粗细
    private int mText;// 中间文字内容
    private int mCount;// 为了达到数字增加效果而添加的变量
    private float mProgressAni;// 为了达到蓝色扇形增加效果而添加的变量
    private float mProgress;// 扇形弧度
    private int mTextSize;// 中间文字大小
    private int mTextSize1;// 上下文字大小
    private int mDistance;// 上下文字的距离
    BarAnimation anim;// 动画
    private int mType;// 根据传入的数值判断应该显示的页面
    public int max = 10000;

    public CircleBar(Context context) {
        super(context);
        init();
    }

    public CircleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        circleStrokeWidth = dip2px(getContext(), 20);// 圆圈的线条粗细
        pressExtraStrokeWidth = dip2px(getContext(), 2);// 按下状下增加的圆圈线条增加的粗细
        mTextSize = dip2px(getContext(), 50);// 中间文字大小
        mTextSize1 = dip2px(getContext(), 20);// 上下文字大小
        mDistance = dip2px(getContext(), 50);// 文字间的距离

        // 绘制蓝色扇形的画�?
        mColorWheelPaint = new Paint();
        mColorWheelPaint.setAntiAlias(true);//
        mColorWheelPaint.setColor(Color.parseColor("#68E614"));// 设置颜色0xFF29a6f6 #68E614
        mColorWheelPaint.setStyle(Paint.Style.STROKE);// 设置空心
        mColorWheelPaint.setStrokeWidth(circleStrokeWidth);// 设置圆圈粗细

        // 绘制底部白色圆圈
        mDefaultWheelPaint = new Paint();
        mDefaultWheelPaint.setAntiAlias(true);
        mDefaultWheelPaint.setColor(Color.parseColor("#FFFFFF"));
        mDefaultWheelPaint.setStyle(Paint.Style.STROKE);
        mDefaultWheelPaint.setStrokeWidth(circleStrokeWidth);

        // 中间文字
        textPaint = new Paint(Paint.LINEAR_TEXT_FLAG);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.parseColor("#6DCAEC"));
        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(mTextSize);

        // 上下文字
        textPaint1 = new Paint(Paint.LINEAR_TEXT_FLAG);
        textPaint1.setAntiAlias(true);
        textPaint1.setColor(Color.parseColor("#FFFFFF"));//#a1a3a6
        textPaint1.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint1.setTextAlign(Paint.Align.LEFT);
        textPaint1.setTextSize(mTextSize1);

        // 中间文字内容
        mText = 0;
        // 扇形弧度
        mProgress = 0;
        // 动画
        anim = new BarAnimation();
        anim.setDuration(0);
    }

    // 对外的一个接口，用来启动
    public void startCustomAnimation() {
        this.startAnimation(anim);
    }

    // 中间的数
    public synchronized void setText(int text) {
        mText = text;
        setProgress(mText,1);
        this.postInvalidate();// 可以用子线程更新视图的方法调用
    }

    // 设置圆圈的进度和圆圈显示的第几个页面
    public void setProgress(float progress, int mType) {
        mProgress = progress / max * 360.0f;
        this.mType = mType;
        this.postInvalidate();// 可以用子线程更新视图的方法调用�??
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        int halfHeight = getHeight() / 2;
        int halfWidth = getWidth() / 2;
        int radius = halfHeight < halfWidth ? halfHeight : halfWidth;
        // 圆圈的矩形范 绘制底部白色圆圈的画
        canvas.drawCircle(halfWidth, halfHeight, radius - 50f, mDefaultWheelPaint);
        // 为了达到蓝色扇形增加效果而添加的变量，他和mSweepAngle其实代表个意

        // mColorWheelRectangle是绘制蓝色扇形的画笔
        mColorWheelRectangle.top = halfHeight - radius + 50f;
        mColorWheelRectangle.bottom = halfHeight + radius - 50f;
        mColorWheelRectangle.left = halfWidth - radius + 50f;
        mColorWheelRectangle.right = halfWidth + radius - 50f;

        // 根据mProgressAni（角度）画扇
        canvas.drawArc(mColorWheelRectangle, -90, mProgressAni, false, mColorWheelPaint);
        Rect bounds = new Rect();
        String middleText = null;// 中间的文字
        String upText = null;// 上面文字
        String downText = null;// 底部文字

        if (this.mType == 1) {// 第一个页
            upText = "前进";
            downText = "目标:"+max+"步";
            middleText = String.valueOf(mCount);
        }

        // 中间文字的画
        textPaint.getTextBounds(middleText, 0, middleText.length(), bounds);
        // drawText各个属的意(文字,x坐标,y坐标,画笔)
        canvas.drawText(middleText, (mColorWheelRectangle.centerX()) - (textPaint.measureText(middleText) / 2),
                mColorWheelRectangle.centerY() + bounds.height() / 2, textPaint);
        textPaint1.getTextBounds(upText, 0, upText.length(), bounds);
        canvas.drawText(upText, (mColorWheelRectangle.centerX()) - (textPaint1.measureText(upText) / 2),
                mColorWheelRectangle.centerY() + bounds.height() / 2 - mDistance, textPaint1);
        textPaint1.getTextBounds(downText, 0, downText.length(), bounds);
//        canvas.drawText(downText, (mColorWheelRectangle.centerX()) - (textPaint1.measureText(downText) / 2),
//                mColorWheelRectangle.centerY() + bounds.height() / 2 + mDistance, textPaint1);
    }

    // 测量父布局的大小
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        mColorWheelRadius = min - circleStrokeWidth - pressExtraStrokeWidth;
        // set方法的参数意思：left,top,right,bottom
        mColorWheelRectangle.set(circleStrokeWidth + pressExtraStrokeWidth, circleStrokeWidth + pressExtraStrokeWidth,
                mColorWheelRadius, mColorWheelRadius);
    }

    /**
     * 继承animation的一个动画类
     */
    public class BarAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                mProgressAni = interpolatedTime * mProgress;
                mCount = (int) (interpolatedTime * mText);
            } else {
                mProgressAni = mProgress;
                mCount = mText;
            }
            postInvalidate();

        }
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}