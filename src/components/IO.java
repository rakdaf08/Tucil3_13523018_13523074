package components;

import java.io.*;
import java.util.*;

public class IO {
    private static int kRow;
    private static int kCol;
    private static int gridRow;
    private static int gridCol;
    private static int a;
    private static int b;
    private static int n;


    public static String[] readFile(String filepath) throws Exception {
        File file = new File(filepath);
        Scanner scanner = new Scanner(file);
        String size = "";
        String N = "";
        List<String> boardRows = new ArrayList<>();

        try {
            if (scanner.hasNextLine()) {
                size = scanner.nextLine();
                if (scanner.hasNextLine()) {
                    N = scanner.nextLine();
                    if(Integer.parseInt(N) >= 25){
                        scanner.close();
                        throw new Exception("Number of piece cannot exceed 25");
                    }
                } else {
                    scanner.close();
                    throw new Exception("No N value found.");
                }
            } else {
                scanner.close();
                throw new Exception("Line is empty.");
            }

            while (scanner.hasNextLine()) {
                String row = scanner.nextLine();
                if (!row.isEmpty()) {
                    boardRows.add(row);
                }
            }
        } catch (Exception e) {
            throw e;
        }

        scanner.close();
        String[] result = new String[boardRows.size() + 2];
        result[0] = size;
        result[1] = N;

        // Copy the board rows to the result array
        for (int i = 0; i < boardRows.size(); i++) {
            result[i + 2] = boardRows.get(i);
        }

        return result;
    }
    
    private static int countOccurrences(String str, char ch) {
    int count = 0;
    for (int i = 0; i < str.length(); i++) {
        if (str.charAt(i) == ch) {
            count++;
        }
    }
    return count;
}
    
