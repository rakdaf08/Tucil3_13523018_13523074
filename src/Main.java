import components.*;
import solver.*;
import java.util.*;

public class Main {
  public static void main(String[] args) {
    try {
      // Read input file
      System.out.println("Enter input file path (from test/input folder): ");
      Scanner scanner = new Scanner(System.in);
      String inputFile = scanner.nextLine();
      String filepath = "test/input/" + inputFile;

      // Parse input and create board
      String[] inputString = IO.readFile(filepath);
      Board initialBoard = IO.parseInput(inputString);
      // Print initial board state
      System.out.println("\nInitial Board State:");
      initialBoard.printBoard();

      if (initialBoard.getPrimaryPiece().getOrientation() == 'H') {
        if (initialBoard.getPrimaryPiece().getRow() != IO.getKRow()) {
          System.out.println("Unsolvable: Exit location and primary piece is not alligned");
          scanner.close();
          return;
        }
      } else {
        if (initialBoard.getPrimaryPiece().getCol() != IO.getKCol()) {
          System.out.println("Unsolvable: Exit location and primary piece is not alligned");
          scanner.close();
          return;
        }
      }

      // Choose search algorithm
      System.out.println("\nChoose search algorithm:");
      System.out.println("1. Uniform Cost Search");
      System.out.println("2. Greedy Best First Search (Coming soon)");
      System.out.println("3. A* Search (Coming soon)");
      System.out.print("Enter choice (1-3): ");

      int choice = scanner.nextInt();
      State solution = null;
      long startTime = System.currentTimeMillis();

      switch (choice) {
        case 1:
          UniformCostSearch ucs = new UniformCostSearch();
          solution = ucs.solve(initialBoard);
          break;
        case 2:
          break;
        case 3:
          solution = AStar.solve(initialBoard);
          break;

        default:
          System.out.println("Invalid choice");
          return;
      }

      long endTime = System.currentTimeMillis();

      if (solution != null) {
        List<Move> path = solution.getPathFromRoot();
        System.out.println("\nSolution found!");
        System.out.println("Number of moves: " + path.size());
        System.out.println("Time taken: " + (endTime - startTime) + "ms");

        // Print each move
        System.out.println("\nMoves:");
        for (int i = 0; i < path.size(); i++) {
          Move move = path.get(i);
          System.out.printf("%d. Move piece %c %s by %d steps\n",
              i + 1,
              move.getPiece().getLetter(),
              move.getDirection(),
              move.getSteps());
        }

        // Print final state
        System.out.println("\nFinal Board State:");
        solution.printState();

        // Save to file option
        String[] output = new String[path.size() + 3];
        output[0] = "Number of moves: " + path.size();
        output[1] = "Time taken: " + (endTime - startTime) + "ms";
        output[2] = "\nMoves:";

        for (int i = 0; i < path.size(); i++) {
          Move move = path.get(i);
          output[i + 3] = String.format("%d. Move piece %c %s by %d steps",
              i + 1,
              move.getPiece().getLetter(),
              move.getDirection(),
              move.getSteps());
        }

        IO.saveOutputToFile(output, new ArrayList<>(initialBoard.getPieces().values()));
      } else {
        System.out.println("\nNo solution found!");
      }

      scanner.close();

    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
      e.printStackTrace();
    }
  }
}