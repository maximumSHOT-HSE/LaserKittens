package ru.hse.team.database.levels;

/**
 * Replacement for common game Entity for purpises of serialisation.
 * Represents common object by its position, size, rotation and type
 * Type should be one of {@code STAR, WALL, MIRROR, PLAYER, GLASS} only.
 */
public class SimpleEntity {
    public enum EntityType {
        STAR,
        WALL,
        MIRROR,
        PLAYER,
        GLASS
    }

    private float positionX;
    private float positionY;

    private float sizeX;
    private float sizeY;

    private float rotation;

    private final EntityType type;

    public SimpleEntity(
        float positionX,
        float positionY,
        float sizeX,
        float sizeY,
        float rotation,
        EntityType type
    ) {
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
