package com.example.toker.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.toker.R;

public class Activity_Post extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_post);

        initialize();
    }

    public void initialize() {

        Intent intent = getIntent();
        String postNo = intent.getExtras().getString("postNo");

        Toast.makeText(getApplicationContext(), postNo, Toast.LENGTH_SHORT).show();
    }
}
