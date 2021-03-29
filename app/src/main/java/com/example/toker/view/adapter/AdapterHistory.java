package com.example.toker.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toker.R;
import com.example.toker.view.Item.ItemHistory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AdapterHistory extends RecyclerView.Adapter<AdapterHistory.ViewHolder> {
    private List<ItemHistory> historyList;

    // 어댑터 생성자
    public AdapterHistory(List<ItemHistory> historyList) {
        this.historyList = historyList;
    }

    // 아이템 갯수 가져오기
    @Override
    public int getItemCount() {
        return historyList.size();
    }


    // 아이템을 레이아웃으로 전환하기
    @NonNull
    @Override
    public AdapterHistory.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = R.layout.item_history;

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);

        return new ViewHolder(view);
    }

    // 레이아웃과 뷰 연결하기
    public class ViewHolder extends  RecyclerView.ViewHolder {

        private TextView item_history_textview_date;
        private TextView item_history_textview_time;
        private TextView item_history_textview_state;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_history_textview_date = itemView.findViewById(R.id.item_history_textview_date);
            item_history_textview_time = itemView.findViewById(R.id.item_history_textview_time);
            item_history_textview_state = itemView.findViewById(R.id.item_history_textview_state);
        }
    }

    // 뷰와 기능 연결하기
    @Override
    public void onBindViewHolder(@NonNull AdapterHistory.ViewHolder holder, int position) {

        // date
        String date = historyList.get(position).getDate();
        System.out.println(date + "@@@@@@@@@@@@@@@@@@@@@@@@");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");
        long dateLong = Long.parseLong(date);
        Date dateDate = new Date(dateLong);
        String dateSTR = simpleDateFormat.format(dateDate);
        holder.item_history_textview_date.setText(dateSTR);

        // time
        int time = historyList.get(position).getTime();
        System.out.println(time + "@@@@@@@@@@@@@@@@@@@@@@@@");
        String timeSTR = String.format("%02d분 %02d초",
                TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
        );
        holder.item_history_textview_time.setText(timeSTR + " 동안 채팅");

        // state
        try {
            String state = historyList.get(position).getState();
            System.out.println(state + "@@@@@@@@@@@@@@@@@@@@@@@@");
            String stateString = "";
            switch (state) {
                case "normal":
                    stateString = "정상대화";
                    break;
                case "abnormal":
                    stateString = "비정상종료";
                    break;
                case "accuse":
                    stateString = "상대신고";
                    break;
                case "block":
                    stateString = "상대차단";
                    break;
            }
            holder.item_history_textview_state.setText(stateString);

        } catch (NullPointerException e) {
            holder.item_history_textview_state.setText("내역없음");
        }
    }
}
