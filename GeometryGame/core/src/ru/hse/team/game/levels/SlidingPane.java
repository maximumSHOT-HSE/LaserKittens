package ru.hse.team.game.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

/**
 * Sliding pane for choose level menu.
 */
public class SlidingPane extends Group {

    // width of one section
    private float sectionWidth = Gdx.app.getGraphics().getWidth();
    // height of one section
    private float sectionHeight = Gdx.app.getGraphics().getHeight();

    // container for sections
    private Group sections = new Group();
    // offset of section by y coordinate
    private float offsetY;

    public float getSectionWidth() {
        return sectionWidth;
    }

    public float getSectionHeight() {
        return sectionHeight;
    }

    public Group getSections() {
        return sections;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public DIRECTION getDirection() {
        return direction;
    }

    public float getStopOffset() {
        return stopOffset;
    }

    public float getSpeed() {
        return speed;
    }

    public int getCurrentSectionId() {
        return currentSectionId;
    }

    public float getFlingSpeed() {
        return flingSpeed;
    }

    public float getOverscrollDistance() {
        return overscrollDistance;
    }

    public Actor getFocusedSection() {
        return focusedSection;
    }

    @Override
    public Rectangle getCullingArea() {
        return cullingArea;
    }

    public ActorGestureListener getActorGestureListener() {
        return actorGestureListener;
    }

    public float getItemWidth() {
        return itemWidth;
    }

    public float getItemHeight() {
        return itemHeight;
    }

    // direction
    public enum DIRECTION {UP, DOWN}
    private DIRECTION direction = DIRECTION.UP;

    /*
    * offset which will indicate that
    * current sections should be switched
    * */
    private float stopOffset;
    private float speed = 4000;
    private int currentSectionId = 1; // 1-indexed

    // speed of gesture which indicates desire to switch section
    private float flingSpeed = 1000;

    // allowed distance of scrolling outside pane
    private float overscrollDistance = 500;

    // section in focus
    private Actor focusedSection;
    private Rectangle cullingArea = new Rectangle();

    private ActorGestureListener actorGestureListener;

    private float itemWidth = getSectionWidth();
    private float itemHeight = getSectionHeight();

    public SlidingPane() {
        this.addActor(getSections());

        actorGestureListener = new ActorGestureListener() {

            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
            }

            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {

            }

            @Override
            public void fling(InputEvent event, float velocityX, float velocityY, int button) {
                if (Math.abs(velocityY) > getFlingSpeed()) {
                    if (velocityY > 0) {
                        setStopSection(getCurrentSectionId() - 1);
                    } else {
                        setStopSection(getCurrentSectionId() + 1);
                    }
                }
                cancelTouchedFocus();
            }

            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                if (getOffsetY() < getMinOffsetByY()) {
                    return;
                }
                if (getOffsetY() > getMaxOffsetByY()) {
                    return;
                }
                offsetY = getOffsetY() - deltaY;
                cancelTouchedFocus();
            }
        };

        this.addListener(getActorGestureListener());
    }

    public float getMinOffsetByY() {
        return -getOverscrollDistance();
    }

    public float getMaxOffsetByY() {
        return (getSections().getChildren().size - 1) * getSectionHeight()
                + getOverscrollDistance();
    }

    public void setStopSection(int stopSectionId) {
        if (stopSectionId < 1) {
            stopSectionId = 1;
        }
        if (stopSectionId > getSections().getChildren().size) {
            stopSectionId = getSections().getChildren().size;
        }
        stopOffset = (stopSectionId - 1) * getSectionHeight();
        if (getOffsetY() < getStopOffset()) {
            direction = DIRECTION.UP;
        } else {
            direction = DIRECTION.DOWN;
        }
    }

    public void setCurrentSection(int section, DIRECTION direction) {
        this.currentSectionId = section;
        this.offsetY = (section - 1) * getSectionHeight();
        this.stopOffset = (section - 1) * getSectionHeight();
        this.direction = direction;
    }

    public void addWidget(Actor widget) {
        widget.setY(getSections().getChildren().size * getSectionHeight() +
                (getSectionHeight() - getItemHeight()) * 0.5f);
        widget.setX((getSectionWidth() - getItemWidth()) * 0.5f);

        widget.setWidth(getItemWidth());
        widget.setHeight(getItemHeight());

        getSections().addActor(widget);
    }

    public int calculateCurrentSection() {
        int section = Math.round(getOffsetY() / getSectionHeight()) + 1;
        if (section > getSections().getChildren().size) {
            return getSections().getChildren().size;
        }
        if (section < 1) {
            return 1;
        }
        return section;
    }

    private void move(float delta) {
        if (getOffsetY() < getStopOffset()) {
            if (getDirection() == DIRECTION.DOWN ) {
                offsetY = getStopOffset();
                currentSectionId = calculateCurrentSection();
                return;
            }
            offsetY = getOffsetY() + getSpeed() * delta;
        } else if(getOffsetY() > getStopOffset()) {
            if (getDirection() == DIRECTION.UP ) {
                offsetY = getStopOffset();
                currentSectionId = calculateCurrentSection();
                return;
            }
            offsetY = getOffsetY() - getSpeed() * delta;
        }
    }

    @Override
    public void act(float delta) {
        getSections().setY(-getOffsetY());

        getCullingArea().set(
            getSections().getX(),
            -getSections().getY(),
                getSectionWidth(),
                getSectionHeight()
        );

        getSections().setCullingArea(getCullingArea());

        if (!getActorGestureListener().getGestureDetector().isPanning()) {
            move(delta);
        }
    }

    private void cancelTouchedFocus() {
        if (getFocusedSection() == null) {
            return;
        }
        this.getStage().cancelTouchFocusExcept(this.getActorGestureListener(), this);
        focusedSection = null;
    }
}
