/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.projects.mcts.tictactoe;

import com.phasmidsoftware.dsaipg.projects.mcts.core.Node;
import com.phasmidsoftware.dsaipg.projects.mcts.core.State;
import java.util.Scanner;

/**
 * Class to represent a Monte Carlo Tree Search for TicTacToe.
 */
public class MCTS {

    public static void main(String[] args) {
        MCTS mcts = new MCTS(new TicTacToeNode(new TicTacToe().new TicTacToeState()));
        Node<TicTacToe> root = mcts.root;
        root=new TicTacToeNode(takeMove(root));
        System.out.println(root.state());
        root.explore();

        // This is where you process the MCTS to try to win the game.
    }

    public MCTS(Node<TicTacToe> root) {
        this.root = root;
    }

    private final Node<TicTacToe> root;

    public static State takeMove(Node<TicTacToe> root){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter i and j:");
        int i=sc.nextInt();
        int j=sc.nextInt();
        if(i>2 || j>2){
            System.out.println("Please enter valid co ordinates");
        }
        TicTacToe.TicTacToeMove userMove = new TicTacToe.TicTacToeMove(1,i,j);
        return  root.state().next(userMove);
    }

}