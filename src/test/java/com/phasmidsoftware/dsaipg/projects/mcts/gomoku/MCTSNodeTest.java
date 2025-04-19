package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class MCTSNodeTest {
    private GomokuState state2x2;
    private MCTSNode root;

    @Before
    public void setUp() {
        state2x2 = new GomokuState(2);
        root = new MCTSNode(state2x2, null, null);
    }

    @Test
    public void testConstructorRoot() {
        // root.playerNo should be opponent of currentPlayer
        assertEquals(GomokuState.getOpponent(GomokuState.PLAYER_ONE), root.getPlayerNo());
        assertNull(root.getParent());
        assertTrue(root.getChildren().isEmpty());
        assertEquals(0, root.getVisits());
        assertEquals(0.0, root.getWins(), 1e-6);
    }

    @Test
    public void testChildConstructor() {
        GomokuMove m = new GomokuMove(0, 0);
        GomokuState s2 = state2x2.clone(); s2.makeMove(m);
        MCTSNode child = new MCTSNode(s2, root, m);
        assertEquals(root, child.getParent());
        assertEquals(m, child.getMove());
        assertEquals(root.getState().getCurrentPlayer(), child.getPlayerNo());
        assertEquals(0, child.getVisits());
        assertEquals(0.0, child.getWins(), 1e-6);
        assertTrue(child.getChildren().isEmpty());
    }

    @Test
    public void testAddChildAndAccessors() {
        GomokuMove m = new GomokuMove(0, 0);
        GomokuState s = state2x2.clone(); s.makeMove(m);
        MCTSNode child = new MCTSNode(s, root, m);
        root.addChild(child);
        List<MCTSNode> children = root.getChildren();
        assertEquals(1, children.size());
        assertEquals(m, children.get(0).getMove());
        assertEquals(root, children.get(0).getParent());
    }

    @Test
    public void testIsFullyExpandedAndUntriedMoves() {
        // initially not fully expanded
        assertFalse(root.isFullyExpanded());
        List<GomokuMove> untried = root.getUntriedMoves();
        assertEquals(state2x2.getLegalMoves().size(), untried.size());
        // expand all
        for (GomokuMove move : state2x2.getLegalMoves()) {
            GomokuState s = state2x2.clone(); s.makeMove(move);
            root.addChild(new MCTSNode(s, root, move));
        }
        assertTrue(root.isFullyExpanded());
        assertTrue(root.getUntriedMoves().isEmpty());
    }

    @Test
    public void testIsTerminalNode() {
        GomokuState terminal = new GomokuState(1);
        terminal.makeMove(new GomokuMove(0, 0));
        MCTSNode node = new MCTSNode(terminal, null, null);
        assertTrue(node.isTerminalNode());
    }

    @Test
    public void testRandomChild() {
        MCTSNode c1 = new MCTSNode(state2x2.clone(), root, new GomokuMove(0, 0));
        MCTSNode c2 = new MCTSNode(state2x2.clone(), root, new GomokuMove(0, 1));
        root.addChild(c1); root.addChild(c2);
        MCTSNode rand = root.getRandomChild();
        assertTrue(rand == c1 || rand == c2);
    }

    @Test
    public void testSelectChildUCT() {
        MCTSNode c1 = new MCTSNode(state2x2.clone(), root, new GomokuMove(0, 0));
        MCTSNode c2 = new MCTSNode(state2x2.clone(), root, new GomokuMove(0, 1));
        root.addChild(c1); root.addChild(c2);
        root.updateStats(0.0);
        c1.updateStats(1.0);
        c2.updateStats(0.0);
        assertEquals(c1, root.selectChild());
    }

    @Test
    public void testUpdateStatsAndBestChild() {
        MCTSNode c1 = new MCTSNode(state2x2.clone(), root, new GomokuMove(0, 0));
        MCTSNode c2 = new MCTSNode(state2x2.clone(), root, new GomokuMove(0, 1));
        root.addChild(c1); root.addChild(c2);
        c1.updateStats(1.0);
        c2.updateStats(1.0);
        c1.updateStats(0.0);
        assertEquals(c1, MCTSNode.bestChild(root));
    }
}