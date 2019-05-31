package ru.hse.team;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/*
* This class is not a singleton, because of android
* special behaviour related with static variables.
* */
public class KittensAssetManager {

    public final AssetManager manager = new AssetManager();

    public static final String BADLOGIC = "badlogic.jpg";
    public static final String LEVEL_INDICATOR_ACTIVE_PNG = "levelIndicatorActive.png";
    public static final String LEVEL_INDICATOR_PASSIVE_PNG = "levelIndicatorPassive.png";
    public static final String CAT_1 = "Cat1.png";
    public static final String CAT_2 = "Cat2.png";
    public static final String CAT_3 = "Cat3.png";
    public static final String STAR_1 = "Star1.png";
    public static final String STAR_2 = "Star2.png";
    public static final String POINTER = "pointer.png";
    public static final String KEY = "key.png";
    public static final String YELLOW_TUMBLER = "yellow-tumbler.png";
    public static final String BLUE_TUMBLER = "blue-tumbler.png";
    public static final String QUESTION = "question.png";
    public static final String FOG = "fog.png";
    public static final String BLUE_BACKGROUND = "blue-background.jpg";
    public static final String ICE_WALL = "ice-wall.png";
    public static final String TRANSPARENT_WALL = "transparent-wall.png";
    public static final String MIRROR = "mirror.png";
    public static final String DOOR = "door.png";
    public static final String GOOGLE_SIGN_IN = "google-sign-in.png";
    public static final String CUP = "cup.png";
    public static final String PLAY_MARKET = "play-market.png";
    public static final String SKIN = "skin/glassy-ui.json";
    public static final String LASER_SOUND = "sounds/laser-shot.wav";
    public static final String FONT = "skin/gameFont.fnt";
    public static final String ERASER = "eraser.png";
    public static final String DELETE = "delete.png";
    public static final String CURSOR = "cursor.png";
    public static final String OK = "ok.png";
    public static final String ROTATE_LEFT = "rotate_left.png";
    public static final String ROTATE_RIGHT = "rotate_right.png";

    private void loadImages() {
        manager.load(BADLOGIC, Texture.class);
        manager.load(BLUE_BACKGROUND, Texture.class);
        manager.load(ICE_WALL, Texture.class);
        manager.load(TRANSPARENT_WALL, Texture.class);
        manager.load(MIRROR, Texture.class);
        manager.load(DOOR, Texture.class);
        manager.load(CAT_1, Texture.class);
        manager.load(CAT_2, Texture.class);
        manager.load(CAT_3, Texture.class);
        manager.load(LEVEL_INDICATOR_ACTIVE_PNG, Texture.class);
        manager.load(LEVEL_INDICATOR_PASSIVE_PNG, Texture.class);
        manager.load(STAR_1, Texture.class);
        manager.load(STAR_2, Texture.class);
        manager.load(POINTER, Texture.class);
        manager.load(QUESTION, Texture.class);
        manager.load(KEY, Texture.class);
        manager.load(YELLOW_TUMBLER, Texture.class);
        manager.load(BLUE_TUMBLER, Texture.class);
        manager.load(GOOGLE_SIGN_IN, Texture.class);
        manager.load(CUP, Texture.class);
        manager.load(PLAY_MARKET, Texture.class);
        manager.load(FOG, Texture.class);
        manager.load(ERASER, Texture.class);
        manager.load(DELETE, Texture.class);
        manager.load(CURSOR, Texture.class);
        manager.load(OK, Texture.class);
        manager.load(ROTATE_LEFT, Texture.class);
        manager.load(ROTATE_RIGHT, Texture.class);
    }

    private void loadSounds() {
        manager.load(LASER_SOUND, Sound.class);
    }

    private void loadSkins() {
        SkinParameter params = new SkinParameter("skin/glassy-ui.atlas");
        manager.load(SKIN, Skin.class, params);
    }

    private void loadFonts() {
        manager.load(FONT, BitmapFont.class);
    }

    public void loadEverything() {
        loadImages();
        loadSkins();
        loadSounds();
        loadFonts();
    }
}
