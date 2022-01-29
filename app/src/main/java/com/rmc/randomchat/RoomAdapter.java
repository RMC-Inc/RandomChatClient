package com.rmc.randomchat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rmc.randomchat.entity.Room;

import java.util.List;
import java.util.Locale;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private List<Room> rooms;
    private OnRoomListner nOnRoomListner;

    public RoomAdapter(List<Room> rooms, OnRoomListner nOnRoomListner) {
        this.rooms = rooms;
        this.nOnRoomListner = nOnRoomListner;
    }

    public List<Room> getData(){
        return this.rooms;
    }
    public void setData(List<Room> rooms){ this.rooms = rooms; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_recycler_view_room, parent, false);
        return new ViewHolder(view, nOnRoomListner);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = rooms.get(position);
        System.out.println(room.toString());
        holder.roomName.setText(room.getName());
        holder.roomid.setText(String.format(Locale.ENGLISH, "#%04d", room.getId()));


        int minutes = room.getTime() / 60;
        int seconds = room.getTime() % 60;
        String time = String.format(" %d:%02d ", minutes, seconds);
        holder.time.setText(time);

        holder.onlineuser.setText(String.format(Locale.ENGLISH, "%d", room.getOnlineUsers()));
        holder.cardView.setCardBackgroundColor(room.getRoomColor() + 0xff000000);
    }


    @Override
    public int getItemCount() { return rooms.size(); }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView roomName, roomid,time,onlineuser;
        private CardView cardView;
        private OnRoomListner onRoomListner;

        public ViewHolder(@NonNull View itemView, OnRoomListner onRoomListner) {
            super(itemView);
            cardView = itemView.findViewById(R.id.CardViewRecycler);
            roomName = itemView.findViewById(R.id.roomName);
            roomid = itemView.findViewById(R.id.roomid);
            time = itemView.findViewById(R.id.time);
            onlineuser = itemView.findViewById(R.id.onlineuser);
            this.onRoomListner = onRoomListner;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onRoomListner.OnRoomClick(getAdapterPosition());
        }
    }

    public interface OnRoomListner {
        void OnRoomClick (int position );
    }

}
