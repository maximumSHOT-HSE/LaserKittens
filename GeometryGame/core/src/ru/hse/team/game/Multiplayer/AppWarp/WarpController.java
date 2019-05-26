package ru.hse.team.game.Multiplayer.AppWarp;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.UpdateEvent;

public class WarpController {

    private final String API_KEY = "db7156caae79ead6817b78c5c69cae32c369af6a6df6297be5f10dcd18980f3a";
    private final String SECRET_KEY = "8bf9884e1ea5de4304c6ab6d9f70935d908fa5ab163dfc6a4056d08a90fd8293";

    private WarpClient warpClient;

    private String localUser;
    private String roomId;
    private boolean isConnected = false;

    private boolean isUDPEnabled = false;

    private WarpListener warpListener;

    public WarpController() throws Exception {
        WarpClient.initialize(API_KEY, SECRET_KEY);
        warpClient = WarpClient.getInstance();
        warpClient.addConnectionRequestListener(new ConnectionListener(this));
        warpClient.addChatRequestListener(new ChatListener(this));
        warpClient.addZoneRequestListener(new ZoneListener(this));
        warpClient.addRoomRequestListener(new RoomListener(this));
        warpClient.addNotificationListener(new NotificationListener(this));
    }

    public void onConnectDone(boolean status) {

    }

    public void onGameUpdateReceived(UpdateEvent event) {

    }

    public void onUserJoinedRoom(RoomData roomData, String s) {

    }

    public void onUserLeftRoom(RoomData roomData, String s) {

    }

    public boolean isUDPEnabled() {
        return isUDPEnabled;
    }

    public void setUDPEnabled(boolean UDPEnabled) {
        isUDPEnabled = UDPEnabled;
    }
}
