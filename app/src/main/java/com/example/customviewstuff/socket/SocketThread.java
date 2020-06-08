package com.example.customviewstuff.socket;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Wang.Wenhui
 * Date: 2020/5/8
 */
class SocketThread extends Thread implements IMessage {
    private Socket socket;
    private BufferedReader br;
    private OutputStream os;
    private Listener listener;
    private ExecutorService threadPool;

    SocketThread(Socket socket, Listener in) throws IOException {
        super("socketThread");
        this.socket = socket;
        this.listener = in;
        br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        os = socket.getOutputStream();
    }

    @Override
    public void run() {
        super.run();
        String read;
        while (!socket.isClosed() && socket.isConnected() && (read = read()) != null) {
            if (listener != null) {
                listener.onReceiveMsg(socket.getInetAddress().getHostName(), read);
            }
        }
        if (listener != null) {
            listener.onClose(socket.getInetAddress().getHostName(), socket);
            listener = null;
        }
    }

    @Override
    public void send(String msg) {
        if (threadPool == null) {
            threadPool = Executors.newCachedThreadPool();
        }
        String finalMsg = msg + "\n";
        threadPool.execute(() -> sendSocketMsg(finalMsg));
    }

    @Override
    public String read() {
        try {
            return br.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("wwh", "SocketThread --> read: fail" + e);
        }
        return null;
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendSocketMsg(String msg) {
        try {
            os.write(msg.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("wwh", "SocketThread --> send: fail" + e);
        }
    }

    interface Listener {
        void onReceiveMsg(String address, String msg);

        void onClose(String address, Socket socket);
    }
}
