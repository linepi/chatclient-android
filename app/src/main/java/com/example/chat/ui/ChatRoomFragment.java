package com.example.chat.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chat.R;
import com.google.protobuf.ByteString;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import proto.chat.ChatGrpc;
import proto.chat.ExitRoomRequest;
import proto.chat.HeartBeatRequest;
import proto.chat.JoinRequest;
import proto.chat.MessageType;
import proto.chat.SendRequest;
import proto.chat.ServerResponse;

public class ChatRoomFragment extends Fragment {

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private EditText editTextMessage;
    private Button buttonSend;
    private List<ChatRoom> chatRoomList;
    private String roomname;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            new UpdateTask().execute();
            handler.postDelayed(this, 100);
        }
    };

    private class JoinTask extends AsyncTask<Void, Void, ServerResponse> {
        private JoinTask() {
        }

        @Override
        protected ServerResponse doInBackground(Void... nothing) {
            ChatGrpc.ChatBlockingStub stub = ChatGrpc.newBlockingStub(ClientStruct.channel);
            JoinRequest req = JoinRequest.newBuilder()
                    .setRoomname(roomname)
                    .setClient(ClientStruct.client).build();
            return stub.join(req);
        }

        @Override
        protected void onPostExecute(ServerResponse response) {
        }
    }

    private class ExitTask extends AsyncTask<Void, Void, ServerResponse> {
        private ExitTask() {
        }

        @Override
        protected ServerResponse doInBackground(Void... nothing) {
            ChatGrpc.ChatBlockingStub stub = ChatGrpc.newBlockingStub(ClientStruct.channel);
            ExitRoomRequest req = ExitRoomRequest.newBuilder()
                    .setRoomname(roomname)
                    .setClient(ClientStruct.client).build();
            return stub.exitroom(req);
        }

        @Override
        protected void onPostExecute(ServerResponse response) {
        }
    }

    private class SendTask extends AsyncTask<Void, Void, ServerResponse> {
        private String content;
        private SendTask(String content) {
            this.content = content;
        }

        @Override
        protected ServerResponse doInBackground(Void... nothing) {
                ChatGrpc.ChatBlockingStub stub = ChatGrpc.newBlockingStub(ClientStruct.channel);
                SendRequest req = SendRequest.newBuilder()
                        .setMessage(
                                proto.chat.Message.newBuilder()
                                        .setMsgTypeValue(MessageType.Text_VALUE)
                                        .setTime(System.currentTimeMillis())
                                    .setBytes(ByteString.copyFrom(content.getBytes()))
                                    .setClient(ClientStruct.client)
                    )
                    .setRoomname(roomname)
                    .setClient(ClientStruct.client).build();
            return stub.send(req);
        }

        @Override
        protected void onPostExecute(ServerResponse response) {
            editTextMessage.setText("");
        }
    }

    private class UpdateTask extends AsyncTask<Void, Void, ServerResponse> {
        private UpdateTask() {
        }

        @Override
        protected ServerResponse doInBackground(Void... nothing) {
            ChatGrpc.ChatBlockingStub stub = ChatGrpc.newBlockingStub(ClientStruct.channel);
            HeartBeatRequest req = HeartBeatRequest.newBuilder()
                    .setRoomname(roomname)
                    .setMsgnum(messageList.size())
                    .setClient(ClientStruct.client).build();
            return stub.heartbeat(req);
        }

        @Override
        protected void onPostExecute(ServerResponse response) {
            loadChatRoomMessages(response);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chatroom, container, false);

        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            setHasOptionsMenu(true);
        }
        recyclerView = rootView.findViewById(R.id.recyclerViewChatRoom);
        editTextMessage = rootView.findViewById(R.id.editTextMessage);
        buttonSend = rootView.findViewById(R.id.buttonSend);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(messageAdapter);

        // 获取传递的ChatRoom ID
        Bundle args = getArguments();
        if (args != null) {
            roomname = args.getString("chatRoomName");
            new JoinTask().execute();
            new UpdateTask().execute();
        }

        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(roomname);
            }
            setHasOptionsMenu(true);
        }

        handler.postDelayed(runnable, 100);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editTextMessage.getText().toString();
                if (!messageText.isEmpty()) {
                    new SendTask(messageText).execute();
                }
            }
    });

        return rootView;
    }

    private void loadChatRoomMessages(ServerResponse response) {
        List<proto.chat.Message> msglist = response.getMessagesList();
        if (msglist.isEmpty())  {
            return;
        }
        List<Message> outlist = new ArrayList<>();
        for (proto.chat.Message msg : msglist) {
            outlist.add(new Message(
                    msg.getClient().getUser().getName(),
                    new String(msg.getBytes().toByteArray(), java.nio.charset.StandardCharsets.UTF_8),
                    msg.getClient().getUser().getName().equals(ClientStruct.username),
                    new Date(msg.getTime())
                    ));
        }
        messageList.addAll(outlist);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Navigation.findNavController(getView()).navigate(R.id.action_chatroom_to_chat);
                new ExitTask().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // 移除Runnable和任何的消息
        handler.removeCallbacks(runnable);
    }
}
