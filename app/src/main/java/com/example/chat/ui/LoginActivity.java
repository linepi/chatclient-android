package com.example.chat.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.dd.processbutton.iml.ActionProcessButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.chat.R;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import proto.chat.ChatGrpc;
import proto.chat.Client;
import proto.chat.Device;
import proto.chat.GetRoomsRequest;
import proto.chat.ResponseCode;
import proto.chat.ServerResponse;
import proto.chat.UserSignupRequest;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private ActionProcessButton loginButton;
    private TextView registerLink;
    ShimmerTextView tv;
    Shimmer shimmer;

    private class SignupTask extends AsyncTask<Void, Void, ServerResponse> {
        private String username;
        private String password;
        private SignupTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected ServerResponse doInBackground(Void... nothing) {
            ChatGrpc.ChatBlockingStub stub = ChatGrpc.newBlockingStub(ClientStruct.channel);
            UserSignupRequest req = UserSignupRequest.newBuilder()
                    .setClient(
                        Client.newBuilder()
                            .setDevice(Device.newBuilder().setSerialNumber(""))
                            .setUser(proto.chat.User.newBuilder()
                                    .setName(username)
                                    .setPassword(password)
                                    .setGenderValue(1))
                    )
                    .setPassword(password).build();

            return stub.signup(req);
        }

        @Override
        protected void onPostExecute(ServerResponse response) {
            if (response.getCodeValue() == ResponseCode.Ok_VALUE) {
                // Save login state
                ClientStruct.username = username;
                ClientStruct.password = password;
                ClientStruct.client = Client.newBuilder()
                        .setDevice(Device.newBuilder().setSerialNumber(""))
                        .setUser(proto.chat.User.newBuilder()
                                .setName(ClientStruct.username)
                                .setPassword(ClientStruct.password)
                                .setGenderValue(1)).build();

                SharedPreferences sharedPref = getSharedPreferences("MyApp", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("username", username);
                editor.putString("password", password);
                editor.apply();
                // Return result to MainActivity
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                loginButton.setProgress(100); // Indicate success
                finish(); // Close LoginActivity
            } else {
                loginButton.setProgress(-1); // Indicate failure
                Toast.makeText(LoginActivity.this, "Invalid login", Toast.LENGTH_SHORT).show();
                // Reset progress after some delay
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loginButton.setProgress(0);
                    }
                }, 2000); // Delay for 2 seconds
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setMyTheme();
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerLink = findViewById(R.id.register_link);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                // Start loading animation
                loginButton.setProgress(1);
                new SignupTask(username, password).execute();
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_register, null);

                EditText registerUsername = dialogView.findViewById(R.id.register_username);
                EditText registerPassword = dialogView.findViewById(R.id.register_password);
                Button registerButton = dialogView.findViewById(R.id.register_button);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                dialogBuilder.setView(dialogView);
                AlertDialog dialog = dialogBuilder.create();

                registerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = registerUsername.getText().toString();
                        String password = registerPassword.getText().toString();
                        if (!username.isEmpty() && !password.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "注册成功，即刻登陆吧", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(LoginActivity.this, "请输入完整的用户名或密码", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    public void setMyTheme(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        tv = (ShimmerTextView) findViewById(R.id.shimmer_tv);
        shimmer = new Shimmer();
        shimmer.setDuration(2500);
        shimmer.start(tv);
    }
}