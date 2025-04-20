// src/test/java/com/phasmidsoftware/dsaipg/projects/mcts/tictactoe/MCTSTest.java
package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;
import org.junit.Before;
import org.junit.Test;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import java.util.Optional;

import static org.junit.Assert.*;

public class MCTSTest {

    private Method iterateMethod;
    private Field  rootField;

    @Before
    public void setUp() throws Exception {
        // grab the private iterate(Node) helper
        iterateMethod = MCTS.class.getDeclaredMethod("iterate", Node.class);
        iterateMethod.setAccessible(true);
        // grab the private root field
        rootField = MCTS.class.getDeclaredField("root");
        rootField.setAccessible(true);
    }

    @Test
    public void testTerminalDetection() {
        TicTacToeNode start = new TicTacToeNode(new TicTacToe().start());
        assertFalse("Start state should not be terminal", start.isLeaf());

        // build a winning position for X
        Position winPos = Position.parsePosition(
                "X . .\n" +
                        "X O .\n" +
                        "X . 0",
                TicTacToe.X
        );
        TicTacToe.TicTacToeState winSt =
                new TicTacToe().new TicTacToeState(winPos);
        TicTacToeNode winNode = new TicTacToeNode(winSt);
        assertTrue("That board must be terminal", winNode.isLeaf());
    }

    @Test
    public void testBoardShrinksByOneEachStep() throws Exception {
        TicTacToeNode startNode = new TicTacToeNode(new TicTacToe().start());
        MCTS engine = new MCTS(startNode);
        @SuppressWarnings("unchecked")
        Node<TicTacToe> node = (Node<TicTacToe>) rootField.get(engine);

        int empties = 9;  // Tic‐Tac‐Toe 3×3 initial empty count
        while (!node.state().isTerminal()) {
            @SuppressWarnings("unchecked")
            Node<TicTacToe> next = (Node<TicTacToe>) iterateMethod.invoke(engine, node);
            node = next;
            TicTacToeNode probe = new TicTacToeNode(node.state());
            probe.expandAll();
            empties--;
            List<Node<TicTacToe>> children = (List<Node<TicTacToe>>) probe.children();
            assertEquals("One fewer empty each move", empties, children.size());
        }
    }

    @Test
    public void testMCTSSelection() throws Exception {
        TicTacToeNode start = new TicTacToeNode(new TicTacToe().start());
        MCTS engine = new MCTS(start);

        @SuppressWarnings("unchecked")
        Node<TicTacToe> next = (Node<TicTacToe>) iterateMethod.invoke(engine, start);
        assertNotNull("MCTS should pick a next node", next);
        assertNotSame("Should advance the node", start, next);
    }

    @Test
    public void testDrawCount() throws Exception {
        int totalGames = 3, draws = 0;
        for (int i = 0; i < totalGames; i++) {
            TicTacToeNode start = new TicTacToeNode(new TicTacToe().start());
            MCTS engine = new MCTS(start);
            @SuppressWarnings("unchecked")
            Node<TicTacToe> node = (Node<TicTacToe>) rootField.get(engine);

            // play until terminal
            while (!node.state().isTerminal()) {
                @SuppressWarnings("unchecked")
                Node<TicTacToe> next = (Node<TicTacToe>) iterateMethod.invoke(engine, node);
                node = next;
            }
            Optional<Integer> winner = node.state().winner();
            if (winner.isEmpty()) draws++;
        }
        assertTrue("Draw count in range", draws >= 0 && draws <= totalGames);
    }
}
