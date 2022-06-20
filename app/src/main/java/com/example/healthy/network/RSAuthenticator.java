package com.example.healthy.network;


import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class RSAuthenticator implements Authenticator {

    @Nullable
    @Override
    public synchronized Request authenticate(@Nullable Route route, Response response) throws IOException {
        return null;
    }
}
