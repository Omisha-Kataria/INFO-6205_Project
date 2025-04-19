package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import static org.junit.Assert.*;

public class GomokuStateTest {

    private GomokuState state;

    @Before
    public void setUp() {
        state = new GomokuState(5);
    }

    @Test
    public void testInitialState() {
        assertEquals(GomokuState.PLAYER_ONE, state.getCurrentPlayer());
        assertEquals(5, state.getBoardSize());
        int[][] board = state.getBoard();
        assertNotNull(board);
        List<GomokuMove> moves = state.getLegalMoves();
        assertEquals(25, moves.size());
        // initial is not terminal
        assertFalse(state.isTerminal());
    }

    @Test
    public void testGetLegalMovesAfterMove() {
        GomokuMove m = new GomokuMove(0, 0);
        state.makeMove(m);
        List<GomokuMove> moves = state.getLegalMoves();
        assertEquals(24, moves.size());
        assertFalse(moves.contains(m));
    }

    @Test
    public void testMakeMoveAndSwitchPlayer() {
        GomokuMove move = new GomokuMove(2, 2);
        state.makeMove(move);
        assertEquals(GomokuState.PLAYER_TWO, state.getCurrentPlayer());
        // occupied now
        assertThrows(IllegalArgumentException.class, () -> state.makeMove(move));
    }

    @Test
    public void testGetOpponent() {
        assertEquals(GomokuState.PLAYER_TWO, GomokuState.getOpponent(GomokuState.PLAYER_ONE));
        assertEquals(GomokuState.PLAYER_ONE, GomokuState.getOpponent(GomokuState.PLAYER_TWO));
    }

    @Test
    public void testCloneIndependence() {
        state.makeMove(new GomokuMove(1, 1));
        GomokuState clone = state.clone();
        // clone move by PLAYER_TWO
        clone.makeMove(new GomokuMove(2, 2));
        assertNotEquals(state.getCurrentPlayer(), clone.getCurrentPlayer());
        // original unaffected
        assertEquals(GomokuState.EMPTY, state.getBoard()[2][2]);
        assertEquals(GomokuState.PLAYER_TWO, clone.getBoard()[2][2]);
    }

    @Test
    public void testCheckWinHorizontal() {
        int[][] board = state.getBoard();
        for (int j = 0; j < 5; j++) board[0][j] = GomokuState.PLAYER_ONE;
        assertTrue(state.isTerminal());
        assertEquals(GomokuState.PLAYER_ONE, state.checkWin());
    }

    @Test
    public void testCheckWinVertical() {
        int[][] board = state.getBoard();
        for (int i = 0; i < 5; i++) board[i][0] = GomokuState.PLAYER_ONE;
        assertTrue(state.isTerminal());
        assertEquals(GomokuState.PLAYER_ONE, state.checkWin());
    }

    @Test
    public void testCheckWinDiagonalDown() {
        int[][] board = state.getBoard();
        for (int k = 0; k < 5; k++) board[k][k] = GomokuState.PLAYER_ONE;
        assertTrue(state.isTerminal());
        assertEquals(GomokuState.PLAYER_ONE, state.checkWin());
    }

    @Test
    public void testCheckWinDiagonalUp() {
        int[][] board = state.getBoard();
        // positions (4,0),(3,1),(2,2),(1,3),(0,4)
        for (int k = 0; k < 5; k++) board[4 - k][k] = GomokuState.PLAYER_TWO;
        assertTrue(state.isTerminal());
        assertEquals(GomokuState.PLAYER_TWO, state.checkWin());
    }

    @Test
    public void testToString() {
        GomokuState s = new GomokuState(2);
        String repr = s.toString();
        String[] lines = repr.split("\n");
        assertEquals(2, lines.length);
        assertTrue(lines[0].trim().equals(". ."));
        assertTrue(lines[1].trim().equals(". ."));
    }

    @Test
    public void testIsTerminalDraw() {
        GomokuState s = new GomokuState(2);
        s.makeMove(new GomokuMove(0, 0));
        s.makeMove(new GomokuMove(0, 1));
        s.makeMove(new GomokuMove(1, 0));
        s.makeMove(new GomokuMove(1, 1));
        assertTrue(s.isTerminal());
        assertEquals(GomokuState.EMPTY, s.checkWin());
    }

    @Test
    public void testCloneContent() {
        state.makeMove(new GomokuMove(1, 1));
        GomokuState clone = state.clone();
        assertEquals(state.getBoardSize(), clone.getBoardSize());
        assertEquals(state.getBoard()[1][1], clone.getBoard()[1][1]);
    }

    @Test
    public void testAllEqualFails() {
        int[][] board = state.getBoard();
        for (int j = 0; j < 4; j++) board[0][j] = GomokuState.PLAYER_ONE;
        board[0][4] = GomokuState.PLAYER_TWO;
        assertEquals(GomokuState.EMPTY, state.checkWin()); // No win
    }

    @Test
    public void testCloneDeepEquality() {
        state.makeMove(new GomokuMove(2, 2));
        GomokuState clone = state.clone();
        assertArrayEquals(state.getBoard(), clone.getBoard());
        assertEquals(state.getCurrentPlayer(), clone.getCurrentPlayer());
    }

    @Test
    public void testWinnerOptional() {
        int[][] board = state.getBoard();
        for (int j = 0; j < 5; j++) board[0][j] = GomokuState.PLAYER_ONE;
        assertTrue(state.winner().isPresent());
        assertEquals(Integer.valueOf(GomokuState.PLAYER_ONE), state.winner().get());
    }

    @Test
    public void testMovesInterfaceMethod() {
        Collection<Move<GomokuGame>> moves = state.moves(state.getCurrentPlayer());
        assertEquals(25, moves.size());
    }

    @Test
    public void testRandomNonNull() {
        assertNotNull(state.random());
    }


    @Test
    public void testPlayerMethod() {
        assertEquals(GomokuState.PLAYER_ONE, state.player());
    }

    @Test
    public void testGameMethod() {
        assertNotNull(state.game());
    }

}