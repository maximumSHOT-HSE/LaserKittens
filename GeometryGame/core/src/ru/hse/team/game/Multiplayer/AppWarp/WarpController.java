package ru.hse.team.game.Multiplayer.AppWarp;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.shephertz.app42.gaming.multiplayer.client.WarpClient;
import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.MatchedRoomsEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.UpdateEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ru.hse.team.game.gamelogic.algorithms.RandomGenerator;

public class WarpController {

    public enum State {
        WAITING_FOR_CONNECTION,
        CONNECTION_DONE,
        FAILURE,
        JOINING_ROOM,
        LEAVING_ROOM,
        WAITING_OTHER_PLAYERS,
        PLAYING
    }

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
    private Set<String> joinedUsers = new TreeSet<>();
    private Map<String, RoomData> activeRooms = new HashMap<>();
    private Label connectionStatusLabel;
    private State state = State.WAITING_FOR_CONNECTION;

    private void setConnectionStatusText(State state, String text, Color color) {
        this.state = state;
        if (connectionStatusLabel == null) {
            return;
        }
        connectionStatusLabel.setText(text);
        connectionStatusLabel.setColor(color);
    }

    public WarpController() throws Exception {
        WarpClient.initialize(API_KEY, SECRET_KEY);
        warpClient = WarpClient.getInstance();
        warpClient.addConnectionRequestListener(new ConnectionListener(this));
        warpClient.addChatRequestListener(new ChatListener(this));
        warpClient.addZoneRequestListener(new ZoneListener(this));
        warpClient.addRoomRequestListener(new RoomListener(this));
        warpClient.addNotificationListener(new NotificationListener(this));
    }

