package com.example.toker.view.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toker.R;
import com.example.toker.view.Item.ItemMessage;
import com.example.toker.view.listner.OnItemClickListnerMessage;

import java.util.List;

public class AdapterMessage extends RecyclerView.Adapter<AdapterMessage.ViewHolder> implements OnItemClickListnerMessage {

    private List<ItemMessage> messageList;
    OnItemClickListnerMessage onItemClickListnerMessage;

    public AdapterMessage(List<ItemMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @NonNull
    @Override
    public AdapterMessage.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = R.layout.item_message;

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        private TextView item_message_textview_title;
        private TextView item_message_textview_description;
        private Button item_message_button_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_message_textview_title = itemView.findViewById(R.id.item_message_textview_title);
            item_message_textview_description = itemView.findViewById(R.id.item_message_textview_description);
            item_message_button_delete = itemView.findViewById(R.id.item_message_button_delete);

            item_message_button_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (onItemClickListnerMessage != null) {
                        onItemClickListnerMessage.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull AdapterMessage.ViewHolder holder, int position) {
        String user1 = messageList.get(position).getUser1();
        String message = messageList.get(position).getDescription();

        if (user1.equals("admin")) {
            holder.item_message_textview_title.setText("개발자에게서 쪽지가 도착했어요!");
            holder.item_message_textview_title.setTextColor(R.color.positive);
        }
        holder.item_message_textview_description.setText(message);
    }

    public ItemMessage getItem(int position) {
        return messageList.get(position);
    }

    public void setOnItemClicklistener(OnItemClickListnerMessage listener) {
        this.onItemClickListnerMessage = listener;
    }

    @Override
    public void onItemClick(AdapterMessage.ViewHolder holder, View view, int position) {
        if(onItemClickListnerMessage != null) {
            onItemClickListnerMessage.onItemClick(holder,view,position);
        }
    }

}
