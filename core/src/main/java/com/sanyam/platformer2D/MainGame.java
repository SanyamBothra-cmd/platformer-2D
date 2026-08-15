package com.sanyam.platformer2D;

import com.badlogic.gdx.Game;
import com.sanyam.platformer2D.screens.GameScreen;

public class MainGame extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
