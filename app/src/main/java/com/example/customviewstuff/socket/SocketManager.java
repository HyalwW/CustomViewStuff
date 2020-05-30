package com.example.customviewstuff.socket;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketManager implements Handler.Callback, SocketThread.Listener {
    private ServerSocket serverSocket;
    private Socket socket;
    private IMessage messager;
    private String hostName;
    private HandlerThread searchThread;
    private Handler searchHandler;
    private static final int SEARCH_CLIENT = 207, CONNECT_SERVE = 208;
    private boolean isConnected;
    private SocketListener listener;
    private TYPE type;

    public SocketManager() {
        searchThread = new HandlerThread("searchThread");
        searchThread.start();
        searchHandler = new Handler(searchThread.getLooper(), this);
    }

    public void searchClient(Context context) {
        if (type == null) {
            type = TYPE.SERVICE;
        } else if (type != TYPE.SERVICE) {
            if (listener != null) {
                listener.onConnectFailed("客户端无法搜索客户端！");
            }
            return;
        }
        hostName = NetworkUtil.getIpAddr(context);
        try {
            serverSocket = new ServerSocket(1250, 50, Inet4Address.getByName(hostName));
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(e);
            }
        }
        if (serverSocket != null) {
            connect(SEARCH_CLIENT);
        }
    }

    public void searchService(String ip) {
        if (type == null) {
            type = TYPE.CLIENT;
        } else if (type != TYPE.CLIENT) {
            if (listener != null) {
                listener.onConnectFailed("服务器无法搜索服务器！");
            }
            return;
        }
        hostName = ip;
        connect(CONNECT_SERVE);
    }

    public void setListener(SocketListener listener) {
        this.listener = listener;
    }

    public void sendMessage(String msg) {
        if (isConnected && messager != null) {
            messager.send(msg);
        }
    }

    public void reset() {
        if (type != null) {
            switch (type) {
                case SERVICE:
                    if (isConnected) {
                        try {
                            serverSocket.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    serverSocket = null;
                    socket = null;
                    break;
                case CLIENT:
                    if (isConnected) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    socket = null;
                    break;
            }
            messager = null;
            type = null;
        }
    }

    public void destroy() {
        setListener(null);
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

    private void connect(int what) {
        if (!isConnected) {
            if (listener != null) {
                listener.onStartConnect();
            }
            Message message = searchHandler.obtainMessage();
            message.what = what;
            message.sendToTarget();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case SEARCH_CLIENT:
                try {
                    socket = serverSocket.accept();
                    SocketThread messager = new SocketThread(socket, this);
                    this.messager = messager;
                    messager.start();
                    if (listener != null) {
                        listener.onConnectSuccess(true, socket);
                    }
                    isConnected = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onError(e);
                        listener.onConnectFailed(e.getMessage());
                    }
                }
                break;
            case CONNECT_SERVE:
                if (socket == null) {
                    socket = new Socket();
                }
                try {
                    socket.connect(new InetSocketAddress(hostName, 1250), 10000);
                    SocketThread messager = new SocketThread(socket, this);
                    this.messager = messager;
                    messager.start();
                    if (listener != null) {
                        listener.onConnectSuccess(false, socket);
                    }
                    isConnected = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("wwh", "SoccerActivity --> onInit: " + e.getMessage());
                    socket = null;
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    connect(CONNECT_SERVE);
                }
                break;
        }
        return true;
    }

    @Override
    public void onReceiveMsg(String msg) {
        if (listener != null) {
            listener.onReceiveMsg(msg);
        }
    }

    @Override
    public void onClose() {
        isConnected = false;
        if (socket != null) {
            socket = null;
        }
        if (listener != null) {
            listener.onDisconnect();
        }
    }

    public String getIpName() {
        return hostName;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public enum TYPE {
        SERVICE, CLIENT
    }
}
