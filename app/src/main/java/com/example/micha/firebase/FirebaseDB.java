package com.example.micha.firebase;

import android.util.Log;

import com.example.micha.firebase.model.Movie;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by micha on 2/13/2018.
 */

public class FirebaseDB {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("defaultReference");
    DatabaseReference movieRef = database.getReference("Movies");
    public static final String TAG = FirebaseDB.class.getSimpleName();

    public void saveSimpleData(String string){
        myRef.push().setValue(string);
    }

    public void getMovies(){

        movieRef.addValueEventListener(new ValueEventListener() {

            List<Movie> list = new ArrayList<>();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Movie movie = snapshot.getValue(Movie.class);
                    list.add(movie);
                }
                Log.d(TAG, "onDataChange: "+list.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.getDetails();
            }
        });

    }

    public void saveMovie(Movie movie){
        //movieRef.push().setValue(movie);
        movieRef.child(movie.getName()).setValue(movie);
    }
}
