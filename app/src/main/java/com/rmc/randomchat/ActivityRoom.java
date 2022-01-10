package com.rmc.randomchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.telecom.Call;
import android.view.Menu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import com.rmc.randomchat.entity.Room;
import com.rmc.randomchat.net.CallbackComm;
import com.rmc.randomchat.net.ServerCommImpl;

public class ActivityRoom extends AppCompatActivity {

    RecyclerView rvRoom;
    LinearLayoutManager layoutManager;
    TextView titletoolbar;
    List<Room> rooms = new ArrayList<>();
    RoomAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        setSupportActionBar(findViewById(R.id.toolbar_c));
        titletoolbar =  (TextView)findViewById(R.id.toolbar_title);
        titletoolbar.setText("Random Chat");
        initRecyclerView();
        initData();
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
        adapter = new RoomAdapter(rooms);
        rvRoom.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void initData() {
        //rooms = ServerCommImpl.getInstance().getRooms(10, "");

        CallbackComm.getRooms(20, "", (r) -> {
            rooms.addAll(r);
            adapter.notifyDataSetChanged();
        });

        // rooms.add(new Room("Amanti della Pizza \uD83C\uDF55", "#6020","10:10","Online:10"));
        // rooms.add(new Room("StarWars \uD83C\uDF89", "#6020","10:10","Online:10"));
        // rooms.add(new Room("Appassionati di musica \uD83C\uDFB6", "#6020","10:10","Online:10"));
        // rooms.add(new Room("Gattini \uD83D\uDC08", "#6020","10:10","Online:10"));
        // rooms.add(new Room("Cani \uD83D\uDC36", "#6020","10:10","Online:10"));

    }

}