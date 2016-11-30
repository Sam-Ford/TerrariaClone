package com.fordtech.game.entity;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by samuel on 13/11/16.
 */

public class DynamicEntity extends Entity {
    Vector2 velocity, acceleration;

    public DynamicEntity() {
        super();
        velocity = new Vector2();
        acceleration = new Vector2();
    }

    public DynamicEntity(float x, float y, float w, float h) {
        super(x, y, w, h);
        velocity = new Vector2();
        acceleration = new Vector2();
    }

}
