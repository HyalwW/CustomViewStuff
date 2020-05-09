package com.example.customviewstuff.activities.soccer;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.R;
import com.example.customviewstuff.databinding.ActivitySoccerBinding;
import com.example.customviewstuff.helpers.NetworkUtil;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SoccerActivity extends BaseActivity<ActivitySoccerBinding> implements SocketListener, Handler.Callback, View.OnClickListener {
    private boolean isHost, isConnected;
    private ServerSocket serverSocket;
    private Socket socket;
    private SocketThread socketThread;
    private String hostName;
    private HandlerThread searchThread;
    private Handler searchHandler;
    private BindingCommand command;
    private IpDialog dialog;
    private static final int SEARCH_CLIENT = 207, CONNECT_SERVE = 208;

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
        dataBinding.gameView.setListener(msg -> {
            if (socketThread != null) {
                socketThread.send(msg);
            }
        });

        searchThread = new HandlerThread("searchThread");
        searchThread.start();
        searchHandler = new Handler(searchThread.getLooper(), this);

        dialog = new IpDialog(this);
        dialog.setListener(ip -> {
            command.showButtons(false);
            hostName = ip;
            callConnectHost();
        });
    }

    private void callSearchClient() {
        command.setHelpText("等待副机连接。。。(主机IP：" + hostName + ")");
        send(SEARCH_CLIENT);
    }

    private void callConnectHost() {
        command.setHelpText("寻找主机并连接。。。");
        send(CONNECT_SERVE);
    }

    private void send(int what) {
        if (!isConnected) {
            Message message = searchHandler.obtainMessage();
            message.what = what;
            message.sendToTarget();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        searchHandler.removeMessages(SEARCH_CLIENT);
        searchHandler.removeMessages(CONNECT_SERVE);
        searchThread.quit();
    }

    @Override
    public void onReceiveMsg(String msg) {
        dataBinding.gameView.receiveMsg(msg);
    }

    @Override
    public void onClose() {
        isConnected = false;
        dataBinding.gameView.setConnect(false);
        command.showMainPanel(true);
        socketThread = null;
        if (isHost) {
            if (socket != null) {
                socket = null;
            }
            callSearchClient();
        } else {
            callConnectHost();
        }
        Log.e("wwh", "SoccerActivity-->onClose(): 断开连接");
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case SEARCH_CLIENT:
                try {
                    socket = serverSocket.accept();
                    socketThread = new SocketThread(socket, this);
                    socketThread.start();
                    command.showMainPanel(false);
                    isConnected = true;
                    dataBinding.gameView.setConnect(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case CONNECT_SERVE:
                if (socket == null) {
                    socket = new Socket();
                }
                try {
                    socket.connect(new InetSocketAddress(hostName, 1250), 10000);
                    socketThread = new SocketThread(socket, this);
                    socketThread.start();
                    command.showMainPanel(false);
                    isConnected = true;
                    dataBinding.gameView.setConnect(true);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("wwh", "SoccerActivity --> onInit: " + e.getMessage());
                    socket = null;
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    callConnectHost();
                }
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        isHost = v.getId() == R.id.host;
        dataBinding.gameView.setHost(isHost);
        if (isHost) {
            command.showButtons(false);
            hostName = NetworkUtil.getIpAddr(this);
            try {
                serverSocket = new ServerSocket(1250, 50, Inet4Address.getByName(hostName));
            } catch (IOException e) {
                e.printStackTrace();
                finish();
                Log.e("wwh", "SoccerActivity --> onInit: error" + e);
            }
            if (serverSocket != null) {
                callSearchClient();
            }
        } else {
            dialog.show();
        }
    }
}
