import components.*;
import solver.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainGUI extends JFrame {
  private Board board;
  private JPanel boardPanel;
  private JLabel statusLabel;
  private JComboBox<String> algoBox;
  private JButton solveButton, loadButton;
  private File currentFile;
  private Timer animationTimer;
  private List<State> solutionStates;
  private int currentStateIndex;
  private JButton playButton;
  private JButton nextButton;
  private JButton prevButton;
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

    algoBox = new JComboBox<>(new String[] {
        "Uniform Cost Search", "Greedy Best First Search", "A*", "Iterative Deepening DFS"
    });
    topPanel.add(algoBox);

    solveButton = new JButton("Solve");
    solveButton.addActionListener(e -> solveBoard());
    solveButton.setEnabled(false);
    topPanel.add(solveButton);

    add(topPanel, BorderLayout.NORTH);

    boardPanel = new JPanel();
    add(boardPanel, BorderLayout.CENTER);

    statusLabel = new JLabel("Please load a board file.");
    add(statusLabel, BorderLayout.SOUTH);

    setSize(700, 700);
    setVisible(true);

    createAnimationControls();

    animationTimer = new Timer(ANIMATION_DELAY, e -> showNextState());
    animationTimer.setRepeats(true);
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
          this,                           // Parent component
          message,                        // Message text
          "Error Loading Board",          // Dialog title
          JOptionPane.ERROR_MESSAGE       // Message type
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
    boardPanel.setLayout(new GridLayout(rows, cols));
    char[][] grid = board.getGrid();

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        JPanel cell = new JPanel();
        cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        cell.setOpaque(true);

        char piece = grid[i][j];
        if (piece == '.') {
          cell.setBackground(Color.WHITE);
        } else if (piece == 'P') {
          cell.setBackground(Color.RED); // Primary piece
        } else if (piece == 'K') {
          cell.setBackground(Color.GREEN); // Exit
        } else {
          // Generate a consistent color for each piece letter
          cell.setBackground(new Color(
              (piece * 83) % 255,
              (piece * 157) % 255,
              (piece * 223) % 255));
        }

        JLabel label = new JLabel(String.valueOf(piece), SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        cell.add(label);

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
    solveButton.setEnabled(false);

    SwingWorker<components.State, Void> worker = new SwingWorker<components.State, Void>() {
      long time = 0;

      @Override
      protected components.State doInBackground() {
        components.State solution = null;
        String algo = (String) algoBox.getSelectedItem();
        System.out.println("Using algorithm: " + algo);
        long start = System.currentTimeMillis();
        try {
          switch (algo) {
            case "Uniform Cost Search":
              System.out.println("Starting UCS...");
              solution = new UniformCostSearch().solve(board.copy());
              break;
            case "Greedy Best First Search":
              System.out.println("Starting GBFS...");
              solution = new GreedyBestFirstSearch().solve(board.copy(), "pieceToDest");
              break;
            case "A*":
              System.out.println("Starting A*...");
              solution = AStar.solve(board.copy(), "pieceToDest");
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
            statusLabel.setText("Solution found! " + (solutionStates.size() - 1) + " moves");

            // Enable animation controls
            prevButton.setEnabled(true);
            playButton.setEnabled(true);
            nextButton.setEnabled(true);

            // Show initial state
            updateBoardDisplay();
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

  private void createAnimationControls() {
    JPanel controlPanel = new JPanel();

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

    add(controlPanel, BorderLayout.SOUTH);
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