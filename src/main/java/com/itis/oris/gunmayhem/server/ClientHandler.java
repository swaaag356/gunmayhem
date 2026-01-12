package com.itis.oris.gunmayhem.server;

import com.itis.oris.gunmayhem.common.protocol.*;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ServerGameLoop loop;
    private final int playerId;

    public ClientHandler(Socket socket, ServerGameLoop loop, int playerId) {
        this.socket = socket;
        this.loop = loop;
        this.playerId = playerId;
    }

    @Override
    public void run() {
        try (BufferedReader in =
                     new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String line;
            while ((line = in.readLine()) != null) {
                GameMessage msg = ProtocolUtils.decode(line);
                loop.pushInput(playerId, msg);
            }

        } catch (Exception e) {
            System.out.println("Client disconnected");
        }
    }
}
