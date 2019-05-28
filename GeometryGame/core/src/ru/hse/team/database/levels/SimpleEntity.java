package ru.hse.team.database.levels;

public class SimpleEntity {

    public enum EntityType {
        STAR,
        WALL,
        MIRROR,
        PLAYER
    }

    private float positionX;
    private float positionY;

    private float sizeX;
    private float sizeY;

    private float rotation;

    private final EntityType type;

    public SimpleEntity(float positionX, float positionY, float sizeX, float sizeY, float rotation, EntityType type) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.rotation = rotation;
        this.type = type;
    }

    public float getPositionX() {
        return positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public float getSizeX() {
        return sizeX;
    }

    public float getSizeY() {
        return sizeY;
    }

    public float getRotation() {
        return rotation;
    }

    public EntityType getType() {
        return type;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public void setSizeX(float sizeX) {
        this.sizeX = sizeX;
    }

    public void setSizeY(float sizeY) {
        this.sizeY = sizeY;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
}
