package com.example.toker.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toker.R;
import com.example.toker.http.RetrofitAPI;
import com.example.toker.view.Item.ItemHistory;
import com.example.toker.view.Item.ItemMessage;
import com.example.toker.view.Item.ItemPost;
import com.example.toker.view.adapter.AdapterHistory;
import com.example.toker.view.adapter.AdapterPost;
import com.example.toker.view.listner.OnItemClickListnerPost;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_About extends Activity {

    Dialog alertDialog;
    Dialog inputDialog;

    Button activity_about_button_back;
    Button activity_about_button_request;

    RecyclerView activity_post_recyclerview;
    AdapterPost postAdapter;
    List<ItemPost> postList = new ArrayList<>();
    Gson gson = new GsonBuilder().setLenient().create();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
        initialize();
    }

    public void initialize() {
        
        activity_about_button_back = findViewById(R.id.activity_about_button_back);
        activity_about_button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        activity_about_button_request = findViewById(R.id.activity_about_button_request);
        activity_about_button_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialog = new Dialog(Activity_About.this);
                inputDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                inputDialog.setContentView(R.layout.dialog_input);

                Button popup_input_button_back = inputDialog.findViewById(R.id.popup_input_button_back);
                popup_input_button_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputDialog.dismiss();
                    }
                });

                Button popup_input_button_send = inputDialog.findViewById(R.id.popup_input_button_send);
                popup_input_button_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog = new Dialog(Activity_About.this);
                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        alertDialog.setContentView(R.layout.dialog_alert);

                        Button popup_alert_button_yes = alertDialog.findViewById(R.id.popup_alert_button_yes);
                        popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "소중한 의견 전달되었습니다.", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                                inputDialog.dismiss();
                            }
                        });

                        Button popup_alert_button_no = alertDialog.findViewById(R.id.popup_alert_button_no);
                        popup_alert_button_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                });
                inputDialog.show();
            }
        });

        activity_post_recyclerview = findViewById(R.id.activity_about_recyclerview);
        activity_post_recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        postAdapter = new AdapterPost(postList);
        activity_post_recyclerview.setAdapter(postAdapter);
        postAdapter.setOnItemClicklistener(new OnItemClickListnerPost() {
            @Override
            public void onItemClick(AdapterPost.ViewHolder holder, View view, int position) {
                ItemPost itemPost = postAdapter.getItem(position);
                String postNo = itemPost.getNo();

                Intent intent = new Intent(getApplicationContext(), Activity_Post.class);
                intent.putExtra("postNo", postNo);
                startActivity(intent);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitAPI.url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        retrofitAPI.GetPost().enqueue(new Callback<List<ItemPost>>() {
            @Override
            public void onResponse(Call<List<ItemPost>> call, Response<List<ItemPost>> response) {
//                if (!response.isSuccessful()) {
//                    textViewResult.setText("code: " + response.code());
//                    return;
//                }

                postList.addAll(response.body());
                postAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<List<ItemPost>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }
}
