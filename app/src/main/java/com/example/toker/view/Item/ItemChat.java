package com.example.toker.view.Item;

public class ItemRead {

    public static final int TYPE_Notice = 0;
    public static final int TYPE_MY_MSG = 1;
    public static final int TYPE_YOUR_MSG = 2;
    public static final int TYPE_TYPING = 3;
    public static final int TYPE_SEND_MSG = 4;

    private int type;
    private String message;


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ItemRead(int type, String message) {
        this.type = type;
        this.message = message;
    }
}
