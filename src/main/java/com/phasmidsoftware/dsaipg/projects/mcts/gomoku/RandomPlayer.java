package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import java.util.List;
import java.util.Random;

/**
 *
 */
public class RandomPlayer implements Player {
    private Random random;

    public RandomPlayer() {
        random = new Random();
    }

    @Override
    public GomokuMove getMove(GomokuState state) {
        List<GomokuMove> legalMoves = state.getLegalMoves();
        return legalMoves.get(random.nextInt(legalMoves.size()));
    }
}

