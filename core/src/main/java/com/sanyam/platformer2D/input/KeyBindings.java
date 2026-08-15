package com.sanyam.platformer2D.input;

import com.badlogic.gdx.Input;

import java.util.EnumMap;
import java.util.Map;

public class KeyBindings {

    private Map<GameAction, Integer> bindings;

    public KeyBindings() {
        bindings = new EnumMap<>(GameAction.class);
        bindings.put(GameAction.MOVE_LEFT, Input.Keys.A);
        bindings.put(GameAction.MOVE_RIGHT, Input.Keys.D);
        bindings.put(GameAction.JUMP, Input.Keys.W);
        bindings.put(GameAction.THROW, Input.Keys.F);
        bindings.put(GameAction.DROP_THROUGH, Input.Keys.S);
    }

    public int getKey(GameAction action) {
        return bindings.get(action);
    }

    public void rebind(GameAction action, int keycode) {
        bindings.put(action, keycode);
    }
}
