package com.thotsakan.lightsout.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.thotsakan.lightsout.MainActivity;
import com.thotsakan.lightsout.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

public final class BoardView extends View {

    private Resources resources;

    private int currentMoves;

    private int gameDifficulty;

    private Paint borderPaint;

    private Paint lightOffPaint;

    private int lightOnColor;

    private Paint lightOnPaint;

    private Board board;

    private boolean gameOver;

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        resources = getResources();

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.DKGRAY);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2);

        lightOffPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lightOffPaint.setColor(Color.LTGRAY);
        lightOffPaint.setStyle(Paint.Style.FILL);

        lightOnPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lightOnPaint.setStyle(Paint.Style.FILL);

        board = new Board();
        newGame();
    }

    public void newGame() {
        // initialize the board
        gameDifficulty = getGameDifficulty();
        board.init(gameDifficulty);

        // set member variables
        currentMoves = 0;
        Random random = new Random(System.currentTimeMillis());
        lightOnColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        gameOver = false;

        // force draw the view
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int boardSize = board.getBoardSize();
        float cellLen = Math.min(getWidth(), getHeight()) / boardSize;

        float boardLen = boardSize * cellLen;
        float originX = (getWidth() - boardLen) / 2;
        float originY = (getHeight() - boardLen) / 2;

        boolean[][] cells = board.getCells();
        float roundRectRadius = 5;
        float originOffset = cellLen / 2;
        float gradientRadius = cellLen * 3.0f;

        for (int i = 0; i < boardSize; i++) {
            float topX = i * cellLen + originX;
            float bottomX = topX + cellLen;
            float gradientX = topX + originOffset;

            for (int j = 0; j < boardSize; j++) {
                float topY = j * cellLen + originY;
                float bottomY = topY + cellLen;

                RectF rect = new RectF(topX, topY, bottomX, bottomY);
                rect.inset(2, 2);

                if (cells[i][j]) {
                    float gradientY = topY + originOffset;
                    lightOnPaint.setShader(new RadialGradient(gradientX, gradientY, gradientRadius, lightOnColor, Color.BLACK, Shader.TileMode.CLAMP));
                    canvas.drawRoundRect(rect, roundRectRadius, roundRectRadius, lightOnPaint);
                } else {
                    canvas.drawRoundRect(rect, roundRectRadius, roundRectRadius, lightOffPaint);
                }
                canvas.drawRoundRect(rect, roundRectRadius, roundRectRadius, borderPaint);
            }
        }

        showMoves();

        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!gameOver) {
            final int boardSize = board.getBoardSize();
            final float cellLen = Math.min(getWidth(), getHeight()) / boardSize;

            final float boardLen = boardSize * cellLen;
            final float originX = (getWidth() - boardLen) / 2;
            final float originY = (getHeight() - boardLen) / 2;

            final float touchX = event.getX();
            final float touchY = event.getY();

            if (originX <= touchX && touchX <= originX + boardLen && originY <= touchY && touchY <= originY + boardLen) {

                int row = (int) ((touchX - originX) / cellLen);
                int col = (int) ((touchY - originY) / cellLen);

                if (board.isCellValid(row, col)) {
                    ++currentMoves;
                    board.toggle(row, col);
                    invalidate();

                    if (board.areLightsOut()) {
                        Toast toast = Toast.makeText(getContext(), "Congratulations! All lights are off.", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();

                        updateBestMoves();
                        gameOver = true;
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }

    private void showMoves() {
        NumberFormat scoreFormat = new DecimalFormat("000");

        // show best moves
        String key = resources.getString(R.string.best_moves_key) + "_" + gameDifficulty;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        final int bestMoves = preferences.getInt(key, 0);
        TextView bestMovesView = (TextView) ((MainActivity) getContext()).findViewById(R.id.best_moves);
        bestMovesView.setText("BEST: " + scoreFormat.format(bestMoves));

        // show current moves
        TextView currentMovesView = (TextView) ((MainActivity) getContext()).findViewById(R.id.current_moves);
        currentMovesView.setText("MOVES: " + scoreFormat.format(currentMoves));
    }

    private void updateBestMoves() {
        String key = resources.getString(R.string.best_moves_key) + "_" + gameDifficulty;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int currentBest = preferences.getInt(key, 0);
        if (currentBest == 0 || currentBest > currentMoves) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(key, currentMoves);
            editor.apply();
        }
    }

    private int getGameDifficulty() {
        String key = resources.getString(R.string.game_difficulty_key);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return preferences.getInt(key, resources.getInteger(R.integer.game_difficulty_default));
    }
}
