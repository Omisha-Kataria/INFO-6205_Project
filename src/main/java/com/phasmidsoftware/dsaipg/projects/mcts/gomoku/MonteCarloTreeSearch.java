package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import java.util.List;
import java.util.Random;

/**
 *
 */
public class MonteCarloTreeSearch {
    private int iterationLimit;

    public MonteCarloTreeSearch(int iterationLimit) {
        this.iterationLimit = iterationLimit;
    }

    /**
     *
     * @param rootState
     * @return
     */
    public GomokuMove findNextMove(GomokuState rootState) {
        MCTSNode rootNode = new MCTSNode(rootState, null, null);
        for (int i = 0; i < iterationLimit; i++) {
            // 1. Selection: Select a promising node.
            MCTSNode promisingNode = selectPromisingNode(rootNode);

            // 2. Expansion: Expand the node if it is not terminal.
            if (!promisingNode.getState().isTerminal()) {
                expandNode(promisingNode);
            }
            // 3. Simulation: Choose one random child (or the node itself if no children).
            MCTSNode nodeToExplore = promisingNode;
            if (!promisingNode.getChildren().isEmpty()) {
                nodeToExplore = promisingNode.getRandomChild();
            }
            int playoutResult = simulateRandomPlayout(nodeToExplore);

            // 4. Backpropagation: Propagate the simulation result up the tree.
            backPropagate(nodeToExplore, playoutResult);
        }
        MCTSNode bestChild = MCTSNode.bestChild(rootNode);
        return bestChild.getMove();
    }

    /**
     * Walk down the tree by selecting children with best UCT until a leaf is reached.
     * @param rootNode
     * @return
     */
    private MCTSNode selectPromisingNode(MCTSNode rootNode) {
        MCTSNode node = rootNode;
        while (!node.getChildren().isEmpty()) {
            node = node.selectChild();
        }
        return node;
    }

    /**
     * Expand the node by generating all possible moves from its state.
     * @param node
     */
    private void expandNode(MCTSNode node) {
        List<GomokuMove> possibleMoves = node.getUntriedMoves();
        for (GomokuMove move : possibleMoves) {
            GomokuState newState = node.getState().clone();
            newState.makeMove(move);
            MCTSNode childNode = new MCTSNode(newState, node, move);
            node.addChild(childNode);
        }
    }

    /**
     * Simulate a random playout from the given node until the game reaches a terminal state.
     * Returns the winning player (or EMPTY for a draw).
     * @param node
     * @return
     */
    private int simulateRandomPlayout(MCTSNode node) {
        GomokuState tempState = node.getState().clone();
        while (!tempState.isTerminal()) {
            List<GomokuMove> legalMoves = tempState.getLegalMoves();
            GomokuMove randomMove = legalMoves.get(new Random().nextInt(legalMoves.size()));
            tempState.makeMove(randomMove);
        }
        return tempState.checkWin();
    }

    /**
     * Backpropagate the simulation result. For a win, we add 1.0 to nodes corresponding to the winning
     * player; in case of a draw, 0.5 is added.
     * @param node
     * @param winner
     */
    private void backPropagate(MCTSNode node, int winner) {
        MCTSNode tempNode = node;
        while (tempNode != null) {
            if (winner == GomokuState.EMPTY) {
                tempNode.updateStats(0.5);
            } else if (tempNode.getPlayerNo() == winner) {
                tempNode.updateStats(1.0);
            } else {
                tempNode.updateStats(0.0);
            }
            tempNode = tempNode.getParent();
        }
    }
}
