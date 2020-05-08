package com.example.customviewstuff.activities;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.R;
import com.example.customviewstuff.activities.soccer.SocketListener;
import com.example.customviewstuff.activities.soccer.SocketThread;
import com.example.customviewstuff.databinding.ActivitySoccerBinding;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SoccerActivity extends BaseActivity<ActivitySoccerBinding> implements SocketListener, Handler.Callback {
    private boolean isHost, isConnected;
    private ServerSocket serverSocket;
    private Socket socket;
    private SocketThread socketThread;
    private static final String hostName = "192.168.43.1";
    private HandlerThread searchThread;
    private Handler searchHandler;

    @Override
    protected int layoutId() {
        return R.layout.activity_soccer;
    }

    @Override
    protected void onInit() {
        isHost = false;
        searchThread = new HandlerThread("searchThread");
        searchThread.start();
        searchHandler = new Handler(searchThread.getLooper(), this);
        if (isHost) {
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
            callConnectHost();
        }
        dataBinding.send.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(dataBinding.content.getText()) && isConnected) {
                socketThread.send(dataBinding.content.getText().toString());
            }
        });
    }

    private void callSearchClient() {
        send(207);
    }

    private void callConnectHost() {
        send(208);
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
        searchHandler.removeMessages(207);
        searchHandler.removeMessages(208);
        searchThread.quit();
    }

    @Override
    public void onReceiveMsg(String msg) {
        if (isHost) {
            dataBinding.text.post(() -> dataBinding.text.setText(dataBinding.text.getText() + "\nclient:" + msg));
        } else {
            dataBinding.text.post(() -> dataBinding.text.setText(dataBinding.text.getText() + "\nserve:" + msg));
        }
    }

    @Override
    public void onClose() {
        isConnected = false;
        socketThread = null;
        if (isHost) {
            if (socket != null) {
                socket = null;
            }
            callSearchClient();
        } else {
            callConnectHost();
        }
        dataBinding.text.post(() -> dataBinding.text.setText("断开连接"));
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 207:
                try {
                    dataBinding.text.post(() -> dataBinding.text.setText("等待设备。。。"));
                    socket = serverSocket.accept();
                    dataBinding.text.post(() -> dataBinding.text.setText("连接成功"));
                    socketThread = new SocketThread(socket, this);
                    socketThread.start();
                    isConnected = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 208:
                if (socket == null) {
                    socket = new Socket();
                }
                try {
                    socket.connect(new InetSocketAddress(hostName, 1250), 10000);
                    dataBinding.text.post(() -> dataBinding.text.setText("连接成功"));
                    socketThread = new SocketThread(socket, this);
                    socketThread.start();
                    isConnected = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("wwh", "SoccerActivity --> onInit: " + e.getMessage());
                    dataBinding.text.post(() -> dataBinding.text.setText("等待主机。。。"));
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
}
