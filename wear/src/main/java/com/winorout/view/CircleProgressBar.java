package com.winorout.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义圆形进度条
 * @author  ryzhang
 * @data 2016/10/23 10:47
 */
public class CircleProgressBar extends View {

        private static final String TAG = "CircleProgressBar";

        private int mMaxProgress = 10000;

        private int mProgress = 0;

        private final int mCircleLineStrokeWidth = 8;

        private final int mTxtStrokeWidth = 2;

        // 画圆所在的距形区域
        private final RectF mRectF;

        private final Paint mPaint;

        private final Context mContext;

        private String mTxtHint1;

        private String mTxtHint2;

        public CircleProgressBar(Context context, AttributeSet attrs) {
            super(context, attrs);

            mContext = context;
            mRectF = new RectF();
            mPaint = new Paint();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int width = this.getWidth();
            int height = this.getHeight();

            if (width != height) {
                int min = Math.min(width, height);
                width = min;
                height = min;
            }

            // 设置画笔相关属性
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.rgb(0xe9, 0xe9, 0xe9));
            canvas.drawColor(Color.TRANSPARENT);//设置画布透明
            mPaint.setStrokeWidth(mCircleLineStrokeWidth);//设置画笔宽度
            mPaint.setStyle(Paint.Style.STROKE);
            // 位置
            mRectF.left = mCircleLineStrokeWidth / 2; // 左上角x
            mRectF.top = mCircleLineStrokeWidth / 2; // 左上角y
            mRectF.right = width - mCircleLineStrokeWidth / 2; // 左下角x
            mRectF.bottom = height - mCircleLineStrokeWidth / 2; // 右下角y

            // 绘制圆圈，进度条背景
            canvas.drawArc(mRectF, -90, 360, false, mPaint);
            mPaint.setColor(Color.parseColor("#2abda3"));
            canvas.drawArc(mRectF, -90, ((float) mProgress / mMaxProgress) * 360, false, mPaint);

            // 绘制进度文案显示
            mPaint.setStrokeWidth(mTxtStrokeWidth);
            String text = mProgress+"";
            int textHeight = height / 6;
            mPaint.setTextSize(textHeight);
            int textWidth = (int) mPaint.measureText(text, 0, text.length());
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.parseColor("#129cee"));
            canvas.drawText(text, width / 2 - textWidth / 2, height / 2-textHeight/2, mPaint);//上

            text=mMaxProgress+"";
            textWidth = (int) mPaint.measureText(text, 0, text.length());
            mPaint.setColor(Color.parseColor("#ffffff"));
            canvas.drawText(text, width / 2 - textWidth / 2, height / 2 +textHeight*3/2 , mPaint);//下

            mPaint.setColor(Color.parseColor("#111111"));
            canvas.drawLine(width/4,height/2+1,width*3/4,height/2+1,mPaint);//线

        }

        public int getMaxProgress() {
            return mMaxProgress;
        }

        public void setMaxProgress(int maxProgress) {
            this.mMaxProgress = maxProgress;
        }

        public void setProgress(int progress) {
            this.mProgress = progress;
            this.invalidate();
        }

        public void setProgressNotInUiThread(int progress) {
            this.mProgress = progress;
            this.postInvalidate();
        }

        public String getmTxtHint1() {
            return mTxtHint1;
        }

        public void setmTxtHint1(String mTxtHint1) {
            this.mTxtHint1 = mTxtHint1;
        }

        public String getmTxtHint2() {
            return mTxtHint2;
        }

        public void setmTxtHint2(String mTxtHint2) {
            this.mTxtHint2 = mTxtHint2;
        }
}
