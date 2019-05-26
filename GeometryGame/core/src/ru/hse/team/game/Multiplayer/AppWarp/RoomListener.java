package ru.hse.team.game.Multiplayer.AppWarp;

import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
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
        if (roomEvent.getResult() == WarpResponseResultCode.SUCCESS) {
            warpController.onRoomSubscribed(roomEvent);
        } else {
            warpController.onRoomSubscribed(null);
        }
    }

    @Override
    public void onUnSubscribeRoomDone(RoomEvent roomEvent) {

    }

    @Override
    public void onJoinRoomDone(RoomEvent roomEvent) {
        warpController.onJoinRoomDone(roomEvent);
    }

    @Override
    public void onLeaveRoomDone(RoomEvent roomEvent) {

    }

    @Override
    public void onGetLiveRoomInfoDone(LiveRoomInfoEvent liveRoomInfoEvent) {
        if (liveRoomInfoEvent.getResult() == WarpResponseResultCode.SUCCESS) {
            warpController.onGetLiveRoomInfoDone(liveRoomInfoEvent);
        } else {
            warpController.onGetLiveRoomInfoDone(null);
        }
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

    }

    @Override
    public void onLeaveAndUnsubscribeRoomDone(RoomEvent roomEvent) {

    }
}
