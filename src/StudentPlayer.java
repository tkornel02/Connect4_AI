import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class StudentPlayer extends Player {


    public StudentPlayer(int playerIndex, int[] boardSize, int nToConnect) {
        super(playerIndex, boardSize, nToConnect);
    }

    @Override
    public int step(Board board) {
        int depth = 5;

        return Minimax(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, playerIndex == 2).col;

    }

    public moveScore Minimax(Board board, int depth, int alpha, int beta, boolean maximizing) {
        if (board.gameEnded()) return new moveScore(-1, 10000000 * value(board));
        if (depth == 0) return new moveScore(-1, evalState2(board, 2));

        if (maximizing) { //Maximizing branch
            int value = Integer.MIN_VALUE;
            int col = board.getValidSteps().get(new Random().nextInt(board.getValidSteps().size()));
            for (Integer move : board.getValidSteps()) {
                int new_score = Minimax(result(board, move), depth - 1, alpha, beta, false).colscore;
                if (new_score > value) {
                    value = new_score;
                    col = move;
                }
                alpha = max(alpha, value);
                if (alpha >= beta) break;
            }
            return new moveScore(col, value);
        } else { //Minimizing branch
            int value = Integer.MAX_VALUE;
            int col = board.getValidSteps().get(new Random().nextInt(board.getValidSteps().size()));
            for (Integer move : board.getValidSteps()) {
                int new_score = Minimax(result(board, move), depth - 1, alpha, beta, true).colscore;
                if (new_score < value) {
                    value = new_score;
                    col = move;
                }
                beta = min(beta, value);
                if (alpha >= beta) break;
            }
            return new moveScore(col, value);
        }
    }

    public int value(Board board) {

        int winner = board.getWinner();
        if (winner != 0 && winner != this.playerIndex) return -1;
        else if (winner == 0) {
            return 0;
        } else return 1;
    }



    public Board result(Board board, int move) {
        Board resBoard = new Board(board);
        resBoard.step(Player(resBoard), move);
        return resBoard;
    }

    public int Player(Board board) {
        if (board.getLastPlayerIndex() == -1) return 1;
        else if (board.getLastPlayerIndex() == 1) return 2;
        else return 1;
    }
    //Scoring//
    public int wCount(int[] win, int sym) {
        int zeros = 0, ones = 0, twos = 0;
        for (Integer i : win) {
            if (i == 0) zeros++;
            else if (i == 1) ones++;
            else twos++;
        }
        if (sym == 0) return zeros;
        else if (sym == 1) return ones;
        else return twos;
    }

    public int evalWindow(int[] win, int idx) {
        int score = 0;
        int op_idx = 1;
        if (idx == 1) op_idx = 2;

        if (wCount(win, idx) == 4) score += 100;
        else if (wCount(win, idx) == 3 && wCount(win, 0) == 1) score += 5;
        else if (wCount(win, idx) == 2 && wCount(win, 0) == 2) score += 2;
        if (wCount(win, op_idx) == 4) score -= 100;
        else if (wCount(win, op_idx) == 3 && wCount(win, 0) == 1) score -= 4;
        else if (wCount(win, op_idx) == 2 && wCount(win, 0) == 2) score -= 2;

        return score;
    }

    public int evalState2(Board board, int idx) {
        int score = 0;

        if (board.gameEnded()) {
            if (board.getWinner() == 1) return -1;
            else if (board.getWinner() == 2) {
                return 1;
            } else return 0;
        }

        //Center
        int centerPiece = 0;
        for (int i = 0; i < 6; i++) {
            if (board.getState()[i][3] == idx) centerPiece++;
        }
        score += centerPiece * 3;

        //Horizontal
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 4; c++) {
                int[] window = new int[]{
                        board.getState()[r][c],
                        board.getState()[r][c + 1],
                        board.getState()[r][c + 2],
                        board.getState()[r][c + 3]
                };
                score += evalWindow(window, idx);
            }
        }

        //Vertical
        for (int c = 0; c < 7; c++) {
            for (int r = 0; r < 3; r++) {
                int[] window = new int[]{
                        board.getState()[r][c],
                        board.getState()[r + 1][c],
                        board.getState()[r + 2][c],
                        board.getState()[r + 3][c]
                };
                score += evalWindow(window, idx);
            }
        }

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 4; c++) {
                int[] window = new int[]{
                        board.getState()[r][c],
                        board.getState()[r + 1][c + 1],
                        board.getState()[r + 2][c + 2],
                        board.getState()[r + 3][c + 3]
                };
                score += evalWindow(window, idx);
            }
        }
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 4; c++) {
                int[] window = new int[]{
                        board.getState()[r + 3][c],
                        board.getState()[r + 2][c + 1],
                        board.getState()[r + 1][c + 2],
                        board.getState()[r][c + 3]
                };
                score += evalWindow(window, idx);
            }
        }

        return score;
    }

    class moveScore {
        public int col;
        public int colscore;

        public moveScore(int column, int score) {
            col = column;
            colscore = score;
        }
    }

}
