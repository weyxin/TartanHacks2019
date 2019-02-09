package com.example.tartanhacks2019;

import android.arch.lifecycle.ViewModel;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    ArrayList<Person> contactPeople = new ArrayList<>();
    Person currCard;
    Person galleryPerson;
    FragmentManager fragmentManager;

    public ArrayList<Person> getContacts() {
        return contactPeople;
    }

    public void setContacts(List<Person> mPeople) {
        contactPeople = (ArrayList<Person>) mPeople;
    }

    public Person getCurrCard() {
        return currCard;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public Person getGalleryPerson() { return galleryPerson; }

    public void setGalleryPerson(Person person) {
        galleryPerson = person;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void setCurrCard(Person person) {
        currCard = person;
    }

}
