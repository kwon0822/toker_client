package com.example.toker.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.toker.R;

public class Activity_Login extends Activity {

    EditText activity_login_edittext_phoneNumber;
    Button activity_login_button_getAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        initialize();
    }

    private void initialize() {
        activity_login_edittext_phoneNumber = findViewById(R.id.activity_login_edittext_phoneNumber);
        activity_login_button_getAuth = findViewById(R.id.activity_login_button_getAuth);
        activity_login_button_getAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user1 = activity_login_edittext_phoneNumber.getText().toString();

                Intent intent = new Intent(getApplicationContext(), Activity_Main.class);
                intent.putExtra("user1", user1);
                startActivity(intent);
            }
        });
    }
}