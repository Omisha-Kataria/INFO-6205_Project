// src/test/java/com/phasmidsoftware/dsaipg/projects/mcts/tictactoe/TicTacToeNodeTest.java
package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;

public class TicTacToeNodeTest {

    @Test
    public void testStateAndWhite() {
        TicTacToe.TicTacToeState s = new TicTacToe().new TicTacToeState();
        TicTacToeNode node = new TicTacToeNode(s);

        // state() should return exactly what we passed in
        assertSame(s, node.state());

        // opener (X) moves first, so at root white() should be true
        assertTrue(node.white());
    }

    @Test
    public void testIsLeaf() {
        // starting position is not terminal
        TicTacToeNode root = new TicTacToeNode(new TicTacToe().new TicTacToeState());
        assertFalse(root.isLeaf());

        // force a terminal position (three X's in first column)
        Position p = Position.parsePosition(
                "X . .\n" +
                        "X O .\n" +
                        "X . 0",
                TicTacToe.X
        );
        TicTacToeNode leaf = new TicTacToeNode(new TicTacToe().new TicTacToeState(p));
        assertTrue(leaf.isLeaf());
    }

    @Test
    public void testChildrenAndExpandAll() {
        TicTacToeNode root = new TicTacToeNode(new TicTacToe().new TicTacToeState());

        // no children before expand
        assertEquals(0, root.children().size());

        // after expandAll there should be 9 possible first moves
        root.expandAll();
        assertEquals(9, root.children().size());

        // none of those children are themselves terminal on a fresh board
        for (Node<TicTacToe> c : root.children()) {
            assertFalse(c.isLeaf());
        }
    }

    @Test
    public void testBackPropagateAggregates() {
        TicTacToeNode root = new TicTacToeNode(new TicTacToe().new TicTacToeState());
        // expand all to create children
        root.expandAll();

        // after expandAll, each child has wins=0,playouts=0 (since none are terminal),
        // and a backPropagate() will sum them all to 0
        root.backPropagate();
        assertEquals(0, root.wins());
        assertEquals(0, root.playouts());
    }

    @Test
    public void testIncrementPlayoutsAndAddWins() {
        TicTacToe.TicTacToeState s = new TicTacToe().new TicTacToeState();
        TicTacToeNode node = new TicTacToeNode(s);

        // initial non-terminal node has wins=0,playouts=0
        assertEquals(0, node.wins());
        assertEquals(0, node.playouts());

        // exercise the helpers
        for (int i = 1; i <= 5; i++) {
            node.incrementPlayouts();
            node.addWins(2);
            assertEquals(i, node.playouts());
            assertEquals(2*i, node.wins());
        }
    }


    @Test
    public void testParentLink() {
        TicTacToeNode root = new TicTacToeNode(new TicTacToe().new TicTacToeState());
        root.expandAll();

        // pick one child and verify its parent is the root
        TicTacToeNode child = (TicTacToeNode) root.children().iterator().next();
        assertSame(root, child.getParent());
    }
}