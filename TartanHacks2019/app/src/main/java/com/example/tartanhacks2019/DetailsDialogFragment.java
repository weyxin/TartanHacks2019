package com.example.tartanhacks2019;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

public class DetailsDialogFragment extends DialogFragment {

    public DetailsDialogFragment () {}

    private TextView tvName;
    private TextView tvRelationship;
    private ImageView ivProfile;
    private SharedViewModel model;

    public static DetailsDialogFragment newInstance() {
        Log.d("DetailsDialogFragment", "Made new dialog fragment");
        DetailsDialogFragment frag = new DetailsDialogFragment();
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("DetailsDiaglogFragment", "Making dialog");
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.fragment_details_dialog ,container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvName = view.findViewById(R.id.tvName);
        tvRelationship = view.findViewById(R.id.tvRelationship);
        ivProfile = view.findViewById(R.id.ivImage);
        model = ViewModelProviders.of((FragmentActivity) getContext()).get(SharedViewModel.class);
        tvName.setText(model.getGalleryPerson().getName());
        tvRelationship.setText(model.getGalleryPerson().getRelationship());
        ParseFile profile = model.getGalleryPerson().getProfileImage();
        if(profile != null) {
            Glide.with(getContext()).load(profile.getUrl()).into(ivProfile);
        }
    }
}
