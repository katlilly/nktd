package com.example.nathan.nktd.nktd2048;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.example.nathan.nktd.R;
import com.example.nathan.nktd.Recognizer;
import com.example.nathan.nktd.interfaces.RecognizedActivity;
import com.example.nathan.nktd.interfaces.SpeechResultListener;

public class MainActivity2048 extends RecognizedActivity {

    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String SCORE = "score";
    private static final String HIGH_SCORE = "high score temp";
    private static final String UNDO_SCORE = "undo score";
    private static final String CAN_UNDO = "can undo";
    private static final String UNDO_GRID = "undo";
    private static final String GAME_STATE = "game state";
    private static final String UNDO_GAME_STATE = "undo game state";
    private MainView view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new MainView(this);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        view.hasSaveState = settings.getBoolean("save_state", false);

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("hasState")) {
                load();
            }
        }
        setContentView(view);

        bindRecognizer();
        recognizerService.swapSearch(Recognizer.TWENTY_FORTY_EIGHT_SEARCH);

        recognizerListener = new SpeechResultListener() {
            @Override
            public void onSpeechResult() {
                String result = recognizerService.getResult();
                switch (result) {
                    case "up":
                        view.game.move(0);
                        break;
                    case "down":
                        view.game.move(2);
                        break;
                    case "left":
                        view.game.move(3);
                        break;
                    case "right":
                        view.game.move(1);
                        break;
                    case "exit":
                        showExitDialog();
                        break;
                    case "new game":
                        voiceNewGame();
                    case "back":
                        if(view.game.canUndo) {
                            view.game.revertUndoState();
                        }
                        break;
                }
            }

            @Override
            public void onStartRecognition() {
            }

            @Override
            public void onStopRecognition() {
            }

            @Override
            public void onNumberRecognition() {
            }

            @Override
            public void onConfirm() {
                exitGame(null);
            }

            @Override
            public void onDeny() {
                dismissExitDialog(null);
            }
        };
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            //Do nothing
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            view.game.move(2);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            view.game.move(0);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            view.game.move(3);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            view.game.move(1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("hasState", true);
        save();
        super.onSaveInstanceState(savedInstanceState);
    }

    protected void onPause() {
        super.onPause();
        save();
    }

    private void save() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        Tile[][] field = view.game.grid.field;
        Tile[][] undoField = view.game.grid.undoField;
        editor.putInt(WIDTH, field.length);
        editor.putInt(HEIGHT, field.length);
        for (int xx = 0; xx < field.length; xx++) {
            for (int yy = 0; yy < field[0].length; yy++) {
                if (field[xx][yy] != null) {
                    editor.putInt(xx + " " + yy, field[xx][yy].getValue());
                } else {
                    editor.putInt(xx + " " + yy, 0);
                }

                if (undoField[xx][yy] != null) {
                    editor.putInt(UNDO_GRID + xx + " " + yy, undoField[xx][yy].getValue());
                } else {
                    editor.putInt(UNDO_GRID + xx + " " + yy, 0);
                }
            }
        }
        editor.putLong(SCORE, view.game.score);
        editor.putLong(HIGH_SCORE, view.game.highScore);
        editor.putLong(UNDO_SCORE, view.game.lastScore);
        editor.putBoolean(CAN_UNDO, view.game.canUndo);
        editor.putInt(GAME_STATE, view.game.gameState);
        editor.putInt(UNDO_GAME_STATE, view.game.lastGameState);
        editor.commit();
    }

    protected void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        //Stopping all animations
        view.game.aGrid.cancelAnimations();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        for (int xx = 0; xx < view.game.grid.field.length; xx++) {
            for (int yy = 0; yy < view.game.grid.field[0].length; yy++) {
                int value = settings.getInt(xx + " " + yy, -1);
                if (value > 0) {
                    view.game.grid.field[xx][yy] = new Tile(xx, yy, value);
                } else if (value == 0) {
                    view.game.grid.field[xx][yy] = null;
                }

                int undoValue = settings.getInt(UNDO_GRID + xx + " " + yy, -1);
                if (undoValue > 0) {
                    view.game.grid.undoField[xx][yy] = new Tile(xx, yy, undoValue);
                } else if (value == 0) {
                    view.game.grid.undoField[xx][yy] = null;
                }
            }
        }

        view.game.score = settings.getLong(SCORE, view.game.score);
        view.game.highScore = settings.getLong(HIGH_SCORE, view.game.highScore);
        view.game.lastScore = settings.getLong(UNDO_SCORE, view.game.lastScore);
        view.game.canUndo = settings.getBoolean(CAN_UNDO, view.game.canUndo);
        view.game.gameState = settings.getInt(GAME_STATE, view.game.gameState);
        view.game.lastGameState = settings.getInt(UNDO_GAME_STATE, view.game.lastGameState);
    }

    public void voiceNewGame() {
        if (!view.game.gameLost()) {
            final AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            view.game.newGame();
                        }
                    })
                    .setNegativeButton("No", null)
                    .setTitle(R.string.reset_dialog_title)
                    .setMessage(R.string.reset_dialog_message)
                    .show();
            recognizerService.swapSearch(Recognizer.YESNO_SEARCH);
            final SpeechResultListener oldListener = recognizerListener;
            recognizerService.setListener(new SpeechResultListener() {
                @Override
                public void onSpeechResult() {
                }

                @Override
                public void onStartRecognition() {
                }

                @Override
                public void onStopRecognition() {
                }

                @Override
                public void onNumberRecognition() {
                }

                @Override
                public void onConfirm() {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                    recognizerService.setListener(oldListener);
                    recognizerService.swapSearch(Recognizer.TWENTY_FORTY_EIGHT_SEARCH);
                }
                @Override
                public void onDeny() {
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).performClick();
                    recognizerService.setListener(oldListener);
                    recognizerService.swapSearch(Recognizer.TWENTY_FORTY_EIGHT_SEARCH);
                }
            });
        } else {
            view.game.newGame();
        }
    }
}
