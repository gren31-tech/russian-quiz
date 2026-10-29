package com.russianquiz.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        int score = getIntent().getIntExtra("score", 0);
        int maxScore = getIntent().getIntExtra("max_score", 100);
        String testName = getIntent().getStringExtra("test_name");
        int totalQuestions = getIntent().getIntExtra("total_questions", 10);
        int correctAnswers = getIntent().getIntExtra("correct_answers", 0);

        TextView tvTestName = findViewById(R.id.tv_result_test_name);
        TextView tvScore = findViewById(R.id.tv_result_score);
        TextView tvDetails = findViewById(R.id.tv_result_details);
        TextView tvEmoji = findViewById(R.id.tv_result_emoji);
        TextView tvGrade = findViewById(R.id.tv_result_grade);
        Button btnBack = findViewById(R.id.btn_back_to_main);

        tvTestName.setText(testName);
        tvScore.setText(score + " / " + maxScore + " очков");
        tvDetails.setText("Правильных ответов: " + correctAnswers + " из " + totalQuestions);

        // Grade and emoji based on percentage
        double percentage = (double) score / maxScore * 100;
        if (percentage >= 90) {
            tvEmoji.setText("🏆");
            tvGrade.setText("Отлично! Ты молодец!");
            tvGrade.setTextColor(getColor(R.color.green_score));
        } else if (percentage >= 70) {
            tvEmoji.setText("😊");
            tvGrade.setText("Хорошо! Продолжай учиться!");
            tvGrade.setTextColor(getColor(R.color.blue_primary));
        } else if (percentage >= 50) {
            tvEmoji.setText("📚");
            tvGrade.setText("Неплохо, но нужно повторить!");
            tvGrade.setTextColor(getColor(R.color.orange_warning));
        } else {
            tvEmoji.setText("💪");
            tvGrade.setText("Не сдавайся, попробуй ещё раз!");
            tvGrade.setTextColor(getColor(R.color.red_wrong));
        }

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
