package ru.hse.team.leveleditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

import ru.hse.team.Background;
import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;
import ru.hse.team.database.levels.SimpleEntity;
import ru.hse.team.settings.about.PagedScrollPane;

public class LevelCreateScreen implements Screen {

    private final LaserKittens laserKittens;
    private OrthographicCamera camera = new OrthographicCamera();
    private Background background;
    private Stage stage;
    private EditorTools tools;

    private InputMultiplexer inputMultiplexer;
    private LevelCreateInputProcessor inputProcessor;

    private List<SimpleEntity> entities = new ArrayList<>();

    public LevelCreateScreen(final LaserKittens laserKittens) {
        this.laserKittens = laserKittens;

        background = new Background(this.laserKittens.assetManager.manager.get("blue-background.jpg", Texture.class));
        stage = new Stage(new ScreenViewport());

        inputProcessor = new LevelCreateInputProcessor(this.laserKittens, this, camera);
        inputMultiplexer = new InputMultiplexer(stage, inputProcessor);
    }

    public void addSimpleEntity(SimpleEntity entity) {
        entities.add(entity);
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(inputMultiplexer);
        tools = new EditorTools(stage);

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        laserKittens.batch.setProjectionMatrix(camera.combined);
        camera.update();
    }

    private TextureRegion getTextureByType(SimpleEntity.EntityType type) {
        switch (type) {
            case STAR:
                return new TextureRegion(laserKittens.assetManager.manager.get(KittensAssetManager.Star2, Texture.class));
            case WALL:
                return new TextureRegion(laserKittens.assetManager.manager.get(KittensAssetManager.ICE_WALL, Texture.class));
            case MIRROR:
                return new TextureRegion(laserKittens.assetManager.manager.get(KittensAssetManager.MIRROR, Texture.class));
            case PLAYER:
                return new TextureRegion(laserKittens.assetManager.manager.get(KittensAssetManager.Cat3, Texture.class));
            default:
                throw new AssertionError("Panic");
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        laserKittens.batch.begin();
        background.draw(laserKittens.batch, camera);

        for (SimpleEntity entity : entities) {
            TextureRegion texture = getTextureByType(entity.getType());

            final float width = texture.getRegionWidth();
            final float height = texture.getRegionHeight();

            System.out.println("AAA");
            laserKittens.batch.draw(texture, entity.getPositionX() - width / 2, entity.getPositionY() - width / 2,
                    width / 2, height / 2,
                    width, height,
                    entity.getSizeX() / width, entity.getSizeY() / height, entity.getRotation());
        }

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

    private class EditorTools {

        private Skin skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"));
        private Table table = new Table();
        private final float screenWidth = Gdx.graphics.getWidth();
        private final float screenHeight = Gdx.graphics.getHeight();

        final float toolButtonWidth = screenWidth / 5;
        final float toolButtonHeight = screenHeight / 10;

        private ImageButton playerButton = new ImageButton(new TextureRegionDrawable(laserKittens.assetManager.manager.get(KittensAssetManager.Cat3, Texture.class)));
        private ImageButton wallButton = new ImageButton(new TextureRegionDrawable(laserKittens.assetManager.manager.get(KittensAssetManager.ICE_WALL, Texture.class)));
        private ImageButton mirrorButton = new ImageButton(new TextureRegionDrawable(laserKittens.assetManager.manager.get(KittensAssetManager.MIRROR, Texture.class)));
        private ImageButton starButton = new ImageButton(new TextureRegionDrawable(laserKittens.assetManager.manager.get(KittensAssetManager.Star2, Texture.class)));
        private ImageButton rotateLeft = new ImageButton(new TextureRegionDrawable(laserKittens.assetManager.manager.get(KittensAssetManager.Cat1, Texture.class)));
        private ImageButton rotateRight = new ImageButton(new TextureRegionDrawable(laserKittens.assetManager.manager.get(KittensAssetManager.Cat1, Texture.class)));
        private ImageButton finishButton = new ImageButton(new TextureRegionDrawable(laserKittens.assetManager.manager.get(KittensAssetManager.Cat1, Texture.class)));
        private ImageButton eraserButton = new ImageButton(new TextureRegionDrawable(laserKittens.assetManager.manager.get(KittensAssetManager.Cat1, Texture.class)));

        public EditorTools(Stage stage) {
            table.setFillParent(true);
            //table.setDebug(true);
            stage.addActor(table);
            table.align(Align.top);

            PagedScrollPane scroll = new PagedScrollPane(skin);
            scroll.setFlingTime(0.1f);
            scroll.setPageSpacing(25);
            Table buttons = new Table();

            buttons.add(playerButton).width(toolButtonWidth).height(toolButtonHeight);
            buttons.add(wallButton).width(toolButtonWidth).height(toolButtonHeight);
            buttons.add(mirrorButton).width(toolButtonWidth).height(toolButtonHeight);
            buttons.add(starButton).width(toolButtonWidth).height(toolButtonHeight);
            buttons.add(rotateLeft).width(toolButtonWidth).height(toolButtonHeight);
            buttons.add(rotateRight).width(toolButtonWidth).height(toolButtonHeight);
            buttons.add(eraserButton).width(toolButtonWidth).height(toolButtonHeight);
            buttons.add(finishButton).width(toolButtonWidth).height(toolButtonHeight);
            scroll.addPage(buttons);
            scroll.setHeight(toolButtonHeight + screenHeight / 30);

            table.add(scroll);


            setListeners();
        }

        private void setListeners() {

            playerButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    System.out.println("HERE!");
                }
            });

            wallButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                }
            });

            mirrorButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                }
            });

            starButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                }
            });

            rotateLeft.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                }
            });

            rotateRight.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                }
            });

            finishButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                }
            });

            eraserButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                }
            });

        }

    }

}
