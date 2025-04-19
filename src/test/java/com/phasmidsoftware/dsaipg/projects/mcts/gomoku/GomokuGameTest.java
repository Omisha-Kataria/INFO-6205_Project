package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class GomokuGameTest {
    private static class PredefinedPlayer implements Player {
        private final List<GomokuMove> moves;
        private int idx = 0;
        PredefinedPlayer(List<GomokuMove> moves) { this.moves = moves; }
        @Override
        public GomokuMove getMove(GomokuState state) { return moves.get(idx++); }
    }

    @Test
    public void testGetStateInitial() {
        Player p = new PredefinedPlayer(Arrays.asList(new GomokuMove(0,0)));
        GomokuGame game = new GomokuGame(p, p, 3);
        GomokuState s = game.getState();
        assertNotNull(s);
        assertEquals(3, s.getBoardSize());
        assertEquals(GomokuState.PLAYER_ONE, s.getCurrentPlayer());
        assertEquals(9, s.getLegalMoves().size());
    }

    @Test
    public void testPlayP1Win() {
        List<GomokuMove> p1 = Arrays.asList(
                new GomokuMove(0,0), new GomokuMove(0,1), new GomokuMove(0,2),
                new GomokuMove(0,3), new GomokuMove(0,4)
        );
        List<GomokuMove> p2 = Arrays.asList(
                new GomokuMove(1,0), new GomokuMove(1,1), new GomokuMove(1,2),
                new GomokuMove(1,3)
        );
        GomokuGame game = new GomokuGame(new PredefinedPlayer(p1), new PredefinedPlayer(p2), 5);
        assertEquals(GomokuState.PLAYER_ONE, game.play());
    }

    @Test
    public void testPlayP2Win() {
        List<GomokuMove> p1 = Arrays.asList(
                new GomokuMove(4,4), new GomokuMove(4,3), new GomokuMove(4,2),
                new GomokuMove(4,1), new GomokuMove(2,2)
        );
        List<GomokuMove> p2 = Arrays.asList(
                new GomokuMove(1,0), new GomokuMove(1,1), new GomokuMove(1,2),
                new GomokuMove(1,3), new GomokuMove(1,4)
        );
        GomokuGame game = new GomokuGame(new PredefinedPlayer(p1), new PredefinedPlayer(p2), 5);
        assertEquals(GomokuState.PLAYER_TWO, game.play());
    }

    @Test
    public void testPlayDraw() {
        List<GomokuMove> p1 = Arrays.asList(
                new GomokuMove(0,0), new GomokuMove(1,1)
        );
        List<GomokuMove> p2 = Arrays.asList(
                new GomokuMove(0,1), new GomokuMove(1,0)
        );
        GomokuGame game = new GomokuGame(new PredefinedPlayer(p1), new PredefinedPlayer(p2), 2);
        assertEquals(GomokuState.EMPTY, game.play());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlayThrowsExceptionOnInvalidMove() {
        List<GomokuMove> p1 = Arrays.asList(
                new GomokuMove(0,0), new GomokuMove(0,0)
        );
        List<GomokuMove> p2 = Arrays.asList(
                new GomokuMove(1,1)
        );
        GomokuGame game = new GomokuGame(new PredefinedPlayer(p1), new PredefinedPlayer(p2), 3);
        game.play();
    }
}