package com.example.toker.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toker.R;
import com.example.toker.http.RetrofitAPI;
import com.example.toker.view.Item.ItemNotice;
import com.example.toker.view.adapter.AdapterAbout;
import com.example.toker.view.listner.OnItemClickListnerAbout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Notice extends Activity {

    Dialog alertDialog;
    Dialog inputDialog;

    Button activity_notice_button_back;
    Button activity_notice_button_request;

    RecyclerView activity_notice_recyclerview;
    AdapterAbout postAdapter;
    List<ItemNotice> postList = new ArrayList<>();

    Gson gson = new GsonBuilder().setLenient().create();
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(RetrofitAPI.url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
    RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_notice);
        initialize();
    }

    public void initialize() {

        activity_notice_button_back = findViewById(R.id.activity_notice_button_back);
        activity_notice_button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        activity_notice_button_request = findViewById(R.id.activity_notice_button_request);
        activity_notice_button_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputDialog = new Dialog(Activity_Notice.this);
                inputDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                inputDialog.setContentView(R.layout.dialog_input);

                EditText dialog_input_edittext_description = inputDialog.findViewById(R.id.dialog_input_edittext_description);
                dialog_input_edittext_description.setHint(R.string.dialog_input_notice_request_editText);
                TextView dialog_input_textview_description = inputDialog.findViewById(R.id.dialog_input_textview_description);
                dialog_input_textview_description.setText(R.string.dialog_input_notice_request_textView);
                Button dialog_input_button_send = inputDialog.findViewById(R.id.dialog_input_button_send);
                dialog_input_button_send.setText(R.string.dialog_input_notice_request_button);

                Button dialog_input_button_back = inputDialog.findViewById(R.id.dialog_input_button_back);
                dialog_input_button_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputDialog.dismiss();
                    }
                });

                dialog_input_button_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog = new Dialog(Activity_Notice.this);
                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        alertDialog.setContentView(R.layout.dialog_alert);

                        Button popup_alert_button_yes = alertDialog.findViewById(R.id.dialog_alert_button_yes);
                        popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditText popup_input_edittext_description = inputDialog.findViewById(R.id.dialog_input_edittext_description);
                                String description = popup_input_edittext_description.getText().toString();
                                String location = "notice";

                                retrofitAPI.PostRequest(Activity_Main.user1, description, location).enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {

                                        if (response.body().equals("success")) {
                                            Toast.makeText(getApplicationContext(), "소중한 의견 전달되었습니다.", Toast.LENGTH_SHORT).show();
                                            alertDialog.dismiss();
                                            inputDialog.dismiss();
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        System.out.println(t.getMessage());
                                    }
                                });
                            }
                        });

                        Button popup_alert_button_no = alertDialog.findViewById(R.id.dialog_alert_button_no);
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

        activity_notice_recyclerview = findViewById(R.id.activity_notice_recyclerview);
        activity_notice_recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        postAdapter = new AdapterAbout(postList);
        activity_notice_recyclerview.setAdapter(postAdapter);
        postAdapter.setOnItemClicklistener(new OnItemClickListnerAbout() {
            @Override
            public void onItemClick(AdapterAbout.ViewHolder holder, View view, int position) {
                ItemNotice itemNotice = postAdapter.getItem(position);
                String postNo = itemNotice.getNo();

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
        retrofitAPI.GetNotice().enqueue(new Callback<List<ItemNotice>>() {
            @Override
            public void onResponse(Call<List<ItemNotice>> call, Response<List<ItemNotice>> response) {
                if (!response.isSuccessful()) {

                }

                postList.addAll(response.body());
                postAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<List<ItemNotice>> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }
}
