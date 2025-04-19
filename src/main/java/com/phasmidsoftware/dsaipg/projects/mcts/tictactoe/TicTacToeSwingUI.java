package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * A Swing-based UI for the TicTacToe game.
 * This class implements a graphical user interface for the existing TicTacToe game logic.
 */
public class TicTacToeSwingUI extends JFrame {
    private final TicTacToe game;
    private State<TicTacToe> currentState;
    private final JButton[][] buttons;
    private final JLabel statusLabel;
    private int currentPlayer;

    public TicTacToeSwingUI() {
        game = new TicTacToe();
        currentState = game.start();
        currentPlayer = game.opener(); // X starts (value 1)


        setTitle("Tic Tac Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 450);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // Center on screen


        JPanel statusPanel = new JPanel();
        statusLabel = new JLabel("Player X's turn");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.NORTH);


        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 3));
        buttons = new JButton[3][3];

        //buttons
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 40));
                buttons[i][j].setFocusPainted(false);

                final int row = i;
                final int col = j;

                buttons[i][j].addActionListener(e -> handleButtonClick(row, col));
                boardPanel.add(buttons[i][j]);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        //panel
        JPanel controlPanel = new JPanel();
        JButton resetButton = new JButton("New Game");
        resetButton.addActionListener(e -> resetGame());
        controlPanel.add(resetButton);
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * handles a button click on the game board.
     *
     * @param row The row of the clicked button
     * @param col The column of the clicked button
     */
    private void handleButtonClick(int row, int col) {
        if (currentState.isTerminal() || !buttons[row][col].getText().isEmpty()) {
            return;
        }

        TicTacToe.TicTacToeMove move = new TicTacToe.TicTacToeMove(currentPlayer, row, col);
        currentState = currentState.next(move);

        updateUI();

        if (!currentState.isTerminal()) {
            currentPlayer = 1 - currentPlayer; // Toggle between 0 and 1
            statusLabel.setText("Player " + (currentPlayer == 1 ? "X" : "O") + "'s turn");
        } else {
            Optional<Integer> winner = currentState.winner();
            if (winner.isPresent()) {
                statusLabel.setText("Player " + (winner.get() == 1 ? "X" : "O") + " wins!");
            } else {
                statusLabel.setText("It's a draw!");
            }
        }
    }

    /**
     * updates the ui based on the current game state.
     */
    private void updateUI() {
        TicTacToe.TicTacToeState state = (TicTacToe.TicTacToeState) currentState;
        Position position = state.position();

        String boardStr = position.render();
        String[] rows = boardStr.split("\n");

        for (int i = 0; i < 3; i++) {
            String[] cells = rows[i].split(" ");
            for (int j = 0; j < 3; j++) {
                String cellValue = cells[j];
                if (cellValue.equals("X")) {
                    buttons[i][j].setText("X");
                    buttons[i][j].setForeground(Color.BLUE);
                } else if (cellValue.equals("O")) {
                    buttons[i][j].setText("O");
                    buttons[i][j].setForeground(Color.RED);
                }
            }
        }

        //if game won
        if (currentState.winner().isPresent()) {
            highlightWinningCells();
        }
    }

    /**
     * highlights the cells that form the winning line.
     */
    private void highlightWinningCells() {
        TicTacToe.TicTacToeState state = (TicTacToe.TicTacToeState) currentState;
        Position position = state.position();

        for (int i = 0; i < 3; i++) {
            if (allEqual(buttons[i][0].getText(), buttons[i][1].getText(), buttons[i][2].getText()) &&
                    !buttons[i][0].getText().isEmpty()) {
                buttons[i][0].setBackground(Color.GREEN);
                buttons[i][1].setBackground(Color.GREEN);
                buttons[i][2].setBackground(Color.GREEN);
                return;
            }
        }


        for (int j = 0; j < 3; j++) {
            if (allEqual(buttons[0][j].getText(), buttons[1][j].getText(), buttons[2][j].getText()) &&
                    !buttons[0][j].getText().isEmpty()) {
                buttons[0][j].setBackground(Color.GREEN);
                buttons[1][j].setBackground(Color.GREEN);
                buttons[2][j].setBackground(Color.GREEN);
                return;
            }
        }


        if (allEqual(buttons[0][0].getText(), buttons[1][1].getText(), buttons[2][2].getText()) &&
                !buttons[0][0].getText().isEmpty()) {
            buttons[0][0].setBackground(Color.GREEN);
            buttons[1][1].setBackground(Color.GREEN);
            buttons[2][2].setBackground(Color.GREEN);
            return;
        }

        if (allEqual(buttons[0][2].getText(), buttons[1][1].getText(), buttons[2][0].getText()) &&
                !buttons[0][2].getText().isEmpty()) {
            buttons[0][2].setBackground(Color.GREEN);
            buttons[1][1].setBackground(Color.GREEN);
            buttons[2][0].setBackground(Color.GREEN);
        }
    }

    /**
     * checks  three strings are all equal and non-empty.
     */
    private boolean allEqual(String a, String b, String c) {
        return !a.isEmpty() && a.equals(b) && b.equals(c);
    }

    /**
     * resets the game to the initial state.
     */
    private void resetGame() {
        currentState = game.start();
        currentPlayer = game.opener(); // X starts first

        statusLabel.setText("Player X's turn");

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setBackground(null);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicTacToeSwingUI::new);
    }
}