package com.inhatc.travellog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class CaptchaActivity extends AppCompatActivity {
    private static final int CAPTCHA_LENGTH = 4;
    TextView captchaTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_captcha);

        captchaTextView = findViewById(R.id.captchaTextView);
        String captcha = generateCaptcha();
        captchaTextView.setText(captcha);

        Button verifyButton = findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredCaptcha = ((EditText) findViewById(R.id.captchaEditText)).getText().toString();
                boolean isCaptchaValid = validateCaptcha(enteredCaptcha, captcha);

                if (isCaptchaValid) {
                    Toast.makeText(CaptchaActivity.this, "환영합니다.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CaptchaActivity.this, LocationDetailActivity.class));
                    finish();
                } else {
                    Toast.makeText(CaptchaActivity.this, "입력하신 문자와 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static String generateCaptcha() {
        StringBuilder captchaBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            int digit = random.nextInt(10); // 0부터 9까지 랜덤한 숫자 생성
            captchaBuilder.append(digit);
        }
        return captchaBuilder.toString();
    }

    public static boolean validateCaptcha(String enteredCaptcha, String correctCaptcha) {
        return enteredCaptcha.equals(correctCaptcha);
    }
}
