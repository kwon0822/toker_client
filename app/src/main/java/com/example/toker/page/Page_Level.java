package com.example.toker.page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toker.R;
import com.example.toker.http.HTTP;
import com.example.toker.http.RetrofitAPI;
import com.example.toker.recyclerview.Adapter_Chat;
import com.example.toker.recyclerview.Adapter_Chat_History;
import com.example.toker.recyclerview.Item_Chat_History;
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

public class Page_Level extends AppCompatActivity {

    Button page_level_button_back;
    TextView page_level_textview_levelSystem;
    TextView page_level_textview_level;
    TextView page_level_textview_time;
    TextView page_level_textview_chatData;

    RecyclerView page_level_recyclerview;
    Adapter_Chat_History historyAdapter;
    List<Item_Chat_History> historyList = new ArrayList<>();

    private String id = Page_Login.myID;
    Gson gson = new GsonBuilder().setLenient().create();

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_level);
        initialize();
    }

    private void initialize() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HTTP.url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        retrofitAPI.postLevelOn(id).enqueue(new Callback<String>() {
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

                page_level_textview_level = findViewById(R.id.page_level_textview_level);
                page_level_textview_level.setText(chatLevel);

                page_level_textview_time = findViewById(R.id.page_level_textview_time);
                page_level_textview_time.setText("( 평균 대화시간 : " + chatTimeSTR + " )");
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                System.out.println(t.getMessage());
            }
       });

        retrofitAPI.postChatHistory(id).enqueue(new Callback<List<Item_Chat_History>>() {
            @Override
            public void onResponse(Call<List<Item_Chat_History>> call, Response<List<Item_Chat_History>> response) {
                historyList = response.body();

                page_level_recyclerview = findViewById(R.id.page_level_recyclerview);
                page_level_recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                historyAdapter = new Adapter_Chat_History(historyList);
                page_level_recyclerview.setAdapter(historyAdapter);
            }
            @Override
            public void onFailure(Call<List<Item_Chat_History>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });

        page_level_button_back = findViewById(R.id.page_level_button_back);
        page_level_button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Page_Main.class);
                startActivity(intent);
            }
        });
    }
}
