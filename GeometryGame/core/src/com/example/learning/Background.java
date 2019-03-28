package com.example.learning;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Background {
    private Texture backgroundTexture;
    private Sprite backgroundSprite;


    public Background(Texture background) {
        backgroundTexture = background;
        backgroundTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        backgroundSprite = new Sprite(backgroundTexture);
    }

    public void draw(SpriteBatch batch, Camera camera) {
        batch.draw(
            backgroundTexture,
                0, 0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight()
        );
    }

    public void dispose() {
        backgroundTexture.dispose();
    }
}
