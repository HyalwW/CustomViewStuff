package com.example.customviewstuff;

import android.util.Log;
import android.util.SparseArray;

/**
 * 对象池--（雾）
 *
 * @param <T> 具体类
 */
public class Pool<T extends Reusable> {
    private SparseArray<T> pools;
    private int poolSize;
    private Creator<T> creator;

    public Pool(Creator<T> creator) {
        this(Integer.MAX_VALUE, creator);
    }

    public Pool(int size, Creator<T> creator) {
        this.poolSize = size;
        this.creator = creator;
        pools = new SparseArray<>();
    }

    public synchronized T get() {
        Log.e("wwh", "Pool-->get(): " + pools.size());
        for (int i = 0; i < pools.size(); i++) {
            T value = pools.valueAt(i);
            if (value.isLeisure()) {
                value.reset();
                return value;
            }
        }
        if (pools.size() < poolSize) {
            T t = creator.create();
            pools.put(pools.size(), t);
            return t;
        } else {
            return creator.create();
        }
    }

    public interface Creator<T> {
        T create();
    }
}
