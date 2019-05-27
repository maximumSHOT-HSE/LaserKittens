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
        System.out.println("ConnectionListener.onConnectDone: " + connectEvent.getResult());
        warpController.onConnectDone(connectEvent.getResult() == WarpResponseResultCode.SUCCESS);
    }

    @Override
    public void onDisconnectDone(ConnectEvent connectEvent) {

    }

    @Override
    public void onInitUDPDone(byte b) {
        if (b == WarpResponseResultCode.SUCCESS) {
            System.out.println("ConnectionListener.onInitUPDDone: b = " + b);
            warpController.setUDPEnabled(true);
        }
    }
}
