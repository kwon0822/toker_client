package com.example.toker.view.listner;

import android.view.View;

import com.example.toker.view.adapter.AdapterMessage;

public interface OnItemClickListnerMessage {
    public void onItemClick(AdapterMessage.ViewHolder holder, View view, int position);
}
