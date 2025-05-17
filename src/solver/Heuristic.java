package solver;

import components.*;

public class Heuristic {
  public static int pieceToDest(Board board) {
    // Find K (exit) position
    int exitRow = -1;
    int exitCol = -1;
    for (int i = 0; i < board.getRows(); i++) {
      for (int j = 0; j < board.getCols(); j++) {
        if (board.getCell(i, j) == 'K') {
          exitRow = i;
          exitCol = j;
          break;
        }
      }
    }

    // Find P (primary piece) position
    Piece primary = board.getPieces().get("P");
    if (primary == null)
      return 0;

    // Calculate base distance
    int distance = 0;
    if (primary.getOrientation() == 'H') {
      // For horizontal piece, calculate horizontal distance to exit
      int pieceEndCol = primary.getY() + primary.getSize() - 1;
      distance = Math.abs(exitCol - pieceEndCol);
    } else {
      // For vertical piece, calculate vertical distance to exit
      int pieceEndRow = primary.getX() + primary.getSize() - 1;
      distance = Math.abs(exitRow - pieceEndRow);
    }

    // Add blocking pieces penalty
    int blockingPieces = countBlockingPieces(board, primary, exitRow, exitCol);

    return distance + blockingPieces;
  }

  private static int countBlockingPieces(Board board, Piece primary, int exitRow, int exitCol) {
    int count = 0;

    if (primary.getOrientation() == 'H') {
      // Count pieces blocking horizontal path to exit
      int row = primary.getX();
      int startCol = primary.getY() + primary.getSize();
      for (int col = startCol; col <= exitCol; col++) {
        if (board.getCell(row, col) != '.' && board.getCell(row, col) != 'K') {
          count++;
        }
      }
    } else {
      // Count pieces blocking vertical path to exit
      int col = primary.getY();
      int startRow = primary.getX() + primary.getSize();
      for (int row = startRow; row <= exitRow; row++) {
        if (board.getCell(row, col) != '.' && board.getCell(row, col) != 'K') {
          count++;
        }
      }
    }

    return count;
  }
}