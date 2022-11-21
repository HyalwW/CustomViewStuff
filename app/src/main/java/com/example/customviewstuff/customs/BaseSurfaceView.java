package com.example.customviewstuff.customs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Wang.Wenhui
 * Date: 2020/1/10
 * Description:SurfaceView父类
 */
public abstract class BaseSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Handler.Callback {
    private HandlerThread mHandlerThread;
    private Handler drawHandler;
    private MsgBuilder builder;
    protected SurfaceHolder holder;
    protected long UPDATE_RATE = 16;
    protected Paint mPaint;
    protected volatile boolean running = true, isDrawing = false, isAlive;
    protected int mWidth, mHeight;
    private List<Runnable> queue;
    private LifecycleListener listener;
    private ExecutorService threadPool;

    public BaseSurfaceView(Context context) {
        this(context, null);
    }

    public BaseSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setFilterBitmap(true);

        holder = getHolder();
        holder.addCallback(this);

        builder = new MsgBuilder();
        threadPool = Executors.newCachedThreadPool();
        onInit();
    }

    private static final int BASE_RUN = 207, CALL_RUN = 208, RUN_ON_THREAD = 209;

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case BASE_RUN:
                if (running) {
                    long before = System.currentTimeMillis();
                    drawEverything(null, null);
                    long waste = System.currentTimeMillis() - before;
                    builder.newMsg().what(BASE_RUN).sendDelay(UPDATE_RATE - waste);
                }
                break;
            case CALL_RUN:
                if (msg.peekData() != null) {
                    Bundle bundle = msg.peekData();
                    Rect dirty = bundle.getParcelable("dirty");
                    drawEverything(msg.obj, dirty);
                } else {
                    drawEverything(msg.obj, null);
                }
                break;
            case RUN_ON_THREAD:
                threadPool.execute((Runnable) msg.obj);
                break;
        }
        return true;
    }

    private synchronized void drawEverything(Object data, Rect dirty) {
        if (!isAlive) return;
        dispatchSafeModifyData();
        isDrawing = true;
        onDataUpdate();
        if (dirty != null) {
            Canvas canvas = holder.lockCanvas(dirty);
            //todo 第一次会出现问题
            if (canvas != null) {
                if (!preventClear()) {
                    clearCanvas(canvas);
                }
                onDrawRect(canvas, data, dirty);
                if (holder.getSurface().isValid()) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        } else {
            Canvas canvas = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                canvas = holder.lockHardwareCanvas();
            } else {
                canvas = holder.lockCanvas();
            }
            if (canvas != null) {
                if (!preventClear()) {
                    clearCanvas(canvas);
                }
                if (running) {
                    onRefresh(canvas);
                }
                if (data != null) {
                    draw(canvas, data);
                }
                if (holder.getSurface().isValid()) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
        isDrawing = false;
    }

    private void dispatchSafeModifyData() {
        if (queue != null && queue.size() > 0 && !isDrawing) {
            for (Runnable runnable : queue) {
                runnable.run();
            }
            queue.clear();
        }
    }

    protected void clearCanvas(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHandlerThread = new HandlerThread("drawThread");
        mHandlerThread.start();
        drawHandler = new Handler(mHandlerThread.getLooper(), this);
        isAlive = true;
        //解决第一次使用dirty会被改变的问题
        callDraw("", new Rect());
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        onReady();
        if (listener != null) {
            listener.onCreate();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (listener != null) {
            listener.onChanged();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (running) {
            stopAnim();
        }
        isAlive = false;
        drawHandler.removeCallbacksAndMessages(null);
        if (queue != null && queue.size() > 0) {
            queue.clear();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mHandlerThread.quitSafely();
        } else {
            mHandlerThread.quit();
        }
        if (listener != null) {
            listener.onDestroy();
        }
    }

    public void startAnim() {
        running = true;
        builder.newMsg().what(BASE_RUN).send();
    }

    public void stopAnim() {
        running = false;
        drawHandler.removeMessages(BASE_RUN);
    }

    public void callDraw(Object data) {
        callDrawDelay(data, 0);
    }

    public void callDrawDelay(Object data, long millis) {
        builder.newMsg().obj(data).what(CALL_RUN).sendDelay(millis);
    }

    public void callDraw(Object data, Rect rect) {
        callDrawDelay(data, rect, 0);
    }

    public void callDrawDelay(Object data, Rect rect, long millis) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("dirty", rect);
        builder.newMsg().obj(data).what(CALL_RUN).bundle(bundle).sendDelay(millis);
    }

    //注意死循环线程
    public void doInThread(Runnable runnable) {
        doInThread(runnable, false);
    }

    //注意死循环线程
    public void doInThread(Runnable runnable, boolean forceRun) {
        if (isAlive) {
            builder.newMsg().what(RUN_ON_THREAD).obj(runnable).send();
        } else {
            if (forceRun) {
                threadPool.execute(runnable);
            }
        }
    }

    /**
     * 要随时对list进行操作时用这个
     *
     * @param runnable run
     */
    public void safeModifyData(Runnable runnable) {
        if (!isDrawing) {
            runnable.run();
        } else {
            if (queue == null) {
                queue = new CopyOnWriteArrayList<>();
            }
            queue.add(runnable);
        }
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private final class MsgBuilder {

        private Message message;

        MsgBuilder newMsg() {
            message = Message.obtain();
            return this;
        }

        MsgBuilder what(int what) {
            checkMessageNonNull();
            message.what = what;
            return this;
        }

        MsgBuilder obj(Object o) {
            checkMessageNonNull();
            message.obj = o;
            return this;
        }

        MsgBuilder bundle(Bundle bundle) {
            checkMessageNonNull();
            message.setData(bundle);
            return this;
        }

        void send() {
            sendDelay(0);
        }

        private void sendDelay(long millis) {
            checkMessageNonNull();
            if (mHandlerThread.isAlive() && isAlive) {
                drawHandler.sendMessageAtTime(message, millis < 0 ? 10 : SystemClock.uptimeMillis() + millis);
            }
        }

        private void checkMessageNonNull() {
            if (message == null) {
                throw new IllegalStateException("U should call newMsg() before use");
            }
        }
    }

    public void setListener(LifecycleListener listener) {
        this.listener = listener;
    }

    public interface LifecycleListener {

        void onCreate();

        void onChanged();

        void onDestroy();

    }

    /**
     * 用于初始化画笔，基础数据等
     */
    protected abstract void onInit();

    /**
     * 此时handler可用，异步加载数据调用doInThread
     */
    protected abstract void onReady();

    /**
     * 数据更新，刷新前调用
     */
    protected abstract void onDataUpdate();

    /**
     * 绘制内容，用于默认开启的绘图刷新,若不需要则调用stopAnim停止会刷新
     *
     * @param canvas 画布
     */
    protected void onRefresh(Canvas canvas) {}

    /**
     * 调用callDraw后触发，根据特定data绘制
     *
     * @param canvas 画布
     * @param data   数据
     */
    protected void draw(Canvas canvas, Object data) {}

    /**
     * 局部刷新
     *
     * @param canvas
     * @param data   数据
     * @param rect   画布大小
     */
    protected void onDrawRect(Canvas canvas, Object data, Rect rect) {}

    /**
     * 是否阻止刷新时清空画布
     *
     * @return
     */
    protected boolean preventClear() {
        return false;
    }
}
