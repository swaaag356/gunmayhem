package com.itis.oris.gunmayhem.common.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameState {

    private Player redPlayer;

    private Player bluePlayer;

    @Builder.Default
    private List<MagicBall> magicBalls = new ArrayList<>();

    @Builder.Default
    private List<Platform> platforms = List.of(
            new Platform(100, 420, 600, 30),
            new Platform(120, 320, 160, 25),   // левая
            new Platform(520, 320, 160, 25),
            new Platform(360, 230, 120, 25),
            new Platform(200, 250, 120, 20),
            new Platform(480, 250, 120, 20)
    );

    private boolean gameOver;

    private Integer looserId;
}


