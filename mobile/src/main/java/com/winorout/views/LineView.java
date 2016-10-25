package com.winorout.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.winorout.followme.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangchaohu on 2016/10/24.
 */

public class LineView extends View {

    private int xori; //圆点x的坐标
    private int yori;//圆点y坐标
    private int xinit;//第一个点x坐标
    private int minXinit;//在移动时，第一个点允许最小的x坐标
    private int maxXinit;//在移动时，第一个点允许允许最大的x坐标
    private int xylinecolor;//xy坐标轴颜色
    private int xylinewidth;//xy坐标轴大小
    private int xytextcolor;//xy坐标轴文字颜色
    private int xytextsize;//xy坐标轴文字大小
    private int lineColor;//折线的颜色
    private int interval;//坐标间的间隔
    private int bgColor;//背景颜色


    private List<String> x_coords;//x坐标点的值
    private List<String> x_coord_values;//每个点状态值


    private int width;//控件宽度
    private int heigth;//控件高度
    private int imageWidth;   //图片宽度
    private float textwidth;//y轴文字的宽度
    float startX = 0;//滑动时候，上一次手指的x坐标

    Map<String, String> mapValue;

    public LineView(Context context) {
        this(context, null);
    }

    public LineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LineView);

        xylinecolor = ta.getColor(R.styleable.LineView_xycolor, Color.BLUE);
        lineColor = ta.getColor(R.styleable.LineView_linecolor, Color.BLUE);
        xylinewidth = ta.getInt(R.styleable.LineView_xywidth, 5);
        xytextcolor = ta.getColor(R.styleable.LineView_textcolor, Color.BLACK);
        xytextsize = ta.getDimensionPixelSize(R.styleable.LineView_textsize, 20);
        lineColor = ta.getColor(R.styleable.LineView_linecolor, Color.GRAY);
        interval = ta.getDimensionPixelSize(R.styleable.LineView_interval, 100);
        bgColor = ta.getColor(R.styleable.LineView_bgcolor, Color.BLUE);

        ta.recycle();

        x_coords = new ArrayList<String>();

        /**
         * x轴下方的时间
         * */
        x_coords.add("10/11");
        x_coords.add("10/13");
        x_coords.add("10/14");
        x_coords.add("10/15");
        x_coords.add("10/16");
        x_coords.add("10/17");
        x_coords.add("10/18");
        x_coords.add("10/19");
        x_coords.add("10/12");
        x_coords.add("10/13");
        x_coords.add("10/14");
        x_coords.add("10/15");
        x_coords.add("10/18");
        x_coords.add("10/19");
        x_coords.add("10/12");
        x_coords.add("10/13");
        x_coords.add("10/14");
        x_coords.add("10/15");


        x_coord_values = new ArrayList<String>();

        /**
         * y轴上每个点的状态值
         * */
        x_coord_values.add("5000");
        x_coord_values.add("12000");
        x_coord_values.add("5340");
        x_coord_values.add("15500");
        x_coord_values.add("14400");
        x_coord_values.add("20100");
        x_coord_values.add("10400");
        x_coord_values.add("3000");
        x_coord_values.add("5000");
        x_coord_values.add("11000");
        x_coord_values.add("3000");
        x_coord_values.add("13420");
        x_coord_values.add("5430");
        x_coord_values.add("2000");
        x_coord_values.add("5400");
        x_coord_values.add("10000");
        x_coord_values.add("5200");
        x_coord_values.add("2000");
    }

    /**
     * 设置坐标折线图的值
     *
     * @param x_points
     * @param x_points_values
     */

    public void setValue(List<String> x_points, List<String> x_points_values) {

        if ((x_points.size() != x_points_values.size())) {
            throw new IllegalArgumentException("坐标轴点和坐标轴点的值的个数必须一样");
        }

        this.x_coords = x_points;
        this.x_coord_values = x_points_values;

    }

    /*
    * 重写onLayout方法，来计算控件宽高和坐标轴的原点坐标，
    * 坐标轴原点的x坐标可以通过y轴文字的宽度，y轴宽度，和距离y的水平距离进行计算
    *
    * **/
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        if (changed) {
            width = getWidth();
            heigth = getHeight() - 250;
            Paint paint = new Paint();
            paint.setTextSize(xytextsize);
            textwidth = paint.measureText("A");
            imageWidth = BitmapFactory.decodeResource(getResources(), R.drawable.circle).getWidth();
            xori = (int) (textwidth + 6 + 2 * xylinewidth);//
            yori = heigth - xytextsize - 2 * xylinewidth - 3;
            xinit = interval / 2 + xori;
            minXinit = width - xori - x_coords.size() * interval;
            maxXinit = xinit;
            setBackgroundColor(bgColor);
        }

        //得到每个值对应的坐标点
        mapValue = new HashMap<>();
        for (int i = 1; i <= 30000; i++) {
            mapValue.put(i + "", heigth * (30000 - i) / 30000 + "");
        }

        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawX(canvas);
        drawXY(canvas);
        drawY(canvas);
    }


    /**
     * 画x轴坐标点，折线
     */
    private void drawX(Canvas canvas) {
        Paint x_coordPaint = new Paint();

        x_coordPaint.setTextSize(xytextsize);

        x_coordPaint.setStyle(Paint.Style.FILL);

        Path path = new Path();
        //
        for (int i = 0; i < x_coords.size(); i++) {
            int x = i * interval + xinit;
            if (i == 0) {
                path.moveTo(x, getYValue(x_coord_values.get(i)));
            } else {
                path.lineTo(x, getYValue(x_coord_values.get(i)));
            }

            //画x轴上的小点
            x_coordPaint.setColor(lineColor);
            canvas.drawCircle(x, yori, xylinewidth, x_coordPaint);
            //画x轴下方的日期
            String text = x_coords.get(i);
            x_coordPaint.setColor(xytextcolor);
            canvas.drawText(text, x - x_coordPaint.measureText(text) / 2, yori + xytextsize + xylinewidth * 2, x_coordPaint);
        }

        //画折线
        x_coordPaint.setStyle(Paint.Style.STROKE);
        x_coordPaint.setStrokeWidth(xylinewidth);
        x_coordPaint.setColor(lineColor);

        canvas.drawPath(path, x_coordPaint);


        for (int i = 0; i < x_coords.size(); i++) {
            int x = i * interval + xinit;
            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.circle), x - imageWidth / 2, getYValue(x_coord_values.get(i)) - imageWidth / 2, x_coordPaint);
        }

        x_coordPaint.setStyle(Paint.Style.FILL);
        x_coordPaint.setColor(bgColor);
        x_coordPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        RectF rectF = new RectF(0, 0, xori, heigth);
        canvas.drawRect(rectF, x_coordPaint);
    }

    /**
     * 画坐标轴
     */

    private void drawXY(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(lineColor);
        paint.setStrokeWidth(xylinewidth);
        //画 坐标轴y
//        canvas.drawLine(xori, 0, xori, yori, paint);
        //画x坐标轴x
        canvas.drawLine(xori, yori, width, yori, paint);
    }

    /**
     * 画y轴坐标点
     */

    private void drawY(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(lineColor);
        paint.setStyle(Paint.Style.FILL);

        paint.setTextSize(xytextsize);
        paint.setColor(xytextcolor);

        //坐标轴y上的对应的4个值
        canvas.drawText("5000", xori - textwidth - 10 - xylinewidth, Float.parseFloat(mapValue.get("5000")) , paint);
        canvas.drawLine(xori - textwidth - 10 - xylinewidth, Float.parseFloat(mapValue.get("5000")), width, Float.parseFloat(mapValue.get("5000")), paint);
        canvas.drawText("10000", xori - textwidth - 10 - xylinewidth, (float) (Float.parseFloat(mapValue.get("10000"))), paint);
        canvas.drawLine(xori - textwidth - 10 - xylinewidth, Float.parseFloat(mapValue.get("10000")), width, Float.parseFloat(mapValue.get("10000")), paint);
        canvas.drawText("15000", xori - textwidth - 10 - xylinewidth, Float.parseFloat(mapValue.get("15000")), paint);
        canvas.drawLine(xori - textwidth - 10 - xylinewidth, Float.parseFloat(mapValue.get("15000")), width, Float.parseFloat(mapValue.get("15000")), paint);
        canvas.drawText("20000", xori - textwidth - 10 - xylinewidth, Float.parseFloat(mapValue.get("20000")), paint);
        canvas.drawLine(xori - textwidth - 10 - xylinewidth, Float.parseFloat(mapValue.get("20000")), width, Float.parseFloat(mapValue.get("20000")), paint);

    }


    /**
     * 得到y轴的坐标值
     */

    private float getYValue(String value) {
        return Float.parseFloat(mapValue.get(value));
    }


    /**
     * 左右滑动
     **/
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //如果不用滑动就可以展示所有数据，就不让滑动
        if (interval * x_coord_values.size() <= width - xori) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                break;

            case MotionEvent.ACTION_MOVE:
                float dis = event.getX() - startX;
                startX = event.getX();
                if (xinit + dis > maxXinit) {
                    xinit = maxXinit;
                } else if (xinit + dis < minXinit) {
                    xinit = minXinit;
                } else {
                    xinit = (int) (xinit + dis);
                }
                invalidate();

                break;
        }
        return true;
    }
}
