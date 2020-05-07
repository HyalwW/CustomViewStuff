package com.example.customviewstuff.customs.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * Created by Wang.Wenhui
 * Date: 2019/12/26
 */
public class WeatherLine extends View {
    private Paint linePaint, dotPaint, textPaint, dashPaint;
    private float minLevel, maxLevel;
    private float maxTmp, minTmp;
    private float[] low, high;
    private float radius, lineWidth;
    private Data data;

    public WeatherLine(Context context) {
        this(context, null);
    }

    public WeatherLine(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
//        radius = getResources().getDimension(R.dimen.qb_px_6);
        radius = 6f;
//        lineWidth = getResources().getDimension(R.dimen.qb_px_3);
        lineWidth = 3f;
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        dashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setColor(Color.GRAY);
        dashPaint.setStrokeWidth(2);
        dashPaint.setPathEffect(new DashPathEffect(new float[]{7, 5}, 0));

        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setColor(Color.CYAN);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.CYAN);
        linePaint.setStrokeWidth(lineWidth);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
//        textPaint.setTextSize(getResources().getDimension(R.dimen.qb_px_20));
        textPaint.setTextSize(20f);

        low = new float[3];
        high = new float[3];
    }

    public void setData(Data data, List<Data> list) {
        minTmp = 99;
        maxTmp = -99;
        this.data = data;
        low[1] = data.min;
        high[1] = data.max;
        for (Data d : list) {
            float min = d.min;
            float max = d.max;
            if (minTmp > min) {
                minTmp = min;
            }
            if (maxTmp < max) {
                maxTmp = max;
            }
        }
        if (data.last != null) {
            low[0] = (data.min + data.last.min) / 2;
            high[0] = (data.max + data.last.max) / 2;
        }
        if (data.next != null) {
            low[2] = (data.min + data.next.min) / 2;
            high[2] = (data.max + data.next.max) / 2;
        }
        postInvalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        minLevel = (float) (getMeasuredHeight() * 0.15);
        maxLevel = getMeasuredHeight() - minLevel;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        if (data.last != null) {
            //左高到中高
            canvas.drawLine(0, getYPosition(high[0]), width >> 1, getYPosition(high[1]), linePaint);
            //左低到中低
            canvas.drawLine(0, getYPosition(low[0]), width >> 1, getYPosition(low[1]), linePaint);
        }
        if (data.next != null) {
            //中高到右高
            canvas.drawLine(width >> 1, getYPosition(high[1]), width, getYPosition(high[2]), linePaint);
            //中低到右低
            canvas.drawLine(width >> 1, getYPosition(low[1]), width, getYPosition(low[2]), linePaint);
        }
        canvas.drawLine(width >> 1, getYPosition(high[1]), width >> 1, getYPosition(low[1]), dashPaint);
        //上温度
//        canvas.drawText(data.maxStr, width >> 1, getYPosition(high[1]) - getResources().getDimension(R.dimen.qb_px_12), textPaint);
        canvas.drawText(data.maxStr, width >> 1, getYPosition(high[1]) - 12f, textPaint);
        //下温度
//        canvas.drawText(data.minStr, width >> 1, getYPosition(low[1]) + getResources().getDimension(R.dimen.qb_px_30), textPaint);
        canvas.drawText(data.minStr, width >> 1, getYPosition(low[1]) + 12f, textPaint);
        //上圆
        canvas.drawCircle(width >> 1, getYPosition(high[1]), radius, dotPaint);
        //下圆
        canvas.drawCircle(width >> 1, getYPosition(low[1]), radius, dotPaint);
    }

    private float getYPosition(float tmp) {
        return maxLevel - ((tmp - minTmp) / (maxTmp - minTmp) * (maxLevel - minLevel));
    }

    public static class Data {
        public String minStr, maxStr;
        public float min, max;
        public Data next, last;

        public Data(float min, float max, String minStr, String maxStr) {
            this.min = min;
            this.max = max;
            this.minStr = minStr;
            this.maxStr = maxStr;
        }
    }

    public interface Converter<T> {
        Data convert(T t);
    }
}
