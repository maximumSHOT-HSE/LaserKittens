package ru.hse.team.leveleditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

import ru.hse.team.Background;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;
import ru.hse.team.database.levels.SavedLevel;
import ru.hse.team.settings.about.PagedScrollPane;

/**
 * Screen for saving levels.
 * Prompts user to name new level.
 * Won't accept a name if it is too long or already used.
 */
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

        background = new Background(laserKittens.getAssetManager()
                .getImage(KittensAssetManager.Images.BLUE_BACKGROUND));
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
        laserKittens.getBatch().setProjectionMatrix(camera.combined);
    }

    @Override
    public void render(float delta) {
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

    /**
     * Get's all {@code SavedLevel} instances from database.
     */
    private List<SavedLevel> allLevels() {
        List<List<SavedLevel>> levelsList = new ArrayList<>();
        Thread t = new Thread(() -> {
            levelsList.add(laserKittens.getDatabase().levelsDao().getAll());
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return levelsList.get(0);
    }

    private class Menu {
        private Table table = new Table();
        private Skin skin = laserKittens.getAssetManager().getSkin(KittensAssetManager.Skins.BLUE_SKIN);

        private Label titleLabel = new Label("Save level", new Label.LabelStyle(laserKittens.getFont(), Color.WHITE));
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
                    savedLevel.id = 1;
                    for (SavedLevel level : levels) {
                        savedLevel.id = Math.max(savedLevel.id, level.id + 1);
                    }
                    savedLevel.levelName = null;
                    AskName listener = new AskName(savedLevel, levels, laserKittens);
                    Gdx.input.getTextInput(listener, "Add a name to your level", "", null);
                }
            });
        }

    }


    private static class AskName implements Input.TextInputListener {

        private final SavedLevel level;
        private final List<SavedLevel> levels;
        private final LaserKittens laserKittens;

        public AskName(SavedLevel level, List<SavedLevel> levels, LaserKittens laserKittens) {
            this.level = level;
            this.levels = levels;
            this.laserKittens = laserKittens;
        }

        @Override
        public void input (String text) {
            level.levelName = " " + text + " ";

            if (text.length() > 10) {
                AskName listener = new AskName(level, levels, laserKittens);
                Gdx.input.getTextInput(listener, "Add a name to your level",
                        "", "Max length is 10 symbols");
                return;
            }

            for (SavedLevel savedLevel : levels) {
                if (level.levelName.equals(savedLevel.levelName)) {
                    AskName listener = new AskName(level, levels, laserKittens);
                    Gdx.input.getTextInput(listener, "Add a name to your level",
                            "", "Name already exist");
                    return;
                }
            }

            addLevel();
            laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.SAVED_LEVELS_SCREEN);
        }

        @Override
        public void canceled () {
        }

        /**
         * Add new {@code SavedLevel} to database.
         */
        private void addLevel() {
            Thread t =  (new Thread(() -> {
                laserKittens.getDatabase().levelsDao().insert(level);
            }));
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}