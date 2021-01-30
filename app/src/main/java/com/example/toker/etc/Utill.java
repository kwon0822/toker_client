package com.example.toker.etc;

import android.content.Context;
import android.widget.Toast;

public class Utill {

    public void Toast(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }

    public void Print(String string) {
        System.out.println(string);
    }
}
