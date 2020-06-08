package com.example.customviewstuff;

import com.example.customviewstuff.activities.ArrowActivity;
import com.example.customviewstuff.activities.CartActivity;
import com.example.customviewstuff.avChat.ChatRoomActivity;
import com.example.customviewstuff.activities.EventDispatchActivity;
import com.example.customviewstuff.activities.EyeActivity;
import com.example.customviewstuff.activities.FadeAwayActivity;
import com.example.customviewstuff.activities.FireActivity;
import com.example.customviewstuff.activities.FrameActivity;
import com.example.customviewstuff.activities.HeartActivity;
import com.example.customviewstuff.activities.HelixActivity;
import com.example.customviewstuff.activities.HexaActivity;
import com.example.customviewstuff.activities.JumpBeanActivity;
import com.example.customviewstuff.activities.JumpTextActivity;
import com.example.customviewstuff.activities.LightingActivity;
import com.example.customviewstuff.activities.MultiTouchActivity;
import com.example.customviewstuff.activities.PTTActivity;
import com.example.customviewstuff.activities.RadarActivity;
import com.example.customviewstuff.activities.RippleActivity;
import com.example.customviewstuff.activities.SAEActivity;
import com.example.customviewstuff.activities.SnakeActivity;
import com.example.customviewstuff.activities.StackActivity;
import com.example.customviewstuff.activities.StaringActivity;
import com.example.customviewstuff.activities.StayAwayActivity;
import com.example.customviewstuff.activities.StereoActivity;
import com.example.customviewstuff.activities.TextAnimActivity;
import com.example.customviewstuff.activities.TimeActivity;
import com.example.customviewstuff.activities.ToastActivity;
import com.example.customviewstuff.activities.TreeActivity;
import com.example.customviewstuff.activities.TreeGrowActivity;
import com.example.customviewstuff.activities.TriAngleActivity;
import com.example.customviewstuff.activities.VideoActivity;
import com.example.customviewstuff.activities.WaveActivity;
import com.example.customviewstuff.activities.WaveTextActivity;
import com.example.customviewstuff.activities.WebActivity;
import com.example.customviewstuff.customs.soccerGame.SoccerActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    private static final String TIME = "酷炫时钟";
    private static final String TEXT_ANIM = "字的路径";
    private static final String TEXT_TREE_GROW = "成长的树（深度优先）";
    private static final String TREE = "成长的树（广度优先）";
    private static final String LIGHTING = "闪电";
    private static final String HEXA = "六边形";
    private static final String STAR = "星际穿越";
    private static final String SNAKE = "玩蛇";
    private static final String EVENT_DISPATCH = "事件分发";
    private static final String STAY_AWAY = "离我远点";
    private static final String FRAME = "gif";
    private static final String WAVE_TEXT = "波状的字";
    private static final String JUMP_TEXT = "弹起的字";
    private static final String WAVE_WATER = "水波";
    private static final String SOCCER = "足球小子";
    private static final String FADE_AWAY = "消散的光点";
    private static final String D_N_A = "螺旋飞天";
    private static final String STEREO = "立体";
    private static final String MULTI_TOUCH = "多点触控";
    private static final String FIRE = "火焰";
    private static final String EYE = "写轮眼";
    private static final String RADAR = "扫描";
    private static final String TOAST = "toast";
    private static final String STACK = "堆栈";
    private static final String CHAT = "聊天室";

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
        caches.put(TIME, TimeActivity.class);
        caches.put(TEXT_ANIM, TextAnimActivity.class);
        caches.put(TEXT_TREE_GROW, TreeGrowActivity.class);
        caches.put(LIGHTING, LightingActivity.class);
        caches.put(HEXA, HexaActivity.class);
        caches.put(TREE, TreeActivity.class);
        caches.put(STAR, StaringActivity.class);
        caches.put(SNAKE, SnakeActivity.class);
        caches.put(EVENT_DISPATCH, EventDispatchActivity.class);
        caches.put(STAY_AWAY, StayAwayActivity.class);
        caches.put(FRAME, FrameActivity.class);
        caches.put(JUMP_TEXT, JumpTextActivity.class);
        caches.put(WAVE_TEXT, WaveTextActivity.class);
        caches.put(WAVE_WATER, WaveActivity.class);
        caches.put(SOCCER, SoccerActivity.class);
        caches.put(FADE_AWAY, FadeAwayActivity.class);
        caches.put(D_N_A, HelixActivity.class);
        caches.put(STEREO, StereoActivity.class);
        caches.put(MULTI_TOUCH, MultiTouchActivity.class);
        caches.put(FIRE, FireActivity.class);
        caches.put(EYE, EyeActivity.class);
        caches.put(RADAR, RadarActivity.class);
        caches.put(TOAST, ToastActivity.class);
        caches.put(STACK, StackActivity.class);
        caches.put(CHAT, ChatRoomActivity.class);
    }

    public static Class create(String s) {
        for (Map.Entry<String, Class> entry : caches.entrySet()) {
            if (entry.getKey().equals(s)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static Set<String> keys() {
        return caches.keySet();
    }
}
