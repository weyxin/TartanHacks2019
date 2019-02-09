package com.example.tartanhacks2019;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.Collections;

public class CardContainerFragment extends Fragment {

    public static SharedViewModel model;
    private boolean cardFlipped = false;

    public CardContainerFragment() {
        Log.d("created", "CardContainer");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_container, container, false);
        model = ViewModelProviders.of((FragmentActivity) getContext()).get(SharedViewModel.class);
        model.setCurrCard(getRandomCard(model.getContacts()));
        //Log.d("CardContainerFragment", model.getCurrCard().getName());
        getChildFragmentManager()
                .beginTransaction()
                 .add(R.id.container, new CardFrontFragment())
                .commit();
        return rootView;
    }

    public Person getRandomCard(ArrayList<Person> contacts) {
        Collections.shuffle(contacts);
        Log.d("Tab2Fragment", "Got random card");
        return(contacts.get(0));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.card, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_flip:
                flipCard();
                return true;
        }
        return false;
    }

    public void flipCard() {
        Fragment newFragment;
        if (cardFlipped) {
            model.setCurrCard(getRandomCard(model.getContacts()));
            newFragment = new CardFrontFragment();
        } else {
            newFragment = new CardBackFragment();
        }

        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                .replace(R.id.container, newFragment)
                .commit();
        cardFlipped = !cardFlipped;

    }

    public static class CardFrontFragment extends Fragment {

        public ImageView frontProfile;

        public CardFrontFragment() { }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_card_front, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            frontProfile = view.findViewById(R.id.frontProfile);
            ParseFile profile = model.getCurrCard().getProfileImage();
            if(profile != null) {
                Glide.with(getContext()).load(profile.getUrl()).into(frontProfile);
            }
        }
    }

    public static class CardBackFragment extends Fragment {

        public TextView relationship;
        public TextView name;

        public CardBackFragment() { }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_card_back, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            relationship = view.findViewById(R.id.backRelationship);
            name = view.findViewById(R.id.backName);
            relationship.setText(model.getCurrCard().getRelationship());
            name.setText(model.getCurrCard().getName());
        }
    }
}