    public static Board parseInput(String[] inputString) throws Exception {
        char[][] grid = null;
        char[][] innerGrid = null;
        try {
            String[] sizeStr = inputString[0].split(" ");
            String[] boardRows = new String[inputString.length - 2];
            for (int i = 2; i < inputString.length; i++) {
                boardRows[i - 2] = inputString[i];
            }
            a = Integer.parseInt(sizeStr[0]);
            b = Integer.parseInt(sizeStr[1]);
            gridRow = a - 1;
            gridCol = b - 1;
            if (a < 0 || b < 0)
                throw new IllegalArgumentException("Grid dimensions must be non-negative.");

            n = Integer.parseInt(inputString[1]);
            System.out.println("A: " + a + " B: " + b + " N: " + n);

            int borderRow = a + 2;
            int borderCol = b + 2;
            grid = new char[borderCol][borderRow];
            for (String string : boardRows) {
                System.out.println(string);
            }
            boolean kFound = false;
            int kRowPosition = -1; // -1 for top, a for bottom
            int kColPosition = -1;
            
            int totalKCount = 0;
            for (String row : boardRows) {
                totalKCount += countOccurrences(row, 'K');
            }
            
            if (totalKCount > 1) {
                throw new IllegalArgumentException("Multiple 'K' characters found in the grid.");
            }
            
            /* Check if K is on Top */
            if(boardRows[0].contains("K")){
                if(kFound) throw new IllegalArgumentException("Multiple 'K' characters found in the grid.");
                
                kFound = true;
                int leadingSpaces = boardRows[0].indexOf('K');
                System.out.println("Board Rows: " + Arrays.toString(boardRows));
                if (countOccurrences(boardRows[0], 'K') > 1) {
                throw new IllegalArgumentException("The exit 'K' should be a single character, not a multi-character piece.");
                }

                kRowPosition = -1;
                kColPosition = leadingSpaces;

                kRow = kRowPosition;
                kCol = kColPosition;
                // Remove this row from our array
                List<String> rowsList = new ArrayList<>(Arrays.asList(boardRows));
                rowsList.remove(0);
                boardRows = rowsList.toArray(new String[0]);
            }

            /* Check if K is on Bottom*/
            if(boardRows[boardRows.length-1].contains("K")){
                if(kFound) throw new IllegalArgumentException("Multiple 'K' characters found in the grid.");
                
                kFound = true;
                int leadingSpaces = boardRows[boardRows.length-1].indexOf('K');
                System.out.println("Board Rows: " + Arrays.toString(boardRows));
                if (countOccurrences(boardRows[boardRows.length-1], 'K') > 1) {
                throw new IllegalArgumentException("The exit 'K' should be a single character, not a multi-character piece.");
                }
                
                kRowPosition = a;
                kColPosition = leadingSpaces;
                
                kRow = a;
                kCol = kColPosition;

                // Remove this row from our array
                List<String> rowsList = new ArrayList<>(Arrays.asList(boardRows));
                rowsList.remove(boardRows.length-1);
                boardRows = rowsList.toArray(new String[0]);
            }
            
            if (boardRows.length != a) {
                throw new IllegalArgumentException("Number of rows (" + boardRows.length
                        + ") does not match the specified dimension (" + a + ").");
            }

            for (int i = 0; i < borderRow; i++) {
                for (int j = 0; j < borderCol; j++) {
                    if (i == 0 || i == borderRow - 1) {
                        grid[i][j] = '-';
                    } else if (j == 0 || j == borderCol - 1) {
                        grid[i][j] = '|';
                    } else {
                        grid[i][j] = '.';
                    }
                }
            }

            
            for (int i = 0; i < boardRows.length; i++) {
                int kIndex = boardRows[i].indexOf('K');
                if (kIndex != -1) {
                    if(kFound) throw new IllegalArgumentException("Multiple 'K' characters found in the grid.");
                    if (kIndex + 1 < boardRows[i].length() && boardRows[i].charAt(kIndex + 1) == 'K') {
                        throw new IllegalArgumentException("The exit 'K' should be a single character, not a multi-character piece.");
                        }
                    
                    // Check if this is part of a vertical multi-character piece
                    if (i > 0 && kIndex < boardRows[i-1].length() && boardRows[i-1].charAt(kIndex) == 'K') {
                        throw new IllegalArgumentException("The exit 'K' should be a single character, not a multi-character piece.");
                    }
                    if (i < boardRows.length - 1 && kIndex < boardRows[i+1].length() && boardRows[i+1].charAt(kIndex) == 'K') {
                        throw new IllegalArgumentException("The exit 'K' should be a single character, not a multi-character piece.");
                    }
                    kRowPosition = i;
                    kColPosition = kIndex;
                    // Remove the K from the string
                    kRow = kRowPosition;
                    kCol = kColPosition;
                    boardRows[i] = boardRows[i].substring(0, kIndex) + boardRows[i].substring(kIndex + 1);
                    kFound = true;
                    break;
                }
            }

            if (!kFound) {
                throw new IllegalArgumentException("No exit 'K' found in the grid.");
            }

            // Fill the inner grid
            for (int i = 0; i < a; i++) {
                for (int j = 0; j < b; j++) {
                    if (i < boardRows.length && j < boardRows[i].length()) {
                        grid[i + 1][j + 1] = boardRows[i].charAt(j);
                    } else {
                        grid[i + 1][j + 1] = '.'; // Default to empty space
                    }
                }
            }

            // Place K on the correct border
            if (kRowPosition == -1) {
                // K is on top border
                if (kColPosition >= 0 && kColPosition < b) {
                    grid[0][kColPosition + 1] = 'K';
                    System.out.println("Placed K on top border at column " + (kColPosition + 1));
                } else {
                    throw new IllegalArgumentException(
                            "K position on top border is outside valid range: " + kColPosition);
                }
            } else if (kRowPosition == a) {
                // K is on bottom border
                if (kColPosition >= 0 && kColPosition < b) {
                    grid[borderRow - 1][kColPosition + 1] = 'K'; // <-- perbaiki di sini
                    System.out.println("Placed K on bottom border at column " + (kColPosition + 1));
                } else {
                    throw new IllegalArgumentException(
                            "K position on bottom border is outside valid range: " + kColPosition);
                }
            } else if (kColPosition == 0) {
                // K is on left border
                grid[kRowPosition + 1][0] = 'K';
                System.out.println("Placed K on left border at row " + (kRowPosition + 1));
            } else if (kColPosition == b - 1
                    || (kRowPosition < boardRows.length && kColPosition >= boardRows[kRowPosition].length())) {
                // K is on right border (either at position b-1 or at the end of a shorter row)
                grid[kRowPosition + 1][borderCol - 1] = 'K';
                System.out.println("Placed K on right border at row " + (kRowPosition + 1));
            } else {
                // If K is not at the edge of the inner grid, we can't place it on a border
                throw new IllegalArgumentException(
                        "Exit 'K' must be at the edge of the grid: row=" + kRowPosition + ", col=" + kColPosition);
            }

            innerGrid = new char[a][b];
            for (int i = 0; i < a; i++) {
                for (int j = 0; j < b; j++) {
                    innerGrid[i][j] = grid[i + 1][j + 1];
                }
            }

        } catch (Exception e) {
            throw new IOException("Failed to parse input: " + e.getMessage());
        }
        System.out.printf("KCOL: %d, KROW: %d\n", IO.getKCol(), IO.getKRow());

        return new Board(innerGrid);
    }

