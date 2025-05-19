package solver;

import java.util.*;
import components.*;

public class AStar {

    public static State solve(Board initialBoard) {
        // Priority queue for A* algorithm
        PriorityQueue<State> openList = new PriorityQueue<>();
        Set<String> closedList = new HashSet<>();

        // Start with the initial state
        int initialHeuristic = Heuristic.pieceToDest(initialBoard);
        State initialState = new State(initialBoard, 0, initialHeuristic, null, null);
        openList.add(initialState);
        int statesExplored = 0;

        while (!openList.isEmpty()) {
            // Get the state with the lowest f value
            State current = openList.poll();
            statesExplored++;

            int kCol = IO.getKCol();
            int kRow = IO.getKRow();
            Piece primaryPiece = current.getBoard().getPieces().get("P");
            int pRowStart = primaryPiece.getRow();
            int pColStart = primaryPiece.getCol();
            int pRowEnd = pRowStart + (primaryPiece.isVertical() ? primaryPiece.getSize() - 1 : 0);
            int pColEnd = pColStart + (primaryPiece.isHorizontal() ? primaryPiece.getSize() - 1 : 0);
            current.getBoard().printBoard();

            System.out.printf(
                    "States: %d, Primary Start Row/Col: (%d, %d), End Row/Col: (%d, %d), Exit Row/Col: (%d, %d)\n",
                    statesExplored,
                    pRowStart,
                    pColStart,
                    pRowEnd,
                    pColEnd,
                    kRow,
                    kCol);

            if (current.isWin()) {
                System.out.println("Solution found after exploring " + statesExplored + " states!");
                current.getBoard().printBoard();
                return current;
            }

            // Add current state to closed list to avoid revisiting
            String boardHash = current.getBoard().toString();
            if (closedList.contains(boardHash)) {
                continue;
            }
            closedList.add(boardHash);

            // Generate all possible next states
            List<Move> possibleMoves = current.getBoard().getPossibleMoves();
            for (Move move : possibleMoves) {
                // Create a copy of the board and apply the move
                Board nextBoard = current.getBoard().copy();
                // nextBoard.printBoard();
                Piece movedPiece = nextBoard.getPieces().get(String.valueOf(move.getPiece().getLetter()));

                Move newMove = new Move(
                    movedPiece,
                    move.getStartX(),
                    move.getStartY(),
                    move.getDirection(),
                    move.getSteps());
                try {
                    nextBoard.makeMove(newMove);

                    // Create new state
                    int heuristic = Heuristic.pieceToDest(nextBoard);
                    State nextState = new State(nextBoard, current.getCostSoFar() + 1, heuristic, current, move);

                    // If this board hasn't been explored yet, add it to the open list
                    String nextBoardHash = nextBoard.toString();
                    if (!closedList.contains(nextBoardHash)) {
                        openList.add(nextState);
                    }
                } catch (IllegalArgumentException e) {
                    // Skip invalid moves
                    System.err.println("Warning: " + e.getMessage());
                }
            }
        }

        // No solution found
        System.out.println("No solution found after exploring " + statesExplored + " states.");
        return null;
    }

    public static void printSolution(State solutionState) {
        if (solutionState == null) {
            System.out.println("No solution found.");
            return;
        }

        List<Move> moves = solutionState.getPathFromRoot();
        System.out.println("Solution found in " + moves.size() + " moves:");

        State currentState = new State(solutionState.getBoard().copy(), 0, 0, null, null);
        while (currentState.getParent() != null) {
            currentState = currentState.getParent();
        }

        // Print initial state
        System.out.println("Initial state:");
        currentState.printState();
        System.out.println();

        for (int i = 0; i < moves.size(); i++) {
            Move move = moves.get(i);
            Board board = currentState.getBoard().copy();
            board.makeMove(move);
            currentState = new State(board, 0, 0, null, null);

            System.out.println("Move " + (i + 1) + ": " + move);
            currentState.printState();
            System.out.println();
        }
    }
}
