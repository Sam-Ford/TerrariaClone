package com.fordtech.game.entity;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by samuel on 13/11/16.
 */

public class Player implements InputProcessor {

    private OrthographicCamera cameraHandle;

    private Texture texture;
    public Vector2 position, velocity, scale, size, maxSpeed;
    private float gravity;
    private boolean canJump;

    private TiledMapTileLayer collisionLayer;
    private int tileWidth, tileHeight;

    public Player(TiledMapTileLayer layer, Texture t, float x, float y) { // x and y in tile co-ords.
        collisionLayer = layer;
        tileWidth = (int) collisionLayer.getTileWidth();
        tileHeight = (int) collisionLayer.getTileHeight();

        texture = t;
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        position = new Vector2(x * collisionLayer.getTileWidth(), y * collisionLayer.getTileWidth());
        velocity = new Vector2();
        gravity = 60 * 10f;
        canJump = false;
        maxSpeed = new Vector2(60 * 2.4f, 60 * 10);
        scale = new Vector2(0.9f, 0.9f);
        System.out.println(1 / 0.9f);
        size = new Vector2(texture.getWidth(), texture.getHeight()).scl(scale);
    }

    public void update(float delta) {
        // Gravity
        velocity.y -= gravity * delta;

        // Clamp vertical velocity
        velocity.y = Math.max(velocity.y, -maxSpeed.y);
        velocity.y = Math.min(velocity.y, maxSpeed.y);

        // Save old position
        Vector2 oldPosition = new Vector2(position.x, position.y);
        boolean collisionX = false, collisionY = false;

        // X
        position.x = position.x + velocity.x * delta;

        if (velocity.x < 0) { // Left
            collisionX = collidesLeft();
        } else if (velocity.x > 0) { // Right
            collisionX = collidesRight();
        }

        // React to x collision
        if (collisionX) {
            position.x = oldPosition.x;
            //velocity.x = 0;
        }

        // Y
        position.y = position.y + velocity.y * delta;

        if (velocity.y < 0) { // Down
            canJump = collisionY = collidesBottom();
        } else if (velocity.y > 0) { // Up
            collisionY = collidesTop();
        }

        // React to y collision
        if (collisionY) {
            position.y = oldPosition.y;
            velocity.y = 0;
        }

    }

    public void setCameraHandle(OrthographicCamera camera) {
        cameraHandle = camera;
    }

    private boolean isCellSolid(float x, float y) {
        return isCell(x, y, "solid");
    }

    private boolean isCell(float x, float y, String key) {
        TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) (x / tileWidth), (int) (y / tileHeight));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey(key);
    }

    public boolean collidesRight() {
        for(float step = 0; step < size.y + 1; step += (scale.y < 1 ? tileHeight * scale.y : tileHeight / 2))
            if(isCellSolid(position.x + size.x, position.y + step))
                return true;
        return false;
    }

    public boolean collidesLeft() {
        for(float step = 0; step < size.y + 1; step += (scale.y < 1 ? tileHeight * scale.y : tileHeight / 2))
            if(isCellSolid(position.x, position.y + step))
                return true;
        return false;
    }

    public boolean collidesTop() {
        for(float step = 0; step < size.x + 1; step += (scale.x < 1 ? tileWidth * scale.x : tileWidth / 2))
            if(isCellSolid(position.x + step, position.y + size.y))
                return true;
        return false;

    }

    public boolean collidesBottom() {
        for(float step = 0; step < size.x + 1; step += (scale.x < 1 ? tileWidth * scale.x : tileWidth / 2))
            if (isCellSolid(position.x + step, position.y))
                return true;
        return false;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, size.x, size.y);
    }

    @Override
    public boolean keyDown(int keycode) {
        switch(keycode) {
            case Keys.SPACE:
                if(canJump) {
                    velocity.y = gravity / 2;
                    canJump = false;
                }
                break;
            case Keys.A:
                velocity.x = -maxSpeed.x;
                break;
            case Keys.D:
                velocity.x = maxSpeed.x;
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.A:
                velocity.x = 0;
                break;
            case Keys.D:
                velocity.x = 0;
                break;
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    public boolean inRangeOfPlayer(float x, float y, float range) {
        double distance = Math.pow(Math.pow(position.x + size.x / 2 - x, 2) + Math.pow(position.y + size.y / 2 - y, 2), 0.5f);
        return (float) distance < range;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            removeBlock(screenX, screenY);
        } else if (button == 1) {
            placeBlock(screenX, screenY);
        }
        return true;
    }

    private void placeBlock(int screenX, int screenY) {
        Vector3 screenCoords = new Vector3(screenX, screenY, 0);
        Vector3 worldCoords = cameraHandle.unproject(screenCoords);
        if (inRangeOfPlayer(worldCoords.x, worldCoords.y, 4 * tileWidth)) {
            if (isCell(worldCoords.x, worldCoords.y, "air")) {
                TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) (worldCoords.x / tileWidth), (int) (worldCoords.y / tileHeight));
                StaticTiledMapTile oldTile = (StaticTiledMapTile) cell.getTile();
                StaticTiledMapTile newTile = new StaticTiledMapTile(oldTile);
                newTile.getProperties().put("solid", null);
                newTile.getProperties().put("breakable", null);
                newTile.setTextureRegion(new TextureRegion(new Texture("maps/terrain.png"), tileWidth, 0, tileWidth, tileHeight));
                cell.setTile(newTile);
            }
        }
    }

    private void removeBlock(int screenX, int screenY) {
        Vector3 screenCoords = new Vector3(screenX, screenY, 0);
        Vector3 worldCoords = cameraHandle.unproject(screenCoords);
        if (inRangeOfPlayer(worldCoords.x, worldCoords.y, 4 * tileWidth)) {
            if (isCell(worldCoords.x, worldCoords.y, "breakable")) {
                TiledMapTileLayer.Cell cell = collisionLayer.getCell((int) (worldCoords.x / tileWidth), (int) (worldCoords.y / tileHeight));
                StaticTiledMapTile oldTile = (StaticTiledMapTile) cell.getTile();
                StaticTiledMapTile newTile = new StaticTiledMapTile(oldTile);
                newTile.getProperties().remove("solid");
                newTile.getProperties().put("air", null);
                newTile.setTextureRegion(new TextureRegion(new Texture("maps/terrain.png"), 2 * tileWidth, 0 * tileHeight, tileWidth, tileHeight));
                cell.setTile(newTile);
            }
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
