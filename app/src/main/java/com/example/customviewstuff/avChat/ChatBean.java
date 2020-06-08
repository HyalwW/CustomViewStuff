package com.example.customviewstuff.avChat;

public class ChatBean {
    private int type;
    private String roomName;
    private String account;
    private String content;
    private String tip;
    private byte[] bitmap;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
    }


    public static final int CONTENT_IN = 1;
    public static final int CONTENT_OUT = 2;
    public static final int TIP = 3;
    public static final int IMAGE = 4;
}
