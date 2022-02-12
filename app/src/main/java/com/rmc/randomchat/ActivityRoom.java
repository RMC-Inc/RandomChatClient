package com.rmc.randomchat;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rmc.randomchat.RecyclerChat.ActivityChat;
import com.rmc.randomchat.depinjection.MyApplication;
import com.rmc.randomchat.entity.Room;
import com.rmc.randomchat.net.RandomChatRepository;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;


public class ActivityRoom extends AppCompatActivity implements RoomAdapter.OnRoomListener {

    private static final String TAG = "";
    private RecyclerView rvRoom;
    private LinearLayoutManager layoutManager;
    private TextView titleToolbar;
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
    private List<Room> searchRooms = new ArrayList<>();
    private TextView curr_nick;
    private SearchView search;
    private LinearLayout linearLayout;
    private String nickname;
    private RandomChatRepository randomChatRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        nickname = (String) getIntent().getSerializableExtra("nickname");
        randomChatRepository = ((MyApplication) getApplication()).appContainer.randomChatRepository;

        curr_nick = findViewById(R.id.username);
        linearLayout = findViewById(R.id.title_and_nick);
        titleToolbar = findViewById(R.id.toolbar_title);

        curr_nick.setText(nickname);

        search = findViewById(R.id.search_view);
        search.setOnSearchClickListener(v -> {
            linearLayout.setVisibility(View.GONE);
            curr_nick.setVisibility(View.GONE);
            titleToolbar.setVisibility(View.GONE);
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchRooms.clear();

                rooms.forEach(r -> {
                    if (r.getName().toLowerCase().trim().contains(s.toLowerCase().trim())){
                        searchRooms.add(r);
                    }
                });

                adapter.setData(searchRooms);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        search.setOnCloseListener(() -> {
            linearLayout.setVisibility(View.VISIBLE);
            curr_nick.setVisibility(View.VISIBLE);
            titleToolbar.setVisibility(View.VISIBLE);
            return false;
        });

        initRecyclerView();
        checkForFirstTimeUser();

        buttonnewroom = findViewById(R.id.buttonNewRoom);
        buttonnewroom.setOnClickListener(v -> {
            PopupNewRoom popUpClass = new PopupNewRoom();
            popUpClass.showPopupWindow(v, r -> {
                runOnUiThread(() -> swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true)));
                AsyncTask.execute(() -> {
                    try {
                        Room newRoom = randomChatRepository.addRoom(r);
                        if (newRoom != null){
                            rooms.add(0, newRoom);
                            runOnUiThread(() -> {
                                swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(false));
                                checkRecyclerviewEmpty();
                                adapter.notifyDataSetChanged();
                            });
                        } else {
                            runOnUiThread(() -> swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(false)));
                            runOnUiThread(() -> NetworkErrorFactory.newNetworkError(this, "Errore il server ha impiegato troppo tempo a rispondere. Chiudi l'applicazione e riprova", NetworkErrorFactory.TYPE.TERMINATE).show());
                        }
                    } catch (IOException e) {
                        runOnUiThread(() -> NetworkErrorFactory.newNetworkError(this, "Errore di connessione con il server, riavvia l'applicazione", NetworkErrorFactory.TYPE.TERMINATE).show());
                    }
                });
            });
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
                p_change.showPopupWindow(view1, curr_nick, this, randomChatRepository);
                return true;

            case R.id.enter_in_room:
                PopupEnterInRoom p_enter_room = new PopupEnterInRoom();
                p_enter_room.showPopupWindow(view1, this);
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

        adapter = new RoomAdapter(this.rooms, this);
        rvRoom.setAdapter(adapter);
        swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true));
        refreshData();
        checkRecyclerviewEmpty();
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

    }


    void checkRecyclerviewEmpty(){
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
    }

    private void refreshData() {
        rooms.clear();

        adapter.setData(rooms);

        AsyncTask.execute(() -> {
            try {
                this.rooms.addAll(randomChatRepository.getAllRooms());
                runOnUiThread(() -> {
                    swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(false));
                    checkRecyclerviewEmpty();
                    adapter.notifyDataSetChanged();
                });
            } catch (SocketTimeoutException e){
                runOnUiThread(() -> NetworkErrorFactory.newNetworkError(this, "Errore il server ha impiegato troppo tempo a rispondere", NetworkErrorFactory.TYPE.DO_NOTHING).show());
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> NetworkErrorFactory.newNetworkError(this, "Errore di connessione con il server, riavvia l'applicazione", NetworkErrorFactory.TYPE.TERMINATE).show());
            }
        });
    }

    @Override
    public void OnRoomClick(int position) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Intent intent = new Intent(this, ActivityChat.class);
        intent.putExtra("room", adapter.getData().get(position));
        startActivity(intent);
    }

    public List<Room> getListRooms(){
        return this.rooms;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void checkForFirstTimeUser(){
        final String PREFS_NAME = "PrefFile";

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if(settings.getBoolean("my_first_time", true)){

            runOnUiThread(() -> {
                new MaterialTapTargetPrompt.Builder(this)
                        .setTarget(R.id.search_view)
                        .setCaptureTouchEventOutsidePrompt(true)
                        .setCaptureTouchEventOnFocal(true)
                        .setPrimaryText("Usa questo tasto per cercare tra le stanze!")
                        .setSecondaryText("Per chiudere la barra di ricerca basta premere sulla 'x' che appare sulla destra.")
                        .setIcon(R.drawable.ic_baseline_search)
                        .setPromptStateChangeListener((prompt1, state1) -> {
                            if (state1 == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED || state1 == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                                new MaterialTapTargetPrompt.Builder(this)
                                        .setTarget(R.id.buttonNewRoom)
                                        .setCaptureTouchEventOutsidePrompt(true)
                                        .setCaptureTouchEventOnFocal(true)
                                        .setPrimaryText("Clicca su questo pulsante per creare una nuova stanza!")
                                        .setSecondaryText("Dopodichè assegnale un nome, un colore ed un tempo limite di chat.")
                                        .setPromptStateChangeListener((prompt, state) -> {
                                            if (state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {

                                            }
                                        }).show();
                            }
                        }).show();
            });


            settings.edit().putBoolean("my_first_time", false).apply();
        }
    }
}
