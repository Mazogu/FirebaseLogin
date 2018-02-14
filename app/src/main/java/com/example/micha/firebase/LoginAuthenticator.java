package com.example.micha.firebase;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by micha on 2/13/2018.
 */

public class LoginAuthenticator {

    public static final String TAG = LoginAuthenticator.class.getSimpleName();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Activity activity;
    onLoginInteraction listener;

    public LoginAuthenticator(Activity activity){
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.activity = activity;
        listener = (onLoginInteraction) activity;
    }

    public void createUSer(String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            firebaseUser = firebaseAuth.getCurrentUser();
                            listener.onUserCreation(firebaseUser);
                            //goToDataActivity();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            //updateUI(null);
                        }

                        // ...
                    }
                }).addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onUserCreation(firebaseUser);
                e.printStackTrace();
            }
        });
    }

    public void authenticateUser(String email, String password){
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            firebaseUser = firebaseAuth.getCurrentUser();
                            listener.onUserAuthenticated(firebaseUser);
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                }).addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onUserAuthenticated(firebaseUser);
                e.printStackTrace();
            }
        });
    }

    public FirebaseUser getUser(){
        return firebaseUser;
    }

    public void signOut(){
        firebaseAuth.signOut();
        boolean sign;
        sign = (firebaseUser == null)? true:false;
        listener.onUserSignOut(sign);
    }

    public interface onLoginInteraction{
        void onUserCreation(FirebaseUser user);
        void onUserAuthenticated(FirebaseUser user);
        void onUserSignOut(boolean isSignedOut);
    }

    public FirebaseUser checkSession(){
        firebaseUser = firebaseAuth.getCurrentUser();
        return firebaseUser;
    }

}
