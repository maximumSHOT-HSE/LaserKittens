package com.example.learning;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

public class ModelGestureListener implements GestureDetector.GestureListener {

    private OrthographicCamera camera;
    float currentZoom;

    public ModelGestureListener(OrthographicCamera camera) {

        this.camera = camera;
        currentZoom = camera.zoom;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {

        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        System.out.println("WOW");
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {

        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {

        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        camera.translate(-deltaX, deltaY);
        camera.update();
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {

        currentZoom = camera.zoom;
        return false;
    }

    @Override
    public boolean zoom (float originalDistance, float currentDistance){
        camera.zoom = (originalDistance / currentDistance) * currentZoom;
        camera.update();
        return true;
    }

    @Override
    public boolean pinch (Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer){

        return false;
    }
    @Override
    public void pinchStop () {
    }
}
