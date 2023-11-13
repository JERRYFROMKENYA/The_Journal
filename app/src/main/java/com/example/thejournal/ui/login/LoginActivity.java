package com.example.thejournal.ui.login;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.thejournal.MainActivity;
import com.example.thejournal.ProfileActivity;
import com.example.thejournal.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
     FirebaseAuth mAuth= FirebaseAuth.getInstance();

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
            finish();
        }
    }
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String value = intent.getStringExtra("key"); //if it's a string you stored.
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        final ImageView JouLogo=binding.loginImage;
        final TextView Slogan=binding.loginSlogan;
        final TextView WelText=binding.loginWelcome;

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {

                loadingProgressBar.setVisibility(View.GONE);
//                if (loginResult.getError() != null) {
//                    showLoginFailed(loginResult.getError());
//                }
//                if (loginResult.getSuccess() != null) {
//
//                }
//                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {


                    usernameEditText.setVisibility(View.INVISIBLE);
                    passwordEditText.setVisibility(View.INVISIBLE);
                    loginButton.setVisibility(View.INVISIBLE);
                    loadingProgressBar.setVisibility(View.INVISIBLE);
                    JouLogo.setVisibility(View.INVISIBLE);
                    Slogan.setVisibility(View.INVISIBLE);
                    WelText.setVisibility(View.INVISIBLE);






                    loadingProgressBar.setVisibility(View.VISIBLE);
                    String email=usernameEditText.getText().toString();
                    String password=passwordEditText.getText().toString();
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign up success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");

                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUiWithSignUp(user);
                                        Toast.makeText(LoginActivity.this, "Sign Up Success!",
                                                Toast.LENGTH_SHORT).show();
                                        finish();

                                    } else {
                                        // If sign up fails, display a message to the user.
                                        mAuth.signInWithEmailAndPassword(email, password)
                                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            // Sign in success, update UI with the signed-in user's information
                                                            Log.d(TAG, "signInWithEmail:success");
                                                            FirebaseUser user = mAuth.getCurrentUser();
                                                            loadingProgressBar.setVisibility(View.INVISIBLE);
//                                                        updateUI(user);
                                                            Toast.makeText(LoginActivity.this, "Signed In",
                                                                    Toast.LENGTH_SHORT).show();
                                                            updateUiWithUser(user);
                                                            finish();
                                                        } else {
                                                            // If sign in fails, display a message to the user.
                                                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                                                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                                    Toast.LENGTH_SHORT).show();
                                                            loadingProgressBar.setVisibility(View.INVISIBLE);
                                                            loadingProgressBar.setVisibility(View.VISIBLE);
                                                            usernameEditText.setVisibility(View.VISIBLE);
                                                            passwordEditText.setVisibility(View.VISIBLE);
                                                            loginButton.setVisibility(View.VISIBLE);
                                                            loadingProgressBar.setVisibility(View.VISIBLE);
                                                            JouLogo.setVisibility(View.VISIBLE);
                                                            Slogan.setVisibility(View.VISIBLE);
                                                            WelText.setVisibility(View.VISIBLE);

//                                                        updateUI(null);
                                                        }
                                                    }
                                                });
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        loadingProgressBar.setVisibility(View.INVISIBLE);
                                        loadingProgressBar.setVisibility(View.VISIBLE);
                                        usernameEditText.setVisibility(View.VISIBLE);
                                        passwordEditText.setVisibility(View.VISIBLE);
                                        loginButton.setVisibility(View.VISIBLE);
                                        loadingProgressBar.setVisibility(View.VISIBLE);
                                        JouLogo.setVisibility(View.VISIBLE);
                                        Slogan.setVisibility(View.VISIBLE);
                                        WelText.setVisibility(View.VISIBLE);

