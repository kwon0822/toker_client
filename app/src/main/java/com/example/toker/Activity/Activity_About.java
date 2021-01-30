package com.example.toker.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.toker.R;

public class Activity_About extends Activity {

    Dialog alertDialog;
    Dialog inputDialog;

    Button activity_about_button_back;
    Button activity_about_button_request;

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
    }
}
