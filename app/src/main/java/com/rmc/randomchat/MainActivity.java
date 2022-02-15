package com.rmc.randomchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.rmc.randomchat.depinjection.MyApplication;
import com.rmc.randomchat.net.RandomChatRepository;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

     private EditText NicknameUser;
     private Button button_start_chat;
     CheckBox checkBox;
     RandomChatRepository randomChatRepository = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isConnected();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NicknameUser = findViewById(R.id.name_user);
        checkBox = findViewById(R.id.checkBox_nickname);
        checkBox.setVisibility(View.VISIBLE);


        SharedPreferences username = PreferenceManager.getDefaultSharedPreferences(this);
        String name = username.getString("Nick", "");
        if(name != null && !name.equals("")) {
            checkBox.setEnabled(true);
            checkBox.setAlpha(1.0f);
            NicknameUser.setText(name);
        }else{
            checkBox.setEnabled(false);
            checkBox.setAlpha(0.4f);
        }


        NicknameUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(checkBox.isChecked()) {
                    SharedPreferences.Editor editor = username.edit();
                    editor.putString("Nick", NicknameUser.getText().toString());
                    editor.apply();
                }
                if(NicknameUser.getText().toString().isEmpty()){
                    runOnUiThread(() -> {
                        if(checkBox.isChecked())
                            checkBox.toggle();
                        checkBox.setEnabled(false);
                        checkBox.setAlpha(0.4f);
                    });

                }else
                    runOnUiThread(() -> {
                        checkBox.setEnabled(true);
                        checkBox.setAlpha(1.0f);
                    });
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        checkBox.setOnClickListener(v -> {
            SharedPreferences.Editor editor = username.edit();
            if(checkBox.isChecked())
                editor.putString("Nick", NicknameUser.getText().toString());
            editor.apply();
        });

        button_start_chat = findViewById(R.id.button_start_chat);

        button_start_chat.setOnClickListener(v -> {

            SharedPreferences.Editor editor = username.edit();

            if(!EditTextisEmpty(NicknameUser)){
                if(!checkBox.isChecked()){
                    editor.remove("Nick");
                    editor.apply();
                }
                String nickname = NicknameUser.getText().toString();
                if (nickname.getBytes().length > 20){
                    NicknameUser.setError("Nickname Troppo lungo");
                    return;
                } else if(nickname.trim().isEmpty()){
                    NicknameUser.setError("Inserisci un Nickname");
                    return;
                }
                if(randomChatRepository != null){
                    AsyncTask.execute(() -> {
                        try {
                            randomChatRepository.setNickname(nickname);

                            Intent roomRecyclerView = new Intent(MainActivity.this, ActivityRoom.class);
                            roomRecyclerView.putExtra("nickname", nickname);

                            startActivity(roomRecyclerView);
                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> NetworkErrorFactory.newNetworkError(this, "Errore di connessione al server, riavvia l'applicazione", NetworkErrorFactory.TYPE.TERMINATE).show());
                        }
                    });
                } else {
                    isConnected();
                }
            }
        });
    }


    private void isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            if(networkInfo.isConnected()) {
                if (randomChatRepository == null) {
                    randomChatRepository = ((MyApplication) getApplication()).appContainer.randomChatRepository;
                    AsyncTask.execute(() -> {
                        try {
                            randomChatRepository.connect();
                        } catch (IOException e) {
                            e.printStackTrace();
                            randomChatRepository = null;
                            runOnUiThread(() -> NetworkErrorFactory.newNetworkError(this, "Impossibile connettersi al server, prova a riavviare l'applicazione o ritenta più tardi.", NetworkErrorFactory.TYPE.TERMINATE).show());
                        }
                    });
                }
            }
        } else {
            NetworkErrorFactory.newNetworkError(this, "Internet non disponibile, Controlla la tua connessione e riprova", NetworkErrorFactory.TYPE.CONNECT).show();
        }
    }


    public boolean EditTextisEmpty(EditText nicknameuser) {
        if (TextUtils.isEmpty(nicknameuser.getText())) {
            nicknameuser.setError("Nickname non inserito!");
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