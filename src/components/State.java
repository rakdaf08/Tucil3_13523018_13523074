package components;

import java.util.*;

public class State implements Comparable<State> {
  private Board board;
  private int costSoFar;
  private int heuristic;
  private State parent;
  private Move move;

  public State(Board board, int costSoFar, int heuristic, State parent, Move move) {
    this.board = board;
    this.costSoFar = costSoFar;
    this.heuristic = heuristic;
    this.parent = parent;
    this.move = move;
  }

  public int getF() {
    return costSoFar + heuristic;
  }

  public int compareTo(State other) {
    return Integer.compare(this.getF(), other.getF());
  }

  public boolean equals(Object o) {
    if (!(o instanceof State))
      return false;
    return board.equals(((State) o).board);
  }

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
