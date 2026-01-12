package com.itis.oris.gunmayhem.server.storage;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class StatsStorageModel {

    /**
     * key = playerId
     */
    private Map<Integer, PlayerStats> stats = new HashMap<>();
}

