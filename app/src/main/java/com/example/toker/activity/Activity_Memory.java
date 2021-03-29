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

    String user = Activity_Main.user1;

    Dialog alertDialog;
    Dialog inputDialog;

    AdapterMemory memoryAdapter;
    List<ItemMemory> memoryList = new ArrayList<>();

    Gson gson = new GsonBuilder().setLenient().create();
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(RetrofitAPI.url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
    RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

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
                        Intent intent = new Intent(getApplicationContext(), Activity_Reminisce.class);
                        intent.putExtra("no", itemMemory.getNo());
                        intent.putExtra("title", itemMemory.getTitle());
                        startActivity(intent);
                        break;

                    case "delete" :
                        alertDialog = new Dialog(Activity_Memory.this);
                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        alertDialog.setContentView(R.layout.dialog_alert);
                        TextView popup_alert_textview_title = alertDialog.findViewById(R.id.dialog_alert_textview_title);
                        popup_alert_textview_title.setText("정말 삭제하시겠습니까?");
                        TextView popup_alert_textview_subtitle = alertDialog.findViewById(R.id.dialog_alert_textview_description);
                        popup_alert_textview_subtitle.setText("다시 복구 못해 임마!");
                        TextView popup_alert_button_yes = alertDialog.findViewById(R.id.dialog_alert_button_yes);
                        popup_alert_button_yes.setText("네");
                        TextView popup_alert_button_no = alertDialog.findViewById(R.id.dialog_alert_button_no);
                        popup_alert_button_no.setText("아니오");

                        popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                retrofitAPI.PostChatOff(itemMemory.getNo()).enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (response.body().equals("chatOff")) {
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
                        inputDialog = new Dialog(Activity_Memory.this);
                        inputDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        inputDialog.setContentView(R.layout.dialog_input);

                        EditText dialog_input_edittext_description = inputDialog.findViewById(R.id.dialog_input_edittext_description);
                        dialog_input_edittext_description.setHint(R.string.dialog_input_memory_editTitle_editText);
                        TextView dialog_input_textview_description = inputDialog.findViewById(R.id.dialog_input_textview_description);
                        dialog_input_textview_description.setText(R.string.dialog_input_memory_editTitle_textView);
                        Button dialog_input_button_send = inputDialog.findViewById(R.id.dialog_input_button_send);
                        dialog_input_button_send.setText(R.string.dialog_input_memory_editTitle_button);

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
                                alertDialog = new Dialog(Activity_Memory.this);
                                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                alertDialog.setContentView(R.layout.dialog_alert);

                                Button popup_alert_button_yes = alertDialog.findViewById(R.id.dialog_alert_button_yes);
                                popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        EditText popup_input_edittext_description = inputDialog.findViewById(R.id.dialog_input_edittext_description);
                                        String description = popup_input_edittext_description.getText().toString();

                                        retrofitAPI.PostChatEdit(itemMemory.getNo(), description).enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {

                                                if (response.body().equals("success")) {
                                                    alertDialog.dismiss();
                                                    inputDialog.dismiss();

                                                    memoryAdapter.getItem(position).setTitle(description);
                                                    memoryAdapter.notifyItemChanged(position);
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
                        break;
                }
            }
        });

        retrofitAPI.PostChatOn(user).enqueue(new Callback<List<ItemMemory>>() {
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
