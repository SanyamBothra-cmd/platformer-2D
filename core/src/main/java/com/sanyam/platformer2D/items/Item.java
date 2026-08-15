package com.sanyam.platformer2D.items;

import com.badlogic.gdx.graphics.Color;

public abstract class Item {
    protected String name;
    protected Color color; // placeholder visual until sprites exist

    public Item(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() { return name; }
    public Color getColor() { return color; }
}
