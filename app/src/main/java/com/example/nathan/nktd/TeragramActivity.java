package com.example.nathan.nktd;

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
import android.widget.TextView;

import com.example.nathan.nktd.interfaces.SpeechResultListener;

import java.util.Random;


public class TeragramActivity extends AppCompatActivity {

    String[] commands = new String[]{"too easy", "too hard", "new question"};

    Context context;

    /* Recognizer-related. */
    private SpeechResultListener listener;

    private boolean recognizerBound = false;
    private Recognizer recognizerService;

    /* Response files */
    MediaPlayer correctSound;
    MediaPlayer tryagainSound;

    TextView question;
    EditText answer;
    TextView response;
    TextView whatIHeard;
    int level = 1;
    int maxLevel = 20;
    int correctCount = 0;
    int wrongCount = 0;

    // first question will be addition
    String operation = "+";

    // get random numbers for initial question
    Random rand = new Random();
    int operand1 = rand.nextInt(5 + level * 20);
    int operand2 = rand.nextInt(5 + level * 20);


     public void setOperands() {
        operand1 = rand.nextInt(5 + level * 20);
        operand2 = rand.nextInt(5 + level * 20);
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
        setOperands();
        question.setText("" + operand1 + " " + operation + " " + operand2 + " =");
        response.setText("can you answer this one?");
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
        newQuestion();
        //operand1 = rand.nextInt(5 + level * 50);
        //operand2 = rand.nextInt(5 + level * 50);
        //question.setText("" + operand1 + " " + operation + " " + operand2 + " =");
    }

    public void tooHard() {
        level--;
        if (level < 0) level = 0;
        newQuestion();
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
        operation = "*";
        newQuestion();
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
                recognizerService.startRecognition();
                nextQuestion();
            } else {
                response.setText("try again");
                recognizerService.stopRecognition();
                tryagainSound.start();
                wrongCount++;
                correctCount = 0;
                if (wrongCount == 2) {
                    level--;
                    wrongCount = 0;
                }
                recognizerService.startRecognition();
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

        correctSound = MediaPlayer.create(this, R.raw.correct);
        tryagainSound = MediaPlayer.create(this, R.raw.tryagain);

        /* Bind recognizer service */
        context = getApplicationContext();
        Intent intent = new Intent(this, Recognizer.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

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

    public void goBack() {
         Log.d("status", "goBack");
         finish();
    }

    /* Recognizer-related interactions should go here. */
    public ServiceConnection serviceConnection = new ServiceConnection() {

        Recognizer.RecognizerBinder binder;

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (Recognizer.RecognizerBinder) service;
            recognizerService = binder.getService();
            recognizerBound = true;
            /* Create listener and link it to recognizer. */
            recognizerService.setListener(new SpeechResultListener() {
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
                            case "exit":
                                Log.d("status", "exitHeard");
                                goBack();
                                break;
                            default:
                                currentText = currentText + stringToDigit(result);
                                setAnswerBoxValue(currentText);
                                break;
                        }
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            recognizerBound = false;
        }
    };

}
