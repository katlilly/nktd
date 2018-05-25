package com.example.nathan.nktd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nathan.nktd.interfaces.SpeechResultListener;

import java.util.Random;


public class TeragramActivity extends AppCompatActivity {

    String[] commands = new String[]{"too easy", "too hard", "new question"};

    TextView question;
    EditText answer;
    TextView response;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teragram);

        final MediaPlayer correctSound = MediaPlayer.create(this, R.raw.correct);
        final MediaPlayer tryagainSound = MediaPlayer.create(this, R.raw.tryagain);

        // create references to the text elements and buttons
        question = (TextView) findViewById(R.id.question);
        answer = (EditText) findViewById(R.id.answer);
        response = (TextView) findViewById(R.id.response);
        //answer.setText(numyesSounds);
        // set the first question
        question.setText("" + operand1 + " " + operation + " " + operand2 + " =");

        answer.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    int correctAnswer = 0;
                    if (operation == "+") correctAnswer = operand1 + operand2;
                    else if (operation == "-") correctAnswer = operand1 - operand2;
                    else if (operation == "*") correctAnswer = operand1 * operand2;

                    try {
                        int submittedAnswer = Integer.parseInt(answer.getText().toString());
                        if (submittedAnswer == correctAnswer) {
                            response.setText("correct!");
                            correctSound.start();
                            correctCount++;
                            wrongCount = 0;
                            if (correctCount == 10) {
                                level++;
                                correctCount = 0;
                            }
                            clearAnswer();
                            nextQuestion();
                        } else {
                            response.setText("try again");
                            tryagainSound.start();
                            wrongCount++;
                            correctCount = 0;
                            if (wrongCount == 2) {
                                level--;
                                wrongCount = 0;
                            }
                            clearAnswer();
                        }
                    } catch (NumberFormatException e) {
                        response.setText("try again");
                        // shouldn't need to do anything here
                        // do nothing in the case that there is no answer to submit
                    }
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
                level++;
                if (level > maxLevel) level = maxLevel;
                newQuestion();
                //operand1 = rand.nextInt(5 + level * 50);
                //operand2 = rand.nextInt(5 + level * 50);
                //question.setText("" + operand1 + " " + operation + " " + operand2 + " =");
            }
        });

        Button tooHard = (Button) findViewById(R.id.tooHard);
        tooHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                level--;
                if (level < 0) level = 0;
                newQuestion();
            }
        });


        Button plus = (Button) findViewById(R.id.plus);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operation = "+";
                newQuestion();
            }
        });

        Button minus = (Button) findViewById(R.id.minus);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operation = "-";
                newQuestion();
            }
        });

        Button times = (Button) findViewById(R.id.times);
        times.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operation = "*";
                newQuestion();
            }
        });



    }

}
