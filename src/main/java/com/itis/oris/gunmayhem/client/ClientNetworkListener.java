package com.itis.oris.gunmayhem.client;

import com.itis.oris.gunmayhem.common.model.enums.MageType;
import com.itis.oris.gunmayhem.common.model.enums.MagicType;
import com.itis.oris.gunmayhem.common.protocol.*;
import com.itis.oris.gunmayhem.common.protocol.payloads.*;

import java.io.BufferedReader;

public class ClientNetworkListener implements Runnable {

    private final BufferedReader in;
    private final ClientGameState gameState;
    private final GameClient client;

    public ClientNetworkListener(
            BufferedReader in,
            ClientGameState gameState,
            GameClient client
    ) {
        this.in = in;
        this.gameState = gameState;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {

                GameMessage msg = ProtocolUtils.decode(line);

                switch (msg.getType()) {

                    case ASSIGN_ROLE -> {
                        AssignPayload p =
                                ProtocolUtils.fromJson(msg.getPayload(), AssignPayload.class);

                        client.setPlayerId(p.getPlayerId());
                        client.setMagicType(p.getRole()== MageType.RED ? MagicType.FIRE : MagicType.WATER);
                        gameState.setInitialized(true);

                        System.out.println("You are " + p.getRole());
                    }

                    case STATE -> {
                        StatePayload p =
                                ProtocolUtils.fromJson(msg.getPayload(), StatePayload.class);
                        gameState.update(p.getGameState());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Connection closed");
        }
    }
}
