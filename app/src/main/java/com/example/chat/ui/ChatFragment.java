package com.example.chat.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.chat.R;
import com.google.protobuf.InvalidProtocolBufferException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import proto.chat.*;

public class ChatFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    private RecyclerView recyclerViewChatRooms;
    private Button buttonCreateChatRoom;
    private List<ChatRoomList> chatRoomList;
    private List<ChatRoomList> filteredChatRoomList;
    private ChatRoomListAdapter chatRoomListAdapter;
    private Handler mainHandler;
    private ServerResponse response;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(false); // 禁用返回按钮
            }
        }

        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
        searchView = rootView.findViewById(R.id.searchView);
        recyclerViewChatRooms = rootView.findViewById(R.id.recyclerViewChatRooms);
        buttonCreateChatRoom = rootView.findViewById(R.id.buttonCreateChatRoom);

        chatRoomList = new ArrayList<>();
        filteredChatRoomList = new ArrayList<>(chatRoomList);
        chatRoomListAdapter = new ChatRoomListAdapter(filteredChatRoomList, this::onChatRoomClicked);

        recyclerViewChatRooms.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewChatRooms.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewChatRooms.setAdapter(chatRoomListAdapter);

        buttonCreateChatRoom.setOnClickListener(v -> createNewChatRoom());

        swipeRefreshLayout.setOnRefreshListener(() -> {
            new GetRoomTask().execute();
            swipeRefreshLayout.setRefreshing(false);
        });

        mainHandler = new Handler(Looper.getMainLooper());

        new GetRoomTask().execute();
        setupSearchView();

        return rootView;
    }

    private class GetRoomTask extends AsyncTask<Void, Void, ServerResponse> {
        private GetRoomTask() {
        }

        @Override
        protected ServerResponse doInBackground(Void... nothing) {
            ChatGrpc.ChatBlockingStub stub = ChatGrpc.newBlockingStub(ClientStruct.channel);
            GetRoomsRequest req = GetRoomsRequest.newBuilder().setClient(
                    Client.newBuilder()
                            .setDevice(Device.newBuilder().setSerialNumber(""))
                            .setUser(proto.chat.User.newBuilder()
                                    .setName("d")
                                    .setGenderValue(1))
            ).build();

            return stub.getrooms(req);
        }

        @Override
        protected void onPostExecute(ServerResponse response) {
            updateChatRooms(response);
        }
    }
    private class CreateRoomTask extends AsyncTask<Void, Void, ServerResponse> {
        private ChatRoomList newChatRoom;
        private CreateRoomTask(ChatRoomList newChatRoom) {
            this.newChatRoom = newChatRoom;
        }

        @Override
        protected ServerResponse doInBackground(Void... nothing) {
            ChatGrpc.ChatBlockingStub stub = ChatGrpc.newBlockingStub(ClientStruct.channel);

            CreateRoomRequest.Builder reqbd = CreateRoomRequest.newBuilder()
                    .setRoomname(newChatRoom.getChatRoomName())
                    .setHistoryVisible(true)
                    .setClient(
                            Client.newBuilder()
                                    .setDevice(Device.newBuilder().setSerialNumber(""))
                                    .setUser(proto.chat.User.newBuilder().setName(ClientStruct.username).setGenderValue(1))
                    );
            if (newChatRoom.getPassword() != null) {
                reqbd.setPassword(newChatRoom.getPassword());
            }
            return stub.createroom(reqbd.build());
        }

        @Override
        protected void onPostExecute(ServerResponse response) {
        }
    }

    public void updateChatRooms(ServerResponse response) {
        chatRoomList.clear();
        filteredChatRoomList.clear();

        List<RoomInfo> list = response.getRoominfosList();
        // 添加一些假数据
        for (int i = 0; i < list.size(); i++) {
            RoomInfo info = list.get(i);
            ChatRoomList chatRoom;
            if (info.getPassword().isEmpty()) {
                chatRoom = new ChatRoomList(info.getName());
            } else {
                chatRoom = new ChatRoomList(info.getName(), info.getPassword());
            }
            chatRoom.setUser_num(info.getOnlineUsersCount());
            chatRoomList.add(chatRoom);
        }
        filteredChatRoomList.addAll(chatRoomList);
        chatRoomListAdapter.notifyDataSetChanged();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterChatRooms(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterChatRooms(newText);
                return false;
            }
        });
    }

    private void filterChatRooms(String query) {
        filteredChatRoomList.clear();
        if (query.isEmpty()) {
            filteredChatRoomList.addAll(chatRoomList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            filteredChatRoomList.addAll(chatRoomList.stream()
                    .filter(chatRoom -> chatRoom.getChatRoomName().toLowerCase().contains(lowerCaseQuery))
                    .collect(Collectors.toList()));
        }
        chatRoomListAdapter.notifyDataSetChanged();
    }


    private void createNewChatRoom() {
        // 创建新的聊天室
        // 创建一个 AlertDialog.Builder 对象
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // 设置提示框的布局
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_create_chat_room, null);
        builder.setView(dialogView);

        // 获取布局中的视图
        EditText editTextChatRoomName = dialogView.findViewById(R.id.editTextChatRoomName);
        EditText editTextPassword = dialogView.findViewById(R.id.editTextPassword);
        CheckBox checkBoxPassword = dialogView.findViewById(R.id.checkBoxPassword);

        // 初始状态下隐藏密码输入框
        editTextPassword.setVisibility(View.GONE);

        // 设置CheckBox的监听器
        checkBoxPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editTextPassword.setVisibility(View.VISIBLE);
            } else {
                editTextPassword.setVisibility(View.GONE);
            }
        });

        // 设置按钮
        builder.setPositiveButton("创建", (dialog, which) -> {
            String chatRoomName = editTextChatRoomName.getText().toString();
            String password = checkBoxPassword.isChecked() ? editTextPassword.getText().toString() : "";

            if (!chatRoomName.isEmpty()) {
                ChatRoomList newChatRoom = new ChatRoomList(
                        chatRoomName,
                        password.isEmpty() ? null : password // 如果没有设置密码则为null
                );
                chatRoomList.add(newChatRoom);
                new CreateRoomTask(newChatRoom).execute();
                filteredChatRoomList.add(newChatRoom);
                chatRoomListAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        // 显示提示框
        builder.create().show();
    }

    private void onChatRoomClicked(ChatRoomList chatRoom) {
        // 创建一个 AlertDialog.Builder 对象
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // 设置提示框的布局
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_chat_room_details, null);
        builder.setView(dialogView);

        // 获取布局中的视图
        ImageView imageViewChatRoom = dialogView.findViewById(R.id.dialogImageView);
        TextView textViewChatRoomName = dialogView.findViewById(R.id.dialogTextView);
        EditText editTextPassword = dialogView.findViewById(R.id.dialogEditTextPassword);

        imageViewChatRoom.setImageResource(chatRoom.getChatRoomImage());
        textViewChatRoomName.setText(chatRoom.getChatRoomName());

        if (chatRoom.getPassword() == null || chatRoom.getPassword().isEmpty()) {
            editTextPassword.setEnabled(false);
            editTextPassword.setHint("无需输入密码");
        } else {
            editTextPassword.setEnabled(true);
        }
        // 设置按钮
        builder.setPositiveButton("加入", (dialog, which) -> {
            String enteredPassword = editTextPassword.getText().toString();
            String chatRoomPassword = chatRoom.getPassword() != null ? chatRoom.getPassword() : "";

            if (chatRoomPassword.isEmpty() || chatRoomPassword.equals(enteredPassword)) {
                // 密码正确或不需要密码，执行导航
                Bundle bundle = new Bundle();
                bundle.putString("chatRoomName", chatRoom.getChatRoomName());
                Navigation.findNavController(getView()).navigate(R.id.action_chat_to_chatroom, bundle);
            } else {
                // 密码错误，显示错误提示
                editTextPassword.setError("密码错误");
            }
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        // 显示提示框
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
