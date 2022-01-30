package com.rmc.randomchat;

import android.app.Activity;
import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.provider.Settings;

public class NetworkErrorFactory {

    public enum TYPE{
        TERMINATE,
        CONNECT,
        DO_NOTHING,
    }

    public static AlertDialog newNetworkError(Activity activity, String msg, TYPE type){
        androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(activity);
        builder1.setMessage(msg);
        switch (type){
            case TERMINATE:
                builder1.setPositiveButton("OK", (dialog, id) -> {
                    System.exit(0);
                });
                builder1.setCancelable(false);
                break;
            case CONNECT:
                builder1.setPositiveButton("Connetti", (dialog, id) -> activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
                builder1.setCancelable(true);
                break;
            case DO_NOTHING:
                builder1.setPositiveButton("OK", (dialog, id) -> {});
                builder1.setCancelable(true);
                break;
        }
        return builder1.create();
    }
}
