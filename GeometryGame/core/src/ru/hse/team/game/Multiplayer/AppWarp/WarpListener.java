package ru.hse.team.game.Multiplayer.AppWarp;

import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;

public interface WarpListener {

    void onConnectDone();

    void onConnectError(String message);

    void onCreateRoomDone(boolean isSuccess, RoomData roomData);

    void onDeleteRoomDone(boolean isSuccess, RoomData roomData);

    void onGetMatchedRoomsDone(boolean isSuccess, RoomData[] roomDatas);

    void onJoinRoomDone(boolean isSuccess, RoomData roomData);

    void onLeaveRoomDone(boolean isSuccess, RoomData roomData);

    void onUserJoinedRoom(String userName);

    void onUserLeftRoom(String userName);
}
