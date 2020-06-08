package com.example.customviewstuff.avChat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.customviewstuff.R;
import com.example.customviewstuff.avChat.viewholders.BaseViewHolder;
import com.example.customviewstuff.avChat.viewholders.InViewHolder;
import com.example.customviewstuff.avChat.viewholders.OutViewHolder;
import com.example.customviewstuff.avChat.viewholders.TipViewHolder;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<ChatBean> list;
    private Context mContext;

    public ChatAdapter(Context mContext, List<ChatBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ChatBean bean = list.get(position);
        switch (bean.getType()) {
            case ChatBean.CONTENT_IN:
                return new InViewHolder(inflater.inflate(R.layout.item_chat_in, viewGroup, false));
            case ChatBean.CONTENT_OUT:
                return new OutViewHolder(inflater.inflate(R.layout.item_chat_out, viewGroup, false));
            case ChatBean.TIP:
                return new TipViewHolder(inflater.inflate(R.layout.item_chat_tip, viewGroup, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder viewHolder, int position) {
        viewHolder.bind(list.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addBean(ChatBean chatBean) {
        list.add(chatBean);
        notifyDataSetChanged();
    }
}
