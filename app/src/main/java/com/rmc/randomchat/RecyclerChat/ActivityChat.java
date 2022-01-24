package com.rmc.randomchat.RecyclerChat;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.rmc.randomchat.R;
import com.rmc.randomchat.depinjection.MyApplication;
import com.rmc.randomchat.entity.Messages;
import com.rmc.randomchat.entity.Room;
import com.rmc.randomchat.net.ChatListener;
import com.rmc.randomchat.net.RandomChatRepository;
import com.rmc.randomchat.net.RoomNotExistsException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;


public class ActivityChat extends AppCompatActivity implements ChatListener {

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
    private AlertDialog dialog;
    private Button button;

    private RandomChatRepository randomChatRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        randomChatRepository = ((MyApplication) getApplication()).appContainer.randomChatRepository;
        selectedRoom = (Room) getIntent().getSerializableExtra("room");
        initRecyclerView();
        loadingDialog();

        AsyncTask.execute(() -> {
            try {
                randomChatRepository.enterRoom(selectedRoom, this);
            } catch (IOException e) {
                e.printStackTrace();
                // TODO Errore di connessione
                Toast.makeText(getApplicationContext(), "Errore di connessione", Toast.LENGTH_LONG).show();
            } catch (RoomNotExistsException e){
                e.printStackTrace();
                // TODO Errore Stanza non esiste
            }
        });
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

        backbuttonofspecificchatroom.setOnClickListener(view -> {
            finish();
        });

        msendmessagebutton.setOnClickListener(view -> {

            enteredmessage=mgetmessage.getText().toString();
            if(enteredmessage.isEmpty()) {
                Toast.makeText(getApplicationContext(),"Inserisci un messaggio!",Toast.LENGTH_SHORT).show();

            } else {
                AsyncTask.execute(() -> {
                    try {
                        randomChatRepository.sendMessage(enteredmessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                        // TODO errore di connessione
                    }
                });
                Messages messages = new Messages(enteredmessage, true);
                messagesArrayList.add(messages);
                mgetmessage.setText("");
                scrollToBottom(mmessagerecyclerview);
                runOnUiThread(() -> messagesAdapter.notifyDataSetChanged());
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
        if(messagesAdapter!=null) {
            messagesAdapter.notifyDataSetChanged();
        }
    }

    private void scrollToBottom(RecyclerView r){
        final LinearLayoutManager lm = (LinearLayoutManager) r.getLayoutManager();
        final RecyclerView.Adapter adapter = r.getAdapter();
        final int lastItemPosition = adapter.getItemCount() - 1;

        lm.scrollToPositionWithOffset(lastItemPosition, 0);

        r.post(() -> {
            View target = lm.findViewByPosition(lastItemPosition);
            if(target != null){
                int offset = r.getMeasuredHeight() - target.getMeasuredHeight();
                lm.scrollToPositionWithOffset(lastItemPosition, offset);
            }
        });
    }

    private void loadingDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflaters = this.getLayoutInflater();
        builder.setView(inflaters.inflate(R.layout.layout_loading, null));
        builder.setCancelable(false);

        builder.setPositiveButton(
                "Esci",
                (dialog, id) -> {
                    finish();
                    dialog.cancel();
                });

        dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                randomChatRepository.exitRoom();
            } catch (IOException e) {
                e.printStackTrace();
                // Todo Errore di connessione
            }
        });
    }

    @Override
    public void onUserFound(String otherUsername) {
        Messages msg = new Messages("Stai chattando con: " + otherUsername, false);
        messagesArrayList.add(msg);
        runOnUiThread(() -> {
            messagesAdapter.notifyDataSetChanged();
            dialog.dismiss();
        });
    }

    @Override
    public void onMessage(String msg) {
        Messages messages = new Messages(msg, false);
        messagesArrayList.add(messages);
        runOnUiThread(() -> {
            messagesAdapter.notifyDataSetChanged();
            scrollToBottom(mmessagerecyclerview);
        });
    }

    @Override
    public void onNextUser() {
        Messages messages = new Messages("L'utente ha terminato la conversazione.", false);
        messagesArrayList.add(messages);

        runOnUiThread(() -> {
            messagesAdapter.notifyDataSetChanged();
            scrollToBottom(mmessagerecyclerview);
        });
    }

    @Override
    public void onTimeExpired() {
        Messages messages = new Messages("Il tempo per la conversazione è scaduto, sei stato disconesso dall'utente.", false);
        messagesArrayList.add(messages);

        runOnUiThread(() -> {
            messagesAdapter.notifyDataSetChanged();
            scrollToBottom(mmessagerecyclerview);
        });
    }

    @Override
    public void onExit() {
        Messages messages = new Messages("L'utente è uscito dalla stanza.", false);
        messagesArrayList.add(messages);

        runOnUiThread(() -> {
            messagesAdapter.notifyDataSetChanged();
            scrollToBottom(mmessagerecyclerview);
        });
    }
}