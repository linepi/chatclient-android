package com.example.chat.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.chat.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.grpc.ManagedChannelBuilder;
import proto.chat.Client;
import proto.chat.Device;

public class MainActivity extends AppCompatActivity {
    private static final int LOGIN_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ClientStruct.channel = ManagedChannelBuilder.forAddress("frp-art.top", 44711)
                .usePlaintext()
                .build();

        SharedPreferences sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE);
        boolean isLoggedIn = sharedPref.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST_CODE);
        } else {
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivityForResult(intent, LOGIN_REQUEST_CODE);
            ClientStruct.username = sharedPref.getString("username", "");
            ClientStruct.password = sharedPref.getString("password", "");
            ClientStruct.client = Client.newBuilder()
                            .setDevice(Device.newBuilder().setSerialNumber(""))
                            .setUser(proto.chat.User.newBuilder()
                                    .setName(ClientStruct.username)
                                    .setPassword(ClientStruct.password)
                                    .setGenderValue(1)).build();
            proceedToMainActivity();
        }
    }

    private void proceedToMainActivity() {
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // User logged in successfully, proceed to MainActivity
                proceedToMainActivity();
            } else {
                // User did not log in, finish MainActivity
                finish();
            }
        }
    }
}
