package com.rmc.randomchat;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.telecom.Call;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import com.rmc.randomchat.entity.User;
import com.rmc.randomchat.net.CallbackComm;
import com.rmc.randomchat.net.ServerCommImpl;

public class MainActivity extends AppCompatActivity {

     private EditText NiknameUser;
     private Button button_start_chat;
     User user = new User("Guest");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NiknameUser = (EditText) findViewById(R.id.name_user);
        button_start_chat = (Button) findViewById(R.id.button_start_chat);

        button_start_chat.setOnClickListener(v -> {

            if(!EditTextisEmpty(NiknameUser)){
                user.setNickname(NiknameUser.getText().toString());
                ServerCommImpl.user.setNickname(user.getNickname());

                if(!ServerCommImpl.isClosed()){
                    CallbackComm.setNickname(user.getNickname(), () -> {});
                }

                //CallbackComm.setNickname(user.getNickname(), () -> runOnUiThread(() -> {
                Intent roomRecyclerView = new Intent(MainActivity.this, ActivityRoom.class);
                startActivity(roomRecyclerView);
                //}));
            }
        });

    }

    //  Controlla lo stato della connessione

    private void isConnected() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null) {
            if(networkInfo.isConnected()) {
            }
            else
                showNetworkError();

        } else
            showNetworkError();

    }

    //    Mostra un errore in caso di connessione assente

    private void showNetworkError() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage("Internet non disponibile, Controlla la tua connessione e riprova");
        builder1.setCancelable(true);
        builder1.setPositiveButton("Connetti", (dialog, id) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    // Controlla se la EditText è vuota

    public boolean EditTextisEmpty(EditText niknameuser) {
        if (TextUtils.isEmpty(niknameuser.getText())) {
            niknameuser.setError("Nikname non inserito!");
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isConnected();

    }

    @Override
    protected void onPause() {
        super.onPause();
        isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isConnected();
    }

}