package com.example.learning.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.example.learning.LaserKittens;
import com.example.learning.MyAssetManager;
import com.example.learning.game.levels.AbstractLevel;

public class PopUpScreen implements Screen {

    private LaserKittens parent;
    private Stage stage;
    private AbstractLevel parentLevel;
    private GameScreen gameScreen;

    public PopUpScreen(LaserKittens parent, Stage stage, AbstractLevel level, GameScreen gameScreen) {
        this.parent = parent;
        this.stage = stage;
        this.parentLevel = level;
    }

    private void showEndGameDialog() {
        Dialog dialog = new Dialog(
                "End game",
                (Skin) parent.assetManager.manager.get(MyAssetManager.skin)
        ) {
            @Override
            protected void result(Object object) {
                System.out.println("emem");
            }
        };

        dialog.setModal(true);
        dialog.setMovable(true);
        dialog.setResizable(false);

        Skin skin = parent.assetManager.manager.get(MyAssetManager.skin);
        TextButton restartButton = new TextButton("Restart", skin);
        TextButton quitButton = new TextButton("Quit", skin);
        Label questionLabel = new Label("Eng game", skin);

        Table buttonTable = new Table(skin);

        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.setScreen(new GameScreen(parent, parentLevel));
            }
        });

        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
            parent.changeScreen(LaserKittens.SCREEN_TYPE.CHOOSE_LEVEL_SCREEN);
            }
        });

        dialog.getContentTable().add(questionLabel);
        dialog.setMovable(false);

        buttonTable.add(restartButton);
        buttonTable.add(quitButton);

        dialog.getButtonTable().add(buttonTable).center();
        stage.clear();
        dialog.show(stage);

        stage.addActor(dialog);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        showEndGameDialog();
    }

    @Override
    public void render(float delta) {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

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
}
