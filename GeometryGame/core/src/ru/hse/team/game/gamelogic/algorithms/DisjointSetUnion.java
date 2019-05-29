package ru.hse.team.game.gamelogic.algorithms;

public class DisjointSetUnion {

    // max number of vertex, vertices are numbered from 0 to n - 1
    // at the very beginning there are noe any edge
    private int n;
    private int[] parent;

    public int find(int x) {
        return x == parent[x] ? x : (parent[x] = find(parent[x]));
    }

    public DisjointSetUnion(int n) {
        this.n = n;
        parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }

    public boolean areConnected(int u, int v) {
        return find(u) == find(v);
    }

    public void addEdge(int u, int v) {
        parent[find(u)] = parent[find(v)];
    }
}
