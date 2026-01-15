package com.itis.oris.gunmayhem.common.protocol;

import com.itis.oris.gunmayhem.common.protocol.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameMessage {

    private MessageType type;

    private String payload;
}