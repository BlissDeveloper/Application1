package com.example.application1.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.application1.Image;
import com.example.application1.ImageViewHolder;
import com.example.application1.MainActivity;
import com.example.application1.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentGallery extends Fragment {
    private FirebaseAuth mAuth;
    private String currentUserID;
    private CollectionReference imagesRef;

    private View mView;
    private RecyclerView recyclerViewGallery;
    private GridLayoutManager gridLayoutManager;

    public FragmentGallery() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_fragment_gallery, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        imagesRef = FirebaseFirestore.getInstance().collection("Uploaded_Images");

        recyclerViewGallery = mView.findViewById(R.id.recyclerViewGallery);
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);

        recyclerViewGallery.setLayoutManager(gridLayoutManager);
        recyclerViewGallery.setHasFixedSize(true);

        loadImages();

        return mView;
    }

    public void loadImages() {
        Query query = imagesRef.orderBy("timestamp", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Image> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Image>().setQuery(query, Image.class).build();
        FirestoreRecyclerAdapter<Image, ImageViewHolder> firestoreRecyclerAdapter = new FirestoreRecyclerAdapter<Image, ImageViewHolder>(firestoreRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ImageViewHolder holder, int i, @NonNull Image model) {
                holder.setImage_url(model.getImage_url());
                holder.setFull_name(model.getFull_name());
            }

            @NonNull
            @Override
            public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_images_layout, parent, false);
                return new ImageViewHolder(view, getActivity());
            }
        };
        firestoreRecyclerAdapter.startListening();
        recyclerViewGallery.setAdapter(firestoreRecyclerAdapter);
    }

}
