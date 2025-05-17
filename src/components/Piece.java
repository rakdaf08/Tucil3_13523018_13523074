package components;

public class Piece {
  protected int x;
  protected int y;
  protected int size;
  protected int totalPiece;
  protected char orientation;
  protected boolean isPrimary;

  private static final char PRIMARY_PIECE = 'P';

  public Piece(int x, int y, int size, char orientation, boolean isPrimary) {
    this.x = x;
    this.y = y;
    this.size = size;
    this.orientation = orientation;
    this.totalPiece = 0;
    this.isPrimary = isPrimary;
  }

  public static Piece pieceFromBoard(char letter, int x, int y, Board board) {
    if (letter == '.' || letter == 'K') {
      return null;
    }

    char orientation = pieceOrientation(letter, x, y, board);
    int size = pieceSize(letter, x, y, board);

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

  private static int pieceSize(char letter, int x, int y, Board board) {
    int size = 1;
    if (pieceOrientation(letter, x, y, board) == 'H') {
      int j = y;
      while (j < board.getCols() && board.getCell(x, j + 1) == letter) {
        size++;
        j++;
      }
    } else if (pieceOrientation(letter, x, y, board) == 'V') {
      int i = x;
      while (i < board.getRows() && board.getCell(i + 1, y) == letter) {
        size++;
        i++;
      }
    }
    return size;
  }
}