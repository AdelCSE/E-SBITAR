package com.example.e_sbitar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ImageView backBtn;
    private AppCompatButton sendBtn;
    private TextInputLayout rpEmailLayout;
    private TextInputEditText rpEmailEditTxt;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        backBtn = findViewById(R.id.forgot_back_button);
        sendBtn = findViewById(R.id.sendEmailButton);
        progressBar = findViewById(R.id.fpProgressBar);
        rpEmailLayout = findViewById(R.id.fpEmailLayout);
        rpEmailEditTxt = findViewById(R.id.fpEmailEditTxt);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        dialog = new Dialog(ForgotPasswordActivity.this);
        dialog.setContentView(R.layout.resset_password_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        AppCompatButton gotItBtn = dialog.findViewById(R.id.gotItButton);

        gotItBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onBackPressed();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

        rpEmailEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                rpEmailLayout.setError(null);
            }
        });
    }

    private void resetPassword() {
        final String emailTxt = rpEmailEditTxt.getText().toString().trim();

        if (emailTxt.isEmpty()){
            rpEmailLayout.setError("*Please enter your email !");
            rpEmailEditTxt.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()){
            rpEmailLayout.setError("*Please enter a valid email !");
            rpEmailEditTxt.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        db.collection("users").whereEqualTo("Email",emailTxt).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    if (docs.size() != 0){
                        mAuth.sendPasswordResetEmail(emailTxt).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    progressBar.setVisibility(View.GONE);
                                    dialog.show();
                                }else{
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else {
                        progressBar.setVisibility(View.GONE);
                        rpEmailLayout.setError("*There's no account associated with this email !");
                        rpEmailEditTxt.requestFocus();
                    }
                }else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage() , Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}