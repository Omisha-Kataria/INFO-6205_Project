package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GomokuUI {
    private JFrame frame;
    private BoardPanel boardPanel;
    private JLabel statusLabel;
    private GomokuState gameState;
    private Player player1;
    private Player player2;
    private boolean gameOver;
    private boolean humanTurn;

    public GomokuUI(Player aiPlayer) {
        gameState = new GomokuState();
        this.player1 = new HumanPlayer();
        this.player2 = aiPlayer;
        this.gameOver = false;
        this.humanTurn = true;            //human first

        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Gomoku - Five in a Row");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 650);
        frame.setLayout(new BorderLayout());

        boardPanel = new BoardPanel();
        frame.add(boardPanel, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Your turn (X)");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        statusPanel.add(statusLabel, BorderLayout.WEST);

        JButton restartButton = new JButton("Restart Game");
        restartButton.addActionListener(e -> restartGame());
        statusPanel.add(restartButton, BorderLayout.EAST);

        frame.add(statusPanel, BorderLayout.SOUTH);

        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameOver || !humanTurn) return;


                int cellSize = boardPanel.getCellSize();
                int row = e.getY() / cellSize;
                int col = e.getX() / cellSize;

                //making sure a valid move
                if (row >= 0 && row < gameState.getBoardSize() &&
                        col >= 0 && col < gameState.getBoardSize() &&
                        gameState.getBoard()[row][col] == GomokuState.EMPTY) {

                    //move
                    GomokuMove move = new GomokuMove(row, col);
                    makeMove(move);

                    // if over?
                    if (!gameOver) {
                        humanTurn = false;
                        statusLabel.setText("AI is thinking...");

                        SwingWorker<GomokuMove, Void> worker = new SwingWorker<>() {
                            @Override
                            protected GomokuMove doInBackground() {
                                return player2.getMove(gameState);
                            }

                            @Override
                            protected void done() {
                                try {
                                    GomokuMove aiMove = get();
                                    makeMove(aiMove);
                                    humanTurn = true;
                                    if (!gameOver) {
                                        statusLabel.setText("Your turn (X)");
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        };
                        worker.execute();
                    }
                }
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void makeMove(GomokuMove move) {
        gameState.makeMove(move);
        boardPanel.repaint();


        if (gameState.isTerminal()) {
            gameOver = true;
            int winner = gameState.checkWin();
            if (winner == GomokuState.EMPTY) {
                statusLabel.setText("Game over: It's a draw!");
            } else if (winner == GomokuState.PLAYER_ONE) {
                statusLabel.setText("Game over: You win!");
            } else {
                statusLabel.setText("Game over: AI wins!");
            }
        }
    }

    private void restartGame() {
        gameState = new GomokuState();
        gameOver = false;
        humanTurn = true;
        statusLabel.setText("Your turn (X)");
        boardPanel.repaint();
    }


    private class BoardPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // graphic forthe wooden background
            g2d.setColor(new Color(210, 180, 140));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            int boardSize = gameState.getBoardSize();
            int cellSize = getCellSize();

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1.0f));

            for (int i = 0; i < boardSize; i++) {
                g2d.drawLine(cellSize/2, cellSize/2 + i * cellSize,
                        cellSize/2 + (boardSize - 1) * cellSize, cellSize/2 + i * cellSize);
            }

            for (int i = 0; i < boardSize; i++) {
                g2d.drawLine(cellSize/2 + i * cellSize, cellSize/2,
                        cellSize/2 + i * cellSize, cellSize/2 + (boardSize - 1) * cellSize);
            }

            // the dots
            int[][] board = gameState.getBoard();
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    if (board[i][j] == GomokuState.PLAYER_ONE) {
                        g2d.setColor(Color.BLACK);
                        g2d.fillOval(cellSize/2 + j * cellSize - cellSize/3,
                                cellSize/2 + i * cellSize - cellSize/3,
                                2 * cellSize/3, 2 * cellSize/3);
                    } else if (board[i][j] == GomokuState.PLAYER_TWO) {
                        g2d.setColor(Color.WHITE);
                        g2d.fillOval(cellSize/2 + j * cellSize - cellSize/3,
                                cellSize/2 + i * cellSize - cellSize/3,
                                2 * cellSize/3, 2 * cellSize/3);
                        g2d.setColor(Color.BLACK);
                        g2d.drawOval(cellSize/2 + j * cellSize - cellSize/3,
                                cellSize/2 + i * cellSize - cellSize/3,
                                2 * cellSize/3, 2 * cellSize/3);
                    }
                }
            }
        }

        public int getCellSize() {
            int boardSize = gameState.getBoardSize();
            return Math.min(getWidth(), getHeight()) / boardSize;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(600, 600);
        }
    }

    private static class HumanPlayer implements Player {
        @Override
        public GomokuMove getMove(GomokuState state) {
            throw new UnsupportedOperationException("Human moves are made through the UI");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Player mctsPlayer = new MCTSPlayer(1000);

            new GomokuUI(mctsPlayer);
        });
    }
}