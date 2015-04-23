package com.thotsakan.lightsout.ui;

final class Board {
    private boolean[][] cells;

    private int boardSize;

    public void init(int difficulty) {
        boardSize = 3 + difficulty;
        cells = new boolean[boardSize][boardSize];
        do {
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    cells[i][j] = Math.random() > 0.5f;
                }
            }
        } while (areLightsOut());
    }

    public void toggle(int row, int col) {
        cells[row][col] = !cells[row][col];

        if (row > 0) {
            cells[row - 1][col] = !cells[row - 1][col];
        }
        if (row < boardSize - 1) {
            cells[row + 1][col] = !cells[row + 1][col];
        }
        if (col > 0) {
            cells[row][col - 1] = !cells[row][col - 1];
        }
        if (col < boardSize - 1) {
            cells[row][col + 1] = !cells[row][col + 1];
        }
    }

    public boolean areLightsOut() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (cells[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isCellValid(int row, int col) {
        return -1 < row && row < boardSize && -1 < col && col < boardSize;
    }

    public boolean[][] getCells() {
        return cells;
    }

    public int getBoardSize() {
        return boardSize;
    }
}
