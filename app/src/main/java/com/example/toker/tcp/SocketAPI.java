package com.example.toker.tcp;

import android.app.Application;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import java.net.URISyntaxException;

public class SocketAPI extends Application {

    String url = "172.30.1.22:3002";

    private Socket socket;
    {
        try {
            socket = IO.socket("http://" + url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return socket;
    }

}

