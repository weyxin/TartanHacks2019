package com.example.tartanhacks2019;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.Random;
import java.io.File;
import java.util.stream.IntStream;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.FindCallback;

public class Tab2Fragment extends Fragment {

    public static boolean cardFlipped = false;
    public Fragment frontCard = null;
    public Fragment backCard = null;
    public static Image currCard = null;
    Random random = new Random();
    List<Image> flashCards = new ArrayList<>();

    public Tab2Fragment(){
        loadImages();
        Collections.shuffle(flashCards);
        //Image hardcode = new Image( "app/src/main/res/drawable-v24/emily.jpg","Henry", "382435872395");
        //flashCards.add(hardcode);
        currCard = flashCards.get(0);
    }

    public void loadImages(){
        Log.d("item", "hello");
        final ParseQuery<Image> picQ = ParseQuery.getQuery("Image");
       picQ.findInBackground(new FindCallback<Image>(){
           @Override
           public void done(List<Image> objects, ParseException e){
               if (e == null) {
                   flashCards.clear();
                   for(int i = 0; i <objects.size();i++) {
                       Log.d("item", Integer.toString(objects.size()));
                       Image im = objects.get(i);
                       flashCards.add(im);
                   }
               } else {
                   Log.d("item", "Error: " + e.getMessage());
               }
           }
       }
       );

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View card = inflater.inflate(R.layout.fragment_tab2, container, false);
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.container, new CardFrontFragment())
                .commit();

        Button button = card.findViewById(R.id.action_flip);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                flipCard();
            }
        });
        return card;
    }

    public void flipCard(){
        Fragment newFragment;
        if (cardFlipped) {
            currCard = flashCards.get(random.nextInt(flashCards.size()));
            newFragment = new CardFrontFragment();
            frontCard = newFragment;
            Bundle bundle = new Bundle();
            String uri = "";
            try{uri = currCard.getPicture().getFile().getPath();}
            catch(ParseException e){}

            bundle.putString("Image", uri);
            newFragment.setArguments(bundle);
            String nam = currCard.getName();
            bundle.putString("Name", nam);
            backCard = new CardBackFragment();
        } else {
            newFragment = backCard;
        }

        getChildFragmentManager()
                .beginTransaction()
                //.setCustomAnimations(R.anim.cardflipleftin,R.anim.cardfliprightin)
                .replace(R.id.container, newFragment)
                .commit();

        cardFlipped = !cardFlipped;
    }




    public static class CardFrontFragment extends Fragment {



        public CardFrontFragment() { }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.card_front, container, false);
            //String imgPath = getArguments().getString("Image");
            ImageView im = rootView.findViewById(R.id.person);
            //Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            try{im.setImageBitmap(BitmapFactory.decodeFile((currCard.getPicture().getFile().getPath())));}
            catch(ParseException e){

            }
            //ParseFile image =
            return rootView;
        }
    }

    public static class CardBackFragment extends Fragment {

        public CardBackFragment() { }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.card_back, container, false);
            //String name = getArguments().getString("Name");
            TextView na = rootView.findViewById(R.id.nameP);
            na.setText(currCard.getName());
            return rootView;
        }
    }
}

