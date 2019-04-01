package com.example.learning;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class MyAssetManager {

    public final AssetManager manager = new AssetManager();

    // Textures
    public static final String badlogic = "badlogic.jpg";
    public static final String Cat1 = "Cat1.png";
    public static final String levelIndicatorActive = "levelIndicatorActive.png";
    public static final String levelIndicatorPassive = "levelIndicatorPassive.png";

    //BackGrounds
    public static final String blueBackground = "blue-background.jpg";

    // Skin
    public static final String skin = "skin/glassy-ui.json";

    public void loadImages() {
        manager.load(badlogic, Texture.class);
        manager.load(blueBackground, Texture.class);
        manager.load(Cat1, Texture.class);
        manager.load(levelIndicatorActive, Texture.class);
        manager.load(levelIndicatorPassive, Texture.class);
    }

    public void loadSkins() {
        SkinParameter params = new SkinParameter("skin/glassy-ui.atlas");
        manager.load(skin, Skin.class, params);
    }

}
