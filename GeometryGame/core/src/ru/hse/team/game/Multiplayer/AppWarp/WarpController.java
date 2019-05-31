package ru.hse.team.game.Multiplayer.AppWarp;

import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.MatchedRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import ru.hse.team.game.gamelogic.algorithms.RandomGenerator;

public class WarpController {

    // the size of string which will be added to the left of user name
    private static final int SALT_SIZE = 10;

    private final String API_KEY = "c459fdeab1cfdc5c0f6853fe34d19b166ce6513fc81160a78146422732056788";
    private final String SECRET_KEY = "1a270f8f243c231ba0666226f1349aa1167e1fa6aacf162d0061c1abfa2c6735";
    private static final String LEVEL_NAME_PROPERTY = "level name";

    private static WarpController instance = null;

    private WarpClient warpClient;
    private WarpListener warpListener;
    private Byte connectionStatus = null;
    private String playerName;
    private String playerNameSalt;
    private String roomId = null;
    private Set<String> joinedRooms = new HashSet<>();
    private Set<String> subscribedRooms = new HashSet<>();

    public WarpController() throws Exception {
        WarpClient.initialize(API_KEY, SECRET_KEY);
        warpClient = WarpClient.getInstance();
        warpClient.addConnectionRequestListener(new ConnectionListener(this));
        warpClient.addChatRequestListener(new ChatListener(this));
        warpClient.addZoneRequestListener(new ZoneListener(this));
        warpClient.addRoomRequestListener(new RoomListener(this));
        warpClient.addNotificationListener(new NotificationListener(this));
    }

    public void setWarpListener(WarpListener warpListener) {
        this.warpListener = warpListener;
    }

    /**
     * Returns singleton instance of warp controller.
     * If during creating instance there will be some problems
     * null will returned
     *
     * @return warp controller instance in case of success, otherwise
     * null will be returned
     * */
    public static WarpController getInstance() {
        if (instance == null) {
            try {
                instance = new WarpController();
            } catch (Exception ignored) {
                instance = null;
            }
        }
        return instance;
    }

    public void onConnectDone(byte result) {
        System.out.println("WarpController.onConnectDone(): result = " + result);
        if (result == WarpResponseResultCode.SUCCESS) {
            warpListener.onConnectDone();
        } else {
            warpListener.onConnectError("Connection failure: result code = " + result);
        }
    }

    public void start(String playerName) {
        playerNameSalt = RandomGenerator.generateRandomString(SALT_SIZE);
        this.playerName = playerName;
        warpClient.connectWithUserName(playerNameSalt + playerName);

        System.out.println("WarpController.start(): FULL NAME = " + playerNameSalt + "|" + playerName);
    }

    public void stop() {
        System.out.println("WarpController.stop()");

        for (String roomId : subscribedRooms) {
            warpClient.unsubscribeRoom(roomId);
        }
        for (String roomId : joinedRooms) {
            warpClient.leaveRoom(roomId);
        }
        warpClient.disconnect();

        subscribedRooms.clear();
        joinedRooms.clear();

        playerName = null;
        playerNameSalt = null;
        connectionStatus = null;
    }

    private void disconnect() {
        warpClient.removeConnectionRequestListener(new ConnectionListener(this));
        warpClient.removeChatRequestListener(new ChatListener(this));
        warpClient.removeZoneRequestListener(new ZoneListener(this));
        warpClient.removeRoomRequestListener(new RoomListener(this));
        warpClient.removeNotificationListener(new NotificationListener(this));
        warpClient.disconnect();
    }

