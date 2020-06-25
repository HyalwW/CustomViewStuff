package com.example.customviewstuff.bitmapViewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
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
    private LruCache<String, Bitmap> cache;

    public MyAdapter(Context context, List<Bitmap> list) {
        super(context, list);
        int max = (int) Runtime.getRuntime().maxMemory() / 2;
        cache = new LruCache<String, Bitmap>(max) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.isRecycled() ? max : value.getByteCount();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                Log.e("wwh", "MyAdapter-->entryRemoved(): " + key);
                super.entryRemoved(evicted, key, oldValue, newValue);
            }
        };
    }

    @Override
    protected AbsViewHolder<Bitmap, ?> viewHolder(LayoutInflater inflater, ViewGroup parent, int type) {
        return new MyViewHolder(inflater.inflate(R.layout.item_bitmap, parent, false), cache);
    }
}
