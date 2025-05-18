package solver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import components.Board;
import components.Move;
import components.Piece;
import components.State;

public class AStar {
    
    public static State solve(Board initialBoard) {
        // Priority queue for A* algorithm
        PriorityQueue<State> openList = new PriorityQueue<>();
        Set<String> closedList = new HashSet<>();
        
        // Start with the initial state
        int initialHeuristic = calculateHeuristic(initialBoard);
        State initialState = new State(initialBoard, 0, initialHeuristic, null, null);
        openList.add(initialState);
        
        while (!openList.isEmpty()) {
            // Get the state with the lowest f value
            State current = openList.poll();
            
            // Check if we've reached the goal state
            if (current.isWin()) {
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
                nextBoard.makeMove(move);
                
                // Create new state
                int heuristic = calculateHeuristic(nextBoard);
                State nextState = new State(nextBoard, current.getCostSoFar() + 1, heuristic, current, move);
                
                // If this board hasn't been explored yet, add it to the open list
                if (!closedList.contains(nextBoard.toString())) {
                    openList.add(nextState);
                }
            }
        }
        
        // No solution found
        return null;
    }
    
    // Calculate heuristic value (estimated cost to goal)
    private static int calculateHeuristic(Board board) {
        // Get the primary piece (usually labeled as 'P')
        Piece primaryPiece = board.getPieces().get("P");
        
        if (primaryPiece == null) {
            return 0; // This shouldn't happen in a valid Rush Hour puzzle
        }
        
        // For Rush Hour, a simple heuristic is the number of blocking pieces
        // plus the distance to the exit
        int blockingPieces = 0;
        int distanceToExit = 0;
        
        // Assuming the exit is on the right side and primary piece moves horizontally
        if (primaryPiece.getOrientation() == 'H') {
            int exitX = board.getCols() - 1;
            int exitY = primaryPiece.getRow(); // Assume the exit is in the same row
            
            // Calculate distance to exit
            distanceToExit = exitX - (primaryPiece.getCol() + primaryPiece.getSize() - 1);
            
            // Count blocking pieces
            for (int x = primaryPiece.getCol() + primaryPiece.getSize(); x <= exitX; x++) {
                if (board.getGrid()[exitY][x] != Board.EMPTY_GRID) {
                    blockingPieces++;
                }
            }
        }
        
        return blockingPieces + distanceToExit;
    }
    
    // Check if the current state is the goal state
    private static boolean isGoalState(Board board) {
        Piece primaryPiece = board.getPieces().get("P");
        
        if (primaryPiece == null) {
            return false;
        }
        
        // For Rush Hour, goal state is when the primary piece can exit
        // Assuming the exit is on the right side
        if (primaryPiece.getOrientation() == 'H') {
            int rightEdge = primaryPiece.getCol() + primaryPiece.getSize() - 1;
            int exitCol = board.getCols() - 1;
            
            // Check if there's a clear path to the exit
            boolean clearPath = true;
            for (int col = rightEdge + 1; col <= exitCol; col++) {
                if (board.getGrid()[primaryPiece.getRow()][col] != Board.EMPTY_GRID) {
                    clearPath = false;
                    break;
                }
            }
            
            return clearPath;
        }
        
        return false;
    }
    
    // Reconstruct the path from the goal state to the initial state
    private static List<String> reconstructPath(State goalState) {
        List<String> path = new ArrayList<>();
        State current = goalState;
        
        // Trace back from goal to start
        while (current.getParent() != null) {
            path.add(current.getMove().toString());
            current = current.getParent();
        }
        
        // Reverse to get path from start to goal
        Collections.reverse(path);
        return path;
    }
}
