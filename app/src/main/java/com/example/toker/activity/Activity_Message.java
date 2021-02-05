package com.example.toker.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toker.R;
import com.example.toker.http.RetrofitAPI;
import com.example.toker.view.Item.ItemMessage;
import com.example.toker.view.adapter.AdapterMessage;
import com.example.toker.view.listner.OnItemClickListnerMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Message extends Activity {

    String id = Activity_Login.myID;

    Dialog alertDialog;
    Dialog inputDialog;

    AdapterMessage messageAdapter;
    List<ItemMessage> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_message);
        initialize();
    }

    public void initialize() {

        Button activity_message_button_back = findViewById(R.id.activity_message_button_back);
        activity_message_button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RecyclerView activity_message_recyclerview_contents = findViewById(R.id.activity_message_recyclerview_contents);
        activity_message_recyclerview_contents.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        messageAdapter = new AdapterMessage(messageList);
        activity_message_recyclerview_contents.setAdapter(messageAdapter);
        messageAdapter.setOnItemClicklistener(new OnItemClickListnerMessage() {
            @Override
            public void onItemClick(AdapterMessage.ViewHolder holder, View view, int position) {
                ItemMessage item_msg = messageAdapter.getItem(position);

                alertDialog = new Dialog(Activity_Message.this);
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(R.layout.dialog_alert);
                TextView popup_alert_textview_title = alertDialog.findViewById(R.id.popup_alert_textview_title);
                popup_alert_textview_title.setText("정말 삭제하시겠습니까?");
                TextView popup_alert_textview_subtitle = alertDialog.findViewById(R.id.popup_alert_textview_description);
                popup_alert_textview_subtitle.setText("다시 복구 못해 임마!");
                TextView popup_alert_button_yes = alertDialog.findViewById(R.id.popup_alert_button_yes);
                popup_alert_button_yes.setText("네");
                popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Gson gson = new GsonBuilder().setLenient().create();
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(RetrofitAPI.url)
                                .addConverterFactory(GsonConverterFactory.create(gson))
                                .build();
                        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
                        retrofitAPI.PostMessageOff(id, item_msg.getNo()).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                if (response.body().equals("msgOff")) {
                                    messageList.remove(position);
                                    messageAdapter.notifyItemRemoved(position);
                                    alertDialog.dismiss();
                                }
                            }
                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                            }
                        });
                    }
                });
                TextView popup_alert_button_no = alertDialog.findViewById(R.id.popup_alert_button_no);
                popup_alert_button_no.setText("아니오");
                popup_alert_button_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitAPI.url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        retrofitAPI.PostMessageOn(id).enqueue(new Callback<List<ItemMessage>>() {
            @Override
            public void onResponse(Call<List<ItemMessage>> call, Response<List<ItemMessage>> response) {
                messageList.addAll(response.body());
                messageAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<List<ItemMessage>> call, Throwable t) {
            }
        });
    }
}
