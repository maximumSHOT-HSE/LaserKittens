package ru.hse.team.game.gamelogic.algorithms;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

import ru.hse.team.game.gamelogic.systems.RenderingSystem;

public class GridGraph extends AbstractGraph {

    private static int VERTEX_COUNT_WIDTH = 2;
    private static int VERTEX_COUNT_HEIGHT = 4;

    private int countWidth;
    private int countHeight;
    private float cellWidth;
    private float cellHeight;

    private Vertex[][] graph;

    public GridGraph(int countWidth, int countHeight, float cellWidth, float cellHeight) {
        this.countWidth = countWidth;
        this.countHeight = countHeight;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        buildGraph();
    }

    private void buildGraph() {
        int n = VERTEX_COUNT_WIDTH * countWidth;
        int m = VERTEX_COUNT_HEIGHT * countHeight;
        float dx = cellWidth / (countWidth + 1);
        float dy = cellHeight / (countHeight + 1);
        graph = new Vertex[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                graph[i][j] = new Vertex(new Vector2((i + 1) * dx, (j + 1) * dy));
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
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                Vertex v = graph[i][j];
                shapeRenderer.setColor(Color.YELLOW);
                shapeRenderer.circle(v.position.x, v.position.y, 0.1f);
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
