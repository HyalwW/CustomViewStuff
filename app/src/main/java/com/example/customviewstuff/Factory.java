package com.example.customviewstuff;

import com.example.customviewstuff.activities.ArrowActivity;
import com.example.customviewstuff.activities.BobbleActivity;
import com.example.customviewstuff.activities.CartActivity;
import com.example.customviewstuff.activities.ChessActivity;
import com.example.customviewstuff.activities.ChessMultiActivity;
import com.example.customviewstuff.activities.EventDispatchActivity;
import com.example.customviewstuff.activities.EyeActivity;
import com.example.customviewstuff.activities.FadeAwayActivity;
import com.example.customviewstuff.activities.FireActivity;
import com.example.customviewstuff.activities.FrameActivity;
import com.example.customviewstuff.activities.HeartActivity;
import com.example.customviewstuff.activities.HelixActivity;
import com.example.customviewstuff.activities.HexaActivity;
import com.example.customviewstuff.activities.IllusionActivity;
import com.example.customviewstuff.activities.ImageVerifyActivity;
import com.example.customviewstuff.activities.JumpBeanActivity;
import com.example.customviewstuff.activities.JumpTextActivity;
import com.example.customviewstuff.activities.LightingActivity;
import com.example.customviewstuff.activities.LoveActivity;
import com.example.customviewstuff.activities.MultiTouchActivity;
import com.example.customviewstuff.activities.PTTActivity;
import com.example.customviewstuff.activities.RadarActivity;
import com.example.customviewstuff.activities.RippleActivity;
import com.example.customviewstuff.activities.SAEActivity;
import com.example.customviewstuff.activities.SnakeActivity;
import com.example.customviewstuff.activities.SnowActivity;
import com.example.customviewstuff.activities.StackActivity;
import com.example.customviewstuff.activities.StarWarActivity;
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
import com.example.customviewstuff.avChat.ChatRoomActivity;
import com.example.customviewstuff.bitmapViewer.BitmapViewActivity;
import com.example.customviewstuff.customs.soccerGame.SoccerActivity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Factory {
    private static Map<String, Class> caches;

    public static final String HEART = "心脏蹦蹦";
    public static final String ARROW = "拇指射箭";
    public static final String CART_ANIM = "添加购物车动画";
    public static final String JUMP_BEAN = "抛体运动弹跳";
    public static final String RIPPLE = "上升气泡";
    public static final String SAE = "抖动与爆炸";
    public static final String TRIANGLE = "酷炫随机三角";
    public static final String SHINNY_WEB = "浮动的网";
    public static final String STRING_VIDEO = "纯字电影";
    public static final String PIC_TO_TEXT = "画画的字";
    public static final String TIME = "酷炫时钟";
    public static final String TEXT_ANIM = "字的路径";
    public static final String TEXT_TREE_GROW = "成长的树（深度优先）";
    public static final String TREE = "成长的树（广度优先）";
    public static final String LIGHTING = "闪电";
    public static final String HEXA = "六边形";
    public static final String STAR = "星际穿越";
    public static final String SNAKE = "玩蛇";
    public static final String EVENT_DISPATCH = "事件分发";
    public static final String STAY_AWAY = "离我远点";
    public static final String FRAME = "gif";
    public static final String WAVE_TEXT = "波状的字";
    public static final String JUMP_TEXT = "弹起的字";
    public static final String WAVE_WATER = "水波";
    public static final String SOCCER = "足球小子";
    public static final String FADE_AWAY = "消散的光点";
    public static final String D_N_A = "螺旋飞天";
    public static final String STEREO = "立体";
    public static final String MULTI_TOUCH = "多点触控";
    public static final String FIRE = "火焰";
    public static final String EYE = "写轮眼";
    public static final String RADAR = "扫描";
    public static final String TOAST = "toast";
    public static final String STACK = "堆栈";
    public static final String CHAT = "聊天室";
    public static final String BOBBLE = "泡泡龙";
    public static final String IMAGE_VIEW = "PDF查看器";
    public static final String ILLUSION = "幻觉线条";
    public static final String CHESS = "五子棋（单人）";
    public static final String CHESS_MULTI = "五子棋（联机）";
    public static final String IMAGE_VERIFY = "图片滑块验证";
    public static final String SNOW = "雪花飘飘";
    public static final String STAR_WAR = "飞机大战";
    public static final String LOVE_HEART = "芳心捕获器";

    static {
        caches = new LinkedHashMap<>();
        caches.put(LOVE_HEART, LoveActivity.class);
        caches.put(HEART, HeartActivity.class);
        caches.put(STAR_WAR, StarWarActivity.class);
        caches.put(JUMP_BEAN, JumpBeanActivity.class);
        caches.put(PIC_TO_TEXT, PTTActivity.class);
        caches.put(TIME, TimeActivity.class);
//        caches.put(CART_ANIM, CartActivity.class);
        caches.put(SOCCER, SoccerActivity.class);
        caches.put(STACK, StackActivity.class);
        caches.put(RIPPLE, RippleActivity.class);
        caches.put(SAE, SAEActivity.class);
        caches.put(ARROW, ArrowActivity.class);
        caches.put(CHESS, ChessActivity.class);
        caches.put(CHESS_MULTI, ChessMultiActivity.class);
        caches.put(TRIANGLE, TriAngleActivity.class);
        caches.put(SHINNY_WEB, WebActivity.class);
        caches.put(STRING_VIDEO, VideoActivity.class);
        caches.put(TEXT_ANIM, TextAnimActivity.class);
        caches.put(TEXT_TREE_GROW, TreeGrowActivity.class);
//        caches.put(LIGHTING, LightingActivity.class);
        caches.put(HEXA, HexaActivity.class);
        caches.put(TREE, TreeActivity.class);
        caches.put(STAR, StaringActivity.class);
        caches.put(SNAKE, SnakeActivity.class);
        caches.put(EVENT_DISPATCH, EventDispatchActivity.class);
        caches.put(STAY_AWAY, StayAwayActivity.class);
//        caches.put(FRAME, FrameActivity.class);
        caches.put(JUMP_TEXT, JumpTextActivity.class);
        caches.put(WAVE_TEXT, WaveTextActivity.class);
        caches.put(WAVE_WATER, WaveActivity.class);
        caches.put(FADE_AWAY, FadeAwayActivity.class);
        caches.put(D_N_A, HelixActivity.class);
//        caches.put(STEREO, StereoActivity.class);
        caches.put(MULTI_TOUCH, MultiTouchActivity.class);
        caches.put(FIRE, FireActivity.class);
        caches.put(EYE, EyeActivity.class);
//        caches.put(RADAR, RadarActivity.class);
//        caches.put(TOAST, ToastActivity.class);
        caches.put(CHAT, ChatRoomActivity.class);
        caches.put(BOBBLE, BobbleActivity.class);
        caches.put(IMAGE_VIEW, BitmapViewActivity.class);
//        caches.put(ILLUSION, IllusionActivity.class);
        caches.put(IMAGE_VERIFY, ImageVerifyActivity.class);
        caches.put(SNOW, SnowActivity.class);
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
