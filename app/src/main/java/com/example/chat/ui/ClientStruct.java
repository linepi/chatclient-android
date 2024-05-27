package com.example.chat.ui;

import io.grpc.ManagedChannel;

public class ClientStruct {
    public static String username;
    public static String password;
    public static ManagedChannel channel;
    public static proto.chat.Client client;
}
