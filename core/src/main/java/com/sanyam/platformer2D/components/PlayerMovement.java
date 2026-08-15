package com.sanyam.platformer2D.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class PlayerMovement {

    private static final float WIDTH = 32f;
    private static final float HEIGHT = 48f;

    private static final float MOVE_SPEED = 200f;
    private static final float GRAVITY = -900f;
    private static final float JUMP_VELOCITY = 450f;

    private static final float WALL_SLIDE_SPEED = -100f;
    private static final float WALL_JUMP_VELOCITY_X = 150f;
    private static final float WALL_JUMP_VELOCITY_Y = 350f;
    private static final float WALL_JUMP_LOCK_DURATION = 0.25f;

    private static final float GROUND_ACCELERATION = 2000f;
    private static final float AIR_ACCELERATION = 500f;

    private static final float DOUBLE_TAP_WINDOW = 0.3f; // seconds allowed between taps to count as a double-tap

    private Vector2 position;
    private Vector2 velocity;
    private boolean onGround;
    private boolean onWallLeft;
    private boolean onWallRight;
    private float wallJumpLockTimer;

    private List<Rectangle> solids;   // full collision — ground, walls, obstacles
    private List<Rectangle> platforms; // one-way collision — land on top only

    private Rectangle currentPlatform;   // the platform currently stood on, if any (null if on solid ground or airborne)
    private Rectangle ignoredPlatform;   // platform being actively dropped through — ignored until cleared

    private float lastDownPressTime = -DOUBLE_TAP_WINDOW; // primed so the very first press can't false-trigger

    public PlayerMovement(Vector2 startPosition) {
        this.position = startPosition;
        this.velocity = new Vector2(0, 0);
        this.onGround = false;
        this.onWallLeft = false;
        this.onWallRight = false;
        this.wallJumpLockTimer = 0f;
    }

    public void setSolids(List<Rectangle> solids) {
        this.solids = solids;
    }

    public void setPlatforms(List<Rectangle> platforms) {
        this.platforms = platforms;
    }

    public void update(float delta) {
        tickTimers(delta);
        handleInput(delta);
        handleDropThrough();
        applyGravity(delta);
        moveX(delta);
        moveY(delta);
        clearIgnoredPlatformIfClear();
    }

    private void tickTimers(float delta) {
        if (wallJumpLockTimer > 0) {
            wallJumpLockTimer -= delta;
        }
    }

    private void handleInput(float delta) {
        boolean locked = wallJumpLockTimer > 0;

        if (!locked) {
            float targetVelocityX = 0f;

            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                targetVelocityX = -MOVE_SPEED;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                targetVelocityX = MOVE_SPEED;
            }

            float acceleration = onGround ? GROUND_ACCELERATION : AIR_ACCELERATION;
            velocity.x = moveTowards(velocity.x, targetVelocityX, acceleration * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            boolean holdingLeft = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
            boolean holdingRight = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

            if (onGround) {
                velocity.y = JUMP_VELOCITY;
                onGround = false;

            } else if (onWallLeft) {
                if (holdingRight) {
                    velocity.x = WALL_JUMP_VELOCITY_X;
                    velocity.y = WALL_JUMP_VELOCITY_Y;
                    onWallLeft = false;
                    wallJumpLockTimer = WALL_JUMP_LOCK_DURATION;
                } else {
                    onWallLeft = false;
                }

            } else if (onWallRight) {
                if (holdingLeft) {
                    velocity.x = -WALL_JUMP_VELOCITY_X;
                    velocity.y = WALL_JUMP_VELOCITY_Y;
                    onWallRight = false;
                    wallJumpLockTimer = WALL_JUMP_LOCK_DURATION;
                } else {
                    onWallRight = false;
                }
            }
        }
    }

    // Double-tap detection for S/Down. Uses the same "record a timestamp, compare
    // against a threshold on the next press" approach as the wall-jump lockout timer,
    // just applied to elapsed real time instead of a countdown.
    private void handleDropThrough() {
        boolean downJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.S)
            || Gdx.input.isKeyJustPressed(Input.Keys.DOWN);

        if (!downJustPressed) return;

        float now = (float) System.nanoTime() / 1_000_000_000f;

        if (now - lastDownPressTime <= DOUBLE_TAP_WINDOW) {
            // Second press arrived within the window — this is a double-tap.
            if (currentPlatform != null) {
                ignoredPlatform = currentPlatform;
                currentPlatform = null;
                onGround = false; // we're about to fall through, so we're no longer grounded
            }
        }

        lastDownPressTime = now;
    }

    private float moveTowards(float current, float target, float maxDelta) {
        if (Math.abs(target - current) <= maxDelta) {
            return target;
        }
        return current + Math.signum(target - current) * maxDelta;
    }

    private void applyGravity(float delta) {
        if (onGround) {
            return;
        }

        if ((onWallLeft || onWallRight) && velocity.y < 0) {
            velocity.y = Math.max(velocity.y + GRAVITY * delta, WALL_SLIDE_SPEED);
        } else {
            velocity.y += GRAVITY * delta;
        }
    }

    private void moveX(float delta) {
        position.x += velocity.x * delta;
        onWallLeft = false;
        onWallRight = false;

        if (solids == null) return;

        for (Rectangle solid : solids) {
            if (getBounds().overlaps(solid)) {
                if (velocity.x > 0) {
                    position.x = solid.x - WIDTH;
                    onWallRight = true;
                } else if (velocity.x < 0) {
                    position.x = solid.x + solid.width;
                    onWallLeft = true;
                }
                velocity.x = 0;
            }
        }
        // Note: platforms are intentionally NOT checked here — they only ever
        // collide vertically (from above), never block horizontal movement.
    }

    private void moveY(float delta) {
        // Capture the bottom edge BEFORE moving — this is the key value that lets
        // us tell "was I already above this platform" apart from "did I just jump
        // up into it from below."
        float previousBottom = position.y;

        position.y += velocity.y * delta;
        onGround = false;
        currentPlatform = null;

        if (solids != null) {
            for (Rectangle solid : solids) {
                if (getBounds().overlaps(solid)) {
                    if (velocity.y < 0) {
                        position.y = solid.y + solid.height;
                        onGround = true;
                    } else if (velocity.y > 0) {
                        position.y = solid.y - HEIGHT;
                    }
                    velocity.y = 0;
                }
            }
        }

        if (platforms != null) {
            for (Rectangle platform : platforms) {
                if (platform == ignoredPlatform) continue; // actively dropping through this one

                boolean wasAboveTop = previousBottom >= platform.y + platform.height;
                boolean fallingIntoIt = velocity.y < 0 && getBounds().overlaps(platform);

                if (wasAboveTop && fallingIntoIt) {
                    position.y = platform.y + platform.height;
                    velocity.y = 0;
                    onGround = true;
                    currentPlatform = platform;
                }
            }
        }
    }

    // Once the player has genuinely fallen clear of the platform they were
    // dropping through, stop ignoring it — otherwise it would stay permanently
    // passable, which isn't what "drop through" should mean.
    private void clearIgnoredPlatformIfClear() {
        if (ignoredPlatform == null) return;

        boolean stillOverlapping = getBounds().overlaps(ignoredPlatform);
        if (!stillOverlapping) {
            ignoredPlatform = null;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, WIDTH, HEIGHT);
    }

    public void reset(Vector2 newPosition) {
        position.set(newPosition);
        velocity.set(0, 0);
        onGround = false;
        onWallLeft = false;
        onWallRight = false;
        wallJumpLockTimer = 0f;
        currentPlatform = null;
        ignoredPlatform = null;
    }

    public Vector2 getPosition() { return position; }
    public Vector2 getVelocity() { return velocity; }
    public boolean isOnGround() { return onGround; }
    public boolean isOnWall() { return onWallLeft || onWallRight; }
    public boolean isWallJumpLocked() { return wallJumpLockTimer > 0; }
}
