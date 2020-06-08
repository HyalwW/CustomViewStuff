package com.example.customviewstuff.activities;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.R;
import com.example.customviewstuff.databinding.ActivityChatRoomBinding;
import com.example.customviewstuff.socket.SocketListener;
import com.example.customviewstuff.socket.SocketManager;

public class ChatRoomActivity extends BaseActivity<ActivityChatRoomBinding> implements SocketListener {
    private SocketManager socketManager;

    @Override
    protected int layoutId() {
        return R.layout.activity_chat_room;
    }

    @Override
    protected void onInit() {
        socketManager = new SocketManager();
        socketManager.setListener(this);
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
