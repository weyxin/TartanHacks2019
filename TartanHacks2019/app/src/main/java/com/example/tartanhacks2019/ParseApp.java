package com.example.tartanhacks2019;

import android.app.Application;

import com.parse.Parse;

public class ParseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //ParseObject.registerSubclass(User.class);
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("twem19")
                .clientKey("weyxin")
                .server("https://tartanhack19.herokuapp.com/parse")
                .build();
        Parse.initialize(configuration);
    }
}
