package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.customviewstuff.helpers.BitmapUtil;
import com.example.customviewstuff.videoAbout.MediaDecoder;

import java.util.Arrays;

/**
 * Created by Wang.Wenhui
 * Date: 2020/1/15
 * Description:多线程视频帧处理
 */
public class VideoView extends BaseSurfaceView {
    private TextPaint mPaint;
    private String[] temps;
    //颜色跨度
    private int colorGap;
    //字体大小
    private int textSize;
    //一行多少字
    private int textInLine = 210;
    private MediaDecoder decoder;
    private boolean isPlaying;
    private Rect positionRect, videoRect;
    private String[] drawStrs;
    private String fileName;
    //处理的线程个数
    private int threadCount = 8;
    //播放进度
    private int playingIndex = 0;
    private PlayerListener listener;
//    private Runnable drawRunnable = () -> {
//        while (true) {
//            Log.e("wwh", "VideoView --> : " + isPlaying);
//            if (isPlaying) {
//                if (drawStrs != null && playingIndex < drawStrs.length) {
//                    if (!TextUtils.isEmpty(drawStrs[playingIndex])) {
//                        callDraw(drawStrs[playingIndex], videoRect);
//                        playingIndex++;
//                    } /*else {
//                        isPlaying = false;
//                    }*/
//                } else {
//                    playingIndex = 0;
//                    isPlaying = false;
//                }
//            }
//            try {
//                Thread.sleep(16);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    };

