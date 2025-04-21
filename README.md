# Gomoku with Monte Carlo Tree Search (MCTS)

This project implements the game of **Gomoku** (also known as "Five in a Row") using the **Monte Carlo Tree Search (MCTS)** algorithm for AI decision making. It supports:
- Human vs AI gameplay via a Swing-based GUI
- AI vs AI simulations
- MCTS benchmarking and performance timing

---

## 🧠 How It Works

- The game board is a `15x15` grid by default.
- The goal is to get **five pieces in a row** (horizontally, vertically, or diagonally).
- The AI uses **Monte Carlo Tree Search (MCTS)**:
    - **Selection**: navigate tree via UCT
    - **Expansion**: generate children for untried moves
    - **Simulation**: play random playouts to terminal state
    - **Backpropagation**: update statistics based on result

---

## 🗂️ Project Structure

| Package | Purpose |
|--------|---------|
| `gomoku/` | Core game logic (state, moves, players) |
| `gomoku.MCTSPlayer` | AI that uses MCTS |
| `gomoku.RandomPlayer` | Baseline random move AI |
| `gomoku.GomokuUI` | Graphical interface (Human vs MCTS) |
| `gomoku.GomokuExperiment` | Batch test runner for benchmarking |
| `gomoku.GomokuGameTest` | JUnit tests for gameplay and AI behavior |

---

## ▶️ Running the Project

### 💡 Prerequisites
- Java 11 or above
- IDE (IntelliJ, Eclipse) or terminal-based build tool (like `javac`, `maven`, or `gradle`)

### 🕹️ Run the GUI (Human vs MCTS)
```bash
# Run the GomokuUI main method
java com.phasmidsoftware.dsaipg.projects.mcts.gomoku.GomokuUI

# Run the benchmark experiments
java com.phasmidsoftware.dsaipg.projects.mcts.gomoku.GomokuExperiment

```

# In your IDE or terminal, run:
GomokuGameTest
GomokuStateTest
MonteCarloTreeSearchTest
MCTSNodeTest

📈 Performance Evaluation
When running GomokuExperiment, you'll see output like:

```
=== Gomoku MCTS Benchmark & Win Rates ===
MCTS vs Random (iters=1000, games=20) -> Avg time: 250.321 ms | P1(MCTS) wins: 18 | P2(Random) wins: 2 | Draws: 0
MCTS vs MCTS   (iters=1000, games=20) -> Avg time: 490.877 ms | P1(MCTS) wins: 10 | P2(MCTS) wins: 10 | Draws: 0
```

This helps you assess:

How many iterations lead to better play

Whether the MCTS AI reliably beats Random

Whether two MCTS agents balance (draw/win evenly)

You can tweak the number of iterations or board size for further insights.

# 📌 Future Improvements
- Add heuristics to guide simulations

- Support variable win lengths (e.g., 4-in-a-row, 6-in-a-row)

- Improve GUI visuals and controls

- Parallelize MCTS iterations for speed

# 🧑‍💻 Developers
This project was developed as part of an AI game programming project using Java and Monte Carlo Tree Search by Harshith Patil, Omisha Kataria & Prajeshkumar Sundareswaran.
Feel free to extend, adapt, or contribute ideas!

