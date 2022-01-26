package com.rmc.randomchat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.rmc.randomchat.RecyclerChat.ActivityChat;
import com.rmc.randomchat.entity.Room;

import java.util.List;

public class PopupEnterInRoom {
    private Button confirmEnter;
    private EditText stringRoomID;
    private int id;

    @SuppressLint("ClickableViewAccessibility")
    public void showPopupWindow(final View view, ActivityRoom activity) {

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
                try{
                    id = Integer.parseInt(roomID);
                }catch(NumberFormatException e){
                    stringRoomID.setError("Formato errato.");
                    return;
                }

                Room room = findRoomById(id, activity.getListRooms());

                if(room == null){
                    Toast.makeText(v.getContext(),"La stanza #000" + id + " non esiste.",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(activity, ActivityChat.class);
                    intent.putExtra("room", room);
                    activity.startActivity(intent);
                    popupWindow.dismiss();
                }

            }else{
                stringRoomID.setError("Inserisci un numero.");
            }
        });

        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            return true;
        });

    }

    Room findRoomById(int id, List<Room> rooms){
        for (Room r:rooms) {
            if(r.getId() == id)
                return r;
        }

        return null;
    }
}
