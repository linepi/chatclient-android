package com.example.chat.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.example.chat.R;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 获取 SharedPreferences
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", "");
        String userInfoText = sharedPref.getString("userInfo", "");

        ImageView userAvatar = view.findViewById(R.id.user_avatar);
        TextView userName = view.findViewById(R.id.user_name);
        TextView userInfo = view.findViewById(R.id.user_info);
        Button logoutButton = view.findViewById(R.id.logout_button);

        // 设置用户名和用户信息
        userName.setText(username);
        userInfo.setText(userInfoText);

        // 设置工具提示
        Balloon avatarTooltip = new Balloon.Builder(requireContext())
                .setText("长按修改头像")
                .setArrowSize(10)
                .setWidthRatio(0.5f)
                .setHeight(65)
                .setArrowPosition(0.5f)
                .setCornerRadius(4f)
                .setTextSize(15f)
                .setBackgroundColorResource(R.color.primaryColor)
                .setBalloonAnimation(BalloonAnimation.ELASTIC)
                .build();

        userAvatar.setOnLongClickListener(v -> {
            avatarTooltip.showAlignBottom(v);
            return true;
        });

        userName.setOnLongClickListener(v -> {
            showEditDialog("修改用户名", userName, "username");
            return true;
        });

        userInfo.setOnLongClickListener(v -> {
            showEditDialog("修改个人信息", userInfo, "userInfo");
            return true;
        });

        logoutButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.remove("username");
            editor.remove("userInfo");
            editor.apply();

            // 跳转到登录活动
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    private void showEditDialog(String title, TextView textView, String preferenceKey) {
        Context context = requireContext();
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("MyApp", Context.MODE_PRIVATE);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        // Set up the input
        final EditText input = new EditText(context);
        input.setText(textView.getText());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("确定", (dialog, which) -> {
            String newText = input.getText().toString();
            textView.setText(newText);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(preferenceKey, newText);
            editor.apply();
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
