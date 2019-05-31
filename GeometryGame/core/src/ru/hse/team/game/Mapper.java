package ru.hse.team.game;

import com.badlogic.ashley.core.ComponentMapper;

import ru.hse.team.game.gamelogic.components.BodyComponent;
import ru.hse.team.game.gamelogic.components.BulletComponent;
import ru.hse.team.game.gamelogic.components.DoorComponent;
import ru.hse.team.game.gamelogic.components.KeyComponent;
import ru.hse.team.game.gamelogic.components.PatrolComponent;
import ru.hse.team.game.gamelogic.components.StateComponent;
import ru.hse.team.game.gamelogic.components.TextureComponent;
import ru.hse.team.game.gamelogic.components.TransformComponent;
import ru.hse.team.game.gamelogic.components.TumblerComponent;
import ru.hse.team.game.gamelogic.components.TypeComponent;

/**
 * Simple class used for getting entity components.
 */
public class Mapper {
    public static ComponentMapper<BodyComponent> bodyComponent =
            ComponentMapper.getFor(BodyComponent.class);
    public static ComponentMapper<StateComponent> stateComponent =
            ComponentMapper.getFor(StateComponent.class);
    public static ComponentMapper<TextureComponent> textureComponent =
            ComponentMapper.getFor(TextureComponent.class);
    public static ComponentMapper<TransformComponent> transformComponent =
            ComponentMapper.getFor(TransformComponent.class);
    public static ComponentMapper<TypeComponent> typeComponent =
            ComponentMapper.getFor(TypeComponent.class);
    public static ComponentMapper<BulletComponent> bulletComponent =
            ComponentMapper.getFor(BulletComponent.class);
    public static ComponentMapper<KeyComponent> keyComponent =
            ComponentMapper.getFor(KeyComponent.class);
    public static ComponentMapper<DoorComponent> doorComponent = ComponentMapper.getFor(DoorComponent.class);
    public static ComponentMapper<TumblerComponent> tumblerComponent =
            ComponentMapper.getFor(TumblerComponent.class);
    public static ComponentMapper<PatrolComponent> patrolComponent =
            ComponentMapper.getFor(PatrolComponent.class);
}
