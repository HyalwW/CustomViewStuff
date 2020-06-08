package com.example.customviewstuff.avChat.viewholders;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.customviewstuff.avChat.ChatBean;

public abstract class BaseViewHolder<DB extends ViewDataBinding> extends RecyclerView.ViewHolder {
    protected DB dataBinding;

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
        dataBinding = DataBindingUtil.bind(itemView);
    }

    public abstract void bind(ChatBean chatBean);
}
