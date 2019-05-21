package ru.hse.team;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import javax.xml.soap.Text;

public class KittensAssetManager {

    public final AssetManager manager = new AssetManager();


    //Textures
    public static final String badlogic = "badlogic.jpg";
    public static final String levelIndicatorActive = "levelIndicatorActive.png";
    public static final String levelIndicatorPassive = "levelIndicatorPassive.png";
    //Cats
    public static final String Cat1 = "Cat1.png";
    public static final String Cat2 = "Cat2.png";
    public static final String Cat3 = "Cat3.png";
    //Stars
    public static final String Star1 = "Star1.png";
    public static final String Star2 = "Star2.png";

    // Pointer
    public static final String Pointer = "pointer.png";

    // Question
    public static final String Question = "question.png";

    //BackGrounds
    public static final String blueBackground = "blue-background.jpg";
    public static final String ICE_WALL = "ice-wall.png";
    public static final String TRANSPARENT_WALL = "transparent-wall.png";
    public static final String MIRROR = "mirror.png";

    //Skin
    public static final String skin = "skin/glassy-ui.json";

    //Sounds
    public static final String laserSound = "sounds/laser-shot.wav";

    //Font
    public static final String font = "skin/font-export.fnt";

    public void loadImages() {
        manager.load(badlogic, Texture.class);
        manager.load(blueBackground, Texture.class);
        manager.load(ICE_WALL, Texture.class);
        manager.load(TRANSPARENT_WALL, Texture.class);
        manager.load(MIRROR, Texture.class);
        manager.load(Cat1, Texture.class);
        manager.load(Cat2, Texture.class);
        manager.load(Cat3, Texture.class);
        manager.load(levelIndicatorActive, Texture.class);
        manager.load(levelIndicatorPassive, Texture.class);
        manager.load(Star1, Texture.class);
        manager.load(Star2, Texture.class);
        manager.load(Pointer, Texture.class);
        manager.load(Question, Texture.class);
    }

    public void loadSounds() {
        manager.load(laserSound, Sound.class);
    }

    public void loadSkins() {
        SkinParameter params = new SkinParameter("skin/glassy-ui.atlas");
        manager.load(skin, Skin.class, params);
    }

    public void loadFonts() {
        //TODO add nice font
    }

    public void loadEverything() {
        loadImages();
        loadSkins();
        loadSounds();
        loadFonts();
    }
}
