package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.Toast;

import com.example.customviewstuff.R;
import com.example.customviewstuff.helpers.BitmapUtil;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Wang.Wenhui
 * Date: 2020/1/11
 */
public class BTTView extends BaseSurfaceView {
    private Bitmap bitmap, base;
    private String[] temps;
    private int colorGap;
    private int textSize;
    //一行多少字
    private int textInLine = 0;
    private Rect dst;
    private Random random;

    public BTTView(Context context) {
        super(context);
    }

    public BTTView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BTTView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInit() {
        //一二三工中水井四苗新鑫 口吕品㗊
        String temp = "一二三四";
        temps = temp.split("");
        temps = Arrays.copyOfRange(temps, 1, temps.length);
        colorGap = -Color.BLACK / temps.length;
        dst = new Rect();
        mPaint.setFilterBitmap(true);
        random = new Random();
        setZOrderOnTop(true);
        holder.setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    protected void onReady() {
        doInThread(() -> {
            if (base == null) {
                setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.gm));
                toast("默认图片载入完成");
            }
            draw();
        });
    }

    @Override
    protected void onDataUpdate() {
    }

    public void draw() {
        if (base == null) {
            toast("请先设置图片");
            return;
        }
        textInLine += 10;
        if (textInLine < base.getWidth()) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = BitmapUtil.scaleBitmap(base, (float) textInLine / base.getWidth());
        }
        String[][] strings = new String[bitmap.getHeight()][bitmap.getWidth()];
        textSize = getMeasuredWidth() / textInLine;
        for (int i = 0; i < strings.length; i++) {
            for (int j = 0; j < strings[i].length; j++) {
                int pixel = bitmap.getPixel(j, i);
                strings[i][j] = genText(pixel);
            }
        }
        callDraw(strings);
    }

    public void load(String url) {
        if (base != null && !base.isRecycled()) {
            base.recycle();
            textInLine = 0;
        }
        doInThread(() -> {
            Bitmap bitmap = BitmapUtil.getURLimage(url);
            post(() -> {
                if (bitmap == null) {
                    toast("图片加载失败");
                } else {
                    toast("图片加载完成");
                }
            });
            if (bitmap == null) {
                return;
            }
            setBitmap(bitmap);
        });
    }

    public void setBitmap(Bitmap bitmap) {
        textInLine = 0;
        if (bitmap.getHeight() > bitmap.getWidth()) {
            bitmap = BitmapUtil.rotate(bitmap, 90, bitmap.getWidth() >> 1, bitmap.getWidth() >> 1, true);
        }
        base = bitmap;
    }


    private String genText(int pixel) {
        int index = temps.length - (pixel - Color.BLACK) / colorGap - 1;
        return temps[index < 0 ? 0 : index];
    }

    @Override
    protected void onRefresh(Canvas canvas) {

    }

    String[] tags = new String[]{"知道上面是什么图吗？", "猜猜上面什么图"};

    @Override
    protected void draw(Canvas canvas, Object data) {
//        canvas.drawColor(Color.WHITE);
        if (data instanceof String[][]) {
            String[][] strings = (String[][]) data;
            mPaint.setTextSize(textSize);
            StringBuilder builder;
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.argb(180, 255, 255, 255));
            canvas.drawRect(0, 0, getMeasuredWidth(), textSize * (strings.length + 1), mPaint);
            mPaint.setColor(Color.BLACK);
            for (int i = 0; i < strings.length; i++) {
                String[] string = strings[i];
                builder = new StringBuilder();
                for (String s : string) {
                    builder.append(s);
                }
                float width = mPaint.measureText(builder.toString());
                canvas.drawText(builder.toString(), (float) getMeasuredWidth() / 2 - width / 2, textSize * (i + 1), mPaint);
            }
            if (textInLine < 540) {
                mPaint.setTextSize(50 + random.nextFloat() * 20);
                mPaint.setColor(randomColor());
                String text = tags[random.nextInt(2)];
                float width = mPaint.measureText(text);
                canvas.rotate(-20 + random.nextFloat() * 40, (float) getMeasuredWidth() / 2 - width / 2, (float) (getMeasuredHeight() * 0.7));
                canvas.drawText(text, (float) getMeasuredWidth() / 2 - width / 2, (float) (getMeasuredHeight() * 0.6), mPaint);
            } else {
                int top = textSize * (strings.length + 2);
                dst.set(0, top, getMeasuredWidth(), (int) (top + base.getHeight() * ((float) getMeasuredWidth() / base.getWidth())));
                canvas.drawBitmap(base, null, dst, mPaint);
//                mPaint.setTextAlign(Paint.Align.CENTER);
//                mPaint.setFakeBoldText(true);
//                mPaint.setColor(Color.RED);
//                mPaint.setTextSize(60);
//                canvas.drawText("D~D~D~DENG~~", getMeasuredWidth() >> 1, (float) (getMeasuredHeight() * 0.75), mPaint);
//                mPaint.setTextSize(68);
//                canvas.drawText("《鬼灭之刃》", getMeasuredWidth() >> 1, (float) (getMeasuredHeight() * 0.8), mPaint);
            }
        }
    }

    @Override
    protected void onDrawRect(Canvas canvas, Object data, Rect rect) {

    }

    private int randomColor() {
        return Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    @Override
    protected boolean preventClear() {
        return false;
    }

    private void toast(String s) {
        post(() -> Toast.makeText(getContext().getApplicationContext(), s, Toast.LENGTH_SHORT).show());
    }
}
