package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;

import java.util.function.Supplier;

public class GomokuExperiment {
    private static final int[] GAME_COUNTS = {10, 20, 40, 80, 100};
    private static final int[] ITERATIONS = {100, 200, 500, 1000};

    public static void main(String[] args) {
        System.out.println("=== Gomoku MCTS Benchmark & Win Rates ===");
        for (int iters : ITERATIONS) {
            for (int games : GAME_COUNTS) {
                runExperiment(iters, games, true);
                runExperiment(iters, games, false);
            }
        }
    }

    private static void runExperiment(int iterations, int numGames, boolean mctsVsRandom) {
        String mode = mctsVsRandom ? "MCTS vs Random" : "MCTS vs MCTS";
        String desc = String.format("%s (iters=%d, games=%d)", mode, iterations, numGames);

        Supplier<GomokuGame> supplier = () -> {
            Player p1 = new MCTSPlayer(iterations);
            Player p2 = mctsVsRandom ? new RandomPlayer() : new MCTSPlayer(iterations);
            return new GomokuGame(p1, p2, 15);
        };

        long totalTime = 0;
        int p1Wins = 0, p2Wins = 0, draws = 0;
        for (int i = 0; i < numGames; i++) {
            GomokuGame game = supplier.get();
            long start = System.currentTimeMillis();
            int winner = game.play();
            totalTime += System.currentTimeMillis() - start;

            if (winner == GomokuState.PLAYER_ONE) {
                p1Wins++;
            } else if (winner == GomokuState.PLAYER_TWO) {
                p2Wins++;
            } else {
                draws++;
            }
        }

        double avgMs = totalTime / (double) numGames;
        System.out.printf(
                "%s -> Avg time: %.3f ms | P1(MCTS) wins: %d | P2(%s) wins: %d | Draws: %d%n",
                desc,
                avgMs,
                p1Wins,
                mctsVsRandom ? "Random" : "MCTS",
                p2Wins,
                draws
        );
    }
}
