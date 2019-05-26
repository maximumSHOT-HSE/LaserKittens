package ru.hse.team.game.gamelogic.algorithms;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

import ru.hse.team.game.gamelogic.systems.RenderingSystem;

public class GridGraph extends AbstractGraph {

    private static int VERTEX_COUNT_IN_CELL_WIDTH = 2;
    private static int VERTEX_COUNT_IN_CELL_HEIGHT = 4;

    private int countScreensWidth;
    private int countScreensHeight;
    private float cellWidth;
    private float cellHeight;

    private Vertex[][] graph;

    public GridGraph(int countScreensWidth, int countScreensHeight, float cellWidth, float cellHeight) {
        this.countScreensWidth = countScreensWidth;
        this.countScreensHeight = countScreensHeight;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        buildGraph();
    }

    private void buildGraph() {
        int n = VERTEX_COUNT_IN_CELL_WIDTH * countScreensWidth;
        int m = VERTEX_COUNT_IN_CELL_HEIGHT * countScreensHeight;
        float dx = cellWidth / (VERTEX_COUNT_IN_CELL_WIDTH + 1);
        float dy = cellHeight / (VERTEX_COUNT_IN_CELL_HEIGHT + 1);
        graph = new Vertex[n][m];
        float currentX = -dx;
        for (int i = 0; i < n; i++) {
            float currentY = -dy;
            currentX += dx;
            if (i % VERTEX_COUNT_IN_CELL_WIDTH == 0) {
                currentX += dx;
            }
            for (int j = 0; j < m; j++) {
                currentY += dy;
                if (j % VERTEX_COUNT_IN_CELL_HEIGHT == 0) {
                    currentY += dy;
                }
                graph[i][j] = new Vertex(new Vector2(currentX, currentY));
            }
        }
    }

    private Vertex getVertexByPosition(Vector2 position) {
        Vertex result = null;
        int n = graph.length;
        int m = graph[0].length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (result == null) {
                    result = graph[i][j];
                    continue;
                }
                float currentDist = position.cpy().sub(result.position).len();
                float candidateDist = position.cpy().sub(graph[i][j].position).len();
                if (candidateDist < currentDist) {
                    result = graph[i][j];
                }
            }
        }
        return result;
    }

    @Override
    public void addEdge(Vector2 u, Vector2 v) {
        Vertex vu = getVertexByPosition(u);
        Vertex vv = getVertexByPosition(v);
        Vertex.addEdge(vu, vv);
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        int n = graph.length;
        int m = graph[0].length;
        shapeRenderer.setColor(Color.GOLD);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                Vertex v = graph[i][j];
                shapeRenderer.circle(v.position.x, v.position.y, 1f);
            }
        }
    }

    private static class Vertex {

        public Vector2 position;
        public List<Vertex> neighbours = new ArrayList<>();

        public Vertex(Vector2 position) {
            this.position = position;
        }

        public static void addEdge(Vertex u, Vertex v) {
            u.neighbours.add(v);
            v.neighbours.add(u);
        }
    }
}
