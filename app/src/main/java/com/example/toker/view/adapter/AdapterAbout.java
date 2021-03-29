package com.example.toker.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toker.R;
import com.example.toker.view.Item.ItemNotice;
import com.example.toker.view.listner.OnItemClickListnerAbout;

import java.util.List;

public class AdapterAbout extends RecyclerView.Adapter<AdapterAbout.ViewHolder> implements OnItemClickListnerAbout {
    private List<ItemNotice> postList;
    OnItemClickListnerAbout onItemClickListnerAbout;

    // 어댑터 생성자
    public AdapterAbout(List<ItemNotice> postList) {
        this.postList = postList;
    }

    // 아이템 갯수 가져오기
    @Override
    public int getItemCount() {
        return postList.size();
    }


    // 아이템을 레이아웃으로 전환하기
    @NonNull
    @Override
    public AdapterAbout.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = R.layout.item_post;

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);

        return new ViewHolder(view);
    }

    // 레이아웃과 뷰 연결하기
    public class ViewHolder extends  RecyclerView.ViewHolder {

        private TextView item_post_textview_title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_post_textview_title = itemView.findViewById(R.id.item_post_textview_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (onItemClickListnerAbout != null) {
                        onItemClickListnerAbout.onItemClick(AdapterAbout.ViewHolder.this, v, position);
                    }
                }
            });
        }
    }

    // 뷰와 기능 연결하기
    @Override
    public void onBindViewHolder(@NonNull AdapterAbout.ViewHolder holder, int position) {

        String title = postList.get(position).getTitle();
        holder.item_post_textview_title.setText(title);
    }

    public ItemNotice getItem(int position) {
        return postList.get(position);
    }

    public void setOnItemClicklistener(OnItemClickListnerAbout listener) {
        this.onItemClickListnerAbout = listener;
    }

    @Override
    public void onItemClick(AdapterAbout.ViewHolder holder, View view, int position) {
        if(onItemClickListnerAbout != null) {
            onItemClickListnerAbout.onItemClick(holder,view,position);
        }
    }
}
