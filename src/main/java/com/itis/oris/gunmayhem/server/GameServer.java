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
import java.util.List;

public class GameServer {

    public void start(int port) {
        try (ServerSocket server = new ServerSocket(port)) {

            System.out.println("Server listening on port " + port);
            System.out.println("Waiting for RED client...");
            Socket red = server.accept();
            System.out.println("RED client connected");

            System.out.println("Waiting for BLUE client...");
            Socket blue = server.accept();
            System.out.println("BLUE client connected");

            System.out.println("RED client PrintWriter try create");
            PrintWriter out1 = new PrintWriter(red.getOutputStream(), true);
            System.out.println("RED client PrintWriter " + out1);
            System.out.println("BLUE client PrintWriter try create");
            PrintWriter out2 = new PrintWriter(blue.getOutputStream(), true);
            System.out.println("BLUE client PrintWriter " + out2);

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

            System.out.println("Roles assigned");


            System.out.println("try create GAME STATE");
            GameState state = GameState.builder()
                    .redPlayer(
                            Player.builder()
                                    .id(1)
                                    .mageType(MageType.RED)
                                    .position(new Vector2D(120, 300))
                                    .velocity(new Vector2D(0, 0))
                                    .onGround(false)
                                    .state(PlayerState.IDLE)
                                    .lives(3)
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
                                    .lives(3)
                                    .build()
                    )
                    .gameOver(false)
                    .build();
            System.out.println("GAME STATE was created");

            System.out.println("try create GAME LOOP");
            ServerGameLoop loop = new ServerGameLoop(state, out1, out2);
            System.out.println("GAME LOOP was created");

            System.out.println("try create CLIENT HANDLER for RED");
            new Thread(new ClientHandler(red, loop, 1)).start();
            System.out.println("was created CLIENT HANDLER for RED");
            System.out.println("try create CLIENT HANDLER for BLUE");
            new Thread(new ClientHandler(blue, loop, 2)).start();
            System.out.println("was created CLIENT HANDLER for BLUE");
            System.out.println("OPEN LOOP THREAD");
            new Thread(loop).start();

            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
