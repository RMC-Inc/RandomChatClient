package com.rmc.randomchat;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.rmc.randomchat.entity.Room;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import java.util.function.Consumer;

public class PopupNewRoom  {

    private Button createnewroom;
    private EditText timeEditText;
    private Button buttoncolor;
    private EditText room_name;
    private int color = 0;

    public void showPopupWindow(final View view, Consumer<Room> onCreateRoom) {

        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popupnewroom, null);

        boolean focusable = true;

        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, focusable);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        room_name = popupView.findViewById(R.id.newroomname);

        timeEditText = popupView.findViewById(R.id.newroomtime);


        createnewroom = popupView.findViewById(R.id.createnewroom);
        createnewroom.setOnClickListener(v -> {
            if(color == 0)
                Toast.makeText(v.getContext(),"Seleziona un colore per la stanza.",Toast.LENGTH_SHORT).show();
            else
                if((room_name.getText().toString()).trim().isEmpty())
                    room_name.setError("Inserisci un nome.");
                else if((room_name.getText().toString()).getBytes().length > 30)
                    room_name.setError("Nome troppo lungo.");
                else{
                    int time = 0;
                    try {
                        if (!timeEditText.getText().toString().isEmpty())
                            time = Integer.parseInt(timeEditText.getText().toString());
                        popupWindow.dismiss();
                        onCreateRoom.accept(new Room(room_name.getText().toString(), time, color));
                    } catch (NumberFormatException e){
                        timeEditText.setError("Formato errato");
                    }
                }
        });

        buttoncolor = popupView.findViewById(R.id.buttoncolor);
        buttoncolor.setOnClickListener(view1 -> new ColorPickerDialog.Builder(view1.getContext())
                .setTitle("Seleziona un colore")
                .setPositiveButton(getString(R.string.confirm),
                        (ColorEnvelopeListener) (envelope, fromUser) -> {
                            setLayoutColor();
                            color = envelope.getColor() & 0x00ffffff;
                            buttoncolor.setBackgroundColor(color + 0xff000000);
                        })
                .setNegativeButton(getString(R.string.cancel),
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .attachAlphaSlideBar(false)
                .attachBrightnessSlideBar(false)
                .setBottomSpace(12)
                .show());


        popupView.setOnTouchListener((v, event) -> {
            v.performClick();
            popupWindow.dismiss();
            return true;
        });

    }

    private void setLayoutColor() {
    }

    private int cancel;
    private int getString(int cancel) {
        this.cancel = cancel;
        return cancel;
    }
}