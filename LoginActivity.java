package com.project.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button btnOut, btnDelete;
    TextView textView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("登入中");

        btnOut = (Button)findViewById(R.id.btnOut);
        btnDelete = (Button)findViewById(R.id.btnDelete);
        textView = (TextView)findViewById(R.id.textView);

        btnOut.setOnClickListener(btnClick);
        btnDelete.setOnClickListener(btnClick);

        mAuth = FirebaseAuth.getInstance();
    }

    Button.OnClickListener btnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                //登出
                case R.id.btnOut:
                    FirebaseAuth.getInstance().signOut();

                    //Facebook SignOut
                    LoginManager.getInstance().logOut();

                    finish();
                    break;

                //刪除帳號
                case R.id.btnDelete:
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("刪除帳號")
                            .setMessage("確定刪除帳號嗎?")
                            .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    user.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("DelAccount", "User account deleted.");
                                                        Toast.makeText(LoginActivity.this, "帳號已刪除", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                    break;
            }
        }
    };
}
