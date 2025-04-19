package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import java.util.ArrayList;
import java.util.List;

public class GomokuState implements Cloneable {
    public static final int EMPTY = 0;
    public static final int PLAYER_ONE = 1;
    public static final int PLAYER_TWO = 2;

    private int[][] board;
    private int boardSize;
    private int currentPlayer;

    public GomokuState() {
        this(15);
    }

    public GomokuState(int boardSize) {
        this.boardSize = boardSize;
        board = new int[boardSize][boardSize];
        currentPlayer = PLAYER_ONE;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int[][] getBoard() {
        return board;
    }

    /**
     *
     * Return all empty positions as legal moves.
     */
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

    /**
     * Place the current player's mark and then switch to the opponent.
     * @param move
     */
    public void makeMove(GomokuMove move) {
        if (board[move.getRow()][move.getCol()] != EMPTY) {
            throw new IllegalArgumentException("Invalid move! Position already occupied.");
        }
        board[move.getRow()][move.getCol()] = currentPlayer;
        currentPlayer = getOpponent(currentPlayer);
    }

    /**
     *
     * @param player
     * Returns the opponent number.
     */
    public static int getOpponent(int player) {
        return player == PLAYER_ONE ? PLAYER_TWO : PLAYER_ONE;
    }

    /**
     *
     * The game ends if somebody wins or there are no moves left.
     */
    public boolean isTerminal() {
        return (checkWin() != EMPTY) || getLegalMoves().isEmpty();
    }

    /**
     * Check every cell for any occurrence of 5 in a row.
     * Returns the winning player number or EMPTY if there is no winner.
     */
    public int checkWin() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                int player = board[i][j];
                if (player == EMPTY)
                    continue;

                if (j <= boardSize - 5) {
                    boolean win = true;
                    for (int k = 0; k < 5; k++) {
                        if (board[i][j + k] != player) {
                            win = false;
                            break;
                        }
                    }
                    if (win) return player;
                }

                if (i <= boardSize - 5) {
                    boolean win = true;
                    for (int k = 0; k < 5; k++) {
                        if (board[i + k][j] != player) {
                            win = false;
                            break;
                        }
                    }
                    if (win) return player;
                }

                if (i <= boardSize - 5 && j <= boardSize - 5) {
                    boolean win = true;
                    for (int k = 0; k < 5; k++) {
                        if (board[i + k][j + k] != player) {
                            win = false;
                            break;
                        }
                    }
                    if (win) return player;
                }

                if (i >= 4 && j <= boardSize - 5) {
                    boolean win = true;
                    for (int k = 0; k < 5; k++) {
                        if (board[i - k][j + k] != player) {
                            win = false;
                            break;
                        }
                    }
                    if (win) return player;
                }
            }
        }
        return EMPTY;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                sb.append(board[i][j] == EMPTY ? "." : board[i][j] == PLAYER_ONE ? "X" : "O");
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}