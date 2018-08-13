package com.example.nathan.nktd;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    Context context = this;

    /*
     * get random numbers for first question on startup
     * */
    Random rand = new Random();
    int operand1 = rand.nextInt(5 + level * 10);
    int operand2 = rand.nextInt(5 + level * 10);
    int exponent = rand.nextInt(level + 3);

     public void setOperands() {
        operand1 = rand.nextInt(5 + level * 10);
        operand2 = rand.nextInt(5 + level * 10);
        if (operand2 > operand1) {
            int temp = operand1;
            operand1 = operand2;
            operand2 = temp;
        }
    }


    public void clearAnswer() {
        answer = (EditText) findViewById(R.id.answer);
        answer.setText("");
    }


    // use this method when user asks for a different question
    public void newQuestion() {
        question = (TextView) findViewById(R.id.question);
         if (operation == "*") {
            operand1 = level+2;
            operand2 = rand.nextInt(13);
            response.setText("Lets practice " + operand1 + " times tables");
            question.setText("" + operand1 + " " + operation + " " + operand2 + " =");
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
    }


    // increase difficulty level
    public void tooEasy() {
        level++;
        if (level > maxLevel) level = maxLevel;
        if (operation == "*") {
            timesTables();
        } else {
            newQuestion();
        }
    }

    // decrease difficulty level
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


    public void timesTables() {
         operation = "*";
         newQuestion();
    }


    /*
    * start up powers of two multi-choice quiz in a new page
    * */
    public void launchPowersTwo(View view){
         recognizerService.swapSearch(Recognizer.POWERS_OF_TWO_SEARCH);
        Intent intent = new Intent(this, PowersofTwo.class);
        intent.putExtra("listening", recognizerListening);
        startActivity(intent);
    }

    public void showHelpDialog() {
         final Dialog helpDialog = new Dialog(this);
         helpDialog.setContentView(R.layout.teragram_help);
         recognizerService.swapSearch(Recognizer.HELP_SEARCH);
         recognizerService.setListener(new SpeechResultListener() {
             @Override
             public void onSpeechResult() {
                 String result = recognizerService.getResult();
                 if (result.equals("exit")) {
                     recognizerService.swapSearch(Recognizer.TERAGRAM_SEARCH);
                     recognizerService.setListener(recognizerListener);
                     helpDialog.dismiss();
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
             }

             @Override
             public void onDeny() {
             }
         });
         helpDialog.show();
    }


    /*
    * check for correct answer, and do automatic levelling up/down
    * */
    public void confirm() {
        int correctAnswer = 0;
        if (operation == "+") correctAnswer = operand1 + operand2;
        else if (operation == "-") correctAnswer = operand1 - operand2;
        else if (operation == "*") correctAnswer = operand1 * operand2;
        else if (operation == "^") correctAnswer = (int) Math.pow(2, exponent);
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
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teragram);

        /* Recognizer Setup */
        recognizerBound = false;
        bindRecognizer();
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
                        case "times tables":
                            timesTables();
                            break;
                        case "okay":
                            confirm();
                            break;
                        case "number":
                            break;
                        case "exit":
                            showExitDialog();
                            break;
                        case "powers of two":
                            launchPowersTwo(null);
                            break;
                        case "help":
                            showHelpDialog();
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
                recognizerListening = true;
            }

            @Override
            public void onStopRecognition() {
                recognizerButton.setImageDrawable(getResources().getDrawable(R.drawable.notlistening));
                recognizerListening = false;
            }

            @Override
            public void onNumberRecognition() {
                recognizerButton.setImageDrawable(getResources().getDrawable(R.drawable.listening_number));
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


        /*
         * set up media players for the sounds to play in response.
         * set the voice command recogniser to restart after response sounds have finished playing.
         * */
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

        /*
        * set up onClickListeners for each of the buttons
        * */
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
                timesTables();
            }
        });

        /* use spannable class to format 2^n correctly */
        final Button powers = (Button) findViewById(R.id.powers);
        //SpannableStringBuilder p = new SpannableStringBuilder("2n");
        //p.setSpan(new SuperscriptSpan(), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //p.setSpan(new RelativeSizeSpan(0.75f), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //powers.setText(p);
        powers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchPowersTwo(powers);
            }
        });

    }

    @Override
    public void onResume() {
         super.onResume();
         restartRecognizer();
    }


    /*
     * method for providing feedback to the user on what voice commands are being interpreted
     * */
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
