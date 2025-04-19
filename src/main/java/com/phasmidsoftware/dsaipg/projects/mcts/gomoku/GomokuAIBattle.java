package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicInteger;

public class GomokuAIBattle {
    private JFrame frame;
    private BoardPanel boardPanel;
    private JTextArea gameLog;
    private JButton startButton;
    private JButton stepButton;
    private JButton pauseButton;
    private JComboBox<String> player1TypeCombo;
    private JComboBox<String> player2TypeCombo;
    private JSpinner iterationsSpinner1;
    private JSpinner iterationsSpinner2;
    private JLabel statusLabel;
    private JLabel statsLabel;

    private GomokuState gameState;
    private Player player1;
    private Player player2;
    private boolean gameRunning;
    private boolean autoPlay;
    private Timer gameTimer;
    private int moveDelay = 500;

    // Statistics
    private AtomicInteger player1Wins = new AtomicInteger(0);
    private AtomicInteger player2Wins = new AtomicInteger(0);
    private AtomicInteger draws = new AtomicInteger(0);
    private AtomicInteger totalGames = new AtomicInteger(0);

    public GomokuAIBattle() {
        gameState = new GomokuState();
        initializeUI();
        initializePlayers();
    }

    private void initializeUI() {
        // this is the maain frame
        frame = new JFrame("Gomoku AI Battle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLayout(new BorderLayout());

        // game board
        boardPanel = new BoardPanel();
        frame.add(boardPanel, BorderLayout.CENTER);

        // control pane
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        JPanel playerConfigPanel = new JPanel(new GridLayout(2, 3, 10, 5));
        playerConfigPanel.setBorder(BorderFactory.createTitledBorder("Player Configuration"));

        //defining players
        playerConfigPanel.add(new JLabel("Player 1 (Black):"));
        player1TypeCombo = new JComboBox<>(new String[]{"MCTS", "Random"});
        playerConfigPanel.add(player1TypeCombo);

        JPanel p1IterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p1IterPanel.add(new JLabel("Iterations:"));
        iterationsSpinner1 = new JSpinner(new SpinnerNumberModel(1000, 100, 100000, 100));
        p1IterPanel.add(iterationsSpinner1);
        playerConfigPanel.add(p1IterPanel);

        playerConfigPanel.add(new JLabel("Player 2 (White):"));
        player2TypeCombo = new JComboBox<>(new String[]{"MCTS", "Random"});
        playerConfigPanel.add(player2TypeCombo);

        JPanel p2IterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p2IterPanel.add(new JLabel("Iterations:"));
        iterationsSpinner2 = new JSpinner(new SpinnerNumberModel(1000, 100, 100000, 100));
        p2IterPanel.add(iterationsSpinner2);
        playerConfigPanel.add(p2IterPanel);

        controlPanel.add(playerConfigPanel);

        //game buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        startButton = new JButton("New Game");
        startButton.addActionListener(e -> {
            startNewGame();
        });
        buttonPanel.add(startButton);

        stepButton = new JButton("Step Move");
        stepButton.addActionListener(e -> {
            if (!gameState.isTerminal()) {
                makeNextMove();
            }
        });
        stepButton.setEnabled(false);
        buttonPanel.add(stepButton);

        pauseButton = new JButton("Auto Play");
        pauseButton.addActionListener(e -> {
            toggleAutoPlay();
        });
        pauseButton.setEnabled(false);
        buttonPanel.add(pauseButton);

        JButton resetStatsButton = new JButton("Reset Stats");
        resetStatsButton.addActionListener(e -> {
            resetStatistics();
        });
        buttonPanel.add(resetStatsButton);

        controlPanel.add(buttonPanel);

        //panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Configure players and press New Game to start.");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.add(statusLabel, BorderLayout.WEST);

        statsLabel = new JLabel("Stats: P1 Wins: 0 | P2 Wins: 0 | Draws: 0 | Total: 0");
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statsLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.add(statsLabel, BorderLayout.EAST);

        controlPanel.add(statusPanel);

        //game area
        gameLog = new JTextArea(10, 50);
        gameLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(gameLog);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Game Log"));
        controlPanel.add(scrollPane);

        JPanel delayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        delayPanel.add(new JLabel("Move Delay (ms):"));
        JSlider delaySlider = new JSlider(JSlider.HORIZONTAL, 0, 2000, moveDelay);
        delaySlider.setMajorTickSpacing(500);
        delaySlider.setMinorTickSpacing(100);
        delaySlider.setPaintTicks(true);
        delaySlider.setPaintLabels(true);
        delaySlider.addChangeListener(e -> {
            moveDelay = delaySlider.getValue();
            if (gameTimer != null) {
                gameTimer.setDelay(moveDelay);
            }
        });
        delayPanel.add(delaySlider);
        controlPanel.add(delayPanel);

        frame.add(controlPanel, BorderLayout.EAST);

        //time
        gameTimer = new Timer(moveDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameRunning && autoPlay && !gameState.isTerminal()) {
                    makeNextMove();
                } else if (gameState.isTerminal()) {
                    gameTimer.stop();
                    autoPlay = false;
                    pauseButton.setText("Auto Play");
                }
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void initializePlayers() {
        player1 = createPlayer(player1TypeCombo.getSelectedIndex(), (Integer) iterationsSpinner1.getValue());
        player2 = createPlayer(player2TypeCombo.getSelectedIndex(), (Integer) iterationsSpinner2.getValue());
    }

    private Player createPlayer(int playerType, int iterations) {
        switch (playerType) {
            case 0: // this is for mcts
                return new MCTSPlayer(iterations);
            case 1: // rand
                return new RandomPlayer();
            default:
                return new RandomPlayer();
        }
    }

    private void startNewGame() {
        // to stop
        if (gameTimer.isRunning()) {
            gameTimer.stop();
        }

        gameState = new GomokuState();

        player1 = createPlayer(player1TypeCombo.getSelectedIndex(), (Integer) iterationsSpinner1.getValue());
        player2 = createPlayer(player2TypeCombo.getSelectedIndex(), (Integer) iterationsSpinner2.getValue());


        boardPanel.repaint();
        gameRunning = true;
        autoPlay = false;
        pauseButton.setText("Auto Play");

        stepButton.setEnabled(true);
        pauseButton.setEnabled(true);

        gameLog.setText("");
        logMessage("New game started: " + getPlayerDescription(player1TypeCombo.getSelectedIndex(), (Integer) iterationsSpinner1.getValue())
                + " vs " + getPlayerDescription(player2TypeCombo.getSelectedIndex(), (Integer) iterationsSpinner2.getValue()));

        statusLabel.setText("Game started. Player 1's turn (Black).");
    }

    private String getPlayerDescription(int playerType, int iterations) {
        if (playerType == 0) { //
            return "MCTS (iter=" + iterations + ")";
        } else {
            return "Random";
        }
    }

    private void makeNextMove() {
        if (!gameRunning || gameState.isTerminal()) return;

        int currentPlayer = gameState.getCurrentPlayer();

        Player activePlayer = (currentPlayer == GomokuState.PLAYER_ONE) ? player1 : player2;
        String playerName = (currentPlayer == GomokuState.PLAYER_ONE) ? "Player 1 (Black)" : "Player 2 (White)";

        GomokuMove move = activePlayer.getMove(gameState.clone());

        //make the move?
        try {
            gameState.makeMove(move);
            logMessage(playerName + " played: (" + move.getRow() + ", " + move.getCol() + ")");


            boardPanel.repaint();

            if (gameState.isTerminal()) {
                gameRunning = false;
                int winner = gameState.checkWin();

                if (winner == GomokuState.EMPTY) {
                    logMessage("Game over: It's a draw!");
                    statusLabel.setText("Game over: It's a draw!");
                    draws.incrementAndGet();
                } else if (winner == GomokuState.PLAYER_ONE) {
                    logMessage("Game over: Player 1 (Black) wins!");
                    statusLabel.setText("Game over: Player 1 (Black) wins!");
                    player1Wins.incrementAndGet();
                } else {
                    logMessage("Game over: Player 2 (White) wins!");
                    statusLabel.setText("Game over: Player 2 (White) wins!");
                    player2Wins.incrementAndGet();
                }

                totalGames.incrementAndGet();
                updateStatsLabel();

                stepButton.setEnabled(false);
                pauseButton.setEnabled(false);
            } else {

                currentPlayer = gameState.getCurrentPlayer(); // update player
                statusLabel.setText((currentPlayer == GomokuState.PLAYER_ONE) ?
                        "Player 1's turn (Black)" : "Player 2's turn (White)");
            }
        } catch (IllegalArgumentException e) {
            logMessage("ERROR: " + e.getMessage());
        }
    }

    private void toggleAutoPlay() {
        autoPlay = !autoPlay;
        pauseButton.setText(autoPlay ? "Pause" : "Auto Play");

        if (autoPlay) {
            gameTimer.start();
        } else {
            gameTimer.stop();
        }
    }

    private void resetStatistics() {
        player1Wins.set(0);
        player2Wins.set(0);
        draws.set(0);
        totalGames.set(0);
        updateStatsLabel();
        logMessage("Statistics reset");
    }

    private void updateStatsLabel() {
        statsLabel.setText(String.format("Stats: P1 Wins: %d | P2 Wins: %d | Draws: %d | Total: %d",
                player1Wins.get(), player2Wins.get(), draws.get(), totalGames.get()));
    }

    private void logMessage(String message) {
        gameLog.append(message + "\n");
        gameLog.setCaretPosition(gameLog.getDocument().getLength());
    }


    private class BoardPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // graphics for the the wooden background
            g2d.setColor(new Color(210, 180, 140));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            int boardSize = gameState.getBoardSize();
            int cellSize = getCellSize();

            // graphics fo the grid lines
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(1.0f));

            // horizontal lines
            for (int i = 0; i < boardSize; i++) {
                g2d.drawLine(cellSize/2, cellSize/2 + i * cellSize,
                        cellSize/2 + (boardSize - 1) * cellSize, cellSize/2 + i * cellSize);
            }

            //vertical lines
            for (int i = 0; i < boardSize; i++) {
                g2d.drawLine(cellSize/2 + i * cellSize, cellSize/2,
                        cellSize/2 + i * cellSize, cellSize/2 + (boardSize - 1) * cellSize);
            }

            // for the dots
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GomokuAIBattle();
        });
    }
}