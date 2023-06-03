package com.example.e_sbitar.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class DiseaseModel implements Serializable {
    private int DiseaseId;
    private String Name, Description, Picture;
    private ArrayList<String> Symptoms;

    public DiseaseModel(int diseaseId, String name, String description, String picture, ArrayList<String> symptoms) {
        DiseaseId = diseaseId;
        Name = name;
        Description = description;
        Picture = picture;
        Symptoms = symptoms;
    }

    public DiseaseModel() {
    }

    public int getDiseaseId() {
        return DiseaseId;
    }

    public void setDiseaseId(int diseaseId) {
        DiseaseId = diseaseId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }

    public ArrayList<String> getSymptoms() {
        return Symptoms;
    }

    public void setSymptoms(ArrayList<String> symptoms) {
        Symptoms = symptoms;
    }
}
