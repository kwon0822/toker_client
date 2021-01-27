package com.example.toker.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toker.R;
import com.example.toker.view.Item.ItemChat;

import java.util.List;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.ViewHolder> implements OnItemClickListner_Chat {

    private List<ItemChat> chatList;
    OnItemClickListner_Chat onItemClickListner_Chat;

    // 어댑터 생성자
    public AdapterChat(List<ItemChat> chatList) {
        this.chatList = chatList;
    }

    // 아이템 갯수 가져오기
    @Override
    public int getItemCount() {
        return chatList.size();
    }

    // 아이템 타입 가져오기
    @Override
    public int getItemViewType(int position) {
        return chatList.get(position).getType();
    }

    // 아이템을 레이아웃으로 전환하기
    @NonNull
    @Override
    public AdapterChat.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = -1;
        
        // 유형에 따라 다른 아이템으로
        switch (viewType) {
            case ItemChat.TYPE_Notice:
                layout = R.layout.item_chat_notice;
                break;
            case ItemChat.TYPE_MY_MSG:
                layout = R.layout.item_chat_my_msg;
                break;
            case ItemChat.TYPE_YOUR_MSG:
                layout = R.layout.item_chat_your_msg;
                break;
            case ItemChat.TYPE_TYPING:
                layout = R.layout.item_chat_type;
                break;
            case ItemChat.TYPE_SEND_MSG:
                layout = R.layout.item_chat_send_msg;
                break;
        }
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    // 레이아웃과 뷰 연결하기
    public class ViewHolder extends  RecyclerView.ViewHolder {

        private TextView item_chat_textview_msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_chat_textview_msg = itemView.findViewById(R.id.item_chat_textview_msg);

            item_chat_textview_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (onItemClickListner_Chat != null) {
                        onItemClickListner_Chat.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });
        }
    }

    // 뷰와 기능 연결하기
    @Override
    public void onBindViewHolder(@NonNull AdapterChat.ViewHolder holder, int position) {
        String message = chatList.get(position).getMessage();

        holder.item_chat_textview_msg.setText(message);
    }

    public ItemChat getItem(int position) {
        return chatList.get(position);
    }

    public void setOnItemClicklistener(OnItemClickListner_Chat listener) {
        this.onItemClickListner_Chat = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if(onItemClickListner_Chat != null) {
            onItemClickListner_Chat.onItemClick(holder,view,position);
        }
    }
}