    public void sendRequestGetRoomInRangeWithProperties(int minUserNumber, int maxUserNumber, String levelName) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(LEVEL_NAME_PROPERTY, levelName);
        warpClient.getRoomInRangeWithProperties(
                minUserNumber, maxUserNumber,
                properties
        );
    }

    public void onGetMatchedRoomsDone(MatchedRoomsEvent matchedRoomsEvent) {
        warpListener.onGetMatchedRoomsDone(
                matchedRoomsEvent.getResult() == WarpResponseResultCode.SUCCESS,
                matchedRoomsEvent.getRoomsData());
    }

    public void sendRequestCreateRoom(
            String roomName, int maxUsers, String levelName) {
        System.out.println("WarpController.sendRequestCreateRoom(), room name = " + roomName + ", maxUsers = " + maxUsers
                + ", level name = " + levelName);
        HashMap<String, Object> tableProperties = new HashMap<>();
        tableProperties.put(LEVEL_NAME_PROPERTY, levelName);
        warpClient.createRoom(roomName, playerName, maxUsers, tableProperties);
    }

    public void onCreateRoomDone(RoomEvent roomEvent) {
        System.out.println("WarpController.onCreateRoomDone, result = " + roomEvent.getResult() + ", " +
                "data = " + roomEvent.getData() + ", owner = " + roomEvent.getData().getRoomOwner());
        warpListener.onCreateRoomDone(
                roomEvent.getResult() == WarpResponseResultCode.SUCCESS,
                roomEvent.getData());
    }

    public void sendRequestDeleteRoom(String roomId) {
        System.out.println("WarpController.try delete room by user = " + playerName);
        warpClient.deleteRoom(roomId);
    }

    public void onDeleteRoomDone(RoomEvent roomEvent) {
        System.out.println("WarpController.onDeleteDone, result = " + roomEvent.getResult());
        if (roomEvent.getResult() == WarpResponseResultCode.SUCCESS) {
            System.out.println("data = " + roomEvent.getData() + ", owner = " + roomEvent.getData().getRoomOwner());
        }
        warpListener.onDeleteRoomDone(roomEvent.getResult() == WarpResponseResultCode.SUCCESS,
                roomEvent.getData());
    }

    public void sendJoinRoomRequest(String roomId) {
        System.out.println("WarpController.sendJoinRoomRequest() roomID = " + roomId);
        warpClient.joinRoom(roomId);
    }

    public void onJoinRoomDone(RoomEvent roomEvent) {
        System.out.println("WarpController.onJoinRoomDone, result = " + roomEvent.getResult());
        if (roomEvent.getResult() == WarpResponseResultCode.SUCCESS) {
            System.out.println("data = " + roomEvent.getData());
        }
        warpListener.onJoinRoomDone(
                roomEvent.getResult() == WarpResponseResultCode.SUCCESS,
                roomEvent.getData()
        );
        roomId = roomEvent.getData().getId();
    }

    public void sendLeaveRoomRequest() {
        warpClient.leaveRoom(roomId);
    }

    public void onLeaveRoomDone(RoomEvent roomEvent) {
        System.out.println("WarpController.onLeaveRoomDone, result = " + roomEvent.getResult());
        if (roomId == null) {
            System.out.println("ALREADY LEFT");
            return;
        }
        if (roomEvent.getResult() == WarpResponseResultCode.SUCCESS) {
            System.out.println("data = " + roomEvent.getData());
        }
        warpListener.onLeaveRoomDone(
                roomEvent.getResult() == WarpResponseResultCode.SUCCESS,
                roomEvent.getData());
        roomId = null;
    }

    public void sendSubscribeRequest(String roomId) {
        System.out.println("WarpController.sendSubscribeRequest() room id = " + roomId);
        warpClient.subscribeRoom(roomId);
    }

    public void onUserJoinedRoom(RoomData roomData, String userName) {
        System.out.println("WarpController. USER JOINED! userName = " + userName + ", room id = " + roomData.getId());
        if (roomId != null && roomData.getId().equals(roomId)) {
            warpListener.onUserJoinedRoom(userName);
        }
    }

    public void onUserLeftRoom(RoomData roomData, String userName) {
        System.out.println("WarpController. USER LEFT! userName = " + userName + ", room id = " + roomData.getId());
        if (roomId != null && roomData.getId().equals(roomId)) {
            warpListener.onUserLeftRoom(userName);
        }
    }
}
