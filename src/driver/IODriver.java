package driver;

import components.IO;
import components.Board;

public class IODriver {
    public static void main(String[] args) {
        String[] inputString;
        String inputPath = "test/1.txt";
        String outputPath = "test/2.txt";
        int a, b, n;
        char[][] grid;
        try {
            IO.isFileExists(inputPath);
            inputString = IO.readFile(inputPath);
            Board board = IO.parseInput(inputString);
            board.printBoard();

            // String[] sizeStr = inputString[0].split(" ");
            // a = Integer.parseInt(sizeStr[0]);
            // b = Integer.parseInt(sizeStr[1]);
            // grid = new char[a][b];
            // n = Integer.parseInt(inputString[1]);
            // System.out.println("A: " + a + " B: " + b + " N: " + n);

            // String gridString = inputString[2];
            // for(int i= 0; i < a ; i++){
            // for(int j = 0; j < b; j++){
            // int index = i*a + j;
            // grid[i][j] = gridString.charAt(index);
            // }
            // }

            // for(int i= 0; i < a ; i++){
            // for(int j = 0; j < b; j++){
            // System.out.print(grid[i][j]);
            // }
            // System.out.println();
            // }

            // HashMap<Character, Piece> pieces = IO.gridToPieces(grid);
            // System.out.println(pieces.toString());

            /* Test Save File */
            // IO.writeOutputToFile(inputString, outputPath);

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
