package com.rmc.randomchat.RecyclerChat;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
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
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.extras.backgrounds.RectanglePromptBackground;
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal;


public class ActivityChat extends AppCompatActivity implements ChatListener {

    private static final int AUDIO_RECORD_REQUEST_CODE = 1;
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
    private TextView other_username;
    private TextView time_left;
    private TextView users_in_room;
    private RandomChatRepository randomChatRepository;
    private ExecutorService executorService;
    Thread updateUserCount;
    private boolean chatting = true;
    private Integer uCount = 0;

    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        users_in_room = findViewById(R.id.users_in_room);

        executorService = Executors.newSingleThreadExecutor();
        randomChatRepository = ((MyApplication) getApplication()).appContainer.randomChatRepository;
        selectedRoom = (Room) getIntent().getSerializableExtra("room");
        initRecyclerView();
        loadingDialog();


        AsyncTask.execute(() -> {
            try {
                randomChatRepository.enterRoom(selectedRoom, this);

                updateUserCount = new Thread(() -> {
                    while(chatting){
                        try {
                            randomChatRepository.getUserCount();
                            synchronized (uCount){
                                uCount++;
                                //uCount.notify();
                            }
                            Thread.sleep(3000);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                updateUserCount.start();
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

        toolbarofspecificchatroom.setBackgroundColor(selectedRoom.getRoomColor() + 0xff000000);
        getWindow().setStatusBarColor(selectedRoom.getRoomColor() + 0xff000000);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessagerecyclerview.setLayoutManager(linearLayoutManager);

        messagesAdapter = new MessagesRecyclerAdapter(ActivityChat.this, messagesArrayList);
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
                Messages messages = new Messages(enteredmessage, selectedRoom.getRoomColor(), true);
                messagesArrayList.add(messages);
                mgetmessage.setText("");
                scrollToBottom(mmessagerecyclerview);
                runOnUiThread(() -> messagesAdapter.notifyDataSetChanged());
            }
        });

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matches = bundle
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if (matches != null)
                    mgetmessage.setText(matches.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        mgetmessage.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_LEFT = 0;

            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() >= (mgetmessage.getRight() - mgetmessage.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    loadingDialog();

                    AsyncTask.execute(() -> {
                        try {
                            randomChatRepository.nextUser();
                        } catch (IOException e) {
                            // TODO errore di connessione
                            e.printStackTrace();
                        }
                    });

                    return true;
                }else if(event.getRawX() <= (mgetmessage.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width() + 20)){
                    speechRecognizer.stopListening();
                    mgetmessage.setHint("Messaggio");
                }
            }else if(event.getAction() == MotionEvent.ACTION_DOWN){
                if(event.getRawX() <= (mgetmessage.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width() + 20)){
                    checkAudioPermission();
                    speechRecognizer.startListening(mSpeechRecognizerIntent);
                    mgetmessage.setText("");
                    mgetmessage.setHint("In ascolto, rilascia al termine");
                }

            }
            return false;
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
        chatting = false;

        executorService.execute(() -> {
            try {
                if(updateUserCount != null)
                    updateUserCount.join();
                while(uCount != 0)
                    synchronized (uCount){
                        wait(uCount);
                    }
                randomChatRepository.exitRoom();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public void onUserFound(String otherUsername) {
        checkForFirstTimeUser();
        other_username = findViewById(R.id.chatting_with_name);
        time_left = findViewById(R.id.time_left);
        Messages msg = new Messages("Stai chattando con: " + otherUsername, selectedRoom.getRoomColor(), false);
        messagesArrayList.add(msg);
        runOnUiThread(() -> {
            if(selectedRoom.getTime() == 0)
                time_left.setVisibility(View.GONE);
            else{
                time_left.setVisibility(View.VISIBLE);
                time_left.setText(selectedRoom.getTime() / 60 + ":" + selectedRoom.getTime() % 60);
                countdown(selectedRoom.getTime());
            }
            users_in_room.setText(selectedRoom.getOnlineUsers() + "");
            System.out.println("Debug del cazzo users online: " + selectedRoom.getOnlineUsers());
            other_username.setText("Chatti con: " + otherUsername);
            messagesAdapter.notifyDataSetChanged();
            dialog.dismiss();
        });
    }

    @Override
    public void onMessage(String msg) {
        Messages messages = new Messages(msg, selectedRoom.getRoomColor(), false);
        messagesArrayList.add(messages);
        runOnUiThread(() -> {
            messagesAdapter.notifyDataSetChanged();
            scrollToBottom(mmessagerecyclerview);
        });
    }

    @Override
    public void onNextUser() {
        Messages messages = new Messages("L'utente ha terminato la conversazione.", selectedRoom.getRoomColor(), false);
        messagesArrayList.add(messages);

        runOnUiThread(() -> {
            messagesAdapter.notifyDataSetChanged();
            scrollToBottom(mmessagerecyclerview);
        });
    }

    @Override
    public void onTimeExpired() {
        Messages messages = new Messages("Il tempo per la conversazione è scaduto, sei stato disconesso dall'utente.", selectedRoom.getRoomColor(), false);
        messagesArrayList.add(messages);

        runOnUiThread(() -> {
            messagesAdapter.notifyDataSetChanged();
            scrollToBottom(mmessagerecyclerview);
        });
    }

    @Override
    public void onExit() {
        Messages messages = new Messages("L'utente è uscito dalla stanza.", selectedRoom.getRoomColor(), false);
        messagesArrayList.add(messages);

        runOnUiThread(() -> {
            messagesAdapter.notifyDataSetChanged();
            scrollToBottom(mmessagerecyclerview);
        });
    }

    @Override
    public void onUsersCount(long usersCount) {

        synchronized (uCount){
            uCount--;
        }

        runOnUiThread(() -> {
            users_in_room.setText(usersCount + "");
        });
    }

    private void countdown(int limit){

        new CountDownTimer(limit*1000, 1000){
            String text;

            public void onTick(long millis_limit){
                text = String.format(" %d:%02d", millis_limit/1000/60, millis_limit/1000 %60);
                runOnUiThread(() -> {
                    time_left.setText(text);
                });
            }

            public void onFinish(){
            }
        }.start();
    }

    private void checkAudioPermission() {
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_RECORD_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AUDIO_RECORD_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    Toast.makeText(this, "Per usare la digitazione vocale vai nelle impostazioni del sistema e concedi l'autorizzazione al microfono.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void checkForFirstTimeUser(){
        final String PREFS_NAME = "PrefFile_chat";

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if(settings.getBoolean("my_first_time", true)){

            runOnUiThread(() -> {
                new MaterialTapTargetPrompt.Builder(this)
                        .setTarget(R.id.getmessage)
                        .setCaptureTouchEventOutsidePrompt(true)
                        .setCaptureTouchEventOnFocal(true)
                        .setPrimaryText("Scrivi qui il tuo messaggio oppure tieni premuto l'icona del microfono per attivare la dettatura!")
                        .setSecondaryText("Clicca il pulsante sulla destra per ricercare un nuovo utente.")
                        .setPromptBackground(new RectanglePromptBackground())
                        .setPromptFocal(new RectanglePromptFocal())
                        .show();
            });


            settings.edit().putBoolean("my_first_time", false).apply();
        }
    }

}