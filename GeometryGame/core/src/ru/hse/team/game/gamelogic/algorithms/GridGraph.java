package ru.hse.team.game.gamelogic.algorithms;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class GridGraph extends AbstractGraph {

    private static int VERTEX_COUNT_IN_CELL_WIDTH = 2;
    private static int VERTEX_COUNT_IN_CELL_HEIGHT = 4;

    private int countScreensWidth;
    private int countScreensHeight;
    private float cellWidth;
    private float cellHeight;

    private Vertex[][] graph;
    private List<Pair<Vertex, Vertex>> edges = new ArrayList<>();
    private Map<Integer, List<Pair<Vertex, Vertex>>> deletedEdges = new HashMap<>();

    public GridGraph(int countScreensWidth, int countScreensHeight, float cellWidth, float cellHeight) {
        this.countScreensWidth = countScreensWidth;
        this.countScreensHeight = countScreensHeight;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        buildGraph();
    }

    private void addAllEdges() {
        int n = VERTEX_COUNT_IN_CELL_WIDTH * countScreensWidth;
        int m = VERTEX_COUNT_IN_CELL_HEIGHT * countScreensHeight;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (i + 1 < n) {
                    addEdge(i, j, i + 1, j);
                }
                if (j + 1 < m) {
                    addEdge(i, j, i, j + 1);;
                }
                if (i + 1 < n && j + 1 < m) {
                    addEdge(i, j, i + 1, j + 1);
                }
            }
        }
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
                graph[i][j] = new Vertex(new Vector2(currentX, currentY), i * m + j);
            }
        }
        addAllEdges();
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

    public void addEdge(int i1, int j1, int i2, int j2) {
        Vertex vu = graph[i1][j1];
        Vertex vv = graph[i2][j2];
        addEdge(vu, vv);
    }

    public void addEdge(Vertex vu, Vertex vv) {
        vu.addEdge(vv);
        edges.add(new Pair<>(vu, vv));
    }

    @Override
    public void addEdge(Vector2 u, Vector2 v) {
        Vertex vu = getVertexByPosition(u);
        Vertex vv = getVertexByPosition(v);
        addEdge(vu, vv);
    }

    private void findReachable() {
        int n = VERTEX_COUNT_IN_CELL_WIDTH * countScreensWidth;
        int m = VERTEX_COUNT_IN_CELL_HEIGHT * countScreensHeight;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                graph[i][j].isReacheble = false;
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (!graph[i][j].isVisited) {
                    continue;
                }
                graph[i][j].isReacheble = true;
                Queue<Vertex> queue = new LinkedList<>();
                queue.offer(graph[i][j]);
                while (!queue.isEmpty()) {
                    Vertex v = queue.poll();
                    for (Vertex to : v.neighbours) {
                        if (!to.isReacheble) {
                            to.isReacheble = true;
                            queue.offer(to);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        int n = graph.length;
        int m = graph[0].length;
        findReachable();
        shapeRenderer.setColor(Color.GREEN);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                Vertex v = graph[i][j];
                if (!v.isVisited) {
                    continue;
                }
                shapeRenderer.circle(v.position.x, v.position.y, 1);
            }
        }
        DisjointSetUnion disjointSetUnion = new DisjointSetUnion(n * m);
        Collections.sort(edges, (p1, p2) -> {
            float result = p1.x.position.dst(p1.y.position) - p2.x.position.dst(p2.y.position);
            return result < 0 ? -1 : result > 0 ? 1 : 0;
        });
        for (Pair<Vertex, Vertex> edge : edges) {
            Vertex u = edge.x;
            Vertex v = edge.y;
            if (!u.isReacheble || !v.isReacheble) {
                continue;
            }
            if (disjointSetUnion.areConnected(u.id, v.id)) {
                continue;
            }
            if (u.isVisited && v.isVisited) {
                shapeRenderer.setColor(Color.GREEN);
            } else {
                shapeRenderer.setColor(Color.RED);
            }
            disjointSetUnion.addEdge(u.id, v.id);
            shapeRenderer.line(u.position, v.position);
        }
    }

    @Override
    public void visit(Vector2 position) {
        Vertex v = getVertexByPosition(position);
        if (v != null) {
            if (!v.isVisited) {
                System.out.println("VISIT " + position.x + ", " + position.y);
            }
            v.isVisited = true;
        }
    }

    @Override
    public void removeEdgeAgterPlacingRectangleBarrier(Vector2 center, float width, float height, int id) {
        System.out.println("REMOVE (" + center.x + ", " + center.y + ") W = " + width + ", H = " + height + "id = " + id);
        List<Pair<Vertex, Vertex>> deletedEs = new ArrayList<>();
        deletedEdges.put(id, deletedEs);
        Iterator<Pair<Vertex, Vertex>> iterator = edges.iterator();
        while (iterator.hasNext()) {
            Pair<Vertex, Vertex> edge = iterator.next();
            Vertex u = edge.x;
            Vertex v = edge.y;
            if (Geometry.areIntersected(u.position, v.position, center, width, height)) {
                u.removeEdge(v);
                deletedEs.add(edge);
                iterator.remove();
            }
        }
    }

    @Override
    public void updateGraphAfterRemoveRectangleBarrier(int id) {
        System.out.println("REMOVE id = " + id);
        if (!deletedEdges.containsKey(id)) {
            System.out.println("NOT FOUND");
            return;
        }
        System.out.println("FOUND");
        List<Pair<Vertex, Vertex>> deletedEs = deletedEdges.get(id);
        for (Pair<Vertex, Vertex> edge : deletedEs) {
            Vertex u = edge.x;
            Vertex v = edge.y;
            addEdge(u, v);
        }
        deletedEdges.remove(id);
    }

    @Override
    public List<Vector2> getFogPositions() {
        findReachable();
        List<Vector2> fogPositions = new ArrayList<>();
        int n = graph.length;
        int m = graph[0].length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (!graph[i][j].isReacheble) {
                    fogPositions.add(graph[i][j].position);
                }
            }
        }
        return fogPositions;
    }

    @Override
    public float getVertexControlWidth() {
        return 1.5f * cellWidth / (VERTEX_COUNT_IN_CELL_WIDTH + 1);
    }

    @Override
    public float getVertexControlHeight() {
        return 1.5f * cellHeight / (VERTEX_COUNT_IN_CELL_HEIGHT + 1);
    }

    private class Vertex {

        public Vector2 position;
        public Set<Vertex> neighbours = new HashSet<>();
        public boolean isVisited = false;
        public boolean isReacheble = false;
        public int id;

        public Vertex(Vector2 position, int id) {
            this.position = position;
            this.id = id;
        }

        public void addEdge(Vertex u) {
            u.neighbours.add(this);
            this.neighbours.add(u);
        }

        public void removeEdge(Vertex u) {
            u.neighbours.remove(this);
            this.neighbours.remove(u);
        }
    }

    private class Pair<T1, T2> {
        T1 x;
        T2 y;

        public Pair(T1 x, T2 y) {
            this.x = x;
            this.y = y;
        }
    }
}
