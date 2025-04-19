package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;

import java.util.Objects;

public class GomokuMove implements Move<GomokuGame> {
    private final int row;
    private final int col;
    private final int player;

    public GomokuMove(int row, int col) {
        this(row, col, -1);
    }

    // Optional constructor with player info
    public GomokuMove(int row, int col, int player) {
        this.row = row;
        this.col = col;
        this.player = player;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public int player() {
        return player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GomokuMove)) return false;
        GomokuMove move = (GomokuMove) o;
        return row == move.row && col == move.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}
