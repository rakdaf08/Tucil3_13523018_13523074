package components;

import java.util.*;

public class Board {
  public static final char EMPTY_GRID = '.';

  private char[][] grid;
  private int cols;
  private int rows;
  private HashMap<String, Piece> pieces;

  public void printPieces() {
    System.out.println(pieces);
  }

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

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        char cell = grid[i][j];
        if (!visited[i][j] && cell != '.' && cell != 'K' && cell != '-' && cell != '|') {
          Piece piece = Piece.pieceFromBoard(cell, j, i, this);
          if (piece != null) {
            pieces.put(String.valueOf(cell), piece);
            markVisited(piece, visited);
          }
        }
      }
    }
  }

  private void markVisited(Piece piece, boolean[][] visited) {
    if (piece.orientation == 'H') {
      for (int j = piece.col; j < piece.col + piece.size; j++) {
        if (isValidPosition(piece.row, j)) {
          visited[piece.row][j] = true;
        }
      }
    } else if (piece.orientation == 'V') {
      for (int i = piece.row; i < piece.row + piece.size; i++) {
        if (isValidPosition(i, piece.col)) {
          visited[i][piece.col] = true;
        }
      }
    } else if (piece.orientation == 'S') {
      if (isValidPosition(piece.row, piece.col)) {
        visited[piece.row][piece.col] = true;
      }
    }
  }

  private boolean isValidPosition(int row, int col) {
    return row >= 0 && row < rows && col >= 0 && col < cols;
  }

  public Board copy() {
    char[][] newGrid = new char[rows][cols];
    for (int i = 0; i < rows; i++) {
      System.arraycopy(grid[i], 0, newGrid[i], 0, cols);
    }

    Board newBoard = new Board(newGrid);

    newBoard.pieces.clear();
    for (Map.Entry<String, Piece> entry : this.pieces.entrySet()) {
      newBoard.pieces.put(entry.getKey(), entry.getValue().copy());
    }

    return newBoard;
  }

  public boolean isValidMove(Move move) {
    int[] end = move.getEndPosition();
    int endCol = end[0], endRow = end[1];

    // Check if the end position is inside the grid
    if (endRow < 0 || endRow >= rows || endCol < 0 || endCol >= cols) {
      return false;
    }

    Piece piece = move.getPiece();
    int startCol = move.getStartX();
    int startRow = move.getStartY();
    String direction = move.getDirection();

    // Check if the path is clear for the move
    if ((piece.getOrientation() == 'H' && (direction.equals("UP") || direction.equals("DOWN"))) ||
        (piece.getOrientation() == 'V' && (direction.equals("LEFT") || direction.equals("RIGHT")))) {
      return false;
    }

    if (direction.equals("UP")) {
      for (int row = startRow - 1; row >= endRow; row--) {
        if (grid[row][startCol] != '.') {
          return false;
        }
      }
    } else if (direction.equals("DOWN")) {
      for (int row = startRow + piece.getSize(); row <= endRow + piece.getSize() - 1; row++) {
        if (row >= rows || grid[row][startCol] != '.') {
          return false;
        }
      }
    } else if (direction.equals("LEFT")) {
      for (int col = startCol - 1; col >= endCol; col--) {
        if (grid[startRow][col] != '.') {
          return false;
        }
      }
    } else if (direction.equals("RIGHT")) {
      for (int col = startCol + piece.getSize(); col <= endCol + piece.getSize() - 1; col++) {
        if (col >= cols || grid[startRow][col] != '.') {
          return false;
        }
      }
    }

    return true;
  }

  public void makeMove(Move move) {
    if (!isValidMove(move)) {
      throw new IllegalArgumentException("Invalid move: " + move);
    }

    Piece piece = move.getPiece();
    int startCol = move.getStartX();
    int startRow = move.getStartY();
    int[] end = move.getEndPosition();
    int endCol = end[0];
    int endRow = end[1];

    char pieceChar = piece.getLetter();

    if (piece.getOrientation() == 'H') {
      for (int i = 0; i < piece.getSize(); i++) {
        if (startRow >= 0 && startRow < rows && startCol + i >= 0 && startCol + i < cols) {
          grid[startRow][startCol + i] = '.';
        }
      }
    } else if (piece.getOrientation() == 'V') {
      for (int i = 0; i < piece.getSize(); i++) {
        if (startRow + i >= 0 && startRow + i < rows && startCol >= 0 && startCol < cols) {
          grid[startRow + i][startCol] = '.';
        }
      }
    } else {
      if (startRow >= 0 && startRow < rows && startCol >= 0 && startCol < cols) {
        grid[startRow][startCol] = '.';
      }
    }

    if (piece.getOrientation() == 'H') {
      for (int i = 0; i < piece.getSize(); i++) {
        if (endRow >= 0 && endRow < rows && endCol + i >= 0 && endCol + i < cols) {
          grid[endRow][endCol + i] = pieceChar;
        }
      }
    } else if (piece.getOrientation() == 'V') {
      for (int i = 0; i < piece.getSize(); i++) {
        if (endRow + i >= 0 && endRow + i < rows && endCol >= 0 && endCol < cols) {
          grid[endRow + i][endCol] = pieceChar;
        }
      }
    } else {
      if (endRow >= 0 && endRow < rows && endCol >= 0 && endCol < cols) {
        grid[endRow][endCol] = pieceChar;
      }
    }

    piece.setCol(endCol);
    piece.setRow(endRow);
  }

  public List<Move> getPossibleMoves() {
    List<Move> moves = new ArrayList<>();

    for (Map.Entry<String, Piece> entry : pieces.entrySet()) {
      Piece piece = entry.getValue();

      if (piece.getOrientation() == 'H') {
        for (int steps = 1; steps <= cols; steps++) {
          if (piece.getCol() - steps < 0)
            break;

          Move leftMove = new Move(piece, piece.getCol(), piece.getRow(), "LEFT", steps);
          if (isValidMove(leftMove)) {
            moves.add(leftMove);
          } else {
            break;
          }
        }

        for (int steps = 1; steps <= cols; steps++) {
          if (piece.getCol() + piece.getSize() + steps - 1 >= cols)
            break;

          Move rightMove = new Move(piece, piece.getCol(), piece.getRow(), "RIGHT", steps);
          if (isValidMove(rightMove)) {
            moves.add(rightMove);
          } else {
            break; // Stop if we hit an obstacle
          }
        }
      } else if (piece.getOrientation() == 'V') {
        // For vertical pieces, try UP and DOWN moves
        // Try moves upward
        for (int steps = 1; steps <= rows; steps++) {
          if (piece.getRow() - steps < 0)
            break; // Don't go beyond the top edge

          Move upMove = new Move(piece, piece.getCol(), piece.getRow(), "UP", steps);
          if (isValidMove(upMove)) {
            moves.add(upMove);
          } else {
            break; // Stop if we hit an obstacle
          }
        }

        // Try moves downward
        for (int steps = 1; steps <= rows; steps++) {
          if (piece.getRow() + piece.getSize() + steps - 1 >= rows)
            break; // Don't go beyond the bottom edge

          Move downMove = new Move(piece, piece.getCol(), piece.getRow(), "DOWN", steps);
          if (isValidMove(downMove)) {
            moves.add(downMove);
          } else {
            break; // Stop if we hit an obstacle
          }
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
    if (x < 0 || x >= cols || y < 0 || y >= rows) {
      return '.';
    }

    return this.grid[y][x];
  }

  public HashMap<String, Piece> getPieces() {
    return this.pieces;
  }

  public Piece getPrimaryPiece() {
    return pieces.get("P");
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

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        sb.append(grid[i][j]);
      }
    }
    return sb.toString();
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
