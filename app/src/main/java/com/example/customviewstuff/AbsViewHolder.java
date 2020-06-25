package com.example.customviewstuff;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Wang.Wenhui
 * Date: 2020/6/24
 * Description: blablabla
 */
public abstract class AbsViewHolder<T, DB extends ViewDataBinding> extends RecyclerView.ViewHolder {
    protected DB dataBinding;

    public AbsViewHolder(@NonNull View itemView) {
        super(itemView);
        dataBinding = DataBindingUtil.bind(itemView);
        initView(itemView);
    }

    protected abstract void initView(View itemView);

    public abstract void bind(T item, int position, int size);
}
