package com.rmc.randomchat;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

import com.rmc.randomchat.entity.Room;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private List<Room> rooms;

    public  RoomAdapter(List<Room>rooms) {
        this.rooms = rooms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_recycler_view_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = rooms.get(position);

        holder.roomName.setText(room.getName());
        holder.roomid.setText(String.format(Locale.ENGLISH, "%d", room.getId()));
        holder.time.setText(String.valueOf(room.getTime()));
        holder.onlineuser.setText(String.format(Locale.ENGLISH, "%d", room.getOnlieuser()));

        holder.cardView.setCardBackgroundColor(Color.rgb(room.roomRGB[0], room.roomRGB[1], room.roomRGB[2]));
    }


    @Override
    public int getItemCount() {
        return rooms.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView roomName, roomid,time,onlineuser;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.CardViewRecycler);
            roomName = itemView.findViewById(R.id.roomName);
            roomid = itemView.findViewById(R.id.roomid);
            time = itemView.findViewById(R.id.time);
            onlineuser = itemView.findViewById(R.id.onlineuser);
        }
    }

}
