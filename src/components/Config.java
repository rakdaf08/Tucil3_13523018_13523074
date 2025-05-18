package components;

import java.util.*;

public class Config {
    int a, b, n, kx, ky;
    char[][] grid;

    public Config(int a, int b, int n, int kx, int ky, char[][] grid) {
        this.a = a;
        this.b = b;
        this.n = n;
        this.kx = kx;
        this.ky = ky;
        this.grid = grid;
    }

    public void printConfig() {
        System.out.println("A: " + a + " B: " + b + " N: " + n);
        for (char[] row : grid) {
            System.out.println(Arrays.toString(row));
        }
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public char[][] getGrid() {
        return grid;
    }

    public void setGrid(char[][] grid) {
        this.grid = grid;
    }
}
