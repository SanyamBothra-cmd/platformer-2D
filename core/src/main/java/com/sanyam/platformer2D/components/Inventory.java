package com.sanyam.platformer2D.components;

import com.sanyam.platformer2D.items.Weapon;

public class Inventory {
    private Weapon equippedWeapon;

    public void equip(Weapon weapon) {
        this.equippedWeapon = weapon;
    }

    // Removes and returns the current weapon — used when throwing.
    public Weapon unequip() {
        Weapon removed = equippedWeapon;
        equippedWeapon = null;
        return removed;
    }

    public boolean hasWeapon() {
        return equippedWeapon != null;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }
}
