package com.example.learning.game.levels;

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
    public float sectionWidth;
    // height of one section
    public float sectionHeight;

    // container for sections
    public Group sections;
    // offset of section by y coordinate
    public float offsetY;

    // direction
    public enum DIRECTION {UP, DOWN}
    public DIRECTION direction = DIRECTION.UP;

    /*
    * offset which will indicate that
    * current sections should be switched
    * */
    public float stopOffset;
    public float speed = 1500;
    public int currentSectionId = 1; // 1-indexed

    // speed of gesture which indicates desire to switch section
    public float flingSpeed = 1000;

    // allowed distance of scrolling outside pane
    public float overscrollDistance = 500;

    // section in focus
    public Actor focusedSection;
    public Rectangle cullingArea = new Rectangle();

    public ActorGestureListener actorGestureListener;

    public float itemWidth;
    public float itemHeight;

    public SlidingPane() {
        sections = new Group();
        this.addActor(sections);

        sectionWidth = Gdx.app.getGraphics().getWidth();
        sectionHeight = Gdx.app.getGraphics().getHeight();

        itemWidth = sectionWidth * 0.6f;
        itemHeight = sectionHeight * 0.3f;

        actorGestureListener = new ActorGestureListener() {

            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
            }

            @Override
            public void tap(InputEvent event, float x, float y, int count, int button) {

            }

            @Override
            public void fling(InputEvent event, float velocityX, float velocityY, int button) {
                System.out.println("FLING");
                if (Math.abs(velocityY) > flingSpeed) {
                    if (velocityY > 0) {
                        setStopSection(currentSectionId - 1);
                    } else {
                        setStopSection(currentSectionId + 1);
                    }
                }
                cancelTouchedFocus();
            }

            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                System.out.println("PAN " + direction);
                if (offsetY < getMinOffsetByY()) {
                    return;
                }
                if (offsetY > getMaxOffsetByY()) {
                    return;
                }
                offsetY -= deltaY;
                System.out.println("STOP AT " + stopOffset + ", cur = " + offsetY);
                cancelTouchedFocus();
            }
        };

        this.addListener(actorGestureListener);
    }

    public float getMinOffsetByY() {
        return -overscrollDistance;
    }

    public float getMaxOffsetByY() {
        return (sections.getChildren().size - 1) * sectionHeight + overscrollDistance;
    }

    public void setStopSection(int stopSectionId) {
        if (stopSectionId < 1) {
            stopSectionId = 1;
        }
        if (stopSectionId > sections.getChildren().size) {
            stopSectionId = sections.getChildren().size;
        }
        stopOffset = (stopSectionId - 1) * sectionHeight;
        if (offsetY < stopOffset) {
            direction = DIRECTION.UP;
        } else {
            direction = DIRECTION.DOWN;
        }
    }

    public void addWidget(Actor widget) {

        widget.setY(sections.getChildren().size * sectionHeight + (sectionHeight - itemHeight) * 0.5f);
        widget.setX((sectionWidth - itemWidth) * 0.5f);

        widget.setWidth(itemWidth);
        widget.setHeight(itemHeight);

        sections.addActor(widget);
    }

    public int calculateCurrentSection() {
        int section = Math.round(offsetY / sectionHeight) + 1;
        if ( section > sections.getChildren().size) {
            return sections.getChildren().size;
        }
        if (section < 1) {
            return 1;
        }
//        System.out.println("SECTION = " + section);
        return section;
    }

    private void move(float delta) {
        if (offsetY < stopOffset) {
            if (direction == DIRECTION.DOWN ) {
                offsetY = stopOffset;
                currentSectionId = calculateCurrentSection();
                return;
            }
            offsetY += speed * delta;
        } else if(offsetY > stopOffset) {
            if (direction == DIRECTION.UP ) {
                offsetY = stopOffset;
                currentSectionId = calculateCurrentSection();
                return;
            }
            offsetY -= speed * delta;
        }
    }

    @Override
    public void act(float delta) {
        sections.setY(-offsetY);

        cullingArea.set(
            sections.getX(),
            -sections.getY(),
            sectionWidth,
            sectionHeight
        );

        sections.setCullingArea(cullingArea);

        // if panning
        if (actorGestureListener.getGestureDetector().isPanning()) {
            setStopSection(calculateCurrentSection() + 1);
        } else {
            move(delta);
        }
    }

    public void cancelTouchedFocus() {
        if (focusedSection == null) {
            return;
        }
        this.getStage().cancelTouchFocusExcept(this.actorGestureListener, this);
//        this.getStage().cancelTouchFocus(this);
        focusedSection = null;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setFlingSpeed(float flingSpeed) {
        this.flingSpeed = flingSpeed;
    }

    public void setOverscrollDistance(float overscrollDistance) {
        this.overscrollDistance = overscrollDistance;
    }
}
