package com.example.nathan.nktd;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.nathan.nktd.interfaces.RecognizedActivity;
import com.example.nathan.nktd.interfaces.SpeechResultListener;

import java.util.Random;

public class PowersofTwo extends RecognizedActivity {
    /* Response files */
    MediaPlayer correctSound;
    MediaPlayer tryagainSound;

    TextView question;
    TextView response;
    TextView whatIHeard;
    RadioButton option_1;
    RadioButton option_2;
    RadioButton option_3;
    RadioButton option_4;
    int levelp2 = 4;
    int maxLevel = 20;
    int correctCount = 0;
    int wrongCount = 0;
    int correctAnswer;
    int correctMC; // position of correct multi choice answer


    // get random numbers for initial question
    Random rand = new Random();
    public int exponent = rand.nextInt(levelp2 + 1);
    //correctAnswer = (int) Math.pow(2, exponent);

    public void setMultiChoice() {
        // chose where to put correct answer
        int pos;
        if (exponent == 0) pos = 1;
        else if (exponent == 1) pos = rand.nextInt(2) + 1;
        else if (exponent == 2) pos = rand.nextInt(3) + 1;
        else pos = rand.nextInt(4) + 1;
//        while (pos > (exponent - 1)) {
//            pos = rand.nextInt(4) + 1;
//        }
        correctMC = pos;
        option_1 = (RadioButton) findViewById(R.id.radio_1);
        option_2 = (RadioButton) findViewById(R.id.radio_2);
        option_3 = (RadioButton) findViewById(R.id.radio_3);
        option_4 = (RadioButton) findViewById(R.id.radio_4);
        switch (pos) {
            case 1:
                option_1.setText("1: " + (int) Math.pow(2, exponent));
                option_2.setText("2: " + (int) Math.pow(2, exponent+1));
                option_3.setText("3: " + (int) Math.pow(2, exponent+2));
                option_4.setText("4: " + (int) Math.pow(2, exponent+3));
                break;
            case 2:
                option_1.setText("1: " + (int) Math.pow(2, exponent-1));
                option_2.setText("2: " + (int) Math.pow(2, exponent));
                option_3.setText("3: " + (int) Math.pow(2, exponent+1));
                option_4.setText("4: " + (int) Math.pow(2, exponent+2));
                break;
            case 3:
                option_1.setText("1: " + (int) Math.pow(2, exponent-2));
                option_2.setText("2: " + (int) Math.pow(2, exponent-1));
                option_3.setText("3: " + (int) Math.pow(2, exponent));
                option_4.setText("4: " + (int) Math.pow(2, exponent+1));
                break;
            case 4:
                option_1.setText("1: " + (int) Math.pow(2, exponent-3));
                option_2.setText("2: " + (int) Math.pow(2, exponent-2));
                option_3.setText("3: " + (int) Math.pow(2, exponent-1));
                option_4.setText("4: " + (int) Math.pow(2, exponent));
                break;
        }


    }

