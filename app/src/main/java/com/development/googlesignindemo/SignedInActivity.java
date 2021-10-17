package com.development.googlesignindemo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SignedInActivity extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private static ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by the gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        FloatingActionButton fabSignOut = findViewById(R.id.fab_sign_out);

        fabSignOut.setOnClickListener(v -> {
            // Call method for signing out.
            googleSignOut();
        });

        // Initialise the activity launcher for google sign in.
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        //Do nothing                        }
                    }
                });

        // Extract from intent user profile information.
        GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra("account");
        String userGivenName = googleSignInAccount.getGivenName();
        String userSurname = googleSignInAccount.getFamilyName();
        String userEmail = googleSignInAccount.getEmail();
        String userID = googleSignInAccount.getId();
        Uri userImageUri = googleSignInAccount.getPhotoUrl();

        // Assign profile information to text views
        TextView txtGivenName = findViewById(R.id.txt_user_name);
        txtGivenName.setText(getString(R.string.txt_user_name, userGivenName, userSurname));
        TextView txtEmail = findViewById(R.id.txt_user_email);
        txtEmail.setText(userEmail);
        TextView txtUserID = findViewById(R.id.txt_user_id);
        txtUserID.setText(userID);
        ImageView profileIcon = findViewById(R.id.img_user_photo);
        //Glide.with(this).load(userImage).into(profileIcon);
        Glide.with(this).load(userImageUri).apply(RequestOptions.circleCropTransform()).into(profileIcon);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            googleSignOut();
        }

        return super.onKeyDown(keyCode, event);
    }

    private void googleSignOut() {

        // Sign out from Google account and create a listener to detect when this has been done.
        // Now the sign in activity will be launched once again for the application.
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SignedInActivity.this, "User Signed Out", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activityResultLauncher.launch(intent);

                        finish();
                    }
                });
    }
}