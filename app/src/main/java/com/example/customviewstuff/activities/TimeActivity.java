package com.example.customviewstuff.activities;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.R;
import com.example.customviewstuff.databinding.ActivityTimeBinding;

public class TimeActivity extends BaseActivity<ActivityTimeBinding> {

    @Override
    protected int layoutId() {
        return R.layout.activity_time;
    }

    @Override
    protected void onInit() {
        dataBinding.t1.tick();
        dataBinding.t2.start();
    }
}
