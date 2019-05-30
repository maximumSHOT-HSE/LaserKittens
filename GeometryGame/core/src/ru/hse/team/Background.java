package ru.hse.team;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Loads and configures background with given
 * device screen size.
 * */
public class Background {

    private Texture backgroundTexture;

    public Background(Texture background) {
        backgroundTexture = background;
        resizeClampToEdge();
    }

    /**
     * Configures background texture size
     * with given device screen size.
     * */
    public void resizeClampToEdge() {
        backgroundTexture.setWrap(
                Texture.TextureWrap.ClampToEdge,
                Texture.TextureWrap.ClampToEdge);
    }

    /**
     * Draws the background texture
     * using given batch.
     * */
    public void draw(SpriteBatch batch) {
        batch.draw(
            backgroundTexture,
                0f, 0f,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight()
        );
    }

    public void dispose() {
        backgroundTexture.dispose();
    }
}