    // use this method when user asks for a different question
    public void newp2Question() {
        exponent = rand.nextInt(levelp2 + 1);
        correctAnswer = (int) Math.pow(2, exponent);
        question = (TextView) findViewById(R.id.question);
        SpannableStringBuilder q = new SpannableStringBuilder("2" + exponent + " =");
        if (exponent < 10) {
            q.setSpan(new SuperscriptSpan(), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            q.setSpan(new RelativeSizeSpan(0.75f), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            q.setSpan(new SuperscriptSpan(), 1, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            q.setSpan(new RelativeSizeSpan(0.75f), 1, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        question.setText(q);
        response.setText("can you answer this one?");
        setMultiChoice();

    }

    // use this method after a correct question
    public void nextp2Question() {
        exponent = rand.nextInt(levelp2 + 1);
        correctAnswer = (int) Math.pow(2, exponent);
        question = (TextView) findViewById(R.id.question);
        question.setText("2^" + exponent + " =");
        // don't remove "correct" message unless new question is explicitly asked for
    }

    public void tooEasy() {
        levelp2++;
        if (levelp2 > maxLevel) levelp2 = maxLevel;
        newp2Question();
    }

    public void tooHard() {
        levelp2--;
        if (levelp2 < 4) levelp2 = 4;
        newp2Question();
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_1:
                if (checked)
                    if (correctMC == 1) {
                        option_1.setText("correct!");
                        option_1.setChecked(false);
                    } else {
                        option_1.setText("nope!");
                        option_1.setChecked(false);
                    }
                    break;
            case R.id.radio_2:
                if (checked)
                    if (correctMC == 2) {
                        option_2.setText("correct!");
                        option_2.setChecked(false);
                    } else {
                        option_2.setText("nope!");
                        option_2.setChecked(false);
                    }
                    //
                    break;
            case R.id.radio_3:
                if (checked)
                    if (correctMC == 3) {
                        option_3.setText("correct!");
                        option_3.setChecked(false);
                    } else {
                        option_3.setText("nope!");
                        option_3.setChecked(false);
                    }
                    // option c
                    break;
            case R.id.radio_4:
                if (checked)
                    if (correctMC == 4) {
                        option_4.setText("correct!");
                        option_4.setChecked(false);
                    } else {
                        option_4.setText("nope!");
                        option_4.setChecked(false);
                    }
                    break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_powersoftwo);

        /* Recognizer Setup */
        recognizerBound = false;
        bindRecognizer();
        recognizerButton = findViewById(R.id.recognizerStatus);
        setButton(getIntent());

        recognizerListener = new SpeechResultListener() {
            @Override
            public void onSpeechResult() {
                String result = recognizerService.getResult();
                updateResultBox(result);

                switch (result) {
                    case "easier":
                        tooHard();
                        break;
                    case "harder":
                        tooEasy();
                        break;
                    case "new question":
                        newp2Question();
                        break;
                    case "one":
                        option_1.performClick();
                        break;
                    case "two":
                        option_2.performClick();
                        break;
                    case "three":
                        option_3.performClick();
                        break;
                    case "four":
                        option_4.performClick();
                        break;
                    case "exit":
                        showExitDialog();
                        break;


                }
            }

            @Override
            public void onStartRecognition() {
                recognizerButton.setImageDrawable(getResources().getDrawable(R.drawable.listening));
            }

            @Override
            public void onStopRecognition() {
                recognizerButton.setImageDrawable(getResources().getDrawable(R.drawable.notlistening));
            }

            @Override
            public void onNumberRecognition() {
                recognizerButton.setImageDrawable(getResources().getDrawable(R.drawable.listening_number));
            }

            @Override
            public void onConfirm() {
                recognizerService.swapSearch(Recognizer.TERAGRAM_SEARCH);
                finish();
            }

            @Override
            public void onDeny() {
                dismissExitDialog(null);
            }
        };

        correctSound = MediaPlayer.create(this, R.raw.correct);
        correctSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                recognizerService.startRecognition();
            }
        });
        tryagainSound = MediaPlayer.create(this, R.raw.tryagain);
        tryagainSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                recognizerService.startRecognition();
            }
        });

        // create references to the text elements and buttons
        question = (TextView) findViewById(R.id.question);
        response = (TextView) findViewById(R.id.response);
        whatIHeard = findViewById(R.id.speechResult);
        //answer.setText(numyesSounds);
        // set the first question
        nextp2Question();
        //question.setText("2^" + exponent + " =");

        Button newQuestion = (Button) findViewById(R.id.newQuestion);
        newQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newp2Question();
            }
        });

        Button tooEasy = (Button) findViewById(R.id.tooEasy);
        tooEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tooEasy();
            }
        });

        Button tooHard = (Button) findViewById(R.id.tooHard);
        tooHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tooHard();
            }
        });


        option_1 = (RadioButton) findViewById(R.id.radio_1);
        option_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        });

        RadioButton option_2 = (RadioButton) findViewById(R.id.radio_2);
        option_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        });

        RadioButton option_3 = (RadioButton) findViewById(R.id.radio_3);
        option_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        });

        RadioButton option_4 = (RadioButton) findViewById(R.id.radio_4);
        option_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        });

        newp2Question();
    }

    public void updateResultBox(String string) {
        if (null == whatIHeard) {
            Log.d("status", "whatIHeard null");
        } else {
            whatIHeard.setText(string);
        }
    }

}


