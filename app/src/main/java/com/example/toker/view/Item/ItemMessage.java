package com.example.toker.view.Item;

import com.google.gson.annotations.SerializedName;

public class ItemMessage {

    @SerializedName("no")
    String no;

    @SerializedName("description")
    String description;

    public ItemMessage(String no, String description) {
        this.no = no;
        this.description = description;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
