
package components;

import java.util.Set;

public class Piece {
  protected int col;
  protected int row;
  protected int size;
  protected int totalPiece;
  protected char orientation;
  protected char letter;
  protected boolean isPrimary;

  private static char currentLetter;

  private static final char PRIMARY_PIECE = 'P';

  public Piece(char letter, int x, int y, int size, char orientation, boolean isPrimary) {
    this.letter = letter;
    this.col = x;
    this.row = y;
    this.size = size;
    this.orientation = orientation;
    this.totalPiece = 0;
    this.isPrimary = isPrimary;
  }

  public static Piece pieceFromBoard(char letter, int x, int y, Board board) {
    if (letter == '.' || letter == 'K' || letter == '|' || letter == '-') {
      return null;
    }

    currentLetter = letter;

    // Determine orientation and size
    char orientation = pieceOrientation(letter, x, y, board);
    int size = pieceSize(letter, x, y, orientation, board);

    return new Piece(letter, x, y, size, orientation, letter == PRIMARY_PIECE);
  }

  private static char pieceOrientation(char letter, int x, int y, Board board) {
    // Check to the right (horizontally)
    boolean hasHorizontalNeighbor = false;
    if (x + 1 < board.getCols() && board.getCell(x + 1, y) == letter) {
      hasHorizontalNeighbor = true;
    }

    // Check below (vertically)
    boolean hasVerticalNeighbor = false;
    if (y + 1 < board.getRows() && board.getCell(x, y + 1) == letter) {
      hasVerticalNeighbor = true;
    }
    // Determine orientation based on neighbors
    if (hasHorizontalNeighbor) {
      return 'H';
    } else if (hasVerticalNeighbor) {
      return 'V';
    } else {
      return 'S'; // Single cell piece
    }
  }

  private static int pieceSize(char letter, int x, int y, char orientation, Board board) {
    int size = 1; // Start with size 1 (the cell itself)

    if (orientation == 'H') {
      // Count horizontally
      int j = x + 1;
      while (j < board.getCols() && board.getCell(j, y) == letter) {
        size++;
        j++;
      }
    } else if (orientation == 'V') {
      // Count vertically
      int i = y + 1;
      while (i < board.getRows() && board.getCell(x, i) == letter) {
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
  
  public boolean isHorizontal() {
    return orientation == 'H';
  }
  
  public boolean isVertical() {
    return orientation == 'V';
  }

  @Override
  public String toString() {
    return String.valueOf(currentLetter);
  }

  public void setSize(int size) {
    this.size = size;
  }

  public void setTotalPiece(int totalPiece) {
    this.totalPiece = totalPiece;
  }

  public void setOrientation(char orientation) {
    this.orientation = orientation;
  }

  public char getLetter() {
    return letter;
  }

  public void setLetter(char letter) {
    this.letter = letter;
  }

  public void setPrimary(boolean isPrimary) {
    this.isPrimary = isPrimary;
  }

  public static void setCurrentLetter(char currentLetter) {
    Piece.currentLetter = currentLetter;
  }

  public static char getPrimaryPiece() {
    return PRIMARY_PIECE;
  }
}
