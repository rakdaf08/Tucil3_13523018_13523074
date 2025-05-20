package solver;

import java.util.*;
import components.*;

public class AStar {

    public static State solve(Board initialBoard, String heuristicType) throws Exception {
        PriorityQueue<State> openList = new PriorityQueue<>();
        Set<String> closedList = new HashSet<>();

        int initialHeuristic = Heuristic.getHeuristic(initialBoard, heuristicType);
        State initialState = new State(initialBoard, 0, initialHeuristic, null, null, 0);
        openList.add(initialState);
        int statesExplored = 0;

        while (!openList.isEmpty()) {
            State current = openList.poll();
            statesExplored++;
            
            if (current.isWin()) {
                System.out.println("Solution found after exploring " + statesExplored + " states!");
                current.getBoard().printBoard();
                return current;
            }

            String boardHash = current.getBoard().toString();
            if (closedList.contains(boardHash)) {
                continue;
            }
            closedList.add(boardHash);

            // Generate all possible next states
            List<Move> possibleMoves = current.getBoard().getPossibleMoves();
            for (Move move : possibleMoves) {
                Board nextBoard = current.getBoard().copy();
                Piece movedPiece = nextBoard.getPieces().get(String.valueOf(move.getPiece().getLetter()));

                Move newMove = new Move(
                        movedPiece,
                        move.getStartX(),
                        move.getStartY(),
                        move.getDirection(),
                        move.getSteps());
                try {
                    nextBoard.makeMove(newMove);

                    // Buat state baru dan cek apakah sudah pernah dilalui apa belum
                    int heuristic = Heuristic.getHeuristic(nextBoard, heuristicType);
                    State nextState = new State(nextBoard, current.getCostSoFar() + 1, heuristic, current, move, statesExplored);

                    String nextBoardHash = nextBoard.toString();
                    if (!closedList.contains(nextBoardHash)) {
                        openList.add(nextState);
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Warning: " + e.getMessage());
                }
            }
        }

        System.out.println("No solution found after exploring " + statesExplored + " states.");
        return null;
    }
}
