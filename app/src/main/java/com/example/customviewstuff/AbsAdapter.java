package com.example.customviewstuff;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Wang.Wenhui
 * Date: 2020/6/24
 * Description: blablabla
 */
public abstract class AbsAdapter<T> extends RecyclerView.Adapter<AbsViewHolder<T, ?>> {
    private Context context;
    private List<T> list;

    public AbsAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AbsViewHolder<T, ?> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return viewHolder(inflater, parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull AbsViewHolder<T, ?> holder, int position) {
        holder.bind(list.get(position), position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    protected abstract AbsViewHolder<T, ?> viewHolder(LayoutInflater inflater, ViewGroup parent, int type);
}
