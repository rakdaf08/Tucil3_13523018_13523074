import components.*;
import solver.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainGUI extends JFrame {
  private Board board;
  private JPanel boardPanel;
  private JLabel statusLabel;
  private JLabel timeLabel;
  private JLabel nodesVisitedLabel;
  private JComboBox<String> algoBox;
  private JComboBox<String> heuristicBox;
  private JButton solveButton, loadButton, saveButton;
  private File currentFile;
  private Timer animationTimer;
  private List<State> solutionStates;
  private int currentStateIndex;
  private JButton playButton;
  private JButton nextButton;
  private JButton prevButton;
  private JList<String> movesList;
  private DefaultListModel<String> movesListModel;
  private static final int ANIMATION_DELAY = 1;

  public static void main(String[] args) {
    SwingUtilities.invokeLater(MainGUI::new);
  }

  public MainGUI() {
    setTitle("Rush Hour Solver");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    JPanel topPanel = new JPanel();
    loadButton = new JButton("Load Board");
    loadButton.addActionListener(e -> loadBoard());
    topPanel.add(loadButton);

    saveButton = new JButton("Save Solution");

    saveButton.addActionListener(e -> saveSolutionToFile());
    saveButton.setEnabled(false);
    topPanel.add(saveButton);

    algoBox = new JComboBox<>(new String[] {
        "Uniform Cost Search", "Greedy Best First Search", "A*", "Iterative Deepening DFS"
    });
    topPanel.add(algoBox);

    heuristicBox = new JComboBox<>(new String[] {
        "Jarak Piece ke K", "Jumlah Piece Penghalang", "Gabungan Dua Heuristic"
    });
    heuristicBox.setEnabled(false);
    topPanel.add(heuristicBox);

    algoBox.addActionListener(e -> {
      String selectedAlgo = (String) algoBox.getSelectedItem();
      if (selectedAlgo.equals("Greedy Best First Search") || selectedAlgo.equals("A*")) {
        heuristicBox.setEnabled(true);
      } else {
        heuristicBox.setEnabled(false);
      }
    });

    solveButton = new JButton("Solve");
    solveButton.addActionListener(e -> solveBoard());
    solveButton.setEnabled(false);
    topPanel.add(solveButton);

    add(topPanel, BorderLayout.NORTH);
    boardPanel = new JPanel();
    add(boardPanel, BorderLayout.CENTER);
    JPanel centerPanel = new JPanel(new BorderLayout());

    JPanel boardWrapper = new JPanel(new GridBagLayout());
    boardWrapper.add(boardPanel);
    centerPanel.add(boardWrapper, BorderLayout.CENTER);

    movesListModel = new DefaultListModel<>();
    movesList = new JList<>(movesListModel);
    movesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    movesList.setCellRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value,
          int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
            list, value, index, isSelected, cellHasFocus);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return label;
      }
    });
    movesList.addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting() && movesList.getSelectedIndex() != -1) {
        currentStateIndex = movesList.getSelectedIndex();
        updateBoardDisplay();
        movesList.ensureIndexIsVisible(currentStateIndex);
      }
    });

    JScrollPane scrollPane = new JScrollPane(movesList);
    scrollPane.setPreferredSize(new Dimension(250, 0));
    centerPanel.add(scrollPane, BorderLayout.EAST);

    add(centerPanel, BorderLayout.CENTER);
    JPanel bottomPanel = new JPanel(new BorderLayout());
    JPanel statusPanel = new JPanel(new BorderLayout());

    JPanel labelsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

    statusLabel = new JLabel("Please load a board file.");
    timeLabel = new JLabel("Time: -");
    nodesVisitedLabel = new JLabel("Nodes visited: -");

    statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
    timeLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
    nodesVisitedLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

    labelsPanel.add(statusLabel);
    labelsPanel.add(timeLabel);
    labelsPanel.add(nodesVisitedLabel);

    statusPanel.add(labelsPanel, BorderLayout.CENTER);

    bottomPanel.add(statusPanel, BorderLayout.NORTH);

    createAnimationControls(bottomPanel);

    add(bottomPanel, BorderLayout.SOUTH);

    setSize(700, 700);
    setVisible(true);

    animationTimer = new Timer(ANIMATION_DELAY, e -> showNextState());
    animationTimer.setRepeats(true);
  }

  private void saveSolutionToFile() {
    JFileChooser fileChooser = new JFileChooser("test/output");
    fileChooser.setDialogTitle("Save Solution File");
    int userSelection = fileChooser.showSaveDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
      File fileToSave = fileChooser.getSelectedFile();

      try {
        State finalState = solutionStates.get(solutionStates.size() - 1);
        if (finalState != null) {
          String[] solutionSteps = finalState.getSolutionPath();
          Files.write(fileToSave.toPath(), Arrays.asList(solutionSteps));
          JOptionPane.showMessageDialog(this, "Solution saved to " + fileToSave.getAbsolutePath());
        } else {
          JOptionPane.showMessageDialog(this, "No solution available. Please solve the board first.");
        }
      } catch (IOException ex) {
        JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
        ex.printStackTrace();
      }
    }
  }

  private void loadBoard() {
    JFileChooser chooser = new JFileChooser("test/input");
    int res = chooser.showOpenDialog(this);
    if (res == JFileChooser.APPROVE_OPTION) {
      currentFile = chooser.getSelectedFile();
      try {
        String[] input = components.IO.readFile(currentFile.getPath());
        board = components.IO.parseInput(input);
        drawBoard();
        statusLabel.setText("Board loaded: " + currentFile.getName());
        solveButton.setEnabled(true);
      } catch (Exception ex) {
        showErrorDialog(ex.getMessage());
      }
    }
  }

  private void showErrorDialog(String message) {
    JOptionPane.showMessageDialog(
        this,
        message,
        "Error Loading Board",
        JOptionPane.ERROR_MESSAGE);
  }

  private void drawBoard() {
    boardPanel.removeAll();
    if (board == null) {
      boardPanel.revalidate();
      boardPanel.repaint();
      return;
    }

    int rows = board.getRows();
    int cols = board.getCols();
    boardPanel.setLayout(new GridLayout(rows + 2, cols + 2));

    int size = Math.min(getHeight() - 150, getWidth() - 300);
    size = Math.min(size, Math.min(600, Math.max(300, size)));
    boardPanel.setPreferredSize(new Dimension(size, size));

    char[][] grid = board.getGrid();
    int kRow = components.IO.getKRow();
    int kCol = components.IO.getKCol();

    for (int i = -1; i <= rows; i++) {
      for (int j = -1; j <= cols; j++) {
        JPanel cell = new JPanel();
        cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        cell.setOpaque(true);

        if (i == -1 || i == rows || j == -1 || j == cols) {
          cell.setBackground(Color.LIGHT_GRAY);

          if ((i == kRow && j == kCol) ||
              (kRow == -1 && i == -1 && j == kCol) ||
              (kRow == rows && i == rows && j == kCol) ||
              (kCol == -1 && i == kRow && j == -1) ||
              (kCol == cols && i == kRow && j == cols)) {
            cell.setBackground(Color.GREEN);
            JLabel label = new JLabel("K", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 20));
            cell.add(label);
          }
        } else {
          char piece = grid[i][j];
          if (piece == '.') {
            cell.setBackground(Color.WHITE);
          } else if (piece == 'P') {
            cell.setBackground(Color.RED);
          } else {
            cell.setBackground(new Color(
                (piece * 83) % 255,
                (piece * 157) % 255,
                (piece * 223) % 255));
          }

          if (piece != '.') {
            JLabel label = new JLabel(String.valueOf(piece), SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 20));
            cell.add(label);
          }
        }

        boardPanel.add(cell);
      }
    }

    boardPanel.revalidate();
    boardPanel.repaint();
  }

  private void solveBoard() {
    if (board == null) {
      System.out.println("Board is null!");
      statusLabel.setText("No board loaded.");
      return;
    }

    if (animationTimer.isRunning()) {
      animationTimer.stop();
    }
    playButton.setText("▶");
    prevButton.setEnabled(false);
    playButton.setEnabled(false);
    nextButton.setEnabled(false);

    solutionStates = null;
    currentStateIndex = 0;
    movesListModel.clear();

    try {
      String[] input = components.IO.readFile(currentFile.getPath());
      board = components.IO.parseInput(input);
      drawBoard();
    } catch (Exception ex) {
      statusLabel.setText("Failed to reset board: " + ex.getMessage());
      return;
    }
    System.out.println("Starting solve process...");
    statusLabel.setText("Solving...");
    timeLabel.setText("Time: -");
    nodesVisitedLabel.setText("Nodes visited: -");
    solveButton.setEnabled(false);

    SwingWorker<components.State, Void> worker = new SwingWorker<components.State, Void>() {
      long time = 0;

      @Override
      protected components.State doInBackground() {
        components.State solution = null;
        String algo = (String) algoBox.getSelectedItem();
        System.out.println("Using algorithm: " + algo);
        long start = System.currentTimeMillis();
        String heuristic = "";
        String heuristicType = "";

        if (algo.equals("Greedy Best First Search") || algo.equals("A*")) {
          String selectedHeuristic = heuristicBox.getSelectedItem().toString();
          if (selectedHeuristic.equals("Jarak Piece ke K")) {
            heuristic = "pieceToDest";
            heuristicType = "Piece to Destination";
          } else if (selectedHeuristic.equals("Jumlah Piece Penghalang")) {
            heuristic = "countBlockingPieces";
            heuristicType = "Count Blocking Pieces";
          } else {
            heuristic = "combineTwo";
            heuristicType = "Combination of Count Blocking Pieces and Piece to Destination";
          }
        }

        try {
          switch (algo) {
            case "Uniform Cost Search":
              System.out.println("Starting UCS...");
              solution = new UniformCostSearch().solve(board.copy());
              break;
            case "Greedy Best First Search":
              System.out.println("Starting GBFS...");
              solution = new GreedyBestFirstSearch().solve(board.copy(), heuristic);
              break;
            case "A*":
              System.out.println("Starting A*...");
              solution = AStar.solve(board.copy(), heuristic);
              break;
            case "Iterative Deepening DFS":
              System.out.println("Starting IDDFS...");
              solution = new IterativeDeepeningSearch().solve(board.copy());
              break;
          }
        } catch (Exception e) {
          System.out.println("Error during solving: " + e.getMessage());
          e.printStackTrace();
        }
        time = System.currentTimeMillis() - start;
        solution.setExecutionTime(time);
        solution.setAlgorithm(algo);
        solution.setHeuristicType(heuristicType);
        System.out.println("Solve completed in " + time + "ms");
        return solution;
      }

      @Override
      protected void done() {
        try {
          components.State solution = get();
          if (solution != null) {
            System.out.println("Solution found!");
            solutionStates = new ArrayList<>();
            components.State current = solution;
            while (current != null) {
              solutionStates.add(0, current);
              current = current.getParent();
            }
            currentStateIndex = 0;

            statusLabel.setText("Solution found! " + (solutionStates.size() - 1) + " moves");
            timeLabel.setText(String.format("Time: %d ms", time));
            nodesVisitedLabel.setText("Nodes visited: " + solution.getTotalNodeVisited());

            movesListModel.clear();
            movesListModel.addElement("Step 0: Initial State");
            List<Move> moves = solution.getPathFromRoot();
            for (int i = 0; i < moves.size(); i++) {
              Move move = moves.get(i);
              String direction = move.getDirection();
              char piece = move.getPiece().getLetter();
              int steps = move.getSteps();
              movesListModel.addElement(String.format("Step %d: Move %c %s by %d",
                  i + 1, piece, direction, steps));
            }
            movesList.setSelectedIndex(0);

            prevButton.setEnabled(true);
            playButton.setEnabled(true);
            nextButton.setEnabled(true);

            updateBoardDisplay();
            saveButton.setEnabled(true);

            animationTimer.start();
            playButton.setText("⏸");
          } else {
            System.out.println("No solution found!");
            statusLabel.setText("No solution found.");
          }
        } catch (Exception e) {
          System.out.println("Error in done(): " + e.getMessage());
          e.printStackTrace();
          statusLabel.setText("Error during solving: " + e.getMessage());
        }
        solveButton.setEnabled(true);
      }
    };
    worker.execute();
  }

  private void createAnimationControls(JPanel bottomPanel) {
    JPanel controlPanel = new JPanel();
    controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

    prevButton = new JButton("←");
    prevButton.setEnabled(false);
    prevButton.addActionListener(e -> showPreviousState());

    playButton = new JButton("▶");
    playButton.setEnabled(false);
    playButton.addActionListener(e -> toggleAnimation());

    nextButton = new JButton("→");
    nextButton.setEnabled(false);
    nextButton.addActionListener(e -> showNextState());

    controlPanel.add(prevButton);
    controlPanel.add(playButton);
    controlPanel.add(nextButton);

    bottomPanel.add(controlPanel, BorderLayout.CENTER);
  }

  private void toggleAnimation() {
    if (animationTimer.isRunning()) {
      animationTimer.stop();
      playButton.setText("▶");
    } else {
      animationTimer.start();
      playButton.setText("⏸");
    }
  }

  private void showNextState() {
    if (solutionStates != null && currentStateIndex < solutionStates.size() - 1) {
      currentStateIndex++;
      updateBoardDisplay();

      char[][] grid = board.getGrid();
      if (currentStateIndex == solutionStates.size() - 1) {
        int kRow = components.IO.getKRow();
        int kCol = components.IO.getKCol();
        int rows = board.getRows();
        int cols = board.getCols();

        int pRow = -1, pCol = -1, pSize = 0;
        boolean isHorizontal = false;

        for (int i = 0; i < rows; i++) {
          for (int j = 0; j < cols; j++) {
            if (grid[i][j] == 'P') {
              if (pRow == -1) {
                pRow = i;
                pCol = j;
                if (j + 1 < cols && grid[i][j + 1] == 'P') {
                  isHorizontal = true;
                }
              }
              pSize++;
            }
          }
        }

        boolean atExit = false;
        if (kRow == -1 && kCol >= 0 && kCol < cols && grid[0][kCol] == 'P' && !isHorizontal)
          atExit = true;
        else if (kRow == rows && kCol >= 0 && kCol < cols && grid[rows - 1][kCol] == 'P' && !isHorizontal)
          atExit = true;
        else if (kCol == -1 && kRow >= 0 && kRow < rows && grid[kRow][0] == 'P' && isHorizontal)
          atExit = true;
        else if (kCol == cols && kRow >= 0 && kRow < rows && grid[kRow][cols - 1] == 'P' && isHorizontal)
          atExit = true;

        if (atExit) {
          animationTimer.stop();

          Timer exitTimer = new Timer(ANIMATION_DELAY, e -> {
            for (int i = 0; i < grid.length; i++) {
              for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 'P') {
                  grid[i][j] = '.';
                }
              }
            }
            drawBoard();
            statusLabel.setText("Puzzle solved! The red piece has escaped!");
            playButton.setText("▶");
            ((Timer) e.getSource()).stop();
          });
          exitTimer.setRepeats(false);
          exitTimer.start();
        }
      }
    } else {
      animationTimer.stop();
      playButton.setText("▶");
    }
  }

  private void showPreviousState() {
    if (solutionStates != null && currentStateIndex > 0) {
      currentStateIndex--;
      updateBoardDisplay();
    }
  }

  private void updateBoardDisplay() {
    if (solutionStates != null && currentStateIndex >= 0 && currentStateIndex < solutionStates.size()) {
      State currentState = solutionStates.get(currentStateIndex);
      board = currentState.getBoard();
      drawBoard();
      statusLabel.setText("Move " + currentStateIndex + " of " + (solutionStates.size() - 1));
    }
  }
}
