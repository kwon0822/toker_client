package com.example.toker.view.listner;

import android.view.View;

import com.example.toker.view.adapter.AdapterChat;

public interface OnItemClickListnerChat {
    public void onItemClick(AdapterChat.ViewHolder holder, View view, int position);
}
