package com.sanyam.platformer2D.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sanyam.platformer2D.components.Inventory;
import com.sanyam.platformer2D.components.PlayerMovement;
import com.sanyam.platformer2D.items.Sword;
import com.sanyam.platformer2D.items.Weapon;

import java.util.List;

public class Player {

    private static final float THROW_ICON_OFFSET = 36f; // how far in front of the player the weapon icon draws

    private PlayerMovement movement;
    private Inventory inventory;
    private boolean facingRight;

    // One-shot output slot — GameScreen polls and clears this each frame.
    private ThrownWeapon pendingThrow;

    public Player(Vector2 startPosition) {
        this.movement = new PlayerMovement(startPosition);
        this.inventory = new Inventory();
        this.facingRight = true;

        inventory.equip(new Sword()); // spawn with a sword equipped
    }

    public void setSolids(List<Rectangle> solids) {
        movement.setSolids(solids);
    }

    public void update(float delta) {
        movement.update(delta);
        updateFacing();
        handleThrowInput();
    }

    private void updateFacing() {
        float vx = movement.getVelocity().x;
        if (vx > 0) facingRight = true;
        else if (vx < 0) facingRight = false;
        // if vx == 0, facingRight keeps its last value — this is what makes the
        // character "remember" which way it's facing while standing still.
    }

    private void handleThrowInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F) && inventory.hasWeapon()) {
            Weapon thrownWeapon = inventory.unequip();

            float direction = facingRight ? 1 : -1;
            Vector2 throwPosition = movement.getPosition().cpy();
            Vector2 throwVelocity = new Vector2(thrownWeapon.getThrowSpeed() * direction, 150f);

            pendingThrow = new ThrownWeapon(throwPosition, throwVelocity, thrownWeapon);
        }
    }

    // Called once per frame by GameScreen — returns the thrown weapon if one
    // was just created, or null otherwise, then clears its own reference.
    public ThrownWeapon collectThrownWeapon() {
        ThrownWeapon result = pendingThrow;
        pendingThrow = null;
        return result;
    }

    public void pickUp(Weapon weapon) {
        if (!inventory.hasWeapon()) {
            inventory.equip(weapon);
        }
    }

    public boolean hasWeapon() {
        return inventory.hasWeapon();
    }

    public void render(ShapeRenderer shapeRenderer) {
        Rectangle bounds = movement.getBounds();

        if (movement.isOnWall()) {
            shapeRenderer.setColor(Color.ORANGE);
        } else if (!movement.isOnGround()) {
            shapeRenderer.setColor(Color.YELLOW);
        } else {
            shapeRenderer.setColor(Color.CYAN);
        }
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);

        // Small visual indicator of the equipped weapon, offset in the facing direction.
        if (inventory.hasWeapon()) {
            Weapon weapon = inventory.getEquippedWeapon();
            shapeRenderer.setColor(weapon.getColor());
            float iconX = facingRight ? bounds.x + bounds.width : bounds.x - 12;
            shapeRenderer.rect(iconX, bounds.y + bounds.height / 2, 12, 4);
        }
    }

    public Rectangle getBounds() {
        return movement.getBounds();
    }

    public void setPlatforms(List<Rectangle> platforms) {
        movement.setPlatforms(platforms);
    }

    public void reset(Vector2 position) {
        movement.reset(position);
    }
}
