package com.example.chat.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chat.R;
import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendAdapter friendAdapter;
    private List<Friend> friendList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize friend list
        friendList = new ArrayList<>();
        friendList.add(new Friend("1", "John Doe", "https://example.com/avatar1.jpg"));
        friendList.add(new Friend("2", "Jane Smith", "https://example.com/avatar2.jpg"));
        // Add more friends as needed

        friendAdapter = new FriendAdapter(friendList);
        recyclerView.setAdapter(friendAdapter);

        return rootView;
    }
}

