package com.itis.oris.gunmayhem.common.protocol.payloads;

import com.itis.oris.gunmayhem.common.model.GameState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatePayload {

    private GameState gameState;
}

