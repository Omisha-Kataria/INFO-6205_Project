package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

public class GomokuGame {
    private GomokuState state;
    private Player player1;
    private Player player2;

    /**
     *
     * @param player1
     * @param player2
     * @param boardSize
     */
    public GomokuGame(Player player1, Player player2, int boardSize) {
        state = new GomokuState(boardSize);
        this.player1 = player1;
        this.player2 = player2;
    }

    /**
     *
     * Returns the winner (PLAYER_ONE, PLAYER_TWO, or EMPTY for draw).
     */
    public int play() {
        while (!state.isTerminal()) {
            GomokuMove move;
            if (state.getCurrentPlayer() == GomokuState.PLAYER_ONE) {
                move = player1.getMove(state);
            } else {
                move = player2.getMove(state);
            }
            state.makeMove(move);
        }
        return state.checkWin();
    }

    public GomokuState getState() {
        return state;
    }
}