package com.example.customviewstuff;

import com.example.customviewstuff.activities.ArrowActivity;
import com.example.customviewstuff.activities.CartActivity;
import com.example.customviewstuff.activities.HeartActivity;
import com.example.customviewstuff.activities.JumpBeanActivity;
import com.example.customviewstuff.activities.PTTActivity;
import com.example.customviewstuff.activities.RippleActivity;
import com.example.customviewstuff.activities.SAEActivity;
import com.example.customviewstuff.activities.TriAngleActivity;
import com.example.customviewstuff.activities.VideoActivity;
import com.example.customviewstuff.activities.WebActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Factory {
    private static Map<String, Class> caches;

    private static final String HEART = "心脏蹦蹦";
    private static final String ARROW = "拇指射箭";
    private static final String CART_ANIM = "添加购物车动画";
    private static final String JUMP_BEAN = "抛体运动弹跳";
    private static final String RIPPLE = "上升气泡";
    private static final String SAE = "抖动与爆炸";
    private static final String TRIANGLE = "酷炫随机三角";
    private static final String SHINNY_WEB = "浮动的网";
    private static final String STRING_VIDEO = "纯字电影";
    private static final String PIC_TO_TEXT = "画画的字";

    static {
        caches = new HashMap<>();
        caches.put(HEART, HeartActivity.class);
        caches.put(ARROW, ArrowActivity.class);
        caches.put(CART_ANIM, CartActivity.class);
        caches.put(JUMP_BEAN, JumpBeanActivity.class);
        caches.put(RIPPLE, RippleActivity.class);
        caches.put(SAE, SAEActivity.class);
        caches.put(TRIANGLE, TriAngleActivity.class);
        caches.put(SHINNY_WEB, WebActivity.class);
        caches.put(PIC_TO_TEXT, PTTActivity.class);
        caches.put(STRING_VIDEO, VideoActivity.class);
    }

    public static Class create(String s) {
        for (Map.Entry<String, Class> entry : caches.entrySet()) {
            if (entry.getKey().equals(s)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static List<String> keys() {
        List<String> s = new ArrayList<>();
        for (Map.Entry<String, Class> entry : caches.entrySet()) {
            s.add(entry.getKey());
        }
        return s;
    }
}
