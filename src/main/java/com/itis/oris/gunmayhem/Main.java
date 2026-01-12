package com.itis.oris.gunmayhem;

import com.itis.oris.gunmayhem.client.MainClient;
import com.itis.oris.gunmayhem.server.MainServer;

public class Main {

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Usage: java -jar gunmayhem.jar [server|client]");
            return;
        }

        switch (args[0]) {
            case "server" -> MainServer.main(new String[0]);
            case "client" -> MainClient.main(new String[0]);
            default -> System.out.println("Unknown mode: " + args[0]);
        }
    }
}

