package com.example.customviewstuff.activities;

import android.view.MotionEvent;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.R;
import com.example.customviewstuff.customs.CartAnimView;
import com.example.customviewstuff.databinding.ActivityCartBinding;

public class CartActivity extends BaseActivity<ActivityCartBinding> {

    @Override
    protected int layoutId() {
        return R.layout.activity_cart;
    }

    @Override
    protected void onInit() {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            dataBinding.cart.set(new CartAnimView.Pos(ev.getX(), ev.getY()), new CartAnimView.Pos(dataBinding.cart.getMeasuredWidth() >> 1, dataBinding.cart.getMeasuredHeight()));
        }
        return super.dispatchTouchEvent(ev);
    }
}
