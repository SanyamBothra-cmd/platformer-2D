package com.sanyam.platformer2D.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.sanyam.platformer2D.MainGame;
import com.sanyam.platformer2D.input.GameAction;
import com.sanyam.platformer2D.input.KeyBindings;

public class SettingsScreen implements Screen {

    private MainGame game;
    private KeyBindings keyBindings;
    private Stage stage;
    private Skin skin;
    private TextButton fullscreenButton;

    public SettingsScreen(MainGame game, KeyBindings keyBindings) {
        this.game = game;
        this.keyBindings = keyBindings;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = SkinFactory.createBasicSkin();
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.add(new Label("Controls", skin)).padBottom(10);
        table.row();

        // One row per action, generated off the enum — adding a new GameAction
        // later needs no new UI code here.
        for (GameAction action : GameAction.values()) {
            Label actionLabel = new Label(action.name(), skin);
            TextButton keyButton = new TextButton(Input.Keys.toString(keyBindings.getKey(action)), skin);

            keyButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    beginListeningForKey(action, keyButton);
                }
            });

            table.add(actionLabel).padRight(20).padTop(8);
            table.add(keyButton).width(150).height(40).padTop(8);
            table.row();
        }

        table.add(new Label("Display", skin)).padTop(20);
        table.row();

        fullscreenButton = new TextButton(fullscreenLabel(), skin);
        fullscreenButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleFullscreen();
            }
        });
        table.add(fullscreenButton).colspan(2).width(200).height(40).padTop(8);
        table.row();

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game, keyBindings));
            }
        });
        table.add(backButton).colspan(2).width(200).height(40).padTop(30);
    }

    // Temporarily swaps the active InputProcessor: Stage normally owns input
    // (for button clicks), but rebinding needs "capture the next raw keypress,
    // whatever it is." We drop in a plain InputAdapter for exactly one keyDown,
    // then hand control back to the Stage.
    private void beginListeningForKey(GameAction action, TextButton keyButton) {
        keyButton.setText("Press any key...");

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                keyBindings.rebind(action, keycode);
                keyButton.setText(Input.Keys.toString(keycode));
                Gdx.input.setInputProcessor(stage);
                return true;
            }
        });
    }

    private void toggleFullscreen() {
        if (Gdx.graphics.isFullscreen()) {
            Gdx.graphics.setWindowedMode(800, 600);
        } else {
            com.badlogic.gdx.Graphics.DisplayMode mode = Gdx.graphics.getDisplayMode();
            Gdx.graphics.setFullscreenMode(mode);
        }
        fullscreenButton.setText(fullscreenLabel());
    }

    private String fullscreenLabel() {
        return Gdx.graphics.isFullscreen() ? "Fullscreen: ON" : "Fullscreen: OFF";
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
