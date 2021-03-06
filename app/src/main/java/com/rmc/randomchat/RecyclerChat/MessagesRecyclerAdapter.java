package com.rmc.randomchat.RecyclerChat;

import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.rmc.randomchat.R;
import com.rmc.randomchat.entity.Messages;
import java.util.ArrayList;

public class MessagesRecyclerAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Messages> messagesArrayList;

    private int MessageSend=1;
    private int MessageRecieve=2;

    public MessagesRecyclerAdapter (Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MessageSend)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.layout_senderchat,parent,false);
            return new SenderViewHolder(view);
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.layout_recieverchat,parent,false);
            return new RecieverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Messages messages=messagesArrayList.get(position);
        if(holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            viewHolder.textViewmessaage.setText(messages.getMessage());
            viewHolder.timeofmessage.setText(messages.getCurrenttime());

            viewHolder.relativeLayout.getBackground().setColorFilter(messages.getColor(), PorterDuff.Mode.SRC);
        }
        else {
            RecieverViewHolder viewHolder = (RecieverViewHolder) holder;
            viewHolder.textViewmessaage.setText(messages.getMessage());
            viewHolder.timeofmessage.setText(messages.getCurrenttime());

            viewHolder.relativeLayout.getBackground().setColorFilter(messages.getColor(), PorterDuff.Mode.SRC);
        }

    }

    @Override
    public int getItemViewType(int position) {
        Messages messages = messagesArrayList.get(position);
        return (messages.isSend())? MessageSend: MessageRecieve;
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    class SenderViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewmessaage;
        private TextView timeofmessage;
        private RelativeLayout relativeLayout;


        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewmessaage = itemView.findViewById(R.id.sendermessage);
            timeofmessage = itemView.findViewById(R.id.timeofmessage);
            relativeLayout = itemView.findViewById(R.id.layoutformessage);
        }
    }

    class RecieverViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewmessaage;
        private TextView timeofmessage;
        private RelativeLayout relativeLayout;


        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewmessaage=itemView.findViewById(R.id.sendermessage);
            timeofmessage=itemView.findViewById(R.id.timeofmessage);
            relativeLayout = itemView.findViewById(R.id.layoutformessage);
        }
    }
}