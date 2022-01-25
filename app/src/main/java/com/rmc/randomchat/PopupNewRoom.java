package com.rmc.randomchat;


import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TimePicker;
import android.widget.Toast;

import com.rmc.randomchat.entity.Room;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import mobi.upod.timedurationpicker.TimeDurationPicker;

public class PopupNewRoom  {

    private Button createnewroom;
    private Button buttontime;
    private Button buttoncolor;
    //private TimeDurationPicker t_duration;
    private EditText room_name;
    private int color = 0;
    private int time = 0;

    public void showPopupWindow(final View view) {

        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popupnewroom, null);

        boolean focusable = true;

        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, focusable);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        room_name = popupView.findViewById(R.id.newroomname);

        //t_duration = popupView.findViewById(R.id.timeDurationInput);

        buttontime = popupView.findViewById(R.id.newroomtime);
        buttontime.setOnClickListener(v -> {
            time = (int) (Math.random() * 50);
        });

        /*buttontime.setOnClickListener(v -> {
            buttontime.setVisibility(View.GONE);
            buttoncolor.setVisibility(View.GONE);
            t_duration.setVisibility(View.VISIBLE);
            //todo vedere come chiuderlo
        });*/



        createnewroom = popupView.findViewById(R.id.createnewroom);
        createnewroom.setOnClickListener(v -> {
            if(color == 0)
                Toast.makeText(v.getContext(),"Seleziona un colore per la stanza.",Toast.LENGTH_SHORT).show();
            else
                if((room_name.getText().toString()).trim().isEmpty())
                    room_name.setError("Inserisci un nome.");
                else if((room_name.getText().toString()).getBytes().length >30)
                    room_name.setError("Il nome non deve superare i 30 caratteri.");
                else{
                    Room r = new Room(room_name.getText().toString(), time, color);
                    //todo invia r al server
                }
        });

        //Room room = new Room();
        buttoncolor = popupView.findViewById(R.id.buttoncolor);
        buttoncolor.setOnClickListener(view1 -> new ColorPickerDialog.Builder(view1.getContext())
                .setTitle("Seleziona un colore")
                .setPositiveButton(getString(R.string.confirm),
                        (ColorEnvelopeListener) (envelope, fromUser) -> {
                            setLayoutColor(envelope);
                            color = envelope.getColor();
                        })
                .setNegativeButton(getString(R.string.cancel),
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .setBottomSpace(12)
                .show());


        popupView.setOnTouchListener((v, event) -> {
            v.performClick();
            popupWindow.dismiss();
            return true;
        });

    }

    private void setLayoutColor(ColorEnvelope envelope) {
    }

    private int cancel;
    private int getString(int cancel) {
        this.cancel = cancel;
        return cancel;
    }

}