package com.project.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPwActivity extends AppCompatActivity {

    TextView edtForgetEmail;
    Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pw);

        btnFinish = (Button)findViewById(R.id.btnFinish);
        edtForgetEmail = (TextView)findViewById(R.id.edtForgetEmail);
        btnFinish.setOnClickListener(btnClick);

        //ActionBar
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            ((ActionBar) actionBar).setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle("忘記密碼");
    }

    Button.OnClickListener btnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            FirebaseAuth auth = FirebaseAuth.getInstance();

            String emailAddress = edtForgetEmail.getText().toString();

            if(TextUtils.isEmpty(emailAddress)){
                edtForgetEmail.setError("請輸入帳號");
                return;
            }

            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("sent", "Email sent.");
                                new AlertDialog.Builder(ForgetPwActivity.this)
                                        .setTitle("重設密碼")
                                        .setMessage("重設密碼郵件已送出，\n修改完後使用新密碼登入")
                                        .setPositiveButton("確定",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        finish();
                                                    }
                                                })
                                        .show();
                            } else{
                                String fails = task.getException().getMessage();
                                new android.app.AlertDialog.Builder(ForgetPwActivity.this)
                                        .setTitle("重設密碼失敗")
                                        .setMessage(fails)
                                        .setPositiveButton("確定",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        edtForgetEmail.setText("");
                                                    }
                                                })
                                        .show();
                            }
                        }
                    });
        }
    };
}
