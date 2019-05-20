package ru.hse.team.game.levels.RandomLabyrinth;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import ru.hse.team.KittensAssetManager;
import ru.hse.team.game.BodyFactory;
import ru.hse.team.game.gamelogic.systems.RenderingSystem;
import ru.hse.team.game.levels.AbstractLevelFactory;

public class RandomLabyrinthLevelFactory extends AbstractLevelFactory {

    //there may be less stars!
    private int stars = 1;
    //currently it is able to add one door only
    private int keys = 0;

    public RandomLabyrinthLevelFactory() {
        world = new World(new Vector2(0,0), true);
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setKeys(int keys) {
        this.keys = keys;
    }

    private boolean[][][] edges;
    private boolean[][] usedCell;
    private boolean[][] hasObject;
    private boolean[][][] door;
    private int[][][] keyByDoor;
    int keyAmount = 0;
    List<Vector2> starsPositions;
    List<Vector2> keyPositions;
    private Vector2 playerPosition;

    int[][] directions = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};
    private int labyrinthN;
    private int labyrinthM;
    List<Integer> permutation = Arrays.asList(0, 1, 2, 3);

    private Random random = new Random(System.currentTimeMillis());

    private int getRandom(int bound) {
        int random = this.random.nextInt() % bound;
        if (random < 0) {
            random += bound;
        }
        return random;
    }

    private boolean cellExists(int x, int y) {
        return 0 <= x && x < labyrinthN && 0 <= y && y < labyrinthM;
    }

    private void dfsCells(int x, int y) {
        usedCell[x][y] = true;

        Collections.shuffle(permutation, ThreadLocalRandom.current());
        List<Integer> dfsPermutation = new ArrayList<>(permutation);
        for (int i = 0; i < 4; i++) {
            int toX = x + directions[dfsPermutation.get(i)][0];
            int toY = y + directions[dfsPermutation.get(i)][1];

            if (cellExists(toX, toY) && !usedCell[toX][toY]) {
                edges[x][y][dfsPermutation.get(i)] = true;
                edges[toX][toY][(dfsPermutation.get(i) + 2) % 4] = true;
                dfsCells(toX, toY);
            }
        }
    }

    private int getDegree(int x, int y) {
        int degree = 0;
        for (int i = 0; i < 4; i++) {
            if (edges[x][y][i]) {
                degree++;
            }
        }
        return degree;
    }

    private static class IntHolder {
        private int value;
        private int playerX;
        private int playerY;

        public IntHolder(int value, int playerX, int playerY) {
            this.value = value;
            this.playerX = playerX;
            this.playerY = playerY;
        }
    }

