package com.itis.oris.gunmayhem.common.model;

import com.itis.oris.gunmayhem.common.model.enums.MageType;
import com.itis.oris.gunmayhem.common.model.enums.PlayerState;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {

    public static final Vector2D RED_SPAWN  = new Vector2D(120, 300);

    public static final Vector2D BLUE_SPAWN = new Vector2D(123, 300);

    private boolean facingRight = true;

    private int id;

    private MageType mageType;

    private PlayerState state;

    // позиция
    private Vector2D position;

    // скорость
    private Vector2D velocity;

    // физика
    private boolean onGround;

    // жизни
    private int lives;
}

