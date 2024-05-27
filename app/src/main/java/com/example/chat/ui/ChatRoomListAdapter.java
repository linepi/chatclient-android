package com.example.chat.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;
import com.example.chat.R;
import java.util.List;

public class ChatRoomListAdapter extends RecyclerView.Adapter<ChatRoomListAdapter.ViewHolder> {

    private List<ChatRoomList> chatRoomList;
    private OnChatRoomClickListener onChatRoomClickListener;

    public interface OnChatRoomClickListener {
        void onChatRoomClick(ChatRoomList chatRoom);
    }

    public ChatRoomListAdapter(List<ChatRoomList> chatRoomList, OnChatRoomClickListener onChatRoomClickListener) {
        this.chatRoomList = chatRoomList;
        this.onChatRoomClickListener = onChatRoomClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatRoomList chatRoom = chatRoomList.get(position);
        holder.textViewChatRoomName.setText(chatRoom.getChatRoomName());
        // Set image resource if available
        holder.imageViewChatRoom.setImageResource(chatRoom.getChatRoomImage());

        // Check if password is null and set the visibility of imageViewLock
        if (chatRoom.getPassword() == null) {
            holder.imageViewLock.setVisibility(View.GONE);
        } else {
            holder.imageViewLock.setVisibility(View.VISIBLE);
        }

        holder.onlineUserCount.setText(String.valueOf(chatRoom.getUser_num()));

        holder.itemView.setOnClickListener(v -> onChatRoomClickListener.onChatRoomClick(chatRoom));
    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewChatRoomName;
        ImageView imageViewChatRoom;
        ImageView imageViewLock;
        CardView cardView;
        TextView onlineUserCount;

        ViewHolder(View itemView) {
            super(itemView);
            textViewChatRoomName = itemView.findViewById(R.id.textViewChatRoomName);
            imageViewChatRoom = itemView.findViewById(R.id.imageViewChatRoom);
            imageViewLock = itemView.findViewById(R.id.imageViewLock);
            cardView = itemView.findViewById(R.id.card_view);
            onlineUserCount = itemView.findViewById(R.id.textViewUserCount);
        }
    }
}
