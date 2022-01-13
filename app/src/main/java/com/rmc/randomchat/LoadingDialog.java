package com.rmc.randomchat;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;

public class LoadingDialog {

    private Activity activity;
    private AlertDialog dialog;
    private Button button;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflaters = activity.getLayoutInflater();
        View loadingdialog = inflaters.inflate(R.layout.layout_loading, null);

        builder.setView(inflaters.inflate(R.layout.layout_loading, null));
        builder.setCancelable(false);

        button = loadingdialog.findViewById(R.id.button_back_loading);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        dialog = builder.create();
        dialog.show();


    }

    public void CloseLoadingDialog() {
        dialog.dismiss();
    }

}
