package com.itis.oris.gunmayhem.server.storage;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
public class StatsStorageModel {

    private Map<Integer, PlayerStats> stats = new HashMap<>();

    public StatsStorageModel() {}
}

