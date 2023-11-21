package com.example.thejournal;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.example.thejournal.ui.home.HomeActivity;
import com.example.thejournal.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.thejournal.databinding.ActivityMainBinding;

/**
 * The main entry point of the application. Determines whether the user is already logged in
 * and redirects to the appropriate screen (HomeActivity or LoginActivity).
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Called when the activity is starting. Responsible for checking the user's authentication
     * status and redirecting to the appropriate screen.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Authentication
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        // Check if a user is already authenticated
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // If the user is authenticated, reload the user data and redirect to HomeActivity
            currentUser.reload();
            Intent myIntent = new Intent(MainActivity.this, HomeActivity.class);
            MainActivity.this.startActivity(myIntent);
            finish();
        } else {
            // If the user is not authenticated, redirect to LoginActivity
            Intent myIntent = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(myIntent);
            finish();
        }
    }
}
