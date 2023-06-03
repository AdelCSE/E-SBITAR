package com.example.e_sbitar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView backBtn;
    private TextView loginSwitchBtn;
    private TextInputEditText nameEditTxt,emailEditTxt,passwordEditTxt,confirm_passwordEditTxt;
    private ProgressBar progressBar;
    private AppCompatButton SignUpBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser CurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        backBtn = findViewById(R.id.sign_up_back_button);
        backBtn.setOnClickListener(this);
        loginSwitchBtn = findViewById(R.id.sign_in_switch_button);
        loginSwitchBtn.setOnClickListener(this);
        SignUpBtn = findViewById(R.id.sign_up_button);
        SignUpBtn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        nameEditTxt = findViewById(R.id.NameEditTxt);
        emailEditTxt = findViewById(R.id.EmailEditTxt);
        passwordEditTxt = findViewById(R.id.PasswordEditTxt);
        confirm_passwordEditTxt = findViewById(R.id.ConfirmPasswordEditTxt);
        progressBar = findViewById(R.id.registerProgressBar);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_up_back_button:
                onBackPressed();
                break;
            case R.id.sign_in_switch_button:
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
                break;
            case R.id.sign_up_button:
                firebaseAuthWithEmail();
                break;

        }
    }

    private void firebaseAuthWithEmail() {
        final String fullNameTxt = nameEditTxt.getText().toString().trim();
        final String emailTxt = emailEditTxt.getText().toString().trim();
        final String passwordTxt = passwordEditTxt.getText().toString().trim();
        final String confirmPasswordTxt = confirm_passwordEditTxt.getText().toString().trim();

        if (fullNameTxt.isEmpty()){
            nameEditTxt.setError("Full name is required!");
            nameEditTxt.requestFocus();
            return;
        }

        if (emailTxt.isEmpty()){
            emailEditTxt.setError("Email address is required!");
            emailEditTxt.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()){
            emailEditTxt.setError("Please provide a valid email!");
            emailEditTxt.requestFocus();
            return;
        }

        if (passwordTxt.isEmpty()){
            passwordEditTxt.setError("Password is required!");
            passwordEditTxt.requestFocus();
            return;
        }

        if (passwordTxt.length() < 6){
            passwordEditTxt.setError("Password must be 6 characters or more!");
            passwordEditTxt.requestFocus();
            return;
        }

        if (confirmPasswordTxt.isEmpty()){
            confirm_passwordEditTxt.setError("Please confirm your password!");
            confirm_passwordEditTxt.requestFocus();
            return;
        }

        if (!passwordTxt.equals(confirmPasswordTxt)){
            confirm_passwordEditTxt.setError("Passwords don't match!");
            confirm_passwordEditTxt.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(emailTxt,passwordTxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    registerUser(fullNameTxt,emailTxt);

                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Some error has occurred! " + task.getException().getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

    }

    private void registerUser(String fullName, String email) {
        CurrentUser = mAuth.getCurrentUser();

        Date currentDate = Calendar.getInstance().getTime();

        Map<String, Object> user = new HashMap<>();
        user.put("UserId",CurrentUser.getUid());
        user.put("Name",fullName);
        user.put("Email",email);
        user.put("Type",null);
        user.put("IsAdmin",false);
        user.put("ProfilePic",null);

        DocumentReference df = db.collection("users").document(CurrentUser.getUid());
        df.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressBar.setVisibility(View.GONE);
                startActivity(new Intent(RegisterActivity.this,RegisterSuitActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Some error has occurred, please try again later! ", Toast.LENGTH_SHORT)
                        .show();
            }
        });

    }
}