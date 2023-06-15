package com.inhatc.travellog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    TextView back;
    EditText id,pw,pw2,email;
    Button pwcheck, submit;

    private DBLogin dbLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        dbLogin = new DBLogin(this);

        //뒤로 가기 버튼
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed());

        //기입 항목
        id = findViewById(R.id.signID);
        pw = findViewById(R.id.signPW);
        pw2 = findViewById(R.id.signPW2);

        //비밀번호 확인 버튼
        pwcheck = findViewById(R.id.pwcheckbutton);
        pwcheck.setOnClickListener(v -> {
            if (pw.getText().toString().equals(pw2.getText().toString())) {
                pwcheck.setText("일치");
            } else {
                pwcheck.setText("불일치");
                Toast.makeText(SignupActivity.this, "비밀번호가 다릅니다.", Toast.LENGTH_LONG).show();
            }
        });

        //회원가입 완료 버튼
        submit = findViewById(R.id.signupbutton);
        submit.setOnClickListener(v -> {
            String idText = id.getText().toString();
            String pwText = pw.getText().toString();

            Log.d("VariableValues", "idText: " + idText);
            Log.d("VariableValues", "pwText: " + pwText);

            // 회원가입 정보를 SQLite 데이터베이스에 저장하는 코드 작성
            dbLogin.insertUser(idText, pwText);

            // 회원가입 완료 후 로그인 액티비티로 이동
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(SignupActivity.this, "회원가입 성공!", Toast.LENGTH_LONG).show();
        });

    }
}
