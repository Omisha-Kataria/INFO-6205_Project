package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Game;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;

public class GomokuGame implements Game<GomokuGame> {
    private GomokuState state;
    private final Player player1;
    private final Player player2;

    /**
     * Constructor to initialize players and board size
     * @param player1 the first player
     * @param player2 the second player
     * @param boardSize size of the board
     */
    public GomokuGame(Player player1, Player player2, int boardSize) {
        this.state = new GomokuState(boardSize);
        this.player1 = player1;
        this.player2 = player2;
    }

    /**
     * Constructor used by MCTS core to initialize a default game state.
     * Players are null as they are not needed for simulation.
     */
    public GomokuGame() {
        this(null, null, 15); // default board size
    }

    @Override
    public State<GomokuGame> start() {
        return new GomokuState(); // default board size of 15
    }

    @Override
    public int opener() {
        return GomokuState.PLAYER_ONE;
    }

    /**
     * Play the game until terminal state is reached.
     * @return the winner (PLAYER_ONE, PLAYER_TWO, or EMPTY for draw).
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
