package com.fordtech.game.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

/**
 * Created by samuel on 17/11/16.
 */

public class OldLight {

    private LightManager manager;
    private int xPos, yPos, radius, diameter, pixelSize;

    private Color c;
    private Pixmap pixmap;

    public OldLight(LightManager manager, int xPos, int yPos, int radius, int pixelSize) {
        // Create the light using flood fill
        this.manager = manager;
        this.radius = radius;
        this.pixelSize = pixelSize;
        this.xPos = xPos;
        this.yPos = yPos;

        c = Color.WHITE;
        diameter = (radius * 2) + 1;
        Pixmap light = new Pixmap(diameter, diameter, Pixmap.Format.RGBA8888);

        float[][] grid = new float[diameter][diameter];
        for (int x = 0; x < diameter; x++) {
            for (int y = 0; y < diameter; y++) {
                grid[x][y] = 0;
            }
        }

        applyLightRec(grid, radius, radius);

        for (int x = 0; x < light.getWidth(); x++) {
            for (int y = 0; y < light.getHeight(); y++) {
                float v = grid[x][y];
                float r = v * c.r;
                float g = v * c.g;
                float b = v * c.b;
                light.setColor(r, g, b, v < 0.05f ? 0.95f : 0.4f);
                //light.setColor(r, g, b, 0.95f);
                light.drawPixel(x, y);
            }
        }

        pixmap = upscalePixmap(light, pixelSize);
    }

    public int getX() {
        return xPos;
    }

    public int getY() {
        return yPos;
    }

    public Pixmap getPixmap() {
        return pixmap;
    }

    // Value to be subtracted in order for center of light to be in desired place.
    public float getDrawOffset() {
        return (float) pixmap.getWidth() / 2 - (float) pixelSize / 2;
    }

    private Pixmap upscalePixmap(Pixmap original, int scale) {
        if (scale < 1) return null;
        if (original == null) return null;
        int scaledWidth = original.getWidth() * scale;
        int scaledHeight = original.getHeight() * scale;
        Pixmap scaled = new Pixmap(scaledWidth, scaledHeight, original.getFormat());

        for (int x = 0; x < scaledWidth; x++) {
            for (int y = 0; y < scaledHeight; y++) {
                scaled.setColor(original.getPixel((int) x / scale, (int) y / scale));
                scaled.drawPixel(x, y);
            }
        }

        return scaled;
    }

    private void applyLightRec(float[][] map, int lightX, int lightY) {
        applyLightRec(map, lightX, lightY, lightX, lightY, 1f);
    }

    private void applyLightRec(float[][] map, int currentX, int currentY, int lightX, int lightY, float lastLight) {
        boolean validPosition = currentX >= 0 && currentX < map.length && currentY >= 0 && currentY < map[0].length;
        boolean inRange = Math.sqrt(Math.pow(currentX - lightX, 2) + Math.pow(currentY - lightY, 2)) < radius;
        if (!validPosition) return;
        if (!inRange) return;
        //float newLight = lastLight-manager.getBlockingAmount(xPos + currentX, yPos + currentY);
        float newLight = lastLight - 0.05f;
        if (newLight <= map[currentX][currentY]) return;

        map[currentX][currentY] = newLight;

        applyLightRec(map, currentX+1, currentY, lightX, lightY, newLight);
        applyLightRec(map, currentX, currentY+1, lightX, lightY, newLight);
        applyLightRec(map, currentX-1, currentY, lightX, lightY, newLight);
        applyLightRec(map, currentX, currentY-1, lightX, lightY, newLight);
    }

}
