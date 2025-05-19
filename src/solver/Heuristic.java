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

    return distance;
  }

  public static int countBlockingPieces(Board board) {
    int count = 0;

    Piece primary = board.getPieces().get("P");
    if (primary.getOrientation() == 'H') {
      int row = primary.getRow();
      int startCol = primary.getCol() + primary.getSize();
      for (int col = startCol; col <= IO.getKCol(); col++) {
        if (board.getCell(row, col) != '.' && board.getCell(row, col) != 'K') {
          count++;
        }
      }
    } else {
      int col = primary.getCol();
      int startRow = primary.getRow() + primary.getSize();
      for (int row = startRow; row <= IO.getKRow(); row++) {
        if (board.getCell(row, col) != '.' && board.getCell(row, col) != 'K') {
          count++;
        }
      }
    }

    return count;
  }

  public static int getHeuristic(Board board, String heuristicType) {
    if ("countBlockingPieces".equalsIgnoreCase(heuristicType)) {
      return countBlockingPieces(board);
    } else {
      // Default: pieceToDest
      return pieceToDest(board);
    }
  }
}