package com.example.customviewstuff.socket;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.ArrayMap;
import android.util.Log;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class SocketManager implements Handler.Callback, SocketThread.Listener {
    private ServerSocket serverSocket;
    private ArrayMap<String, IMessage> messages;
    private String hostName;
    private HandlerThread searchThread;
    private Handler searchHandler;
    private static final int SEARCH_CLIENT = 207, CONNECT_SERVE = 208;
    private boolean isConnected, searching, isSingle;
    private SocketListener listener;
    private TYPE type;

    public SocketManager() {
        searchThread = new HandlerThread("searchThread");
        searchThread.start();
        searchHandler = new Handler(searchThread.getLooper(), this);
        messages = new ArrayMap<>();
    }

    public void searchClient(Context context, boolean single) {
        if (type == null) {
            type = TYPE.SERVICE;
        } else if (type != TYPE.SERVICE) {
            if (listener != null) {
                listener.onConnectFailed("客户端无法搜索客户端！");
            }
            return;
        }
        this.isSingle = single;
        if (searching) return;
        searching = true;
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

    public void stopSearch() {
        searching = false;
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
        if (isConnected) {
            for (Map.Entry<String, IMessage> entry : messages.entrySet()) {
                entry.getValue().close();
            }
            messages.clear();
        }
        connect(CONNECT_SERVE);
    }

    public void setListener(SocketListener listener) {
        this.listener = listener;
    }

    public void sendMessage(String msg) {
        if (isConnected) {
            for (Map.Entry<String, IMessage> entry : messages.entrySet()) {
                entry.getValue().send(msg);
            }
        }
    }

    public void reset() {
        if (type != null) {
            switch (type) {
                case SERVICE:
                    if (isConnected) {
                        try {
                            serverSocket.close();
                            for (Map.Entry<String, IMessage> entry : messages.entrySet()) {
                                entry.getValue().close();
                            }
                            messages.clear();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    serverSocket = null;
                    break;
                case CLIENT:
                    if (isConnected) {
                        for (Map.Entry<String, IMessage> entry : messages.entrySet()) {
                            entry.getValue().close();
                        }
                        messages.clear();
                    }
                    break;
            }
            type = null;
        }
    }

    public void destroy() {
        isConnected = false;
        searching = false;
        setListener(null);
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, IMessage> entry : messages.entrySet()) {
            entry.getValue().close();
        }
        messages.clear();
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
        if (msg.what == SEARCH_CLIENT) {
            while (searching) {
                try {
                    Socket socket = serverSocket.accept();
                    SocketThread message = new SocketThread(socket, this);
                    message.start();
                    messages.put(socket.getInetAddress().getHostName(), message);
                    if (listener != null) {
                        listener.onConnectSuccess(true, socket.getInetAddress().getHostName());
                    }
                    isConnected = true;
                    if (isSingle) {
                        stopSearch();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onError(e);
                        listener.onConnectFailed(e.getMessage());
                    }
                }
            }
        } else if (msg.what == CONNECT_SERVE) {
            Socket socket;
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(hostName, 1250), 10000);
                SocketThread message = new SocketThread(socket, this);
                message.start();
                messages.put(socket.getInetAddress().getHostName(), message);
                if (listener != null) {
                    listener.onConnectSuccess(false, socket.getInetAddress().getHostName());
                }
                isConnected = true;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("wwh", "SoccerActivity --> onInit: " + e.getMessage());
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                connect(CONNECT_SERVE);
            }
        }
        return true;
    }

    @Override
    public void onReceiveMsg(String address, String msg) {
        if (listener != null) {
            listener.onReceiveMsg(address, msg);
        }
    }

    @Override
    public void onClose(String address, Socket socket) {
        isConnected = false;
        searching = false;
        IMessage remove = messages.remove(address);
        if (remove != null) {
            remove.close();
        }
        if (listener != null) {
            listener.onDisconnect(address);
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
