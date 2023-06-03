package com.example.e_sbitar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_sbitar.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView backBtn;
    private TextView registerSwitchBtn, forgotPasswordBtn, errorText;
    private TextInputLayout passwordLayout,emailLayout;
    private TextInputEditText emailEditTxt,passwordEditTxt;
    private AppCompatButton loginBtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        backBtn = findViewById(R.id.sign_in_back_button);
        backBtn.setOnClickListener(this);
        progressBar = findViewById(R.id.loginProgressBar);
        registerSwitchBtn = findViewById(R.id.sign_up_switch_button);
        registerSwitchBtn.setOnClickListener(this);
        forgotPasswordBtn = findViewById(R.id.forgot_password_button);
        forgotPasswordBtn.setOnClickListener(this);
        loginBtn = findViewById(R.id.sign_in_button);
        loginBtn.setOnClickListener(this);

        errorText = findViewById(R.id.errorText);
        emailEditTxt = findViewById(R.id.userEditTxt);
        passwordEditTxt = findViewById(R.id.loginPasswordEditTxt);
        passwordLayout = findViewById(R.id.loginPasswordLayout);
        emailLayout = findViewById(R.id.userLayout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                emailLayout.setError(null);
                passwordLayout.setError(null);
            }
        });
        passwordEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                emailLayout.setError(null);
                passwordLayout.setError(null);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_in_back_button:
                onBackPressed();
                break;
            case R.id.sign_up_switch_button:
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
                break;
            case R.id.sign_in_button:
                loginUser();
                break;
            case R.id.forgot_password_button:
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                break;
        }
    }

    private void loginUser() {
        final String user = emailEditTxt.getText().toString().trim();
        final String password = passwordEditTxt.getText().toString().trim();

        if (user.isEmpty()){
            emailEditTxt.setError("Username is required!");
            return;
        }

        if (password.isEmpty()){
            passwordEditTxt.setError("Password is required!");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        if (Patterns.EMAIL_ADDRESS.matcher(user).matches()){
            mAuth.signInWithEmailAndPassword(user,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        //Check if user has completed registration//
                        currentUser = mAuth.getCurrentUser();
                        db.collection("users").document(currentUser.getUid())
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            DocumentSnapshot doc = task.getResult();
                                            if(doc.exists()){
                                                userModel = doc.toObject(UserModel.class);
                                                String type = userModel.getType();
                                                progressBar.setVisibility(View.GONE);

                                                Intent intent = new Intent(LoginActivity.this, BottomNavigationActivity.class);
                                                intent.putExtra("UserInfos", (Serializable) userModel);

                                                if (type == null) {
                                                    startActivity(new Intent(LoginActivity.this,RegisterSuitActivity.class));
                                                    finish();
                                                }else{
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        }
                                    }
                                });

                    }else{
                        progressBar.setVisibility(View.GONE);
                        passwordLayout.setError(" ");
                        emailLayout.setError(" ");
                        errorText.setText("Your password is incorrect or this account doesn't exist!");
                        errorText.setVisibility(View.VISIBLE);

                    }
                }
            });
        }
    }
}