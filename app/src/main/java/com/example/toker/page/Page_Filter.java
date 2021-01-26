package com.example.toker.page;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.toker.R;
import com.example.toker.http.RetrofitAPI;
import com.example.toker.tcp.SocketAPI;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Page_Filter extends Activity {

    String id = Page_Login.myID;
    private Socket socket;

    private boolean isMatch = false; // 매칭성사, 매칭취소, 매칭중앱강제종료

    boolean isLevel1 = false;
    boolean isLevel2 = false;
    boolean isLevel3 = false;
    boolean isLevel4 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.page_filter);
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();

        socket.on("matchOn", matchOn);
        socket.on("matchOff", matchOff);
        socket.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        socket.off("matchOn", matchOn);
        socket.off("matchOff", matchOff);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        if (isMatch) {
//            socket.emit("matchOff");
//            isMatch = false;
//        }
//
//        socket.disconnect();
//        socket.close();
//    }

    public void initialize() {

        SocketAPI socketAPI = (SocketAPI) getApplication();
        socket = socketAPI.getSocket();

        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitAPI.url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        retrofitAPI.PostLevel(id).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                System.out.println(response.body());

                int chatTime = Integer.parseInt(response.body().split("@")[0]);
                String chatLevel = response.body().split("@")[1];

                Button page_filter_filter_button_level1 = findViewById(R.id.page_filter_button_level1);
                TextView page_filter_filter_textview_level1 = findViewById(R.id.page_filter_textview_level1);
                page_filter_filter_button_level1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isLevel1 = !isLevel1;
                        if (isLevel1) {
                            page_filter_filter_textview_level1.setText("선택완료");
                            page_filter_filter_textview_level1.setTextColor(Color.BLUE);
                        } else {
                            page_filter_filter_textview_level1.setText("선택가능");
                            page_filter_filter_textview_level1.setTextColor(Color.RED);
                        }
                    }
                });

                Button page_filter_filter_button_level2 = findViewById(R.id.page_filter_button_level2);
                TextView page_filter_filter_textview_level2 = findViewById(R.id.page_filter_textview_level2);
                page_filter_filter_button_level2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isLevel2 = !isLevel2;
                        if (isLevel2) {
                            page_filter_filter_textview_level2.setText("선택완료");
                            page_filter_filter_textview_level2.setTextColor(Color.BLUE);
                        } else {
                            page_filter_filter_textview_level2.setText("선택가능");
                            page_filter_filter_textview_level2.setTextColor(Color.RED);
                        }
                    }
                });

                Button page_filter_filter_button_level3 = findViewById(R.id.page_filter_button_level3);
                TextView page_filter_filter_textview_level3 = findViewById(R.id.page_filter_textview_level3);
                page_filter_filter_button_level3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isLevel3 = !isLevel3;
                        if (isLevel3) {
                            page_filter_filter_textview_level3.setText("선택완료");
                            page_filter_filter_textview_level3.setTextColor(Color.BLUE);
                        } else {
                            page_filter_filter_textview_level3.setText("선택가능");
                            page_filter_filter_textview_level3.setTextColor(Color.RED);
                        }
                    }
                });

                Button page_filter_filter_button_level4 = findViewById(R.id.page_filter_button_level4);
                TextView page_filter_filter_textview_level4 = findViewById(R.id.page_filter_textview_level4);
                page_filter_filter_button_level4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isLevel4 = !isLevel4;
                        if (isLevel4) {
                            page_filter_filter_textview_level4.setText("선택완료");
                            page_filter_filter_textview_level4.setTextColor(Color.BLUE);
                        } else {
                            page_filter_filter_textview_level4.setText("선택가능");
                            page_filter_filter_textview_level4.setTextColor(Color.RED);
                        }
                    }
                });

                switch (chatLevel) {
                    case "광고의심":
                        page_filter_filter_button_level2.setEnabled(false);
                        page_filter_filter_textview_level2.setText("선택불가");
                        page_filter_filter_button_level3.setEnabled(false);
                        page_filter_filter_textview_level3.setText("선택불가");
                        page_filter_filter_button_level4.setEnabled(false);
                        page_filter_filter_textview_level4.setText("선택불가");
                        break;
                    case "변태의심":
                        page_filter_filter_button_level3.setEnabled(false);
                        page_filter_filter_textview_level3.setText("선택불가");
                        page_filter_filter_button_level4.setEnabled(false);
                        page_filter_filter_textview_level4.setText("선택불가");
                        break;
                    case "일반대화":
                        page_filter_filter_button_level4.setEnabled(false);
                        page_filter_filter_textview_level4.setText("선택불가");
                        break;
                    case "깊은대화":
                        break;
                }

                // button : 뒤로가기
                Button page_filter_filter_button_back = findViewById(R.id.page_filter_button_back);
                page_filter_filter_button_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // emit : 'matchOff', 매칭 취소하기
                        if (isMatch) {
                            socket.emit("matchOff");
                            isMatch = false;
                        }

                        finish();
                    }
                });

                // button : 매칭시작
                Button page_filter_filter_button_matchingStart = findViewById(R.id.page_filter_button_matchingStart);
                page_filter_filter_button_matchingStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int isLevel1INT = Boolean.compare(isLevel1, false);
                        int isLevel2INT = Boolean.compare(isLevel2, false);
                        int isLevel3INT = Boolean.compare(isLevel3, false);
                        int isLevel4INT = Boolean.compare(isLevel4, false);

                        String matchCode = Integer.toString(isLevel1INT) + Integer.toString(isLevel2INT) + Integer.toString(isLevel3INT) + Integer.toString(isLevel4INT);

                        // 채팅 명단에 이름 올리기
                        isMatch = true;
                        socket.emit("matchOn", chatLevel + "@" + matchCode);
                    }
                });
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    // region listen ===============================================================================

    private Emitter.Listener matchOn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String message = args[0].toString();

                    if (message.equals("wait")) {
                        Toast.makeText(getApplicationContext(), "matchOn : wait", Toast.LENGTH_SHORT).show();

                    } else {

                        Page_Login.yourID = message;

                        isMatch = false;
                        finish();

                        Intent intent = new Intent(getApplicationContext(), Page_Chat.class);
                        startActivity(intent);
                    }
                }
            });
        }
    };

    private Emitter.Listener matchOff = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "matchOff", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    // endregion  ==================================================================================
}