    private boolean addKey(int x, int y, boolean used[][]) {
        used[x][y] = true;
        if (getDegree(x, y) == 1 && !hasObject[x][y]) {
            keyPositions.add(new Vector2(x, y));
            hasObject[x][y] = true;
            return true;
        }

        for (int i = 0; i < 4; i++) {
            int toX = x + directions[i][0];
            int toY = y + directions[i][1];

            if (cellExists(toX, toY) && !used[toX][toY] && edges[x][y][i] && !door[x][y][i]) {
                if (addKey(toX, toY, used)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfsToStar(int x, int y, int aimX, int aimY, int depth, IntHolder toStar) {
        usedCell[x][y] = true;
        if (x == aimX && y == aimY) {
            toStar.value = depth;
            return true;
        }
        for (int i = 0; i < 4; i++) {
            int toX = x + directions[i][0];
            int toY = y + directions[i][1];

            if (cellExists(toX, toY) && !usedCell[toX][toY] && edges[x][y][i]) {
                if (dfsToStar(toX, toY, aimX, aimY, depth + 1, toStar)) {
                    if (depth == toStar.value * 2 / 3) {
                        door[x][y][i] = true;
                        door[toX][toY][(i + 2) % 4] = true;
                        if (!addKey(toStar.playerX, toStar.playerY, new boolean[labyrinthN][labyrinthM])) {
                            keyPositions.add(new Vector2(toStar.playerX, toStar.playerY));
                        };
                        keyByDoor[x][y][i] = keyAmount;
                        keyByDoor[x][y][(i + 2) % 4] = keyAmount;
                        keyAmount++;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void addDoorKey(int playerX, int playerY, int starX, int starY) {
        usedCell = new boolean[labyrinthN][labyrinthM];
        dfsToStar(playerX, playerY, starX, starY, 0, new IntHolder(0, playerX, playerY));
    }

    private void generateDFSLabyrinth(int n, int m) {
        keyAmount = 0;
        edges = new boolean[n][m][4];
        usedCell = new boolean[n][m];
        hasObject = new boolean[n][m];
        door = new boolean[n][m][4];
        keyByDoor = new int[n][m][4];

        int startX = getRandom(n);
        int startY = getRandom(m);

        labyrinthN = n;
        labyrinthM = m;
        dfsCells(startX, startY);

        playerPosition = new Vector2(startX, startY);
        hasObject[startX][startY] = true;

        starsPositions = new ArrayList<>();
        for (int i = 0; i < stars; i++) {
            for (int x = getRandom(n), y = getRandom(m); x < n;) {
                if (!hasObject[x][y] && getDegree(x, y) == 1) {
                    starsPositions.add(new Vector2(x, y));
                    hasObject[x][y] = true;
                    break;
                }
                if (++y == m) {
                    y = 0;
                    x++;
                }
            }
        }

        addDoorKey(startX, startY, (int)(starsPositions.get(0).x + 0.5f), (int)(starsPositions.get(0).y + 0.5f));

    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void createLevel(PooledEngine engine, KittensAssetManager assetManager) {
        float screenWidth = RenderingSystem.getScreenSizeInMeters().x;
        float screenHeight = RenderingSystem.getScreenSizeInMeters().y;

        float levelWidth = screenWidth * widthInScreens;
        float levelHeight = screenHeight * heightInScreens;

        final float wallThickness = 3;
        final float starRadius = 3;
        final float playerRadius = 3;
        final float keyWidth = 3;
        final float keyHeight = 3;

        this.engine = engine;
        this.manager = assetManager;
        bodyFactory = BodyFactory.getBodyFactory(world);
        createBackground();

        createImpenetrableWall(new Vector2(0, 0.5f * levelHeight), wallThickness, levelHeight); // left big wall
        createImpenetrableWall(new Vector2(levelWidth, 0.5f * levelHeight), wallThickness, levelHeight); // right big wall
        createImpenetrableWall(new Vector2(0.5f * levelWidth, 0), levelWidth, wallThickness); // down big wall
        createImpenetrableWall(new Vector2(0.5f * levelWidth, levelHeight), levelWidth, wallThickness); // up big wall


        final int cellsPerWidth = 2;
        final int cellsPerHeight = 3;
        generateDFSLabyrinth(widthInScreens * cellsPerWidth, heightInScreens * cellsPerHeight);

        for (int i = 0; i < cellsPerWidth * widthInScreens; i++) {
            for (int j = 0; j < cellsPerHeight * heightInScreens; j++) {
                if (!edges[i][j][0]) {
                    createImpenetrableWall(new Vector2(i * screenWidth / cellsPerWidth, (j + 0.5f) * screenHeight / cellsPerHeight),
                            wallThickness,screenHeight / cellsPerHeight);
                }
                if (!edges[i][j][1]) {
                    createImpenetrableWall(new Vector2((i + 0.5f) * screenWidth / cellsPerWidth, j * screenHeight / cellsPerHeight),
                            screenWidth / cellsPerWidth, wallThickness);
                }

                if (door[i][j][0]) {
                    final Entity door = createDoor(new Vector2(i * screenWidth / cellsPerWidth, (j + 0.5f) * screenHeight / cellsPerHeight),
                            wallThickness,screenHeight / cellsPerHeight);
                    createKey(new Vector2(keyPositions.get(keyByDoor[i][j][0])), keyWidth, keyHeight, door);
                }

                if (door[i][j][1]) {
                    final Entity door = createDoor(new Vector2((i + 0.5f) * screenWidth / cellsPerWidth, j * screenHeight / cellsPerHeight),
                            screenWidth / cellsPerWidth, wallThickness);
                    createKey(new Vector2(keyPositions.get(keyByDoor[i][j][1])), keyWidth, keyHeight, door);
                }
            }
        }

        for (Vector2 starPosition : starsPositions) {
            createStar((starPosition.x + 0.5f) * screenWidth / cellsPerWidth,
                    (starPosition.y + 0.5f) * screenHeight / cellsPerHeight, starRadius);
        }

        focusedPlayer = createPlayer((playerPosition.x + 0.5f) * screenWidth / cellsPerWidth,
                (playerPosition.y + 0.5f) * screenHeight / cellsPerHeight,
                playerRadius);


    }
}
