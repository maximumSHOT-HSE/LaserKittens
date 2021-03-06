package ru.hse.team.game.gamelogic.algorithms;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public abstract class AbstractGraph {
    private static final long MAX_DRAW_GRAPH_TIME = 10_000;
    private static final long NEGATIVE_TIME_INFINITY = (long) -1e9;

    protected boolean drawGraph = false;
    protected long previousDrawGraphTime = NEGATIVE_TIME_INFINITY;

    abstract public void addEdge(Vector2 u, Vector2 v);

    abstract public void draw(ShapeRenderer shapeRenderer, SpriteBatch batch);

    public boolean isDrawGraph() {
        if (System.currentTimeMillis() - previousDrawGraphTime > MAX_DRAW_GRAPH_TIME) {
            drawGraph = false;
        }
        return drawGraph;
    }

    public void setDrawGraph(boolean drawGraph) {
        previousDrawGraphTime = System.currentTimeMillis();
        this.drawGraph = drawGraph;
    }

    abstract public void visit(Vector2 position);

    abstract public void removeEdgeAfterPlacingRectangleBarrier(Vector2 center, float width, float height, int id);

    abstract public void updateGraphAfterRemoveRectangleBarrier(int id);

    abstract public List<Vector2> getFogPositions();

    abstract public float getVertexControlWidth();

    abstract public float getVertexControlHeight();
}
