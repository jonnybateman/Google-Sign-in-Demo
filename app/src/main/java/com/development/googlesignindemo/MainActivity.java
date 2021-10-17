package com.development.googlesignindemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private static ActivityResultLauncher<Intent> activityResultLauncher;
    private GoogleSignInAccount googleSignInAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure sign-in to request the user's ID, email, and basic profile. ID and basic profile
        // are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by the gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton btnSignIn = findViewById(R.id.btn_sign_in);

        // Create a listener for the sign-in button
        btnSignIn.setOnClickListener(v -> {
            // Call method for signing in.
            googleSignIn();
        });

        // Initialise the activity launcher for google sign in.
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                // If result returned from launching the intent is ok...
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intentResult = result.getData();
                    processGoogleSignInApi(intentResult);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            // User already signed in, go to signed in activity.
            Intent intentSignedIn = new Intent(this, SignedInActivity.class);
            activityResultLauncher.launch(intentSignedIn);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d("onKeyDown","Event logged");
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    private void googleSignIn() {

        // Start intent to prompt the user to select a Google account to sign in with.
        Intent intentGoogleSignIn = googleSignInClient.getSignInIntent();
        activityResultLauncher.launch(intentGoogleSignIn);
    }

    // Have now signed in launch the SignedInActivity.
    private void processGoogleSignInApi(Intent intentResult) {

        Task<GoogleSignInAccount> taskGoogleSignIn = GoogleSignIn.getSignedInAccountFromIntent(intentResult);

        try {
            googleSignInAccount = taskGoogleSignIn.getResult(ApiException.class);
            Toast.makeText(this, "Sign-In Successful", Toast.LENGTH_SHORT).show();

            // On successful sign in open the second activity.
            Intent intentSignedIn = new Intent(this, SignedInActivity.class);
            intentSignedIn.putExtra("account", googleSignInAccount);
            activityResultLauncher.launch(intentSignedIn);

        } catch (ApiException e) {
            Log.d("Error", "SignInResult:failed Code:" + e.getStatusCode());
        }
    }
}