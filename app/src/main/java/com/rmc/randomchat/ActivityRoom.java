package com.rmc.randomchat;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rmc.randomchat.RecyclerChat.ActivityChat;
import com.rmc.randomchat.entity.Room;
import com.rmc.randomchat.entity.User;
import com.rmc.randomchat.net.CallbackComm;


public class ActivityRoom extends AppCompatActivity implements RoomAdapter.OnRoomListner {

    private static final String TAG = "";
    private RecyclerView rvRoom;
    private LinearLayoutManager layoutManager;
    private TextView titletoolbar;
    private ArrayList<Room> rooms = new ArrayList<>();
    private RoomAdapter adapter;
    private FloatingActionButton buttonnewroom;
    // private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyView1;
    private TextView emptyView2;
    private ImageView emptyView3;
    private View emptyView;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText nikname;
    private Button buttonapply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected()");

        switch (item.getItemId()) {
            case R.id.changenikname:
                changenikname();

            case R.id.changeroomid:
                return true;

            case R.id.exit:
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void initRecyclerView() {
        // swipeRefreshLayout = findViewById(R.id.swiperefresh);
        emptyView1 = (TextView) findViewById(R.id.empty_view1);
        emptyView2 = (TextView) findViewById(R.id.empty_view2);
        emptyView3 = (ImageView) findViewById(R.id.ArrowIcon);
        rvRoom = findViewById(R.id.rvRoom);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rvRoom.setLayoutManager(layoutManager);
        adapter = new RoomAdapter(rooms, this);
        rvRoom.setAdapter(adapter);

//
//        if(rooms.isEmpty()) {
//            emptyView1.setVisibility(View.VISIBLE);
//            emptyView2.setVisibility(View.VISIBLE);
//            emptyView3.setVisibility(View.VISIBLE);
//            rvRoom.setVisibility(View.GONE);
//
//        } else {
//            emptyView1.setVisibility(View.GONE);
//            emptyView2.setVisibility(View.GONE);
//            emptyView3.setVisibility(View.GONE);
//            rvRoom.setVisibility(View.VISIBLE);
//        }


//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                initData();
//
//                adapter.notifyDataSetChanged();
//                swipeRefreshLayout.setRefreshing(false);
//            }
//
//
//        });

    }

    public void changenikname() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View niknamechange = getLayoutInflater().inflate(R.layout.changenikname, null);
        nikname = (EditText) niknamechange.findViewById(R.id.newnikname);
        buttonapply = (Button) niknamechange.findViewById(R.id.apply);

        dialogBuilder.setView(niknamechange);
        dialog = dialogBuilder.create();
        dialog.show();
        User user = new User("");

        buttonapply.setOnClickListener(view -> {
            MainActivity main = new MainActivity();
            if(!main.EditTextisEmpty(nikname)) {
                user.setNickname(nikname.getText().toString());
            } else {

                dialog.dismiss();
            }
        });

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