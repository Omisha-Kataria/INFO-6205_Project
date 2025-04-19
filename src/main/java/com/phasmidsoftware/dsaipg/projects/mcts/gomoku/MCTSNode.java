package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 */
public class MCTSNode {
    private GomokuState state;
    private MCTSNode parent;
    private List<MCTSNode> children;
    private double wins;
    private int visits;
    private GomokuMove move; // The move that led to this state.
    private int playerNo;    // The player who made the move to get here.

    private static final double EXPLORATION_PARAMETER = Math.sqrt(2);

    /**
     *
     * @param state
     * @param parent
     * @param move
     */
    public MCTSNode(GomokuState state, MCTSNode parent, GomokuMove move) {
        this.state = state;
        this.parent = parent;
        this.move = move;
        this.children = new ArrayList<>();
        this.visits = 0;
        this.wins = 0.0;

        // For the root node, no move has been made. We set playerNo to be the opponent
        // (i.e. the last player who moved) so that in backpropagation rewards are assigned correctly.
        if (parent == null) {
            this.playerNo = GomokuState.getOpponent(state.getCurrentPlayer());
        } else {
            // The move that was played was made by the parent's current player.
            this.playerNo = parent.state.getCurrentPlayer();
        }
    }

    public GomokuState getState() {
        return state;
    }

    public MCTSNode getParent() {
        return parent;
    }

    public List<MCTSNode> getChildren() {
        return children;
    }

    public GomokuMove getMove() {
        return move;
    }

    public int getVisits() {
        return visits;
    }

    public double getWins() {
        return wins;
    }

    public int getPlayerNo() {
        return playerNo;
    }

    /**
     * Checking if all possible moves (from this state) have been tried.
     * @return
     */
    public boolean isFullyExpanded() {
        return children.size() == state.getLegalMoves().size();
    }

    public boolean isTerminalNode() {
        return state.isTerminal();
    }

    /**
     *
     * Returns the list of moves that have not been tried (i.e., not in one of the child nodes).
     */
    public List<GomokuMove> getUntriedMoves() {
        List<GomokuMove> possibleMoves = state.getLegalMoves();
        List<GomokuMove> triedMoves = new ArrayList<>();
        for (MCTSNode child : children) {
            triedMoves.add(child.getMove());
        }
        possibleMoves.removeAll(triedMoves);
        return possibleMoves;
    }

    public MCTSNode getRandomChild() {
        Random rand = new Random();
        return children.get(rand.nextInt(children.size()));
    }

    /**
     *
     * Uses the UCT formula to select the next child node for exploration.
     */
    public MCTSNode selectChild() {
        MCTSNode selected = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (MCTSNode child : children) {
            double uctValue = child.wins / (child.visits + 1e-6)
                    + EXPLORATION_PARAMETER * Math.sqrt(Math.log(this.visits + 1) / (child.visits + 1e-6));
            if (uctValue > bestValue) {
                selected = child;
                bestValue = uctValue;
            }
        }
        return selected;
    }

    public void addChild(MCTSNode child) {
        this.children.add(child);
    }

    /**
     *
     * Update the statistics for this node.
     */
    public void updateStats(double result) {
        visits++;
        wins += result;
    }

    /**
     * Returns the child node that has been visited the most.
     * @param node
     * @return
     */
    public static MCTSNode bestChild(MCTSNode node) {
        MCTSNode best = null;
        int maxVisits = -1;
        for (MCTSNode child : node.children) {
            if (child.visits > maxVisits) {
                best = child;
                maxVisits = child.visits;
            }
        }
        return best;
    }
}

