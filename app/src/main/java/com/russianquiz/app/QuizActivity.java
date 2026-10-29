package com.russianquiz.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private int testIndex;
    private String testName;
    private String[][] questions;
    private int currentQuestion = 0;
    private int score = 0;
    private int totalQuestions;

    private TextView tvQuestionNum, tvQuestion, tvTimer, tvScore;
    private Button[] answerButtons = new Button[4];
    private ProgressBar progressBar;
    private CountDownTimer timer;
    private boolean answered = false;

    private static final int TIME_PER_QUESTION = 20; // seconds
    private int timeLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        testIndex = getIntent().getIntExtra("test_index", 0);
        testName = getIntent().getStringExtra("test_name");

        // Get questions based on test index
        switch (testIndex) {
            case 0: questions = QuizData.TEST_ALPHABET; break;
            case 1: questions = QuizData.TEST_VOWELS; break;
            case 2: questions = QuizData.TEST_SPELLING; break;
            case 3: questions = QuizData.TEST_PARTS_OF_SPEECH; break;
            case 4: questions = QuizData.TEST_SENTENCES; break;
        }

        totalQuestions = questions.length;

        // Setup UI
        tvQuestionNum = findViewById(R.id.tv_question_num);
        tvQuestion = findViewById(R.id.tv_question);
        tvTimer = findViewById(R.id.tv_timer);
        tvScore = findViewById(R.id.tv_current_score);
        progressBar = findViewById(R.id.progress_bar);

        answerButtons[0] = findViewById(R.id.btn_answer1);
        answerButtons[1] = findViewById(R.id.btn_answer2);
        answerButtons[2] = findViewById(R.id.btn_answer3);
        answerButtons[3] = findViewById(R.id.btn_answer4);

        TextView tvTestTitle = findViewById(R.id.tv_test_title);
        tvTestTitle.setText(testName);

        progressBar.setMax(totalQuestions);

        showQuestion();
    }

    private void showQuestion() {
        if (currentQuestion >= totalQuestions) {
            finishQuiz();
            return;
        }

        answered = false;
        String[] q = questions[currentQuestion];
        // q[0] = question, q[1..4] = answers, q[5] = correct answer

        tvQuestionNum.setText("Вопрос " + (currentQuestion + 1) + " из " + totalQuestions);
        tvQuestion.setText(q[0]);
        tvScore.setText("Очки: " + score);
        progressBar.setProgress(currentQuestion);

        // Set answers
        String[] answers = {q[1], q[2], q[3], q[4]};
        String correctAnswer = q[5];

        for (int i = 0; i < 4; i++) {
            int finalI = i;
            String ans = answers[i];
            answerButtons[i].setText(ans);
            answerButtons[i].setBackgroundResource(R.drawable.btn_answer_default);
            answerButtons[i].setTextColor(Color.parseColor("#333333"));
            answerButtons[i].setEnabled(true);
            answerButtons[i].setOnClickListener(v -> {
                if (!answered) {
                    checkAnswer(ans, correctAnswer);
                }
            });
        }

        startTimer();
    }

    private void startTimer() {
        if (timer != null) timer.cancel();
        timeLeft = TIME_PER_QUESTION;
        tvTimer.setTextColor(Color.parseColor("#2196F3"));

        timer = new CountDownTimer(TIME_PER_QUESTION * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = (int) (millisUntilFinished / 1000);
                tvTimer.setText("⏱ " + timeLeft + "с");
                if (timeLeft <= 5) {
                    tvTimer.setTextColor(Color.RED);
                }
            }

            @Override
            public void onFinish() {
                if (!answered) {
                    tvTimer.setText("⏱ 0с");
                    checkAnswer("", questions[currentQuestion][5]);
                }
            }
        }.start();
    }

    private void checkAnswer(String selected, String correct) {
        answered = true;
        if (timer != null) timer.cancel();

        // Highlight correct/wrong answers
        String[] answers = {
            questions[currentQuestion][1],
            questions[currentQuestion][2],
            questions[currentQuestion][3],
            questions[currentQuestion][4]
        };

        for (int i = 0; i < 4; i++) {
            answerButtons[i].setEnabled(false);
            if (answers[i].equals(correct)) {
                answerButtons[i].setBackgroundResource(R.drawable.btn_answer_correct);
                answerButtons[i].setTextColor(Color.WHITE);
            } else if (answers[i].equals(selected)) {
                answerButtons[i].setBackgroundResource(R.drawable.btn_answer_wrong);
                answerButtons[i].setTextColor(Color.WHITE);
            }
        }

        if (selected.equals(correct)) {
            score += 10; // 10 points per correct answer
            tvScore.setText("Очки: " + score + " ✓");
        }

        // Move to next question after delay
        tvQuestionNum.postDelayed(() -> {
            currentQuestion++;
            showQuestion();
        }, 1500);
    }

    private void finishQuiz() {
        // Save score
        SharedPreferences prefs = getSharedPreferences("quiz_scores", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("score_" + testIndex, score);
        editor.putInt("max_score_" + testIndex, totalQuestions * 10);
        editor.putBoolean("completed_" + testIndex, true);
        editor.apply();

        // Go to result screen
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("max_score", totalQuestions * 10);
        intent.putExtra("test_name", testName);
        intent.putExtra("total_questions", totalQuestions);
        intent.putExtra("correct_answers", score / 10);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}
