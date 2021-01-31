package com.example.toker.view.Item;

import com.google.gson.annotations.SerializedName;

public class ItemMemory {

    @SerializedName("no")
    String no;

    @SerializedName("title")
    String title;

    public ItemMemory(String no, String title) {
        this.no = no;
        this.title = title;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
