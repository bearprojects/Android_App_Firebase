package com.project.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    Button btnFinish;
    EditText edtAcc, edtPass, edtAgainPass;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle("註冊帳號");

        edtAcc = (EditText) findViewById(R.id.edtAcc);
        edtPass = (EditText) findViewById(R.id.edtPass);
        edtAgainPass = (EditText) findViewById(R.id.edtAgainPass);
        btnFinish = (Button) findViewById(R.id.btnFinish);

        btnFinish.setOnClickListener(btnClick);

        mAuth = FirebaseAuth.getInstance();

        edtAcc.requestFocusFromTouch();
    }

    Button.OnClickListener btnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            String account = edtAcc.getText().toString();
            String password = edtPass.getText().toString();
            String againPass = edtAgainPass.getText().toString();

            if(TextUtils.isEmpty(account)){
                edtAcc.setError("請輸入帳號");
                return;
            }
            if(TextUtils.isEmpty(password)){
                edtPass.setError("請輸入密碼");
                return;
            }
            if(TextUtils.isEmpty(againPass)){
                edtAgainPass.setError("請再次輸入密碼");
                return;
            }
            if(password.length() < 6){
                edtPass.setError("密碼至少6位數");
            }
            if(!againPass.equals(password)){
                edtAgainPass.setError("密碼輸入錯誤");
                return;
            }

            //註冊
            mAuth.createUserWithEmailAndPassword(account, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("LoginSuccess", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                user.sendEmailVerification();

                                new AlertDialog.Builder(RegisterActivity.this)
                                        .setTitle("註冊成功")
                                        .setMessage("點擊驗證信連結後，\n即可使用帳號登入")
                                        .setPositiveButton("確定",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        finish();
                                                    }
                                                })
                                        .show();
                            } else if (!task.isSuccessful()) {
                                // If sign in fails, display a message to the user.
                                Log.d("Loginfailed", "createUserWithEmail:failure", task.getException());
                                String fails = task.getException().getMessage();
                                new AlertDialog.Builder(RegisterActivity.this)
                                        .setTitle("註冊失敗")
                                        .setMessage(fails)
                                        .setPositiveButton("確定",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        edtAcc.setText("");
                                                        edtPass.setText("");
                                                        edtAgainPass.setText("");
                                                        edtAcc.requestFocusFromTouch();
                                                    }
                                                })
                                        .show();
                            }
                        }
                    });
        }
    };
}
