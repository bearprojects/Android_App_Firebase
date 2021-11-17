package com.project.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Button btnLogin, btnForget, btnRegister, btnFB, btnGoogle;
    EditText edtEmail, edtPassword;
    ToggleButton toggleButton;

    private FirebaseAuth mAuth;

    //Google
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;

    //FB
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("會員登入");

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnForget = (Button)findViewById(R.id.btnForget);
        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnFB = (Button)findViewById(R.id.btnFB);
        btnGoogle = (Button)findViewById(R.id.btnGoogle);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtPassword = (EditText)findViewById(R.id.edtPassword);
        toggleButton = (ToggleButton)findViewById(R.id.toggleButton);

        toggleButton.setOnCheckedChangeListener(new ToggleButtonClick());
        btnLogin.setOnClickListener(btnClick);
        btnForget.setOnClickListener(btnClick);
        btnRegister.setOnClickListener(btnClick);
        btnFB.setOnClickListener(btnClick);
        btnGoogle.setOnClickListener(btnClick);

        //Firebase
        mAuth = FirebaseAuth.getInstance();

        edtEmail.requestFocusFromTouch();

        //Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);


        //FB
        //建立 CallbackManager，以處理登入回應
        callbackManager = CallbackManager.Factory.create();

        //檢查登入狀態
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
    }

    //密碼顯示or隱藏
    private class ToggleButtonClick implements CompoundButton.OnCheckedChangeListener{
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked){
                Drawable invisible = getDrawable(R.drawable.pwd_invisible);
                toggleButton.setBackgroundDrawable(invisible);
                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }else {
                Drawable visible = getDrawable(R.drawable.pwd_visible);
                toggleButton.setBackgroundDrawable(visible);
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    //檢查用戶是否登入
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
            currentUser.reload();
        }
    }

    Button.OnClickListener btnClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //登入
                case R.id.btnLogin:
                    String account = edtEmail.getText().toString();
                    String password = edtPassword.getText().toString();

                    if(TextUtils.isEmpty(account)){
                        edtEmail.setError("請輸入帳號");
                        return;
                    }
                    if(TextUtils.isEmpty(password)){
                        edtPassword.setError("請輸入密碼");
                        return;
                    }

                    mAuth.signInWithEmailAndPassword(account, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    if (task.isSuccessful()) {
                                        if(!user.isEmailVerified()){
                                            Toast.makeText(MainActivity.this,"未驗證Email",Toast.LENGTH_LONG).show();
                                            edtEmail.requestFocusFromTouch();
                                            edtEmail.setText("");
                                            edtPassword.setText("");
                                        } else {
                                            //Sign in success, update UI with the signed-in user's information
                                            Log.d("Loginsuccess", "signInWithEmail:success");
                                            Intent set = new Intent();
                                            set.setClass(MainActivity.this, LoginActivity.class);
                                            startActivity(set);
                                            edtEmail.requestFocusFromTouch();
                                            edtEmail.setText("");
                                            edtPassword.setText("");
                                        }
                                    } else {
                                        //If sign in fails, display a message to the user.
                                        Log.d("Loginfailure", "signInWithEmail:failure", task.getException());
                                        String fails = task.getException().getMessage();
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle("登入錯誤")
                                                .setMessage(fails)
                                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        edtEmail.requestFocusFromTouch();
                                                        edtEmail.setText("");
                                                        edtPassword.setText("");
                                                    }
                                                })
                                                .show();
                                    }
                                }
                            });
                    break;

                //忘記密碼
                case R.id.btnForget:
                    edtEmail.requestFocusFromTouch();
                    Intent ForgetPw = new Intent();
                    ForgetPw.setClass(MainActivity.this,ForgetPwActivity.class);
                    startActivity(ForgetPw);
                    break;

                //註冊
                case R.id.btnRegister:
                    edtEmail.requestFocusFromTouch();
                    edtEmail.setText("");
                    edtPassword.setText("");
                    Intent Register = new Intent();
                    Register.setClass(MainActivity.this, RegisterActivity.class);
                    startActivity(Register);
                    break;

                //Google登入
                case R.id.btnGoogle:
                    LoginGoogle();
                    break;

                //FB登入
                case R.id.btnFB:
                    LoginFB();
                    break;
            }
        }
    };

    private void LoginGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("AuthWithGoogle", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());

                Toast.makeText(MainActivity.this, "Google登入成功", Toast.LENGTH_SHORT).show();
                Intent set = new Intent();
                set.setClass(MainActivity.this,LoginActivity.class);
                startActivity(set);
            } catch (ApiException e) {
                //Google Sign In failed, update UI appropriately
                Log.d("GoogleFailed", "Google sign in failed", e);
                Toast.makeText(MainActivity.this, "Google登入失敗", Toast.LENGTH_SHORT).show();
            }
        }

        //Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //資料傳回去給firebase
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Sign in success, update UI with the signed-in user's information
                            Log.d("signInGoogleSuccess", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            //If sign in fails, display a message to the user.
                            Log.d("signInGoogleFailure", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void LoginFB(){
        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("email","public_profile"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {
                        String fails = error.getMessage();
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("FB登入失敗")
                                .setMessage(fails)
                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("handleFacebookAccess", "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Sign in success, update UI with the signed-in user's information
                            Log.d("FBsuccess", "signInWithCredential:success");
                            Toast.makeText(MainActivity.this, "FB登入成功",
                                    Toast.LENGTH_SHORT).show();

                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent Set = new Intent();
                            Set.setClass(MainActivity.this, LoginActivity.class);
                            startActivity(Set);
                        } else {
                            //If sign in fails, display a message to the user.
                            Log.d("FBfailure", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "FB登入失敗",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
