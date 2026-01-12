package com.itis.oris.gunmayhem.common.model;

import com.itis.oris.gunmayhem.common.model.enums.MagicType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MagicBall {

    private long id;

    private int ownerId;

    private MagicType type;

    private Vector2D position;

    private Vector2D velocity;
}

