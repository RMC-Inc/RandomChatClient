package com.rmc.randomchat.RecyclerChat;



import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.rmc.randomchat.LoadingDialog;
import com.rmc.randomchat.R;
import com.rmc.randomchat.entity.Messages;
import com.rmc.randomchat.entity.Room;
import com.rmc.randomchat.entity.User;
import com.rmc.randomchat.net.CallbackComm;
import java.util.ArrayList;


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

            LoadingDialog loadingDialog = new LoadingDialog(ActivityChat.this);
            loadingDialog.startLoadingDialog();

            CallbackComm.setOnNewMsg(msg -> {
                Messages messages = new Messages(msg, false);
                messagesArrayList.add(messages);
                runOnUiThread(() -> messagesAdapter.notifyDataSetChanged());
            });

            CallbackComm.setOnNext(() -> {
                ShowMessage("L'utente ha terminato la conversazione");

                runOnUiThread(() -> messagesAdapter.notifyDataSetChanged());
            });

            CallbackComm.setOnExit(() -> {
                ShowMessage("L'utente è uscito dalla stanza");
                mgetmessage.setText("");

                runOnUiThread(() -> messagesAdapter.notifyDataSetChanged());
            });

            CallbackComm.enterRoom(selectedRoom.getId(), user -> {
                if(user != null){
                    other = user;
                    ShowMessage("Stai chattando con: " + user.getNickname());
                    runOnUiThread(() -> messagesAdapter.notifyDataSetChanged());
                    CallbackComm.startChatting();
                    loadingDialog.CloseLoadingDialog();
                }


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

        backbuttonofspecificchatroom.setOnClickListener(view -> {
            finish();
        });

        msendmessagebutton.setOnClickListener(view -> {

            enteredmessage=mgetmessage.getText().toString();
            if(enteredmessage.isEmpty()) {
                Toast.makeText(getApplicationContext(),"Inserisci un messaggio!",Toast.LENGTH_SHORT).show();

            } else {
                CallbackComm.sendMessage(enteredmessage, () -> {});
                Messages messages = new Messages(enteredmessage, true);
                messagesArrayList.add(messages);
                mgetmessage.setText("");
                scrollToBottom(mmessagerecyclerview);
                runOnUiThread(() -> messagesAdapter.notifyDataSetChanged());
            }
        });

    }

    public void ShowMessage (String Message) {
        AlertDialog alertDialog = new AlertDialog.Builder(ActivityChat.this).create();
        alertDialog.setTitle("Attenzione");
        alertDialog.setMessage(Message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CallbackComm.sendExit(() -> {});
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
}
