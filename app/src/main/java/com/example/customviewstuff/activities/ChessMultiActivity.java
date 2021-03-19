package com.example.customviewstuff.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.R;
import com.example.customviewstuff.avChat.ShowIpDialog;
import com.example.customviewstuff.databinding.ActivityChessBinding;
import com.example.customviewstuff.databinding.ActivityChessMultiBinding;
import com.example.customviewstuff.socket.SocketListener;
import com.example.customviewstuff.socket.SocketManager;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

public class ChessMultiActivity extends BaseActivity<ActivityChessMultiBinding> implements SocketListener, View.OnClickListener {
    private SocketManager manager;
    private boolean isHost;
    private ShowIpDialog showIpDialog;

    @Override
    protected int layoutId() {
        return R.layout.activity_chess_multi;
    }

    @Override
    protected void onInit() {
        manager = new SocketManager();
        manager.setListener(this);
        dataBinding.create.setOnClickListener(this);
        dataBinding.add.setOnClickListener(this);
        dataBinding.tvQr.setOnClickListener(this);
        dataBinding.showIpBtn.setOnClickListener(this);
        dataBinding.chess.setSender(message -> manager.sendMessage(message));
        showIpDialog = new ShowIpDialog(this);
        ZXingLibrary.initDisplayOpinion(this);
    }

    @Override
    public void onStartConnect() {
    }

    @Override
    public void onConnectSuccess(boolean isHost, String toAddress, String meAddress) {
        runOnUiThread(() -> {
            dataBinding.chess.connect(true);
            Toast.makeText(this, toAddress + "加入游戏", Toast.LENGTH_SHORT).show();
            dataBinding.addPanel.setVisibility(View.GONE);
            showIpDialog.dismiss();
            dataBinding.showIpBtn.setVisibility(View.GONE);
        });
    }

    @Override
    public void onConnectFailed(String reason) {

    }

    @Override
    public void onDisconnect(String address) {
        runOnUiThread(() -> {
            dataBinding.chess.connect(false);
            if (isHost) {
                manager.searchClient(this, true);
                dataBinding.showIpBtn.setVisibility(View.VISIBLE);
            } else {
                manager.searchService(manager.getIpName());
            }
        });
    }

    @Override
    public void onReceiveMsg(String address, String msg) {
        dataBinding.chess.receive(msg);
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 207 && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String ip = bundle.getString(CodeUtils.RESULT_STRING);
                    manager.searchService(ip);
                    dataBinding.chess.setIsHost(false, ip);
                    dataBinding.addPanel.setVisibility(View.GONE);
                } else {
                    Toast.makeText(this, "解析二维码失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.destroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create:
                isHost = true;
                manager.searchClient(this, true);
                dataBinding.chess.setIsHost(true, manager.getIpName());
                dataBinding.addPanel.setVisibility(View.GONE);
                dataBinding.showIpBtn.setVisibility(View.VISIBLE);
                break;
            case R.id.add:
                isHost = false;
                if (TextUtils.isEmpty(dataBinding.edit.getText())) {
                    Toast.makeText(this, "IP不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    String ip = dataBinding.edit.getText().toString();
                    manager.searchService(ip);
                    dataBinding.chess.setIsHost(false, ip);
                    dataBinding.addPanel.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_qr:
                startActivityForResult(new Intent(this, CaptureActivity.class), 207);
                break;
            case R.id.show_ip_btn:
                showIpDialog.show(manager.getIpName());
                break;
        }
    }
}
