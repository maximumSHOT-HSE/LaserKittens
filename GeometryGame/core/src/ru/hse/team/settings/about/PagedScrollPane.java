package ru.hse.team.settings.about;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Scrollable pane.
 */
public class PagedScrollPane extends ScrollPane {

    private boolean wasPanDragFling = false;
    private Table content;

    public PagedScrollPane(Skin skin) {
        super(null, skin);
        setup();
    }

    private void setup() {
        content = new Table();
        content.defaults().space(50);
        super.setActor(content);
    }


    public void addPage(Actor page) {
        content.add(page).expandX().fillX();
    }

    @Override
    public void act(float delta) {
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
    public void setHeight(float height) {
        super.setHeight(height);
        if (content != null) {
            for (Cell cell : content.getCells()) {
                cell.height(height);
            }
            content.invalidate();
        }
    }

    public void setPageSpacing(float pageSpacing) {
        if (content != null) {
            content.defaults().space(pageSpacing);
            for (Cell cell : content.getCells()) {
                cell.space(pageSpacing);
            }
            content.invalidate();
        }
    }
}
