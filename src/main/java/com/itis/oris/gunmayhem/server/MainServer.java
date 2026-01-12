package com.itis.oris.gunmayhem.server;

public class MainServer {

    public static final int PORT = 1234;

    public static void main(String[] args) {
        System.out.println("Starting server on port " + PORT);
        new GameServer().start(PORT);
    }
}


