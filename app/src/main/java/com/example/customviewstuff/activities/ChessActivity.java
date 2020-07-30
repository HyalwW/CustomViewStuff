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

    @Override
    protected int layoutId() {
        return R.layout.activity_chess;
    }

    @Override
    protected void onInit() {
        manager = new SocketManager();
        manager.setListener(this);
        dataBinding.create.setOnClickListener(this);
    }

    @Override
    public void onStartConnect() {

    }

    @Override
    public void onConnectSuccess(boolean isHost, String toAddress, String meAddress) {

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create:
                manager.searchClient(this, true);
                dataBinding.chess.setIsHost(true, manager.getIpName());
                break;
            case R.id.add:
                if (TextUtils.isEmpty(dataBinding.edit.getText())) {
                    Toast.makeText(this, "IP不能为空", Toast.LENGTH_SHORT).show();
                } else {

                }
                break;
        }
    }
}
