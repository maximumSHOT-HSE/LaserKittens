package com.example.learning.game.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Contains image to draw.
 */
public class TextureComponent implements Component, Poolable {
    public TextureRegion region;

    @Override
    public void reset() {
        region = null;
    }
}