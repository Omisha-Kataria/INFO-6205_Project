// src/main/java/com/phasmidsoftware/dsaipg/projects/mcts/tictactoe/TicTacToeNode.java
package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import com.phasmidsoftware.dsaipg.projects.mcts.core.Move;

import java.util.*;

/**
 * A full‐featured node for MCTS in TicTacToe,
 * implementing all abstract methods of Node<G>
 * and providing parent links + per-node stats.
 */
public class TicTacToeNode implements Node<TicTacToe> {

    private final State<TicTacToe>          state;
    private final TicTacToeNode             parent;
    private final List<Node<TicTacToe>>     children = new ArrayList<>();
    private int                             wins;
    private final Move<TicTacToe>         move;
    private int                             playouts;

    /** Root‐node ctor. */
    public TicTacToeNode(State<TicTacToe> state) {
        this(state, null, null);
    }

    /** Full constructor */
    public TicTacToeNode(State<TicTacToe> state, TicTacToeNode parent, Move<TicTacToe> move) {
        this.state  = state;
        this.parent = parent;
        this.move   = move;
        initializeNodeData();
    }

    private void initializeNodeData() {
        if (state.isTerminal()) {
            // A terminal node counts as one playout:
            this.playouts = 1;
            // And seed wins = 2 if opener won, 1 if draw, 0 never occurs here
            Optional<Integer> w = state.winner();
            this.wins = w
                    .map(v -> v == state.game().opener() ? 2 : 1)
                    .orElse(1);
        }
    }

    // --- Node<TicTacToe> interface methods ---

    @Override public boolean isLeaf()                { return state.isTerminal(); }
    @Override public State<TicTacToe> state()        { return state;            }
    @Override public boolean white()                 { return state.player() == state.game().opener(); }
    @Override public Collection<Node<TicTacToe>> children() { return children; }
    @Override
    public void addChild(State<TicTacToe> st) {
        children.add(new TicTacToeNode(st, this,null));
    }
    @Override
    public void backPropagate() {
        // recompute wins/playouts from children
        wins = 0; playouts = 0;
        for (Node<TicTacToe> c : children) {
            wins     += c.wins();
            playouts += c.playouts();
        }
    }
    @Override public int wins()      { return wins;      }
    @Override public int playouts()  { return playouts;  }

    // --- MCTS helpers (not in Node<G>) ---

    /** Parent pointer for backprop. */
    public TicTacToeNode getParent() {
        return parent;
    }

    /** Increment this node’s playout count. */
    public void incrementPlayouts() {
        playouts++;
    }

    /** Add reward to this node’s win‐score. */
    public void addWins(int reward) {
        wins += reward;
    }

    public Move<TicTacToe> getMove() { return move; }
    /**
     * Expand all legal moves (one‐shot).  After this,
     * children() is non‐empty and backPropagate() seeds stats.
     */
    public void expandAll() {
        if (isLeaf() || !children.isEmpty()) return;
        for (Move<TicTacToe> m : state.moves(state.player())) {
            State<TicTacToe> st2 = state.next(m);
            // record the move in the child
            children.add(new TicTacToeNode(st2, this, m));
        }
        backPropagate();
    }
}