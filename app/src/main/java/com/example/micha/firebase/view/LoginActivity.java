package com.example.micha.firebase.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.micha.firebase.utils.LoginAuthenticator;
import com.example.micha.firebase.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseUser;
import com.facebook.FacebookSdk;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.internal.TwitterApiConstants;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity implements LoginAuthenticator.onLoginInteraction {

    private static final int FACEBOOK_RC = 64206;
    public static final int TWITTER_RC = 140;
    private EditText password;
    private EditText user;
    private EditText phoneNo;
    String email;
    String pass;
    private String TAG = LoginActivity.class.getSimpleName();
    public static final int GOOGLE_RC = 15;
    LoginAuthenticator loginAuthenticator;
    FirebaseUser currentUser;
    private GoogleSignInClient mClient;
    private CallbackManager manager;
    private String articleId;
    private TwitterLoginButton twitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig("Eh, I have to keep the secret hidden, so might as well",
                "Use your own secret.");
        TwitterConfig config = new TwitterConfig.Builder(this).twitterAuthConfig(authConfig).build();
        Twitter.initialize(config);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        loginAuthenticator = new LoginAuthenticator(this);
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.fire_key)).requestEmail().build();
        mClient = GoogleSignIn.getClient(this, options);
        user = findViewById(R.id.username);
        password = findViewById(R.id.password);
        phoneNo = findViewById(R.id.phone);
        SignInButton google = findViewById(R.id.google);
        LoginButton facebook = findViewById(R.id.facebook);
        twitter = findViewById(R.id.twitter);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "google: button hit");
                Intent signIn = mClient.getSignInIntent();
                startActivityForResult(signIn,GOOGLE_RC);
            }
        });
        manager = CallbackManager.Factory.create();
        facebook.setReadPermissions("email","public_profile");
        facebook.registerCallback(manager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess: "+loginResult.getAccessToken());
                loginAuthenticator.facebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: Why on earth would this cancel?");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError: I don't know what happened.");
                error.printStackTrace();
            }
        });

        twitter.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                loginAuthenticator.twitter(result);
            }

            @Override
            public void failure(TwitterException exception) {
                exception.printStackTrace();
            }
        });


        articleId = getIntent().getStringExtra("articleid");
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
            if(articleId != null){
                goToArtcileActivity();
            }
            else{
                goToDataActivity();
            }
        }
    }

    private void goToArtcileActivity() {
        Intent intent = new Intent(getApplicationContext(),ArticleActivity.class);
        intent.putExtra("articleId", articleId);
        startActivity(intent);
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
        else if(requestCode == FACEBOOK_RC){
            manager.onActivityResult(requestCode, resultCode, data);
        }

        else if(requestCode == TWITTER_RC){
            twitter.onActivityResult(requestCode,resultCode,data);
        }
    }

    public void startPhoneVerify(View view) {
        String phone = phoneNo.getText().toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone,10l, TimeUnit.SECONDS,this,callbacks);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            loginAuthenticator.phone(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            e.printStackTrace();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(s, forceResendingToken.toString());
            loginAuthenticator.phone(credential);
        }
    };
}
