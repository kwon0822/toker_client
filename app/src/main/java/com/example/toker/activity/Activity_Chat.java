package com.example.toker.activity;

import android.app.Dialog;
import android.content.Intent;
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
import com.example.toker.http.RetrofitAPI;
import com.example.toker.view.adapter.AdapterChat;
import com.example.toker.view.Item.ItemChat;
import com.example.toker.view.listner.OnItemClickListnerChat;
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

public class Activity_Chat extends AppCompatActivity {

    Toolbar activity_chat_toolbar;
    ActionBar activity_chat_actionbar;

    Dialog alertDialog;
    Dialog inputDialog;

    Gson gson = new GsonBuilder().setLenient().create();
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(RetrofitAPI.url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
    RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

    TextView activity_chat_textview_accuse;
    TextView activity_chat_textview_block;
    EditText activity_chat_edittext_chat;
    Button activity_chat_button_chat;

    RecyclerView activity_chat_recyclerview_chat;
    private AdapterChat chatAdapter;
    private List<ItemChat> chatList = new ArrayList<>();

    String id = Activity_Main.user1;
    private Socket socket;

    private boolean isChat = false; // ping, chat, chatOff
    private boolean isSave = false; // saveOn, saveOff
    private boolean isMessage = false; // message
    private boolean isAccuse = false;
    private boolean isBlock = false;

    private Handler handler = new Handler();
    private boolean isType = false; // typeOn, typeOff
    private int typePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initialize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.off("chat", ChatListner);
        socket.off("chatOff", ChatOffListner);
        socket.off("typeOn", TypeOnListner);
        socket.off("typeOff", TypeOffListner);
        socket.off("message", MessageListner);
        socket.off("accuse", AccuseListner);
        socket.off("block", BlockListner);
        socket.off("save", SaveListner);
    }

    private void initialize() {

        SocketAPI socketAPI = (SocketAPI) getApplication();
        socket = socketAPI.getSocket();

        Intent intent = getIntent();
        String user2 = intent.getExtras().getString("user2");

        socket.on("chat", ChatListner);
        socket.on("chatOff", ChatOffListner);
        socket.on("typeOn", TypeOnListner);
        socket.on("typeOff", TypeOffListner);
        socket.on("save", SaveListner);
        socket.on("accuse", AccuseListner);
        socket.on("block", BlockListner);
        socket.on("message", MessageListner);

        socket.emit("chatOn", user2);
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

        activity_chat_toolbar = findViewById(R.id.activity_chat_toolbar);
        setSupportActionBar(activity_chat_toolbar);
        activity_chat_actionbar = getSupportActionBar();
        activity_chat_actionbar.setDisplayShowCustomEnabled(true);
        activity_chat_actionbar.setDisplayShowTitleEnabled(false);
        activity_chat_actionbar.setDisplayHomeAsUpEnabled(true);

        activity_chat_textview_accuse = findViewById(R.id.activity_chat_textview_accuse);
        activity_chat_textview_accuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isAccuse) {
                    Toast.makeText(getApplicationContext(), "이미 신고하셨어요! 더 하실말씀 있으시면 \n 개발자에 건의하기를 이용해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isBlock) {
                    Toast.makeText(getApplicationContext(), "이미 차단하셨어요! 더 하실말씀 있으시면 \n 개발자에 건의하기를 이용해주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                ShowAccuseDialog();
            }
        });

