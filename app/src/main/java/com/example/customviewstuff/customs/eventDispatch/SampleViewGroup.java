package com.example.customviewstuff.customs.eventDispatch;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Wang.Wenhui
 * Date: 2020/4/16
 */
public class SampleViewGroup extends ConstraintLayout implements OnTypeChanged {
    private ReturnType dispatchType, interceptType, onTouchType;
    private OnEventActive active;

    public SampleViewGroup(Context context) {
        super(context);
        init();
    }

    public SampleViewGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SampleViewGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        dispatchType = ReturnType.SUPER;
        interceptType = ReturnType.SUPER;
        onTouchType = ReturnType.SUPER;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
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
                return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (active != null) {
            active.onInterceptActive();
        }
        switch (interceptType) {
            case TRUE:
                return true;
            case FALSE:
                return false;
            case SUPER:
            default:
                return super.onInterceptTouchEvent(ev);
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
            case EventDispatchView.VGD:
                dispatchType = type;
                break;
            case EventDispatchView.VGI:
                interceptType = type;
                break;
            case EventDispatchView.VGO:
                onTouchType = type;
                break;
        }
    }
}
