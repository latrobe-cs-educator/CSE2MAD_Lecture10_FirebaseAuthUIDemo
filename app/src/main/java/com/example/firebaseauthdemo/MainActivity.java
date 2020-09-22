package com.example.firebaseauthdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;


import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    /*
    For more informationon how to use Firebase UI visit
    https://firebase.google.com/docs/auth/android/firebaseui
    https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md
    Start with email & GMAIL first then add others one at a time
    You will need to activate authentication in your firebase console
     */
    private static final int RC_SIGN_IN = 123;
    private String TAG = "LoginTAG";
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        //build AuthUI programatically (nothing needing to be changed in your XML file
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.EmailBuilder().build()))
                        .setLogo(R.drawable.logo)
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
                        .build(),
                RC_SIGN_IN);

        //automatically login if user is logged in
        checkIfLoggedIn();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(this, LoggedInActivity.class));
                finish();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "Sign-in cancelled", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, "Sign-in failed, no network", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.e(TAG, "Sign-in error: ", response.getError());
            }
        }
    }

    private void checkIfLoggedIn() {
        if (auth.getCurrentUser() != null) {
            Log.d(TAG, "User logged in : " + auth.getCurrentUser().getDisplayName());
            Intent intent = new Intent(this, LoggedInActivity.class);
            startActivity(intent);

            finish();
        } else {
            Log.d(TAG, "No user logged in");
        }
    }
}