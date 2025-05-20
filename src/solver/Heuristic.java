package solver;

import components.*;

public class Heuristic {
  public static int pieceToDest(Board board) {
    int exitRow = IO.getKRow();
    int exitCol = IO.getKCol();
    int boardRows = board.getRows(); 
    int boardCols = board.getCols();

    Piece primary = board.getPieces().get("P");
    if (primary == null)
      return 0;

    int distance = 0;
    
    boolean exitOnTop = (exitRow == -1);
    boolean exitOnBottom = (exitRow == boardRows);
    boolean exitOnLeft = (exitCol == -1);
    boolean exitOnRight = (exitCol == boardCols);
    
    if (primary.getOrientation() == 'H') {
      int pieceRow = primary.getRow();
      int pieceStartCol = primary.getCol();
      int pieceEndCol = pieceStartCol + primary.getSize() - 1;
      
      if (exitOnRight) {
        distance = Math.max(0, boardCols - 1 - pieceEndCol);
      } 
      else if (exitOnLeft) {
        distance = pieceStartCol;
      }
      else if ((exitOnTop || exitOnBottom) && 
              (exitCol >= 0 && exitCol < boardCols) && 
              (exitCol >= pieceStartCol && exitCol <= pieceEndCol)) {
        distance = 0;
      }
      else {
        // Exit tidak ditemukan atau tidak aligned pakai Manhattan sebagai fallback
        int targetCol = Math.min(Math.max(exitCol, 0), boardCols - 1); // Clamp to valid board position
        distance = Math.abs(targetCol - pieceEndCol) + Math.abs(Math.min(Math.max(exitRow, 0), boardRows - 1) - pieceRow);
      }
    } 
    else { 
      int pieceCol = primary.getCol();
      int pieceStartRow = primary.getRow();
      int pieceEndRow = pieceStartRow + primary.getSize() - 1;
      
      if (exitOnBottom) {
        distance = Math.max(0, boardRows - 1 - pieceEndRow);
      } 
      else if (exitOnTop) {
        distance = pieceStartRow;
      }
      else if ((exitOnLeft || exitOnRight) && 
              (exitRow >= 0 && exitRow < boardRows) && 
              (exitRow >= pieceStartRow && exitRow <= pieceEndRow)) {
        distance = 0;
      }
      else {
        // Exit tidak ditemukan atau tidak aligned pakai Manhattan sebagai fallback
        int targetRow = Math.min(Math.max(exitRow, 0), boardRows - 1);
        distance = Math.abs(targetRow - pieceEndRow) + Math.abs(Math.min(Math.max(exitCol, 0), boardCols - 1) - pieceCol);
      }
    }
    
    return distance;
  }

  public static int countBlockingPieces(Board board) {
    int exitRow = IO.getKRow();
    int exitCol = IO.getKCol();
    int boardRows = board.getRows();
    int boardCols = board.getCols();
    int count = 0;

    Piece primary = board.getPieces().get("P");
    if (primary == null)
      return 0;
    
    // Arah pintu keluar
    boolean exitOnTop = (exitRow == -1);
    boolean exitOnBottom = (exitRow == boardRows);
    boolean exitOnLeft = (exitCol == -1);
    boolean exitOnRight = (exitCol == boardCols);

    if (primary.getOrientation() == 'H') {
      int row = primary.getRow();
      int pieceStartCol = primary.getCol();
      int pieceEndCol = pieceStartCol + primary.getSize() - 1;
      
      if (exitOnRight) {
        for (int col = pieceEndCol + 1; col < boardCols; col++) {
          if (isBlockingPiece(board, row, col)) {
            count++;
          }
        }
      } 
      else if (exitOnLeft) {
        for (int col = 0; col < pieceStartCol; col++) {
          if (isBlockingPiece(board, row, col)) {
            count++;
          }
        }
      }
    } 
    else {
      int col = primary.getCol();
      int pieceStartRow = primary.getRow();
      int pieceEndRow = pieceStartRow + primary.getSize() - 1;
      
      if (exitOnBottom) {
        for (int row = pieceEndRow + 1; row < boardRows; row++) {
          if (isBlockingPiece(board, row, col)) {
            count++;
          }
        }
      } 
      else if (exitOnTop) {
        for (int row = 0; row < pieceStartRow; row++) {
          if (isBlockingPiece(board, row, col)) {
            count++;
          }
        }
      }
    }

    return count;
  }
  
  // Helper method untuk cek apakah cell mengandung piece
  private static boolean isBlockingPiece(Board board, int row, int col) {
    char cell = board.getCell(col, row);
    return cell != '.' && cell != 'K' && cell != '-' && cell != '|';
  }

  public static int combineTwo(Board board) {
    return pieceToDest(board) + countBlockingPieces(board);
  }

  public static int getHeuristic(Board board, String heuristicType) {
    if ("countBlockingPieces".equalsIgnoreCase(heuristicType)) {
      return countBlockingPieces(board);
    } else if ("pieceToDest".equalsIgnoreCase(heuristicType)) {
      return pieceToDest(board);
    } else {
      return combineTwo(board);
    }
  }
}