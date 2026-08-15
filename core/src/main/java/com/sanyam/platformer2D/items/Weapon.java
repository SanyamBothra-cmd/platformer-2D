package com.sanyam.platformer2D.items;

import com.badlogic.gdx.graphics.Color;

public abstract class Weapon extends Item {
    protected float damage;
    protected float throwSpeed;
    protected float gravityScale; // multiplier on base gravity — 1.0 = normal, <1 = floaty, >1 = drops fast

    public Weapon(String name, Color color, float damage, float throwSpeed, float gravityScale) {
        super(name, color);
        this.damage = damage;
        this.throwSpeed = throwSpeed;
        this.gravityScale = gravityScale;
    }

    public float getDamage() { return damage; }
    public float getThrowSpeed() { return throwSpeed; }
    public float getGravityScale() { return gravityScale; }
}
