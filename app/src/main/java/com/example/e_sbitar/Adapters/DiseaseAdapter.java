package com.example.e_sbitar.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.e_sbitar.Interfaces.OnItemClick;
import com.example.e_sbitar.Models.DiseaseModel;
import com.example.e_sbitar.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class DiseaseAdapter extends RecyclerView.Adapter<DiseaseAdapter.ViewHolder> {

    private List<DiseaseModel> diseasesList;
    private Context mContext;
    private OnItemClick mListner;


    public DiseaseAdapter(List<DiseaseModel> diseasesList, Context mContext, OnItemClick mlistner) {
        this.diseasesList = diseasesList;
        this.mContext = mContext;
        mListner = mlistner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.disease_cardview,parent,false);
        return new ViewHolder(view, mListner);
    }

    @Override
    public void onBindViewHolder(@NonNull DiseaseAdapter.ViewHolder holder, int position) {
        if (diseasesList.get(position).getPicture()!=null){
            Glide.with(mContext).load(diseasesList.get(position).getPicture()).apply(RequestOptions.centerCropTransform()).into(holder.diseasePicture);
        }
        holder.name.setText(diseasesList.get(position).getName());
        holder.description.setText(diseasesList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return diseasesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ShapeableImageView diseasePicture;
        TextView name, description;

        public ViewHolder(@NonNull View itemView, OnItemClick listner) {
            super(itemView);

            diseasePicture = itemView.findViewById(R.id.diseasePic);
            name = itemView.findViewById(R.id.diseaseName);
            description = itemView.findViewById(R.id.diseaseDesc);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listner != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listner.onItemClick(position);
                        }
                    }
                }
            });

        }
    }
}
