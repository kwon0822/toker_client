package com.example.toker.view.listner;

import android.view.View;

import com.example.toker.view.adapter.AdapterMemory;

public interface OnItemClickListnerMemory {
    public void onItemClick(AdapterMemory.ViewHolder holder, View view, int position, String button);
}
