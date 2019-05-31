package ru.hse.team.leveleditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
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
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.LaserKittens;
import ru.hse.team.database.levels.SavedLevel;
import ru.hse.team.database.levels.SimpleEntity;
import ru.hse.team.settings.about.PagedScrollPane;

public class LevelCreateScreen implements Screen {

    private final LaserKittens laserKittens;
    private OrthographicCamera camera = new OrthographicCamera();
    private TextureRegion background;
    private Stage stage;
    private EditorTools tools;

    private InputMultiplexer inputMultiplexer;
    private LevelCreateInputProcessor inputProcessor;
    private LevelGestureProcessor gestureProcessor;

    private static final int widthInScreens = 3;
    private static final int heightInScreens = 3;

    private Set<SimpleEntity> entities = new HashSet<>();

    public LevelCreateScreen(final LaserKittens laserKittens) {
        this.laserKittens = laserKittens;

        stage = new Stage(new ScreenViewport());

        inputProcessor = new LevelCreateInputProcessor(this.laserKittens, this, camera);
        gestureProcessor = new LevelGestureProcessor(this.laserKittens, inputProcessor, camera);
        inputMultiplexer = new InputMultiplexer(stage, inputProcessor, new GestureDetector(gestureProcessor));
    }

    public void addSimpleEntity(SimpleEntity entity) {
        entities.add(entity);
    }

    public void removeEntity(SimpleEntity entity) {
        entities.remove(entity);
    }

