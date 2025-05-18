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
    if (letter == '.' || letter == 'K' || letter == '|' || letter == '-') {
        return null;
    }

    currentLetter = letter;

    // Determine orientation and size
    char orientation = pieceOrientation(letter, x, y, board);
    int size = pieceSize(letter, x, y, orientation, board);
    
    System.out.println("Creating piece " + letter + " at [" + x + "][" + y + 
                      "] with orientation " + orientation + " and size " + size);

    return new Piece(x, y, size, orientation, letter == PRIMARY_PIECE);
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
    System.out.println("Piece: " + letter + " Right: " + board.getCell(x+1, y) + " Bottom: " + board.getCell(x, y + 1));
    
    // Determine orientation based on neighbors
    if (hasHorizontalNeighbor) {
        System.out.println("Horizontal piece detected for " + letter + " at [" + x + "][" + y + "]");
        return 'H';
    } else if (hasVerticalNeighbor) {
        System.out.println("Vertical piece detected for " + letter + " at [" + x + "][" + y + "]");
        return 'V';
    } else {
        System.out.println("Single piece detected for " + letter + " at [" + x + "][" + y + "]");
        return 'S';  // Single cell piece
    }
}

private static int pieceSize(char letter, int x, int y, char orientation, Board board) {
    int size = 1;  // Start with size 1 (the cell itself)
    
    if (orientation == 'H') {
        // Count horizontally
        int j = x + 1;
        while (j < board.getCols() && board.getCell(j, y) == letter) {
            size++;
            j++;
        }
        System.out.println("Horizontal piece " + letter + " at [" + x + "][" + y + "] has size " + size);
    } else if (orientation == 'V') {
        // Count vertically
        int i = y + 1;
        while (i < board.getRows() && board.getCell(x, i) == letter) {
            size++;
            i++;
        }
        System.out.println("Vertical piece " + letter + " at [" + x + "][" + y + "] has size " + size);
    }
    // For orientation 'S', size remains 1
    
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