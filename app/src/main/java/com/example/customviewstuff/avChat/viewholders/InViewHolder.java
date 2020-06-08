package com.example.customviewstuff.avChat.viewholders;

import android.support.annotation.NonNull;
import android.view.View;

import com.example.customviewstuff.avChat.ChatBean;
import com.example.customviewstuff.databinding.ItemChatInBinding;

public class InViewHolder extends BaseViewHolder<ItemChatInBinding> {
    public InViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(ChatBean chatBean) {
        dataBinding.account.setText(chatBean.getAccount());
        dataBinding.content.setText(chatBean.getContent());
    }
}
