package ru.hse.team.settings.about;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Arrays;
import java.util.List;

import ru.hse.team.Background;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;

/**
 * Screen with general information about the game.
 * Information list contains
 *     External libraries, sounds, skins
 */
public class AboutScreen implements Screen {
    private final LaserKittens laserKittens;
    private final OrthographicCamera camera = new OrthographicCamera();
    private final Background background;
    private final Stage stage = new Stage(new ScreenViewport());
    private Menu menu;

    public AboutScreen(final LaserKittens laserKittens) {
        this.laserKittens = laserKittens;
        background = new Background(this.laserKittens.getAssetManager()
                .getImage(KittensAssetManager.Images.BLUE_BACKGROUND));
    }

    @Override
    public void show() {
        stage.clear();
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    laserKittens.changeScreen(LaserKittens.ScreenType.SETTINGS_SCREEN);
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
     * Menu with clickable labels and scrollable plane.
     */
    private class Menu {
        private final Table table = new Table();
        private final Skin skin = laserKittens.getAssetManager()
                .getSkin(KittensAssetManager.Skins.BLUE_SKIN);
        private final Label titleLabel = new Label("About", skin);
        private final TextButton backButton = new TextButton("Back", skin);

        private final Label libgdxLicense =
                new Label("This game uses libGDX framework", skin);
        private final Label libgdxLink =
                new Label("https://github.com/libgdx/libgdx", skin, "black");
        private final Label libgdxApacheLink =
                new Label(" Apache 2 License", skin, "black");

        private final Label ashleyLicense =
                new Label("Ashley entity system", skin);
        private final Label ashleyLink =
                new Label("https://github.com/libgdx/ashley", skin, "black");
        private final Label ashleyApacheLink =
                new Label(" Apache 2 License", skin, "black");

        private final Label skinLicense =
                new Label("SkinName by", skin);
        private final Label skinLink =
                new Label("Raymond \"Raeleus\" Buckley", skin, "black");
        private final Label skinCC4Link =
                new Label("CC BY 4.0", skin, "black");

        private final Label laserSoundLicense =
                new Label("Laser sound from freesound by", skin);
        private final Label laserSoundLink =
                new Label("bubaproducer", skin, "black");
        private final Label laserSoundLicenseLink =
                new Label("CC BY 3.0", skin, "black");

        private final List<List<Label>> listOfLicenses = Arrays.asList(
                Arrays.asList(libgdxLicense, libgdxLink, libgdxApacheLink),
                Arrays.asList(ashleyLicense, ashleyLink, ashleyApacheLink),
                Arrays.asList(skinLicense, skinLink, skinCC4Link),
                Arrays.asList(laserSoundLicense, laserSoundLink, laserSoundLicenseLink)
        );

        public Menu(Stage stage) {
            table.setFillParent(true);
            stage.addActor(table);

            titleLabel.setFontScale(3f * LaserKittens.scaleToPreferredWidth());

            table.row().pad(10, 10, 30, 10);
            table.add(titleLabel).colspan(2).expand();

            table.row().pad(20, 0, 20, 10);
            PagedScrollPane scroll = new PagedScrollPane(skin);
            scroll.setFlingTime(0.1f);
            scroll.setPageSpacing(25);

            Table information = new Table().pad(50);
            information.defaults().pad(10, 10, 10, 10);
            for (List<Label> licenseGroup : listOfLicenses) {
                information.row();
                for (Label license : licenseGroup) {
                    license.setFontScale(2);
                    information.add(license);
                }
            }
            scroll.addPage(information);

            table.add(scroll).expand().fill();

            table.row().pad(30, 10, 10, 10);
            table.add(backButton).width(Gdx.graphics.getWidth() * 0.35f)
                    .height(Gdx.graphics.getHeight() * 0.15f).colspan(2).expand();

            setListeners();
        }

        private void setListeners() {
            backButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    laserKittens.changeScreen(LaserKittens.ScreenType.SETTINGS_SCREEN);
                }
            });

            libgdxApacheLink.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.net.openURI("http://www.apache.org/licenses/LICENSE-2.0");
                }
            });

            libgdxLink.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.net.openURI("https://github.com/libgdx/libgdx");
                }
            });

            ashleyApacheLink.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.net.openURI("http://www.apache.org/licenses/LICENSE-2.0");
                }
            });

            ashleyLink.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.net.openURI("https://github.com/libgdx/ashley");
                }
            });

            skinLink.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.net.openURI(
                            "https://ray3k.wordpress.com/software/SKIN-composer-for-libgdx/");
                }
            });

            skinCC4Link.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.net.openURI("https://creativecommons.org/licenses/by/4.0/");
                }
            });

            laserSoundLink.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.net.openURI("https://freesound.org/people/bubaproducer/");
                }
            });

            laserSoundLicenseLink.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.net.openURI("https://creativecommons.org/licenses/by/3.0/");
                }
            });
        }
    }
}