        activity_chat_textview_block = findViewById(R.id.activity_chat_textview_block);
        activity_chat_textview_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isBlock) {
                    Toast.makeText(getApplicationContext(), "이미 차단하셨어요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isAccuse) {
                    Toast.makeText(getApplicationContext(), "이미 신고하셨어요! \n 신고가 접수되면 상대가 바로 차단된답니다~", Toast.LENGTH_SHORT).show();
                    return;
                }

                ShowBlockDialog();
            }
        });

        activity_chat_edittext_chat = findViewById(R.id.activity_chat_edittext_send);
        activity_chat_edittext_chat.addTextChangedListener(new TextWatcher() {
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

        activity_chat_button_chat = findViewById(R.id.activity_chat_button_send);
        activity_chat_button_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isChat) {
                    String message = activity_chat_edittext_chat.getText().toString();

                    if (TextUtils.isEmpty(message)) {
                        activity_chat_edittext_chat.requestFocus();
                        return;

                    } else {

                        if (isType) {
                            socket.emit("typeOff");
                        }

                        ItemChat itemChat = new ItemChat(ItemChat.TYPE_MY_MSG, message);
                        chatList.add(itemChat);
                        chatAdapter.notifyItemInserted(chatList.size() - 1);

                        Gson gson = new Gson();
                        String jsonStr = gson.toJson(itemChat);
                        socket.emit("chat", jsonStr);

                        scrollToBottom();

                        isType = true;
                        activity_chat_edittext_chat.setText("");
                        isType = false;
                    }
                }
            }
        });

        activity_chat_recyclerview_chat = findViewById(R.id.activity_chat_recyclerview_chat);
        activity_chat_recyclerview_chat.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        chatAdapter = new AdapterChat(chatList);
        activity_chat_recyclerview_chat.setAdapter(chatAdapter);
        chatAdapter.setOnItemClicklistener(new OnItemClickListnerChat() {
            @Override
            public void onItemClick(AdapterChat.ViewHolder holder, View view, int position) {
                ItemChat itemChat = chatAdapter.getItem(position);
                if (itemChat.getType() == ItemChat.TYPE_SEND_MSG) {

                    if (isBlock) {
                        Toast.makeText(getApplicationContext(), "차단한 상대에게는 쪽지를 보낼 수 없어요!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (isAccuse) {
                        Toast.makeText(getApplicationContext(), "신고한 상대에게는 쪽지를 보낼 수 없어요!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!isMessage) {
                        ShowMessageDialog();
                    }
                }
            }
        });

        addNotice("변태 광고는 신고해주세요. 다시 대면하기 싫은 상대는 차단해주세요. ");
        addNotice("신고/차단 된 채팅은 레벨시스템에 산정되지 않으니 걱정마세요! ");
        addNotice("채팅이 시작되었습니다.");
    }


    // region listner ==============================================================================

    private Emitter.Listener ChatListner = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = args[0].toString();

                    ItemChat itemChat = new ItemChat(ItemChat.TYPE_YOUR_MSG, message);
                    chatList.add(itemChat);
                    chatAdapter.notifyItemInserted(chatList.size() - 1);
                    scrollToBottom();

                    Gson gson = new Gson();
                    String jsonStr = gson.toJson(itemChat);
                    socket.emit("chat", jsonStr);
                }
            });
        }
    };

    private Emitter.Listener ChatOffListner = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isChat = false;
                    socket.emit("chatOff");

                    chatList.add(new ItemChat(ItemChat.TYPE_Notice, "채팅이 종료되었습니다."));
                    chatAdapter.notifyItemInserted(chatList.size() - 1);

                    chatList.add(new ItemChat(ItemChat.TYPE_Notice, "비매너 대화를 나눴다면 신고 또는 차단해주세요."));
                    chatAdapter.notifyItemInserted(chatList.size() - 1);

                    chatList.add(new ItemChat(ItemChat.TYPE_SEND_MSG, "쪽지전송하기"));
                    chatAdapter.notifyItemInserted(chatList.size() - 1);
                }
            });
        }
    };

    private Emitter.Listener TypeOnListner = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatList.add(new ItemChat(ItemChat.TYPE_TYPING, "입력 중"));
                    typePosition = chatList.size() - 1;
                    chatAdapter.notifyItemInserted(typePosition);
                    scrollToBottom();
                }
            });
        }
    };

    private Emitter.Listener TypeOffListner = new Emitter.Listener() {
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

    private Emitter.Listener MessageListner = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alertDialog.dismiss();
                    isMessage = true;
                }
            });
        }
    };

    private Emitter.Listener SaveListner = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alertDialog.dismiss();

                    if (isSave) {
                        Toast.makeText(getApplicationContext(), "채팅저장이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                        activity_chat_toolbar.getMenu().getItem(0).setTitle("채팅 저장하기");
                        isSave = false;

                    } else {

                        if (isChat) {
                            Toast.makeText(getApplicationContext(), "채팅종료 후 모든 채팅내용이 저장됩니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "채팅내용이 저장되었습니다!", Toast.LENGTH_SHORT).show();
                        }

                        activity_chat_toolbar.getMenu().getItem(0).setTitle("채팅저장 취소하기");
                        isSave = true;
                    }
                }
            });
        }
    };

    private Emitter.Listener AccuseListner = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                    isAccuse = true;

                    socket.emit("chatOff");

                    if (isChat) {
                        finish();
                    }
                }
            });
        }
    };

    private Emitter.Listener BlockListner = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "차단이 접수되었습니다.", Toast.LENGTH_SHORT).show();
                    isBlock = true;

                    socket.emit("chatOff");

                    if (isChat) {
                        finish();
                    }
                }
            });
        }
    };

    // endregion listner ===========================================================================

    // region dialog ================================================================================

    // 채팅저장
    public void ShowSaveDialog() {
        alertDialog = new Dialog(Activity_Chat.this);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_alert);

        Button popup_alert_button_yes = alertDialog.findViewById(R.id.dialog_alert_button_yes);
        popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socket.emit("save");
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

    public void ShowRequestDialog() {
        inputDialog = new Dialog(Activity_Chat.this);
        inputDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        inputDialog.setContentView(R.layout.dialog_input);

        EditText dialog_input_edittext_description = inputDialog.findViewById(R.id.dialog_input_edittext_description);
        dialog_input_edittext_description.setHint(R.string.dialog_input_chat_request_editText);
        TextView dialog_input_textview_description = inputDialog.findViewById(R.id.dialog_input_textview_description);
        dialog_input_textview_description.setText(R.string.dialog_input_chat_request_textView);
        Button dialog_input_button_send = inputDialog.findViewById(R.id.dialog_input_button_send);
        dialog_input_button_send.setText(R.string.dialog_input_chat_request_button);

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
                alertDialog = new Dialog(Activity_Chat.this);
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(R.layout.dialog_alert);

                Button popup_alert_button_yes = alertDialog.findViewById(R.id.dialog_alert_button_yes);
                popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText popup_input_edittext_description = inputDialog.findViewById(R.id.dialog_input_edittext_description);
                        String description = popup_input_edittext_description.getText().toString();
                        String location = "chat";

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

    public void ShowQuitDialog() {
        alertDialog = new Dialog(Activity_Chat.this);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_alert);

        Button popup_alert_button_yes = alertDialog.findViewById(R.id.dialog_alert_button_yes);
        popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChat) {
                    isChat = false;
                    socket.emit("chatOff");
                }

                alertDialog.dismiss();
                finish();
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

    public void ShowAccuseDialog() {
        inputDialog = new Dialog(Activity_Chat.this);
        inputDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        inputDialog.setContentView(R.layout.dialog_input);

        EditText dialog_input_edittext_description = inputDialog.findViewById(R.id.dialog_input_edittext_description);
        dialog_input_edittext_description.setHint(R.string.dialog_input_chat_accuse_editText);
        TextView dialog_input_textview_description = inputDialog.findViewById(R.id.dialog_input_textview_description);
        dialog_input_textview_description.setText(R.string.dialog_input_chat_accuse_textView);
        Button dialog_input_button_send = inputDialog.findViewById(R.id.dialog_input_button_send);
        dialog_input_button_send.setText(R.string.dialog_input_chat_accuse_button);

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

                EditText popup_input_edittext_contents = inputDialog.findViewById(R.id.dialog_input_edittext_description);
                String msg = popup_input_edittext_contents.getText().toString();

                alertDialog = new Dialog(Activity_Chat.this);
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(R.layout.dialog_alert);

                Button popup_alert_button_yes = alertDialog.findViewById(R.id.dialog_alert_button_yes);
                popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        socket.emit("accuse", msg);
                        alertDialog.dismiss();
                        inputDialog.dismiss();
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

    public void ShowBlockDialog() {
        alertDialog = new Dialog(Activity_Chat.this);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_alert);

        Button popup_alert_button_yes = alertDialog.findViewById(R.id.dialog_alert_button_yes);
        popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                socket.emit("block");
                alertDialog.dismiss();
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

    public void ShowMessageDialog() {
        inputDialog = new Dialog(Activity_Chat.this);
        inputDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        inputDialog.setContentView(R.layout.dialog_input);

        EditText dialog_input_edittext_description = inputDialog.findViewById(R.id.dialog_input_edittext_description);
        dialog_input_edittext_description.setHint(R.string.dialog_input_chat_sendMessage_editText);
        TextView dialog_input_textview_description = inputDialog.findViewById(R.id.dialog_input_textview_description);
        dialog_input_textview_description.setText(R.string.dialog_input_chat_sendMessage_textView);
        Button dialog_input_button_send = inputDialog.findViewById(R.id.dialog_input_button_send);
        dialog_input_button_send.setText(R.string.dialog_input_chat_sendMessage_button);

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

                EditText popup_input_edittext_contents = inputDialog.findViewById(R.id.dialog_input_edittext_description);
                String message = popup_input_edittext_contents.getText().toString();

                alertDialog = new Dialog(Activity_Chat.this);
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(R.layout.dialog_alert);

                Button popup_alert_button_yes = alertDialog.findViewById(R.id.dialog_alert_button_yes);
                popup_alert_button_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        socket.emit("message", message);
                        alertDialog.dismiss();
                        inputDialog.dismiss();
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

    // endregion dialog  ============================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_chat_save:
                ShowSaveDialog();
                break;

            case R.id.menu_chat_request:
                ShowRequestDialog();
                break;

            case R.id.menu_chat_quit:
                ShowQuitDialog();
                break;

            case android.R.id.home:
                ShowQuitDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void scrollToBottom() {
        activity_chat_recyclerview_chat.scrollToPosition(chatAdapter.getItemCount() - 1);
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

    private void addNotice(String notice) {
        ItemChat itemChat = new ItemChat(ItemChat.TYPE_Notice, notice);
        chatList.add(itemChat);
        chatAdapter.notifyItemInserted(chatList.size() - 1);

        Gson gson = new Gson();
        String jsonStr = gson.toJson(itemChat);
        socket.emit("chat", jsonStr);
    }
}


