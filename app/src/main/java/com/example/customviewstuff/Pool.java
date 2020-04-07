package com.example.customviewstuff;

import android.util.Log;
import android.util.SparseArray;

/**
 * 对象池--（雾）
 *
 * @param <T> 具体类
 */
public class Pool<T> {
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
            if (creator.isLeisure(value)) {
                creator.reset(value);
                return value;
            }
        }
        if (pools.size() < poolSize) {
            T t = creator.instance();
            pools.put(pools.size(), t);
            return t;
        } else {
            return creator.instance();
        }
    }

    public interface Creator<T> {
        /**
         * 对象实例构造函数
         *
         * @return 构造函数构造的对象
         */
        T instance();

        /**
         * 如果需要充值对象状态调用
         *
         * @param t 待返回的对象
         */
        void reset(T t);

        /**
         * 判断对象是否空闲的条件
         *
         * @param t 待返回的对象
         * @return 是否空闲
         */
        boolean isLeisure(T t);
    }
}
