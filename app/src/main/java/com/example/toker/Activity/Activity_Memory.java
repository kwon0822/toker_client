package com.example.toker.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toker.R;
import com.example.toker.http.RetrofitAPI;
import com.example.toker.view.Item.ItemMemory;
import com.example.toker.view.adapter.AdapterMemory;
import com.example.toker.view.listner.OnItemClickListnerMemory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Memory extends Activity {

    String id = Activity_Login.myID;

    Dialog alertDialog;
    Dialog inputDialog;

    AdapterMemory memoryAdapter;
    List<ItemMemory> memoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_memory);
        initialize();
    }

    public void initialize() {

        Button activity_memory_button_back = findViewById(R.id.activity_memory_button_back);
        activity_memory_button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RecyclerView activity_memory_recyclerview_title = findViewById(R.id.activity_memory_recyclerview_title);
        activity_memory_recyclerview_title.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        memoryAdapter = new AdapterMemory(memoryList);
        activity_memory_recyclerview_title.setAdapter(memoryAdapter);
        memoryAdapter.setOnItemClicklistener(new OnItemClickListnerMemory() {
            @Override
            public void onItemClick(AdapterMemory.ViewHolder holder, View view, int position, String button) {

                ItemMemory itemMemory = memoryAdapter.getItem(position);

                switch(button) {
                    case "item" :
                        Intent intent = new Intent(getApplicationContext(), Activity_ChatM.class);
                        intent.putExtra("no", itemMemory.getNo());
                        startActivity(intent);
                        break;

                    case "delete" :
                        alertDialog = new Dialog(Activity_Memory.this);
                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        alertDialog.setContentView(R.layout.dialog_alert);
                        TextView popup_alert_textview_title = alertDialog.findViewById(R.id.popup_alert_textview_title);
                        popup_alert_textview_title.setText("정말 삭제하시겠습니까?");
                        TextView popup_alert_textview_subtitle = alertDialog.findViewById(R.id.popup_alert_textview_description);
                        popup_alert_textview_subtitle.setText("다시 복구 못해 임마!");
                        TextView popup_alert_button_yes = alertDialog.findViewById(R.id.popup_alert_button_yes);
                        popup_alert_button_yes.setText("네");
                        TextView popup_alert_button_no = alertDialog.findViewById(R.id.popup_alert_button_no);
                        popup_alert_button_no.setText("아니오");

                        popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Gson gson = new GsonBuilder().setLenient().create();
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(RetrofitAPI.url)
                                        .addConverterFactory(GsonConverterFactory.create(gson))
                                        .build();
                                RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
                                retrofitAPI.PostChatOff(id, itemMemory.getNo()).enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (response.body().equals("chatOffTitle")) {
                                            memoryList.remove(position);
                                            memoryAdapter.notifyItemRemoved(position);
                                            alertDialog.dismiss();
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                    }
                                });
                            }
                        });
                        popup_alert_button_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
                        break;
                    case "edit" :
                        Toast.makeText(getApplicationContext(), "수정", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitAPI.url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        retrofitAPI.PostChatOn(id).enqueue(new Callback<List<ItemMemory>>() {
            @Override
            public void onResponse(Call<List<ItemMemory>> call, Response<List<ItemMemory>> response) {
                memoryList.addAll(response.body());
                memoryAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<List<ItemMemory>> call, Throwable t) {
            }
        });
    }

}
