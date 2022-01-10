package com.rmc.randomchat.RecyclerChat;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rmc.randomchat.R;
import com.rmc.randomchat.entity.Messages;
import com.rmc.randomchat.entity.Room;
import com.rmc.randomchat.entity.User;
import com.rmc.randomchat.net.CallbackComm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ActivityChat extends AppCompatActivity {

    private EditText mgetmessage;
    private String enteredmessage;
    private CardView msendmessagecardview;
    private ImageButton msendmessagebutton;
    private androidx.appcompat.widget.Toolbar toolbarofspecificchatroom;
    private TextView Nameofspecificroom;
    private ImageButton backbuttonofspecificchatroom;
    private ArrayList<Messages> messagesArrayList = new ArrayList();
    private RecyclerView mmessagerecyclerview;
    private MessagesRecyclerAdapter messagesAdapter;
    private Room selectedRoom;
    private User other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if(getIntent().getExtras() != null){
            selectedRoom = (Room) getIntent().getSerializableExtra("room");
                //TODO attesa utente creare qualcosa di visivo


            CallbackComm.setOnNewMsg(msg -> {
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                Calendar calendar = new GregorianCalendar();
                dateFormat.setTimeZone(calendar.getTimeZone());


                String currenttime = dateFormat.format(calendar.getTime());
                Messages messages = new Messages(msg, false, currenttime);
                messagesArrayList.add(messages);
                runOnUiThread(() -> messagesAdapter.notifyDataSetChanged());
            });

            CallbackComm.setOnNext(() -> {
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                Calendar calendar = new GregorianCalendar();
                dateFormat.setTimeZone(calendar.getTimeZone());


                String currenttime = dateFormat.format(calendar.getTime());
                Messages messages = new Messages("L'utente ha terminato la conversazione", false, currenttime);
                messagesArrayList.add(messages);
            });

            CallbackComm.setOnExit(() -> {
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                Calendar calendar = new GregorianCalendar();
                dateFormat.setTimeZone(calendar.getTimeZone());


                String currenttime = dateFormat.format(calendar.getTime());
                Messages messages = new Messages("L'utente è uscito dalla stanza...", false, currenttime);
                messagesArrayList.add(messages);
                runOnUiThread(() -> messagesAdapter.notifyDataSetChanged());
            });

            CallbackComm.enterRoom(selectedRoom.getId(), user -> {
                other = user;
                CallbackComm.startChatting();


                // TODO levare il messaggio di attesa
            });
        }

        initRecyclerView();
    }

    private void initRecyclerView() {

        mgetmessage=findViewById(R.id.getmessage);
        msendmessagecardview=findViewById(R.id.carviewofsendmessage);
        msendmessagebutton=findViewById(R.id.buttonsendmessage);
        toolbarofspecificchatroom=findViewById(R.id.toolbarofspecificchatroom);
        Nameofspecificroom=findViewById(R.id.Nameofspecificroom);
        Nameofspecificroom.setText(selectedRoom.getName());
        backbuttonofspecificchatroom=findViewById(R.id.backbuttonbackroom);
        mmessagerecyclerview=findViewById(R.id.recyclerviewochat);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessagerecyclerview.setLayoutManager(linearLayoutManager);

        messagesAdapter=new MessagesRecyclerAdapter(ActivityChat.this,messagesArrayList);
        mmessagerecyclerview.setAdapter(messagesAdapter);

        //altra roba

        backbuttonofspecificchatroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallbackComm.sendExit(() -> {});
                finish();
            }
        });

        msendmessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                enteredmessage=mgetmessage.getText().toString();
                if(enteredmessage.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"Inserisci un messaggio!",Toast.LENGTH_SHORT).show();

                } else {
                    CallbackComm.sendMessage(enteredmessage, () -> {});

                    Date date = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                    Calendar calendar = new GregorianCalendar();
                    dateFormat.setTimeZone(calendar.getTimeZone());


                    String currenttime = dateFormat.format(calendar.getTime());
                    Messages messages = new Messages(enteredmessage, true, currenttime);
                    messagesArrayList.add(messages);
                    runOnUiThread(() -> messagesAdapter.notifyDataSetChanged());
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(messagesAdapter!=null)
        {
            messagesAdapter.notifyDataSetChanged();
        }
    }

}
