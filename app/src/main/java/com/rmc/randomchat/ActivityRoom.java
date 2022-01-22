package com.rmc.randomchat;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rmc.randomchat.RecyclerChat.ActivityChat;
import com.rmc.randomchat.entity.Room;
import com.rmc.randomchat.net.ServerFunctions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ActivityRoom extends AppCompatActivity implements RoomAdapter.OnRoomListner {

    private static final String TAG = "";
    private RecyclerView rvRoom;
    private LinearLayoutManager layoutManager;
    private TextView titletoolbar;
    private RoomAdapter adapter;
    private FloatingActionButton buttonnewroom;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyView1;
    private TextView emptyView2;
    private ImageView emptyView3;
    private View emptyView;
    private AlertDialog dialog;
    private Builder dialogBuilder;
    private List<Room> rooms = new ArrayList<>();
    private TextView curr_nick;
    public static String nick = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        curr_nick = findViewById(R.id.username);
        curr_nick.setText(nick);

        initRecyclerView();


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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected()");
        LayoutInflater inflater = (LayoutInflater) ActivityRoom.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.changenikname, findViewById(R.id.changenikname));

        switch (item.getItemId()) {
            case R.id.changenikname:
                PopupChangeNickname p_change = new PopupChangeNickname();
                p_change.showPopupWindow(view1, curr_nick);
                return true;

            case R.id.changeroomid:
                PopupEnterInRoom p_enter_room = new PopupEnterInRoom();
                p_enter_room.showPopupWindow(view1);
                return true;

            case R.id.exit:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void initRecyclerView() {
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        emptyView1 = findViewById(R.id.empty_view1);
        emptyView2 = findViewById(R.id.empty_view2);
        emptyView3 = findViewById(R.id.ArrowIcon);
        rvRoom = findViewById(R.id.rvRoom);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvRoom.setLayoutManager(layoutManager);
        initData();
        try {
            TimeUnit.MILLISECONDS.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        adapter = new RoomAdapter(this.rooms, this);
        rvRoom.setAdapter(adapter);


        if(adapter.getItemCount() == 0) {
            emptyView1.setVisibility(View.VISIBLE);
            emptyView2.setVisibility(View.VISIBLE);
            emptyView3.setVisibility(View.VISIBLE);
            rvRoom.setVisibility(View.GONE);

        } else {
            emptyView1.setVisibility(View.GONE);
            emptyView2.setVisibility(View.GONE);
            emptyView3.setVisibility(View.GONE);
            rvRoom.setVisibility(View.VISIBLE);
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {
            this.rooms.clear();
            initData();
        });

    }



    private void initData() {

        AsyncTask.execute(() -> {
            try {
                this.rooms.addAll(ServerFunctions.getRooms(0, 20, ""));
                System.out.println("Strunz la size della lista e': " + this.rooms.size());
                runOnUiThread(() -> adapter.notifyDataSetChanged());
                swipeRefreshLayout.setRefreshing(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void OnRoomClick(int position) {
        Intent intent = new Intent(this, ActivityChat.class);
        intent.putExtra("room", adapter.getData().get(position));
        startActivity(intent);
    }

}