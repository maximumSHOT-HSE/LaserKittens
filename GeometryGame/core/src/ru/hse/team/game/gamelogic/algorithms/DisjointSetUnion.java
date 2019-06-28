package ru.hse.team.game.gamelogic.algorithms;

/**
 * Data structure for processing system of
 * disjoint sets
 */
public class DisjointSetUnion {
    private int verticesNumber;
    private final int[] parent;

    /**
     * Constructs default system of sets each of which
     * consists of exactly one element
     *
     * @param verticesNumber is the number of initial sets called vertices
     */
    public DisjointSetUnion(int verticesNumber) {
        this.verticesNumber = verticesNumber;
        parent = new int[verticesNumber];
        for (int i = 0; i < verticesNumber; i++) {
            parent[i] = i;
        }
    }

    public int find(int x) {
        return x == parent[x] ? x : (parent[x] = find(parent[x]));
    }

    public boolean areConnected(int u, int v) {
        return find(u) == find(v);
    }

    public void addEdge(int u, int v) {
        parent[find(u)] = parent[find(v)];
    }
}
