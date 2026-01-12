package com.itis.oris.gunmayhem.server.storage;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class PlayerStatsStorage {

    private static final String FILE_PATH =
            "src/main/resources/data/player_stats.json";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static StatsStorageModel load() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                return new StatsStorageModel();
            }
            return MAPPER.readValue(file, StatsStorageModel.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load player stats", e);
        }
    }

    public static void save(StatsStorageModel model) {
        try {
            MAPPER.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(FILE_PATH), model);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save player stats", e);
        }
    }
}

