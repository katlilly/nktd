package com.example.nathan.nktd;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nathan.nktd.interfaces.RecognizedActivity;
import com.example.nathan.nktd.interfaces.SpeechResultListener;

import java.util.Random;


public class TeragramActivity extends RecognizedActivity {

    /* Response files */
    MediaPlayer correctSound;
    MediaPlayer tryagainSound;

    TextView question;
    EditText answer;
    TextView response;
    TextView whatIHeard;
    int level = 2;
    int maxLevel = 30;
    int correctCount = 0;
    int wrongCount = 0;

    // first question will be addition
    String operation = "+";

    // get random numbers for initial question
    Random rand = new Random();
    int operand1 = rand.nextInt(5 + level * 10);
    int operand2 = rand.nextInt(5 + level * 10);


     public void setOperands() {
        operand1 = rand.nextInt(5 + level * 10);
        operand2 = rand.nextInt(5 + level * 10);
        if (operand2 > operand1) {
            int temp = operand1;
            operand1 = operand2;
            operand2 = temp;
        }
    }

    public void timesTables() {
         operand1 = level+2;
         operand2 = rand.nextInt(13);
         operation = "*";
         response.setText("Lets practice " + operand1 + " times tables");
         question.setText("" + operand1 + " " + operation + " " + operand2 + " =");

    }

    public void clearAnswer() {
        answer = (EditText) findViewById(R.id.answer);
        answer.setText("");
    }

    // use this method when user asks for a different question
    public void newQuestion() {
        question = (TextView) findViewById(R.id.question);
        if (operation == "*") {
            timesTables();
        } else {
            setOperands();
            question.setText("" + operand1 + " " + operation + " " + operand2 + " =");
            response.setText("can you answer this one?");
        }
    }

    // use this method after a correct question
    public void nextQuestion() {
        question = (TextView) findViewById(R.id.question);
        setOperands();
        question.setText("" + operand1 + " " + operation + " " + operand2 + " =");
        // don't remove "correct" message unless new question is explicitly asked for
    }

    public void tooEasy() {
        level++;
        if (level > maxLevel) level = maxLevel;
        if (operation == "*") {
            timesTables();
        } else {
            newQuestion();
        }
    }

    public void tooHard() {
        level--;
        if (level < 0) level = 0;
        if (operation == "*") {
            timesTables();
        } else {
            newQuestion();
        }
    }

    public void addition() {
        operation = "+";
        newQuestion();
    }

    public void subtraction() {
        operation = "-";
        newQuestion();
    }

    public void multiplication() {
        timesTables();
        //operation = "*";
        //newQuestion();
    }

    public void confirm() {
        int correctAnswer = 0;
        if (operation == "+") correctAnswer = operand1 + operand2;
        else if (operation == "-") correctAnswer = operand1 - operand2;
        else if (operation == "*") correctAnswer = operand1 * operand2;
        Log.d("answer", "confirming");
        try {
            int submittedAnswer = Integer.parseInt(answer.getText().toString());
            Log.d("answer", answer.getText().toString());
            if (submittedAnswer == correctAnswer) {
                response.setText("correct!");
                recognizerService.stopRecognition();
                correctSound.start();
                correctCount++;
                wrongCount = 0;
                if (correctCount == 10) {
                    level++;
                    correctCount = 0;
                }
                clearAnswer();
                //recognizerService.startRecognition(recognizerService.TERAGRAM_SEARCH);
                if (operation == "*") {
                    timesTables();
                } else {
                    nextQuestion();
                }
            } else {
                response.setText("try again");
                recognizerService.stopRecognition();
                tryagainSound.start();
                wrongCount++;
                correctCount = 0;
                if (wrongCount == 3) {
                    level--;
                    wrongCount = 0;
                    newQuestion();
                }
                //recognizerService.startRecognition(recognizerService.TERAGRAM_SEARCH);
                clearAnswer();
            }
        } catch (NumberFormatException e) {
            response.setText("try again");
            // shouldn't need to do anything here
            // do nothing in the case that there is no answer to submit
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teragram);

        /* Recognizer Setup */
        recognizerBound = false;
        bindRecognizer(Recognizer.TERAGRAM_SEARCH);
        recognizerService.swapSearch(Recognizer.TERAGRAM_SEARCH);
        recognizerButton = findViewById(R.id.recognizerStatus);
        setButton(getIntent());

        recognizerListener = new SpeechResultListener() {
            @Override
            public void onSpeechResult() {
                String result = recognizerService.getResult();
                updateResultBox(result);
                if (recognizerService.getSearchName()
                        .equals(recognizerService.TERAGRAM_SEARCH)) {
                    switch (result) {
                        case "easier":
                            tooHard();
                            break;
                        case "harder":
                            tooEasy();
                            break;
                        case "new question":
                            newQuestion();
                            break;
                        case "addition":
                            addition();
                            break;
                        case "subtraction":
                            subtraction();
                            break;
                        case "multiplication":
                            multiplication();
                            break;
                        case "enter":
                            confirm();
                            break;
                        case "number":
                            break;
                        case "exit":
                            showExitDialog();
                            break;
                    }
                    // Will be in 'number' search here.
                } else {
                    String currentText = getAnswerBoxValue();

                    if (currentText.equals("0")) {
                        currentText = "";
                    }
                    switch (result) {
                        case "clear":
                            currentText = "";
                            setAnswerBoxValue(currentText);
                            break;
                        case "back":
                            if (currentText.length() > 0) {
                                currentText = currentText.subSequence(0, currentText.length() - 1)
                                        .toString();
                                setAnswerBoxValue(currentText);
                            }
                            break;
                        default:
                            currentText = currentText + stringToDigit(result);
                            setAnswerBoxValue(currentText);
                            break;
                    }
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
            public void onConfirmExit() {
                exitGame(null);
            }

            @Override
            public void onDenyExit() {
                dismissExitDialog(null);
            }
        };

        correctSound = MediaPlayer.create(this, R.raw.correct);
        correctSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                recognizerService.startRecognition(recognizerService.TERAGRAM_SEARCH);
            }
        });
        tryagainSound = MediaPlayer.create(this, R.raw.tryagain);
        tryagainSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                recognizerService.startRecognition(recognizerService.TERAGRAM_SEARCH);
            }
        });

        // create references to the text elements and buttons
        question = (TextView) findViewById(R.id.question);
        answer = (EditText) findViewById(R.id.answer);
        response = (TextView) findViewById(R.id.response);
        whatIHeard = findViewById(R.id.speechResult);
        //answer.setText(numyesSounds);
        // set the first question
        question.setText("" + operand1 + " " + operation + " " + operand2 + " =");

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
                newQuestion();
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

        Button plus = (Button) findViewById(R.id.plus);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addition();
            }
        });

        Button minus = (Button) findViewById(R.id.minus);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtraction();
            }
        });

        Button times = (Button) findViewById(R.id.times);
        times.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                multiplication();
            }
        });
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
