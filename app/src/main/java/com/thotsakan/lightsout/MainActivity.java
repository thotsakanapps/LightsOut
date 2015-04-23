package com.thotsakan.lightsout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.thotsakan.lightsout.ui.BoardView;

public final class MainActivity extends Activity {

    private BoardView boardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // game view
        boardView = (BoardView) findViewById(R.id.board_view);

        // ads
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // show instructions on startup
        showInstructionsOnStartup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem actionGameDifficulty = menu.findItem(R.id.action_game_difficulty);
        View gameDifficultyView = actionGameDifficulty.getActionView();
        if (gameDifficultyView != null) {
            SeekBar gameDifficulty = (SeekBar) gameDifficultyView.findViewById(R.id.game_difficulty);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String key = getString(R.string.game_difficulty_key);
            gameDifficulty.setProgress(preferences.getInt(key, getResources().getInteger(R.integer.game_difficulty_default)));
            gameDifficulty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String key = getString(R.string.game_difficulty_key);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(key, progress);
                    editor.apply();

                    boardView.newGame();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_game:
                boardView.newGame();
                break;
            case R.id.action_instructions:
                showInstructions();
                break;
            case R.id.action_rate_app:
                Intent rateAppIntent = new Intent(Intent.ACTION_VIEW);
                rateAppIntent.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + MainActivity.class.getPackage().getName()));
                startActivity(rateAppIntent);
                break;
            case R.id.action_other_apps:
                Intent otherAppsIntent = new Intent(Intent.ACTION_VIEW);
                otherAppsIntent.setData(Uri.parse("https://play.google.com/store/search?q=pub:" + getString(R.string.action_other_apps_publisher)));
                startActivity(otherAppsIntent);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInstructions() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.game_instructions);
        dialogBuilder.setView(LayoutInflater.from(this).inflate(R.layout.instructions, null));
        dialogBuilder.setNeutralButton(R.string.game_instructions_close_button_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogBuilder.show();
    }

    private void showInstructionsOnStartup() {
        String key = getString(R.string.game_instructions_on_startup_key);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!preferences.contains(key)) {
            showInstructions();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(key, getResources().getBoolean(R.bool.game_instructions_on_startup));
            editor.apply();
        }
    }
}
