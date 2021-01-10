package com.example.toker.page;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.toker.R;

public class Page_Signup extends Activity {

    Button page_signup_button_back;
    TextView page_signup_textview_title;
    TextView page_signup_textview_about;
    EditText page_signup_edittext_id;
    Button page_signup_button_idcheck;
    EditText page_signup_edittext_pw;
    EditText page_signup_edittext_pwcheck;
    TextView page_signup_textview_pwcheck;
    EditText page_signup_edittext_phone;
    Button page_signup_button_phonecheck;
    CheckBox page_signup_checkbox_term;
    TextView page_signup_textview_term;
    CheckBox page_signup_checkbox_privacy;
    TextView page_signup_textview_privacy;
    Button page_signup_button_signup;

    Dialog popup_posting_contents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.page_signup);

        initialize();
    }

    private void initialize() {
        
        page_signup_textview_title = findViewById(R.id.page_signup_textview_title);

        page_signup_edittext_id = findViewById(R.id.page_signup_edittext_id);
        page_signup_button_idcheck = findViewById(R.id.page_signup_button_idcheck);
        page_signup_edittext_pw = findViewById(R.id.page_signup_edittext_pw);
        page_signup_edittext_pwcheck = findViewById(R.id.page_signup_edittext_pwcheck);
        page_signup_textview_pwcheck = findViewById(R.id.page_signup_textview_pwcheck);
        page_signup_edittext_phone = findViewById(R.id.page_signup_edittext_phone);
        page_signup_button_phonecheck = findViewById(R.id.page_signup_button_phonecheck);
        page_signup_checkbox_term = findViewById(R.id.page_signup_checkbox_term);
        page_signup_textview_term = findViewById(R.id.page_signup_textview_term);
        page_signup_checkbox_privacy = findViewById(R.id.page_signup_checkbox_privacy);
        page_signup_textview_privacy = findViewById(R.id.page_signup_textview_privacy);
        page_signup_button_signup = findViewById(R.id.page_signup_button_signup);

        
        // 버튼 : 뒤로가기
        page_signup_button_back = findViewById(R.id.page_login_button_back);
        page_signup_button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 텍스트뷰 : '토커에 대해서' 포스팅 팝업 띄우기
        popup_posting_contents = new Dialog(Page_Signup.this);
        popup_posting_contents.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popup_posting_contents.setContentView(R.layout.popup_posting_contents);
        page_signup_textview_about = findViewById(R.id.page_signup_textview_about);
        page_signup_textview_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupPosting();
            }
        });

        // 버튼 : 회원가입
        page_signup_button_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Page_Login.class);
                startActivity(intent);
            }
        });
    }

    // 팝업 : 팝업 띄우고, 필요한 기능 달기
    public void showPopupPosting() {
        popup_posting_contents.show();

        Button popup_posting_contents_button_quit = popup_posting_contents.findViewById(R.id.popup_posting_contents_button_back);
        popup_posting_contents_button_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_posting_contents.dismiss();
            }
        });


        Button popup_posting_contents_button_request = popup_posting_contents.findViewById(R.id.popup_posting_contents_button_request);
        popup_posting_contents_button_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "개발자에게 의견주기", Toast.LENGTH_SHORT).show();
            }
        });

    }
}