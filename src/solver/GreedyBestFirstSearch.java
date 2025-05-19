package solver;

import components.*;
import java.util.*;

public class GreedyBestFirstSearch {
  public State solve(Board initialBoard, String type) {
    PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(State::getHeuristic));
    HashSet<String> visited = new HashSet<>();
    int nodesExpanded = 0;

    int initialHeuristic = Heuristic.getHeuristic(initialBoard, type);
    State initialState = new State(initialBoard, 0, initialHeuristic, null, null);
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

      System.out.println("Jumlah node:" + nodesExpanded);

      for (Move move : currentBoard.getPossibleMoves()) {
        Board newBoard = currentBoard.copy();
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

        int heuristic = Heuristic.getHeuristic(newBoard, type);
        State newState = new State(
            newBoard,
            current.getCostSoFar() + 1,
            heuristic,
            current,
            newMove);
        queue.add(newState);
      }
    }
    return null;
  }
}