package com.fordtech.game.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by samuel on 13/11/16.
 */

public class Entity {

    public Vector2 position, size;
    public TextureRegion region;
    public String type = "DEFAULT";

    public Entity() {
        position = new Vector2();
        size = new Vector2();
    }

    public Entity(float x, float y, float w, float h) {
        position = new Vector2(x, y);
        size = new Vector2(w, h);
    }

    // Default render method
    public void render(SpriteBatch batch, float delta) {
        batch.draw(region, position.x, position.y, size.x, size.y);
    }

    // Collision
    public float left() {
        return (int) (position.x);
    }
    public float right() {
        return (int) (position.x + size.x);
    }
    public float top() {
        return (int) (position.y + size.y);
    }
    public float bottom() {
        return (int) (position.y);
    }

    public Vector2 absCenter() { // Absolute center
        return position.cpy().add(relCenter());
    }

    // Center relative to position
    public Vector2 relCenter() {
        return size.cpy().scl(0.5f);
    }

    // Debug method
    public Rectangle getBoundingBox() {
        return new Rectangle(left(), bottom(), size.x, size.y);
    }
}
