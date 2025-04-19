package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

/**
 *
 */
public class MCTSPlayer implements Player {
    private MonteCarloTreeSearch mcts;

    /**
     *
     * @param iterations
     */
    public MCTSPlayer(int iterations) {
        mcts = new MonteCarloTreeSearch(iterations);
    }

    @Override
    public GomokuMove getMove(GomokuState state) {
        return mcts.findNextMove(state);
    }
}

