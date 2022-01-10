package com.rmc.randomchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rmc.randomchat.RecyclerChat.ActivityChat;
import com.rmc.randomchat.entity.Room;
import com.rmc.randomchat.net.CallbackComm;
import com.rmc.randomchat.net.ServerCommImpl;

public class ActivityRoom extends AppCompatActivity implements RoomAdapter.OnRoomListner {

    private RecyclerView rvRoom;
    private LinearLayoutManager layoutManager;
    private TextView titletoolbar;
    private ArrayList<Room> rooms = new ArrayList<>();
    private RoomAdapter adapter;
    private FloatingActionButton buttonnewroom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        setSupportActionBar(findViewById(R.id.toolbar_c));
        titletoolbar =  (TextView)findViewById(R.id.toolbar_title);
        titletoolbar.setText("Random Chat");
        initRecyclerView();
        initData();


        buttonnewroom = findViewById(R.id.buttonNewRoom);
        buttonnewroom.setOnClickListener(v -> {
            PopupNewRoom popUpClass = new PopupNewRoom();
            popUpClass.showPopupWindow(v);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_room, menu);
        return true;
    }

    private void initRecyclerView() {
        rvRoom = findViewById(R.id.rvRoom);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvRoom.setLayoutManager(layoutManager);
        adapter = new RoomAdapter(rooms, this);
        rvRoom.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void initData() {

        CallbackComm.getRooms(20, "", r -> {
            rooms.addAll(r);
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        });

    }

    @Override
    public void OnRoomClick(int position) {
        Intent intent = new Intent(this, ActivityChat.class);
        intent.putExtra("room", rooms.get(position));
        startActivity(intent);
    }
}