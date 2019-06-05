package ru.hse.team.game.gamelogic.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import java.util.concurrent.TimeUnit;

import ru.hse.team.AndroidActions;
import ru.hse.team.game.Mapper;
import ru.hse.team.game.gamelogic.components.MessageComponent;

/**
 * Displays messages.
 * Interval between any message showing is at least {@code SHOWING_INTERVAL}
 */
public class MessageSystem extends IteratingSystem {

    //seconds
    public static final long SHOWING_INTERVAL = 3;

    private final AndroidActions androidActions;

    public MessageSystem(AndroidActions androidActions) {
        super(Family.all(MessageComponent.class).get());
        this.androidActions = androidActions;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        MessageComponent message = Mapper.messageComponent.get(entity);

        long currentTimeMillis = System.currentTimeMillis();
        if (message.likeToShow &&
                currentTimeMillis - message.lastTimeShown > TimeUnit.SECONDS.toMillis(SHOWING_INTERVAL)) {
            androidActions.showToast(message.message, false);
            message.lastTimeShown = currentTimeMillis;
            message.likeToShow = false;
        }
    }
}

