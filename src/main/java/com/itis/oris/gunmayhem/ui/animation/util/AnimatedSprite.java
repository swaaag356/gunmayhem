package com.itis.oris.gunmayhem.ui.animation.util;

import lombok.Getter;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

@Getter
public class AnimatedSprite {

    private final BufferedImage[] frames;
    private final int ticksPerFrame;

    private int currentFrame = 0;
    private int tickCounter = 0;

    public AnimatedSprite(BufferedImage[] frames, int ticksPerFrame) {
        this.frames = frames;
        this.ticksPerFrame = ticksPerFrame;
    }

    public void update() {
        tickCounter++;
        if (tickCounter >= ticksPerFrame) {
            tickCounter = 0;
            currentFrame = (currentFrame + 1) % frames.length;
        }
    }


    public void reset() {
        currentFrame = 0;
        tickCounter = 0;
    }

    public void draw(Graphics2D g, int x, int y, int width, int height) {
        g.drawImage(frames[currentFrame], x, y, width, height, null);
    }
    
    public void drawFlipped(Graphics2D g, int x, int y, int width, int height) {
        g.drawImage(frames[currentFrame], x + width, y, -width, height, null);
    }

}


