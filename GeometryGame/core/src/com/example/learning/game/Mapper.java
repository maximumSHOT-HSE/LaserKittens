package com.example.learning.game;

import com.badlogic.ashley.core.ComponentMapper;
import com.example.learning.game.gamelogic.components.BodyComponent;
import com.example.learning.game.gamelogic.components.BulletComponent;
import com.example.learning.game.gamelogic.components.PlayerComponent;
import com.example.learning.game.gamelogic.components.StateComponent;
import com.example.learning.game.gamelogic.components.TextureComponent;
import com.example.learning.game.gamelogic.components.TransformComponent;
import com.example.learning.game.gamelogic.components.TypeComponent;


/**
 * Simple class used for getting entity components.
 */
public class Mapper {
    public static ComponentMapper<BodyComponent> bodyComponent = ComponentMapper.getFor(BodyComponent.class);
    public static ComponentMapper<PlayerComponent> playerComponent = ComponentMapper.getFor(PlayerComponent.class);
    public static ComponentMapper<StateComponent> stateComponent = ComponentMapper.getFor(StateComponent.class);
    public static ComponentMapper<TextureComponent> textureComponent = ComponentMapper.getFor(TextureComponent.class);
    public static ComponentMapper<TransformComponent> transformComponent = ComponentMapper.getFor(TransformComponent.class);
    public static ComponentMapper<TypeComponent> typeComponent = ComponentMapper.getFor(TypeComponent.class);
    public static ComponentMapper<BulletComponent> bulletComponent = ComponentMapper.getFor(BulletComponent.class);

}
