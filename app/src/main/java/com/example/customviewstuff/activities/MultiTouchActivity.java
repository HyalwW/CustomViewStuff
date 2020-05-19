package com.example.customviewstuff.activities;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.R;
import com.example.customviewstuff.databinding.ActivityMultiTouchBinding;

public class MultiTouchActivity extends BaseActivity<ActivityMultiTouchBinding> {

    @Override
    protected int layoutId() {
        return R.layout.activity_multi_touch;
    }

    @Override
    protected void onInit() {
        dataBinding.start.setOnClickListener(v -> dataBinding.multiView.start());
    }
}
