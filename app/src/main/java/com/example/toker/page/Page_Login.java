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
                
                // 입력창에서 휴대폰번호 받아오기
                myID = page_login_edittext_phoneNumber.getText().toString();

                // 아이디와 함께 메인화면으로 넘어가기
                Intent intent = new Intent(getApplicationContext(), Page_Main.class);
                startActivity(intent);

//                // 휴대폰 번호 가져오기
//                id = page_login_edittext_phoneNumber.getText().toString();
//
//                // 서버로 휴대폰 번호 보내기
//                Gson gson = new GsonBuilder().setLenient().create();
//                Retrofit retrofit = new Retrofit.Builder()
//                        .baseUrl("http://172.30.1.22:3001/")
//                        .addConverterFactory(GsonConverterFactory.create(gson))
//                        .build();
//
//                RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
//                AuthObject authObject = new AuthObject(id, "123123");//
//                retrofitAPI.postAuth(authObject).enqueue(new Callback<String>() {
//                                 @Override
//                                 public void onResponse(Call<String> call, Response<String> response) {
//                                     // 회원가입 또는 로그인 확인 되면 고유번호와 함께 메인화면으로 넘어가기
//                                     if (response.body().equals("login") || response.body().equals("signup")) {
//                                         Intent intent = new Intent(getApplicationContext(), Page_Main.class);
//                                         startActivity(intent);
//                                     }
//                                 }
//                                 @Override
//                                 public void onFailure(Call<String> call, Throwable t) {
//                                     System.out.println(t.getMessage());
//                                 }
//                });
            }
        });
    }




}