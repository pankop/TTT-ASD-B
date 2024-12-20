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

import java.util.*;

/** AIPlayer using Minimax algorithm with alpha-beta pruning */
public class AIPlayerMinimax extends AIPlayer {

    /** Constructor with the given game board */
    public AIPlayerMinimax(Board board) {
        super(board);
    }

    /** Get next best move for computer. Return int[2] of {row, col} */
    @Override
    int[] move() {
        int[] result = minimax(2, mySeed, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return new int[] {result[1], result[2]};   // row, col
    }

    /** Minimax (recursive) at level of depth for maximizing or minimizing player
     with alpha-beta cut-off. Return int[3] of {score, row, col}  */
    private int[] minimax(int depth, Seed player, int alpha, int beta) {
        List<int[]> nextMoves = generateMoves();

        int score;
        int bestRow = -1;
        int bestCol = -1;

        if (nextMoves.isEmpty() || depth == 0 || hasWon(mySeed) || hasWon(oppSeed)) {
            score = evaluate();
            return new int[] {score, bestRow, bestCol};
        }

        for (int[] move : nextMoves) {
            cells[move[0]][move[1]].content = player;
            if (player == mySeed) {  // mySeed (computer) is maximizing player
                score = minimax(depth - 1, oppSeed, alpha, beta)[0];
                if (score > alpha) {
                    alpha = score;
                    bestRow = move[0];
                    bestCol = move[1];
                }
            } else {  // oppSeed is minimizing player
                score = minimax(depth - 1, mySeed, alpha, beta)[0];
                if (score < beta) {
                    beta = score;
                    bestRow = move[0];
                    bestCol = move[1];
                }
            }
            cells[move[0]][move[1]].content = Seed.NO_SEED;
            if (alpha >= beta) break;
        }
        return new int[] {(player == mySeed) ? alpha : beta, bestRow, bestCol};
    }

    /** Find all valid next moves.
     Return List of moves in int[2] of {row, col} or empty list if gameover */
    private List<int[]> generateMoves() {
        List<int[]> nextMoves = new ArrayList<int[]>();

        if (hasWon(mySeed) || hasWon(oppSeed)) {
            return nextMoves;   // return empty list
        }

        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == Seed.NO_SEED) {
                    nextMoves.add(new int[] {row, col});
                }
            }
        }
        return nextMoves;
    }

    /** The heuristic evaluation function for the current board */
    private int evaluate() {
        int score = 0;
        // Evaluate score for each of the 8 lines (3 rows, 3 columns, 2 diagonals)
        score += evaluateLine(0, 0, 0, 1, 0, 2);  // row 0
        score += evaluateLine(1, 0, 1, 1, 1, 2);  // row 1
        score += evaluateLine(2, 0, 2, 1, 2, 2);  // row 2
        score += evaluateLine(0, 0, 1, 0, 2, 0);  // col 0
        score += evaluateLine(0, 1, 1, 1, 2, 1);  // col 1
        score += evaluateLine(0, 2, 1, 2, 2, 2);  // col 2
        score += evaluateLine(0, 0, 1, 1, 2, 2);  // diagonal
        score += evaluateLine(0, 2, 1, 1, 2, 0);  // alternate diagonal
        return score;
    }

    /** The heuristic evaluation function for the given line of 3 cells */
    private int evaluateLine(int row1, int col1, int row2, int col2, int row3, int col3) {
        int score = 0;

        // First cell
        if (cells[row1][col1].content == mySeed) {
            score = 1;
        } else if (cells[row1][col1].content == oppSeed) {
            score = -1;
        }

        // Second cell
        if (cells[row2][col2].content == mySeed) {
            if (score == 1) {   // cell1 is mySeed
                score = 10;
            } else if (score == -1) {  // cell1 is oppSeed
                return 0;
            } else {  // cell1 is empty
                score = 1;
            }
        } else if (cells[row2][col2].content == oppSeed) {
            if (score == -1) { // cell1 is oppSeed
                score = -10;
            } else if (score == 1) { // cell1 is mySeed
                return 0;
            } else {  // cell1 is empty
                score = -1;
            }
        }

        // Third cell
        if (cells[row3][col3].content == mySeed) {
            if (score > 0) {  // cell1 and/or cell2 is mySeed
                score *= 10;
            } else if (score < 0) {  // cell1 and/or cell2 is oppSeed
                return 0;
            } else {  // cell1 and cell2 are empty
                score = 1;
            }
        } else if (cells[row3][col3].content == oppSeed) {
            if (score < 0) {  // cell1 and/or cell2 is oppSeed
                score *= 10;
            } else if (score > 1) {  // cell1 and/or cell2 is mySeed
                return 0;
            } else {  // cell1 and cell2 are empty
                score = -1;
            }
        }
        return score;
    }

    /** Returns true if thePlayer wins */
    private boolean hasWon(Seed thePlayer) {
        // Check if player has 3 in a row
        return (cells[0][0].content == thePlayer && cells[0][1].content == thePlayer && cells[0][2].content == thePlayer) || // row 0
                (cells[1][0].content == thePlayer && cells[1][1].content == thePlayer && cells[1][2].content == thePlayer) || // row 1
                (cells[2][0].content == thePlayer && cells[2][1].content == thePlayer && cells[2][2].content == thePlayer) || // row 2
                (cells[0][0].content == thePlayer && cells[1][0].content == thePlayer && cells[2][0].content == thePlayer) || // col 0
                (cells[0][1].content == thePlayer && cells[1][1].content == thePlayer && cells[2][1].content == thePlayer) || // col 1
                (cells[0][2].content == thePlayer && cells[1][2].content == thePlayer && cells[2][2].content == thePlayer) || // col 2
                (cells[0][0].content == thePlayer && cells[1][1].content == thePlayer && cells[2][2].content == thePlayer) || // diagonal
                (cells[0][2].content == thePlayer && cells[1][1].content == thePlayer && cells[2][0].content == thePlayer);   // alternate diagonal
    }
}