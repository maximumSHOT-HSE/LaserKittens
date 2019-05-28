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

    private final String API_KEY = "c459fdeab1cfdc5c0f6853fe34d19b166ce6513fc81160a78146422732056788";
    private final String SECRET_KEY = "1a270f8f243c231ba0666226f1349aa1167e1fa6aacf162d0061c1abfa2c6735";

    private WarpClient warpClient;

    private String localUser;
    private String roomId;
    private String roomOwner;
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
        warpListener.onGameStarted(localUser.equals(roomOwner) ? "1" : "2");
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
        if (event == null) {
            return;
        }
        String message = new String(event.getUpdate());
        String userName = message.substring(0, message.indexOf("#@"));
        String data = message.substring(message.indexOf("#@") + 2);
        System.out.println("WarpController.onGameUpdateReceived, msg = " + message + ", userName = " + userName + ", data = " + data);
        if (!localUser.equals(userName)) {
            warpListener.onGameUpdateReceived(data);
        }
    }

    public void setWarpListener(WarpListener warpListener) {
        this.warpListener = warpListener;
    }

    public void onUserJoinedRoom(RoomData roomData, String userName) {
        System.out.println("WarpController.onUserJoinedRoom: room = " + roomData.getId() + ", userName = " + userName);
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

    public void sendGameUpdate(String message) {
        if(isConnected){
            if(isUDPEnabled){
                warpClient.sendUDPUpdatePeers((localUser + "#@"+ message).getBytes());
            }else{
                warpClient.sendUpdatePeers((localUser + "#@" + message).getBytes());
            }
        }
    }

    public void onJoinRoomDone(RoomEvent event) {
        System.out.println("WarpController.onJoinRoomDone: " + event.getResult());
        if (event.getResult() == WarpResponseResultCode.SUCCESS) {
            this.roomId = event.getData().getId();
            this.roomOwner = event.getData().getRoomOwner();
            warpClient.subscribeRoom(roomId);
            System.out.println("ROOM ID = " + this.roomId);
        } else {
            if (event.getResult() == WarpResponseResultCode.RESOURCE_NOT_FOUND) {
                roomOwner = localUser;
                warpClient.createRoom("Room#" + WarpController.generateRandomName(), localUser,
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

    public void processLeave() {
        System.out.println("WarpController.processLeave: isConnected = " + isConnected + ", warpState = " + warpState);
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
