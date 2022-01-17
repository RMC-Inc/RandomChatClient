package com.rmc.randomchat;


import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

public class PopupNewRoom  {

    private Button createnewroom;
    private Button buttoncolor;

    public void showPopupWindow(final View view) {

        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popupnewroom, null);

        //Per rendere elementi inattivi al di fuori della finestra popup
        boolean focusable = true;

        //creiamo una classe PopupWindow con i parametri personalizzati
        final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, focusable);

        //settiamo il popup al centro dello schermo
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


        //Inizializzare tutti gli elementi da visualizzare nel popup

        createnewroom = popupView.findViewById(R.id.createnewroom);
        createnewroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO INVIA COMANDO AL SERVER PER CREARE LA STANZA
            }
        });
        //Clicca in una zona qualsiasi per chiudere il popup

        buttoncolor = popupView.findViewById(R.id.buttoncolor);
        buttoncolor.setOnClickListener(view1 -> new ColorPickerDialog.Builder(view1.getContext())
                .setTitle("Seleziona un colore")
                .setPositiveButton(getString(R.string.confirm),
                        (ColorEnvelopeListener) (envelope, fromUser) -> {
                            setLayoutColor(envelope);
                            envelope.getColor(); // restituisce un colore in formato intero

                        })
                .setNegativeButton(getString(R.string.cancel),
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .setBottomSpace(12)
                .show());


        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Chiude la finestra quando si clicca a caso
                popupWindow.dismiss();
                return true;
            }
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