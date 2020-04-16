package com.example.customviewstuff.customs.eventDispatch;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Wang.Wenhui
 * Date: 2020/4/16
 */
public class SampleView extends View implements OnTypeChanged {
    private OnEventActive active;
    private ReturnType dispatchType, onTouchType;

    public SampleView(Context context) {
        this(context, null);
    }

    public SampleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SampleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        dispatchType = ReturnType.SUPER;
        onTouchType = ReturnType.SUPER;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (active != null) {
            active.onDispatchActive();
        }
        switch (dispatchType) {
            case TRUE:
                return true;
            case FALSE:
                return false;
            case SUPER:
            default:
                return super.dispatchTouchEvent(event);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (active != null) {
            active.onTouchActive();
        }
        switch (onTouchType) {
            case TRUE:
                return true;
            case FALSE:
                return false;
            case SUPER:
            default:
                return super.onTouchEvent(event);
        }
    }

    public void setActive(OnEventActive active) {
        this.active = active;
    }

    @Override
    public void onTypeChanged(String name, ReturnType type) {
        switch (name) {
            case EventDispatchView.VD:
                dispatchType = type;
                break;
            case EventDispatchView.VO:
                onTouchType = type;
                break;
        }
    }
}
