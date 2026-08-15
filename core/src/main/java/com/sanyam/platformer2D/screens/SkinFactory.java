package com.sanyam.platformer2D.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class SkinFactory {

    // Builds a minimal, code-only UI skin — no external asset files needed.
    // Fine for menus/settings; replace with a real texture-atlas skin once
    // you have UI art.
    public static Skin createBasicSkin() {
        Skin skin = new Skin();

        BitmapFont font = new BitmapFont(); // LibGDX's built-in default font
        skin.add("default-font", font);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));
        pixmap.dispose();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        skin.add("default", labelStyle);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = new TextureRegionDrawable(skin.getRegion("white")).tint(Color.DARK_GRAY);
        buttonStyle.down = new TextureRegionDrawable(skin.getRegion("white")).tint(Color.GRAY);
        buttonStyle.over = new TextureRegionDrawable(skin.getRegion("white")).tint(new Color(0.35f, 0.35f, 0.35f, 1f));
        skin.add("default", buttonStyle);

        return skin;
    }
}