    public VideoView(Context context) {
        super(context);
    }

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float scaleX, scaleY, scale = 3f;
    private boolean zoomIn;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                zoomIn = true;
            case MotionEvent.ACTION_MOVE:
                scaleX = event.getX();
                scaleY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                zoomIn = false;
                break;
        }
        return true;
    }

    @Override
    protected void onInit() {
//        一二三六四品圆履
        String temp = "一十工干天王口凸田回品困圆淼";
        mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        temps = temp.split("");
        temps = Arrays.copyOfRange(temps, 1, temps.length);
        colorGap = -Color.BLACK / temps.length;
        mPaint.setFilterBitmap(true);
        decoder = new MediaDecoder();
        positionRect = new Rect();
        videoRect = new Rect();
    }

    @Override
    protected void onReady() {
        positionRect.set(0, (int) (getMeasuredHeight() * 0.93), getMeasuredWidth(), getMeasuredHeight());
        textSize = getMeasuredWidth() / textInLine;
        callDraw("drawBlack");
    }

    @Override
    protected void onDataUpdate() {

    }

    @Override
    protected void onRefresh(Canvas canvas) {

    }

    @Override
    protected void draw(Canvas canvas, Object data) {
        if (data instanceof String) {
            canvas.drawColor(Color.BLACK);
            if (data.equals(fileName)) {
                mPaint.setColor(Color.RED);
                mPaint.setTextSize(60);
                String text = "正在加载：" + data;
//                float width = mPaint.measureText(text);
//                canvas.drawText(text, (float) getMeasuredWidth() / 2 - width / 2, getMeasuredHeight() >> 1, mPaint);
                StaticLayout layout = new StaticLayout(text, mPaint, getMeasuredWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                layout.draw(canvas);
            }
        }
    }

    @Override
    protected void onDrawRect(Canvas canvas, Object data, Rect rect) {
        if (data instanceof String) {
//            Log.e("wwh", "VideoView --> draw: ");
            long start = System.currentTimeMillis();
            canvas.drawColor(Color.WHITE);
            String strings = (String) data;
            mPaint.setTextSize(textSize);
            mPaint.setColor(Color.BLACK);
            String[] split = strings.split(";");
            if (zoomIn) {
                canvas.scale(scale, scale, scaleX, scaleY);
            }
            //todo 一次性画完字解注释这
//            float width = mPaint.measureText(strings.substring(0, textInLine));
//            StaticLayout layout = new StaticLayout(strings, mPaint, (int) width, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
//            layout.draw(canvas);
            for (int i = 0; i < split.length; i++) {
                float lineWidth = mPaint.measureText(split[0]);
                canvas.drawText(split[i], (float) getMeasuredWidth() / 2 - lineWidth / 2, rect.top + textSize * (i + 1), mPaint);
            }
            long waste = System.currentTimeMillis() - start;
            post(() -> {
                if (listener != null && drawStrs != null) {
                    listener.onProgressPlayed((int) ((float) playingIndex / drawStrs.length * 100));
                }
            });
            if (isPlaying) {
                playingIndex++;
                if (drawStrs != null && playingIndex < drawStrs.length) {
                    if (!TextUtils.isEmpty(drawStrs[playingIndex])) {
                        callDrawDelay(drawStrs[playingIndex], videoRect, waste > 16 ? 10 : 16);
                    }
                } else {
                    playingIndex = 0;
                    isPlaying = false;
                    post(() -> {
                        if (listener != null) {
                            listener.onStop();
                        }
                    });
                }
            }
        } else if (data instanceof Float) {
            canvas.drawColor(Color.GRAY);
            mPaint.setColor(Color.BLUE);
            mPaint.setTextSize(60);
            float position = ((float) data);
            String text = position < 100 ? "视频处理进度:" + String.format("%.2f", position) + "%" : "处理完成";
            float width = mPaint.measureText(text);
            canvas.drawText(text, (float) getMeasuredWidth() / 2 - width / 2, ((rect.bottom - rect.top) >> 1) + rect.top + 30, mPaint);
        }
    }

    @Override
    protected boolean preventClear() {
        return false;
    }

    public void start() {
        Log.e("wwh", "VideoView --> start: ");
        isPlaying = true;
        if (drawStrs != null && !TextUtils.isEmpty(drawStrs[playingIndex])) {
            if (listener != null) {
                listener.onStart();
            }
            callDraw(drawStrs[playingIndex], videoRect);
        } else {
            Toast.makeText(getContext().getApplicationContext(), "还未处理到该视频帧，请稍后再试。。。", Toast.LENGTH_SHORT).show();
        }
    }

    public void pause() {
        isPlaying = false;
        if (listener != null) {
            listener.onStop();
        }
        Log.e("wwh", "VideoView --> pause: ");
    }

    private String genText(int pixel) {
        int index = temps.length - (pixel - Color.BLACK) / colorGap - 1;
        return temps[index < 0 ? 0 : index];
    }

    public void setFile(String fileName) {
        reset();
        this.fileName = fileName;
        callDraw(fileName);
        doInThread(() -> {
            decoder.decore(fileName);
            callDraw("init");
            drawStrs = new String[(int) (decoder.getVedioFileLength() / 30)];
            for (int i = 0; i < threadCount; i++) {
                doInThread(new DecodeBitmapRun(i, fileName));
            }
        });
    }

    private void reset() {
        drawStrs = null;
        isPlaying = false;
        progress = 0;
        playingIndex = 0;
        if (listener != null) {
            listener.onStop();
        }
    }

    public boolean isDecoreDone() {
        return decoder.isDone();
    }

    public String getFileName() {
        return fileName;
    }

    public void seekTo(int progress) {
        if (drawStrs == null) {
            return;
        }
        boolean isPlay = isPlaying;
        if (isPlay) {
            pause();
        }
        int v = (int) ((float) this.progress / drawStrs.length * 100);
        if (progress > v) {
            progress = v;
        }
        playingIndex = (int) ((float) progress / 100 * drawStrs.length);
        if (isPlay) {
            start();
        }
    }

    private class DecodeBitmapRun implements Runnable {
        private final String fileName;
        private int baseIndex;

        DecodeBitmapRun(int baseIndex, String fileName) {
            this.baseIndex = baseIndex;
            this.fileName = fileName;
        }

        @Override
        public void run() {
            int index = baseIndex;
            while (drawStrs != null && index < drawStrs.length && fileName.equals(VideoView.this.fileName)) {
//                Log.e("wwh", "DecodeBitmapRun --> run: 线程" + (baseIndex + 1) + "处理时间戳：" + index * 30 );
                Bitmap bitmap = decoder.decodeFrame(index * 30);
                if (textInLine < bitmap.getWidth()) {
                    bitmap = BitmapUtil.scaleBitmap(bitmap, (float) textInLine / bitmap.getWidth());
                }
                int bottom = textSize * (bitmap.getHeight() + 1);
                videoRect.set(0, 0, getMeasuredWidth(), bottom < getMeasuredHeight() * 0.9 ? bottom : (int) (getMeasuredHeight() * 0.9));
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < bitmap.getHeight(); i++) {
                    for (int j = 0; j < bitmap.getWidth(); j++) {
                        int pixel = bitmap.getPixel(j, i);
                        builder.append(genText(pixel));
                    }
                    //todo 一次性画完字注释这
                    builder.append(";");
                }
                bitmap.recycle();
                if (drawStrs != null && fileName.equals(VideoView.this.fileName)) {
                    drawStrs[index] = builder.toString();
                    //todo 若需要边加载边播放，解注释这里
                    if (isPlaying && playingIndex == index) {
                        callDrawDelay(drawStrs[index], videoRect, 16);
                    }
                    updateProgress();
                }
                index += threadCount;
            }
        }
    }

    private int progress = 0;

    private synchronized void updateProgress() {
        progress++;
        callDraw((float) progress / drawStrs.length * 100, positionRect);
        post(() -> {
            if (listener != null && drawStrs != null) {
                listener.onProgressBuffered((int) ((float) progress / drawStrs.length * 100));
            }
        });
    }

    public void setListener(PlayerListener listener) {
        this.listener = listener;
    }

    public interface PlayerListener {
        void onProgressBuffered(int progress);

        void onProgressPlayed(int progress);

        void onStart();

        void onStop();
    }
}
