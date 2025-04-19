package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RandomPlayerTest {
    @Test
    public void testSingleLegalMove() {
        GomokuState state = new GomokuState(1);
        RandomPlayer player = new RandomPlayer();
        GomokuMove move = player.getMove(state);
        assertEquals(new GomokuMove(0, 0), move);
    }

    @Test
    public void testMultipleLegalMoves() {
        GomokuState state = new GomokuState(2);
        RandomPlayer player = new RandomPlayer();
        GomokuMove move = player.getMove(state);
        assertTrue(state.getLegalMoves().contains(move));
    }
}