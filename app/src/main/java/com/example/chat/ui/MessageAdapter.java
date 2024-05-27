package com.example.chat.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);

        if (message.getUsername().equals(ClientStruct.username)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        TextView textName;
        ImageView imageAvatar;
        TextView textTime;

        SentMessageHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            textName = itemView.findViewById(R.id.textName);
            imageAvatar = itemView.findViewById(R.id.imageAvatar);
            textTime = itemView.findViewById(R.id.textTime);
        }

        void bind(Message message) {
            textMessage.setText(message.getText());
            // Set the name and avatar if needed, example:
            textName.setText(message.getUsername());
            // imageAvatar.setImageResource(R.drawable.your_avatar); // Set the avatar if you have one
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            textTime.setText(formatter.format(message.getSentTime()));
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView textMessage;
        TextView textName;
        ImageView imageAvatar;
        TextView textTime;
        ReceivedMessageHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            textName = itemView.findViewById(R.id.textName);
            imageAvatar = itemView.findViewById(R.id.imageAvatar);
            textTime = itemView.findViewById(R.id.textTime);
        }

        void bind(Message message) {
            textMessage.setText(message.getText());
            // Set the name and avatar if needed, example:
            textName.setText(message.getUsername());
            // imageAvatar.setImageResource(R.drawable.friend_avatar); // Set the avatar if you have one
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            textTime.setText(formatter.format(message.getSentTime()));
        }
    }
}
