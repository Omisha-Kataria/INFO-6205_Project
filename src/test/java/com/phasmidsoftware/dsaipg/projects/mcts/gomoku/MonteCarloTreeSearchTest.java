package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Method;

public class MonteCarloTreeSearchTest {
    @Test
    public void testFindNextMoveSingleIteration() {
        GomokuState state = new GomokuState(2);
        MonteCarloTreeSearch mcts = new MonteCarloTreeSearch(1);
        GomokuMove move = mcts.findNextMove(state);
        assertTrue(state.getLegalMoves().contains(move));
    }

    @Test
    public void testExpandNodeReflection() throws Exception {
        GomokuState state = new GomokuState(3);
        MCTSNode root = new MCTSNode(state, null, null);
        MonteCarloTreeSearch mcts = new MonteCarloTreeSearch(1);
        Method expand = MonteCarloTreeSearch.class.getDeclaredMethod("expandNode", MCTSNode.class);
        expand.setAccessible(true);
        expand.invoke(mcts, root);
        assertEquals(state.getLegalMoves().size(), root.getChildren().size());
    }

    @Test
    public void testSimulateRandomPlayoutReflection() throws Exception {
        GomokuState terminal = new GomokuState(1);
        terminal.makeMove(new GomokuMove(0, 0));
        MCTSNode node = new MCTSNode(terminal, null, null);
        MonteCarloTreeSearch mcts = new MonteCarloTreeSearch(1);
        Method sim = MonteCarloTreeSearch.class.getDeclaredMethod("simulateRandomPlayout", MCTSNode.class);
        sim.setAccessible(true);
        int result = (int) sim.invoke(mcts, node);
        assertEquals(terminal.checkWin(), result);
    }

    @Test
    public void testBackPropagateReflection() throws Exception {
        GomokuState state = new GomokuState(2);
        MCTSNode node = new MCTSNode(state, null, null);
        MonteCarloTreeSearch mcts = new MonteCarloTreeSearch(1);
        Method back = MonteCarloTreeSearch.class.getDeclaredMethod("backPropagate", MCTSNode.class, int.class);
        back.setAccessible(true);
        back.invoke(mcts, node, GomokuState.EMPTY);
        assertEquals(1, node.getVisits());
        assertEquals(0.5, node.getWins(), 1e-6);
        node = new MCTSNode(state, null, null);
        back.invoke(mcts, node, node.getPlayerNo());
        assertEquals(1, node.getVisits());
        assertEquals(1.0, node.getWins(), 1e-6);
        node = new MCTSNode(state, null, null);
        back.invoke(mcts, node, GomokuState.getOpponent(node.getPlayerNo()));
        assertEquals(1, node.getVisits());
        assertEquals(0.0, node.getWins(), 1e-6);
    }
}