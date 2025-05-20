import components.*;
import solver.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
  private static final int ANIMATION_DELAY = 500; // 1 second between states

  public static void main(String[] args) {
    SwingUtilities.invokeLater(MainGUI::new);
  }

  public MainGUI() {
    setTitle("Rush Hour Solver");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
    // Top panel for controls
    JPanel topPanel = new JPanel();
    loadButton = new JButton("Load Board");
    loadButton.addActionListener(e -> loadBoard());
    topPanel.add(loadButton);

    // Save Button
    saveButton = new JButton("Save Solution");

    saveButton.addActionListener(e -> saveSolutionToFile());
    saveButton.setEnabled(false);
    topPanel.add(saveButton);

    // Algorithm Selection
    algoBox = new JComboBox<>(new String[] {
        "Uniform Cost Search", "Greedy Best First Search", "A*", "Iterative Deepening DFS"
    });
    topPanel.add(algoBox);

    // Heuristic Selection
    heuristicBox = new JComboBox<>(new String[] {
        "Jarak Piece ke K", "Jumlah Piece Penghalang", "Gabungan Dua Heuristic"
    });
    heuristicBox.setEnabled(false); // Disabled by default
    topPanel.add(heuristicBox);

    // Enable heuristicBox only for GBFS and A*
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
    // Create center panel to hold board and moves list
    JPanel centerPanel = new JPanel(new BorderLayout());

    // Create a wrapper panel for the board to maintain square ratio
    JPanel boardWrapper = new JPanel(new GridBagLayout());
    boardWrapper.add(boardPanel);
    centerPanel.add(boardWrapper, BorderLayout.CENTER);

    // Create moves list sidebar with custom rendering
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

    // Add scrolling to the moves list
    JScrollPane scrollPane = new JScrollPane(movesList);
    scrollPane.setPreferredSize(new Dimension(250, 0));
    centerPanel.add(scrollPane, BorderLayout.EAST);

    add(centerPanel, BorderLayout.CENTER);
    // Create a bottom panel to hold both controls and status
    JPanel bottomPanel = new JPanel(new BorderLayout());
    // Create status panel
    JPanel statusPanel = new JPanel(new BorderLayout());

    // Create labels panel for multiple status items
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

    // Add status panel to the top of bottom panel
    bottomPanel.add(statusPanel, BorderLayout.NORTH);

    // Create and add animation controls to bottom panel
    createAnimationControls(bottomPanel);

    // Add the combined bottom panel to the frame
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
        // Assuming `finalState` is your solved board's final state
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
        this, // Parent component
        message, // Message text
        "Error Loading Board", // Dialog title
        JOptionPane.ERROR_MESSAGE // Message type
    );
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
    boardPanel.setLayout(new GridLayout(rows + 2, cols + 2)); // Add space for border cells

    // Calculate the size to maintain square cells
    int size = Math.min(getHeight() - 150, getWidth() - 300);
    size = Math.min(size, Math.min(600, Math.max(300, size)));
    boardPanel.setPreferredSize(new Dimension(size, size));

    char[][] grid = board.getGrid();
    int kRow = components.IO.getKRow();
    int kCol = components.IO.getKCol();

    // Create the board with border cells
    for (int i = -1; i <= rows; i++) {
      for (int j = -1; j <= cols; j++) {
        JPanel cell = new JPanel();
        cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        cell.setOpaque(true);

        // Border cells
        if (i == -1 || i == rows || j == -1 || j == cols) {
          cell.setBackground(Color.LIGHT_GRAY);

          // Add exit marker 'K'
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
        }
        // Main board cells
        else {
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

    // Reset animation state
    if (animationTimer.isRunning()) {
      animationTimer.stop();
    }
    playButton.setText("▶");
    prevButton.setEnabled(false);
    playButton.setEnabled(false);
    nextButton.setEnabled(false);
    // Reset solution states
    solutionStates = null;
    currentStateIndex = 0;
    movesListModel.clear();

    // Reset board to initial state
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
        String heuristic = heuristicBox.getSelectedItem().toString();
        if (heuristic == "Jarak Piece ke K") {
          heuristic = "pieceToDest";
        } else if (heuristic == "Jumlah Piece Penghalang") {
          heuristic = "countBlockingPieces";
        } else {
          heuristic = "combineTwo";
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
        System.out.println("Solve completed in " + time + "ms");
        return solution;
      }

      @Override
      protected void done() {
        try {
          components.State solution = get();
          if (solution != null) {
            System.out.println("Solution found!");
            // Generate all states from root to solution
            solutionStates = new ArrayList<>();
            components.State current = solution;
            while (current != null) {
              solutionStates.add(0, current);
              current = current.getParent();
            }
            currentStateIndex = 0;

            // Update all status labels
            statusLabel.setText("Solution found! " + (solutionStates.size() - 1) + " moves");
            timeLabel.setText(String.format("Time: %d ms", time));
            nodesVisitedLabel.setText("Nodes visited: " + solution.getTotalNodeVisited());
            // Populate moves list
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

            // Enable animation controls
            prevButton.setEnabled(true);
            playButton.setEnabled(true);
            nextButton.setEnabled(true);
            // Show initial state
            updateBoardDisplay();
            saveButton.setEnabled(true);

            // Automatically start the animation
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
    controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // Add some vertical padding

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

  // Add these methods to handle animation
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

      // Check if P is at exit position in final state
      char[][] grid = board.getGrid();
      if (currentStateIndex == solutionStates.size() - 1) {
        int kRow = components.IO.getKRow();
        int kCol = components.IO.getKCol();
        int rows = board.getRows();
        int cols = board.getCols();

        boolean atExit = false;
        if (kRow == -1 && kCol >= 0 && kCol < cols && grid[0][kCol] == 'P')
          atExit = true;
        else if (kRow == rows && kCol >= 0 && kCol < cols && grid[rows - 1][kCol] == 'P')
          atExit = true;
        else if (kCol == -1 && kRow >= 0 && kRow < rows && grid[kRow][0] == 'P')
          atExit = true;
        else if (kCol == cols && kRow >= 0 && kRow < rows && grid[kRow][cols - 1] == 'P')
          atExit = true;

        if (atExit) {
          animationTimer.stop();

          // Add a delay before removing P
          Timer exitTimer = new Timer(ANIMATION_DELAY, e -> {
            // Remove P piece from board
            for (int i = 0; i < grid.length; i++) {
              for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 'P') {
                  grid[i][j] = '.';
                  drawBoard();
                  statusLabel.setText("Puzzle solved! The red piece has escaped!");
                  playButton.setText("▶");
                  break;
                }
              }
            }
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
