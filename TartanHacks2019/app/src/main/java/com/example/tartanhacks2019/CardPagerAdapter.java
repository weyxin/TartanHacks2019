package com.example.tartanhacks2019;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CardPagerAdapter extends FragmentPagerAdapter {

    public CardPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return new CardContainerFragment();
    }

    @Override
    public int getCount() {
        return 5;
    }
}
