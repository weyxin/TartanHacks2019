package com.example.tartanhacks2019;

import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    ArrayList<Person> contactPeople = new ArrayList<>();
    Person currCard;

    public ArrayList<Person> getContacts() {
        return contactPeople;
    }

    public void setContacts(List<Person> mPeople) {
        contactPeople = (ArrayList<Person>) mPeople;
    }

    public Person getCurrCard() {
        return currCard;
    }

    public void setCurrCard(Person person) {
        currCard = person;
    }

}
