package com.example.chat.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chat.R;
import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.List;

import proto.chat.*;
import proto.chat.User;

public class FriendsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendAdapter friendAdapter;
    private List<Friend> friendList;

    private class GetUsersTask extends AsyncTask<Void, Void, ServerResponse> {
        private GetUsersTask() {
        }

        @Override
        protected ServerResponse doInBackground(Void... nothing) {
            ChatGrpc.ChatBlockingStub stub = ChatGrpc.newBlockingStub(ClientStruct.channel);
            GetUsersRequest req = GetUsersRequest.newBuilder()
                    .setClient(ClientStruct.client).build();
            return stub.getusers(req);
        }

        @Override
        protected void onPostExecute(ServerResponse response) {
            friendList = new ArrayList<>();
            List<User> users = response.getUsersList();
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                friendList.add(new Friend(String.valueOf(i + 1), user.getName(), "https://example.com/avatar1.jpg"));
            }

            friendAdapter = new FriendAdapter(friendList);
            recyclerView.setAdapter(friendAdapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        new GetUsersTask().execute();

        return rootView;
    }
}

