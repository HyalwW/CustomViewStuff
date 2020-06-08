package com.example.customviewstuff.avChat.viewholders;

import android.support.annotation.NonNull;
import android.view.View;

import com.example.customviewstuff.avChat.ChatBean;
import com.example.customviewstuff.databinding.ItemChatOutBinding;

public class OutViewHolder extends BaseViewHolder<ItemChatOutBinding> {
    public OutViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(ChatBean chatBean) {
        dataBinding.account.setText(chatBean.getAccount());
        dataBinding.content.setText(chatBean.getContent());
    }
}
