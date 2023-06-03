package com.example.e_sbitar;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.e_sbitar.Models.DiseaseModel;
import com.google.android.material.imageview.ShapeableImageView;

public class DiseaseDetailsActivity extends AppCompatActivity {

    private ImageView backBtn;
    private DiseaseModel disease;
    private TextView name, description, symptoms;
    private ShapeableImageView picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_details);

        backBtn = findViewById(R.id.diseaseBackBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        name = findViewById(R.id.diseasePageName);
        description = findViewById(R.id.diseaseDescription);
        symptoms = findViewById(R.id.diseaseSymps);
        picture = findViewById(R.id.diseasePicture);

        disease = (DiseaseModel) getIntent().getSerializableExtra("Tag");
        if (disease != null){
            Glide.with(this).load(disease.getPicture()).apply(RequestOptions.centerCropTransform()).into(picture);
            name.setText(disease.getName());
            description.setText(disease.getDescription());
            String sympsList = "";
            for (String symp: disease.getSymptoms()
                 ) {
                sympsList += "- " + symp + "\n";
            }
            symptoms.setText(sympsList);
        }

    }
}