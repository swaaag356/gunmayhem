package com.itis.oris.gunmayhem.ui.animation.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public final class SpriteUtils {

    private SpriteUtils() {
    }


    public static BufferedImage[] cutHorizontal(String resourcePath, int frameWidth, int frameHeight, int framesCount) {
        try {
            BufferedImage sheet = ImageIO.read(SpriteUtils.class.getResource(resourcePath));
            BufferedImage[] frames = new BufferedImage[framesCount];

            for (int i = 0; i < framesCount; i++) {
                frames[i] = sheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
            }
            return frames;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load sprite sheet: " + resourcePath, e);
        }
    }

    public static BufferedImage loadImage(String resourcePath) {
        try {
            return ImageIO.read(SpriteUtils.class.getResource(resourcePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image: " + resourcePath, e);
        }
    }
}

