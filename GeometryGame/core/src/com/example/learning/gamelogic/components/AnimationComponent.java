package com.example.learning.gamelogic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.IntMap;

import java.util.EnumMap;

public class AnimationComponent implements Component {
    public EnumMap<StateComponent.State, Animation> animations =
            new EnumMap<StateComponent.State, Animation>(StateComponent.State.class);
}
