package com.example.customviewstuff.helpers;

import android.databinding.BindingAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class BindingUtil {
    @BindingAdapter(value = {"viewVisible", "showAnim", "hideAnim"}, requireAll = false)
    public static void setViewVisible(View view, boolean visible, int showAnim, int hideAnim) {
        if (visible) {
            if (view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
                if (showAnim != 0) {
                    view.setAnimation(AnimationUtils.loadAnimation(view.getContext(), showAnim));
                }
            }
        } else {
            if (view.getVisibility() == View.VISIBLE) {
                view.setVisibility(View.GONE);
                if (hideAnim != 0) {
                    view.setAnimation(AnimationUtils.loadAnimation(view.getContext(), hideAnim));
                }
            }
        }
    }

    @BindingAdapter(value = {"onTextChangeCommand"})
    public static void onTextChange(TextView view, OnTextChangeCommand command) {
        view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (command != null) {
                    command.onChanged(s.toString());
                }
            }
        });
    }

    public interface OnTextChangeCommand {
        void onChanged(String s);
    }

    @BindingAdapter(value = {"onClickCommand", "clickData"}, requireAll = false)
    public static <T> void onViewClick(View view, OnClickCommand<T> command, T data) {
        view.setOnClickListener(v -> {
            if (command != null) {
                command.onClick(v, data);
            }
        });
    }

    public interface OnClickCommand<T> {
        void onClick(View view, T data);
    }
}
