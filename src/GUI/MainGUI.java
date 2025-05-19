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

public class MainGUI extends JFrame {  private Board board;
  private JPanel boardPanel;
  private JLabel statusLabel;
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
    setLayout(new BorderLayout());    // Top panel for controls
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


    add(topPanel, BorderLayout.NORTH);    boardPanel = new JPanel();
    add(boardPanel, BorderLayout.CENTER);    // Create center panel to hold board and moves list
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

    statusLabel = new JLabel("Please load a board file.");
    add(statusLabel, BorderLayout.SOUTH);

    setSize(700, 700);
    setVisible(true);

    createAnimationControls();

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
            State finalState = solutionStates.get(solutionStates.size()-1);
            if ( finalState != null) {
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
    
    // Calculate the size to maintain square cells
    int size = Math.min(getHeight() - 150, getWidth() - 300); // Account for controls and moves list
    size = Math.min(size, Math.min(600, Math.max(300, size))); // Set min/max bounds
    boardPanel.setPreferredSize(new Dimension(size, size));
    
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
    nextButton.setEnabled(false);    // Reset solution states
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
        if(heuristic == "Jarak Piece ke K"){
          heuristic = "pieceToDest";
        }else if(heuristic == "Jumlah Piece Penghalang"){
          heuristic = "countBlockingPieces";
        }else{
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
            }            currentStateIndex = 0;
            statusLabel.setText("Solution found! " + (solutionStates.size() - 1) + " moves");            // Populate moves list
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

            // Enable save Button

            // Enable animation controls
            prevButton.setEnabled(true);
            playButton.setEnabled(true);
            nextButton.setEnabled(true);

            // Show initial state
            updateBoardDisplay();
            saveButton.setEnabled(true);
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