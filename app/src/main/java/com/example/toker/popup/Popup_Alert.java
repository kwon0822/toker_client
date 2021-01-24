package com.example.toker.popup;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;

import com.example.toker.R;

public class Popup_Alert {

    Dialog popup_alert;

    public void SetDialog(Context context) {
        popup_alert = new Dialog(context);
        popup_alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popup_alert.setContentView(R.layout.popup_alert);
    }

    public void SetTitle(String title) {
        TextView popup_alert_textview_title = popup_alert.findViewById(R.id.popup_alert_textview_title);
        popup_alert_textview_title.setText(title);
    }

    public void SetDescription(String description) {
        TextView popup_alert_textview_description = popup_alert.findViewById(R.id.popup_alert_textview_description);
        popup_alert_textview_description.setText(description);
    }

}
