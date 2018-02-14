package com.example.micha.firebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.micha.firebase.model.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DataActivity extends AppCompatActivity implements LoginAuthenticator.onLoginInteraction {

    private FirebaseAuth auth;
    private LoginAuthenticator loginAuthenticator;
    private FirebaseDB db;
    private EditText save;
    EditText name,director,year,genre,rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        loginAuthenticator = new LoginAuthenticator(this);
        auth = FirebaseAuth.getInstance();
        db = new FirebaseDB();
        save = findViewById(R.id.save);
        name = findViewById(R.id.movieName);
        director = findViewById(R.id.movieDirector);
        year = findViewById(R.id.movieYear);
        genre = findViewById(R.id.movieGenre);
        rating = findViewById(R.id.movieRating);
    }

    public void onUserSignOut(View view) {
        loginAuthenticator.signOut();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
        }
    }

    public void saveData(View view) {
        db.saveSimpleData(save.getText().toString());
    }

    @Override
    public void onUserCreation(FirebaseUser user) {

    }

    @Override
    public void onUserAuthenticated(FirebaseUser user) {

    }

    @Override
    public void onUserSignOut(boolean isSignedOut) {

    }

    public void saveMovie(View view) {
        Movie movie = new Movie(name.getText().toString(), director.getText().toString(), year.getText().toString(),
                genre.getText().toString(), rating.getText().toString());
        db.saveMovie(movie);
    }

    public void getMovies(View view) {
        db.getMovies();
    }
}
