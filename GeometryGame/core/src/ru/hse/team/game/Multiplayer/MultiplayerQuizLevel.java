package ru.hse.team.game.Multiplayer;

import com.badlogic.ashley.core.PooledEngine;

import org.json.JSONObject;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;
import ru.hse.team.game.MessageCreator;
import ru.hse.team.game.Multiplayer.AppWarp.WarpController;
import ru.hse.team.game.Multiplayer.AppWarp.WarpListener;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class MultiplayerQuizLevel extends AbstractLevel implements WarpListener {

    private static final int WIDTH_SCREENS = 2;
    private static final int HEIGHT_SCREENS = 2;

    private MultiplayerQuizLevelFactory multiplayerQuizLevelFactory;
    private LaserKittens parent;
    private MultiplayerScreen multiplayerScreen;
    private int role = 1;

    public MultiplayerQuizLevel(LaserKittens parent, MultiplayerScreen multiplayerScreen, int role) {
        super("Multiplayer Quiz");
        this.parent = parent;
        this.multiplayerScreen = multiplayerScreen;
        this.role = role;
        WarpController.getInstance().setWarpListener(this);
    }

    @Override
    public void onWaitingStarted(String message) {

    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onGameStarted(String message) {

    }

    @Override
    public void onGameFinished(WarpController.EndType endType, boolean isRemote) {

    }

    @Override
    public void onGameUpdateReceived(String message) {
        System.out.println("MultiplayerQuizLevel.onGameUpdateReceived: " + message);
        JSONObject data = new JSONObject(message);
        String type = data.getString(MessageCreator.TYPE);
        System.out.println("type = " + type);
        switch (type) {
            case MessageCreator.CATCH_KEY:
                int id = data.getInt(MessageCreator.KEY_ID);
                System.out.println("KEY ID = " + id);
                getFactory().removeKey(id);
                break;
        }
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        multiplayerQuizLevelFactory = new MultiplayerQuizLevelFactory(role);
        multiplayerQuizLevelFactory.setLevelSize(WIDTH_SCREENS, HEIGHT_SCREENS);
        multiplayerQuizLevelFactory.createLevel(engine, assetManager);
    }

    @Override
    public AbstractLevelFactory getFactory() {
        return multiplayerQuizLevelFactory;
    }

    @Override
    public boolean isMultiplayer() {
        return true;
    }
}
