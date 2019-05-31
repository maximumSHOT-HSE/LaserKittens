package ru.hse.team.settings;

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
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.tomgrill.gdxdialogs.core.dialogs.GDXTextPrompt;
import de.tomgrill.gdxdialogs.core.listener.TextPromptListener;
import ru.hse.team.Background;
import ru.hse.team.GoogleServicesAction;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;

/**
 * Settings screen provides an ability to manage application settings.
 */
public class SettingsScreen implements Screen {

    private final LaserKittens laserKittens;
    private OrthographicCamera camera = new OrthographicCamera();
    private Background background;
    private Stage stage = new Stage(new ScreenViewport());
    private Menu menu;

    public SettingsScreen(LaserKittens laserKittens) {
        this.laserKittens = laserKittens;
        background = new Background(
                this.laserKittens.getAssetManager()
                        .getImage(KittensAssetManager.Images.BLUE_BACKGROUND));
    }

    @Override
    public void show() {
        stage.clear();
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.MAIN_MENU_SCREEN);
                }
                return true;
            }
        });
        Gdx.input.setInputProcessor(stage);
        menu = new Menu(stage);

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
        private Skin skin = laserKittens.getAssetManager().getSkin(KittensAssetManager.Skins.BLUE_SKIN);

        private Label titleLabel = new Label("Settings",
                new Label.LabelStyle(laserKittens.getFont(), Color.WHITE));

        private Label playerNameLabel = new Label("player name: ",
                new Label.LabelStyle(laserKittens.getFont(), Color.WHITE));

        private Label volumeSoundLabel = new Label("sound volume",
                new Label.LabelStyle(laserKittens.getFont(), Color.WHITE));

        final private TextButton backButton = new TextButton("Back", skin);
        private TextButton about = new TextButton("About", skin);

        final private Label playerNameTextField = new Label(
                laserKittens.getPreferences().getPlayerName(),
                new Label.LabelStyle(laserKittens.getFont(), Color.YELLOW));

        final private Slider volumeSoundSlider =
                new Slider( 0f, 1f, 0.1f,false, skin );

        private CheckBox enableAccelerometer = new CheckBox(null, skin);
        private Label accelerometerLabel =
                new Label("accelerometer",
                        new Label.LabelStyle(laserKittens.getFont(), Color.WHITE));

        private CheckBox showTime = new CheckBox(null, skin);
        private Label showTimeLabel =
                new Label("timer",
                        new Label.LabelStyle(laserKittens.getFont(), Color.WHITE));

        private CheckBox enableFog = new CheckBox(null, skin);
        private Label fogLabel =
                new Label("enable fog",
                        new Label.LabelStyle(laserKittens.getFont(), Color.WHITE));

        public Menu(Stage stage) {
            table.setFillParent(true);
            stage.addActor(table);

            titleLabel.setFontScale(6f);
            playerNameLabel.setFontScale(1.5f);
            volumeSoundLabel.setFontScale(1.5f);
            accelerometerLabel.setFontScale(1.5f);
            showTimeLabel.setFontScale(1.5f);
            fogLabel.setFontScale(1.5f);

            playerNameTextField.setFontScale(1.5f);

            volumeSoundSlider.setValue(laserKittens.getPreferences().getSoundVolume());

            enableAccelerometer.getImageCell().size(20, 20);
            enableAccelerometer.getImage().scaleBy(1.5f);

            showTime.getImageCell().size(20, 20);
            showTime.getImage().scaleBy(1.5f);

            enableFog.getImageCell().size(20, 20);
            enableFog.getImage().scaleBy(1.5f);

            about.getLabel().setFontScale(1.5f);
            backButton.getLabel().setFontScale(1.5f);

            table.row().pad(10, 10, 10, 10);
            table.add(titleLabel).colspan(2);
            table.row().pad(10, 10, 10, 10);
            table.add(playerNameLabel);
            table.add(playerNameTextField).width(Gdx.graphics.getWidth() * 0.35f).height(Gdx.graphics.getHeight() * 0.03f);
            table.row().pad(10, 10, 10, 10);
            table.add(volumeSoundLabel);
            table.add(volumeSoundSlider).width(Gdx.graphics.getWidth() * 0.35f).height(Gdx.graphics.getHeight() * 0.1f);
            table.row().pad(10, 10, 5, 10);
            table.add(accelerometerLabel);
            table.add(enableAccelerometer).width(Gdx.graphics.getWidth() * 0.5f).height(Gdx.graphics.getHeight() * 0.1f);
            table.row().pad(10, 10, 5, 10);
            table.add(showTimeLabel);
            table.add(showTime).width(Gdx.graphics.getWidth() * 0.5f).height(Gdx.graphics.getHeight() * 0.1f);
            table.row().pad(10, 10, 10, 10);
            table.add(fogLabel);
            table.add(enableFog).width(Gdx.graphics.getWidth() * 0.5f).height(Gdx.graphics.getHeight() * 0.1f);
            table.row().pad(10, 10, 10, 10);
            table.add(about).width(Gdx.graphics.getWidth() * 0.35f).height(Gdx.graphics.getHeight() * 0.15f);
            table.add(backButton).width(Gdx.graphics.getWidth() * 0.35f).height(Gdx.graphics.getHeight() * 0.15f);
            table.row().pad(10, 10, 10, 10);

            setListeners();
        }

        private void setListeners() {

            about.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.ABOUT_SCREEN);
                }
            });

            playerNameTextField.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    GDXTextPrompt textPrompt = laserKittens.getDialogs().newDialog(GDXTextPrompt.class);

                    textPrompt.setTitle("Your name");
                    textPrompt.setMessage("Enter your name");

                    textPrompt.setCancelButtonLabel("Cancel");
                    textPrompt.setConfirmButtonLabel("Save name");
                    textPrompt.setValue(playerNameTextField.getText());

                    textPrompt.setTextPromptListener(new TextPromptListener() {

                        @Override
                        public void confirm(String text) {
                            playerNameTextField.setText(text);
                            laserKittens.getPreferences().setPlayerName(text);
                        }

                        @Override
                        public void cancel() {
                            // handle input cancel
                        }
                    });

                    textPrompt.build().show();
                }
            });

            volumeSoundSlider.addListener(event -> {
                laserKittens.getPreferences().setSoundVolume(volumeSoundSlider.getValue());
                return false;
            });

            backButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    laserKittens.changeScreen(LaserKittens.SCREEN_TYPE.MAIN_MENU_SCREEN);
                }
            });

            enableAccelerometer.setChecked(laserKittens.getPreferences().isEnabledAccelerometer());
            if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
                enableAccelerometer.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        laserKittens.getPreferences().setEnableAccelerometer(enableAccelerometer.isChecked());
                        if (enableAccelerometer.isChecked()) {
                            laserKittens.getGoogleServices().unlockAchievement(GoogleServicesAction.accelerometerAchievement);
                        }
                    }
                });
            } else {
                enableAccelerometer.setDisabled(true);
            }

            showTime.setChecked(laserKittens.getPreferences().isShowTime());
            showTime.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    laserKittens.getPreferences().setShowTime(showTime.isChecked());
                }
            });

            enableFog.setChecked(laserKittens.getPreferences().isEnabledFog());
            enableFog.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    laserKittens.getPreferences().setEnabledFog(enableFog.isChecked());
                }
            });
        }
    }
}
