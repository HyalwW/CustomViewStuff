package com.example.customviewstuff.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.Factory;
import com.example.customviewstuff.R;
import com.example.customviewstuff.customs.DnmButton;
import com.example.customviewstuff.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements View.OnClickListener {
    private static final String[] BASIC_PERMISSIONS = new String[]{Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    @Override
    protected int layoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onInit() {
        for (String s : Factory.keys()) {
            DnmButton button = new DnmButton(this);
            button.setText(s);
            button.setTag(s);
            button.setOnClickListener(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp2px(80));
            params.topMargin = 10;
            dataBinding.btnContainer.addView(button, params);
        }
        for (String permission : BASIC_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, BASIC_PERMISSIONS, 111);
                break;
            }
        }

    }

    @Override
    public void onClick(View v) {
        go(this, Factory.create(((String) v.getTag())));
    }

    public int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * getResources().getDisplayMetrics().density);
    }
}
