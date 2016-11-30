package com.fordtech.game.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by samuel on 14/11/16.
 */

public class OldPlayer {

    private Texture texture;
    public Vector2 position, velocity, size, maxSpeed;
    private float gravity;

    private TiledMapTileLayer collisionLayer;
    private float tileWidth, tileHeight;

    public OldPlayer(TiledMapTileLayer layer, Texture t, float x, float y) { // x and y in tile co-ords.
        collisionLayer = layer;
        tileWidth = collisionLayer.getTileWidth();
        tileHeight = collisionLayer.getTileHeight();

        texture = t;
        position = new Vector2(x * collisionLayer.getTileWidth(), y * collisionLayer.getTileWidth());
        velocity = new Vector2();
        gravity = 60 * 10f;
        maxSpeed = new Vector2(60 * 2.4f, 60 * 10);
        size = new Vector2(texture.getWidth(), texture.getHeight());
    }

    public int tileX = 0;
    public int tileY = 0;

    public void update(float delta) {
        // Gravity
        velocity.y -= gravity * delta;

        // Clamp vertical velocity
        velocity.y = Math.max(velocity.y, -maxSpeed.y);
        velocity.y = Math.min(velocity.y, maxSpeed.y);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            velocity.y = maxSpeed.y / 2;
        }

        // Save old position
        Vector2 oldPosition = new Vector2(position.x, position.y);
        boolean collisionX = false, collisionY = false;

        // X
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocity.x = -maxSpeed.x;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocity.x = maxSpeed.x;
        } else {
            velocity.x = 0;
        }

        position.x = position.x + velocity.x * delta;

        if (velocity.x < 0) { // Left
            for (int i = 0; i < 3; i++) {
                if (isCellSolid(position.x, position.y + i * size.y / 2)) {
                    collisionX = true;
                    break;
                }
            }
        } else if (velocity.x > 0) { // Right
            for (int i = 0; i < 3; i++) {
                if ( isCellSolid(position.x + size.x, position.y + i * size.y / 2) ) {
                    collisionX = true;
                    break;
                }
            }
        }

        // React to x collision
        if (collisionX) {
            position.x = oldPosition.x;
            velocity.x = 0;
        }

        // Y
        position.y = position.y + velocity.y * delta;

        if (velocity.y < 0) { // Down
            for (int i = 0; i < 3; i++) {
                if ( isCellSolid(position.x + i * size.x / 2, position.y) ) {
                    collisionY = true;
                    break;
                }
            }

        } else if (velocity.y > 0) { // Up
            for (int i = 0; i < 3; i++) {
                if ( isCellSolid(position.x + i * size.x / 2, position.y + size.y) ) {
                    collisionY = true;
                    break;
                }
            }
        }

        // React to y collision
        if (collisionY) {
            position.y = oldPosition.y;
            velocity.y = 0;
        }

    }

    private boolean isCellSolid(float screenX, float screenY) { // Accepts screen co-ords.
        return collisionLayer.getCell((int) (screenX / tileWidth), (int) (screenY / tileHeight)).getTile().getProperties().containsKey("solid");
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, (int) position.x, (int) position.y);
    }

}
