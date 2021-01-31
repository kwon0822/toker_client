package com.example.toker.view.Item;

import com.google.gson.annotations.SerializedName;

public class ItemMessage {

    @SerializedName("no")
    String no;

    @SerializedName("contents")
    String contents;

    public ItemMessage(String no, String contents) {
        this.no = no;
        this.contents = contents;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
