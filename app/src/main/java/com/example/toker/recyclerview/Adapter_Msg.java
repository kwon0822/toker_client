package com.example.toker.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toker.R;

import java.util.List;

public class Adapter_Msg extends RecyclerView.Adapter<Adapter_Msg.ViewHolder> implements OnItemClickListner_Msg {

    private List<Item_Msg> msgList;
    OnItemClickListner_Msg onItemClickListner_msg;

    // 어댑터 생성자
    public Adapter_Msg(List<Item_Msg> msgList) {
        this.msgList = msgList;
    }

    // 아이템 갯수 가져오기
    @Override
    public int getItemCount() {
        return msgList.size();
    }


    // 아이템을 레이아웃으로 전환하기
    @NonNull
    @Override
    public Adapter_Msg.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = R.layout.item_msg;

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new ViewHolder(view);
    }

    // 레이아웃과 뷰 연결하기
    public class ViewHolder extends  RecyclerView.ViewHolder {

        private TextView item_msg_textview_contents;
        private Button item_msg_button_remove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_msg_textview_contents = itemView.findViewById(R.id.item_msg_textview_contetns);
            item_msg_button_remove = itemView.findViewById(R.id.item_msg_button_remove);

            item_msg_button_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (onItemClickListner_msg != null) {
                        onItemClickListner_msg.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });
        }
    }

    // 뷰와 기능 연결하기
    @Override
    public void onBindViewHolder(@NonNull Adapter_Msg.ViewHolder holder, int position) {
        String message = msgList.get(position).getContents();

        holder.item_msg_textview_contents.setText(message);
    }

    public Item_Msg getItem(int position) {
        return msgList.get(position);
    }

    public void setOnItemClicklistener(OnItemClickListner_Msg listener) {
        this.onItemClickListner_msg = listener;
    }

    @Override
    public void onItemClick(Adapter_Msg.ViewHolder holder, View view, int position) {
        if(onItemClickListner_msg != null) {
            onItemClickListner_msg.onItemClick(holder,view,position);
        }
    }

}
