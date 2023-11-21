package com.example.thejournal.ui.home;

import static androidx.fragment.app.FragmentManager.TAG;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

import static com.example.thejournal.data.SpotifySearchData.searchSpotify;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.thejournal.R;
import com.example.thejournal.adapters.EntryAdapter;
import com.example.thejournal.adapters.PromptAdapter;
import com.example.thejournal.data.ApiHandler;
import com.example.thejournal.data.ImageLoader;
import com.example.thejournal.models.JournalEntry;
import com.example.thejournal.models.Music;
import com.example.thejournal.ui.SpotiySearch.SpotifySearch;
import com.example.thejournal.ui.UpdateView.UpdateView;
import com.example.thejournal.ui.createentry.createJournalEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The main activity responsible for displaying the user's journal entries and providing
 * functionality for creating new entries. It also includes prompts and user profile information.
 */
public class HomeActivity extends AppCompatActivity {
    // Flag to indicate whether the app is in edit mode
    public static boolean EDIT_MODE = false;

    // Request code for adding an entry
    public static final int REQUEST_CODE_ADD_INT = 2;

    // Listener registration for Firebase Firestore changes
    ListenerRegistration listenerRegistration;

    // RecyclerViews for displaying journal entries and prompts
    private RecyclerView homeRecyclerView, promptsRecyler;

    // Lists to store journal entries and prompts
    private List<JournalEntry> journalEntryList;
    private List<String> prompts;

    // Adapters for journal entries and prompts
    private EntryAdapter entryAdapter;
    private PromptAdapter promptAdapter;

    // ImageView for the user's profile photo
    ImageView profilePhoto;

    /**
     * Called when the activity is starting. Initializes UI components, sets up event listeners,
     * and fetches user-specific data such as journal entries and prompts.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down, this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState(Bundle).
     */
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ...

        // Check if the device SDK version is greater than 8
        int SDK_INT = Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            // Apply certain UI changes for devices with SDK version greater than 8
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);

            // Initialize Firebase components
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();

            // Initialize UI components
            ImageView homeFloatingButton = findViewById(R.id.homeFloatingButton);
            ImageView spotifyButton = findViewById(R.id.imageAddMusic);
            profilePhoto = findViewById(R.id.profilePicture);
            homeRecyclerView = findViewById(R.id.journalHomeRecyclerView);
            promptsRecyler = findViewById(R.id.journalPromptHomeRecyclerView);

            // Set the status bar color
            if (Build.VERSION.SDK_INT >= 21) {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(this.getResources().getColor(R.color.gray1));
            }

            // Set up Firebase Firestore listener to retrieve journal entries
            getEntries();

            // Set up prompts
            generateSuggestions();

            // Set up UI event listeners
            spotifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start the Spotify search activity
                    startActivityForResult(new Intent(getApplicationContext(), SpotifySearch.class),
                            REQUEST_CODE_ADD_INT);
                }
            });

            homeFloatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start the create journal entry activity
                    startActivityForResult(new Intent(getApplicationContext(), createJournalEntry.class),
                            REQUEST_CODE_ADD_INT);
                }
            });

            // Set up RecyclerViews and Adapters
            homeRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            promptsRecyler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            journalEntryList = new ArrayList<>();
            entryAdapter = new EntryAdapter(journalEntryList, this);
            homeRecyclerView.setAdapter(entryAdapter);
            prompts = new ArrayList<>();
            promptAdapter = new PromptAdapter(prompts, this);
            promptsRecyler.setAdapter(promptAdapter);
        }
    }

    /**
     * Retrieves journal entries from Firebase Firestore and updates the UI accordingly.
     */
    private void getEntries() {
        // ...
    }

    /**
     * Generates and displays journaling prompts.
     */
    private void generateSuggestions() {
        // ...
    }

    // ...

    /**
     * Handles the result of an activity started for result, such as Spotify search or creating
     * a new journal entry.
     *
     * @param requestCode The request code originally supplied to startActivityForResult(),
     * @param resultCode  The result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller.
     */
    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // ...
    }

    /**
     * Called when the activity will start interacting with the user. Fetches entries and prompts
     * when the activity is resumed.
     */
    @Override
    protected void onStart() {
        // ...
    }

    /**
     * Starts the UpdateView activity with the specified entry ID.
     *
     * @param ID The ID of the entry to be viewed and updated.
     */
    public void startActivitywithID(String ID) {
        // ...
    }
}
