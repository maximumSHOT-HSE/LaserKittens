package com.example.learning;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SettingsScreen implements Screen {

    private final LaserKittens parent;
    private OrthographicCamera camera = new OrthographicCamera();
    private Background background;

    private Stage stage;
    private Menu menu;

    public SettingsScreen(LaserKittens laserKittens) {
        this.parent = laserKittens;
        background = new Background(parent.assetManager.manager.get("blue-background.jpg", Texture.class));


        stage = new Stage(new ScreenViewport());

    }

    @Override
    public void show() {
        stage.clear();

        Gdx.input.setInputProcessor(stage);

        menu = new Menu(stage);

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update(); // good practise -- update camera one time per frame

        parent.batch.begin();
        background.draw(parent.batch, camera);
        parent.batch.end();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
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

    }

    private class Menu {
        private Table table = new Table();
        private Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));

        private Label titleLabel = new Label("Settings", skin);
        private Label volumeMusicLabel = new Label("music volume", skin);
        private Label musicOnOffLabel = new Label("music on/off", skin);

        final TextButton backButton = new TextButton("Back", skin, "small");
        final Slider volumeMusicSlider = new Slider( 0f, 1f, 0.1f,false, skin );
        final CheckBox musicCheckbox = new CheckBox(null, skin);

        public Menu(Stage stage) {
            // creating menu table (actor) for buttons
            table.setFillParent(true);
           // table.setDebug(true);
            stage.addActor(table);

            titleLabel.setFontScale(2f);
            volumeMusicLabel.setFontScale(1.5f);
            musicOnOffLabel.setFontScale(1.5f);

            table.add(titleLabel).width(Gdx.graphics.getWidth() * 0.35f).height(Gdx.graphics.getHeight() * 0.15f).colspan(2);
            table.row().pad(10, 10, 10, 10);
            table.add(volumeMusicLabel).width(Gdx.graphics.getWidth() * 0.35f).height(Gdx.graphics.getHeight() * 0.15f);
            table.add(volumeMusicSlider).width(Gdx.graphics.getWidth() * 0.35f).height(Gdx.graphics.getHeight() * 0.15f);
            table.row().pad(10, 10, 10, 10);
            table.add(musicOnOffLabel).width(Gdx.graphics.getWidth() * 0.35f).height(Gdx.graphics.getHeight() * 0.15f).left();
            table.add(musicCheckbox).width(Gdx.graphics.getWidth() * 0.35f).height(Gdx.graphics.getHeight() * 0.15f);
            table.row().pad(10, 10, 10, 10);
            table.add(backButton).width(Gdx.graphics.getWidth() * 0.35f).height(Gdx.graphics.getHeight() * 0.15f);

            setListeners();
        }

        private void setListeners() {
            //volume
            volumeMusicSlider.setValue( parent.getPreferences().getMusicVolume() );
            volumeMusicSlider.addListener( new EventListener() {
                @Override
                public boolean handle(Event event) {
                    parent.getPreferences().setMusicVolume( volumeMusicSlider.getValue() );
                    return false;
                }
            });


            //music
            musicCheckbox.setChecked( parent.getPreferences().isMusicEnabled() );
            musicCheckbox.addListener( new EventListener() {
                @Override
                public boolean handle(Event event) {
                    boolean enabled = musicCheckbox.isChecked();
                    parent.getPreferences().setMusicEnabled( enabled );
                    return false;
                }
            });


            // return to main screen button
            backButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    parent.changeScreen(LaserKittens.SCREEN_TYPE.MAIN_MENU_SCREEN);
                }
            });

        }

    }
}
