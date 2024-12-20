/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #12
 * 1 - 5026231082 - Naufal Zaky Nugraha
 * 2 - 5026231035 - Aldani Prasetyo
 * 3 - 5026231183 - Astrid Meilendra
 */

/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #12
 * 1 - 5026231082 - Naufal Zaky Nugraha
 * 2 - 5026231035 - Aldani Prasetyo
 * 3 - 5026231183 - Astrid Meilendra
 */

package TicTacToe;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Tic Tac Toe with Sound Effect");

        // Create game mode selection dialog
        Object[] options = {"Player vs Player", "Player vs AI"};
        int choice = JOptionPane.showOptionDialog(null,
                "Choose Game Mode",
                "Tic Tac Toe",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        // Create game instance with selected mode
        TTT game = new TTT(choice == 1); // true for AI mode, false for PvP
        game.play();
    }
}