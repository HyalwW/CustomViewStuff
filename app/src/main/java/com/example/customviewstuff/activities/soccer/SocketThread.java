package com.example.customviewstuff.activities.soccer;

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
public class SocketThread extends Thread implements IMessage {
    private Socket socket;
    private BufferedReader br;
    private OutputStream os;
    private SocketListener listener;
    private ExecutorService threadPool;

    public SocketThread(Socket socket, SocketListener in) throws IOException {
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
        while (socket.isConnected() && (read = read()) != null) {
            listener.onReceiveMsg(read);
        }
        listener.onClose();
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

    private void sendSocketMsg(String msg) {
        try {
            Log.e("wwh", "SocketThread --> handleMessage: " + msg);
            os.write(msg.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("wwh", "SocketThread --> send: fail" + e);
        }
    }
}
