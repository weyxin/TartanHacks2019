package com.example.tartanhacks2019;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Person")
public class Person extends ParseObject {

    public String getName() {
        return getString("Name");
    }

    public ParseFile getProfileImage() {
        return getParseFile("MainImage");
    }

}
