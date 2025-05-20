import components.*;
import solver.*;
import java.util.*;

public class Main {
  public static void main(String[] args) {
    try {
      System.out.println("Enter input file path (from test/input folder): ");
      Scanner scanner = new Scanner(System.in);
      String inputFile = scanner.nextLine();
      String filepath = "test/input/" + inputFile;

      String[] inputString = IO.readFile(filepath);
      Board initialBoard = IO.parseInput(inputString);

      System.out.println("\nInitial Board State:");
      initialBoard.printBoard();
      System.out.printf("Primary Col: %d, KCol: %d\n", initialBoard.getPrimaryPiece().getCol(), IO.getKCol());

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

      System.out.println("\nChoose search algorithm:");
      System.out.println("1. Uniform Cost Search");
      System.out.println("2. Greedy Best First Search");
      System.out.println("3. A*");
      System.out.println("4. Iterative Deepening Depth First Search");
      System.out.print("Enter choice (1-3): ");

      int choice = scanner.nextInt();
      State solution = null;
      String algorithm = "";
      String heuristicType = "";
      String heuristic = "";
      long startTime = System.currentTimeMillis();

      switch (choice) {
        case 1:
          UniformCostSearch ucs = new UniformCostSearch();
          algorithm = "Uniform Cost Search";
          solution = ucs.solve(initialBoard);
          break;
        case 2:
          algorithm = "Greedy Best First Search";
          System.out.println("Choose heuristic:");
          System.out.println("1. Jarak Piece ke K");
          System.out.println("2. Jumlah Piece Penghalang");
          System.out.println("3. Gabungan Dua Heuristic");
          System.out.print("Enter heuristic (1-3): ");
          int hChoice = scanner.nextInt();
          scanner.nextLine();

          if (hChoice < 1 || hChoice > 3) {
            System.out.println("Invalid heuristic choice, using default.");
            heuristicType = "pieceToDest";
          }

          if (hChoice == 1) {
            heuristicType = "pieceToDest";
          } else if (hChoice == 2) {
            heuristicType = "countBlockingPieces";
          } else {
            heuristicType = "combineTwo";
          }

          GreedyBestFirstSearch gbfs = new GreedyBestFirstSearch();
          solution = gbfs.solve(initialBoard, heuristicType);
          break;
        case 3:
          algorithm = "A*";
          System.out.println("Choose heuristic:");
          System.out.println("1. Jarak Piece ke K");
          System.out.println("2. Jumlah Piece Penghalang");
          System.out.println("3. Gabungan Dua Heuristic");
          System.out.print("Enter heuristic (1-3): ");
          hChoice = scanner.nextInt();
          scanner.nextLine();

          if (hChoice < 1 || hChoice > 3) {
            System.out.println("Invalid heuristic choice, using default.");
            heuristicType = "pieceToDest";
          }

          if (hChoice == 1) {
            heuristicType = "pieceToDest";
          } else if (hChoice == 2) {
            heuristicType = "countBlockingPieces";
          } else {
            heuristicType = "combineTwo";
          }
          solution = AStar.solve(initialBoard, heuristicType);
          break;
        case 4:
          algorithm = "Iterative Deepening Depth First Search";
          IterativeDeepeningSearch idfs = new IterativeDeepeningSearch();
          solution = idfs.solve(initialBoard);
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
        System.out.println("Number of moves: " + path.size());
        System.out.println("Time taken: " + (endTime - startTime) + "ms");

        System.out.println("\nMoves:");
        for (int i = 0; i < path.size(); i++) {
          Move move = path.get(i);
          System.out.printf("%d. Move piece %c %s by %d steps\n",
              i + 1,
              move.getPiece().getLetter(),
              move.getDirection(),
              move.getSteps());
        }

        System.out.println("\nFinal Board State:");
        solution.printState();
        solution.setExecutionTime(endTime - startTime);
        solution.setAlgorithm(algorithm);
        solution.setHeuristicType(heuristicType);

        String[] output = solution.getSolutionPath();

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