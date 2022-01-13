package com.rmc.randomchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rmc.randomchat.RecyclerChat.ActivityChat;
import com.rmc.randomchat.entity.Room;
import com.rmc.randomchat.net.CallbackComm;


public class ActivityRoom extends AppCompatActivity implements RoomAdapter.OnRoomListner {

    private RecyclerView rvRoom;
    private LinearLayoutManager layoutManager;
    private TextView titletoolbar;
    private ArrayList<Room> rooms = new ArrayList<>();
    private RoomAdapter adapter;
    private FloatingActionButton buttonnewroom;
    private TextView emptyView1;
    private TextView emptyView2;
    private ImageView emptyView3;
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

        emptyView1 = (TextView) findViewById(R.id.empty_view1);
        emptyView2 = (TextView) findViewById(R.id.empty_view2);
        emptyView3 = (ImageView) findViewById(R.id.ArrowIcon);
        rvRoom = findViewById(R.id.rvRoom);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvRoom.setLayoutManager(layoutManager);
        adapter = new RoomAdapter(rooms, this);
        rvRoom.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if(rooms.isEmpty()) {
            rvRoom.setVisibility(View.GONE);
            emptyView1.setVisibility(View.VISIBLE);
            emptyView2.setVisibility(View.VISIBLE);
            emptyView3.setVisibility(View.VISIBLE);

        } else {
            rvRoom.setVisibility(View.VISIBLE);
            emptyView1.setVisibility(View.GONE);
            emptyView2.setVisibility(View.GONE);
            emptyView3.setVisibility(View.GONE);
        }
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