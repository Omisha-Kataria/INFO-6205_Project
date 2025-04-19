package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

public class GomokuMove {
    private int row;
    private int col;

    public GomokuMove(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GomokuMove move = (GomokuMove) o;
        return row == move.row && col == move.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}