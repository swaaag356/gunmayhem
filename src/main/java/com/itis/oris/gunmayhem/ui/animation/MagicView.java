package com.itis.oris.gunmayhem.ui.animation;

import com.itis.oris.gunmayhem.common.model.enums.MagicType;
import com.itis.oris.gunmayhem.ui.animation.util.AnimatedSprite;
import com.itis.oris.gunmayhem.ui.animation.util.SpriteUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MagicView {

    private static final int FRAME_COUNT = 8;
    private static final int DRAW_SIZE = 32;
    private static final int TICKS_PER_FRAME = 4;

    private final AnimatedSprite animation;

    public MagicView(MagicType type) {

        BufferedImage[] frames = new BufferedImage[FRAME_COUNT];

        String basePath = type == MagicType.FIRE
                ? "/sprites/magic/fire/"
                : "/sprites/magic/water/";

        for (int i = 0; i < FRAME_COUNT; i++) {
            frames[i] = SpriteUtils.loadImage(
                    basePath + (i + 1) + ".png"
            );
        }

        animation = new AnimatedSprite(frames, TICKS_PER_FRAME);
    }

    public void update() {
        animation.update();
    }

    public void draw(Graphics2D g, int x, int y, boolean facingRight) {
        if (facingRight) {
            animation.draw(g, x, y, DRAW_SIZE, DRAW_SIZE);
        } else {
            animation.drawFlipped(g, x, y, DRAW_SIZE, DRAW_SIZE);
        }
    }

}

