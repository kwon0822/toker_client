package com.example.toker.recyclerview;

import com.google.gson.annotations.SerializedName;

public class Item_Chat_Title {

    @SerializedName("no")
    String no;

    @SerializedName("title")
    String title;

    public Item_Chat_Title(String no, String title) {
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