    public static void printGrid(char[][] grid) {
        for (char[] row : grid) {
            System.out.println(Arrays.toString(row));
        }
    }

    public static boolean isFileExists(String path) throws IOException {
        File file = new File(path);
        return file.exists();
    }

    public static void writeOutputToFile(String[] output, String outputPath) throws IOException {
        FileWriter fileWriter = new FileWriter(outputPath);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        for (int i = 0; i < output.length; i++) {
            printWriter.print(output[i]);
            printWriter.println();
        }

        printWriter.close();
    }

    // Write Array of String to a file
    public static void saveOutputToFile(String[] output, List<Piece> pieces) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String directoryPath = "test\\output\\";
        String outputPath = "";
        outputPath = outputPath.concat(directoryPath);

        while (true) {
            System.out.println("\nSave Hasil ke file?(Y/n)");
            char save = scanner.nextLine().charAt(0);
            if (save == 'Y' || save == 'y') {
                System.out.println("Masukkan nama file e.g (output): ");
                String filename = scanner.nextLine();
                outputPath = outputPath.concat(filename);

                boolean isFileExists = isFileExists(outputPath);
                if (isFileExists) {
                    System.out.println("Terdapat file dengan nama yang sama apakah anda ingin overwrite? (y/N)");
                    char overwrite = scanner.next().charAt(0);
                    if (overwrite == 'Y' || overwrite == 'y') {
                        writeOutputToFile(output, outputPath);
                        System.out.println("File berhasil di simpan pada " + outputPath);
                        break;
                    } else if (overwrite == 'n' || overwrite == 'N') {
                        System.out.println("File tidak disimpan\n");
                        break;
                    } else {
                        System.out.println("Masukkan 'Y' untuk ya atau 'n' untuk tidak\n");
                        break;
                    }
                } else {
                    writeOutputToFile(output, outputPath);
                    System.out.println("File berhasil di simpan pada " + outputPath);
                }

                break;
            }

            else if (save == 'n' || save == 'N') {
                System.out.println("File tidak disimpan.\n");
                break; // Exit the loop without saving
            } else {
                System.out.println("Masukkan 'Y' untuk ya atau 'n' untuk tidak\n");
                break;
            }
        }
        scanner.close();
    }

    public static int getKRow() {
        return kRow;
    }

    public static int getKCol() {
        return kCol;
    }

    public static int getGridRow() {
        return gridRow;
    }

    public static int getGridCol() {
        return gridCol;
    }

    public static int getkRow() {
        return kRow;
    }

    public static void setkRow(int kRow) {
        IO.kRow = kRow;
    }

    public static int getkCol() {
        return kCol;
    }

    public static void setkCol(int kCol) {
        IO.kCol = kCol;
    }

    public static void setGridRow(int gridRow) {
        IO.gridRow = gridRow;
    }

    public static void setGridCol(int gridCol) {
        IO.gridCol = gridCol;
    }

    public static int getA() {
        return a;
    }

    public static void setA(int a) {
        IO.a = a;
    }

    public static int getB() {
        return b;
    }

    public static void setB(int b) {
        IO.b = b;
    }

    public static int getN() {
        return n;
    }

    public static void setN(int n) {
        IO.n = n;
    }
}
