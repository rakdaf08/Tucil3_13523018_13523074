package components;

import java.util.Set;

public class Piece {
  protected int col;
  protected int row;
  protected int size;
  protected int totalPiece;
  protected char orientation;
  protected boolean isPrimary;

  private static char currentLetter;

  private static final char PRIMARY_PIECE = 'P';

  public Piece(int x, int y, int size, char orientation, boolean isPrimary) {
    this.col = x;
    this.row = y;
    this.size = size;
    this.orientation = orientation;
    this.totalPiece = 0;
    this.isPrimary = isPrimary;
  }

  public static Piece pieceFromBoard(char letter, int x, int y, Board board) {
    if (letter == '.' || letter == 'K' || letter == '|') {
      return null;
    }

    currentLetter = letter;

    char orientation = pieceOrientation(letter, x, y, board);
    int size = pieceSize(letter, x, y, orientation, board);

    return new Piece(x, y, size, orientation, letter == PRIMARY_PIECE);
  }

  public static Piece pieceFromBoard(char letter, int x, int y, Board board, Set<String> visited) {
    String position = x + "," + y;
    if (visited.contains(position) || letter == '.' || letter == 'K' || letter == '|') {
      return null;
    }

    visited.add(position);
    currentLetter = letter;

    char orientation = pieceOrientation(letter, x, y, board);
    int size = pieceSize(letter, x, y, orientation, board, visited);

    return new Piece(x, y, size, orientation, letter == PRIMARY_PIECE);
  }

  private static char pieceOrientation(char letter, int x, int y, Board board) {
    if (y + 1 < board.getCols() && board.getCell(x, y + 1) == letter) {
      return 'H';
    }

    if (x + 1 < board.getRows() && board.getCell(x + 1, y) == letter) {
      return 'V';
    }

    return 'S';
  }

  private static int pieceSize(char letter, int x, int y, char orientation, Board board) {
    int size = 1;
    if (orientation == 'H') {
      int j = y + 1;
      while (j < board.getCols() && board.getCell(x, j) == letter) {
        size++;
        j++;
      }
    } else if (orientation == 'V') {
      int i = x + 1;
      while (i < board.getRows() && board.getCell(i, y) == letter) {
        size++;
        i++;
      }
    }
    return size;
  }

  private static int pieceSize(char letter, int x, int y, char orientation, Board board, Set<String> visited) {
    int size = 1;
    if (orientation == 'H') {
      int j = y + 1;
      while (j < board.getCols() && board.getCell(x, j) == letter) {
        visited.add(x + "," + j); // Mark cells as visited
        size++;
        j++;
      }
    } else if (orientation == 'V') {
      int i = x + 1;
      while (i < board.getRows() && board.getCell(i, y) == letter) {
        visited.add(i + "," + y); // Mark cells as visited
        size++;
        i++;
      }
    }
    return size;
  }

  public static char getCurrentLetter() {
    return currentLetter;
  }

  public int getCol() {
    return col;
  }

  public void setCol(int x) {
    this.col = x;
  }

  public int getRow() {
    return row;
  }

  public void setRow(int y) {
    this.row = y;
  }

  public int getSize() {
    return size;
  }

  public int getTotalPiece() {
    return totalPiece;
  }

  public char getOrientation() {
    return orientation;
  }

  public boolean isPrimary() {
    return isPrimary;
  }

  @Override
  public String toString() {
    return String.valueOf(currentLetter);
  }
}