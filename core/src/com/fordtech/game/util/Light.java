package com.fordtech.game.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Created by samuel on 17/11/16.
 */

public class Light {

    private TiledMapTileLayer collisionLayer;
    private int xPos, yPos, radius, diameter, pixelSize; // Center of light in tile map coords

    private Pixmap pixmap;
    private Color c;

    public Light(TiledMapTileLayer layer, int x, int y, int radius) {
        new Light(layer, x, y, radius, 64, Color.WHITE);
    }

    public Light(TiledMapTileLayer layer, int xP, int yP, int radius, int pixelSize, Color colour) {
        collisionLayer = layer;
        xPos = xP;
        yPos = yP;
        this.radius = radius;
        diameter = (radius * 2) + 1;
        this.pixelSize = pixelSize;
        c = colour;

        float[][] grid = new float[diameter][diameter];
        for (int x = 0; x < diameter; x++) {
            for (int y = 0; y < diameter; y++) {
                grid[x][y] = 0;
            }
        }

        applyLightRec(grid, radius, radius);

        Pixmap light = new Pixmap(diameter, diameter, Pixmap.Format.RGBA8888);
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

    private float getBlockingAmount(int x, int y) {
        if (collisionLayer.getCell(x, y).getTile().getProperties().containsKey("solid")) return 0.2f;
        return 0.05f;
    }

    private void applyLightRec(float[][] map, int lightX, int lightY) {
        applyLightRec(map, lightX, lightY, lightX, lightY, 1f);
    }

    private void applyLightRec(float[][] map, int currentX, int currentY, int lightX, int lightY, float lastLight) {
        boolean validPosition = currentX >= 0 && currentX < map.length && currentY >= 0 && currentY < map[0].length;
        boolean inRange = Math.sqrt(Math.pow(currentX - lightX, 2) + Math.pow(currentY - lightY, 2)) < radius;
        if (!validPosition) return;
        if (!inRange) return;
        //float newLight = lastLight-getBlockingAmount(xPos + (currentX - lightX), yPos + (currentX - lightX));
        //System.out.println(xPos + (currentX - lightX));
        float newLight = lastLight - 0.05f;
        if (newLight <= map[currentX][currentY]) return;

        map[currentX][currentY] = newLight;

        applyLightRec(map, currentX+1, currentY, lightX, lightY, newLight);
        applyLightRec(map, currentX, currentY+1, lightX, lightY, newLight);
        applyLightRec(map, currentX-1, currentY, lightX, lightY, newLight);
        applyLightRec(map, currentX, currentY-1, lightX, lightY, newLight);
    }

}
