package com.example.toker.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toker.R;
import com.example.toker.http.RetrofitAPI;
import com.example.toker.view.adapter.AdapterHistory;
import com.example.toker.view.Item.ItemHistory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Level extends Activity {

    Button activity_level_button_back;
    TextView activity_level_textview_levelSystem;
    TextView activity_level_textview_level;
    TextView activity_level_textview_time;
    TextView activity_level_textview_chatData;

    RecyclerView activity_level_recyclerview;
    AdapterHistory historyAdapter;
    List<ItemHistory> historyList = new ArrayList<>();

    private String id = Activity_Login.myID;
    Gson gson = new GsonBuilder().setLenient().create();

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_level);
        initialize();
    }

    private void initialize() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitAPI.url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        retrofitAPI.PostLevel(id).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                int chatTime = Integer.parseInt(response.body().split("@")[0]);
                String chatTimeSTR = String.format("%01d시간 %02d분 %02d초",
                        TimeUnit.MILLISECONDS.toHours(chatTime),
                        TimeUnit.MILLISECONDS.toMinutes(chatTime)
                                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(chatTime)),
                        TimeUnit.MILLISECONDS.toSeconds(chatTime)
                                - TimeUnit.HOURS.toSeconds(TimeUnit.MILLISECONDS.toHours(chatTime))
                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(chatTime))
                );
                String chatLevel = response.body().split("@")[1];

                activity_level_textview_level = findViewById(R.id.activity_level_textview_level);
                activity_level_textview_level.setText(chatLevel);

                activity_level_textview_time = findViewById(R.id.activity_level_textview_time);
                activity_level_textview_time.setText("( 평균 대화시간 : " + chatTimeSTR + " )");
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                System.out.println(t.getMessage());
            }
       });

        activity_level_recyclerview = findViewById(R.id.activity_level_recyclerview);
        activity_level_recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        historyAdapter = new AdapterHistory(historyList);
        activity_level_recyclerview.setAdapter(historyAdapter);

        retrofitAPI.PostHistory(id).enqueue(new Callback<List<ItemHistory>>() {
            @Override
            public void onResponse(Call<List<ItemHistory>> call, Response<List<ItemHistory>> response) {
                historyList.addAll(response.body());
                historyAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<List<ItemHistory>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });

        activity_level_button_back = findViewById(R.id.activity_level_button_back);
        activity_level_button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
