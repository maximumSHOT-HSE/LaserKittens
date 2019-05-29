package ru.hse.team.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ru.hse.team.Background;
import ru.hse.team.GoogleServicesAction;
import ru.hse.team.LaserKittens;
import ru.hse.team.KittensAssetManager;

/**
 * Settings screen provides an ability to manage application settings.
 */
public class SettingsScreen implements Screen {

    private final LaserKittens parent;
    private OrthographicCamera camera = new OrthographicCamera();
    private Background background;
    private Stage stage;
    private Menu menu;

    private InputMultiplexer inputMultiplexer;

    public SettingsScreen(LaserKittens laserKittens) {
        this.parent = laserKittens;
        background = new Background(parent.assetManager.manager.get("blue-background.jpg", Texture.class));

        stage = new Stage(new ScreenViewport());

        InputProcessor inputProcessor = new SettingsScreenInputProcessor(parent);
        inputMultiplexer = new InputMultiplexer(stage, inputProcessor);
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(inputMultiplexer);

        menu = new Menu(stage);

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        parent.batch.setProjectionMatrix(camera.combined);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        parent.batch.begin();
        background.draw(parent.batch, camera);
        parent.batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        background.resizeClampToEdge();
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

    private class Menu {
        private Table table = new Table();
        private Skin skin = parent.assetManager.manager.get(KittensAssetManager.skin, Skin.class);

        private Label titleLabel = new Label("Settings", new Label.LabelStyle(parent.font, Color.WHITE));
        private Label volumeMusicLabel = new Label("music volume", new Label.LabelStyle(parent.font, Color.WHITE));
        private Label volumeSoundLabel = new Label("sound volume", new Label.LabelStyle(parent.font, Color.WHITE));

        final private TextButton backButton = new TextButton("Back", skin);
        private TextButton about = new TextButton("About", skin);
        final private Slider volumeMusicSlider = new Slider( 0f, 1f, 0.1f,false, skin);
        final private Slider volumeSoundSlider = new Slider( 0f, 1f, 0.1f,false, skin );

        private CheckBox enableAccelerometer = new CheckBox(null, skin);
        private Label accelerometerLabel = new Label("accelerometer", new Label.LabelStyle(parent.font, Color.WHITE));

        private CheckBox showTime = new CheckBox(null, skin);
        private Label showTimeLabel = new Label("timer", new Label.LabelStyle(parent.font, Color.WHITE));

        public Menu(Stage stage) {
            table.setFillParent(true);
            stage.addActor(table);

            titleLabel.setFontScale(6f);
            volumeMusicLabel.setFontScale(1.5f);
            volumeSoundLabel.setFontScale(1.5f);
            enableAccelerometer.getImageCell().size(20, 20);
            enableAccelerometer.getImage().scaleBy(1.5f);
            showTime.getImageCell().size(20, 20);
            showTime.getImage().scaleBy(1.5f);
            accelerometerLabel.setFontScale(1.5f);
            showTimeLabel.setFontScale(1.5f);

            about.getLabel().setFontScale(1.5f);
            backButton.getLabel().setFontScale(1.5f);

            table.row().pad(10, 10, 10, 10);
            table.add(titleLabel).colspan(2);
            table.row().pad(10, 10, 10, 10);
            table.add(volumeMusicLabel);
            table.add(volumeMusicSlider).width(Gdx.graphics.getWidth() * 0.35f).height(Gdx.graphics.getHeight() * 0.1f);
            table.row().pad(10, 10, 5, 10);
            table.add(volumeSoundLabel);
            table.add(volumeSoundSlider).width(Gdx.graphics.getWidth() * 0.35f).height(Gdx.graphics.getHeight() * 0.1f);
            table.row().pad(10, 10, 5, 10);
            table.add(accelerometerLabel);
            table.add(enableAccelerometer).width(Gdx.graphics.getWidth() * 0.5f).height(Gdx.graphics.getHeight() * 0.1f);
            table.row().pad(10, 10, 5, 10);
            table.add(showTimeLabel);
            table.add(showTime).width(Gdx.graphics.getWidth() * 0.5f).height(Gdx.graphics.getHeight() * 0.1f);
            table.row().pad(10, 10, 10, 10);
            table.add(about).width(Gdx.graphics.getWidth() * 0.35f).height(Gdx.graphics.getHeight() * 0.15f);
            table.add(backButton).width(Gdx.graphics.getWidth() * 0.35f).height(Gdx.graphics.getHeight() * 0.15f);
            table.row().pad(10, 10, 10, 10);

            setListeners();
        }

        private void setListeners() {

            volumeMusicSlider.setValue(parent.getPreferences().getMusicVolume());
            volumeMusicSlider.addListener(event -> {
                parent.getPreferences().setMusicVolume(volumeMusicSlider.getValue());
                return false;
            });

            about.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    parent.changeScreen(LaserKittens.SCREEN_TYPE.ABOUT_SCREEN);
                }
            });

            volumeSoundSlider.setValue(parent.getPreferences().getSoundVolume());
            volumeSoundSlider.addListener(event -> {
                parent.getPreferences().setSoundVolume(volumeSoundSlider.getValue());
                return false;
            });

            backButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    parent.changeScreen(LaserKittens.SCREEN_TYPE.MAIN_MENU_SCREEN);
                }
            });

            enableAccelerometer.setChecked(parent.getPreferences().isEnabledAccelerometer());
            if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
                enableAccelerometer.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        parent.getPreferences().setEnableAccelerometer(enableAccelerometer.isChecked());
                        if (enableAccelerometer.isChecked()) {
                            parent.getGoogleServices().unlockAchievement(GoogleServicesAction.accelerometerAchievement);
                        }
                    }
                });
            } else {
                enableAccelerometer.setDisabled(true);
            }

            showTime.setChecked(parent.getPreferences().isShowTime());
            showTime.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    parent.getPreferences().setShowTime(showTime.isChecked());
                }
            });
        }
    }
}
