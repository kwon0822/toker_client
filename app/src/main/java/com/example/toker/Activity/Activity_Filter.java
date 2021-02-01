package com.example.toker.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class Activity_Filter extends Activity {

    String id = Activity_Login.myID;
    private Socket socket;

    private boolean isMatch = false; // matchOn, matchOff

    boolean isLevel1 = false;
    boolean isLevel2 = false;
    boolean isLevel3 = false;
    boolean isLevel4 = false;

    Button activity_filter_filter_button_back;
    Button activity_filter_filter_button_match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_filter);
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        socket.on("matchOn", matchOn);
        socket.on("matchOff", matchOff);
    }

    @Override
    protected void onStop() {
        super.onStop();

        

        socket.off("matchOn", matchOn);
        socket.off("matchOff", matchOff);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

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

                Button activity_filter_filter_button_level1 = findViewById(R.id.activity_filter_button_level1);
                TextView activity_filter_filter_textview_level1 = findViewById(R.id.activity_filter_textview_level1);
                activity_filter_filter_button_level1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isLevel1 = !isLevel1;
                        if (isLevel1) {
                            activity_filter_filter_textview_level1.setText("선택완료");
                            activity_filter_filter_textview_level1.setTextColor(Color.BLUE);
                        } else {
                            activity_filter_filter_textview_level1.setText("선택가능");
                            activity_filter_filter_textview_level1.setTextColor(Color.RED);
                        }
                    }
                });

                Button activity_filter_filter_button_level2 = findViewById(R.id.activity_filter_button_level2);
                TextView activity_filter_filter_textview_level2 = findViewById(R.id.activity_filter_textview_level2);
                activity_filter_filter_button_level2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isLevel2 = !isLevel2;
                        if (isLevel2) {
                            activity_filter_filter_textview_level2.setText("선택완료");
                            activity_filter_filter_textview_level2.setTextColor(Color.BLUE);
                        } else {
                            activity_filter_filter_textview_level2.setText("선택가능");
                            activity_filter_filter_textview_level2.setTextColor(Color.RED);
                        }
                    }
                });

                Button activity_filter_filter_button_level3 = findViewById(R.id.activity_filter_button_level3);
                TextView activity_filter_filter_textview_level3 = findViewById(R.id.activity_filter_textview_level3);
                activity_filter_filter_button_level3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isLevel3 = !isLevel3;
                        if (isLevel3) {
                            activity_filter_filter_textview_level3.setText("선택완료");
                            activity_filter_filter_textview_level3.setTextColor(Color.BLUE);
                        } else {
                            activity_filter_filter_textview_level3.setText("선택가능");
                            activity_filter_filter_textview_level3.setTextColor(Color.RED);
                        }
                    }
                });

                Button activity_filter_filter_button_level4 = findViewById(R.id.activity_filter_button_level4);
                TextView activity_filter_filter_textview_level4 = findViewById(R.id.activity_filter_textview_level4);
                activity_filter_filter_button_level4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isLevel4 = !isLevel4;
                        if (isLevel4) {
                            activity_filter_filter_textview_level4.setText("선택완료");
                            activity_filter_filter_textview_level4.setTextColor(Color.BLUE);
                        } else {
                            activity_filter_filter_textview_level4.setText("선택가능");
                            activity_filter_filter_textview_level4.setTextColor(Color.RED);
                        }
                    }
                });

                switch (chatLevel) {
                    case "광고의심":
                        activity_filter_filter_button_level2.setEnabled(false);
                        activity_filter_filter_textview_level2.setText("선택불가");
                        activity_filter_filter_button_level3.setEnabled(false);
                        activity_filter_filter_textview_level3.setText("선택불가");
                        activity_filter_filter_button_level4.setEnabled(false);
                        activity_filter_filter_textview_level4.setText("선택불가");
                        break;
                    case "변태의심":
                        activity_filter_filter_button_level3.setEnabled(false);
                        activity_filter_filter_textview_level3.setText("선택불가");
                        activity_filter_filter_button_level4.setEnabled(false);
                        activity_filter_filter_textview_level4.setText("선택불가");
                        break;
                    case "일반대화":
                        activity_filter_filter_button_level4.setEnabled(false);
                        activity_filter_filter_textview_level4.setText("선택불가");
                        break;
                    case "깊은대화":
                        break;
                }

                activity_filter_filter_button_back = findViewById(R.id.activity_filter_button_back);
                activity_filter_filter_button_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isMatch) {
                            socket.emit("matchOff");
                            isMatch = false;
                        }

                        finish();
                    }
                });

                activity_filter_filter_button_match = findViewById(R.id.activity_filter_button_match);
                activity_filter_filter_button_match.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!isMatch) {
                            int isLevel1INT = Boolean.compare(isLevel1, false);
                            int isLevel2INT = Boolean.compare(isLevel2, false);
                            int isLevel3INT = Boolean.compare(isLevel3, false);
                            int isLevel4INT = Boolean.compare(isLevel4, false);

                            String matchCode = String.valueOf(isLevel1INT + isLevel2INT + isLevel3INT + isLevel4INT);
                            socket.emit("matchOn", chatLevel + "@" + matchCode);

                        } else {
                            socket.emit("matchOff");
                        }
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
                        isMatch = true;
                        Toast.makeText(getApplicationContext(), "매칭을 기다리고 있습니다. 잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();

                        activity_filter_filter_button_match.setText("매칭취소");

                    } else {

                        Activity_Login.yourID = message;

                        isMatch = false;
                        finish();

                        Intent intent = new Intent(getApplicationContext(), Activity_Chat.class);
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
                    isMatch = false;
                    Toast.makeText(getApplicationContext(), "매칭을 취소하셨습니다.", Toast.LENGTH_SHORT).show();

                    activity_filter_filter_button_match.setText("매칭시작");
                }
            });
        }
    };

    // endregion  ==================================================================================
}
