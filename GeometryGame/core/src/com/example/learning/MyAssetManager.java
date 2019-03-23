package com.example.learning;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader.ParticleEffectParameter;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MyAssetManager {

    public final AssetManager manager = new AssetManager();

    // Textures
    public static final String badlogic = "badlogic.jpg";

    //BackGrounds
    public static final String blueBackground = "blue-background.jpg";

    // Skin
    public final String skin = "skin/glassy-ui.json";


    public void loadImages() {
        manager.load(badlogic, Texture.class);
        manager.load(blueBackground, Texture.class);
    }

    public void loadSkins() {
        SkinParameter params = new SkinParameter("skin/glassy-ui.atlas");
        manager.load(skin, Skin.class, params);
    }

}