package com.example.toker.page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.toker.R;

public class Page_Login extends Activity {

    EditText page_login_edittext_phoneNumber;
    Button page_login_button_getAuth;

    static public String myID = "";
    static public String yourID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.page_login);
        initialize();
    }

    private void initialize() {
        page_login_edittext_phoneNumber = findViewById(R.id.page_login_edittext_phoneNumber);
        page_login_button_getAuth = findViewById(R.id.page_login_button_getAuth);

        page_login_button_getAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myID = page_login_edittext_phoneNumber.getText().toString();

                Intent intent = new Intent(getApplicationContext(), Page_Main.class);
                startActivity(intent);
            }
        });
    }
}