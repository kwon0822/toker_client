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
import com.example.toker.view.adapter.AdapterChat;
import com.example.toker.view.Item.ItemRead;
import com.example.toker.view.adapter.OnItemClickListner_Chat;
import com.example.toker.tcp.SocketAPI;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Page_Chat extends AppCompatActivity {

    Toolbar page_chat_toolbar;
    ActionBar page_chat_actionbar;

    Dialog popup_alert;
    Dialog popup_input;

    TextView page_chat_textview_accuse;
    TextView page_chat_textview_block;
    EditText page_chat_edittext_chat;
    Button page_chat_button_chat;

    RecyclerView page_chat_recyclerview_chat;
    private AdapterChat chatAdapter;
    private List<ItemRead> chatList = new ArrayList<>();

    String id = Page_Login.myID;
    private Socket socket;

    private boolean isChat = false; // 채팅종료 -> 채팅전송버튼비활성화, 핑쓰레드정지
    private boolean isMsg = false;
    private boolean isSave = false;

    private Handler handler = new Handler();
    private boolean isType = false; // 입력중 -> 제한시간, 전송버튼
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
        socket.on("msg", Msg);
        socket.on("save", Save);
        socket.on("accuse", Accuse);
        socket.on("block", Block);
    }

    @Override
    protected void onStop() {
        super.onStop();
        socket.off("chat", Chat);
        socket.off("chatOff", ChatOff);
        socket.off("typeOn", TypeOn);
        socket.off("typeOff", TypeOff);
        socket.off("msg", Msg);
        socket.off("save", Save);
        socket.off("accuse", Accuse);
        socket.off("block", Block);

    }

    private void initialize() {

        // 소켓
        SocketAPI socketAPI = (SocketAPI) getApplication();
        socket = socketAPI.getSocket();
        socket.emit("chatOn", Page_Login.yourID);
        isChat = true;
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
        
        // 신고하기
        page_chat_textview_accuse = findViewById(R.id.page_chat_textview_accuse);
        page_chat_textview_accuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAccuse();
            }
        });

        // 차단하기
        page_chat_textview_block = findViewById(R.id.page_chat_textview_block);
        page_chat_textview_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogBlock();
            }
        });
        
        // 채팅입력
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
                
        // 채팅전송
        page_chat_button_chat = findViewById(R.id.page_chat_button_chat);
        page_chat_button_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isChat) {
                    String message = page_chat_edittext_chat.getText().toString();

                    if (TextUtils.isEmpty(message)) {
                        page_chat_edittext_chat.requestFocus();
                        return;

                    } else {

                        if (isType) {
                            socket.emit("typeOff");
                        }

                        ItemRead itemRead = new ItemRead(ItemRead.TYPE_MY_MSG, message);
                        chatList.add(itemRead);
                        chatAdapter.notifyItemInserted(chatList.size() - 1);

                        Gson gson = new Gson();
                        String jsonStr = gson.toJson(itemRead);
                        socket.emit("chat", jsonStr);

                        scrollToBottom();

                        isType = true;
                        page_chat_edittext_chat.setText("");
                        isType = false;
                    }
                }
            }
        });

        // 채팅창
        page_chat_recyclerview_chat = findViewById(R.id.page_chat_recyclerview_chat);
        page_chat_recyclerview_chat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        chatAdapter = new AdapterChat(chatList);
        page_chat_recyclerview_chat.setAdapter(chatAdapter);
        chatAdapter.setOnItemClicklistener(new OnItemClickListner_Chat() {
            @Override
            public void onItemClick(AdapterChat.ViewHolder holder, View view, int position) {
                ItemRead itemRead = chatAdapter.getItem(position);
                if (itemRead.getType() == ItemRead.TYPE_SEND_MSG) {

                    if (!isMsg) {
                        showDialogMessage();
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

                    ItemRead itemRead = new ItemRead(ItemRead.TYPE_YOUR_MSG, message);
                    chatList.add(itemRead);
                    chatAdapter.notifyItemInserted(chatList.size() - 1);
                    scrollToBottom();

                    Gson gson = new Gson();
                    String jsonStr = gson.toJson(itemRead);
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
                    chatList.add(new ItemRead(ItemRead.TYPE_Notice, "채팅이 종료되었습니다."));
                    chatAdapter.notifyItemInserted(chatList.size() - 1);
                    
                    // 쪽지버튼 문구 띄어주기
                    chatList.add(new ItemRead(ItemRead.TYPE_SEND_MSG, "쪽지전송하기"));
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
                    chatList.add(new ItemRead(ItemRead.TYPE_TYPING, "입력 중"));
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

    private Emitter.Listener Msg = new Emitter.Listener() {
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

    private Emitter.Listener Save = new Emitter.Listener() {
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

    private Emitter.Listener Accuse = new Emitter.Listener() {
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

    private Emitter.Listener Block = new Emitter.Listener() {
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

    // endregion listner ===========================================================================

    // region popup ================================================================================

    // 채팅저장
    public void showDialogSave() {
        popup_alert = new Dialog(Page_Chat.this);
        popup_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popup_alert.setContentView(R.layout.popup_alert);

        Button popup_alert_button_yes = popup_alert.findViewById(R.id.popup_alert_button_yes);
        popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socket.emit("save");
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
    public void showDialogQuit() {
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
    public void showDialogAccuse() {
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
                        socket.emit("accuse", msg);
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
    public void showDialogBlock() {
        popup_alert = new Dialog(Page_Chat.this);
        popup_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popup_alert.setContentView(R.layout.popup_alert);

        Button popup_alert_button_yes = popup_alert.findViewById(R.id.popup_alert_button_yes);
        popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socket.emit("block");
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

    // 쪽지전송
    public void showDialogMessage() {
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
                        socket.emit("msg", msg);
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

    // endregion popup  ============================================================================

    // 툴바세팅
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
                showDialogSave();
                break;
            case R.id.page_chat_toolbar_menu_quitChat:
                showDialogQuit();
                break;
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                showDialogQuit();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void scrollToBottom() {
        page_chat_recyclerview_chat.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

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

    // 공지추가
    private void addNotice(String notice) {
        ItemRead itemRead = new ItemRead(ItemRead.TYPE_Notice, notice);
        chatList.add(itemRead);
        chatAdapter.notifyItemInserted(chatList.size() - 1);

        Gson gson = new Gson();
        String jsonStr = gson.toJson(itemRead);
        socket.emit("chat", jsonStr);
    }
}


