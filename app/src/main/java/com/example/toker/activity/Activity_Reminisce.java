package com.example.toker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

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

public class Activity_Reminisce extends AppCompatActivity {

    TextView activity_reminisce_textview_title;

    RecyclerView activity_chat_recyclerview_chat;
    private AdapterChat chatAdapter;
    private List<ItemChat> chatList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminisce);
        initialize();
    }

    public void initialize() {

        Intent intent = getIntent();
        String chatNo = intent.getExtras().getString("no");
        String chatTitle = intent.getExtras().getString("title");

        activity_reminisce_textview_title = findViewById(R.id.activity_reminisce_textview_title);
        activity_reminisce_textview_title.setText(chatTitle);

        activity_chat_recyclerview_chat = findViewById(R.id.activity_reminisce_recyclerview_chat);
        activity_chat_recyclerview_chat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        chatAdapter = new AdapterChat(chatList);
        activity_chat_recyclerview_chat.setAdapter(chatAdapter);

        Toolbar activity_chat_toolbar;
        ActionBar activity_chat_actionbar;

        activity_chat_toolbar = findViewById(R.id.activity_reminisce_toolbar);
        setSupportActionBar(activity_chat_toolbar);
        activity_chat_actionbar = getSupportActionBar();
        activity_chat_actionbar.setDisplayShowCustomEnabled(true);
        activity_chat_actionbar.setDisplayShowTitleEnabled(false);
        activity_chat_actionbar.setDisplayHomeAsUpEnabled(true);

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitAPI.url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        retrofitAPI.PostChat(chatNo).enqueue(new Callback<List<ItemChat>>() {
            @Override
            public void onResponse(Call<List<ItemChat>> call, Response<List<ItemChat>> response) {
                chatList.addAll(response.body());
                chatAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<List<ItemChat>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }
}
