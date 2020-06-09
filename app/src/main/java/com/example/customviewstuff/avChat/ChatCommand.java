package com.example.customviewstuff.avChat;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.text.TextUtils;

import com.example.customviewstuff.helpers.BindingUtil;

/**
 * Created by Wang.Wenhui
 * Date: 2020/6/9
 * Description: blablabla
 */
public class ChatCommand {
    private ObservableInt userCount;
    private ObservableBoolean showJoinPanel, joiningShow;
    private ObservableField<String> ip, sendText, roomName;
    private BindingUtil.OnClickCommand onCreateCommand, showIpCommand;
    private BindingUtil.OnClickCommand<String> onClickCommand, onSendCommand;

    public ChatCommand(CommandListener listener) {
        this.listener = listener;
        userCount = new ObservableInt(1);
        showJoinPanel = new ObservableBoolean(true);
        joiningShow = new ObservableBoolean(true);
        onClickCommand = (view, data) -> {
            if (!TextUtils.isEmpty(data)) {
                listener.onJoin(data);
                showJoinPanel.set(false);
            }
        };
        onCreateCommand = (view, data) -> {
            listener.onCreate();
            showJoinPanel.set(false);
        };
        onSendCommand = (view, data) -> {
            if (!TextUtils.isEmpty(data)) {
                listener.onSend(data);
                sendText.set("");
            }
        };
        ip = new ObservableField<>("");
        sendText = new ObservableField<>("");
        roomName = new ObservableField<>("轻聊");
        showIpCommand = (view, data) -> listener.onShowIp();
    }

    public ObservableInt getUserCount() {
        return userCount;
    }

    public ObservableBoolean getShowJoinPanel() {
        return showJoinPanel;
    }

    public BindingUtil.OnClickCommand getOnCreateCommand() {
        return onCreateCommand;
    }

    public BindingUtil.OnClickCommand<String> getOnClickCommand() {
        return onClickCommand;
    }

    public ObservableField<String> getIp() {
        return ip;
    }

    public ObservableField<String> getSendText() {
        return sendText;
    }

    public ObservableField<String> getRoomName() {
        return roomName;
    }

    public void setUserCount(int userCount) {
        this.userCount.set(userCount);
    }

    public void setRoomName(String roomName) {
        this.roomName.set(roomName);
    }

    public BindingUtil.OnClickCommand<String> getOnSendCommand() {
        return onSendCommand;
    }

    public ObservableBoolean getJoiningShow() {
        return joiningShow;
    }

    public BindingUtil.OnClickCommand getShowIpCommand() {
        return showIpCommand;
    }

    private CommandListener listener;

    public void join() {
        userCount.set(userCount.get() + 1);
    }

    public String getRoomText(String roomName, int userCount) {
        return roomName + "(" + userCount + ")";
    }

    public void quit() {
        userCount.set(userCount.get() - 1);
    }

    public void showJoinPanel(boolean show) {
        showJoinPanel.set(show);
    }

    public void showJoining(boolean show) {
        joiningShow.set(show);
    }

    //暂用接口，若使用ViewModel使用liveData回调
    public interface CommandListener {
        void onCreate();

        void onJoin(String ip);

        void onSend(String msg);

        void onShowIp();

        void onChangeName(String name);
    }
}
