
package components;

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

  public static Piece pieceFromBoard(char letter, int x, int y, Board board) throws Exception {
    if (letter == '.' || letter == 'K' || letter == '|' || letter == '-') {
      return null;
    }

    currentLetter = letter;

    char orientation = pieceOrientation(letter, x, y, board);
    int size = pieceSize(letter, x, y, orientation, board);
    if(size == 1){
      throw new Exception("Piece size cannot be 1");
    }

    return new Piece(letter, x, y, size, orientation, letter == PRIMARY_PIECE);
  }

  private static char pieceOrientation(char letter, int x, int y, Board board) {
    boolean hasHorizontalNeighbor = false;
    if (x + 1 < board.getCols() && board.getCell(x + 1, y) == letter) {
      hasHorizontalNeighbor = true;
    }

    if (hasHorizontalNeighbor) {
      return 'H';
    } else {
      return 'V';
    } 
  }

  private static int pieceSize(char letter, int x, int y, char orientation, Board board) {
    int size = 1;

    if (orientation == 'H') {
      int j = x + 1;
      while (j < board.getCols() && board.getCell(j, y) == letter) {
        size++;
        j++;
      }
    } else {
      int i = y + 1;
      while (i < board.getRows() && board.getCell(x, i) == letter) {
        size++;
        i++;
      }
    }

    return size;
  }

  public Piece copy() {
    return new Piece(this.letter, this.col, this.row, this.size, this.orientation, this.isPrimary);
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
    return String.valueOf(this.letter);
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
    return this.letter;
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
