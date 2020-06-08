package com.example.customviewstuff.avChat.viewholders;

import android.support.annotation.NonNull;
import android.view.View;

import com.example.customviewstuff.avChat.ChatBean;
import com.example.customviewstuff.databinding.ItemChatTipBinding;

public class TipViewHolder extends BaseViewHolder<ItemChatTipBinding> {

    public TipViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(ChatBean chatBean) {
        dataBinding.tip.setText(chatBean.getTip());
    }
}
