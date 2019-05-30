package ru.hse.team.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ru.hse.team.Background;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;

/**
 * Main menu screen providing an user interface to move between screens.
 */
public class MainMenuScreen implements Screen {

    private final LaserKittens laserKittens;
    private OrthographicCamera camera = new OrthographicCamera();
    private Background background;
    private Stage stage;
    private Menu menu;

    public MainMenuScreen(final LaserKittens laserKittens) {
        this.laserKittens = laserKittens;

        background = new Background(this.laserKittens.getAssetManager().manager.get(KittensAssetManager.BLUE_BACKGROUND, Texture.class));
        stage = new Stage(new ScreenViewport());
    }

    @Override
    public void show() {
        stage.clear();
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
        background.draw(laserKittens.getBatch(), camera);
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

    private class Menu {
        private Table table = new Table();
        private Skin skin = laserKittens.getAssetManager().manager.get(KittensAssetManager.SKIN, Skin.class);
        private TextButton levels = new TextButton("Levels", skin);
        private TextButton multiplayer = new TextButton("Multiplayer", skin);
        private TextButton settings = new TextButton("Settings", skin);
        private TextButton exit = new TextButton("Exit", skin);

        private ImageButton loginButton = new ImageButton(
                new TextureRegionDrawable(
                        laserKittens.getAssetManager().manager.get(KittensAssetManager.GOOGLE_SIGN_IN, Texture.class)));
        private ImageButton achievementsButton = new ImageButton(
                new TextureRegionDrawable(
                        laserKittens.getAssetManager().manager.get(KittensAssetManager.CUP, Texture.class)));
        private ImageButton rateButton = new ImageButton(
                new TextureRegionDrawable(
                        laserKittens.getAssetManager().manager.get(KittensAssetManager.PLAY_MARKET, Texture.class)));

        public Menu(Stage stage) {
            table.setFillParent(true);
            //table.setDebug(true);
            stage.addActor(table);

            levels.getLabel().setFontScale(2f);
            multiplayer.getLabel().setFontScale(2f);
            settings.getLabel().setFontScale(2f);
            exit.getLabel().setFontScale(2f);

            setListeners();

            final float buttonHeight = Gdx.graphics.getHeight() * 0.15f;
            final float buttonWidth = Gdx.graphics.getWidth() * 0.65f;
            table.row().pad(5, 10, 5, 10);
            table.add(levels).width(buttonWidth).height(buttonHeight).colspan(3);
            table.row().pad(5, 10, 5, 10);
            table.add(multiplayer).width(buttonWidth).height(buttonHeight).colspan(3);
            table.row().pad(5, 10, 5, 10);
            table.add(settings).width(buttonWidth).height(buttonHeight).colspan(3);
            table.row().pad(5, 10, 5, 10);
            table.add(exit).width(buttonWidth).height(buttonHeight).colspan(3);
            table.row().pad(5, 10, 5, 10);

            final float googleButtonWidth = Gdx.graphics.getWidth() * 0.2f;
            final float googleButtonHeight = Gdx.graphics.getHeight() * 0.1f;
            table.add(loginButton).width(googleButtonWidth).height(googleButtonHeight);
            table.add(achievementsButton).width(googleButtonWidth).height(googleButtonHeight);
            table.add(rateButton).width(googleButtonWidth).height(googleButtonHeight);

        }

        private void setListeners() {
            levels.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.CHOOSE_LEVEL_SCREEN);
                }
            });

            multiplayer.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.MULTIPLAYER_SCREEN);
                }
            });

            settings.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.SETTINGS_SCREEN);
                }
            });


            exit.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.exit();
                }
            });

            loginButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    laserKittens.getGoogleServices().signIn();
                }
            });

            achievementsButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    laserKittens.getGoogleServices().showAchievements();
                }
            });

            rateButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    laserKittens.getGoogleServices().rateGame();
                }
            });

        }
    }
}
