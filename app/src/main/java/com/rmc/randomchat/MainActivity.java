package com.rmc.randomchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.rmc.randomchat.entity.User;
import com.rmc.randomchat.net.Server;
import com.rmc.randomchat.net.ServerFunctions;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

     private EditText NicknameUser;
     private Button button_start_chat;
     CheckBox checkBox;
     User user = new User("Guest");
     File f = new File("/data/data/com.rmc.randomchat/shared_prefs/com.rmc.randomchat_preferences.xml");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isConnected();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NicknameUser = findViewById(R.id.name_user);
        checkBox = findViewById(R.id.checkBox_nickname);

        if(f.exists() && NicknameUser.getText().toString().isEmpty()){
            SharedPreferences username = PreferenceManager.getDefaultSharedPreferences(this);
            String name = username.getString("Nick", "");
            if(!name.equalsIgnoreCase(""))
                NicknameUser.setText(name);
            else
                checkBox.setVisibility(View.GONE);
        }

        NicknameUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty())
                    checkBox.setVisibility(View.GONE);
                else
                    checkBox.setVisibility(View.VISIBLE);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        checkBox.setOnClickListener(v -> {
            if(checkBox.isChecked()){
                if (f.exists()){
                    SharedPreferences username = PreferenceManager.getDefaultSharedPreferences(v.getContext());

                    SharedPreferences.Editor editor = username.edit();
                    editor.putString("Nick", NicknameUser.getText().toString());
                    editor.apply();
                }
            }
        });

        button_start_chat = findViewById(R.id.button_start_chat);

        button_start_chat.setOnClickListener(v -> {

            if(!checkBox.isChecked()){
                SharedPreferences username = PreferenceManager.getDefaultSharedPreferences(v.getContext());

                SharedPreferences.Editor editor = username.edit();
                editor.putString("Nick", "");
                editor.apply();
            }

            if(!EditTextisEmpty(NicknameUser)){
                user.setNickname(NicknameUser.getText().toString());
                ActivityRoom.nick = user.getNickname();

                AsyncTask.execute(() -> {
                    try {
                        ServerFunctions.setNickname(user.getNickname());
                        Intent roomRecyclerView = new Intent(MainActivity.this, ActivityRoom.class);
                        startActivity(roomRecyclerView);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Errore di comunicazione", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //  Controlla lo stato della connessione

    private void isConnected() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo.isConnected()) {
            if (!Server.getInstance().isOpen()) {
                AsyncTask.execute(() -> {
                    try {
                        Server.getInstance().openConnection();
                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> showNetworkError("Impossibile connettersi al server, prova a riavviare l'applicazione o ritenta più tardi."));
                    }
                });
            }
        } else showNetworkError("Internet non disponibile, Controlla la tua connessione e riprova");
    }

    //    Mostra un errore in caso di connessione assente

    private void showNetworkError(String msgError) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage(msgError);
        builder1.setCancelable(true);
        if(msgError.equals("Internet non disponibile, Controlla la tua connessione e riprova"))
            builder1.setPositiveButton("Connetti", (dialog, id) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));
        else
            builder1.setPositiveButton("Ok", (dialog, id) -> finish());

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    // Controlla se la EditText è vuota

    public boolean EditTextisEmpty(EditText niknameuser) {
        if (TextUtils.isEmpty(niknameuser.getText())) {
            niknameuser.setError("Nickname non inserito!");
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
    protected void onResume() {
        super.onResume();
        isConnected();
    }

}