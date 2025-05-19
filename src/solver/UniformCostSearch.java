package solver;

import components.*;
import java.util.*;

public class UniformCostSearch {
  private PriorityQueue<State> queue;
  private HashSet<String> visited;
  private int nodesExpanded;

  public UniformCostSearch() {
    this.queue = new PriorityQueue<>(Comparator.comparingInt(State::getCostSoFar));
    this.visited = new HashSet<>();
    this.nodesExpanded = 0;
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

      if (current != null && current.isWin()) {
        return current;
      }

      String boardHash = currentBoard.toString();
      if (visited.contains(boardHash)) {
        continue;
      }
      visited.add(boardHash);
      nodesExpanded++;

      for (Move move : current.getBoard().getPossibleMoves()) {
        Board newBoard = current.getBoard().copy();
        Piece movedPiece = newBoard.getPieces().get(String.valueOf(move.getPiece().getLetter()));
        Move newMove = new Move(
            movedPiece,
            move.getStartX(),
            move.getStartY(),
            move.getDirection(),
            move.getSteps());
        newBoard.makeMove(newMove);

        String newBoardHash = newBoard.toString();
        if (visited.contains(newBoardHash)) {
          continue;
        }

        State newState = new State(
            newBoard,
            current.getCostSoFar() + 1,
            0,
            current,
            newMove);

        queue.add(newState);
      }
    }

    return null;
  }

  public int getNodesExpanded() {
    return nodesExpanded;
  }
}