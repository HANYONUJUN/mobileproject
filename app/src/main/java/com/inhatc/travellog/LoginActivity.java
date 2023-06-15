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

public class LoginActivity extends AppCompatActivity {

    private EditText editID;
    private EditText editPassword;
    private Button btnLogin;
    private DBLogin dbLogin;
    private TextView signin;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);


        dbLogin = new DBLogin(this);

        editID = findViewById(R.id.editID);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.editbtn);
        signin = findViewById(R.id.signin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editID.getText().toString();
                String password = editPassword.getText().toString();

                if(!username.isEmpty() && !password.isEmpty()){
                    boolean isUserExists = dbLogin.getUser(username,password);
                    if(isUserExists){
                        Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        //로그인 성공 후 Captcha화면으로 이동
                        Intent intent = new Intent(LoginActivity.this, CaptchaActivity.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(LoginActivity.this, "아이디 또는 비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }


            }

        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 액티비티로 이동하는 코드 작성
                    Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(intent);
            }
        });
        
    }

}
