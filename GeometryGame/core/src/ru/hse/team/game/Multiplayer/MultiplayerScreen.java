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
import java.util.List;

import de.tomgrill.gdxdialogs.core.dialogs.GDXButtonDialog;
import ru.hse.team.Background;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;
import ru.hse.team.game.GameScreen;
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
    private List<AbstractMultiplayerLevel> abstractMultiplayerLevels = new ArrayList<>();

    private Label connectionStatusLabel;
    private int choosedLevelId = -1;

    private void fillLevels() {
        abstractMultiplayerLevels.add(
                new MultiplayerQuizLevel(laserKittens, this));
    }

    private AbstractMultiplayerLevel getLevelByName(String levelName) {
        for (AbstractMultiplayerLevel level : abstractMultiplayerLevels) {
            if (level.getLevelName().equals(levelName)) {
                return level;
            }
        }
        return null;
    }

    public MultiplayerScreen(LaserKittens laserKittens) {
        this.laserKittens = laserKittens;
        background = new Background(laserKittens.getAssetManager()
                .getImage(KittensAssetManager.Images.BLUE_BACKGROUND));

        inputMultiplexer = new InputMultiplexer(stage);
        fillLevels();
        connectionStatusLabel = new Label("Waiting...",
                new Label.LabelStyle(laserKittens.getFont(), Color.GRAY));
        connectionStatusLabel.setFontScale(2f);
    }

    private void warpControllerInit() {
        System.out.println("Multiplayer.warpControllerInit");
        warpController = WarpController.getInstance();
        if (warpController != null) {
            warpController.setWarpListener(this);
            warpController.setConnectionStatusLabel(connectionStatusLabel);
            warpController.start(laserKittens.getPreferences().getPlayerName());
        }
    }

    private void processBackKey() {
        System.out.println("MultiplayerScreen.processBackKey()");
        switch (warpController.getState()) {
            case WAITING_OTHER_PLAYERS:
                warpController.sendLeaveAndUnsubscribeRoomRequest();
                break;
            default:
                warpController.stop();
                Gdx.app.postRunnable(() -> laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.MAIN_MENU_SCREEN));
        }
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
        Gdx.app.postRunnable(() -> menu = new Menu(stage));
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
    }

    @Override
    public void update(String message) {
        updateStage();
    }

    @Override
    public void start() {
        AbstractMultiplayerLevel level = abstractMultiplayerLevels.get(choosedLevelId);
        level.setRole(warpController.getRole());
        Gdx.app.postRunnable(() -> laserKittens.setScreen(new GameScreen(laserKittens, level)));
    }

    private class Menu {

        private Skin skin = laserKittens.getAssetManager().getSkin(KittensAssetManager.Skins.BLUE_SKIN);
        private Table table = new Table();
        private TextButton createRoomButton = new TextButton("create room", skin);
        private TextButton refreshRoomsButton = new TextButton("refresh", skin);
        private Label levelNameFilterLabel = new Label("FILTER ME",
                new Label.LabelStyle(laserKittens.getFont(), Color.BLACK));

        private void addRooms() {
            Table roomTable = new Table().pad(50).align(Align.top);
            roomTable.defaults().pad(10, 10, 10, 10);

            PagedScrollPane scroll = new PagedScrollPane(skin);
            scroll.setFlingTime(0.1f);
            scroll.setPageSpacing(25);

            for (RoomData roomData : warpController.getActiveRooms()) {
                Label roomLabel = new Label(roomData.getId() + "#" + roomData.getName(),
                        new Label.LabelStyle(laserKittens.getFont(), Color.YELLOW));
                roomLabel.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        GDXButtonDialog bDialog = laserKittens.getDialogs().newDialog(GDXButtonDialog.class);
                        bDialog.setTitle("Room manager");
                        bDialog.setMessage("Choose action");
                        bDialog.setCancelable(true);
                        bDialog.addButton("Join") // 0
                                .addButton("Delete"); // 1
                        bDialog.setClickListener(button -> {
                            switch (button) {
                                case 0: // Join
                                    if (warpController != null && warpController.getState().equals(WarpController.State.CONNECTION_DONE)) {
                                        if (choosedLevelId != -1) {
                                            warpController.sendJoinAndSubscribeRoomRequest(roomData.getId());
                                        }
                                    }
                                    break;
                                case 1: // Delete
                                    if (warpController != null && warpController.getState().equals(WarpController.State.CONNECTION_DONE)) {
                                        warpController.sendRequestDeleteRoom(roomData.getId());
                                    }
                                    break;
                            }
                        });

                        bDialog.build().show();
                    }
                });
                roomTable.row();
                roomTable.add(roomLabel);
            }

            scroll.addPage(roomTable);

            table.add(scroll).colspan(3);
        }

        public Menu(Stage stage) {

            table.setFillParent(true);
            stage.addActor(table);

            connectionStatusLabel.setFontScale(1f * LaserKittens.scaleToPreferredWidth());
            levelNameFilterLabel.setFontScale(1f * LaserKittens.scaleToPreferredWidth());
            createRoomButton.getLabel().setFontScale(1f * LaserKittens.scaleToPreferredWidth());
            refreshRoomsButton.getLabel().setFontScale(1f * LaserKittens.scaleToPreferredWidth());
            if (choosedLevelId != -1) {
                levelNameFilterLabel
                        .setText(abstractMultiplayerLevels.get(choosedLevelId).getLevelName());
            }

            levelNameFilterLabel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    GDXButtonDialog bDialog = laserKittens.getDialogs().newDialog(GDXButtonDialog.class);
                    bDialog.setTitle("Choose level to play");
                    bDialog.setMessage("Choose the level");
                    bDialog.setCancelable(true);

                    for (AbstractMultiplayerLevel level : abstractMultiplayerLevels) {
                        bDialog.addButton(level.getLevelName());
                    }

                    bDialog.setClickListener(button -> {
                        choosedLevelId = button;
                        levelNameFilterLabel
                                .setText(abstractMultiplayerLevels.get(button).getLevelName());
                    });

                    bDialog.build().show();
                }
            });

            createRoomButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (warpController.getState().equals(WarpController.State.FAILURE)) {
                        createRoomButton.setDisabled(true);
                        return;
                    } else {
                        createRoomButton.setDisabled(false);
                    }

                    GDXButtonDialog bDialog = laserKittens.getDialogs().newDialog(GDXButtonDialog.class);
                    bDialog.setTitle("Create level");
                    bDialog.setMessage("Choose the level");
                    bDialog.setCancelable(true);

                    for (AbstractMultiplayerLevel level : abstractMultiplayerLevels) {
                        bDialog.addButton(level.getLevelName());
                    }

                    bDialog.setClickListener(button -> {
                        choosedLevelId = button;
                        warpController.sendRequestCreateRoom(
                                RandomGenerator.generateRandomString(10),
                                abstractMultiplayerLevels.get(button).getNumberOfPlayers(),
                                abstractMultiplayerLevels.get(button).getLevelName());
                        levelNameFilterLabel
                                .setText(abstractMultiplayerLevels.get(choosedLevelId).getLevelName());
                    });

                    bDialog.build().show();
                }
            });

            refreshRoomsButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (warpController != null && !warpController.getState().equals(WarpController.State.FAILURE)) {
                        refreshRoomsButton.setDisabled(false);
                        warpController.sendRequestGetRoomInRangeWithProperties(
                                0,
                                choosedLevelId == -1 ? (int) 1e9 :
                                        abstractMultiplayerLevels.get(choosedLevelId).getNumberOfPlayers() - 1,
                                levelNameFilterLabel.getText().toString());
                    } else {
                        refreshRoomsButton.setDisabled(true);
                    }
                }
            });

            table.row().pad(10, 10, 10, 10);
            table.add(connectionStatusLabel).colspan(1);
            table.add(levelNameFilterLabel).colspan(2);
            table.row().pad(10, 10, 10, 10);
            table.add(createRoomButton).width(Gdx.graphics.getWidth() * 0.4f)
                    .height(Gdx.graphics.getHeight() * 0.05f);
            table.add(refreshRoomsButton).width(Gdx.graphics.getWidth() * 0.4f)
                    .height(Gdx.graphics.getHeight() * 0.05f);
            table.row().pad(10, 10, 10, 10);

            if (warpController != null) {
                addRooms();
            }
        }
    }
}
