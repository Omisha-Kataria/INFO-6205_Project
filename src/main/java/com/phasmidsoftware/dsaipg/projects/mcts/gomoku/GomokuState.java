package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

import java.util.*;

public class GomokuState implements State<GomokuGame>, Cloneable {
    public static final int EMPTY = 0;
    public static final int PLAYER_ONE = 1;
    public static final int PLAYER_TWO = 2;

    private final int[][] board;
    private final int boardSize;
    private int currentPlayer;

    public GomokuState() {
        this(15);
    }

    public GomokuState(int boardSize) {
        this.boardSize = boardSize;
        this.board = new int[boardSize][boardSize];
        this.currentPlayer = PLAYER_ONE;
    }

    @Override
    public GomokuGame game() {
        return new GomokuGame(null, null, boardSize);
    }

    @Override
    public boolean isTerminal() {
        return checkWin() != EMPTY || getLegalMoves().isEmpty();
    }

    @Override
    public int player() {
        return currentPlayer;
    }

    @Override
    public Optional<Integer> winner() {
        int winner = checkWin();
        return winner == EMPTY ? Optional.empty() : Optional.of(winner);
    }

    @Override
    public Random random() {
        return new Random(); // optionally reuse one
    }

    @Override
    public Collection<Move<GomokuGame>> moves(int player) {
        return new ArrayList<>(getLegalMoves()); // GomokuMove implements Move<GomokuGame>
    }

    @Override
    public State<GomokuGame> next(Move<GomokuGame> move) {
        GomokuState next = this.clone();
        next.makeMove((GomokuMove) move);
        return next;
    }

    // ====== GAME LOGIC ======

    public List<GomokuMove> getLegalMoves() {
        List<GomokuMove> legalMoves = new ArrayList<>();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == EMPTY) {
                    legalMoves.add(new GomokuMove(i, j));
                }
            }
        }
        return legalMoves;
    }

    public void makeMove(GomokuMove move) {
        if (board[move.getRow()][move.getCol()] != EMPTY) {
            throw new IllegalArgumentException("Invalid move! Position already occupied.");
        }
        board[move.getRow()][move.getCol()] = currentPlayer;
        currentPlayer = getOpponent(currentPlayer);
    }

    public static int getOpponent(int player) {
        return player == PLAYER_ONE ? PLAYER_TWO : PLAYER_ONE;
    }

    public int checkWin() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                int player = board[i][j];
                if (player == EMPTY) continue;

                // Horizontal
                if (j <= boardSize - 5 && allEqual(player, i, j, 0, 1)) return player;

                // Vertical
                if (i <= boardSize - 5 && allEqual(player, i, j, 1, 0)) return player;

                // Diagonal (\)
                if (i <= boardSize - 5 && j <= boardSize - 5 && allEqual(player, i, j, 1, 1)) return player;

                // Anti-diagonal (/)
                if (i >= 4 && j <= boardSize - 5 && allEqual(player, i, j, -1, 1)) return player;
            }
        }
        return EMPTY;
    }

    private boolean allEqual(int player, int startRow, int startCol, int dRow, int dCol) {
        for (int k = 1; k < 5; k++) {
            if (board[startRow + k * dRow][startCol + k * dCol] != player) {
                return false;
            }
        }
        return true;
    }

    @Override
    public GomokuState clone() {
        GomokuState cloneState = new GomokuState(this.boardSize);
        for (int i = 0; i < boardSize; i++) {
            System.arraycopy(this.board[i], 0, cloneState.board[i], 0, boardSize);
        }
        cloneState.currentPlayer = this.currentPlayer;
        return cloneState;
    }

    public int[][] getBoard() {
        return board;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] row : board) {
            for (int cell : row) {
                sb.append(cell == EMPTY ? "." : (cell == PLAYER_ONE ? "X" : "O")).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
