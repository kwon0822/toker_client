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

import com.example.toker.R;
import com.example.toker.tcp.SocketAPI;
import com.github.nkzawa.socketio.client.Socket;

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
                Intent intent = new Intent(getApplicationContext(), Page_Memory.class);
                startActivity(intent);
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