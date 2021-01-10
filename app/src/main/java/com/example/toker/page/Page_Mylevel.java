package com.example.toker.page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.toker.R;

public class Page_Mylevel extends AppCompatActivity {

    Button page_mylevel_button_back;
    TextView page_mylevel_textview_levelSystem;
    TextView page_mylevel_textview_myLevel;
    TextView page_mylevel_textview_myChatime;
    TextView page_mylevel_textview_chatData;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_mylevel);

        initialize();
    }

    private void initialize() {
       page_mylevel_button_back = findViewById(R.id.page_mylevel_button_back);
       page_mylevel_button_back.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getApplicationContext(), Page_Main.class);
               startActivity(intent);
           }
       });
    }
}
