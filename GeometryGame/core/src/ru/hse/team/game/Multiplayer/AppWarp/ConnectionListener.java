package ru.hse.team.game.Multiplayer.AppWarp;

import com.shephertz.app42.gaming.multiplayer.client.events.ConnectEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ConnectionRequestListener;

public class ConnectionListener implements ConnectionRequestListener {

    private WarpController warpController;

    public ConnectionListener(WarpController warpController) {
        this.warpController = warpController;
    }

    @Override
    public void onConnectDone(ConnectEvent connectEvent) {
        System.out.println("ConnectionListener.onConnectDone: result " + connectEvent.getResult());
        warpController.onConnectDone(connectEvent.getResult());
    }

    @Override
    public void onDisconnectDone(ConnectEvent connectEvent) {
        System.out.println("ConnectionListener.onDisconnectDone: result " + connectEvent.getResult());
        warpController.onDisconnectDone(connectEvent.getResult());
    }

    @Override
    public void onInitUDPDone(byte b) {

    }
}
