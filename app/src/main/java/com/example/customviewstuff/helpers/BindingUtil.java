package com.example.customviewstuff.helpers;

import android.databinding.BindingAdapter;
import android.view.View;
import android.view.animation.AnimationUtils;

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
}
