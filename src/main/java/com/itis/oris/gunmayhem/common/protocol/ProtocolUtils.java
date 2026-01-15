package com.itis.oris.gunmayhem.common.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itis.oris.gunmayhem.common.protocol.enums.MessageType;

import java.io.PrintWriter;

public final class ProtocolUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ProtocolUtils() {}

    // ================= JSON =================

    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialize error", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON deserialize error", e);
        }
    }

    // ================= PROTOCOL =================

    public static String encode(GameMessage msg) {
        return msg.getType() + "|" + msg.getPayload();
    }

    public static GameMessage decode(String raw) {
        String[] parts = raw.split("\\|", 2);
        return new GameMessage(
                MessageType.valueOf(parts[0]),
                parts.length > 1 ? parts[1] : ""
        );
    }
}

