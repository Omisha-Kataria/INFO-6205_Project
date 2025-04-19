package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MCTSPlayerTest {
    @Test
    public void testGetMoveDelegates() {
        MCTSPlayer player = new MCTSPlayer(1);
        GomokuMove move = player.getMove(new GomokuState(2));
        assertTrue(new GomokuState(2).getLegalMoves().contains(move));
    }
}