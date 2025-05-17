package components;

import java.util.*;

public class Board {
  private char[][] grid;
  private int cols;
  private int rows;
  private HashMap<String, Piece> pieces;

  public Board(char[][] grid) {
    this.grid = grid;
    this.cols = grid[0].length;
    this.rows = grid.length;
    this.pieces = new HashMap<>();

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
      for (int j = 0; j < cols; j++) {
        if (!visited[i][j] && grid[i][j] != '.' && grid[i][j] != 'K') {
          Piece piece = Piece.pieceFromBoard(grid[i][j], i, j, this);
          if (piece != null) {
            pieces.put(String.valueOf(grid[i][j]), piece);
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

  public Board copy() {
    char[][] newGrid = new char[rows][cols];
    for (int i = 0; i < rows; i++) {
      newGrid[i] = grid[i].clone();
    }
    return new Board(newGrid);
  }

  public boolean isValidMove(Move move) {
    int[] end = move.getEndPosition();
    int endX = end[0], endY = end[1];

    if (endX < 0 || endX >= rows || endY < 0 || endY >= cols) {
      return false;
    }

    int startX = move.getStartX();
    int startY = move.getStartY();
    String direction = move.getDirection();

    if (direction.equals("UP") || direction.equals("DOWN")) {
      for (int x = Math.min(startX, endX); x <= Math.max(startX, endX); x++) {
        if (x != startX && grid[x][startY] != '.')
          return false;
      }
    } else {
      for (int y = Math.min(startY, endY); y <= Math.max(startY, endY); y++) {
        if (y != startY && grid[startX][y] != '.')
          return false;
      }
    }
    return true;
  }

  public void makeMove(Move move) {
    if (!isValidMove(move))
      throw new IllegalArgumentException("Invalid move");

    Piece piece = move.getPiece();
    int startX = move.getStartX();
    int startY = move.getStartY();
    int[] end = move.getEndPosition();

    for (int i = 0; i < piece.size; i++) {
      if (piece.orientation == 'H') {
        grid[startX][startY + i] = '.';
      } else {
        grid[startX + i][startY] = '.';
      }
    }

    for (int i = 0; i < piece.size; i++) {
      if (piece.orientation == 'H') {
        grid[end[0]][end[1] + i] = piece.isPrimary ? 'P' : grid[startX][startY];
      } else {
        grid[end[0] + i][end[1]] = piece.isPrimary ? 'P' : grid[startX][startY];
      }
    }
  }

  public List<Move> getPossibleMoves() {
    List<Move> moves = new ArrayList<>();
    for (Piece piece : pieces.values()) {
      if (piece.orientation == 'H') {
        for (int steps = -cols; steps <= cols; steps++) {
          if (steps == 0)
            continue;
          Move move = new Move(piece, piece.x, piece.y,
              steps < 0 ? "LEFT" : "RIGHT",
              Math.abs(steps));
          if (isValidMove(move))
            moves.add(move);
        }
      } else {
        for (int steps = -rows; steps <= rows; steps++) {
          if (steps == 0)
            continue;
          Move move = new Move(piece, piece.x, piece.y,
              steps < 0 ? "UP" : "DOWN",
              Math.abs(steps));
          if (isValidMove(move))
            moves.add(move);
        }
      }
    }
    return moves;
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

  public HashMap<String, Piece> getPieces() {
    return this.pieces;
  }
}
