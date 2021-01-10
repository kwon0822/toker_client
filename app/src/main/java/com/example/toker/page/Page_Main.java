package com.example.toker.page;

import android.app.Dialog;
import android.content.Intent;
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
import com.example.toker.http.HTTP;
import com.example.toker.http.RetrofitAPI;
import com.example.toker.recyclerview.Adapter_Msg;
import com.example.toker.recyclerview.Item_Msg;
import com.example.toker.recyclerview.OnItemClickListner_Msg;
import com.example.toker.tcp.Application_Socket;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Page_Main extends AppCompatActivity {

    Toolbar page_main_toolbar;
    Button page_chat_button_communication;
    ActionBar page_main_actionbar;
    TextView page_main_textview_connectingNum;
    TextView page_main_textview_waitingNum;
    TextView page_main_textview_noticeBar;
    Button page_main_button_randomMatching;
    Button page_main_button_readMSG;
    Button page_main_button_readChat;

    Dialog popup_posting_list;
    Dialog popup_feedback_request;
    Dialog popup_alert_request;
    Dialog popup_repository_chat;
    Dialog popup_repository_msg;
    Dialog popup_matching_filter;
    Dialog popup_alert;

    private Socket socket;

    private boolean isLogin = false;
    // 대기자명단 관리 위함
    // true : 매치시작 버튼 누르면
    // false : (1)매치성사되었거나 (2)매치취소했거나 (3)매칭중앱강제종료하면
    private boolean isMatch = false;
    String id = Page_Login.myID;

    Adapter_Msg msgAdapter;
    List<Item_Msg> msgList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_main);

        initialize();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // 소켓 연결하기
        socket.on("matchOn", matchOn);
        socket.on("matchOff", matchOff);
        socket.connect();

        if (!isLogin) {
            socket.emit("login", id);
            isLogin = true;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        socket.off("matchOn", matchOn);
        socket.off("matchOff", matchOff);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isMatch) {
            socket.emit("matchOff");
            isMatch = false;
        }

        socket.disconnect();
        socket.close();
    }

    private void initialize() {

        // 소켓
        Application_Socket application_socket = (Application_Socket) getApplication();
        socket = application_socket.getSocket();

        // 툴바
        page_main_toolbar = findViewById(R.id.page_main_toolbar);
        setSupportActionBar(page_main_toolbar);
        page_main_actionbar = getSupportActionBar();
        page_main_actionbar.setDisplayShowCustomEnabled(true);
        page_main_actionbar.setDisplayShowTitleEnabled(false);
        page_main_actionbar.setDisplayHomeAsUpEnabled(false);

        // 팝업 : 개발자의 말
        popup_posting_list = new Dialog(Page_Main.this);
        popup_posting_list.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popup_posting_list.setContentView(R.layout.popup_posting_list);
        page_chat_button_communication = findViewById(R.id.page_main_button_communication);
        page_chat_button_communication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupPostingList();
            }
        });


        // 버튼 : 랜덤매칭
        popup_matching_filter = new Dialog(Page_Main.this);
        popup_matching_filter.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popup_matching_filter.setContentView(R.layout.popup_matching_filter);
        page_main_button_randomMatching = findViewById(R.id.page_main_button_randomMatching);
        page_main_button_randomMatching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMatchingFilter();
            }
        });

        // 팝업 : 쪽지읽기
        page_main_button_readMSG = findViewById(R.id.page_main_button_readMSG);
        page_main_button_readMSG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupRepositoryMSG();
            }
        });

        // 팝업 : 채팅보관
        popup_repository_chat = new Dialog(Page_Main.this);
        popup_repository_chat.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popup_repository_chat.setContentView(R.layout.popup_repository_chat);
        page_main_button_readChat = findViewById(R.id.page_main_button_readChat);
        page_main_button_readChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupRepositoryChat();
            }
        });
    }

    // socket listner : 'onMatchOn'
    private Emitter.Listener matchOn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String message = args[0].toString();

                    // 메세지 내용이 wait이면 토스트 띄우기
                    if (message.equals("wait")) {
                        Toast.makeText(getApplicationContext(), "matchOn : wait", Toast.LENGTH_SHORT).show();

                    // 메세지 내용이 wait이 아니면 상대 아이디 받고 채팅창으로 이동하기
                    } else {

                        Page_Login.yourID = message;

                        isMatch = false;
                        popup_matching_filter.dismiss();

                        Intent intent = new Intent(getApplicationContext(), Page_Chat.class);
                        startActivity(intent);
                    }
                }
            });
        }
    };

    // socket listner : 'onMatchOff'
    private Emitter.Listener matchOff = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(getApplicationContext(), "matchOff", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    // 툴바 : 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.page_main_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.page_main_toolbar_menu_myinfo:
                Intent intent_menu_myinfo = new Intent(getApplicationContext(), Page_Myinfo.class);
                startActivity(intent_menu_myinfo);
                break;
            case R.id.page_main_toolbar_menu_mylevel:
                Intent intent_menu_mylevel = new Intent(getApplicationContext(), Page_Mylevel.class);
                startActivity(intent_menu_mylevel);
                break;
            case R.id.page_main_toolbar_menu_logout:
                Toast.makeText(getApplicationContext(), "로그아웃", Toast.LENGTH_SHORT).show();;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 팝업 : '개발자의 말'
    public void showPopupPostingList() {
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
                popup_feedback_request = new Dialog(Page_Main.this);
                popup_feedback_request.requestWindowFeature(Window.FEATURE_NO_TITLE);
                popup_feedback_request.setContentView(R.layout.popup_feedback_request);
                showPopupFeedbackRequest();
            }
        });
    }

    // 팝업 : 의견보내기 contents
    public void showPopupFeedbackRequest() {
        popup_feedback_request.show();

        Button popup_feedback_request_button_back = popup_feedback_request.findViewById(R.id.popup_feedback_request_button_back);
        popup_feedback_request_button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_feedback_request.dismiss();
            }
        });

        Button popup_feedback_request_button_send = popup_feedback_request.findViewById(R.id.popup_feedback_request_button_send);
        popup_feedback_request_button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_alert_request = new Dialog(Page_Main.this);
                popup_alert_request.requestWindowFeature(Window.FEATURE_NO_TITLE);
                popup_alert_request.setContentView(R.layout.popup_alert_request);
                showPopupAlertRequest();
            }
        });
    }
    // 팝업 : 의견보내기 alert
    public void showPopupAlertRequest() {
        popup_alert_request.show();

        Button popup_alert_request_button_yes = popup_alert_request.findViewById(R.id.popup_alert_request_button_yes);
        popup_alert_request_button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "소중한 의견 전달되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        Button popup_alert_request_button_no = popup_alert_request.findViewById(R.id.popup_alert_request_button_no);
        popup_alert_request_button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_alert_request.dismiss();
            }
        });
    }

    // 팝업 : 랜덤매칭
    public void showPopupMatchingFilter() {
        popup_matching_filter.show();

        // button : 뒤로가기
        Button popup_matching_filter_button_back = popup_matching_filter.findViewById(R.id.popup_matching_filter_button_back);
        popup_matching_filter_button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // emit : 'matchOff', 매칭 취소하기
                if (isMatch) {
                    socket.emit("matchOff");
                    isMatch = false;
                }

                popup_matching_filter.dismiss();
            }
        });

        // button : 매칭시작
        Button popup_matching_filter_button_matchingStart = popup_matching_filter.findViewById(R.id.popup_matching_filter_button_matchingStart);
        popup_matching_filter_button_matchingStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 채팅 명단에 이름 올리기
                isMatch = true;
                socket.emit("matchOn");
            }
        });
    }

    // 팝업 : 쪽지읽기
    public void showPopupRepositoryMSG() {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HTTP.url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        retrofitAPI.postMsgOn(id).enqueue(new Callback<List<Item_Msg>>() {
            @Override
            public void onResponse(Call<List<Item_Msg>> call, Response<List<Item_Msg>> response) {
                msgList = response.body();

                popup_repository_msg = new Dialog(Page_Main.this);
                popup_repository_msg.requestWindowFeature(Window.FEATURE_NO_TITLE);
                popup_repository_msg.setContentView(R.layout.popup_repository_msg);

                Button popup_repository_msg_button_back = popup_repository_msg.findViewById(R.id.popup_repository_msg_button_back);
                popup_repository_msg_button_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup_repository_msg.dismiss();
                    }
                });

                RecyclerView popup_repository_msg_recyclerview_contents = popup_repository_msg.findViewById(R.id.popup_repository_chat_recyclerview_contents);
                popup_repository_msg_recyclerview_contents.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                msgAdapter = new Adapter_Msg(msgList);
                popup_repository_msg_recyclerview_contents.setAdapter(msgAdapter);
                msgAdapter.setOnItemClicklistener(new OnItemClickListner_Msg() {
                    @Override
                    public void onItemClick(Adapter_Msg.ViewHolder holder, View view, int position) {
                        Item_Msg item_msg = msgAdapter.getItem(position);

                        popup_alert = new Dialog(Page_Main.this);
                        popup_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        popup_alert.setContentView(R.layout.popup_alert);
                        TextView popup_alert_textview_title = popup_alert.findViewById(R.id.popup_alert_textview_title);
                        popup_alert_textview_title.setText("정말 삭제하시겠습니까?");
                        TextView popup_alert_textview_subtitle = popup_alert.findViewById(R.id.popup_alert_textview_subtitle);
                        popup_alert_textview_subtitle.setText("다시 복구 못해 임마!");
                        TextView popup_alert_button_yes = popup_alert.findViewById(R.id.popup_alert_button_yes);
                        popup_alert_button_yes.setText("네");
                        popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Gson gson = new GsonBuilder().setLenient().create();
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(HTTP.url)
                                        .addConverterFactory(GsonConverterFactory.create(gson))
                                        .build();
                                RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
                                retrofitAPI.postMsgOff(id, item_msg.getNo()).enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (response.body().equals("msgOff")) {
                                            msgList.remove(position);
                                            msgAdapter.notifyItemRemoved(position);
                                            popup_alert.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                    }
                                });
                            }
                        });
                        TextView popup_alert_button_no = popup_alert.findViewById(R.id.popup_alert_button_no);
                        popup_alert_button_no.setText("아니오");
                        popup_alert_button_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popup_alert.dismiss();
                            }
                        });
                        popup_alert.show();
                    }
                });
                popup_repository_msg.show();
            }
            @Override
            public void onFailure(Call<List<Item_Msg>> call, Throwable t) {
            }
        });


    }
    // 팝업 : 채팅보관
    public void showPopupRepositoryChat() {
        popup_repository_chat.show();

        Button popup_repository_chat_button_back = popup_repository_chat.findViewById(R.id.popup_repository_chat_button_back);
        popup_repository_chat_button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_repository_chat.dismiss();
            }
        });
    }
}