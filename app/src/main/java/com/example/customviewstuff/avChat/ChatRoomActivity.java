package com.example.customviewstuff.avChat;

import android.support.v7.widget.LinearLayoutManager;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.R;
import com.example.customviewstuff.databinding.ActivityChatRoomBinding;
import com.example.customviewstuff.socket.SocketListener;
import com.example.customviewstuff.socket.SocketManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Random;

public class ChatRoomActivity extends BaseActivity<ActivityChatRoomBinding> implements SocketListener {
    private SocketManager socketManager;
    private ChatAdapter adapter;
    private Gson mGson;

    @Override
    protected int layoutId() {
        return R.layout.activity_chat_room;
    }

    @Override
    protected void onInit() {
        socketManager = new SocketManager();
        socketManager.setListener(this);
        mGson = new Gson();
        adapter = new ChatAdapter(this, new ArrayList<>());
        dataBinding.chatContent.setAdapter(adapter);
        dataBinding.chatContent.setLayoutManager(new LinearLayoutManager(this));
//        Random random = new Random();
//        for (int i = 0; i < 20; i++) {
//            ChatBean bean = new ChatBean();
//            bean.setAccount("我儿砸");
//            bean.setContent("你是煞笔吗？？？");
//            bean.setTip("我儿砸骂骂咧咧退出群聊！");
//            bean.setType(1 + random.nextInt(3));
//            adapter.addBean(bean);
//        }
        dataBinding.chatContent.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void onStartConnect() {

    }

    @Override
    public void onConnectSuccess(boolean isHost, String address) {

    }

    @Override
    public void onConnectFailed(String reason) {

    }

    @Override
    public void onDisconnect(String address) {

    }

    @Override
    public void onReceiveMsg(String address, String msg) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketManager.destroy();
    }
}
