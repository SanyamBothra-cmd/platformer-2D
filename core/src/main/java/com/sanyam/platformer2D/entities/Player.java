package com.sanyam.platformer2D.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sanyam.platformer2D.components.Inventory;
import com.sanyam.platformer2D.components.PlayerMovement;
import com.sanyam.platformer2D.input.GameAction;
import com.sanyam.platformer2D.input.KeyBindings;
import com.sanyam.platformer2D.items.Sword;
import com.sanyam.platformer2D.items.Weapon;

import java.util.List;

public class Player {

    private static final float WIDTH = 10f;
    private static final float HEIGHT = 18f;

    private PlayerMovement movement;
    private Inventory inventory;
    private KeyBindings keyBindings;
    private boolean facingRight;
    private ThrownWeapon pendingThrow;

    public Player(Vector2 startPosition, KeyBindings keyBindings) {
        this.keyBindings = keyBindings;
        this.movement = new PlayerMovement(startPosition, keyBindings, WIDTH, HEIGHT); // pass size in
        this.inventory = new Inventory();
        this.facingRight = true;
        inventory.equip(new Sword());
    }

    public void setSolids(List<Rectangle> solids) {
        movement.setSolids(solids);
    }

    public void setPlatforms(List<Rectangle> platforms) {
        movement.setPlatforms(platforms);
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
    }

    private void handleThrowInput() {
        if (Gdx.input.isKeyJustPressed(keyBindings.getKey(GameAction.THROW)) && inventory.hasWeapon()) {
            Weapon thrownWeapon = inventory.unequip();
            float direction = facingRight ? 1 : -1;
            Vector2 throwPosition = movement.getPosition().cpy();
            Vector2 throwVelocity = new Vector2(thrownWeapon.getThrowSpeed() * direction, 150f);
            pendingThrow = new ThrownWeapon(throwPosition, throwVelocity, thrownWeapon);
        }
    }

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

    public boolean consumeVoidDeath() {
        return movement.consumeVoidDeath();
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

        if (inventory.hasWeapon()) {
            Weapon weapon = inventory.getEquippedWeapon();
            shapeRenderer.setColor(weapon.getColor());
            float iconWidth = 4f;
            float iconHeight = 2f;
            float iconX = facingRight ? bounds.x + bounds.width : bounds.x - iconWidth;
            shapeRenderer.rect(iconX, bounds.y + bounds.height / 2, iconWidth, iconHeight);
        }
    }

    public Rectangle getBounds() {
        return movement.getBounds();
    }

    public void reset(Vector2 position) {
        movement.reset(position);
    }
}
