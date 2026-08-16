package com.sanyam.platformer2D.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sanyam.platformer2D.items.Weapon;

import java.util.List;

public class ThrownWeapon {

    private static final float SIZE = 6f;
    private static final float BASE_GRAVITY = -900f; // shared baseline; each weapon scales it

    private Vector2 position;
    private Vector2 velocity;
    private Weapon weapon;
    private boolean landed;

    public ThrownWeapon(Vector2 startPosition, Vector2 initialVelocity, Weapon weapon) {
        this.position = startPosition;
        this.velocity = initialVelocity;
        this.weapon = weapon;
        this.landed = false;
    }

    public void update(float delta, List<Rectangle> solids) {
        if (landed) return;

        // Each weapon type falls differently — a dagger (gravityScale 0.6) arcs
        // flatter and travels farther before dropping; a sword (1.0) drops normally.
        velocity.y += (BASE_GRAVITY * weapon.getGravityScale()) * delta;

        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        for (Rectangle solid : solids) {
            if (getBounds().overlaps(solid)) {
                landed = true;
                velocity.set(0, 0);
                break;
            }
        }
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(weapon.getColor());
        shapeRenderer.rect(position.x, position.y, SIZE, SIZE);
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, SIZE, SIZE);
    }

    public boolean isLanded() { return landed; }
    public Weapon getWeapon() { return weapon; }
}
