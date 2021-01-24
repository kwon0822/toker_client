package com.example.toker.page;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.toker.R;
import com.example.toker.recyclerview.Adapter_Chat;
import com.example.toker.recyclerview.Item_Chat;
import com.example.toker.recyclerview.OnItemClickListner_Chat;
import com.example.toker.tcp.Application_Socket;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Page_Chat extends AppCompatActivity {

    Toolbar page_chat_toolbar;
    ActionBar page_chat_actionbar;
    TextView page_chat_textview_accuse;
    TextView page_chat_textview_block;
    EditText page_chat_edittext_chat;
    Button page_chat_button_chat;
    Dialog popup_alert;
    Dialog popup_input;

    RecyclerView page_chat_recyclerview_chat;
    private Adapter_Chat chatAdapter;
    private List<Item_Chat> chatList = new ArrayList<>();

    String id = Page_Login.myID;
    private Socket socket;

    // 채팅 상태 컨트롤 : (1)채팅종료메세지
    // => 채팅전송버튼작동안하게, 핑쓰레드작동안하게
    private boolean isChat = false;
    private boolean isMsg = false;
    private boolean isSave = false;

    // 타이핑 문구 컨트롤 : (1)제한시간되면꺼짐 (2)채팅전송하면꺼짐
    // => 타이핑상태전송안하게
    private boolean isType = false;
    private Handler handler = new Handler();
    private int typePosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_chat);

        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();

        socket.on("chat", Chat);
        socket.on("chatOff", ChatOff);
        socket.on("typeOn", TypeOn);
        socket.on("typeOff", TypeOff);
        socket.on("msgOn", MsgOn);
        socket.on("saveOn", SaveOn);
        socket.on("accuseOn", AccuseOn);
        socket.on("blockOn", BlockOn);
    }

    @Override
    protected void onStop() {
        super.onStop();

        socket.off("chat", Chat);
        socket.off("chatOff", ChatOff);
        socket.off("typeOn", TypeOn);
        socket.off("typeOff", TypeOn);
        socket.off("msgOn", MsgOn);
        socket.off("saveOff", SaveOn);
        socket.off("accuseOn", AccuseOn);
        socket.off("blockOn", BlockOn);

    }

    private void initialize() {

        popup_alert = new Dialog(Page_Chat.this);
        popup_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popup_alert.setContentView(R.layout.popup_alert);

        Application_Socket application_socket = (Application_Socket) getApplication();
        socket = application_socket.getSocket();

        socket.emit("chatOn", Page_Login.yourID);
        isChat = true;
        
        // isChat 상태인 경우 3초 마다 핑 보내기
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isChat) {
                    try {
                        socket.emit("ping");
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        // 툴바
        page_chat_toolbar = findViewById(R.id.page_chat_toolbar);
        setSupportActionBar(page_chat_toolbar);
        page_chat_actionbar = getSupportActionBar();
        page_chat_actionbar.setDisplayShowCustomEnabled(true);
        page_chat_actionbar.setDisplayShowTitleEnabled(false);
        page_chat_actionbar.setDisplayHomeAsUpEnabled(true);


        
        // 팝업 : 신고하기
        page_chat_textview_accuse = findViewById(R.id.page_chat_textview_accuse);
        page_chat_textview_accuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupFeedbackAccuse();
            }
        });

        // 팝업 : 차단하기
        page_chat_textview_block = findViewById(R.id.page_chat_textview_block);
        page_chat_textview_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupAlertBlock();
            }
        });
        
        // edittext : 채팅입력
        page_chat_edittext_chat = findViewById(R.id.page_chat_edittext_chat);
        page_chat_edittext_chat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isChat) {
                    if (!socket.connected()) {
                        return;
                    }

                    if (!isType) {
                        isType = true;
                        socket.emit("typeOn");
                    }

                    handler.removeCallbacks(TypeOffTimer);
                    handler.postDelayed(TypeOffTimer, 500);
                }
            }
        });
        
        
        // button : 채팅전송
        page_chat_button_chat = findViewById(R.id.page_chat_button_chat);
        page_chat_button_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isChat) {
                    // 입력창에서 메세지 받아오기
                    String message = page_chat_edittext_chat.getText().toString();

                    // 메세지 비어있는 경우 포커스해주기
                    if (TextUtils.isEmpty(message)) {
                        page_chat_edittext_chat.requestFocus();
                        return;

                        // 메세지 들어있는 경우 보내주기
                    } else {

                        if (isType) {
                            socket.emit("typeOff");
                        }

                        Item_Chat item_chat = new Item_Chat(Item_Chat.TYPE_MY_MSG, message);
                        chatList.add(item_chat);
                        chatAdapter.notifyItemInserted(chatList.size() - 1);

                        Gson gson = new Gson();
                        String jsonStr = gson.toJson(item_chat);
                        socket.emit("chat", jsonStr);

                        scrollToBottom();

                        isType = true;
                        page_chat_edittext_chat.setText("");
                        isType = false;
                    }
                }
            }
        });

        // 리사이클러뷰 : 채팅
        page_chat_recyclerview_chat = findViewById(R.id.page_chat_recyclerview_chat);
        page_chat_recyclerview_chat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        chatAdapter = new Adapter_Chat(chatList);
        page_chat_recyclerview_chat.setAdapter(chatAdapter);
        chatAdapter.setOnItemClicklistener(new OnItemClickListner_Chat() {
            @Override
            public void onItemClick(Adapter_Chat.ViewHolder holder, View view, int position) {
                Item_Chat item_chat = chatAdapter.getItem(position);
                if (item_chat.getType() == Item_Chat.TYPE_SEND_MSG) {

                    if (!isMsg) {

                        popup_input = new Dialog(Page_Chat.this);
                        popup_input.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        popup_input.setContentView(R.layout.popup_input);

                        Button popup_feedback_msg_button_back = popup_input.findViewById(R.id.popup_input_button_back);
                        popup_feedback_msg_button_back.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popup_input.dismiss();
                            }
                        });

                        Button popup_feedback_msg_button_send = popup_input.findViewById(R.id.popup_input_button_send);
                        popup_feedback_msg_button_send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                EditText popup_input_edittext_contents = popup_input.findViewById(R.id.popup_input_edittext_contents);
                                String msg = popup_input_edittext_contents.getText().toString();

                                popup_alert = new Dialog(Page_Chat.this);
                                popup_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                popup_alert.setContentView(R.layout.popup_alert);

                                Button popup_alert_button_yes = popup_alert.findViewById(R.id.popup_alert_button_yes);
                                popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        socket.emit("msgOn", msg);
                                        popup_alert.dismiss();
                                        popup_input.dismiss();
                                    }
                                });

                                Button popup_alert_button_no = popup_alert.findViewById(R.id.popup_alert_button_no);
                                popup_alert_button_no.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        popup_alert.dismiss();
                                    }
                                });

                                popup_alert.show();
                            }
                        });

                        popup_input.show();
                    }
                }
            }
        });
        addNotice("채팅이 시작되었습니다.");
    }


    // region listner ==============================================================================

    private Emitter.Listener Chat = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = args[0].toString();

                    Item_Chat item_chat = new Item_Chat(Item_Chat.TYPE_YOUR_MSG, message);
                    chatList.add(item_chat);
                    chatAdapter.notifyItemInserted(chatList.size() - 1);
                    scrollToBottom();

                    Gson gson = new Gson();
                    String jsonStr = gson.toJson(item_chat);
                    socket.emit("chat", jsonStr);
                }
            });
        }
    };

    private Emitter.Listener ChatOff = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // chatOff 들어오면 더 이상 채팅전송, 핑전송 못하게 isChat 상태 바꿔주고
                    isChat = false;
                    
                    // tcp 서버쪽 timeout 클리어 해주고
                    socket.emit("chatOff");
                    
                    // 채팅종료 문구 띄어주기
                    chatList.add(new Item_Chat(Item_Chat.TYPE_Notice, "채팅이 종료되었습니다."));
                    chatAdapter.notifyItemInserted(chatList.size() - 1);
                    
                    // 쪽지버튼 문구 띄어주기
                    chatList.add(new Item_Chat(Item_Chat.TYPE_SEND_MSG, "쪽지전송하기"));
                    chatAdapter.notifyItemInserted(chatList.size() - 1);
                }
            });
        }
    };

    private Emitter.Listener TypeOn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatList.add(new Item_Chat(Item_Chat.TYPE_TYPING, "입력 중"));
                    typePosition = chatList.size() - 1;
                    chatAdapter.notifyItemInserted(typePosition);
                    scrollToBottom();
                }
            });
        }
    };

    private Emitter.Listener TypeOff = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatList.remove(typePosition);
                    chatAdapter.notifyItemRemoved(typePosition);
                }
            });
        }
    };

    private Emitter.Listener MsgOn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    popup_alert.dismiss();
                    isMsg = true;
                }
            });
        }
    };

    private Emitter.Listener SaveOn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    popup_alert.dismiss();
                    if (isSave) {
                        Toast.makeText(getApplicationContext(), "대화저장이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                        page_chat_toolbar.getMenu().getItem(0).setTitle("대화 저장하기");
                        isSave = false;
                    } else {
                        Toast.makeText(getApplicationContext(), "대화종료 후 모든 채팅내용이 저장됩니다.", Toast.LENGTH_SHORT).show();
                        page_chat_toolbar.getMenu().getItem(0).setTitle("대화 저장취소");
                        isSave = true;
                    }
                }
            });
        }
    };

    private Emitter.Listener AccuseOn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                    socket.emit("chatOff");
                    finish();
                }
            });
        }
    };

    private Emitter.Listener BlockOn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "차단이 접수되었습니다.", Toast.LENGTH_SHORT).show();
                    socket.emit("chatOff");
                    finish();
                }
            });
        }
    };

    private Runnable TypeOffTimer = new Runnable() {
        @Override
        public void run() {
            if (!isType) {
                return;
            }

            isType = false;
            socket.emit("typeOff");
        }
    };

    // endregion listner ===========================================================================

    // region popup ================================================================================

    // 채팅저장
    public void showPopupAlertSave() {
        popup_alert = new Dialog(Page_Chat.this);
        popup_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popup_alert.setContentView(R.layout.popup_alert);

        Button popup_alert_button_yes = popup_alert.findViewById(R.id.popup_alert_button_yes);
        popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socket.emit("saveOn");
            }
        });

        Button popup_alert_button_no = popup_alert.findViewById(R.id.popup_alert_button_no);
        popup_alert_button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_alert.dismiss();
            }
        });

        popup_alert.show();
    }

    // 채팅종료
    public void showPopupAlertQuit() {
        popup_alert = new Dialog(Page_Chat.this);
        popup_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popup_alert.setContentView(R.layout.popup_alert);

        Button popup_alert_button_yes = popup_alert.findViewById(R.id.popup_alert_button_yes);
        popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChat = false;
                socket.emit("chatOff");
                finish();
            }
        });

        Button popup_alert_button_no = popup_alert.findViewById(R.id.popup_alert_button_no);
        popup_alert_button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_alert.dismiss();
            }
        });

        popup_alert.show();
    }

    // 신고하기
    public void showPopupFeedbackAccuse() {
        popup_input = new Dialog(Page_Chat.this);
        popup_input.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popup_input.setContentView(R.layout.popup_input);

        Button popup_input_button_back = popup_input.findViewById(R.id.popup_input_button_back);
        popup_input_button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_input.dismiss();
            }
        });

        Button popup_input_button_send = popup_input.findViewById(R.id.popup_input_button_send);
        popup_input_button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText popup_input_edittext_contents = popup_input.findViewById(R.id.popup_input_edittext_contents);
                String msg = popup_input_edittext_contents.getText().toString();

                popup_alert = new Dialog(Page_Chat.this);
                popup_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                popup_alert.setContentView(R.layout.popup_alert);

                Button popup_alert_button_yes = popup_alert.findViewById(R.id.popup_alert_button_yes);
                popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        socket.emit("accuseOn", msg);
                        popup_alert.dismiss();
                        popup_input.dismiss();
                    }
                });

                Button popup_alert_button_no = popup_alert.findViewById(R.id.popup_alert_button_no);
                popup_alert_button_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup_alert.dismiss();
                    }
                });

                popup_alert.show();
            }
        });
        popup_input.show();
    }

    // 차단하기
    public void showPopupAlertBlock() {
        popup_alert = new Dialog(Page_Chat.this);
        popup_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popup_alert.setContentView(R.layout.popup_alert);

        Button popup_alert_button_yes = popup_alert.findViewById(R.id.popup_alert_button_yes);
        popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socket.emit("blockOn");
                popup_alert.dismiss();
            }
        });

        Button popup_alert_button_no = popup_alert.findViewById(R.id.popup_alert_button_no);
        popup_alert_button_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_alert.dismiss();
            }
        });

        popup_alert.show();
    }

    // endregion popup  ============================================================================



    // 메뉴툴바
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.page_chat_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.page_chat_toolbar_menu_saveChat:
                showPopupAlertSave();
                break;
            case R.id.page_chat_toolbar_menu_quitChat:
                showPopupAlertQuit();
                break;
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                showPopupAlertQuit();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void scrollToBottom() {
        page_chat_recyclerview_chat.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

    private void addNotice(String notice) {
        Item_Chat item_chat = new Item_Chat(Item_Chat.TYPE_Notice, notice);
        chatList.add(item_chat);
        chatAdapter.notifyItemInserted(chatList.size() - 1);

        Gson gson = new Gson();
        String jsonStr = gson.toJson(item_chat);
        socket.emit("chat", jsonStr);
    }
}


