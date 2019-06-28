package ru.hse.team.game.Multiplayer;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import org.json.JSONObject;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;
import ru.hse.team.game.Multiplayer.AppWarp.WarpListener;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class MultiplayerQuizLevel extends AbstractMultiplayerLevel implements WarpListener {
    private static final int WIDTH_SCREENS = 2;
    private static final int HEIGHT_SCREENS = 2;

    private MultiplayerQuizLevelFactory multiplayerQuizLevelFactory;
    private LaserKittens parent;
    private MultiplayerScreen multiplayerScreen;

    public MultiplayerQuizLevel(LaserKittens parent, MultiplayerScreen multiplayerScreen) {
        super("Multiplayer Quiz", WIDTH_SCREENS, HEIGHT_SCREENS);
        this.parent = parent;
        this.multiplayerScreen = multiplayerScreen;
    }

    @Override
    public void update(String message) {
        System.out.println("MultiplayerQuizLevel.update: " + message);
        JSONObject data = new JSONObject(message);
        String type = data.getString(MessageCreator.TYPE);
        System.out.println("type = " + type);
        switch (type) {
            case MessageCreator.CATCH_KEY:
                int keyID = data.getInt(MessageCreator.KEY_ID);
                System.out.println("KEY ID = " + keyID);
                if (getFactory() != null) {
                    Gdx.app.postRunnable(() -> getFactory().removeKey(keyID));
                }
                break;
            case MessageCreator.CATCH_STAR:
                int starID = data.getInt(MessageCreator.STAR_ID);
                System.out.println("STAR ID = " + starID);
                if (getFactory() != null) {
                    Gdx.app.postRunnable(() -> getFactory().removeStar(starID));
                }
                break;
            case MessageCreator.SHOOT:
                Vector2 source = new Vector2(0, 0);
                Vector2 direction = new Vector2(0, 0);
                int lifetime;
                source.x = Float.parseFloat((String) data.get(MessageCreator.SHOOT_SOURCE + "x"));
                source.y = Float.parseFloat((String) data.get(MessageCreator.SHOOT_SOURCE + "y"));
                direction.x = Float.parseFloat((String) data.get(MessageCreator.SHOOT_DIRECTION + "x"));
                direction.y = Float.parseFloat((String) data.get(MessageCreator.SHOOT_DIRECTION + "y"));
                lifetime = (int) data.get(MessageCreator.SHOOT_LIFETIME);

                float length = (float) Math.sqrt(direction.x * direction.x + direction.y * direction.y);
                float playerRadius = getPlayerRadius() / length;

                Vector2 catPosition = new Vector2(
                    source.x - playerRadius * direction.x * 0.96f,
                    source.y - playerRadius * direction.y * 0.96f
                );
                if (getFactory() != null) {
                    Gdx.app.postRunnable(() -> {
                        getFactory().createLaser(source, direction, lifetime);
                        getFactory().setOpponentPosition(catPosition);
                    });
                }
                break;
            default:
                throw new IllegalArgumentException("Incorrect message");
        }
    }

    @Override
    public void start() {
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        multiplayerQuizLevelFactory = new MultiplayerQuizLevelFactory(
            engine,
            assetManager,
            getBodyFactory(),
            getRole()
        );
        multiplayerQuizLevelFactory.createLevel(
            getWidthInScreens(),
            getHeightInScreens(),
            this
        );
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return multiplayerQuizLevelFactory;
    }

    @Override
    public int getNumberOfPlayers() {
        return 2;
    }
}
