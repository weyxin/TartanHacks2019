package com.example.tartanhacks2019;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

public class Tab2Fragment extends Fragment {

    public static Person currCard = null;
    public CardPagerAdapter cardPagerAdapter;
    public ViewPager viewPager;
    private SharedViewModel model;
    protected FragmentActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = ViewModelProviders.of((FragmentActivity) getContext()).get(SharedViewModel.class);
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.Tab2, new CardContainerFragment())
                .commit();
        loadCards();
    }

    /*
    public void getRandomCard() {
        Collections.shuffle(flashCards);
        currCard = flashCards.get(0);
        Log.d("Tab2Fragment", "Got random card");
        Log.d("Tab2Fragment", currCard.getName());
    }
    */

    public void loadCards() {
        final ParseQuery<Person> query= ParseQuery.getQuery(Person.class);
        try {
            List<Person> people = query.find();
            Log.d(("Tab2Fragment"), Integer.toString(people.size()));
            model.setContacts(people);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
