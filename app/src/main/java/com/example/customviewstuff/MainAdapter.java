package com.example.customviewstuff;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.customviewstuff.databinding.ItemMainBinding;

import java.util.List;

/**
 * Created by Wang.Wenhui
 * Date: 2020/7/31
 * Description: blablabla
 */
public class MainAdapter extends AbsAdapter<String> {
    private View.OnClickListener listener;

    public MainAdapter(Context context, List<String> list) {
        super(context, list);
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected AbsViewHolder<String, ?> viewHolder(LayoutInflater inflater, ViewGroup parent, int type) {
        return new MainViewHolder(inflater.inflate(R.layout.item_main, parent, false));
    }

    public class MainViewHolder extends AbsViewHolder<String, ItemMainBinding> {

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        protected void initView(View itemView) {
            dataBinding.btn.setOnClickListener(listener);
        }

        @Override
        public void bind(String item, int position, int size) {
            dataBinding.btn.setText(item);
            dataBinding.btn.setTag(item);
        }
    }
}
