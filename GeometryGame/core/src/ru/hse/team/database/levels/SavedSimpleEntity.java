package ru.hse.team.database.levels;

public class SavedSimpleEntity {

    public enum EntityType {
        STAR,
        WALL,
        MIRROR,
        PLAYER
    }

    private final float positionX;
    private final float positionY;

    private final float sizeX;
    private final float sizeY;

    private final EntityType type;

    public SavedSimpleEntity(float positionX, float positionY, float sizeX, float sizeY, EntityType type) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
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

    public EntityType getType() {
        return type;
    }
}
