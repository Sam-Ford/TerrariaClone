package com.fordtech.game.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Created by samuel on 16/11/16.
 */

public class LightManager {

    private int lightSize = 30, pixelSize = 64;

    TiledMapTileLayer tileLayer;
    Pixmap lightMap;

    public LightManager(TiledMapTileLayer t) {
        tileLayer = t;
        lightMap = new Pixmap((int) (t.getWidth() * t.getTileWidth()), (int) (t.getHeight() * t.getTileHeight()), Pixmap.Format.RGBA8888);
        lightMap.setBlending(Pixmap.Blending.None);
        lightMap.setColor(new Color(0, 0, 0, 0.95f));
        lightMap.fill();


        /*
        Pixmap p = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        p.setColor(Color.WHITE);
        p.fill();
        lightMap.drawPixmap(p, 8 * 64, lightMap.getHeight() - (37 * 64));
        */

        //addLight(new Light(this, 8, 37, 5, pixelSize));
    }

    /*
    public void addLight(Light light) {
        lightMap.drawPixmap(light.getPixmap(), light.getX() * 64 - (int) light.getDrawOffset(), lightMap.getHeight() - (light.getY() * 64 + (int) light.getDrawOffset()));
    }
    */

    public Pixmap getLightMap() {
        return lightMap;
    }

    public void update() {

    }


}
