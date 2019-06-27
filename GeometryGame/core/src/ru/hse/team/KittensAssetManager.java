package ru.hse.team;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Class for simplification of assets loading.
 */
public class KittensAssetManager {
    private final AssetManager manager = new AssetManager();

    public enum Images {
        BADLOGIC("badlogic.jpg"),
        LEVEL_INDICATOR_ACTIVE_PNG("levelIndicatorActive.png"),
        LEVEL_INDICATOR_PASSIVE_PNG("levelIndicatorPassive.png"),
        CAT_1("Cat1.png"),
        CAT_2("Cat2.png"),
        CAT_3("Cat3.png"),
        CAT_4("Cat4.png"),
        STAR_1("Star1.png"),
        STAR_2("Star2.png"),
        POINTER("pointer.png"),
        KEY("key.png"),
        YELLOW_TUMBLER("yellow-tumbler.png"),
        BLUE_TUMBLER("blue-tumbler.png"),
        QUESTION("question.png"),
        FOG("fog.png"),
        BLUE_BACKGROUND("blue-background.jpg"),
        ICE_WALL("ice-wall.png"),
        TRANSPARENT_WALL("transparent-wall.png"),
        MIRROR("mirror.png"),
        DOOR("door.png"),
        GOOGLE_SIGN_IN("google-sign-in.png"),
        CUP("cup.png"),
        PLAY_MARKET("play-market.png"),
        ERASER("eraser.png"),
        DELETE("delete.png"),
        CURSOR("cursor.png"),
        OK("ok.png"),
        ROTATE_LEFT("rotate_left.png"),
        ROTATE_RIGHT("rotate_right.png");

        private final String imageName;

        Images(String imageName) {
            this.imageName = imageName;
        }

        public String getImageName() {
            return imageName;
        }
    }

    public enum Skins {
        BLUE_SKIN("skin/glassy-ui.json", "skin/glassy-ui.atlas");

        private final String skinName;
        private final String skinAtlasName;

        Skins(String skinName, String skinAtlasName) {
            this.skinName = skinName;
            this.skinAtlasName = skinAtlasName;
        }

        public String getSkinName() {
            return skinName;
        }

        public String getSkinAtlasName() {
            return skinAtlasName;
        }
    }

    public enum Sounds {
        LASER_SOUND("sounds/laser-shot.wav");

        private final String soundName;

        Sounds(String soundName) {
            this.soundName = soundName;
        }

        public String getSoundName() {
            return soundName;
        }
    }

    public enum Fonts {
        FONT("skin/gameFont.fnt");

        private final String fontName;

        Fonts(String fontName) {
            this.fontName = fontName;
        }

        public String getFontName() {
            return fontName;
        }
    }

    private void loadImages() {
        for (Images image : Images.values()) {
            manager.load(image.getImageName(), Texture.class);
        }
    }

    private void loadSounds() {
        for (Sounds sound : Sounds.values()) {
            manager.load(sound.getSoundName(), Sound.class);
        }
    }

    private void loadSkins() {
        for (Skins skin : Skins.values()) {
            SkinParameter params = new SkinParameter(skin.getSkinAtlasName());
            manager.load(skin.getSkinName(), Skin.class, params);
        }
    }

    private void loadFonts() {
        for (Fonts font : Fonts.values()) {
            manager.load(font.getFontName(), BitmapFont.class);
        }
    }

    public Texture getImage(Images image) {
        return manager.get(image.getImageName(), Texture.class);
    }

    public Skin getSkin(Skins skin) {
        return manager.get(skin.getSkinName(), Skin.class);
    }

    public Sound getSound(Sounds sound) {
        return manager.get(sound.getSoundName(), Sound.class);
    }

    public BitmapFont getFont(Fonts font) {
        return manager.get(font.getFontName(), BitmapFont.class);
    }

    public void loadEverything() {
        loadImages();
        loadSkins();
        loadSounds();
        loadFonts();
    }

    public void finishLoading() {
        manager.finishLoading();
    }

    public void dispose() {
        manager.dispose();
    }
}
