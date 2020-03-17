package com.example.customviewstuff;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity<DB extends ViewDataBinding> extends AppCompatActivity {
    protected DB dataBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, layoutId());
        onInit();
    }

    protected static void go(Context context, Class aClass) {
        context.startActivity(new Intent(context, aClass));
    }

    protected abstract int layoutId();

    protected abstract void onInit();
}