    public SimpleEntity entityOnPoint(float positionX, float positionY) {
        for (SimpleEntity entity : entities) {
            if (inBounds(entity, positionX, positionY)) {
                return entity;
            }
        }
        return null;
    }

    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(inputMultiplexer);
        tools = new EditorTools(stage);
        background = createBackground();

        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        laserKittens.getBatch().setProjectionMatrix(camera.combined);
        camera.update();
    }

    public TextureRegion getTextureByType(SimpleEntity.EntityType type) {
        switch (type) {
            case STAR:
                return new TextureRegion(laserKittens.getAssetManager().getImage(KittensAssetManager.Images.STAR_2));
            case WALL:
                return new TextureRegion(laserKittens.getAssetManager().getImage(KittensAssetManager.Images.ICE_WALL));
            case MIRROR:
                return new TextureRegion(laserKittens.getAssetManager().getImage(KittensAssetManager.Images.MIRROR));
            case PLAYER:
                return new TextureRegion(laserKittens.getAssetManager().getImage(KittensAssetManager.Images.CAT_3));
            case GLASS:
                return new TextureRegion(laserKittens.getAssetManager().getImage(KittensAssetManager.Images.TRANSPARENT_WALL));
            default:
                throw new AssertionError("Panic");
        }
    }

    private void makeBordersForCamera(Vector3 position) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float levelWidth = screenWidth * widthInScreens;
        float levelHeight = screenHeight * heightInScreens;

        position.x = Math.max(position.x, screenWidth * camera.zoom / 2 - screenWidth / (2 * camera.zoom));
        position.y = Math.max(position.y, screenHeight * camera.zoom / 2 - screenHeight / (2 * camera.zoom));
        position.x = Math.min(position.x, levelWidth - screenWidth * camera.zoom / 2 + screenWidth / (2 * camera.zoom));
        position.y = Math.min(position.y, levelHeight - screenHeight * camera.zoom / 2 + screenHeight / (2 * camera.zoom));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(26f / 256f, 144f / 256f, 255f / 256f, 0.3f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        laserKittens.getBatch().setProjectionMatrix(camera.combined);
        laserKittens.getBatch().begin();
        float width = background.getRegionWidth();
        float height = background.getRegionHeight();
        laserKittens.getBatch().draw(background,
                -(float)Gdx.graphics.getWidth(), -(float)Gdx.graphics.getHeight(),
                0, 0,
                width, height,
                (float)Gdx.graphics.getWidth() * (widthInScreens + 2) / width,
                (float)Gdx.graphics.getHeight() * (heightInScreens + 2) / height , 0);

        for (SimpleEntity entity : entities) {
            TextureRegion texture = getTextureByType(entity.getType());

            width = texture.getRegionWidth();
            height = texture.getRegionHeight();

            laserKittens.getBatch().draw(texture, entity.getPositionX() - width / 2, entity.getPositionY() - width / 2,
                    width / 2, height / 2,
                    width, height,
                    entity.getSizeX() / width, entity.getSizeY() / height, entity.getRotation());
        }

        laserKittens.getBatch().end();
        makeBordersForCamera(camera.position);

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
    public void dispose () {
        stage.dispose();
    }

    private boolean inBounds(SimpleEntity entity, float positionX, float positionY) {
        return Math.abs(positionX - entity.getPositionX()) < entity.getSizeX() / 2
                && Math.abs(positionY - entity.getPositionY()) < entity.getSizeY() / 2;
    }

    private TextureRegion createBackground() {
        Texture background = laserKittens.getAssetManager().getImage(KittensAssetManager.Images.BLUE_BACKGROUND);
        background.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        TextureRegion backgroundRegion = new TextureRegion(background);

        backgroundRegion.setRegion(0, 0,
                background.getWidth() * (widthInScreens + 2),
                background.getHeight() * (heightInScreens + 2));
        return backgroundRegion;
    }

    private class EditorTools {

        private Skin skin = laserKittens.getAssetManager().getSkin(KittensAssetManager.Skins.BLUE_SKIN);
        private Table table = new Table();
        private final float screenWidth = Gdx.graphics.getWidth();
        private final float screenHeight = Gdx.graphics.getHeight();

        final float toolButtonWidth = screenWidth / 5;
        final float toolButtonHeight = screenHeight / 10;

        private ImageButton playerButton = new ImageButton(new TextureRegionDrawable(
                laserKittens.getAssetManager().getImage(KittensAssetManager.Images.CAT_3)));
        private ImageButton wallButton = new ImageButton(new TextureRegionDrawable(
                laserKittens.getAssetManager().getImage(KittensAssetManager.Images.ICE_WALL)));
        private ImageButton mirrorButton = new ImageButton(new TextureRegionDrawable(
                laserKittens.getAssetManager().getImage(KittensAssetManager.Images.MIRROR)));
        private ImageButton glassButton = new ImageButton(new TextureRegionDrawable(
                laserKittens.getAssetManager().getImage(KittensAssetManager.Images.TRANSPARENT_WALL)));
        private ImageButton starButton = new ImageButton(new TextureRegionDrawable(
                laserKittens.getAssetManager().getImage(KittensAssetManager.Images.STAR_2)));
        private ImageButton rotateLeft = new ImageButton(new TextureRegionDrawable(
                laserKittens.getAssetManager().getImage(KittensAssetManager.Images.ROTATE_LEFT)));
        private ImageButton rotateRight = new ImageButton(new TextureRegionDrawable(
                laserKittens.getAssetManager().getImage(KittensAssetManager.Images.ROTATE_RIGHT)));
        private ImageButton finishButton = new ImageButton(new TextureRegionDrawable(
                laserKittens.getAssetManager().getImage(KittensAssetManager.Images.OK)));
        private ImageButton eraserButton = new ImageButton(new TextureRegionDrawable(
                laserKittens.getAssetManager().getImage(KittensAssetManager.Images.ERASER)));
        private ImageButton cursorButton = new ImageButton(new TextureRegionDrawable(
                laserKittens.getAssetManager().getImage(KittensAssetManager.Images.CURSOR)));

        public EditorTools(Stage stage) {
            table.setFillParent(true);
            //table.setDebug(true);
            stage.addActor(table);
            table.align(Align.top);

            PagedScrollPane scroll = new PagedScrollPane(skin);
            scroll.setFlingTime(0.1f);
            scroll.setPageSpacing(25);
            Table buttons = new Table();

            buttons.add(cursorButton).width(toolButtonWidth).height(toolButtonHeight);
            buttons.add(eraserButton).width(toolButtonWidth).height(toolButtonHeight);
            buttons.add(playerButton).width(toolButtonWidth).height(toolButtonHeight);
            buttons.add(wallButton).width(toolButtonWidth).height(toolButtonHeight);
            buttons.add(mirrorButton).width(toolButtonWidth).height(toolButtonHeight);
            buttons.add(glassButton).width(toolButtonWidth).height(toolButtonHeight);
            buttons.add(starButton).width(toolButtonWidth).height(toolButtonHeight);
            buttons.add(rotateLeft).width(toolButtonWidth).height(toolButtonHeight);
            buttons.add(rotateRight).width(toolButtonWidth).height(toolButtonHeight);
            buttons.add(finishButton).width(toolButtonWidth).height(toolButtonHeight);
            scroll.addPage(buttons);
            scroll.setHeight(toolButtonHeight + screenHeight / 30);

            table.add(scroll);


            setListeners();
        }

        private void setListeners() {

            cursorButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    inputProcessor.changeTool(LevelCreateInputProcessor.Tool.CURSOR);
                }
            });

            playerButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    inputProcessor.chooseAnotherEntity(SimpleEntity.EntityType.PLAYER);

                }
            });

            glassButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    inputProcessor.chooseAnotherEntity(SimpleEntity.EntityType.GLASS);

                }
            });

            wallButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    inputProcessor.chooseAnotherEntity(SimpleEntity.EntityType.WALL);
                }
            });

            mirrorButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    inputProcessor.chooseAnotherEntity(SimpleEntity.EntityType.MIRROR);
                }
            });

            starButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    inputProcessor.chooseAnotherEntity(SimpleEntity.EntityType.STAR);
                }
            });

            eraserButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    inputProcessor.changeTool(LevelCreateInputProcessor.Tool.ERASER);
                }
            });

            rotateLeft.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    inputProcessor.rotateCurrentEntity(10);
                }
            });

            rotateRight.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    inputProcessor.rotateCurrentEntity(-10);
                }
            });

            finishButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    laserKittens.setScreen(new LevelSavingScreen(
                            laserKittens, new SavedLevel(-1, new ArrayList<>(entities), widthInScreens, heightInScreens, "Empty")
                    ));
                }
            });

        }

    }
}
