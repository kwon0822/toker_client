package com.example.toker.view.listner;

import android.view.View;

import com.example.toker.view.adapter.AdapterMessage;
import com.example.toker.view.adapter.AdapterPost;

public interface OnItemClickListnerPost {
    public void onItemClick(AdapterPost.ViewHolder holder, View view, int position);
}
