package com.example.toker.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toker.R;
import com.example.toker.view.Item.ItemMemory;
import com.example.toker.view.listner.OnItemClickListnerMemory;

import java.util.List;

public class AdapterMemory extends RecyclerView.Adapter<AdapterMemory.ViewHolder> implements OnItemClickListnerMemory {

    private List<ItemMemory> chatTitleList;
    OnItemClickListnerMemory onItemClickListnerMemory;

    // 어댑터 생성자
    public AdapterMemory(List<ItemMemory> chatTitleList) {
        this.chatTitleList = chatTitleList;
    }

    // 아이템 갯수 가져오기
    @Override
    public int getItemCount() {
        return chatTitleList.size();
    }


    // 아이템을 레이아웃으로 전환하기
    @NonNull
    @Override
    public AdapterMemory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = R.layout.item_memory;

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    // 레이아웃과 뷰 연결하기
    public class ViewHolder extends  RecyclerView.ViewHolder {

        private TextView item_chat_title_textview_title;
        private Button item_chat_title_button_delete;
        private Button item_msg_button_edit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_chat_title_textview_title = itemView.findViewById(R.id.item_chat_title_textview_title);
            item_chat_title_button_delete = itemView.findViewById(R.id.item_chat_title_button_delete);
            item_msg_button_edit= itemView.findViewById(R.id.item_chat_title_button_edit);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (onItemClickListnerMemory != null) {
                        onItemClickListnerMemory.onItemClick(ViewHolder.this, v, position, "item");
                    }
                }
            });

            item_chat_title_button_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (onItemClickListnerMemory != null) {
                        onItemClickListnerMemory.onItemClick(ViewHolder.this, v, position, "delete");
                    }
                }
            });

            item_msg_button_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (onItemClickListnerMemory != null) {
                        onItemClickListnerMemory.onItemClick(ViewHolder.this, v, position, "edit");
                    }
                }
            });
        }
    }

    // 뷰와 기능 연결하기
    @Override
    public void onBindViewHolder(@NonNull AdapterMemory.ViewHolder holder, int position) {
        String message = chatTitleList.get(position).getTitle();

        holder.item_chat_title_textview_title.setText(message);
    }

    public ItemMemory getItem(int position) {
        return chatTitleList.get(position);
    }

    public void setOnItemClicklistener(OnItemClickListnerMemory listener) {
        this.onItemClickListnerMemory = listener;
    }

    @Override
    public void onItemClick(AdapterMemory.ViewHolder holder, View view, int position, String button) {
        if(onItemClickListnerMemory != null) {
            onItemClickListnerMemory.onItemClick(holder,view,position, button);
        }
    }

}
