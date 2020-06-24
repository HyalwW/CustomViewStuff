package com.example.customviewstuff.bitmapViewer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.R;
import com.example.customviewstuff.databinding.ActivityBitmapViewBinding;

import java.util.ArrayList;
import java.util.List;

public class BitmapViewActivity extends BaseActivity<ActivityBitmapViewBinding> {

    @Override
    protected int layoutId() {
        return R.layout.activity_bitmap_view;
    }

    @Override
    protected void onInit() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gm);
        List<Bitmap> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add(Bitmap.createBitmap(bitmap));
        }
        MyAdapter adapter = new MyAdapter(this, list);
        dataBinding.listView.setAdapter(adapter);
        dataBinding.listView.setLayoutManager(new LinearLayoutManager(this));
    }
}
