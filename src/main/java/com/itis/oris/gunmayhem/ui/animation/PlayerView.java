package com.itis.oris.gunmayhem.ui.animation;

import com.itis.oris.gunmayhem.common.model.enums.MageType;
import com.itis.oris.gunmayhem.common.model.enums.PlayerState;
import com.itis.oris.gunmayhem.ui.animation.util.AnimatedSprite;
import com.itis.oris.gunmayhem.ui.animation.util.SpriteUtils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

public class PlayerView {

    private static final int FRAME_W = 32;
    private static final int FRAME_H = 32;
    private static final int DRAW_W = 64;
    private static final int DRAW_H = 64;

    private final Map<PlayerState, AnimatedSprite> animations = new EnumMap<>(PlayerState.class);

    private PlayerState currentState = PlayerState.IDLE;

    public PlayerView(MageType mageType) {

        String basePath = mageType == MageType.RED ? "/sprites/player/red/" : "/sprites/player/blue/";

        animations.put(PlayerState.IDLE, load(basePath + "idle.png", 5, 4));

        animations.put(PlayerState.RUN, load(basePath + "walk.png", 2, 6));

        animations.put(PlayerState.JUMP, load(basePath + "jump.png", 8, 8));

        animations.put(PlayerState.ATTACK, load(basePath + "attack.png", 3, 6));

    }

    private AnimatedSprite load(String path, int speed, int framesCount) {
        BufferedImage[] imgs = SpriteUtils.cutHorizontal(path, FRAME_W, FRAME_H,  framesCount);
        return new AnimatedSprite(imgs, speed);
    }

    public void setState(PlayerState newState) {
        if (currentState != newState) {
            currentState = newState;
            animations.get(currentState).reset();
        }
    }

    public void update() {
        animations.get(currentState).update();
    }

    public void draw(Graphics2D g, int x, int y, boolean facingRight) {

        if (facingRight) {
            animations.get(currentState).draw(g, x, y, DRAW_W, DRAW_H);
        } else {
            animations.get(currentState).drawFlipped(g, x, y, DRAW_W, DRAW_H);
        }
    }

}


