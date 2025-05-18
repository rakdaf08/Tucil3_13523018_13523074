package components;

import java.util.*;

public class Board {
  private char[][] grid;
  private int cols;
  private int rows;
  private HashMap<String, Piece> pieces;

  public static final char EMPTY_GRID = '.';

  public Board(char[][] grid) {
    if (grid == null || grid.length == 0 || grid[0].length == 0) {
      throw new IllegalArgumentException("Invalid grid dimensions");
    }

    this.grid = grid;
    this.rows = grid.length;
    this.cols = grid[0].length;
    this.pieces = new HashMap<>();
    initializePieces();
  }

  public void printBoard() {
    System.out.println("A: " + cols + " B: " + rows + " N: " + pieces.size());
    for (char[] row : grid) {
      System.out.println(Arrays.toString(row));
    }

    System.out.println(pieces.keySet());
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
      for (int j = piece.col; j < Math.min(piece.col + piece.size, cols); j++) {
        if (piece.row >= 0 && piece.row < rows && j >= 0 && j < cols) {
          visited[piece.row][j] = true;
        }
      }
    }

    if (piece.orientation == 'V') {
      for (int i = piece.row; i < Math.min(piece.row + piece.size, rows); i++) {
        if (i >= 0 && i < rows && piece.col >= 0 && piece.col < cols) {
          visited[i][piece.col] = true;
        }
      }
    }
  }

  public Board copy() {
    char[][] newGrid = new char[rows][cols];
    for (int i = 0; i < rows; i++) {
      System.arraycopy(grid[i], 0, newGrid[i], 0, cols);
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
          Move move = new Move(piece, piece.col, piece.row,
              steps < 0 ? "LEFT" : "RIGHT",
              Math.abs(steps));
          if (isValidMove(move))
            moves.add(move);
        }
      } else {
        for (int steps = -rows; steps <= rows; steps++) {
          if (steps == 0)
            continue;
          Move move = new Move(piece, piece.col, piece.row,
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
    if (x < 0 || x >= rows || y < 0 || y >= cols) {
      return '.';
    }
    return this.grid[x][y];
  }

  public HashMap<String, Piece> getPieces() {
    return this.pieces;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Board board = (Board) o;
    return Arrays.deepEquals(this.grid, board.grid);
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(this.grid);
  }
}
