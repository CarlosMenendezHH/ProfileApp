package com.example.userprofile.Clases;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.example.userprofile.R;

public class Progress {
    private Activity myActivity;
    private AlertDialog alertDialog;

    public Progress(Activity activity) {
        myActivity = activity;
    }

    public void startProgressDialog() {
        if (myActivity != null) {
            LayoutInflater inflater = myActivity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.activity_progress, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(myActivity);
            builder.setView(dialogView);
            builder.setCancelable(false);
            alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public void dismissDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }
}
