package com.example.customviewstuff.avChat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.example.customviewstuff.BaseActivity;
import com.example.customviewstuff.R;
import com.example.customviewstuff.databinding.ActivityChatRoomBinding;
import com.example.customviewstuff.socket.SocketListener;
import com.example.customviewstuff.socket.SocketManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatRoomActivity extends BaseActivity<ActivityChatRoomBinding> implements SocketListener {
    private SocketManager socketManager;
    private ChatAdapter adapter;
    private Gson mGson;
    private ChatCommand chatCommand;
    private boolean isConnect, isHost;
    private String account, address, hostAddress;
    private HashMap<String, String> users;
    private AlertDialog exit;
    private ShowIpDialog showIp;
    private long lastSendTime;

    @Override
    protected int layoutId() {
        return R.layout.activity_chat_room;
    }

    @Override
    protected void onInit() {
        socketManager = new SocketManager();
        socketManager.setListener(this);
        users = new HashMap<>();
        mGson = new Gson();
        adapter = new ChatAdapter(this, new ArrayList<>());
        dataBinding.chatContent.setAdapter(adapter);
        dataBinding.chatContent.setLayoutManager(new MyLinearLayoutManager(this));
        chatCommand = new ChatCommand(new ChatCommand.CommandListener() {
            @Override
            public void onCreate() {
                String acc = dataBinding.chatAccount.getText().toString();
                lastSendTime = System.currentTimeMillis();
                account = TextUtils.isEmpty(acc) ? "房主" : acc;
                isHost = true;
                socketManager.searchClient(ChatRoomActivity.this, false);
                chatCommand.showJoining(false);
                hostAddress = address = socketManager.getIpName();
            }

            @Override
            public void onJoin(String ip) {
                String acc = dataBinding.chatAccount.getText().toString();
                account = TextUtils.isEmpty(acc) ? "游客" + ((int) (Math.random() * 1000)) : acc;
                isHost = false;
                socketManager.searchService(ip);
            }

            @Override
            public void onSend(String msg) {
                send(ChatBean.CONTENT_IN, address, account, msg, "");
            }

            @Override
            public void onShowIp() {
                showIp.show(hostAddress);
            }

            @Override
            public void onChangeName(String name) {
                account = name;
            }
        });
        dataBinding.setCommand(chatCommand);

        exit = new AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage("退出聊天室？")
                .setPositiveButton("确定", (dialog, which) -> {
                    exit.dismiss();
                    finish();
                })
                .create();
        showIp = new ShowIpDialog(this);
        dataBinding.scanQr.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        dataBinding.scanQr.setOnClickListener(v -> startActivityForResult(new Intent(this, CaptureActivity.class), 207));
        ZXingLibrary.initDisplayOpinion(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 207) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        String res = bundle.getString(CodeUtils.RESULT_STRING);
                        String acc = dataBinding.chatAccount.getText().toString();
                        account = TextUtils.isEmpty(acc) ? "游客" + ((int) (Math.random() * 1000)) : acc;
                        isHost = false;
                        socketManager.searchService(res);
                        chatCommand.showJoinPanel(false);
                    } else {
                        Toast.makeText(this, "解析二维码失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public void onStartConnect() {

    }

    @Override
    public void onConnectSuccess(boolean isHost, String toAddress, String meAddress) {
        isConnect = true;
        this.isHost = isHost;
        address = meAddress;
        if (isHost) {
            chatCommand.join();
        } else {
            hostAddress = toAddress;
            chatCommand.showJoining(false);
            dataBinding.chatContainer.postDelayed(() -> send(ChatBean.TIP, address, account, "", "加入聊天室~"), 200);
        }
    }

    @Override
    public void onConnectFailed(String reason) {
        Log.e("wwh", "ChatRoomActivity --> onConnectFailed: " + reason);
        socketManager.reset();
        chatCommand.showJoinPanel(true);
    }

    @Override
    public void onDisconnect(String address) {
        isConnect = false;
        runOnUiThread(() -> {
            if (isHost) {
                String remove = users.remove(address);
                chatCommand.quit();
                if (!TextUtils.isEmpty(remove)) {
                    send(ChatBean.TIP, address, remove, "", "骂骂咧咧退出了群聊");
                }
            } else {
                adapter.clear();
                chatCommand.showJoinPanel(true);
                chatCommand.showJoining(true);
            }
        });
    }

    @Override
    public void onReceiveMsg(String address, String msg) {
        ChatBean chatBean = mGson.fromJson(msg, new TypeToken<ChatBean>() {
        }.getType());
        if (isHost) {
            send(chatBean.getType(), chatBean.getAddress(), chatBean.getAccount(), chatBean.getContent(), chatBean.getTip());
            if (chatBean.getType() == ChatBean.TIP) {
                users.put(chatBean.getAddress(), chatBean.getAccount());
            }
        } else {
            if (chatBean.getType() == ChatBean.CONTENT_IN && chatBean.getAddress().equals(this.address)) {
                chatBean.setType(ChatBean.CONTENT_OUT);
            }
            chatCommand.setRoomName(chatBean.getRoomName());
            chatCommand.setUserCount(chatBean.getUserCount());
            runOnUiThread(() -> {
                adapter.addBean(chatBean);
                dataBinding.chatContent.smoothScrollToPosition(adapter.getItemCount());
            });
        }
    }

    @Override
    public void onError(Throwable e) {
        socketManager.reset();
        chatCommand.showJoinPanel(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketManager.destroy();
    }

    private void send(int type, String address, String account, String content, String tip) {
        ChatBean bean = new ChatBean();
        bean.setType(type);
        bean.setAddress(address);
        bean.setAccount(account);
        bean.setContent(content);
        bean.setTime(System.currentTimeMillis());
        bean.setTip(tip);
        if (isHost) {
            bean.setRoomName(chatCommand.getRoomName().get());
            bean.setUserCount(chatCommand.getUserCount().get());
            long timeNow = System.currentTimeMillis();
            if (timeNow - lastSendTime > 1000 * 60 * 2) {
                lastSendTime = timeNow;
                send(ChatBean.TIME, this.address, account, "", "");
            }
        }
        String s = mGson.toJson(bean);
        Log.e("wwh", "ChatRoomActivity --> send: " + s);
        socketManager.sendMessage(s);
        if (isHost) {
            runOnUiThread(() -> {
                if (bean.getAddress().equals(ChatRoomActivity.this.address) && bean.getType() == ChatBean.CONTENT_IN)
                    bean.setType(ChatBean.CONTENT_OUT);
                adapter.addBean(bean);
                dataBinding.chatContent.smoothScrollToPosition(adapter.getItemCount());
            });
        }
    }


    public class MyLinearLayoutManager extends LinearLayoutManager {

        MyLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {

            LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                @Override
                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return 200f / displayMetrics.densityDpi;
                }
            };
            smoothScroller.setTargetPosition(position);
            startSmoothScroll(smoothScroller);
        }
    }

    @Override
    public void onBackPressed() {
        exit.show();
    }
}
