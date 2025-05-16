package components;

import java.util.*;

public class Board {
  private char[][] grid;
  private int cols;
  private int rows;
  private List<Piece> pieces;

  public Board(char[][] grid) {
    this.grid = grid;
    this.cols = grid[0].length;
    this.rows = grid.length;
    this.pieces = new ArrayList<Piece>();

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        this.grid[i][j] = grid[i][j];
      }
    }

    initializePieces();
  }

  private void initializePieces() {
    boolean[][] visited = new boolean[rows][cols];

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < rows; j++) {
        if (!visited[i][j] && grid[i][j] != '.' && grid[i][j] != 'K') {
          Piece piece = Piece.pieceFromBoard(grid[i][j], i, j, this);
          if (piece != null) {
            pieces.add(piece);
            markVisited(piece, visited);
          }
        }
      }
    }
  }

  private void markVisited(Piece piece, boolean[][] visited) {
    if (piece.orientation == 'H') {
      for (int j = piece.x; j < piece.x + piece.size; j++) {
        visited[piece.y][j] = true;
      }
    }

    if (piece.orientation == 'V') {
      for (int i = piece.y; i < piece.y + piece.size; i++) {
        visited[i][piece.x] = true;
      }
    }
  }

  public int getRows() {
    return this.rows;
  }

  public int getCols() {
    return this.cols;
  }

  public char getCell(int x, int y) {
    return this.grid[x][y];
  }
}
