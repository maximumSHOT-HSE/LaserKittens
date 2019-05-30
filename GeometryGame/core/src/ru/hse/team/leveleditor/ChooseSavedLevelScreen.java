package ru.hse.team.leveleditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

import ru.hse.team.Background;
import ru.hse.team.LaserKittens;
import ru.hse.team.settings.about.PagedScrollPane;

public class ChooseSavedLevelScreen implements Screen {

    private final LaserKittens laserKittens;
    private OrthographicCamera camera = new OrthographicCamera();
    private Background background;
    private Stage stage;
    private Menu menu;

    public ChooseSavedLevelScreen(final LaserKittens laserKittens) {
        this.laserKittens = laserKittens;

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
                    laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.CHOOSE_LEVEL_SCREEN);
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
    public void dispose () {
        stage.dispose();
        background.dispose();
    }

    private class Menu {
        private Table table = new Table();
        private Skin skin = laserKittens.assetManager.manager.get("skin/glassy-ui.json", Skin.class);

        private Label titleLabel = new Label("Saved levels", new Label.LabelStyle(laserKittens.font, Color.WHITE));

        List<TextButton> openLevelButtons = new ArrayList<>();

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

            scroll.addPage(buttonsTable);

            table.add(scroll).expand().fill();

        }

        private void initializeButtons() {
            for (int i = 0; i < 10; i++) {
                String levelName = "Empty";
                TextButton button = new TextButton(levelName, skin);
                button.getLabel().setFontScale(2);

                openLevelButtons.add(button);
            }
        }

    }
}

