package solver;

import components.*;

public class Heuristic {
  public static int pieceToDest(Board board) {
    int exitRow = IO.getKRow();
    int exitCol = IO.getKCol();

    Piece primary = board.getPieces().get("P");
    if (primary == null)
      return 0;

    int distance = 0;
    if (primary.getOrientation() == 'H') {
      int pieceEndCol = primary.getCol() + primary.getSize() - 1;
      distance = Math.abs(exitCol - pieceEndCol);
    } else {
      int pieceEndRow = primary.getRow() + primary.getSize() - 1;
      distance = Math.abs(exitRow - pieceEndRow);
    }

    // Add blocking pieces penalty
    int blockingPieces = countBlockingPieces(board, primary, exitRow, exitCol);

    return distance + blockingPieces;
  }

  private static int countBlockingPieces(Board board, Piece primary, int exitRow, int exitCol) {
    int count = 0;

    if (primary.getOrientation() == 'H') {
      int row = primary.getRow();
      int startCol = primary.getCol() + primary.getSize();
      for (int col = startCol; col <= exitCol; col++) {
        if (board.getCell(col, row) != '.' && board.getCell(col, row) != 'K') {
          count++;
        }
      }
    } else {
      int col = primary.getCol();
      int startRow = primary.getRow() + primary.getSize();
      for (int row = startRow; row <= exitRow; row++) {
        if (board.getCell(col, row) != '.' && board.getCell(col, row) != 'K') {
          count++;
        }
      }
    }

    return count;
  }
}