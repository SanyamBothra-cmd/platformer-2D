package com.sanyam.platformer2D.items;

import com.badlogic.gdx.graphics.Color;

public class Sword extends Weapon {
    public Sword() {
        // name, color, damage, throwSpeed, gravityScale
        super("Sword", Color.LIGHT_GRAY, 15f, 500f, 1.0f);
    }
}
