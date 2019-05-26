package ru.hse.team.game.Multiplayer.AppWarp;

public interface WarpListener {

    void onWaitingStarted(String message);

    void onError(String message);

    void onGameStarted(String message);

    void onGameFinished(int code, boolean isRemote);

    void onGameUpdateReceived(String message);
}
