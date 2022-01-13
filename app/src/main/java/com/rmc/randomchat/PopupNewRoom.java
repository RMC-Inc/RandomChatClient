package com.rmc.randomchat;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import top.defaults.colorpicker.ColorPickerPopup;

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
                System.out.println("TEST!!!!!");
            }
        });

        buttoncolor = popupView.findViewById(R.id.buttoncolor);
        buttoncolor.setOnClickListener(view1 -> new ColorPickerPopup.Builder(view1.getContext())
                .initialColor(Color.RED)
                .enableBrightness(true)
                .enableAlpha(true)
                .okTitle("Scegli")
                .cancelTitle("Cancella")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int color) {
                        String c = colorHex(color);
                        System.out.println(c);  //Debug, stampa correttamente il valore come stringa del tipo r.g.b (con r, g, b interi compresi tra 0 e 255)

                    }
                }));

        //Clicca in una zona qualsiasi per chiudere il popup

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Chiude la finestra quando si clicca a caso
                popupWindow.dismiss();
                return true;
            }
        });

    }

    private String colorHex(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return r + "." + g + "." + b;
    }

}