package com.example.toker.Activity;

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

public class Activity_Main extends AppCompatActivity {

    Toolbar activity_main_toolbar;
    ActionBar activity_main_actionbar;

    Button activity_main_button_filter;
    Button activity_main_button_message;
    Button activity_main_button_memory;

    TextView activity_main_textview_connectingNum;
    TextView activity_main_textview_waitingNum;
    TextView activity_main_textview_noticeBar;

    Dialog alertDialog;
    Dialog inputDialog;

    private Socket socket;

    private boolean isLogin = false;

    String id = Activity_Login.myID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        activity_main_toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(activity_main_toolbar);
        activity_main_actionbar = getSupportActionBar();
        activity_main_actionbar.setDisplayShowCustomEnabled(true);
        activity_main_actionbar.setDisplayShowTitleEnabled(false);
        activity_main_actionbar.setDisplayHomeAsUpEnabled(false);

        activity_main_button_filter = findViewById(R.id.activity_main_button_filter);
        activity_main_button_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Activity_Filter.class);
                startActivity(intent);
            }
        });

        activity_main_button_message = findViewById(R.id.activity_main_button_message);
        activity_main_button_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Activity_Message.class);
                startActivity(intent);
            }
        });

        activity_main_button_memory = findViewById(R.id.activity_main_button_memory);
        activity_main_button_memory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Activity_Memory.class);
                startActivity(intent);
            }
        });
    }

    // region Listner ==============================================================================
    // endregion listner  ==========================================================================

    // region ToolBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_about:
                Intent intent_about = new Intent(getApplicationContext(), Activity_Level.class);
                startActivity(intent_about);
                break;

            case R.id.menu_main_level:
                Intent intent_level = new Intent(getApplicationContext(), Activity_About.class);
                startActivity(intent_level);
                break;

            case R.id.menu_main_request:
                inputDialog = new Dialog(Activity_Main.this);
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
                        alertDialog = new Dialog(Activity_Main.this);
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
                break;
            case R.id.menu_main_logout:
                Intent intent_logout = new Intent(getApplicationContext(), Activity_Login.class);
                startActivity(intent_logout);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // endregion =================================================================================
}