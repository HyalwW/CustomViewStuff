package com.example.customviewstuff.bitmapViewer;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.View;

import com.example.customviewstuff.AbsViewHolder;
import com.example.customviewstuff.R;
import com.example.customviewstuff.databinding.ItemBitmapBinding;

/**
 * Created by Wang.Wenhui
 * Date: 2020/6/24
 * Description: blablabla
 */
public class MyViewHolder extends AbsViewHolder<Bitmap, ItemBitmapBinding> {
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    protected void initView(View itemView) {

    }

    @Override
    public void bind(Bitmap item, int position) {
        dataBinding.image.setBitmap(R.drawable.gm, true);
    }
}
