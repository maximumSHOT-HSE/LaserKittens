package ru.hse.team.game.Multiplayer.AppWarp;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.UpdateEvent;

import java.util.Random;

public class WarpController {

    private static final Random random = new Random(System.currentTimeMillis());

    private final String API_KEY = "73b9a6fc13b16819e033f3be473a6da0cfb460b5dc396cbeadadf945fb26736b";
    private final String SECRET_KEY = "e6b08e9e2b7c46d20ae29f5705646791d5ee135966c80d0dd0a9f412298f16ff";

    private WarpClient warpClient;

    private String localUser;
    private String roomId;
    private boolean isConnected = false;

    private boolean isUDPEnabled = false;

    private WarpListener warpListener;
    private static WarpController instance = null;

    public enum WarpState {
        PREPARING,
        WAITING,
        STARTED,
        COMPLETED,
        FINISHED
    }

    public enum EndType {
        SOLVED,
        PARTNER_LEFT
    }

    private WarpState warpState = WarpState.PREPARING;

    public WarpController()  {
        WarpClient.initialize(API_KEY, SECRET_KEY);
        try {
            warpClient = WarpClient.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        warpClient.addConnectionRequestListener(new ConnectionListener(this));
        warpClient.addChatRequestListener(new ChatListener(this));
        warpClient.addZoneRequestListener(new ZoneListener(this));
        warpClient.addRoomRequestListener(new RoomListener(this));
        warpClient.addNotificationListener(new NotificationListener(this));
    }

    private void startGame() {
        warpState = WarpState.STARTED;
        warpListener.onGameStarted("Start the Game");
    }

    private void waitForOtherUser() {
        warpState = WarpState.WAITING;
        warpListener.onWaitingStarted("Waiting for the other player");
    }

    public static WarpController getInstance() {
        if (instance == null) {
            instance = new WarpController();
        }
        return instance;
    }

    public void onConnectDone(boolean status) {
        System.out.println("WarpController.onConnectDone: status = " + status);
        if (status) {
            warpClient.initUDP();
            warpClient.joinRoomInRange(1, 5, false);
        } else {
            isConnected = false;

        }
    }

    public void onGameUpdateReceived(UpdateEvent event) {
        warpListener.onGameUpdateReceived("event");
    }

    public void setWarpListener(WarpListener warpListener) {
        this.warpListener = warpListener;
    }

    public void onUserJoinedRoom(RoomData roomData, String userName) {
        if (!localUser.equals(userName)) {
            startGame();
        }
    }

    public void onUserLeftRoom(RoomData roomData, String userName) {
        if (warpState.equals(WarpState.STARTED) && !localUser.equals(userName)) {
            warpListener.onGameFinished(EndType.PARTNER_LEFT, true);
        }
    }

    public void onRoomSubscribed(RoomEvent roomEvent) {
        if (roomEvent == null) {
            warpClient.disconnect();
            processError();
        } else {
            isConnected = true;
            warpClient.getLiveRoomInfo(roomEvent.getData().getId());
        }
    }

    public void onRoomCreated(RoomEvent roomEvent) {
        if (roomEvent != null) {
            warpClient.joinRoom(roomEvent.getData().getId());
        } else {
            processError();
        }
    }

    public void onSendChatDone(boolean status) {
        System.out.println("on send chat done: " + status);
    }

    public void onGetLiveRoomInfoDone(LiveRoomInfoEvent liveRoomInfoEvent) {
        String[] liveUsers = liveRoomInfoEvent.getJoinedUsers();
        if (liveUsers != null) {
            if (liveUsers.length == 2) {
                startGame();
            } else {
                waitForOtherUser();
            }
        } else {
            warpClient.disconnect();
            processError();
        }
    }

    public void onJoinRoomDone(RoomEvent event) {
        System.out.println("WarpController.onJoinRoomDone: " + event.getResult());
        if (event.getResult() == WarpResponseResultCode.SUCCESS) {
            this.roomId = event.getData().getId();
            warpClient.subscribeRoom(roomId);
            System.out.println("ROOM ID = " + this.roomId);
        } else {
            if (event.getResult() == WarpResponseResultCode.RESOURCE_NOT_FOUND) {
                warpClient.createRoom("Room#" + WarpController.generateRandomName(),
                        "RoomOwner#" + WarpController.generateRandomName(),
                        2, null);
            } else {
                warpClient.disconnect();
                processError();
            }
        }
    }

    private void processError() {
        if (roomId != null && roomId.length() > 0) {
            warpClient.deleteRoom(roomId);
        }
        disconnect();
    }

    private void processLeave() {
        if (isConnected) {
            warpClient.unsubscribeRoom(roomId);
            warpClient.leaveRoom(roomId);

            if (!warpState.equals(WarpState.STARTED)) {
                warpClient.deleteRoom(roomId);
            }
            warpClient.disconnect();
        }
    }

    public void start(String localUser) {
        this.localUser = localUser;
        warpClient.connectWithUserName(localUser);
    }

    public boolean isUDPEnabled() {
        return isUDPEnabled;
    }

    public void setUDPEnabled(boolean UDPEnabled) {
        isUDPEnabled = UDPEnabled;
    }

    private void disconnect() {
        System.out.println("DISCONNECT");
        warpClient.removeConnectionRequestListener(new ConnectionListener(this));
        warpClient.removeChatRequestListener(new ChatListener(this));
        warpClient.removeZoneRequestListener(new ZoneListener(this));
        warpClient.removeRoomRequestListener(new RoomListener(this));
        warpClient.removeNotificationListener(new NotificationListener(this));
        warpClient.disconnect();
    }

    public String getLocalUser() {
        return localUser;
    }

    public String getRoomId() {
        return roomId;
    }

    public static String generateRandomName() {
        int length = 8;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append((char) (random.nextInt(26) + 'A'));
        }
        return stringBuilder.toString();
    }
}
