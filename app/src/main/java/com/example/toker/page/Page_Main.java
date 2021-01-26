package com.example.toker.page;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toker.R;
import com.example.toker.http.RetrofitAPI;
import com.example.toker.view.adapter.AdapterChat;
import com.example.toker.view.adapter.AdapterRead;
import com.example.toker.view.adapter.AdapterMsg;
import com.example.toker.view.Item.ItemRead;
import com.example.toker.view.Item.ItemChat;
import com.example.toker.view.Item.ItemMsg;
import com.example.toker.view.adapter.OnItemClickListner_Chat_Title;
import com.example.toker.view.adapter.OnItemClickListner_Msg;
import com.example.toker.tcp.SocketAPI;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Page_Main extends AppCompatActivity {

    Toolbar page_main_toolbar;
    ActionBar page_main_actionbar;

    Button page_chat_button_notice;
    Button page_main_button_filter;
    Button page_main_button_message;
    Button page_main_button_memory;

    TextView page_main_textview_connectingNum;
    TextView page_main_textview_waitingNum;
    TextView page_main_textview_noticeBar;

    Dialog alertDialog;
    Dialog inputDialog;

    private Socket socket;

    private boolean isLogin = false;

    String id = Page_Login.myID;

    AdapterChat chatAdapter;
    AdapterRead chatTitleAdapter;
    List<ItemChat> chatTitleList = new ArrayList<>();
    List<ItemRead> chatList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_main);

        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        socket.connect();

        if (!isLogin) {
            socket.emit("login", id);
            isLogin = true;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
        socket.close();
    }

    private void initialize() {

        SocketAPI socketAPI = (SocketAPI) getApplication();
        socket = socketAPI.getSocket();

        page_main_toolbar = findViewById(R.id.page_main_toolbar);
        setSupportActionBar(page_main_toolbar);
        page_main_actionbar = getSupportActionBar();
        page_main_actionbar.setDisplayShowCustomEnabled(true);
        page_main_actionbar.setDisplayShowTitleEnabled(false);
        page_main_actionbar.setDisplayHomeAsUpEnabled(false);

        page_chat_button_notice = findViewById(R.id.page_main_button_notice);
        page_chat_button_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogNotice();
            }
        });

        page_main_button_filter = findViewById(R.id.page_main_button_filter);
        page_main_button_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Page_Filter.class);
                startActivity(intent);
            }
        });

        page_main_button_message = findViewById(R.id.page_main_button_message);
        page_main_button_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Page_Message.class);
                startActivity(intent);
            }
        });

        page_main_button_memory = findViewById(R.id.page_main_button_memory);
        page_main_button_memory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogMemory();
            }
        });
    }

    // region Listner ==============================================================================
    // endregion listner  ==========================================================================

    // region Dialog ================================================================================

    public void showDialogNotice() {
        Dialog popup_posting_list = new Dialog(Page_Main.this);
        popup_posting_list.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popup_posting_list.setContentView(R.layout.page_posting);
        popup_posting_list.show();

        Button popup_posting_list_button_back = popup_posting_list.findViewById(R.id.popup_posting_list_button_back);
        popup_posting_list_button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_posting_list.dismiss();
            }
        });

        Button popup_posting_list_button_request = popup_posting_list.findViewById(R.id.popup_posting_list_button_request);
        popup_posting_list_button_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogRequest();
            }
        });
    }

    public void showDialogRequest() {
        inputDialog = new Dialog(Page_Main.this);
        inputDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        inputDialog.setContentView(R.layout.popup_input);

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
                alertDialog = new Dialog(Page_Main.this);
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(R.layout.popup_alert);

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

    public void showDialogMemory() {

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitAPI.url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        retrofitAPI.PostChatOn(id).enqueue(new Callback<List<ItemChat>>() {
            @Override
            public void onResponse(Call<List<ItemChat>> call, Response<List<ItemChat>> response) {
                chatTitleList = response.body();

                Dialog popup_repository_chat = new Dialog(Page_Main.this);
                popup_repository_chat.requestWindowFeature(Window.FEATURE_NO_TITLE);
                popup_repository_chat.setContentView(R.layout.page_memory);

                Button popup_repository_chat_button_back = popup_repository_chat.findViewById(R.id.popup_repository_chat_button_back);
                popup_repository_chat_button_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup_repository_chat.dismiss();
                    }
                });

                RecyclerView popup_repository_chat_recyclerview_title = popup_repository_chat.findViewById(R.id.popup_repository_chat_recyclerview_title);
                popup_repository_chat_recyclerview_title.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                chatTitleAdapter = new AdapterRead(chatTitleList);
                popup_repository_chat_recyclerview_title.setAdapter(chatTitleAdapter);
                chatTitleAdapter.setOnItemClicklistener(new OnItemClickListner_Chat_Title() {
                    @Override
                    public void onItemClick(AdapterRead.ViewHolder holder, View view, int position, String button) {
                        ItemChat itemChat = chatTitleAdapter.getItem(position);
                        switch(button) {
                            case "item" :
                                Gson gson = new GsonBuilder().setLenient().create();
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(RetrofitAPI.url)
                                        .addConverterFactory(GsonConverterFactory.create(gson))
                                        .build();
                                RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
                                retrofitAPI.PostRead(itemChat.getNo()).enqueue(new Callback<List<ItemRead>>() {
                                    @Override
                                    public void onResponse(Call<List<ItemRead>> call, Response<List<ItemRead>> response) {

                                        chatList = response.body();
                                        Dialog page_chat = new Dialog(Page_Main.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
                                        page_chat.setContentView(R.layout.page_chat);

                                        Toolbar page_chat_toolbar;
                                        ActionBar page_chat_actionbar;

                                        page_chat_toolbar = page_chat.findViewById(R.id.page_chat_toolbar);
                                        setSupportActionBar(page_chat_toolbar);
                                        page_chat_actionbar = getSupportActionBar();
                                        page_chat_actionbar.setDisplayShowCustomEnabled(true);
                                        page_chat_actionbar.setDisplayShowTitleEnabled(false);
                                        page_chat_actionbar.setDisplayHomeAsUpEnabled(true);

                                        RecyclerView page_chat_recyclerview_chat = page_chat.findViewById(R.id.page_chat_recyclerview_chat);
                                        page_chat_recyclerview_chat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                        chatAdapter = new AdapterChat(chatList);
                                        page_chat_recyclerview_chat.setAdapter(chatAdapter);

                                        page_chat.show();
                                    }
                                    @Override
                                    public void onFailure(Call<List<ItemRead>> call, Throwable t) {
                                        System.out.println(t.getMessage());
                                    }
                                });

                                break;

                            case "delete" :
                                alertDialog = new Dialog(Page_Main.this);
                                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                alertDialog.setContentView(R.layout.popup_alert);
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
                                        retrofitAPI.PostChatOff(id, itemChat.getNo()).enqueue(new Callback<String>() {
                                            @Override
                                            public void onResponse(Call<String> call, Response<String> response) {
                                                if (response.body().equals("chatOffTitle")) {
                                                    chatTitleList.remove(position);
                                                    chatTitleAdapter.notifyItemRemoved(position);
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
                                break;
                            case "edit" :
                                Toast.makeText(getApplicationContext(), "수정", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
                popup_repository_chat.show();
            }
            @Override
            public void onFailure(Call<List<ItemChat>> call, Throwable t) {
            }
        });
    }

    // endregion ===================================================================================

    // region ToolBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.page_main_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.page_main_toolbar_menu_mylevel:
                Intent intent_menu_mylevel = new Intent(getApplicationContext(), Page_Level.class);
                startActivity(intent_menu_mylevel);
                break;
            case R.id.page_main_toolbar_menu_logout:
                Toast.makeText(getApplicationContext(), "로그아웃", Toast.LENGTH_SHORT).show();;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // endregion =================================================================================
}