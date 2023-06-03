package com.example.e_sbitar.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.example.e_sbitar.Adapters.DiseaseAdapter;
import com.example.e_sbitar.DiseaseDetailsActivity;
import com.example.e_sbitar.Interfaces.OnItemClick;
import com.example.e_sbitar.Models.DiseaseModel;
import com.example.e_sbitar.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class HealthFragment extends Fragment implements OnItemClick {

    private View root;
    private RecyclerView recyclerView;
    private DiseaseAdapter diseaseAdapter;
    private List<DiseaseModel> DiseasesDataHolder, diseaseDataHolder;
    private int limit = 15;
    private DocumentSnapshot lastVisible;
    private boolean isScrolling = false;
    private boolean isLastItemReached = false;

    private FirebaseAuth mAuth;
    private FirebaseUser CurrentUser;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_health, container, false);
        diseaseDataHolder = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


        fetchData();

        return root;

    }

    private void fetchData(){
        recyclerView = root.findViewById(R.id.eventsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        CollectionReference worksRef = db.collection("diseases");
        Query query = worksRef.limit(limit);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        DiseaseModel workModel = document.toObject(DiseaseModel.class);
                        diseaseDataHolder.add(workModel);
                    }

                    if (task.getResult().size() > 0)
                        lastVisible = task.getResult().getDocuments().get(task.getResult().size() - 1);

                    BuildRecyclerView(diseaseDataHolder,recyclerView);

                    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                                isScrolling = true;
                            }
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            LinearLayoutManager linearLayoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
                            int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                            int visibleItemCount = linearLayoutManager.getChildCount();
                            int totalItemCount = linearLayoutManager.getItemCount();

                            if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItemReached) {
                                isScrolling = false;
                                Query nextQuery = worksRef.startAfter(lastVisible).limit(limit);
                                nextQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> t) {
                                        if (t.isSuccessful()) {
                                            for (DocumentSnapshot d : t.getResult()) {
                                                DiseaseModel productModel = d.toObject(DiseaseModel.class);
                                                diseaseDataHolder.add(productModel);
                                            }
                                            diseaseAdapter.notifyDataSetChanged();

                                            if (t.getResult().size() > 0)
                                                lastVisible = t.getResult().getDocuments().get(t.getResult().size() - 1);

                                            if (t.getResult().size() < limit) {
                                                isLastItemReached = true;
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    };
                    recyclerView.addOnScrollListener(onScrollListener);
                }
            }
        });
    }

    private void BuildRecyclerView(List<DiseaseModel> diseasesDataHolder, RecyclerView recyclerView) {
        diseaseAdapter = new DiseaseAdapter(diseasesDataHolder, getContext(), this);
        recyclerView.setAdapter(diseaseAdapter);
    }

    @Override
    public void onItemClick(int position) {
        lunchDiseaseDetailsActivity(position);
    }

    private void lunchDiseaseDetailsActivity(int position){
        DiseaseModel disease = new DiseaseModel(diseaseDataHolder.get(position).getDiseaseId(),
                diseaseDataHolder.get(position).getName(),
                diseaseDataHolder.get(position).getDescription(),
                diseaseDataHolder.get(position).getPicture(),diseaseDataHolder.get(position).getSymptoms());

        Intent intent = new Intent(getActivity(), DiseaseDetailsActivity.class);
        intent.putExtra("Tag", (Serializable) disease);
        startActivity(intent);

    }
}