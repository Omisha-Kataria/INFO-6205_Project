package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import static org.junit.Assert.*;
import org.junit.Test;

public class GomokuGameTest {

    @Test
    public void testMCTSvsRandom() {
        int mctsWins = 0;
        int games = 10;
        for (int i = 0; i < games; i++) {
            // MCTS plays as PLAYER_ONE and Random as PLAYER_TWO.
            Player mctsPlayer = new MCTSPlayer(1000);
            Player randomPlayer = new RandomPlayer();
            GomokuGame game = new GomokuGame(mctsPlayer, randomPlayer, 15);
            int winner = game.play();
            if (winner == GomokuState.PLAYER_ONE) {
                mctsWins++;
            }
        }
        System.out.println("MCTS wins " + mctsWins + " out of " + games + " games.");
        // We expect the MCTS algorithm to win the majority of games against a random player.
        assertTrue("MCTS should win more games against random", mctsWins > games / 2);
    }

    @Test
    public void testMCTSvsMCTS() {
        int player1Wins = 0;
        int player2Wins = 0;
        int draws = 0;
        int games = 10;
        for (int i = 0; i < games; i++) {
            Player mctsPlayer1 = new MCTSPlayer(1000);
            Player mctsPlayer2 = new MCTSPlayer(1000);
            GomokuGame game = new GomokuGame(mctsPlayer1, mctsPlayer2, 15);
            int winner = game.play();
            if (winner == GomokuState.PLAYER_ONE) {
                System.out.println("MCTS vs MCTS - Player1 wins the game: " + i);
                player1Wins++;
            } else if (winner == GomokuState.PLAYER_TWO) {
                System.out.println("MCTS vs MCTS - Player2 wins the game: " + i);
                player2Wins++;
            } else {
                draws++;
            }
        }
        System.out.println("MCTS vs MCTS - Player1 wins: " + player1Wins
                + ", Player2 wins: " + player2Wins + ", Draws: " + draws);
        // We expect the match between two MCTS players to be well-balanced(Sometimes, it will give advantage to the first player).
        assertTrue("Outcomes should be balanced", Math.abs(player1Wins - player2Wins) <= games / 2);
    }
}

