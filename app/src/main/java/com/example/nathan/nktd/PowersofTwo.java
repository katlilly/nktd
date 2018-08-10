package com.example.nathan.nktd;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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
    EditText answer;
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

    public void clearAnswer() {
        answer = (EditText) findViewById(R.id.answer);
        answer.setText("");
    }

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
        question.setText("2^" + exponent + " =");
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


    public void confirm() {

        correctAnswer = (int) Math.pow(2, exponent);
        Log.d("answer", "confirming");
        int submittedAnswer = Integer.parseInt(answer.getText().toString());
        Log.d("answer", answer.getText().toString());
        try {

            if (submittedAnswer == correctAnswer) {
                //response.setText("correct!");
                response.setText("correct " + correctAnswer);
                recognizerService.stopRecognition();
                correctSound.start();
                correctCount++;
                wrongCount = 0;
                if (correctCount == 10) {
                    levelp2++;
                    correctCount = 0;
                }
                clearAnswer();
                nextp2Question();

            } else {
                //response.setText("try again");
                response.setText("wrong " + exponent + " "+ correctAnswer);
                recognizerService.stopRecognition();
                tryagainSound.start();
                wrongCount++;
                correctCount = 0;
                if (wrongCount == 3) {
                    levelp2--;
                    wrongCount = 0;
                    newp2Question();
                }
                clearAnswer();
            }
        } catch (NumberFormatException e) {
            // comment out below line before beta release
            response.setText("number format exception");
            // shouldn't need to do anything here
            // do nothing in the case that there is no answer to submit
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
                    case "okay":
                        if(option_1.isChecked() || option_2.isChecked() || option_3.isChecked()
                                || option_4.isChecked()) {
                            confirm();
                        }
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
        answer = (EditText) findViewById(R.id.answer);
        response = (TextView) findViewById(R.id.response);
        whatIHeard = findViewById(R.id.speechResult);
        //answer.setText(numyesSounds);
        // set the first question
        nextp2Question();
        //question.setText("2^" + exponent + " =");

        answer.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    confirm();
                    return true;
                }
                return false;
            }
        }); // end answer listener

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

    public String getAnswerBoxValue() {
        return answer.getText().toString();
    }

    public void setAnswerBoxValue(String value) {
        answer.setText(value);
    }

    private String stringToDigit(String number) {
        switch (number) {
            case "zero": return "0";
            case "one": return "1";
            case "two": return "2";
            case "three": return "3";
            case "four": return "4";
            case "five": return "5";
            case "six": return "6";
            case "seven": return "7";
            case "eight": return "8";
            case "nine": return "9";
            default: return "0";
        }
    }
}


