package ru.hse.team.game.levels.RandomLabyrinth;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.BodyFactory;
import ru.hse.team.game.gamelogic.components.TypeComponent;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.levels.AbstractLevel;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class RandomLabyrinthLevelFactory extends AbstractLevelFactory {

    //there may be less stars!
    private int stars = 1;
    //there may be less keys/doors
    private int keys = 0;


    public RandomLabyrinthLevelFactory(PooledEngine engine, KittensAssetManager manager, BodyFactory bodyFactory) {
        super(engine, manager, bodyFactory);
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setKeys(int keys) {
        this.keys = keys;
    }


    private int[][] directions = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
    private List<Integer> permutation = Arrays.asList(0, 1, 2, 3);
    private Random random = new Random(System.currentTimeMillis());

    private int getRandom(int bound) {
        int randomInt = random.nextInt() % bound;
        if (randomInt < 0) {
            randomInt += bound;
        }
        return randomInt;
    }

    private Cell[][] cells;
    Map<EdgePosition, Cell> doorToKey = new HashMap<>();
    private int startX;
    private int startY;

    private boolean cellExists(int x, int y) {
        return 0 <= x && x < cells.length && 0 <= y && y < cells[0].length;
    }

    private void markReachable(Cell start) {
        Queue<Cell> queue = new LinkedList<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            Cell vertex = queue.poll();
            for (int i = 0; i < 4; i++) {
                int toX = vertex.x + directions[i][0];
                int toY = vertex.y + directions[i][1];
                if (cellExists(toX, toY) && cells[toX][toY].used == 0
                        && vertex.edges[i] == EdgeType.EMPTY) {
                    cells[toX][toY].used = 1;
                    queue.add(cells[toX][toY]);
                }
            }
            vertex.used = 2;
        }
    }

    private void bfsGenerate(Cell cell, int depth) {
        Queue<Cell> queue = new LinkedList<>();
        queue.add(cell);

        while (!queue.isEmpty()) {
            Cell vertex = queue.poll();
            if (cell.distance + depth == vertex.distance) {
                continue;
            }

            Collections.shuffle(permutation, ThreadLocalRandom.current());
            for (int i = 0; i < 2; i++) {
                int toX = vertex.x + directions[permutation.get(i)][0];
                int toY = vertex.y + directions[permutation.get(i)][1];

                if (cellExists(toX, toY) && cells[toX][toY].used == 0) {
                    cells[toX][toY].used = 1;
                    placeBetween(vertex, cells[toX][toY], EdgeType.EMPTY, permutation.get(i));
                    cells[toX][toY].distance = vertex.distance + 1;
                    cells[toX][toY].parent = vertex;
                    queue.add(cells[toX][toY]);
                }
            }


            vertex.used = 2;
        }

    }

    private void dfsGenerate(Cell cell, int depth) {
        cell.used = 1;
        if (depth == 0) return;

        Collections.shuffle(permutation, ThreadLocalRandom.current());
        List<Integer> dfsPermutation = new ArrayList<>(permutation);
        for (int i = 0; i < 4; i++) {
            int toX = cell.x + directions[dfsPermutation.get(i)][0];
            int toY = cell.y + directions[dfsPermutation.get(i)][1];

            if (cellExists(toX, toY) && cells[toX][toY].used == 0) {
                placeBetween(cell, cells[toX][toY], EdgeType.EMPTY, dfsPermutation.get(i));
                cells[toX][toY].distance = cell.distance + 1;
                cells[toX][toY].parent = cell;
                dfsGenerate(cells[toX][toY], depth - 1);
            }
        }
        cell.used = 2;
    }


    private void generateLabyrinth(int n, int m) {
        cells = new Cell[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
        startX = getRandom(n);
        startY = getRandom(m);

        cells[startX][startY].contentType = TypeComponent.Type.PLAYER;

        int maxIterations = 10;
        cells[startX][startY].used = 1;
        for (int iterations = 1; iterations <= maxIterations; iterations++) {
            List<Cell> toUpdate = new ArrayList<>();
            for (int x = 0; x < n; x++) {
                for (int y = 0; y < m; y++) {
                    if (cells[x][y].used == 1 ) {
                        toUpdate.add(cells[x][y]);
                    }
                }
            }

            for (Cell cell : toUpdate) {
                if (iterations == maxIterations) {
                    dfsGenerate(cell, -1);
                } else {
                    if (iterations % 2 == 1) {
                        dfsGenerate(cell, iterations * 5);
                    } else {
                        bfsGenerate(cell, 3);
                    }
                }
            }

        }

        for (int starIteration = 0; starIteration < stars; starIteration++) {
            int posX = getRandom(n);
            int posY = getRandom(m);
            for (int x = 0; x < n; x++, posX++) {
                if (posX == n) posX = 0;
                for (int y = 0; y < m; y++, posY++) {
                    if (posY == m) posY = 0;

                    if (!cells[posX][posY].hasObject() && cells[posX][posY].getDegree() == 1) {
                        cells[posX][posY].contentType = TypeComponent.Type.STAR;
                        x = n;
                        break;
                    }

                }
            }
        }

        for (int i = 0; i < keys; i++) {
            for (int x = 0, posX = getRandom(n); x < n; x++, posX++) {
                if (posX == n) {
                    posX = 0;
                }
                for (int y = 0, posY = getRandom(m); y < n; y++, posY++) {
                    if (posY == m) {
                        posY = 0;
                    }
                    if (cells[posX][posY].contentType == TypeComponent.Type.STAR) {
                        int initialDistance = cells[posX][posY].distance;
                        Cell now = cells[posX][posY];
                        while (now.distance != initialDistance / 2) {
                            now = now.parent;
                        }
                        if (now.parent == null) continue;
                        placeBetween(now, now.parent, EdgeType.DOOR);
                        clearUsed();
                        Cell keyPosition = placeKey(cells[startX][startY]);
                        if (keyPosition != null) {
                            doorToKey.put(new EdgePosition(now.x, now.y, getDirection(now, now.parent)), keyPosition);
                            doorToKey.put(new EdgePosition(now.parent.x, now.parent.y, getDirection(now.parent, now)), keyPosition);
                            x = n;
                            break;
                        } else {
                            placeBetween(now, now.parent, EdgeType.EMPTY);
                        }
                    }
                }
            }
        }
    }

    private int getDirection(Cell from, Cell to) {
        for (int i = 0; i < 4; i++) {
            if (from.x + directions[i][0] == to.x &&
                    from.y + directions[i][1] == to.y) {
                return i;
            }
        }
        return -1;
    }

    private void placeBetween(Cell from, Cell to, EdgeType type) {
        for (int i = 0; i < 4; i++) {
            if (from.x + directions[i][0] == to.x &&
            from.y + directions[i][1] == to.y) {
                placeBetween(from, to, type, i);
            }
        }
    }

    private void placeBetween(Cell from, Cell to, EdgeType type, int edgeIndex) {
            from.edges[edgeIndex] = type;
            to.edges[(edgeIndex + 2) % 4] = type;
    }

    private Cell placeKey(Cell from) {
        markReachable(from);
        int n = cells.length;
        int m = cells[0].length;

        for (int x = 0, posX = getRandom(n); x < n; x++, posX++) {
            if (posX == n) {
                posX = 0;
            }
            for (int y = 0, posY = getRandom(m); y < m; y++, posY++) {
                if (posY == m) {
                    posY = 0;
                }
                if (cells[posX][posY].used == 2 && !cells[posX][posY].hasObject()) {
                    cells[posX][posY].contentType = TypeComponent.Type.KEY;
                    return cells[posX][posY];
                }
            }
        }
        return null;
    }

    private void clearUsed() {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j].used = 0;
            }
        }
    }

    @Override
    public void createLevel(int widthInScreens, int heightInScreens, AbstractLevel abstractLevel) {
        final float wallThickness = 3;
        final float starRadius = 3;
        final float playerRadius = 3;
        final float keyWidth = 3;
        final float keyHeight = 3;
        final float screenWidth = RenderingSystem.getScreenSizeInMeters().x;
        final float screenHeight = RenderingSystem.getScreenSizeInMeters().y;
        final float levelWidth = screenWidth * widthInScreens;
        final float levelHeight = screenHeight * heightInScreens;
        final int cellsPerWidth = 2;
        final int cellsPerHeight = 3;


        createBackground(widthInScreens, heightInScreens);
        createImpenetrableWall(new Vector2(0, 0.5f * levelHeight), wallThickness, levelHeight); // left big wall
        createImpenetrableWall(new Vector2(levelWidth, 0.5f * levelHeight), wallThickness, levelHeight); // right big wall
        createImpenetrableWall(new Vector2(0.5f * levelWidth, 0), levelWidth, wallThickness); // down big wall
        createImpenetrableWall(new Vector2(0.5f * levelWidth, levelHeight), levelWidth, wallThickness); // up big wall


        generateLabyrinth(widthInScreens * cellsPerWidth, heightInScreens * cellsPerHeight);

        for (int i = 0; i < cellsPerWidth * widthInScreens; i++) {
            for (int j = 0; j < cellsPerHeight * heightInScreens; j++) {
                switch (cells[i][j].edges[0]) {
                    case WALL:
                        createImpenetrableWall(new Vector2(i * screenWidth / cellsPerWidth, (j + 0.5f) * screenHeight / cellsPerHeight),
                                wallThickness,screenHeight / cellsPerHeight);
                        break;
                    case DOOR:
                        final Entity door = createDoor(new Vector2(i * screenWidth / cellsPerWidth, (j + 0.5f) * screenHeight / cellsPerHeight),
                            wallThickness,screenHeight / cellsPerHeight);
                        createKey(doorToKey.get(new EdgePosition(i, j, 0)).getPosition().add(new Vector2(0.5f, 0.5f))
                                    .scl(screenWidth / cellsPerWidth, screenHeight / cellsPerHeight), keyWidth, keyHeight, door);

                        break;

                }
                switch (cells[i][j].edges[1]) {
                    case WALL:
                        createImpenetrableWall(new Vector2((i + 0.5f) * screenWidth / cellsPerWidth, j * screenHeight / cellsPerHeight),
                                screenWidth / cellsPerWidth, wallThickness);
                        break;
                    case DOOR:
                        final Entity door = createDoor(new Vector2((i + 0.5f) * screenWidth / cellsPerWidth, j * screenHeight / cellsPerHeight),
                            screenWidth / cellsPerWidth, wallThickness);
                    createKey(doorToKey.get(new EdgePosition(i, j, 1)).getPosition().add(new Vector2(0.5f, 0.5f))
                            .scl(screenWidth / cellsPerWidth, screenHeight / cellsPerHeight), keyWidth, keyHeight, door);
                        break;
                }

                if (cells[i][j].hasObject()) {
                    switch (cells[i][j].contentType) {
                        case STAR:
                            createStar((i + 0.5f) * screenWidth / cellsPerWidth, (j + 0.5f) * screenHeight / cellsPerHeight, starRadius);
                            break;
                        case PLAYER:
                            abstractLevel.setPlayer(createPlayer(
                                    (i + 0.5f) * screenWidth / cellsPerWidth,
                                    (j + 0.5f) * screenHeight / cellsPerHeight,
                                    playerRadius));
                    }
                }
            }
        }

    }

    public enum EdgeType {
        EMPTY,
        WALL,
        DOOR;
    }

    private static class Cell {
        private final int x;
        private final int y;
        private TypeComponent.Type contentType;
        private EdgeType[] edges;
        private int used;
        private int distance;
        private Cell parent;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
            edges = new EdgeType[4];
            for (int i = 0; i < 4; i++) {
                edges[i] = EdgeType.WALL;
            }
        }

        private int getDegree() {
            int degree = 0;
            for (EdgeType edge : edges) {
                if (edge == EdgeType.EMPTY) {
                    degree++;
                }
            }
            return degree;
        }

        private boolean hasObject() {
            return contentType != null;
        }

        public Vector2 getPosition() {
            return new Vector2(x, y);
        }
    }

    private class EdgePosition {
        private final int x;
        private final int y;
        private final int direction;

        public EdgePosition(int x, int y, int direction) {
            this.x = x;
            this.y = y;
            this.direction = direction;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EdgePosition that = (EdgePosition) o;
            return x == that.x &&
                    y == that.y &&
                    direction == that.direction;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, direction);
        }

    }
}
