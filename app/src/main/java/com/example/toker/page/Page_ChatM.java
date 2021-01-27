package com.example.toker.page;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toker.R;
import com.example.toker.http.RetrofitAPI;
import com.example.toker.view.Item.ItemChat;
import com.example.toker.view.adapter.AdapterChat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Page_ChatM extends AppCompatActivity {


    AdapterChat chatAdapter;
    List<ItemChat> chatList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_chatm);
        initialize();
    }

    public void initialize() {

        Intent intent = getIntent();
        String ChatNo = intent.getExtras().getString("no");

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitAPI.url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        retrofitAPI.PostRead(ChatNo).enqueue(new Callback<List<ItemChat>>() {
            @Override
            public void onResponse(Call<List<ItemChat>> call, Response<List<ItemChat>> response) {

                chatList = response.body();

                Toolbar page_chat_toolbar;
                ActionBar page_chat_actionbar;

                page_chat_toolbar = findViewById(R.id.page_chat_toolbar);
                setSupportActionBar(page_chat_toolbar);
                page_chat_actionbar = getSupportActionBar();
                page_chat_actionbar.setDisplayShowCustomEnabled(true);
                page_chat_actionbar.setDisplayShowTitleEnabled(false);
                page_chat_actionbar.setDisplayHomeAsUpEnabled(true);

                RecyclerView page_chat_recyclerview_chat = findViewById(R.id.page_chat_recyclerview_chat);
                page_chat_recyclerview_chat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                chatAdapter = new AdapterChat(chatList);
                page_chat_recyclerview_chat.setAdapter(chatAdapter);
            }
            @Override
            public void onFailure(Call<List<ItemChat>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }
}
