package com.rmc.randomchat;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import entity.User;

public class MainActivity extends AppCompatActivity {

     private EditText NiknameUser;
     private Button button_start_chat;
     User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        NiknameUser = (EditText) findViewById(R.id.name_user);
        button_start_chat = (Button) findViewById(R.id.button_start_chat);

        button_start_chat.setOnClickListener(new View.OnClickListener()

        {
            public void onClick(View v) {
                user.setNickname(NiknameUser.getText().toString());
                EditTextisEmpty(NiknameUser);
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
        builder1.setPositiveButton("Connetti", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    // Controlla se la EditText è vuota

    private void EditTextisEmpty(EditText niknameuser) {
        if(TextUtils.isEmpty(user.getNickname()))
            niknameuser.setError("Nikname non inserito!");
        else
            Toast.makeText(MainActivity.this, "Non so ancora che madonna fare \uD83D\uDE05 " + user.getNickname(),Toast.LENGTH_LONG).show();
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