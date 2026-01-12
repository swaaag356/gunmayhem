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
            new Platform(0, 400, 800, 40));

    private boolean gameOver;
}


