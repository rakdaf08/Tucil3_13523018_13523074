package solver;

import components.*;
import java.util.*;

public class UniformCostSearch {
  private PriorityQueue<State> queue;
  private HashSet<Board> visited;
  private int nodesExpanded;

  public UniformCostSearch() {
    this.queue = new PriorityQueue<>();
    this.visited = new HashSet<>();
    this.nodesExpanded = 0;
  }

  private String boardToString(Board board) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < board.getRows(); i++) {
      for (int j = 0; j < board.getCols(); j++) {
        sb.append(board.getCell(i, j));
      }
    }
    return sb.toString();
  }

  public State solve(Board initialBoard) {
    queue.clear();
    visited.clear();
    nodesExpanded = 0;

    State initialState = new State(initialBoard, 0, 0, null, null);
    queue.add(initialState);

    while (!queue.isEmpty()) {
      State current = queue.poll();
      Board currentBoard = current.getBoard();

      if (visited.contains(currentBoard)) {
        continue;
      }

      visited.add(currentBoard);
      nodesExpanded++;

      if (current.isWin()&& current != null) {
        return current;
      }

      List<Move> possibleMoves = current.getBoard().getPossibleMoves();

      for (Move move : possibleMoves) {
        Board newBoard = current.getBoard().copy();
        newBoard.makeMove(move);

        State newState = new State(
            newBoard,
            current.getCostSoFar() + 1,
            0,
            current,
            move);

        queue.add(newState);
      }
    }

    return null;
  }

  public int getNodesExpanded() {
    return nodesExpanded;
  }
}