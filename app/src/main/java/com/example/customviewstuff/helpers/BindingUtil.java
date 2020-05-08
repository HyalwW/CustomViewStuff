package com.example.customviewstuff.helpers;

import android.databinding.BindingAdapter;
import android.view.View;

public class BindingUtil {
    @BindingAdapter(value = {"viewVisible"})
    public static void setViewVisible(View view, boolean visible) {
        if (visible) {
            if (view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            }
        } else {
            if (view.getVisibility() == View.VISIBLE) {
                view.setVisibility(View.GONE);
            }
        }
    }
}
