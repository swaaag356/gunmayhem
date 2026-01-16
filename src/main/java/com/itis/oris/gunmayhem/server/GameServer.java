package com.itis.oris.gunmayhem.server;

import com.itis.oris.gunmayhem.common.model.*;
import com.itis.oris.gunmayhem.common.model.enums.*;
import com.itis.oris.gunmayhem.common.protocol.GameMessage;
import com.itis.oris.gunmayhem.common.protocol.ProtocolUtils;
import com.itis.oris.gunmayhem.common.protocol.enums.MessageType;
import com.itis.oris.gunmayhem.common.protocol.payloads.AssignPayload;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {

    public void start(int port) {
        try (ServerSocket server = new ServerSocket(port)) {

            Socket red = server.accept();

            Socket blue = server.accept();

            PrintWriter out1 = new PrintWriter(red.getOutputStream(), true);

            PrintWriter out2 = new PrintWriter(blue.getOutputStream(), true);

            out1.println(ProtocolUtils.encode(
                    new GameMessage(
                            MessageType.ASSIGN_ROLE,
                            ProtocolUtils.toJson(
                                    new AssignPayload(1, MageType.RED)
                            )
                    )
            ));

            out2.println(ProtocolUtils.encode(
                    new GameMessage(
                            MessageType.ASSIGN_ROLE,
                            ProtocolUtils.toJson(
                                    new AssignPayload(2, MageType.BLUE)
                            )
                    )
            ));

            GameState state = GameState.builder()
                    .redPlayer(
                            Player.builder()
                                    .id(1)
                                    .mageType(MageType.RED)
                                    .position(new Vector2D(120, 300))
                                    .velocity(new Vector2D(0, 0))
                                    .onGround(false)
                                    .state(PlayerState.IDLE)
                                    .lives(5)
                                    .build()
                    )
                    .bluePlayer(
                            Player.builder()
                                    .id(2)
                                    .mageType(MageType.BLUE)
                                    .position(new Vector2D(580, 300))
                                    .velocity(new Vector2D(0, 0))
                                    .onGround(false)
                                    .state(PlayerState.IDLE)
                                    .lives(5)
                                    .build()
                    )
                    .gameOver(false)
                    .build();

            ServerGameLoop loop = new ServerGameLoop(state, out1, out2);

            new Thread(new ClientHandler(red, loop, 1)).start();

            new Thread(new ClientHandler(blue, loop, 2)).start();

            new Thread(loop).start();

            Thread.currentThread().join();
        } catch (Exception e) {
        }
    }
}
