package ru.hse.team.game.Multiplayer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.tomgrill.gdxdialogs.core.dialogs.GDXButtonDialog;
import de.tomgrill.gdxdialogs.core.listener.ButtonClickListener;
import ru.hse.team.Background;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;
import ru.hse.team.game.Multiplayer.AppWarp.WarpController;
import ru.hse.team.game.Multiplayer.AppWarp.WarpListener;
import ru.hse.team.game.gamelogic.algorithms.RandomGenerator;
import ru.hse.team.settings.about.PagedScrollPane;

public class MultiplayerScreen implements Screen, WarpListener {

    private final LaserKittens laserKittens;
    private OrthographicCamera camera = new OrthographicCamera();
    private Background background;
    private Stage stage = new Stage(new ScreenViewport());
    private Menu menu;
    private InputMultiplexer inputMultiplexer;
    private WarpController warpController = null;
    private List<AbstractMultiplayerLevel> abstractMultiplayerLevels =
            new ArrayList<>();

    private Set<String> activeRoooms = new HashSet<>();
    private Map<String, Label> roomToLabel = new HashMap<>();
    private Map<String, RoomData> nameToRoomData = new HashMap<>();

    private State state = State.WAITING_FOR_CONNECTION;
    private Label connectionStatusLabel;
    private Label joinedRoomLabel;

    private void fillLevels() {
        abstractMultiplayerLevels.add(
                new MultiplayerQuizLevel(laserKittens, this, 0));
    }

    public MultiplayerScreen(LaserKittens laserKittens) {
        this.laserKittens = laserKittens;
        background = new Background(laserKittens.getAssetManager()
                .getImage(KittensAssetManager.Images.BLUE_BACKGROUND));

        inputMultiplexer = new InputMultiplexer(stage);
        connectionStatusLabel = new Label("Waiting...",
                new Label.LabelStyle(laserKittens.getFont(), Color.GRAY));
        joinedRoomLabel = new Label("...",
                new Label.LabelStyle(laserKittens.getFont(), Color.BLACK));
        fillLevels();
    }

    private void warpControllerInit() {
        System.out.println("Multiplayer.warpControllerInit");
        warpController = WarpController.getInstance();
        if (warpController != null) {
            warpController.setWarpListener(this);
            warpController.start(laserKittens.getPreferences().getPlayerName());
        } else {
            menu.setConnectionStatus(State.CONNECITON_FAILURE,
                    "Warp get client failure", Color.RED);
        }
    }

    private void processDisconnect() {
        System.out.println("MultiplayerScreen.processDisconnect()");
        if (state.equals(State.CONNECTION_DONE)) {
            warpController.stop();
        }
        menu.setConnectionStatus(State.WAITING_FOR_CONNECTION,
                "Waiting...", Color.GRAY);
    }

