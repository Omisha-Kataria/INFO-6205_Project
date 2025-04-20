// src/main/java/com/phasmidsoftware/dsaipg/projects/mcts/tictactoe/MCTS.java
package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;

import java.util.*;

/**
 * Full MCTS driver: Select→Expand→Simulate→Backpropagate with UCT,
 * plus per-phase timing printed to console (no CSV).
 */
public class MCTS {

    private static final int    SIMULATION_COUNT = 5000;
    private static final double C                = 1.414;

    // Accumulators for timing each phase
    public static long totalSelectTime   = 0;
    public static long totalExpandTime   = 0;
    public static long totalSimulateTime = 0;
    public static long totalBackpropTime = 0;
    public static long totalTime         = 0;

    public final Node<TicTacToe> root;

    public MCTS(Node<TicTacToe> root) {
        this.root = root;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        MCTS engine = new MCTS(new TicTacToeNode(new TicTacToe().start()));
        Node<TicTacToe> node = engine.root;

        System.out.println("Start!");
        System.out.println(showBoard(node.state()));

        // Play until terminal, advancing by MCTS‐chosen moves
        while (!node.state().isTerminal()) {
            node = nextNodeWithTiming(node);
            System.out.println(showBoard(node.state()));
        }

        // Print timing summary
        System.out.println("Game over\n==== Timing Summary ====");
        System.out.printf(" Selection:  %.3f ms%n", totalSelectTime   / 1e6);
        System.out.printf(" Expansion:  %.3f ms%n", totalExpandTime   / 1e6);
        System.out.printf(" Simulation: %.3f ms%n", totalSimulateTime / 1e6);
        System.out.printf(" Backprop:   %.3f ms%n", totalBackpropTime / 1e6);
        System.out.printf(" Total MCTS: %.3f ms%n", totalTime         / 1e6);
    }

    /**
     * Perform SIMULATION_COUNT rounds of Select→Expand→Simulate→Backpropagate
     * from the given root, accumulate timings, then return the best‐visit child.
     */
    public static Node<TicTacToe> nextNodeWithTiming(Node<TicTacToe> node) {
        long selectTime = 0, expandTime = 0, simTime = 0, backTime = 0;
        long t0 = System.nanoTime();

        for (int i = 0; i < SIMULATION_COUNT; i++) {
            long t1, t2;

            t1 = System.nanoTime();
            Node<TicTacToe> cur = select(node);
            t2 = System.nanoTime(); selectTime += (t2 - t1);

            t1 = System.nanoTime();
            cur = expand(cur);
            t2 = System.nanoTime(); expandTime += (t2 - t1);

            t1 = System.nanoTime();
            int reward = simulate(cur);
            t2 = System.nanoTime(); simTime += (t2 - t1);

            t1 = System.nanoTime();
            backPropagate(cur, reward);
            t2 = System.nanoTime(); backTime += (t2 - t1);
        }

        long t1 = System.nanoTime(), total = t1 - t0;
        totalSelectTime   += selectTime;
        totalExpandTime   += expandTime;
        totalSimulateTime += simTime;
        totalBackpropTime += backTime;
        totalTime         += total;

        // pick the child with the highest empirical win‐rate (wins/playouts)
        Node<TicTacToe> best = Collections.max(
                node.children(),
                Comparator.comparing(c -> c.wins() / (double)c.playouts())
        );
        return new TicTacToeNode(best.state());
    }

    /**
     * UCT‐style select: unvisited first, then best UCT.
     */
    public static Node<TicTacToe> select(Node<TicTacToe> node) {
        while (!node.isLeaf()) {
            if (node.children().isEmpty()) return node;
            for (Node<TicTacToe> c : node.children())
                if (c.playouts() == 0) return c;
            node = bestUCT(node);
        }
        return node;
    }

    /**
     *
     * @param node
     * @return
     */
    private static Node<TicTacToe> bestUCT(Node<TicTacToe> node) {
        double lnN = Math.log(node.playouts());
        return Collections.max(
                node.children(),
                Comparator.comparing(c ->
                        (c.wins() / (double)c.playouts()) +
                                C * Math.sqrt(lnN / c.playouts())
                )
        );
    }

    /**
     * Expand all children, then pick one at random.
     */
    public static Node<TicTacToe> expand(Node<TicTacToe> node) {
        if (node.isLeaf()) return node;
        if (node.children().isEmpty()) {
            ((TicTacToeNode) node).expandAll();
        }
        List<Node<TicTacToe>> kids = new ArrayList<>(node.children());
        return kids.get(node.state().random().nextInt(kids.size()));
    }

    /**
     * Random rollout, returning 2=win,1=draw,0=loss for the start‐player.
     */
    public static int simulate(Node<TicTacToe> node) {
        State<TicTacToe> st = node.state();
        int pl = st.player();
        while (!st.isTerminal()) {
            Move<TicTacToe> m = st.chooseMove(st.player());
            st = st.next(m);
        }
        Optional<Integer> w = st.winner();
        if (w.isEmpty())       return 1;    // draw
        else if (w.get() == pl) return 2;   // win
        else                    return 0;   // loss
    }

    /**
     * Walk back up, updating visits & wins.
     */
    public static void backPropagate(Node<TicTacToe> node, int reward) {
        int rootPlayer = node.state().player();
        TicTacToeNode cur = (TicTacToeNode) node;
        while (cur != null) {
            cur.incrementPlayouts();
            // if this node’s player equals the original, add reward; else add (2−reward)
            int p = cur.state().player();
            cur.addWins(p == rootPlayer ? reward : 2 - reward);
            cur = cur.getParent();
        }
    }

    /**
     * Nicely render a TicTacToeState.
     * @param state
     * @return
     */
    public static String showBoard(State<TicTacToe> state) {
        return state.toString()
                .replace("-1", "_")
                .replace("1",  "X")
                .replace("0",  "O")
                .replace(",",  " ");
    }
}