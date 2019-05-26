package ru.hse.team.game.Multiplayer.AppWarp;

import com.shephertz.app42.gaming.multiplayer.client.command.WarpResponseResultCode;
import com.shephertz.app42.gaming.multiplayer.client.events.ChatEvent;
import com.shephertz.app42.gaming.multiplayer.client.listener.ChatRequestListener;

import java.util.ArrayList;

public class ChatListener implements ChatRequestListener {

    private WarpController warpController;

    public ChatListener(WarpController warpController) {
        this.warpController = warpController;
    }

    @Override
    public void onSendChatDone(byte b) {
        warpController.onSendChatDone(b == WarpResponseResultCode.SUCCESS);
    }

    @Override
    public void onSendPrivateChatDone(byte b) {

    }

    @Override
    public void onGetChatHistoryDone(byte b, ArrayList<ChatEvent> arrayList) {

    }
}
