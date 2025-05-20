package components;

import java.util.*;

public class State implements Comparable<State> {
  private Board board;
  private int costSoFar;
  private int heuristic;
  private State parent;
  private Move move;
  private int totalNodeVisited;

  public int getTotalNodeVisited() {
    return totalNodeVisited;
  }

  public void setTotalNodeVisited(int totalNodeVisited) {
    this.totalNodeVisited = totalNodeVisited;
  }

  public State(Board board, int costSoFar, int heuristic, State parent, Move move, int totalNodeVisited) {
    this.board = board;
    this.costSoFar = costSoFar;
    this.heuristic = heuristic;
    this.parent = parent;
    this.move = move;
    this.totalNodeVisited = totalNodeVisited;
  }

  public int getF() {
    return costSoFar + heuristic;
  }

  public int compareTo(State other) {
    return Integer.compare(this.getF(), other.getF());
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof State))
      return false;
    State other = (State) o;
    if (this.board == null || other.board == null)
      return false;
    return board.equals(other.board);
  }

  @Override
  public int hashCode() {
    return board.hashCode();
  }

  public List<Move> getPathFromRoot() {
    List<Move> path = new ArrayList<>();
    State current = this;
    while (current.parent != null) {
      path.add(current.move);
      current = current.parent;
    }
    Collections.reverse(path);
    return path;
  }

  public String[] getSolutionPath() {
    List<String> steps = new ArrayList<>();
    State current = this;
    List<State> path = new ArrayList<>();
    // Trace the path back to the root
    while (current != null) {
        path.add(current);
        current = current.parent;
    }
    Collections.reverse(path);

    // Format the path into strings
    for (State state : path) {
        int index = state.getCostSoFar() + 1;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d. Move: ", index));
        
        // Include the move description if not the initial state
        Move move = state.getMove();
        if (move != null) {
          
        sb.append(String.format("Move piece %c %s by %d steps",
                            move.getPiece().getLetter(),
                            move.getDirection(),
                            move.getSteps()));}
                             else {
          sb.append("Initial State");
        }
        
        sb.append("\nHeuristic: ").append(state.getHeuristic());
        sb.append("\nBoard:\n");
        
        // Print the board
        for (int i = 0; i < state.getBoard().getRows(); i++) {
            for (int j = 0; j < state.getBoard().getCols(); j++) {
                sb.append(state.getBoard().getCell(j, i));
            }
            sb.append("\n");
        }
        
        steps.add(sb.toString());
    }

    return steps.toArray(new String[0]);
}

  public boolean isWin() {
  if (board == null) {
      return false;
  }

  Piece primaryPiece = board.getPieces().get("P");
  if (primaryPiece == null) {
      return false;
  }

  int kRow = IO.getKRow();
  int kCol = IO.getKCol();
  
  // Determine if K is on a border
  boolean kOnTopBorder = (kRow == 0);
  boolean kOnBottomBorder = (kRow == board.getRows() - 1);
  boolean kOnLeftBorder = (kCol == 0);
  boolean kOnRightBorder = (kCol == board.getCols() - 1);
  
  // K could also be just outside the board
  boolean kAboveBoard = (kRow == -1);
  boolean kBelowBoard = (kRow == board.getRows());
  boolean kLeftOfBoard = (kCol == -1);
  boolean kRightOfBoard = (kCol == board.getCols());
  
  // Check if the primary piece has reached the exit
  if (primaryPiece.getOrientation() == 'H') {
      // For horizontal pieces
      int pieceStart = primaryPiece.getCol();
      int pieceEnd = pieceStart + primaryPiece.getSize() - 1;
      int pieceRow = primaryPiece.getRow();

      if (kAboveBoard || kOnTopBorder) {
          // Exit is on top - piece must be on top row and covering the K column
          return (pieceRow == 0) && (kCol >= pieceStart && kCol <= pieceEnd);
      } 
      else if (kBelowBoard || kOnBottomBorder) {
          // Exit is on bottom - piece must be on bottom row and covering the K column
          return (pieceRow == board.getRows() - 1) && (kCol >= pieceStart && kCol <= pieceEnd);
      }
      else if (kLeftOfBoard || kOnLeftBorder) {
          // Exit is on left - piece must touch left edge and be at K's row
          return (pieceStart == 0) && (pieceRow == kRow);
      } 
      else if (kRightOfBoard || kOnRightBorder) {
          // Exit is on right - piece must touch right edge and be at K's row
          return (pieceEnd == board.getCols() - 1) && (pieceRow == kRow);
      }
  } 
  else if (primaryPiece.getOrientation() == 'V') {
      // For vertical pieces
      int pieceStart = primaryPiece.getRow();
      int pieceEnd = pieceStart + primaryPiece.getSize() - 1;
      int pieceCol = primaryPiece.getCol();

      if (kAboveBoard || kOnTopBorder) {
          // Exit is on top - piece must touch top edge and be at K's column
          return (pieceStart == 0) && (pieceCol == kCol);
      } 
      else if (kBelowBoard || kOnBottomBorder) {
          // Exit is on bottom - piece must touch bottom edge and be at K's column
          return (pieceEnd == board.getRows() - 1) && (pieceCol == kCol);
      }
      else if (kLeftOfBoard || kOnLeftBorder) {
          // Exit is on left - piece must be at leftmost column and covering K's row
          return (pieceCol == 0) && (kRow >= pieceStart && kRow <= pieceEnd);
      } 
      else if (kRightOfBoard || kOnRightBorder) {
          // Exit is on right - piece must be at rightmost column and covering K's row
          return (pieceCol == board.getCols() - 1) && (kRow >= pieceStart && kRow <= pieceEnd);
      }
  }
  
  return false;
}

  public void printState() {
    for (int i = 0; i < board.getRows(); i++) {
      if (i > 0) {
        System.out.println("");
      }
      for (int j = 0; j < board.getCols(); j++) {
        System.out.print(board.getCell(j, i));
      }
    }
  }

  public Board getBoard() {
    return board;
  }

  public int getCostSoFar() {
    return costSoFar;
  }

  public int getHeuristic() {
    return heuristic;
  }

  public State getParent() {
    return parent;
  }

  public Move getMove() {
    return move;
  }
}
