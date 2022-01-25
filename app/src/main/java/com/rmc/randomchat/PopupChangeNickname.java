package com.rmc.randomchat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.rmc.randomchat.net.RandomChatRepository;

import java.io.IOException;

public class PopupChangeNickname {
    private Button confirmChange;
    private EditText stringNick;

    @SuppressLint("ClickableViewAccessibility")
    public void showPopupWindow(final View view, TextView curr_nick, Activity activity, RandomChatRepository randomChatRepository) {


        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.changenikname, null);

        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);

        switch (popupView.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:   //Eventualmente si può cambiare il bg per il tema scuro
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
                popupWindow.getBackground().setAlpha(128);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
                popupWindow.getBackground().setAlpha(128);
                break;
        }

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        stringNick = popupView.findViewById(R.id.newnickname);
        stringNick.setText("");


        confirmChange = popupView.findViewById(R.id.apply);
        confirmChange.setOnClickListener(v -> {

            String newNick = stringNick.getText().toString();

            if(!newNick.isEmpty()){
                curr_nick.setText(newNick);
                AsyncTask.execute(() -> {
                    try {
                        randomChatRepository.setNickname(newNick);
                        activity.runOnUiThread(() -> Toast.makeText(v.getContext(),"Nickname cambiato in " + newNick + "!",Toast.LENGTH_SHORT).show());
                    } catch (IOException e) {
                        e.printStackTrace();
                        activity.runOnUiThread(() -> Toast.makeText(v.getContext(),"Errore di comunicazione",Toast.LENGTH_SHORT).show());
                    }
                });
                popupWindow.dismiss();
            }else{
                curr_nick.setError("Inserisci un nickname valido.");
            }
        });

        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            return true;
        });

    }
}
