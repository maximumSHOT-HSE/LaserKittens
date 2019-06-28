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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

import de.tomgrill.gdxdialogs.core.dialogs.GDXButtonDialog;
import ru.hse.team.Background;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;
import ru.hse.team.database.levels.SavedLevel;
import ru.hse.team.game.GameScreen;
import ru.hse.team.settings.about.PagedScrollPane;

/**
 * Screen that shows all levels made with levelEditor tool.
 * Allows to launch and delete these levels.
 */
public class ChooseSavedLevelScreen implements Screen {
    private final LaserKittens laserKittens;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final Background background;
    private final Stage stage;
    private Menu menu;
    private List<SavedLevel> levels;

    public ChooseSavedLevelScreen(final LaserKittens laserKittens) {
        this.laserKittens = laserKittens;
        background = new Background(this.laserKittens.getAssetManager()
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
                    laserKittens.changeScreen(LaserKittens.ScreenType.CHOOSE_LEVEL_SCREEN);
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
    public void dispose () {
        stage.dispose();
        background.dispose();
    }

    /**
     * Get's all saved levels from database.
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

    /**
     * Deletes savedLevel from database by it's id.
     */
    private void deleteLevel(int id) {
        Thread t = new Thread(() -> {
            laserKittens.getDatabase().levelsDao().delete(levels.get(id));
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class Menu {
        private final Table table = new Table();
        private final Skin skin = laserKittens.getAssetManager()
                .getSkin(KittensAssetManager.Skins.BLUE_SKIN);
        private Label titleLabel = new Label("Saved levels",
                new Label.LabelStyle(laserKittens.getFont(), Color.WHITE));
        private final List<TextButton> openLevelButtons = new ArrayList<>();

        public Menu(Stage stage) {
            levels = allLevels();
            stage.addActor(table);
            table.setFillParent(true);

            titleLabel.setFontScale(4f * LaserKittens.scaleToPreferredWidth());
            table.add(titleLabel);
            table.row().pad(10, 10, 10, 10);

            initializeButtons();

            PagedScrollPane scroll = new PagedScrollPane(skin);
            scroll.setFlingTime(0.1f);
            scroll.setPageSpacing(25);
            Table buttonsTable = new Table();

            final float buttonHeight = Gdx.graphics.getHeight() * 0.15f;
            final float buttonWidth = Gdx.graphics.getWidth() * 0.65f;
            for (int i = 0; i < openLevelButtons.size(); i++) {
                TextButton button = openLevelButtons.get(i);
                buttonsTable.row().pad(10, 10, 10, 10);
                buttonsTable.add(button).width(buttonWidth).height(buttonHeight);

                ImageButton binButton = new ImageButton(new TextureRegionDrawable(
                        laserKittens.getAssetManager().getImage(KittensAssetManager.Images.DELETE)));
                buttonsTable.add(binButton).width(0.15f * Gdx.graphics.getWidth()).height(0.15f * Gdx.graphics.getHeight());

                final int ii = i;
                binButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {

                        GDXButtonDialog bDialog = laserKittens.getDialogs().newDialog(GDXButtonDialog.class);
                        bDialog.setTitle("Are you sure?");
                        bDialog.setMessage("It will not be possible to restore the level");

                        bDialog.setClickListener(button1 -> {
                            if (button1 == 0) {
                                deleteLevel(ii);
                                laserKittens.changeScreen(LaserKittens.ScreenType.SAVED_LEVELS_SCREEN);
                            }
                        });

                        bDialog.addButton("Yes");
                        bDialog.addButton("No");

                        bDialog.build().show();
                    }
                });
            }
            scroll.addPage(buttonsTable);
            table.add(scroll).expand().fill();
        }

        private void initializeButtons() {
            for (int i = 0; i < levels.size(); i++) {
                String levelName = levels.get(i).levelName;
                TextButton button = new TextButton(levelName, skin);
                button.getLabel().setFontScale(1.5f * LaserKittens.scaleToPreferredWidth());
                final int ii = i;
                button.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        laserKittens.setScreen(new GameScreen(laserKittens, LevelGenerator.generate(levels.get(ii))));
                    }
                });
                openLevelButtons.add(button);
            }
        }
    }
}