    private void processBackKey() {
        System.out.println("MultiplayerScreen.processBackKey()");
        if (warpController != null) {
            processDisconnect();
        } else {
            menu.setConnectionStatus(State.CONNECITON_FAILURE,
                    "Warp get client failure", Color.RED);
        }
        laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.MAIN_MENU_SCREEN);
    }

    @Override
    public synchronized void render(float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        laserKittens.getBatch().begin();
        background.draw(laserKittens.getBatch());
        laserKittens.getBatch().end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        background.resizeClampToEdge();
    }

    private synchronized void updateStage() {
        stage.clear();
        menu = new Menu(stage);
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    processBackKey();
                }
                return true;
            }
        });
    }

    @Override
    public void show() {
        updateStage();
        warpControllerInit();
        Gdx.input.setInputProcessor(inputMultiplexer);
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        laserKittens.getBatch().setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        System.out.println("MultiplayerScreen().Dispose!");
        processDisconnect();
    }

    @Override
    public void onConnectDone() {
        menu.setConnectionStatus( State.CONNECTION_DONE, "OK.", Color.GREEN);
    }

    @Override
    public void onConnectError(String message) {
        menu.setConnectionStatus(State.CONNECITON_FAILURE, "FAIL: " + message, Color.RED);
    }

    @Override
    public void onCreateRoomDone(boolean isSuccess, RoomData roomData) {
        if (!isSuccess) {
            // TODO process error
            return;
        }
        addRoom(roomData);
    }

    @Override
    public void onDeleteRoomDone(boolean isSuccess, RoomData roomData) {
        if (!isSuccess) {
            // TODO process error
            return;
        }
        removeRoom(roomData);
    }

    @Override
    public void onGetMatchedRoomsDone(boolean isSuccess, RoomData[] roomDatas) {
        if (!isSuccess) {
            // TODO process error
            return;
        }
        activeRoooms.clear();
        roomToLabel.clear();
        nameToRoomData.clear();
        for (RoomData roomData : roomDatas) {
            activeRoooms.add(roomData.getName());
            nameToRoomData.put(roomData.getName(), roomData);
        }
        updateStage();
    }

    public void addRoom(RoomData roomData) {
        activeRoooms.add(roomData.getName());
        nameToRoomData.put(roomData.getName(), roomData);
        updateStage();
    }

    public void removeRoom(RoomData roomData) {
        activeRoooms.remove(roomData.getName());
        roomToLabel.remove(roomData.getName());
        nameToRoomData.remove(roomData.getName());
        updateStage();
    }

    public enum State {
        WAITING_FOR_CONNECTION,
        CONNECTION_DONE,
        CONNECITON_FAILURE
    }

    private class Menu {

        private Skin skin = laserKittens.getAssetManager().getSkin(KittensAssetManager.Skins.BLUE_SKIN);
        private Table table = new Table();
        private TextButton createRoomButton = new TextButton("create room", skin);
        private TextButton refreshRoomsButton = new TextButton("refresh", skin);

        public Menu(Stage stage) {

//            table.setDebug(true);
            table.setFillParent(true);
            stage.addActor(table);

            connectionStatusLabel.setFontScale(1f);
            joinedRoomLabel.setFontScale(1f);

            createRoomButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (warpController != null && state.equals(State.CONNECTION_DONE)) {
                        warpController.sendRequestCreateRoom(
                                RandomGenerator.generateRandomString(6),
                                5, null); // TODO UI
                    }
                }
            });

            refreshRoomsButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (warpController != null && state.equals(State.CONNECTION_DONE)) {
                        warpController.sendRequestGetRoomInRange(0, 5); // TODO add UI for it
                    }
                }
            });

            table.row().pad(10, 10, 10, 10);
            table.add(connectionStatusLabel).colspan(1);
            table.add(joinedRoomLabel).colspan(2);
            table.row().pad(10, 10, 10, 10);
            table.add(createRoomButton).width(Gdx.graphics.getWidth() * 0.4f)
                    .height(Gdx.graphics.getHeight() * 0.05f);
            table.add(refreshRoomsButton).width(Gdx.graphics.getWidth() * 0.4f)
                    .height(Gdx.graphics.getHeight() * 0.05f);
            table.row().pad(10, 10, 10, 10);

            Table roomTable = new Table().pad(50).align(Align.top);
            roomTable.defaults().pad(10, 10, 10, 10);

            PagedScrollPane scroll = new PagedScrollPane(skin);
            scroll.setFlingTime(0.1f);
            scroll.setPageSpacing(25);

            for (String roomName : activeRoooms) {
                RoomData roomData = nameToRoomData.get(roomName);
                Label roomLabel = roomToLabel.get(roomData.getName());
                if (roomLabel == null) {
                    roomLabel = new Label(roomData.getId() + "#" + roomData.getName(),
                            new Label.LabelStyle(laserKittens.getFont(), Color.YELLOW));
                    roomLabel.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            GDXButtonDialog bDialog = laserKittens.getDialogs().newDialog(GDXButtonDialog.class);
                            bDialog.setTitle("Room manager");
                            bDialog.setMessage("Choose action");

                            bDialog.setCancelable(true);

                            bDialog.addButton("Join") // 0
                                .addButton("Leave") // 1
                                .addButton("Delete"); // 2

                            bDialog.setClickListener(new ButtonClickListener() {

                                @Override
                                public void click(int button) {
                                    switch (button) {
                                        case 0: // Join
                                            break;
                                        case 1: // Leave
                                            break;
                                        case 2: // Delete
                                            if (warpController != null && state.equals(State.CONNECTION_DONE)) {
                                                warpController.sendRequestDeleteRoom(roomData.getId());
                                            }
                                            break;
                                    }
                                }
                            });

                            bDialog.build().show();
                        }
                    });
                    roomToLabel.put(roomName, roomLabel);
                }
                roomTable.row();
                roomTable.add(roomLabel);
            }

            scroll.addPage(roomTable);

            table.add(scroll).colspan(3);
        }

        public void setConnectionStatus(State state, String connectionStatus, Color color) {
            MultiplayerScreen.this.state = state;
            connectionStatusLabel.setText(connectionStatus);
            connectionStatusLabel.setColor(color);
        }
    }
}
