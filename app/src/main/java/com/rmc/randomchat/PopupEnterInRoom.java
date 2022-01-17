package com.rmc.randomchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class PopupEnterInRoom {
    private Button confirmEnter;
    private EditText stringRoomID;

    @SuppressLint("ClickableViewAccessibility")
    public void showPopupWindow(final View view) {

        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.enterinroom, null);

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

        stringRoomID = popupView.findViewById(R.id.roomid);
        stringRoomID.setText("");

        confirmEnter = popupView.findViewById(R.id.EnterRoom);
        confirmEnter.setOnClickListener(v -> {

            String roomID = stringRoomID.getText().toString();

            if(!roomID.isEmpty()){
                Toast.makeText(v.getContext(),"DEBUG - Il codice della stanza è: " + roomID, Toast.LENGTH_SHORT).show();
                //TODO INVIARE CODICE STANZA AL SERVER
                popupWindow.dismiss();
            }else{
                Toast.makeText(v.getContext(),"Errore! Inserisci un codice valido.",Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });

        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            return true;
        });

    }
}
