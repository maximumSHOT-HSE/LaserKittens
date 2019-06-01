package ru.hse.team.game.Multiplayer.AppWarp;

import com.shephertz.app42.gaming.multiplayer.client.events.LiveRoomInfoEvent;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.RoomRequestListener;

public class RoomListener implements RoomRequestListener {

    private WarpController warpController;

    public RoomListener(WarpController warpController) {
        this.warpController = warpController;
    }

    @Override
    public void onSubscribeRoomDone(RoomEvent roomEvent) {
        warpController.onSubscribeRoomDone(roomEvent);
    }

    @Override
    public void onUnSubscribeRoomDone(RoomEvent roomEvent) {

    }

    @Override
    public void onJoinRoomDone(RoomEvent roomEvent) {
    }

    @Override
    public void onLeaveRoomDone(RoomEvent roomEvent) {
    }

    @Override
    public void onGetLiveRoomInfoDone(LiveRoomInfoEvent liveRoomInfoEvent) {
        System.out.println("RoomListener.onGetLiveRoomInfoDone(), users = ");
        for (String userName : liveRoomInfoEvent.getJoinedUsers()) {
            System.out.print(userName + " ");
        }
        System.out.println();
        warpController.onGetLiveRoomInfoDone(liveRoomInfoEvent);
    }

    @Override
    public void onSetCustomRoomDataDone(LiveRoomInfoEvent liveRoomInfoEvent) {

    }

    @Override
    public void onUpdatePropertyDone(LiveRoomInfoEvent liveRoomInfoEvent) {

    }

    @Override
    public void onLockPropertiesDone(byte b) {

    }

    @Override
    public void onUnlockPropertiesDone(byte b) {

    }

    @Override
    public void onJoinAndSubscribeRoomDone(RoomEvent roomEvent) {
        System.out.println("RoomListener.onJoinAndSubscribeRoomDone " + roomEvent.getResult());
        warpController.onJoinAndSubscribeRoomDone(roomEvent);
    }

    @Override
    public void onLeaveAndUnsubscribeRoomDone(RoomEvent roomEvent) {
        System.out.println("RoomListener.onLeaveRoomDone " + roomEvent.getResult());
        warpController.onLeaveAndUnsubscribeRoomDone(roomEvent);
    }
}
