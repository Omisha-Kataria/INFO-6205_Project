package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MCTS {

    private static final int    NUM_SIMULATIONS = 5000;
    private static final double UCT_CONST       = 1.414;

    private final Node<TicTacToe> root;

    // Helpers to measure each phase
    private static class PhaseTimer {
        private long lastStart;
        private long lastDuration;
        private long totalDuration;

        void start() { lastStart = System.nanoTime(); }
        void stop()  { lastDuration = System.nanoTime() - lastStart; }
        void reset() { lastDuration = 0; }
        void accumulate() { totalDuration += lastDuration; }
        double totalMillis() { return totalDuration / 1_000_000.0; }
    }

    private final PhaseTimer selectTimer   = new PhaseTimer();
    private final PhaseTimer expandTimer   = new PhaseTimer();
    private final PhaseTimer rolloutTimer  = new PhaseTimer();
    private final PhaseTimer backpropTimer = new PhaseTimer();
    private final PhaseTimer totalTimer    = new PhaseTimer();

    public MCTS(Node<TicTacToe> root) {
        this.root = root;
    }

    public static void main(String[] args) {
        MCTS engine = new MCTS(new TicTacToeNode(new TicTacToe().start()));
        engine.run();
    }

    /** Run the game until terminal, printing the board each move. */
    public void run() {
        System.out.println("MCTS starting...");
        System.out.println(render(root.state()));
        Node<TicTacToe> node = root;
        while (!node.state().isTerminal()) {
            node = iterate(node);
            System.out.println(render(node.state()));
        }
        System.out.println("Game complete\n=== Phase Timing (ms) ===");
        System.out.printf(" Selection:   %.3f%n", selectTimer.totalMillis());
        System.out.printf(" Expansion:   %.3f%n", expandTimer.totalMillis());
        System.out.printf(" Rollout:     %.3f%n", rolloutTimer.totalMillis());
        System.out.printf(" Backprop:    %.3f%n", backpropTimer.totalMillis());
        System.out.printf(" Overall:     %.3f%n", totalTimer.totalMillis());
    }

    /**
     * Perform NUM_SIMULATIONS of Select→Expand→Rollout→Backpropagate,
     * accumulate timings, then return the child with highest visits.
     */
    private Node<TicTacToe> iterate(Node<TicTacToe> rootNode) {
        // reset per‐iteration timers
        selectTimer.reset();   expandTimer.reset();
        rolloutTimer.reset();  backpropTimer.reset();
        totalTimer.reset();

        totalTimer.start();
        for (int i = 0; i < NUM_SIMULATIONS; i++) {
            selectTimer.start();
            Node<TicTacToe> leaf = selectLeaf(rootNode);
            selectTimer.stop();

            expandTimer.start();
            Node<TicTacToe> next = expandNode(leaf);
            expandTimer.stop();

            rolloutTimer.start();
            int result = rollout(next);
            rolloutTimer.stop();

            backpropTimer.start();
            backpropagate(next, result);
            backpropTimer.stop();
        }
        totalTimer.stop();

        // accumulate into overall timers
        selectTimer.accumulate();
        expandTimer.accumulate();
        rolloutTimer.accumulate();
        backpropTimer.accumulate();
        totalTimer.accumulate();

        // pick the most‐visited child
        return Collections.max(
                rootNode.children(),
                Comparator.comparing(Node::playouts)
        );
    }

    /**
     * UCT‐style select: unvisited first, then highest UCT value.
     */
    private Node<TicTacToe> selectLeaf(Node<TicTacToe> start) {
        Node<TicTacToe> node = start;
        while (!node.isLeaf()) {
            List<Node<TicTacToe>> kids = (List<Node<TicTacToe>>) node.children();
            if (kids.isEmpty()) return node;
            // first unvisited
            for (Node<TicTacToe> c : kids) {
                if (c.playouts() == 0) return c;
            }
            // pull out the parent‐visit count into a final local
            final int parentPlays = node.playouts();
            node = Collections.max(
                    kids,
                    Comparator.comparing(c -> uctValue(c, parentPlays))
            );
        }
        return node;
    }

    private double uctValue(Node<TicTacToe> child, int parentPlayouts) {
        double winRate = child.wins() / (double) child.playouts();
        return winRate + UCT_CONST * Math.sqrt(Math.log(parentPlayouts) / child.playouts());
    }

    /** Expand all children on first call, otherwise pick one at random. */
    private Node<TicTacToe> expandNode(Node<TicTacToe> leaf) {
        if (leaf.isLeaf()) return leaf;
        if (leaf.children().isEmpty()) {
            ((TicTacToeNode) leaf).expandAll();
            if (leaf.children().isEmpty()) return leaf;
        }
        List<Node<TicTacToe>> kids = (List<Node<TicTacToe>>) leaf.children();
        return kids.get(leaf.state().random().nextInt(kids.size()));
    }

    /** Random playout: +1 win, 0 draw, -1 loss for the leaf’s player. */
    private int rollout(Node<TicTacToe> at) {
        State<TicTacToe> st = at.state();
        int pl = st.player();
        while (!st.isTerminal()) {
            Move<TicTacToe> m = st.chooseMove(st.player());
            st = st.next(m);
        }
        return st.winner()
                .map(w -> w == pl ? +1 : -1)
                .orElse(0);
    }

    /** Backpropagate the +1/0/-1 result, flipping sign by player turn. */
    private void backpropagate(Node<TicTacToe> node, int score) {
        int origin = node.state().player();
        TicTacToeNode cur = (TicTacToeNode) node;
        while (cur != null) {
            cur.incrementPlayouts();
            int cp = cur.state().player();
            cur.addWins(cp == origin ? score : -score);
            cur = cur.getParent();
        }
    }

    /** Neatly render the board replacing digits with X/O/_ */
    private String render(State<TicTacToe> s) {
        return s.toString()
                .replace("-1", "_")
                .replace("1",  "X")
                .replace("0",  "O")
                .replace(",",  " ");
    }
}