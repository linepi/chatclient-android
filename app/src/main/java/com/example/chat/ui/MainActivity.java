package com.example.chat.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.chat.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.TimeUnit;

import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
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

        if (!waitForChannelReady(ClientStruct.channel, 3, TimeUnit.SECONDS)) {
            runOnUiThread(() -> showAlertAndExit("服务器未响应", "请联系开发作者"));
        } else {
            // 如果连接成功，可以执行其他RPC调用
            // Your gRPC calls here
            SharedPreferences sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE);
            boolean isLoggedIn = sharedPref.getBoolean("isLoggedIn", false);

            if (!isLoggedIn) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
            } else {
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
    }

    private boolean waitForChannelReady(ManagedChannel channel, int timeout, TimeUnit unit) {
        try {
            // 指定等待连接的最长时间
            long timeoutMillis = unit.toMillis(timeout);
            long deadline = System.currentTimeMillis() + timeoutMillis;

            while (System.currentTimeMillis() < deadline) {
                // 获取当前通道状态
                ConnectivityState state = channel.getState(true);
                if (state == ConnectivityState.READY) {
                    // 如果状态是READY，表示连接成功
                    return true;
                } else if (state == ConnectivityState.TRANSIENT_FAILURE || state == ConnectivityState.SHUTDOWN) {
                    // 如果连接状态是短暂失败或已关闭，立即返回失败
                    return false;
                }

                // 等待一段时间再次检查状态
                try {
                    Thread.sleep(100);  // 休眠100毫秒
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error while checking channel state", e);
        }
        // 如果超时仍未连接，返回失败
        return false;
    }


    private void showAlertAndExit(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    // 在提示框消失后延迟1秒退出应用
                    new Handler(Looper.getMainLooper()).postDelayed(this::finishAndExit, 1000);
                })
                .show();
    }

    private void finishAndExit() {
        // 关闭所有Activity
        finishAffinity();
        // 结束进程
        System.exit(0);
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
