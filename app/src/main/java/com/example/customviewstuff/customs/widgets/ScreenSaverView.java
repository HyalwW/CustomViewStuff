package com.example.customviewstuff.customs.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.animation.DecelerateInterpolator;


import com.example.customviewstuff.customs.BaseSurfaceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wang.Wenhui
 * Date: 2020/4/24
 */
public class ScreenSaverView extends BaseSurfaceView {
    private static final String storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private Type type;
    private MediaPlayer player;
    private String videoFileDir;

    private List<String> imgFileDirs;
    private List<Bitmap> images;
    private static final long showTime = 5000;
    private int showIndex;
    private ValueAnimator animator;
    private Rect src, dst;
    private boolean isDraw;


    public ScreenSaverView(Context context) {
        super(context);
    }

    public ScreenSaverView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScreenSaverView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private long drawTime;

    @Override
    protected void onInit() {
        type = Math.random() > 0.5 ? Type.VIDEO : Type.IMAGE;
        switch (type) {
            case IMAGE:
                imgFileDirs = new ArrayList<>();
                imgFileDirs.add(storagePath + "/1/image1.jpg");
                imgFileDirs.add(storagePath + "/1/image2.jpg");
                images = new ArrayList<>();
                for (String fileDir : imgFileDirs) {
                    images.add(BitmapFactory.decodeFile(fileDir));
                }
                dst = new Rect();
                src = new Rect();
                animator = new ValueAnimator();
                animator.setDuration(800);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.addUpdateListener(animation -> {
                    float value = (float) animation.getAnimatedValue();
                    if (!isDraw && System.currentTimeMillis() - drawTime >= 16) {
                        drawTime = System.currentTimeMillis();
                        callDraw((int) value);
                    }
                    if (value == 0) {
                        showImage(true);
                    }
                });
                break;
            case VIDEO:
                initMediaPlayer();
                break;
        }
    }

    private void initMediaPlayer() {
        player = new MediaPlayer();
        videoFileDir = storagePath + "/ScreenSaverVideo.mp4";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            player.setAudioAttributes(new AudioAttributes
                    .Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                    .build());
        } else {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        player.setVolume(0, 0);
        player.setOnPreparedListener(mp -> player.start());
        player.setOnCompletionListener(mp -> player.start());
        try {
            player.setDataSource(videoFileDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onReady() {
        switch (type) {
            case IMAGE:
                scaleImages();
                animator.setFloatValues(getMeasuredWidth(), 0);
                showImage(false);
                break;
            case VIDEO:
                if (player == null) {
                    initMediaPlayer();
                }
                player.setDisplay(getHolder());
                play();
                break;
        }
    }

    private void scaleImages() {
//        for (int i = 0; i < images.size(); i++) {
//            Bitmap bitmap = images.get(i);
//            Bitmap scale = ImageUtils.scale(bitmap, getMeasuredWidth(), getMeasuredHeight(), true);
//            images.set(i, scale);
//        }
    }

    private Runnable imgRun = () -> animator.start();
    private Runnable raiseRun = () -> {
        showIndex++;
        if (showIndex == images.size()) {
            showIndex = 0;
        }
        callDraw(getMeasuredWidth());
        postDelayed(imgRun, showTime);
    };

    private void showImage(boolean raise) {
        if (raise) {
            postDelayed(raiseRun, 100);
        } else {
            callDraw(getMeasuredWidth());
            postDelayed(imgRun, showTime);
        }
    }

    @Override
    protected void onDataUpdate() {
    }

    @Override
    protected void onRefresh(Canvas canvas) {
    }

    @Override
    protected void draw(Canvas canvas, Object data) {
        if (data instanceof Integer) {
            isDraw = true;
            int offset = (int) data;
            src.set(getMeasuredWidth() - offset, 0, getMeasuredWidth(), getMeasuredHeight());
            dst.set(0, 0, offset, getMeasuredHeight());
            canvas.drawBitmap(images.get(showIndex), src, dst, mPaint);
            if (images.size() > 1) {
                int nextIndex = showIndex + 1 > images.size() - 1 ? 0 : showIndex + 1;
                src.set(0, 0, getMeasuredWidth() - offset, getMeasuredHeight());
                dst.set(offset, 0, getMeasuredWidth(), getMeasuredHeight());
                canvas.drawBitmap(images.get(nextIndex), src, dst, mPaint);
            }
            isDraw = false;
        }
    }

    @Override
    protected void onDrawRect(Canvas canvas, Object data, Rect rect) {

    }

    @Override
    protected boolean preventClear() {
        return false;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        switch (type) {
            case VIDEO:
                if (player != null) {
                    if (player.isPlaying()) {
                        player.stop();
                    }
                }
                break;
            case IMAGE:
                if (animator != null && animator.isRunning()) {
                    animator.cancel();
                }
                removeCallbacks(imgRun);
                removeCallbacks(raiseRun);
                break;
        }
    }

    private void play() {
        player.prepareAsync();
    }


    public enum Type {
        IMAGE,
        VIDEO
    }
}
