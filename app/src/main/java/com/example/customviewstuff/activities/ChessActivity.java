package com.example.customviewstuff.activities;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.R;
import com.example.customviewstuff.databinding.ActivityChessBinding;
import com.example.customviewstuff.socket.SocketListener;
import com.example.customviewstuff.socket.SocketManager;

public class ChessActivity extends BaseActivity<ActivityChessBinding> implements SocketListener, View.OnClickListener {
    private SocketManager manager;
    private boolean isHost;

    @Override
    protected int layoutId() {
        return R.layout.activity_chess;
    }

    @Override
    protected void onInit() {
        manager = new SocketManager();
        manager.setListener(this);
        dataBinding.create.setOnClickListener(this);
        dataBinding.add.setOnClickListener(this);
        dataBinding.chess.setSender(message -> manager.sendMessage(message));
    }

    @Override
    public void onStartConnect() {
    }

    @Override
    public void onConnectSuccess(boolean isHost, String toAddress, String meAddress) {
        dataBinding.chess.connect(true);
        Toast.makeText(this, toAddress + "加入游戏", Toast.LENGTH_SHORT).show();
        dataBinding.addPanel.setVisibility(View.GONE);
    }

    @Override
    public void onConnectFailed(String reason) {

    }

    @Override
    public void onDisconnect(String address) {
        dataBinding.chess.connect(false);
        if (isHost) {
            manager.searchClient(this, true);
        } else {
            manager.searchService(manager.getIpName());
        }
    }

    @Override
    public void onReceiveMsg(String address, String msg) {
        dataBinding.chess.receive(msg);
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.destroy();
    }

    @Override
    public void onClick(View v) {
        dataBinding.addPanel.setVisibility(View.GONE);
        switch (v.getId()) {
            case R.id.create:
                isHost = true;
                manager.searchClient(this, true);
                dataBinding.chess.setIsHost(true, manager.getIpName());
                break;
            case R.id.add:
                isHost = false;
                if (TextUtils.isEmpty(dataBinding.edit.getText())) {
                    Toast.makeText(this, "IP不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    String ip = dataBinding.edit.getText().toString();
                    manager.searchService(ip);
                    dataBinding.chess.setIsHost(false, ip);
                }
                break;
        }
    }
}
