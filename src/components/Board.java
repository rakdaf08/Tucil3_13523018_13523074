package components;

import java.util.*;

public class Board {
  private char[][] grid;
  private int cols;
  private int rows;
  private HashMap<String, Piece> pieces;

  public void printPieces() {
    System.out.println(pieces);
  }

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

    System.out.println("\n" + pieces.keySet());
  }

  private void initializePieces() {
    boolean[][] visited = new boolean[rows][cols];

    // Print grid for debugging
    System.out.println("Grid before initialization:");
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        System.out.print(grid[i][j]);
      }
      System.out.println();
    }

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        char cell = grid[i][j];
        if (!visited[i][j] && cell != '.' && cell != 'K' && cell != '-' && cell != '|') {
          System.out.println("Found piece " + cell + " at position [" + i + "][" + j + "]");
          Piece piece = Piece.pieceFromBoard(cell, j, i, this);
          if (piece != null) {
            pieces.put(String.valueOf(cell), piece);
            System.out.println("Added piece " + cell + " with orientation " + piece.orientation +
                " and size " + piece.size + " at [" + piece.row + "][" + piece.col + "]");
            markVisited(piece, visited);

            // Print visited array after marking each piece
            System.out.println("Visited array after marking piece " + cell + ":");
            for (int r = 0; r < rows; r++) {
              for (int c = 0; c < cols; c++) {
                System.out.print(visited[r][c] ? "T " : "F ");
              }
              System.out.println();
            }
          }
        }
      }
    }

    // Print final visited array
    System.out.println("Final visited array:");
    for (boolean[] row : visited) {
      System.out.println(Arrays.toString(row));
    }

    // Print the pieces found
    System.out.println("Pieces found: " + pieces.size());
    for (Map.Entry<String, Piece> entry : pieces.entrySet()) {
      Piece p = entry.getValue();
      System.out.println("Piece " + entry.getKey() + ": position [" + p.row + "][" + p.col +
          "], orientation " + p.orientation + ", size " + p.size);
    }
  }

  private void markVisited(Piece piece, boolean[][] visited) {
    System.out.println("Marking as visited: piece at [" + piece.row + "][" + piece.col +
        "] with orientation " + piece.orientation + " and size " + piece.size);

    if (piece.orientation == 'H') {
      for (int j = piece.col; j < piece.col + piece.size; j++) {
        if (isValidPosition(piece.row, j)) {
          System.out.println("  Marking [" + piece.row + "][" + j + "] as visited");
          visited[piece.row][j] = true;
        } else {
          System.out.println("  WARNING: Position [" + piece.row + "][" + j + "] is out of bounds!");
        }
      }
    } else if (piece.orientation == 'V') {
      for (int i = piece.row; i < piece.row + piece.size; i++) {
        if (isValidPosition(i, piece.col)) {
          System.out.println("  Marking [" + i + "][" + piece.col + "] as visited");
          visited[i][piece.col] = true;
        } else {
          System.out.println("  WARNING: Position [" + i + "][" + piece.col + "] is out of bounds!");
        }
      }
    } else if (piece.orientation == 'S') {
      // Single cell piece
      if (isValidPosition(piece.row, piece.col)) {
        System.out.println("  Marking single cell [" + piece.row + "][" + piece.col + "] as visited");
        visited[piece.row][piece.col] = true;
      }
    }
  }

  // Helper method to check if a position is valid
  private boolean isValidPosition(int row, int col) {
    return row >= 0 && row < rows && col >= 0 && col < cols;
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
    if (x < 0 || y >= rows || y < 0 || x >= cols) {
      return '.';
    }
    return this.grid[y][x];
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

  public char[][] getGrid() {
    return grid;
  }

  public void setGrid(char[][] grid) {
    this.grid = grid;
  }

  public void setCols(int cols) {
    this.cols = cols;
  }

  public void setRows(int rows) {
    this.rows = rows;
  }

  public void setPieces(HashMap<String, Piece> pieces) {
    this.pieces = pieces;
  }

  public static char getEmptyGrid() {
    return EMPTY_GRID;
  }
}
