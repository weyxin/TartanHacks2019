package com.example.tartanhacks2019;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class Tab4Fragment extends Fragment {

    private GridAdapter gridAdapter;
    private RecyclerView rvPeople;
    private ArrayList<Person> mPeople;
    private SharedViewModel model;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab4, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model = ViewModelProviders.of((FragmentActivity) getContext()).get(SharedViewModel.class);
        rvPeople = view.findViewById(R.id.rvGrid);
        mPeople = new ArrayList<>();
        gridAdapter = new GridAdapter(mPeople, model);
        rvPeople.setLayoutManager(new GridLayoutManager(this.getActivity(), 2));
        rvPeople.setAdapter(gridAdapter);
        loadContacts();
    }

    private void loadContacts() {
        final ParseQuery<Person> query= ParseQuery.getQuery(Person.class);
        query.findInBackground(new FindCallback<Person>() {
            @Override
            public void done(List<Person> objects, ParseException e) {
                if(e == null) {
                    mPeople.clear();
                    gridAdapter.clearPeople();
                    Log.d("Tab4Fragment", Integer.toString(objects.size()));
                    for(int i = 0; i < objects.size(); i++) {
                        mPeople.add(objects.get(i));
                        gridAdapter.notifyItemInserted(i);
                    }
                }
                else {
                    Log.d("Tab4Fragment", "Error: " + e.getMessage());
                }
            }
        });
    }
}
