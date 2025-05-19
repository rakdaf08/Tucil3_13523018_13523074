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
    
    // Determine if exit is on a border or outside
    boolean exitOnTop = (exitRow == -1);
    boolean exitOnBottom = (exitRow == boardRows);
    boolean exitOnLeft = (exitCol == -1);
    boolean exitOnRight = (exitCol == boardCols);
    
    if (primary.getOrientation() == 'H') {
      // Horizontal piece
      int pieceRow = primary.getRow();
      int pieceStartCol = primary.getCol();
      int pieceEndCol = pieceStartCol + primary.getSize() - 1;
      
      if (exitOnRight) {
        // Exit is on the right border
        distance = Math.max(0, boardCols - 1 - pieceEndCol);
      } 
      else if (exitOnLeft) {
        // Exit is on the left border
        distance = pieceStartCol;
      }
      else if ((exitOnTop || exitOnBottom) && 
              (exitCol >= 0 && exitCol < boardCols) && 
              (exitCol >= pieceStartCol && exitCol <= pieceEndCol)) {
        // Exit is on top/bottom AND the piece is already in the correct column
        distance = 0;
      }
      else {
        // Exit is elsewhere or not aligned - use Manhattan distance as fallback
        int targetCol = Math.min(Math.max(exitCol, 0), boardCols - 1); // Clamp to valid board position
        distance = Math.abs(targetCol - pieceEndCol) + Math.abs(Math.min(Math.max(exitRow, 0), boardRows - 1) - pieceRow);
      }
    } 
    else { // Vertical piece
      int pieceCol = primary.getCol();
      int pieceStartRow = primary.getRow();
      int pieceEndRow = pieceStartRow + primary.getSize() - 1;
      
      if (exitOnBottom) {
        // Exit is on the bottom border
        distance = Math.max(0, boardRows - 1 - pieceEndRow);
      } 
      else if (exitOnTop) {
        // Exit is on the top border
        distance = pieceStartRow;
      }
      else if ((exitOnLeft || exitOnRight) && 
              (exitRow >= 0 && exitRow < boardRows) && 
              (exitRow >= pieceStartRow && exitRow <= pieceEndRow)) {
        // Exit is on left/right AND the piece is already in the correct row
        distance = 0;
      }
      else {
        // Exit is elsewhere or not aligned - use Manhattan distance as fallback
        int targetRow = Math.min(Math.max(exitRow, 0), boardRows - 1); // Clamp to valid board position
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
    
    // Determine exit direction
    boolean exitOnTop = (exitRow == -1 || exitRow == 0);
    boolean exitOnBottom = (exitRow == boardRows - 1 || exitRow == boardRows);
    boolean exitOnLeft = (exitCol == -1 || exitCol == 0);
    boolean exitOnRight = (exitCol == boardCols - 1 || exitCol == boardCols);

    if (primary.getOrientation() == 'H') {
      int row = primary.getRow();
      int pieceStartCol = primary.getCol();
      int pieceEndCol = pieceStartCol + primary.getSize() - 1;
      
      if (exitOnRight) {
        // Count pieces between the primary and right border
        for (int col = pieceEndCol + 1; col < boardCols; col++) {
          if (isBlockingPiece(board, row, col)) {
            count++;
          }
        }
      } 
      else if (exitOnLeft) {
        // Count pieces between the left border and the primary
        for (int col = 0; col < pieceStartCol; col++) {
          if (isBlockingPiece(board, row, col)) {
            count++;
          }
        }
      }
      // If exit is on top/bottom and aligned with piece, no blocking pieces
    } 
    else { // Vertical piece
      int col = primary.getCol();
      int pieceStartRow = primary.getRow();
      int pieceEndRow = pieceStartRow + primary.getSize() - 1;
      
      if (exitOnBottom) {
        // Count pieces between the primary and bottom border
        for (int row = pieceEndRow + 1; row < boardRows; row++) {
          if (isBlockingPiece(board, row, col)) {
            count++;
          }
        }
      } 
      else if (exitOnTop) {
        // Count pieces between the top border and the primary
        for (int row = 0; row < pieceStartRow; row++) {
          if (isBlockingPiece(board, row, col)) {
            count++;
          }
        }
      }
      // If exit is on left/right and aligned with piece, no blocking pieces
    }

    return count;
  }
  
  // Helper method to check if a cell contains a blocking piece
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