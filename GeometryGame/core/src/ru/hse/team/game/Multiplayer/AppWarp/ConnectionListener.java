package ru.hse.team.game.Multiplayer.AppWarp;

import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;

public class ConnectionListener implements ConnectionRequestListener {

    private WarpController warpController;

    public ConnectionListener(WarpController warpController) {
        this.warpController = warpController;
    }

    @Override
    public void onConnectDone(ConnectEvent connectEvent) {
        warpController.onConnectDone(connectEvent.getResult() == WarpResponseResultCode.SUCCESS);
    }

    @Override
    public void onDisconnectDone(ConnectEvent connectEvent) {

    }

    @Override
    public void onInitUDPDone(byte b) {
        if (b == WarpResponseResultCode.SUCCESS) {
            warpController.setUDPEnabled(true);
        }
    }
}
