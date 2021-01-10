package com.example.toker.page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.toker.R;

public class Page_Myinfo extends AppCompatActivity {

    Button page_myinfo_button_back;
    TextView page_myinfo_textview_privacy;
    TextView page_myinfo_textview_myID;
    EditText page_myinfo_oldPW;
    EditText page_myinfo_newPW;
    EditText page_myinfo_newPWCheck;
    Button page_myinfo_button_changePW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_myinfo);

        initialize();
    }

    private void initialize() {
        page_myinfo_button_back = findViewById(R.id.page_myinfo_button_back);
        page_myinfo_textview_privacy = findViewById(R.id.page_myinfo_textview_privacy);
        page_myinfo_textview_myID = findViewById(R.id.page_myinfo_textview_myID);
        page_myinfo_oldPW = findViewById(R.id.page_myinfo_oldPW);
        page_myinfo_newPW = findViewById(R.id.page_myinfo_newPW);
        page_myinfo_newPWCheck = findViewById(R.id.page_myinfo_newPWCheck);
        page_myinfo_button_changePW = findViewById(R.id.page_myinfo_button_changePW);

        page_myinfo_button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Page_Main.class);
                startActivity(intent);
            }
        });

    }
}