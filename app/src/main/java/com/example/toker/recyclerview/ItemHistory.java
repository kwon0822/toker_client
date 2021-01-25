package com.example.toker.recyclerview;

public class ItemHistory {

    String date;
    int time;

    public ItemHistory(String date, int time) {
        this.date = date;
        this.time = time;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
