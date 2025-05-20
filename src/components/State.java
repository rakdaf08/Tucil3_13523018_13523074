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
    while (current != null) {
      path.add(current);
      current = current.parent;
    }
    Collections.reverse(path);

    for (State state : path) {
      int index = state.getCostSoFar() + 1;
      StringBuilder sb = new StringBuilder();
      sb.append(String.format("%d. Move: ", index));

      Move move = state.getMove();
      if (move != null) {

        sb.append(String.format("Move piece %c %s by %d steps",
            move.getPiece().getLetter(),
            move.getDirection(),
            move.getSteps()));
      } else {
        sb.append("Initial State");
      }

      sb.append("\nHeuristic: ").append(state.getHeuristic());
      sb.append("\nBoard:\n");

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

    boolean kOnTopBorder = (kRow == 0);
    boolean kOnBottomBorder = (kRow == board.getRows() - 1);
    boolean kOnLeftBorder = (kCol == 0);
    boolean kOnRightBorder = (kCol == board.getCols() - 1);

    boolean kAboveBoard = (kRow == -1);
    boolean kBelowBoard = (kRow == board.getRows());
    boolean kLeftOfBoard = (kCol == -1);
    boolean kRightOfBoard = (kCol == board.getCols());

    if (primaryPiece.getOrientation() == 'H') {
      int pieceStart = primaryPiece.getCol();
      int pieceEnd = pieceStart + primaryPiece.getSize() - 1;
      int pieceRow = primaryPiece.getRow();

      if (kAboveBoard || kOnTopBorder) {
        return (pieceRow == 0) && (kCol >= pieceStart && kCol <= pieceEnd);
      } else if (kBelowBoard || kOnBottomBorder) {
        return (pieceRow == board.getRows() - 1) && (kCol >= pieceStart && kCol <= pieceEnd);
      } else if (kLeftOfBoard || kOnLeftBorder) {
        return (pieceStart == 0) && (pieceRow == kRow);
      } else if (kRightOfBoard || kOnRightBorder) {
        return (pieceEnd == board.getCols() - 1) && (pieceRow == kRow);
      }
    } else if (primaryPiece.getOrientation() == 'V') {
      int pieceStart = primaryPiece.getRow();
      int pieceEnd = pieceStart + primaryPiece.getSize() - 1;
      int pieceCol = primaryPiece.getCol();

      if (kAboveBoard || kOnTopBorder) {
        return (pieceStart == 0) && (pieceCol == kCol);
      } else if (kBelowBoard || kOnBottomBorder) {
        return (pieceEnd == board.getRows() - 1) && (pieceCol == kCol);
      } else if (kLeftOfBoard || kOnLeftBorder) {
        return (pieceCol == 0) && (kRow >= pieceStart && kRow <= pieceEnd);
      } else if (kRightOfBoard || kOnRightBorder) {
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
