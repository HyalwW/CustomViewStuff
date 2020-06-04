package com.example.customviewstuff.activities;

import android.view.MotionEvent;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.R;
import com.example.customviewstuff.databinding.ActivityToastBinding;

import java.util.Random;

public class ToastActivity extends BaseActivity<ActivityToastBinding> {
    private Random random;
    private StringBuilder builder;

    @Override
    protected int layoutId() {
        return R.layout.activity_toast;
    }

    @Override
    protected void onInit() {
        random = new Random();
        builder = new StringBuilder();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            dataBinding.toast.toast(getStr());
        }
        return super.onTouchEvent(event);
    }

    public String getStr() {
        builder.delete(0, builder.length());
        int count = 5 + random.nextInt(10);
        for (int i = 0; i < count; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }
}
