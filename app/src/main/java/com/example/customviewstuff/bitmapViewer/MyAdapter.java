package com.example.customviewstuff.bitmapViewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.customviewstuff.AbsAdapter;
import com.example.customviewstuff.AbsViewHolder;
import com.example.customviewstuff.R;

import java.util.List;

/**
 * Created by Wang.Wenhui
 * Date: 2020/6/24
 * Description: blablabla
 */
public class MyAdapter extends AbsAdapter<Bitmap> {

    public MyAdapter(Context context, List<Bitmap> list) {
        super(context, list);
    }

    @Override
    protected AbsViewHolder<Bitmap, ?> viewHolder(LayoutInflater inflater, ViewGroup parent, int type) {
        return new MyViewHolder(inflater.inflate(R.layout.item_bitmap, parent, false));
    }
}
