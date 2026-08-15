package com.sanyam.platformer2D;

import com.badlogic.gdx.Game;
import com.sanyam.platformer2D.input.KeyBindings;
import com.sanyam.platformer2D.screens.MenuScreen;

public class MainGame extends Game {

    private KeyBindings keyBindings;

    @Override
    public void create() {
        keyBindings = new KeyBindings(); // one shared instance for the whole session
        setScreen(new MenuScreen(this, keyBindings));
    }
}
