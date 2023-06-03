package com.example.e_sbitar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.e_sbitar.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Serializable;
import java.util.HashMap;

public class RegisterSuitActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private ShapeableImageView registerProfilePicture;
    private Uri imageUri;
    private String imageUrl;
    private AppCompatButton joinBtn;
    private RadioGroup radioGroup;
    private RadioButton doctorButton, patientButton, radioButton;
    private FirebaseAuth mAuth;
    private FirebaseUser CurrentUser;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_suit);
        progressBar = findViewById(R.id.registrationSuitProgressBar);
        doctorButton = findViewById(R.id.radio_doctor);
        patientButton = findViewById(R.id.radio_patient);
        radioGroup = findViewById(R.id.radio_grp);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        CurrentUser = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference().child("profileImages");

        registerProfilePicture = findViewById(R.id.registerProfilePicture);
        registerProfilePicture.setOnClickListener(this);

        joinBtn = findViewById(R.id.join_button);
        joinBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerProfilePicture:
                pickMedia();
                break;
            case R.id.join_button:
                finishRegistrationProcess();
                break;

        }
    }

    private void pickMedia() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK
                        && result.getData() != null) {
                    imageUri = result.getData().getData();
                    registerProfilePicture.setBackground(null);
                    registerProfilePicture.setImageURI(imageUri);
                }
            }
    );

    private void finishRegistrationProcess(){
        final String Type;

        if (doctorButton.isChecked() || patientButton.isChecked() ){
            int selectedId = radioGroup.getCheckedRadioButtonId();
            radioButton = findViewById(selectedId);
            String TypeTxt = radioButton.getText().toString().trim();

            if(TypeTxt.isEmpty()){
                Toast.makeText(this, "Please select your role", Toast.LENGTH_SHORT).show();
                return;
            }else if (TypeTxt.equals("Doctor")){
                Type = "Doctor";
            }else{
                Type = "Patient";
            }
        } else {
            Toast.makeText(this, "Please select your role", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        DocumentReference df = db.collection("users").document(CurrentUser.getUid());

        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    UserModel userModel = doc.toObject(UserModel.class);
                    if (doc.exists()) {
                        HashMap<String, Object> user = new HashMap<>();

                        user.put("Type", Type);

                        Intent intent = new Intent(RegisterSuitActivity.this,BottomNavigationActivity.class);
                        intent.putExtra("UserInfos", (Serializable) userModel);

                        if (imageUri != null) {
                            uploadPicture(user,df,userModel);
                        }else{

                            df.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressBar.setVisibility(View.GONE);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(RegisterSuitActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void uploadPicture(HashMap<String, Object> user, DocumentReference df,UserModel userModel) {

        StorageReference fileReference = storageReference.child(CurrentUser.getUid());

        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUrl = uri.toString();

                        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot doc = task.getResult();
                                    if (doc.exists()) {
                                        user.put("ProfilePic", imageUrl);

                                        Intent intent = new Intent(RegisterSuitActivity.this,BottomNavigationActivity.class);
                                        intent.putExtra("UserInfos", (Serializable) userModel);

                                        df.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressBar.setVisibility(View.GONE);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(RegisterSuitActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterSuitActivity.this, "Failed to Upload the picture. Please try again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}