    public void setConnectionStatusLabel(Label connectionStatusLabel) {
        this.connectionStatusLabel = connectionStatusLabel;
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
            setConnectionStatusText(State.CONNECTION_DONE,"OK", Color.GREEN);
        }
    }

    public void onDisconnectDone(byte result) {
        if (result != WarpResponseResultCode.SUCCESS) {
            setConnectionStatusText(State.FAILURE,"CONNECTION ERROR", Color.RED);
        }
    }

    private void processGameStart(int maxUsers) {
        if (joinedUsers.size() == maxUsers) {
            warpListener.start();
        }
    }

    private void free() {
        subscribedRooms.clear();
        joinedRooms.clear();
        joinedUsers.clear();
        activeRooms.clear();

        playerName = null;
        playerNameSalt = null;
        connectionStatus = null;

        setConnectionStatusText(State.WAITING_FOR_CONNECTION,"Waiting...", Color.GRAY);
    }

    public void start(String playerName) {
        free();
        playerNameSalt = RandomGenerator.generateRandomString(SALT_SIZE);
        System.out.println("WarpController.start(): FULL NAME = " + playerNameSalt + "|" + playerName);

        this.playerName = playerName;

        warpClient.connectWithUserName(playerNameSalt + playerName);
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
        free();
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
        activeRooms.clear();
        if (matchedRoomsEvent.getResult() != WarpResponseResultCode.SUCCESS) {
            return;
        }
        for (RoomData roomData : matchedRoomsEvent.getRoomsData()) {
            activeRooms.put(roomData.getName(), roomData);
        }
        warpListener.update("Get matched room done");
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
        System.out.println("WarpController.onCreateRoomDone, result = " + roomEvent.getResult());
        if (roomEvent.getResult() != WarpResponseResultCode.SUCCESS) {
            return;
        }
        System.out.println("data = " + roomEvent.getData() + ", owner = " + roomEvent.getData().getRoomOwner());
        activeRooms.put(roomEvent.getData().getName(), roomEvent.getData());
        warpListener.update("create room done");
    }

    public void sendRequestDeleteRoom(String roomId) {
        System.out.println("WarpController.try delete room by user = " + playerName);
        warpClient.deleteRoom(roomId);
    }

    public void onDeleteRoomDone(RoomEvent roomEvent) {
        System.out.println("WarpController.onDeleteDone, result = " + roomEvent.getResult());
        if (roomEvent.getResult() != WarpResponseResultCode.SUCCESS) {
            return;
        }
        System.out.println("data = " + roomEvent.getData() + ", owner = " + roomEvent.getData().getRoomOwner());
        activeRooms.remove(roomEvent.getData().getName());
        subscribedRooms.remove(roomEvent.getData().getId());
        joinedRooms.remove(roomEvent.getData().getId());
        warpListener.update("room deleted");
    }

    public void sendJoinAndSubscribeRoomRequest(String roomId) {
        System.out.println("WarpController.sendJoinRoomRequest() roomID = " + roomId);
        setConnectionStatusText(State.JOINING_ROOM,"Joining...", Color.GRAY);
        warpClient.joinAndSubscribeRoom(roomId);
    }

    public void onJoinAndSubscribeRoomDone(RoomEvent roomEvent) {
        System.out.println("WarpController.onJoinAndSubscribeRoomDone, result = " + roomEvent.getResult());
        if (roomEvent.getResult() != WarpResponseResultCode.SUCCESS) {
            return;
        }
        System.out.println("data = " + roomEvent.getData());
        roomId = roomEvent.getData().getId();
        subscribedRooms.add(roomId);
        joinedRooms.add(roomId);
        warpClient.getLiveRoomInfo(roomId);
        warpListener.update("join and subscribe room");
        setConnectionStatusText(State.WAITING_OTHER_PLAYERS,"Waiting others...", Color.YELLOW);
    }

    public void sendLeaveAndUnsubscribeRoomRequest() {
        setConnectionStatusText(State.LEAVING_ROOM,"Leaving...", Color.GRAY);
        warpClient.leaveAndUnsubscribeRoom(roomId);
    }

    public void onLeaveAndUnsubscribeRoomDone(RoomEvent roomEvent) {
        System.out.println("WarpController.onLeaveAndUnsubscribeRoomDone, result = " + roomEvent.getResult());
        if (roomId == null) {
            free();
            System.out.println("ALREADY LEFT");
            return;
        }
        if (roomEvent.getResult() != WarpResponseResultCode.SUCCESS) {
            return;
        }
        System.out.println("data = " + roomEvent.getData());
        roomId = null;
        joinedRooms.remove(roomEvent.getData().getId());
        subscribedRooms.remove(roomEvent.getData().getId());
        setConnectionStatusText(State.CONNECTION_DONE,"OK", Color.GREEN);
    }

    public void onSubscribeRoomDone(RoomEvent roomEvent) {
        subscribedRooms.add(roomEvent.getData().getId());
    }

    public void onUserJoinedRoom(RoomData roomData, String userName) {
        System.out.println("WarpController. USER JOINED! userName = " + userName + ", room id = " + roomData.getId());
        if (roomId != null && roomData.getId().equals(roomId)) {
            joinedUsers.add(userName);
            warpListener.update(userName + " join room");
            setWaitingOthersNumber(roomData.getMaxUsers());
            processGameStart(roomData.getMaxUsers());
        }
    }

    public void onUserLeftRoom(RoomData roomData, String userName) {
        System.out.println("WarpController. USER LEFT! userName = " + userName + ", room id = " + roomData.getId());
        if (roomId != null && roomData.getId().equals(roomId)) {
            joinedUsers.remove(userName);
            warpListener.update(userName + " left rome");
            setWaitingOthersNumber(roomData.getMaxUsers());
        }
    }

    private void setWaitingOthersNumber(int maxUsers) {
        setConnectionStatusText(State.WAITING_OTHER_PLAYERS, "Waiting others ("
                + joinedUsers.size() + "/"
                + maxUsers + ")", Color.ORANGE);
    }

    public void onGetLiveRoomInfoDone(LiveRoomInfoEvent liveRoomInfoEvent) {
        if (liveRoomInfoEvent.getResult() != WarpResponseResultCode.SUCCESS) {
            setConnectionStatusText(State.FAILURE, "Room connection fail", Color.RED);
            return;
        }
        joinedUsers.clear();
        joinedUsers.addAll(Arrays.asList(liveRoomInfoEvent.getJoinedUsers()));
        warpListener.update("Room updated");
        setWaitingOthersNumber(liveRoomInfoEvent.getData().getMaxUsers());
        processGameStart(liveRoomInfoEvent.getData().getMaxUsers());
    }

    public void sendGameUpdate(String message) {
        System.out.println("WarpController.sendGameUpdate, msg = " + message);
        warpClient.sendUpdatePeers((playerNameSalt + playerName + "#@" + message).getBytes());
    }

    public void onGameUpdateReceived(UpdateEvent event) {
        if (event == null) {
            System.out.println("WarpController.update, null :(");
            return;
        }
        String message = new String(event.getUpdate());
        String userName = message.substring(0, message.indexOf("#@"));
        String data = message.substring(message.indexOf("#@") + 2);
        System.out.println("WarpController.onGameUpdateReceived, msg = " + message + ", userName = " + userName + ", data = " + data);
        if (!(playerNameSalt + playerName).equals(userName)) {
            warpListener.update(data);
        }
    }

    public Set<RoomData> getActiveRooms() {
        Set<RoomData> set = new HashSet<>();
        for (Map.Entry<String, RoomData> entry : activeRooms.entrySet()) {
            set.add(entry.getValue());
        }
        return set;
    }

    public State getState() {
        return state;
    }

    public int getRole() {
        int role = 1;
        System.out.println("WarpController.getRole() : ");
        for (String user : joinedUsers) {
            System.out.print(user + " ");
        }
        for (String user : joinedUsers) {
            if (user.equals(playerNameSalt + playerName)) {
                System.out.println(", role = " + role);
                return role;
            } else {
                role++;
            }
        }
        System.out.println("NOT FOUND!!!");
        return role;
    }
}


