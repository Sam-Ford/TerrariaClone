package com.fordtech.game.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;

/**
 * Created by samuel on 21/11/16.
 */

public class Tiles {

    private String[] solidTileKeys = {"solid", "blocked"};

    public StaticTiledMapTile[] staticTiles = new StaticTiledMapTile[3];

    public Tiles() {

    }

    public void load(int tileSize) {
        Texture terrain = new Texture("maps/terrain.png");

        /*
        staticTiles[0] = newTile(new TextureRegion(terrain, 0, 0, tileSize, tileSize), 0, solidTileKeys);
        staticTiles[1] = newTile(new TextureRegion(terrain, 0, 0, tileSize, tileSize), 1, solidTileKeys);
        */
    }

    private StaticTiledMapTile newSolidBreakableTile(TextureRegion region, int id) {
        StaticTiledMapTile tile = new StaticTiledMapTile(region);
        tile.setId(id);
        MapProperties p = tile.getProperties();
        p.put("solid", null);
        p.put("breakable", null);
        tile.setOffsetX(0);
        tile.setOffsetY(0);
        return tile;
    }

    public StaticTiledMapTile getTile(int id) {
        if (id < staticTiles.length) return staticTiles[id];
        return null;
    }

    public int getId(StaticTiledMapTile tile) {
        int index = getIndex(tile, staticTiles);
        if (index > -1) {
            return index;
        }
        return index;
    }

    private int getIndex(Object object, Object[] objects) {
        for (int i = 0; i < objects.length; i++) {
            if (object == objects[i]) {
                return i;
            }
        }
        return -1;
    }

}
