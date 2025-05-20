package solver;

import components.*;
import java.util.*;

public class IterativeDeepeningSearch {
  private int nodesExpanded;
  private int currentTotalVisited;

  public IterativeDeepeningSearch() {
    this.nodesExpanded = 0;
  }
  public State solve(Board initialBoard) throws Exception {
    int maxDepth = 0;
    currentTotalVisited = 0;
    
    while (true) {
      nodesExpanded = 0;
      State result = depthLimitedSearch(new State(initialBoard, 0, 0, null, null, currentTotalVisited), maxDepth);
      
      if (result != null) {
        return result;
      }
      maxDepth++;
    }
  }

  private State depthLimitedSearch(State current, int depthLimit) throws Exception {
    Stack<State> stack = new Stack<>();
    Set<String> visited = new HashSet<>();
    stack.push(current);

    while (!stack.isEmpty()) {
      State state = stack.pop();
      nodesExpanded++;
      
      if (state.isWin()) {
        return state;
      }

      if (state.getCostSoFar() < depthLimit) {
        for (Move move : state.getBoard().getPossibleMoves()) {
          Board newBoard = state.getBoard().copy();
          Piece movedPiece = newBoard.getPieces().get(String.valueOf(move.getPiece().getLetter()));
          
          Move newMove = new Move(
            movedPiece,
            move.getStartX(),
            move.getStartY(),
            move.getDirection(),
            move.getSteps());
          newBoard.makeMove(newMove);          String newBoardHash = newBoard.toString();
          if (!visited.contains(newBoardHash)) {
            visited.add(newBoardHash);            State newState = new State(newBoard, state.getCostSoFar() + 1, 0, state, move, currentTotalVisited + nodesExpanded);
            stack.push(newState);
          }
        }
      }
    }

    return null;
  }

  public int getNodesExpanded() {
    return nodesExpanded;
  }
}
