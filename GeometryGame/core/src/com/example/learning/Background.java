package com.example.learning;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Background {

    private Texture backgroundTexture;

    public Background(Texture background) {
        backgroundTexture = background;
        resizeClampToEdge();
    }

    public void resizeClampToEdge() {
        backgroundTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
    }

    public void draw(SpriteBatch batch, Camera camera) {
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
