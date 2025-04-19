package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

public interface Player {
    GomokuMove getMove(GomokuState state);
}
