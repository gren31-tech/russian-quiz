package com.russianquiz.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    private int totalScore = 0;
    private boolean[] completedTests = new boolean[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupTestButtons();
        updateTotalScore();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh scores when returning from quiz
        updateTotalScore();
    }

    private void setupTestButtons() {
        // Test 1 - Алфавит
        Button btn1 = findViewById(R.id.btn_test1);
        btn1.setOnClickListener(v -> startQuiz(0, "Алфавит и буквы", QuizData.TEST_ALPHABET));

        // Test 2 - Гласные и согласные
        Button btn2 = findViewById(R.id.btn_test2);
        btn2.setOnClickListener(v -> startQuiz(1, "Гласные и согласные", QuizData.TEST_VOWELS));

        // Test 3 - Правила написания
        Button btn3 = findViewById(R.id.btn_test3);
        btn3.setOnClickListener(v -> startQuiz(2, "Правила написания", QuizData.TEST_SPELLING));

        // Test 4 - Части речи
        Button btn4 = findViewById(R.id.btn_test4);
        btn4.setOnClickListener(v -> startQuiz(3, "Части речи", QuizData.TEST_PARTS_OF_SPEECH));

        // Test 5 - Предложения
        Button btn5 = findViewById(R.id.btn_test5);
        btn5.setOnClickListener(v -> startQuiz(4, "Предложения", QuizData.TEST_SENTENCES));
    }

    private void startQuiz(int testIndex, String testName, String[][] questions) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("test_index", testIndex);
        intent.putExtra("test_name", testName);
        intent.putExtra("questions", questions);
        startActivity(intent);
    }

    private void updateTotalScore() {
        // Load scores from SharedPreferences
        android.content.SharedPreferences prefs = getSharedPreferences("quiz_scores", MODE_PRIVATE);
        totalScore = 0;
        for (int i = 0; i < 5; i++) {
            totalScore += prefs.getInt("score_" + i, 0);
            completedTests[i] = prefs.getBoolean("completed_" + i, false);
        }

        TextView tvTotal = findViewById(R.id.tv_total_score);
        tvTotal.setText("Общий счёт: " + totalScore + " очков");

        // Update score badges on test buttons
        updateScoreBadge(R.id.tv_score1, 0);
        updateScoreBadge(R.id.tv_score2, 1);
        updateScoreBadge(R.id.tv_score3, 2);
        updateScoreBadge(R.id.tv_score4, 3);
        updateScoreBadge(R.id.tv_score5, 4);
    }

    private void updateScoreBadge(int textViewId, int testIndex) {
        TextView tv = findViewById(textViewId);
        android.content.SharedPreferences prefs = getSharedPreferences("quiz_scores", MODE_PRIVATE);
        if (prefs.getBoolean("completed_" + testIndex, false)) {
            int score = prefs.getInt("score_" + testIndex, 0);
            int maxScore = prefs.getInt("max_score_" + testIndex, 10);
            tv.setText("✓ " + score + "/" + maxScore + " очков");
            tv.setTextColor(getColor(R.color.green_score));
        } else {
            tv.setText("Не пройден");
            tv.setTextColor(getColor(R.color.gray_text));
        }
    }
}
