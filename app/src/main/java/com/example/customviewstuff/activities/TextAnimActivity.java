package com.example.customviewstuff.activities;

import android.text.TextUtils;
import android.view.View;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.R;
import com.example.customviewstuff.databinding.ActivityTextAnimBinding;

public class TextAnimActivity extends BaseActivity<ActivityTextAnimBinding> implements View.OnClickListener {

    @Override
    protected int layoutId() {
        return R.layout.activity_text_anim;
    }

    @Override
    protected void onInit() {
        dataBinding.add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                if (!TextUtils.isEmpty(dataBinding.edit.getText())) {
                    dataBinding.textView.setText(dataBinding.edit.getText().toString());
                }
                break;
        }
    }
}
