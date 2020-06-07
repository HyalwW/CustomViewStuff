package com.example.customviewstuff.activities;

import android.app.AlertDialog;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.R;
import com.example.customviewstuff.databinding.ActivityStackBinding;

public class StackActivity extends BaseActivity<ActivityStackBinding> {
    private AlertDialog exit;

    @Override
    protected int layoutId() {
        return R.layout.activity_stack;
    }

    @Override
    protected void onInit() {
        exit = new AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage("是否退出？")
                .setPositiveButton("确定", (dialog, which) -> {
                    exit.dismiss();
                    finish();
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    exit.dismiss();
                })
                .create();
    }

    @Override
    public void onBackPressed() {
        exit.show();
    }
}
