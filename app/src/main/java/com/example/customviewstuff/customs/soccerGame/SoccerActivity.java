package com.example.customviewstuff.customs.soccerGame;

import android.graphics.Paint;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.R;
import com.example.customviewstuff.databinding.ActivitySoccerBinding;
import com.example.customviewstuff.socket.SocketListener;
import com.example.customviewstuff.socket.SocketManager;

public class SoccerActivity extends BaseActivity<ActivitySoccerBinding> implements View.OnClickListener, SocketListener {
    private boolean isHost;
    private BindingCommand command;
    private IpDialog dialog;
    private Vibrator vibrator;
    private SocketManager manager;
    private String ip;

    @Override
    protected int layoutId() {
        return R.layout.activity_soccer;
    }

    @Override
    protected void onInit() {
        command = new BindingCommand();
        dataBinding.setCommand(command);
        dataBinding.host.setOnClickListener(this);
        dataBinding.client.setOnClickListener(this);
        dataBinding.practice.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        dataBinding.practice.setOnClickListener(v -> {
            dataBinding.gameView.setHost(true);
            dataBinding.gameView.practice();
            command.showMainPanel(false);
        });
        manager = new SocketManager();
        manager.setListener(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        dataBinding.gameView.setListener(new SoccerView.OnMsgSendListener() {
            @Override
            public void onSend(String msg) {
                manager.sendMessage(msg);
            }

            @Override
            public void onGoal() {
                vibrator.vibrate(500);
            }
        });

        dialog = new IpDialog(this);
        dialog.setListener(ip -> {
            command.showButtons(false);
            SoccerActivity.this.ip = ip;
            callConnectHost(ip);
        });
    }

    private void callSearchClient() {
        manager.searchClient(this, true);
        command.setHelpText("等待陪玩连接。。。(主机IP：" + manager.getIpName() + ")");
    }

    private void callConnectHost(String ip) {
        command.setHelpText("寻找主机并连接。。。");
        manager.searchService(ip);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.destroy();
    }

    @Override
    public void onStartConnect() {
        Log.e("wwh", "SoccerActivity-->onStartConnect(): ");
    }

    @Override
    public void onConnectSuccess(boolean isHost, String toAddress, String meAddress) {
        command.showMainPanel(false);
        dataBinding.gameView.setConnect(true);
        Log.e("wwh", "SoccerActivity-->onConnectSuccess(): " + toAddress + " " + meAddress);
    }

    @Override
    public void onConnectFailed(String reason) {
        command.showButtons(true);
        command.showMainPanel(true);
        Log.e("wwh", "SoccerActivity-->onConnectFailed(): ");
    }

    @Override
    public void onDisconnect(String address) {
        dataBinding.gameView.setConnect(false);
        command.showMainPanel(true);
        if (isHost) {
            callSearchClient();
        } else {
            callConnectHost(ip);
        }
        Log.e("wwh", "SoccerActivity-->onDisconnect(): 断开连接 " + address);
    }

    @Override
    public void onReceiveMsg(String address, String msg) {
        dataBinding.gameView.receiveMsg(msg);
//        Log.e("wwh", "SoccerActivity-->onReceiveMsg(): ");
    }

    @Override
    public void onError(Throwable e) {
        Log.e("wwh", "SoccerActivity-->onError(): " + e.getMessage());
    }

    @Override
    public void onClick(View v) {
        isHost = v.getId() == R.id.host;
        dataBinding.gameView.setHost(isHost);
        if (isHost) {
            command.showButtons(false);
            callSearchClient();
        } else {
            dialog.show();
        }
    }
}
