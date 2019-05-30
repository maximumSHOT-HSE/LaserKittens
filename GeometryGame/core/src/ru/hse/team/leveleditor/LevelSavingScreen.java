package ru.hse.team.leveleditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

import ru.hse.team.Background;
import ru.hse.team.LaserKittens;
import ru.hse.team.database.levels.SavedLevel;
import ru.hse.team.settings.about.PagedScrollPane;

public class LevelSavingScreen implements Screen {

    private final LaserKittens laserKittens;
    private OrthographicCamera camera = new OrthographicCamera();
    private Background background;
    private Stage stage;
    private Menu menu;

    private final SavedLevel savedLevel;

    public LevelSavingScreen(final LaserKittens laserKittens, SavedLevel savedLevel) {
        this.laserKittens = laserKittens;
        this.savedLevel = savedLevel;

        background = new Background(this.laserKittens.assetManager.manager.get("blue-background.jpg", Texture.class));
        stage = new Stage(new ScreenViewport());
    }

    @Override
    public void show() {
        stage.clear();
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.LEVEL_CREATE_SCREEN);
                }
                return true;
            }
        });
        menu = new Menu(stage);
        Gdx.input.setInputProcessor(stage);

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        laserKittens.batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        laserKittens.batch.begin();
        background.draw(laserKittens.batch, camera);
        laserKittens.batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        background.resizeClampToEdge();
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
        background.dispose();
    }

    private List<SavedLevel> allLevels() {
        List<List<SavedLevel>> levelsList = new ArrayList<>();
        Thread t = new Thread(() -> {
            levelsList.add(laserKittens.getSavedLevels().levelsDao().getAll());
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return levelsList.get(0);
    }

    private void addLevel() {
       Thread t =  (new Thread(() -> {
            laserKittens.getSavedLevels().levelsDao().insert(savedLevel);
        }));
       t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class Menu {
        private Table table = new Table();
        private Skin skin = laserKittens.assetManager.manager.get("skin/glassy-ui.json", Skin.class);

        private Label titleLabel = new Label("Saved levels", new Label.LabelStyle(laserKittens.font, Color.WHITE));
        private TextButton newLevelButton = new TextButton("New level", skin);

        List<TextButton> openLevelButtons = new ArrayList<>();

        List<SavedLevel> levels = allLevels();

        public Menu(Stage stage) {
            stage.addActor(table);
            table.setFillParent(true);
            //table.setDebug(true);

            titleLabel.setFontScale(4f);
            table.add(titleLabel);
            table.row().pad(10, 10, 10, 10);

            initializeButtons();

            PagedScrollPane scroll = new PagedScrollPane(skin);
            scroll.setFlingTime(0.1f);
            scroll.setPageSpacing(25);
            Table buttonsTable = new Table();

            final float buttonHeight = Gdx.graphics.getHeight() * 0.15f;
            final float buttonWidth = Gdx.graphics.getWidth() * 0.65f;
            for (TextButton button : openLevelButtons) {
                buttonsTable.row().pad(10, 10, 10, 10);
                buttonsTable.add(button).width(buttonWidth).height(buttonHeight);
            }
            buttonsTable.row().pad(10, 10, 10, 10);
            buttonsTable.add(newLevelButton).width(buttonWidth).height(buttonHeight);
            scroll.addPage(buttonsTable);

            table.add(scroll).expand().fill();

        }

        private void initializeButtons() {

            newLevelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    savedLevel.id = levels.size();
                    savedLevel.levelName = "AArgh";
                    addLevel();

                    laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.SAVED_LEVELS_SCREEN);
                }
            });
        }

    }
}