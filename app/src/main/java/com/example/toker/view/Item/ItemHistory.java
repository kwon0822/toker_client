package com.example.toker.view.Item;

public class ItemHistory {

    String date;
    int time;
    String state;

    public ItemHistory(String date, int time, String state) {
        this.date = date;
        this.time = time;
        this.state = state;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
