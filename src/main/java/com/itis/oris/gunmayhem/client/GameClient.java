package com.itis.oris.gunmayhem.client;

import com.itis.oris.gunmayhem.common.protocol.GameMessage;
import com.itis.oris.gunmayhem.common.protocol.ProtocolUtils;
import com.itis.oris.gunmayhem.common.protocol.enums.MessageType;
import com.itis.oris.gunmayhem.common.protocol.payloads.InputPayload;
import com.itis.oris.gunmayhem.common.protocol.payloads.CastPayload;
import com.itis.oris.gunmayhem.common.protocol.enums.InputCommand;
import com.itis.oris.gunmayhem.common.model.enums.MagicType;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.Socket;

public class GameClient {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    @Getter
    private final ClientGameState gameState = new ClientGameState();

    // setters from ASSIGN_ROLE
    @Setter
    private int playerId = 0;
    @Getter
    @Setter
    private MagicType magicType;

    public void connect(String host, int port) {

        try {
            socket = new Socket(host, port);

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            ClientNetworkListener listener =
                    new ClientNetworkListener(in, gameState, this);

            new Thread(listener, "client-network").start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendInput(InputCommand cmd) {
        if (!isReady()) return;

        InputPayload payload = new InputPayload(playerId, cmd);
        send(new GameMessage(MessageType.INPUT, ProtocolUtils.toJson(payload)));
    }

    public void sendCast(MagicType type) {
        if (!isReady()) return;

        CastPayload payload = new CastPayload(playerId, type);
        send(new GameMessage(MessageType.CAST, ProtocolUtils.toJson(payload)));
    }

    private void send(GameMessage msg) {
        if (out == null) return;
        out.println(ProtocolUtils.encode(msg));
    }

    public boolean isReady() {
        return playerId != 0 && out != null;
    }

}
