# 🚗 Rush Hour Game Solver

<p align="center">
  <img src="https://img.lakeshorelearning.com/is/image/OCProduction/fg503?wid=800&fmt=jpeg&qlt=85,1&pscan=auto&op_sharpen=0&resMode=sharp2&op_usm=1,0.65,6,0" alt="Rush Hour Board Game" width="600"/>
</p>

This program was created to fulfill **Tugas Kecil 3 IF2211 Strategi Algoritma**. It implements four classic route finding algorithms to solve the Rush Hour puzzle and find the quickest path for the red car to reach the exit.

---

## 🧠 Implemented Algorithms

- **Uniform Cost Search (UCS)**
- **Greedy Best First Search (GBFS)**
- **A\* Search**
- **Iterative Deepening Depth First Search (IDDFS)**

Each algorithm explores the puzzle using a different strategy and can be used to compare performance and path optimality.

---

## 📂 Folder Structure
```
Tucil3_13523018_13523074
├── src/ # Source code
│   ├── components/ # Board Piece Component
│   │   ├── Board.java
│   │   ├── IO.java
│   │   ├── Move.java
│   │   ├── Piece.java
│   │   └── State.java    
│   ├── solver/ # Solver Algoritm
│   │   ├── AStar.java
│   │   ├── GreedyBestFirstSearch.java
│   │   ├── Heuristic.java
│   │   ├── IterativeDeepeningSearch.java
│   │   └── UniformCostSearch.java   
│   ├── GUI/
│   └── Main.java # Terminal program
├── test/ # Test cases
│   ├── input/
│   └── output/ 
├── bin/ # Output binaries 
├── doc/ # Document 
└── README.md
```
---
 
## 🛠️ How to Compile
Make sure that you're in the root directory path
```
javac -encoding UTF-8 -d bin src/components/*.java src/GUI/*.java src/solver/*.java src/Main.java
```

## 🏃‍♂️ How to Run
```
java -cp bin MainGUI
```

---

## 🕹️ How to Use
1. Prepare a puzzle input in the required format (see test/ folder for examples).
2. Choose the algorithm you'd like to use.
3. Run the program and provide the input file path.
4. The program will display the solution steps and performance metrics.

## 📈 Output Example
- Total steps to solution
- Time taken
- Nodes visited
- Path to solution (step-by-step)

---

## 👨‍💻 Authors

<p align="center">
  <img src="https://drive.google.com/uc?export=view&id=1GWsUQ1jMcXiUtt48Cgjc_dUwHo2z1_B7" alt="Author Image" width="600"/>
</p>

- 13523018 - Raka Daffa Iftikhar
- 13523074 - Ahsan Malik Al Farisi

