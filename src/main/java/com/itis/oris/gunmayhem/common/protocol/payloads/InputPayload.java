package com.itis.oris.gunmayhem.common.protocol.payloads;

import com.itis.oris.gunmayhem.common.protocol.enums.InputCommand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputPayload {

    private int playerId;

    private InputCommand command;
}

