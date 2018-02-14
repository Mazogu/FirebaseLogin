package com.example.micha.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements LoginAuthenticator.onLoginInteraction {

    private EditText password;
    private EditText user;
    String email;
    String pass;
    private String TAG = LoginActivity.class.getSimpleName();
    public static final int GOOGLE_RC = 15;
    LoginAuthenticator loginAuthenticator;
    FirebaseUser currentUser;
    private GoogleSignInClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginAuthenticator = new LoginAuthenticator(this);
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.fire_key)).requestEmail().build();
        mClient = GoogleSignIn.getClient(this, options);
        user = findViewById(R.id.username);
        password = findViewById(R.id.password);
        SignInButton button = findViewById(R.id.google);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "google: button hit");
                Intent signIn = mClient.getSignInIntent();
                startActivityForResult(signIn,GOOGLE_RC);
            }
        });
    }

    public void initCredentials(){
        email = user.getText().toString();
        pass = password.getText().toString();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = loginAuthenticator.checkSession();
        if (currentUser != null) {
            Log.d(TAG, "onStart: move to second activity");
            goToDataActivity();
        }
    }

    public void onAuthenticateUser(View view) {
        switch (view.getId()){
            case R.id.signIn:
                initCredentials();
                loginAuthenticator.authenticateUser(email,pass);


            break;

            case R.id.create:
                initCredentials();
                loginAuthenticator.createUSer(email,pass);
                break;
        }
    }

    private void goToDataActivity() {
        Intent intent = new Intent(getApplicationContext(),DataActivity.class);
        startActivity(intent);
    }

    @Override
    public void onUserCreation(FirebaseUser user) {
        if(user != null){
            goToDataActivity();
        }
    }

    @Override
    public void onUserAuthenticated(FirebaseUser user) {
        if(user != null){
            goToDataActivity();
        }
    }

    @Override
    public void onUserSignOut(boolean isSignedOut) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GOOGLE_RC){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                loginAuthenticator.google(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }
}