//                                    updateUI(null);
                                    }
                                }
                            });

                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usernameEditText.setVisibility(View.INVISIBLE);
                passwordEditText.setVisibility(View.INVISIBLE);
                loginButton.setVisibility(View.INVISIBLE);
                loadingProgressBar.setVisibility(View.INVISIBLE);
                JouLogo.setVisibility(View.INVISIBLE);
                Slogan.setVisibility(View.INVISIBLE);
                WelText.setVisibility(View.INVISIBLE);






                loadingProgressBar.setVisibility(View.VISIBLE);
                String email=usernameEditText.getText().toString();
                String password=passwordEditText.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign up success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUiWithSignUp(user);
                                    Toast.makeText(LoginActivity.this, "Sign Up Success!",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
//                                    loadingProgressBar.setVisibility(View.VISIBLE);
//                                    usernameEditText.setVisibility(View.VISIBLE);
//                                    passwordEditText.setVisibility(View.VISIBLE);
//                                    loginButton.setVisibility(View.VISIBLE);
//                                    loadingProgressBar.setVisibility(View.VISIBLE);
//                                    JouLogo.setVisibility(View.VISIBLE);
//                                    Slogan.setVisibility(View.VISIBLE);
//                                    WelText.setVisibility(View.VISIBLE);


//                                    updateUI(user); for welcome
                                } else {
                                    // If sign up fails, display a message to the user.
                                    mAuth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        // Sign in success, update UI with the signed-in user's information
                                                        Log.d(TAG, "signInWithEmail:success");
                                                        FirebaseUser user = mAuth.getCurrentUser();
                                                        loadingProgressBar.setVisibility(View.INVISIBLE);
//                                                        updateUI(user);
                                                        Toast.makeText(LoginActivity.this, "Signed In",
                                                                Toast.LENGTH_SHORT).show();
                                                        updateUiWithUser(user);
                                                        finish();
                                                    } else {
                                                        // If sign in fails, display a message to the user.
                                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                                Toast.LENGTH_SHORT).show();
                                                        loadingProgressBar.setVisibility(View.INVISIBLE);
                                                        loadingProgressBar.setVisibility(View.VISIBLE);
                                                        usernameEditText.setVisibility(View.VISIBLE);
                                                        passwordEditText.setVisibility(View.VISIBLE);
                                                        loginButton.setVisibility(View.VISIBLE);
                                                        loadingProgressBar.setVisibility(View.VISIBLE);
                                                        JouLogo.setVisibility(View.VISIBLE);
                                                        Slogan.setVisibility(View.VISIBLE);
                                                        WelText.setVisibility(View.VISIBLE);

//                                                        updateUI(null);
                                                    }
                                                }
                                            });
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    loadingProgressBar.setVisibility(View.INVISIBLE);
                                    loadingProgressBar.setVisibility(View.VISIBLE);
                                    usernameEditText.setVisibility(View.VISIBLE);
                                    passwordEditText.setVisibility(View.VISIBLE);
                                    loginButton.setVisibility(View.VISIBLE);
                                    loadingProgressBar.setVisibility(View.VISIBLE);
                                    JouLogo.setVisibility(View.VISIBLE);
                                    Slogan.setVisibility(View.VISIBLE);
                                    WelText.setVisibility(View.VISIBLE);

//                                    updateUI(null);
                                }
                            }
                        });
            }
        });

//
//        private void hideUI(){
//            usernameEditText.setVisibility(View.INVISIBLE);
//            passwordEditText.setVisibility(View.INVISIBLE);
//            loginButton.setVisibility(View.INVISIBLE);
//            loadingProgressBar.setVisibility(View.INVISIBLE);
//            JouLogo.setVisibility(View.INVISIBLE);
//            Slogan.setVisibility(View.INVISIBLE);
//            WelText.setVisibility(View.INVISIBLE);
//        }


    }
    private void loginAction()
    {

    }

    private void updateUiWithUser(FirebaseUser user) {
        Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
            myIntent.putExtra("key", user.getEmail()); //Optional parameters
        LoginActivity.this.startActivity(myIntent);
    }

    private void updateUiWithSignUp(FirebaseUser user)
    {
        Intent myIntent = new Intent(LoginActivity.this, ProfileActivity.class);
        myIntent.putExtra("email", user.getEmail()); //Optional parameters
        myIntent.putExtra("uid", user.getUid()); //Optional parameters
        LoginActivity.this.startActivity(myIntent);
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }


}

