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
import android.util.Log;
import android.view.MotionEvent;
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
    private final int maxTextInLine = 540;
    private Bitmap bitmap, base;
    //需要用哪些字来绘制
    private String[] temps;
    //颜色跨度
    private int colorGap;
    //字的大小
    private int textSize;
    //一行多少字
    private int textInLine = 10;
    //绘制的字
    private String[][] strings;
    //原图片展示位置
    private Rect dst, textRect;
    private Random random;
    private boolean isDraw, isScale;
    private float sx, sy;

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
        textRect = new Rect();
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
            draw(false);
        });
    }

    @Override
    protected void onDataUpdate() {
    }

    public void draw(boolean raiseTextInLine) {
        if (base == null) {
            toast("请先设置图片");
            return;
        }
        if (isDraw) {
            return;
        }
        //先清除上次画的
//        callDraw("clear", textRect);
//        callDraw("clear", dst);
        if (raiseTextInLine) {
            if (textInLine < maxTextInLine)
            textInLine += 10;
            resize();
        } else {
            if (strings == null) {
                resize();
            }
        }
        callDraw("textPaint", textRect);
        if (textInLine >= maxTextInLine) {
            callDraw(base, dst);
        }
    }

    private void resize() {
        if (textInLine < base.getWidth()) {
            if (bitmap != null && !bitmap.isRecycled() && !bitmap.equals(base)) {
                bitmap.recycle();
            }
            bitmap = BitmapUtil.scaleBitmap(base, (float) textInLine / base.getWidth());
        } else {
            if (bitmap != null && !bitmap.isRecycled() && !bitmap.equals(base)) {
                bitmap.recycle();
            }
            bitmap = Bitmap.createBitmap(base);
        }
        strings = new String[bitmap.getHeight()][bitmap.getWidth()];
        textSize = getMeasuredWidth() / textInLine;
//        adjustTextSize();
        for (int i = 0; i < strings.length; i++) {
            for (int j = 0; j < strings[i].length; j++) {
                int pixel = bitmap.getPixel(j, i);
                strings[i][j] = genText(pixel);
            }
        }
        int bottom = Math.max(textSize * (strings.length + 1), textRect.bottom);
        textRect.set(0, 0, mWidth, bottom);
        int top = bottom + textSize;
        dst.set(0, top, mWidth, (int) (top + base.getHeight() * ((float) mWidth / base.getWidth())));
    }

    // todo 调整
    private void adjustTextSize() {
        textSize = 1;
        mPaint.setTextSize(textSize);
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < textInLine; i++) {
            text.append("四");
        }
        String s = text.toString();
        while (mPaint.measureText(s) < mWidth) {
            textSize += 1;
            mPaint.setTextSize(textSize);
        }
        Log.e("TAG", "adjustTextSize: " + textSize );
    }

    public void load(String url) {
        if (base != null && !base.isRecycled()) {
            base.recycle();
            textInLine = 10;
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
        textInLine = 10;
        if (bitmap.getHeight() > bitmap.getWidth()) {
            bitmap = BitmapUtil.rotate(bitmap, 90, bitmap.getWidth() >> 1, bitmap.getWidth() >> 1, true);
        }
        base = bitmap;
        strings = null;
        draw(false);
    }


    private String genText(int pixel) {
        int index = temps.length - (pixel - Color.BLACK) / colorGap - 1;
        return temps[Math.min(Math.max(index, 0), temps.length - 1)];
    }

    @Override
    protected void onRefresh(Canvas canvas) {

    }

//    String[] tags = new String[]{"知道上面是什么图吗？", "猜猜上面什么图"};

    @Override
    protected void draw(Canvas canvas, Object data) {
    }

    @Override
    protected void onDrawRect(Canvas canvas, Object data, Rect rect) {
        isDraw = true;
        if (data instanceof String) {
            switch (((String) data)) {
                case "clear":
                    clearCanvas(canvas);
                    break;
                case "textPaint":
                    if (isScale) {
                        canvas.save();
                        float scale = (float) Math.sqrt(textInLine / 10f);
                        canvas.scale(scale, scale, sx, sy);
                    }
                    mPaint.setTextSize(textSize);
                    StringBuilder builder;
                    mPaint.setStyle(Paint.Style.FILL);
                    mPaint.setColor(Color.argb(180, 255, 255, 255));
                    canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, mPaint);
                    mPaint.setColor(Color.BLACK);
                    mPaint.setTextAlign(Paint.Align.CENTER);
                    for (int i = 0; i < strings.length; i++) {
                        String[] string = strings[i];
                        builder = new StringBuilder();
                        for (String s : string) {
                            builder.append(s);
                        }
                        canvas.drawText(builder.toString(), mWidth / 2f, rect.top + textSize * (i + 1), mPaint);
                    }
                    if (isScale) {
                        canvas.restore();
                    }
//            if (textInLine < 540) {
//                mPaint.setTextSize(50 + random.nextFloat() * 20);
//                mPaint.setColor(randomColor());
//                String text = tags[random.nextInt(2)];
//                float width = mPaint.measureText(text);
//                canvas.rotate(-20 + random.nextFloat() * 40, (float) mWidth / 2 - width / 2, (float) (getMeasuredHeight() * 0.7));
//                canvas.drawText(text, (float) mWidth / 2 - width / 2, (float) (getMeasuredHeight() * 0.6), mPaint);
//            }
                    break;
            }
        } else if (data instanceof Bitmap) {
            canvas.drawBitmap(base, null, rect, mPaint);
        }
        isDraw = false;
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                sx = event.getX();
                sy = event.getY();
                isScale = true;
                draw(false);
                break;
            case MotionEvent.ACTION_UP:
                isScale = false;
                draw(false);
                break;
        }
        return true;
    }
}
