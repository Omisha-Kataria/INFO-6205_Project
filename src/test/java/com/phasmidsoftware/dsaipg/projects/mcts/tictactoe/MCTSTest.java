// src/test/java/com/phasmidsoftware/dsaipg/projects/mcts/tictactoe/MCTSTest.java
package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MCTSTest {

    @Before
    public void resetTimers() {
        MCTS.totalSelectTime   = 0;
        MCTS.totalExpandTime   = 0;
        MCTS.totalSimulateTime = 0;
        MCTS.totalBackpropTime = 0;
        MCTS.totalTime         = 0;
    }

    @Test
    public void testTerminalDetection() {
        TicTacToeNode root = new TicTacToeNode(new TicTacToe().start());
        assertFalse("Empty start state should not be terminal", root.isLeaf());

        Position winPos = Position.parsePosition(
                "X . .\n" +
                        "X O .\n" +
                        "X . 0",
                TicTacToe.X
        );
        TicTacToe.TicTacToeState winState =
                new TicTacToe().new TicTacToeState(winPos);
        TicTacToeNode leaf = new TicTacToeNode(winState);
        assertTrue("That board must be terminal", leaf.isLeaf());
    }

    @Test
    public void testBoardShrinksByOneEachStep() {
        TicTacToeNode node = new TicTacToeNode(new TicTacToe().start());
        int empties = 9;

        while (!node.isLeaf()) {
            node = (TicTacToeNode) MCTS.nextNodeWithTiming(node);
            TicTacToeNode probe = new TicTacToeNode(node.state());
            probe.expandAll();
            empties--;
            assertEquals("Should lose exactly one empty each move",
                    empties,
                    probe.children().size());
        }
    }

    @Test
    public void testTimingCountersIncrement() {
        // All start at zero
        assertEquals(0, MCTS.totalTime);
        assertEquals(0, MCTS.totalSelectTime);
        assertEquals(0, MCTS.totalExpandTime);
        assertEquals(0, MCTS.totalSimulateTime);
        assertEquals(0, MCTS.totalBackpropTime);

        // Run a single MCTS step
        MCTS.nextNodeWithTiming(new TicTacToeNode(new TicTacToe().start()));

        assertTrue("totalTime>0",         MCTS.totalTime         > 0);
        assertTrue("selectTime>0",        MCTS.totalSelectTime   > 0);
        assertTrue("expandTime>0",        MCTS.totalExpandTime   > 0);
        assertTrue("simulateTime>0",      MCTS.totalSimulateTime > 0);
        assertTrue("backpropTime>0",      MCTS.totalBackpropTime > 0);
    }


    @Test
    public void testMCTS() {
        TicTacToe game = new TicTacToe();
        State<TicTacToe> initialState = game.start();
        TicTacToeNode rootNode = new TicTacToeNode(initialState);
        rootNode.expandAll();
        assertFalse("Root should have children after expansion", rootNode.children().isEmpty());
        Node<TicTacToe> nextNode = MCTS.nextNodeWithTiming(rootNode);
        assertNotNull("MCTS should select a next move", nextNode);
    }

    @Test
    public void testDraw() {
        int totalGames = 3;
        int draws = 0;

        for (int i = 0; i < totalGames; i++) {
            TicTacToe game = new TicTacToe();
            Node<TicTacToe> node = new TicTacToeNode(game.start());
            while (!node.state().isTerminal()) {
                node = MCTS.nextNodeWithTiming(node);
            }
            if (node.state().winner().isEmpty()) {
                draws++;
            }
        }
        System.out.println("Games played: " + totalGames + ", Draws: " + draws);
    }
}
