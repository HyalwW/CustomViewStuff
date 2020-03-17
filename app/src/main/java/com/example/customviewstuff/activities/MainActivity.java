package com.example.customviewstuff.activities;

import android.view.View;
import android.widget.LinearLayout;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.Factory;
import com.example.customviewstuff.R;
import com.example.customviewstuff.customs.DnmButton;
import com.example.customviewstuff.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements View.OnClickListener {

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
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp2px(70));
            params.topMargin = 10;
            dataBinding.btnContainer.addView(button, params);
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
