package com.sanyam.platformer2D.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.sanyam.platformer2D.entities.Player;
import com.sanyam.platformer2D.entities.ThrownWeapon;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private Player player;

    private List<Rectangle> solids;
    private List<Rectangle> platforms;
    private List<ThrownWeapon> thrownWeapons;
    private Rectangle resetZone;
    private Vector2 spawnPoint;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        shapeRenderer = new ShapeRenderer();

        spawnPoint = new Vector2(50, 100);
        player = new Player(spawnPoint.cpy());
        thrownWeapons = new ArrayList<>();

        buildTestLevel();
        player.setSolids(solids);
        player.setPlatforms(platforms);
    }

    private void buildTestLevel() {
        solids = new ArrayList<>();
        solids.add(new Rectangle(0, -1000, 800, 1000));   // ground
        solids.add(new Rectangle(380, 0, 40, 60));         // jump obstacle
        solids.add(new Rectangle(560, 0, 20, 400));        // wall-jump chimney (left wall)
        solids.add(new Rectangle(660, 0, 20, 400));        // wall-jump chimney (right wall)

        platforms = new ArrayList<>();
        platforms.add(new Rectangle(150, 150, 100, 10));   // floating platform to jump up onto / drop through
        platforms.add(new Rectangle(300, 280, 100, 10));   // a second, higher platform to chain jumps between

        resetZone = new Rectangle(740, 0, 50, 40);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        player.update(delta);
        checkResetZone();
        updateThrownWeapons(delta);
        checkWeaponPickup();

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.GRAY);
        for (Rectangle solid : solids) {
            shapeRenderer.rect(solid.x, solid.y, solid.width, solid.height);
        }

        shapeRenderer.setColor(Color.BROWN);
        for (Rectangle platform : platforms) {
            shapeRenderer.rect(platform.x, platform.y, platform.width, platform.height);
        }

        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(resetZone.x, resetZone.y, resetZone.width, resetZone.height);

        for (ThrownWeapon tw : thrownWeapons) {
            tw.render(shapeRenderer);
        }

        player.render(shapeRenderer);

        shapeRenderer.end();
    }

    private void updateThrownWeapons(float delta) {
        ThrownWeapon newThrow = player.collectThrownWeapon();
        if (newThrow != null) {
            thrownWeapons.add(newThrow);
        }

        for (ThrownWeapon tw : thrownWeapons) {
            tw.update(delta, solids);
        }
    }

    private void checkWeaponPickup() {
        if (player.hasWeapon()) return;

        for (int i = 0; i < thrownWeapons.size(); i++) {
            ThrownWeapon tw = thrownWeapons.get(i);
            if (tw.isLanded() && player.getBounds().overlaps(tw.getBounds())) {
                player.pickUp(tw.getWeapon());
                thrownWeapons.remove(i);
                break;
            }
        }
    }

    private void checkResetZone() {
        if (player.getBounds().overlaps(resetZone)) {
            player.reset(spawnPoint.cpy());
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
