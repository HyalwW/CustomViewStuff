package com.example.customviewstuff.avChat.viewholders;

import android.support.annotation.NonNull;
import android.view.View;

import com.example.customviewstuff.avChat.ChatBean;
import com.example.customviewstuff.databinding.ItemChatTimeBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Wang.Wenhui
 * Date: 2020/6/9
 * Description: blablabla
 */
public class TimeViewHolder extends BaseViewHolder<ItemChatTimeBinding> {

    private SimpleDateFormat format;

    public TimeViewHolder(@NonNull View itemView) {
        super(itemView);
        format = new SimpleDateFormat("HH:mm", Locale.CHINA);
    }

    @Override
    public void bind(ChatBean chatBean) {
        dataBinding.time.setText(getTime(chatBean.getTime()));
    }

    private String getTime(long time) {
        Date date = new Date(time);
        return format.format(date);
    }
}
