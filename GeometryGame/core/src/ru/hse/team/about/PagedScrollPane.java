package ru.hse.team.about;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

/**
 * Scrollable pane for about screen.
 */
public class PagedScrollPane extends ScrollPane {

    private boolean wasPanDragFling = false;
    private Table content;

    public PagedScrollPane () {
        super(null);
        setup();
    }

    public PagedScrollPane (Skin skin) {
        super(null, skin);
        setup();
    }

    public PagedScrollPane (Skin skin, String styleName) {
        super(null, skin, styleName);
        setup();
    }

    public PagedScrollPane (Actor widget, ScrollPaneStyle style) {
        super(null, style);
        setup();
    }

    private void setup() {
        content = new Table();
        content.defaults().space(50);
        super.setActor(content);
    }

    public void addPages (Actor... pages) {
        for (Actor page : pages) {
            content.add(page).expandX().fillX();
        }
    }

    public void addPage (Actor page) {
        content.add(page).expandX().fillX();
    }

    @Override
    public void act (float delta) {
        super.act(delta);
        if (wasPanDragFling && !isPanning() && !isDragging() && !isFlinging()) {
            wasPanDragFling = false;
        } else {
            if (isPanning() || isDragging() || isFlinging()) {
                wasPanDragFling = true;
            }
        }
    }

    @Override
    public void setHeight (float height) {
        super.setHeight(height);
        if (content != null) {
            for (Cell cell : content.getCells()) {
                cell.height(height);
            }
            content.invalidate();
        }
    }

    public void setPageSpacing (float pageSpacing) {
        if (content != null) {
            content.defaults().space(pageSpacing);
            for (Cell cell : content.getCells()) {
                cell.space(pageSpacing);
            }
            content.invalidate();
        }
    }
}
