package com.example.thejournal.ui.home;

import static androidx.fragment.app.FragmentManager.TAG;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    public static boolean EDIT_MODE=false;

    public static final int REQUEST_CODE_ADD_INT=2;
    ListenerRegistration listenerRegistration;
    private RecyclerView homeRecyclerView, promptsRecyler;
    private List<JournalEntry> journalEntryList;

    private EditText searchBox;
    private List<String> prompts;
    private EntryAdapter entryAdapter;
    private PromptAdapter promptAdapter;
    ImageView profilePhoto;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.gray1));
        }
        int SDK_INT = Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid=user.getUid();
            ImageView homeFloatingButton= findViewById(R.id.homeFloatingButton);
            ImageView spotifybutton=findViewById(R.id.imageAddMusic);
            searchBox=findViewById(R.id.inputsearch);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(uid);
            Source source = Source.CACHE;
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
// Get the document, forcing the SDK to use the offline cache
            docRef.get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Log.d(TAG, "Cached document data: " + document.getData());

                        // Replace getBitmapFromURL with ImageLoader
                        String imageUrl = String.valueOf(document.getData().get("imageUrl"));
                        Log.d(TAG, "Image URL: " + imageUrl);
                        ImageLoader.getInstance().loadImage(HomeActivity.this, imageUrl,
                                profilePhoto);
                        Log.d(TAG, "Image loading completed");
                    } else {
                        Log.d(TAG, "Go online");
                        docRef.get(Source.CACHE).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @SuppressLint("RestrictedApi")
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    Log.d(TAG, "Server document data: " + document.getData());

                                    // Replace getBitmapFromURL with ImageLoader
                                    String imageUrl = document.getData().get("imageUrl").toString();
                                    Log.d(TAG, "Image URL: " + imageUrl);
                                    ImageLoader.getInstance().loadImage(HomeActivity.this, imageUrl, profilePhoto);
                                    Log.d(TAG, "Image loading completed");
                                } else {
                                    Log.d(TAG, "Failed to get data");
                                }
                                Log.d(TAG, "Alpha:"+profilePhoto.getImageAlpha());

                            }
                        });
                    }
                }
            });

            profilePhoto = findViewById(R.id.profilePicture);


        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(String.valueOf(searchBox.getText()).trim().isEmpty())
                {
                    journalEntryList.clear();
                    getEntries();
                    entryAdapter.notifyDataSetChanged();
                }
                else
                {
                    getEntriesBySearch(String.valueOf(searchBox.getText()));

                }

            }
        });

spotifybutton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        startActivityForResult(new Intent(getApplicationContext(), SpotifySearch.class),
                REQUEST_CODE_ADD_INT);

    }
});
            homeFloatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getApplicationContext(), createJournalEntry.class),
                            REQUEST_CODE_ADD_INT);
                }
            });

            homeRecyclerView=findViewById(R.id.journalHomeRecyclerView);
            homeRecyclerView.setLayoutManager(
                    new LinearLayoutManager(getApplicationContext())
            );
            promptsRecyler=findViewById(R.id.journalPromptHomeRecyclerView);
            promptsRecyler.setLayoutManager(
                    new LinearLayoutManager(getApplicationContext())
            );
            journalEntryList=new ArrayList<>();
            entryAdapter=new EntryAdapter(journalEntryList,this);
            homeRecyclerView.setAdapter(entryAdapter);
            prompts=new ArrayList<>();
            promptAdapter=new PromptAdapter(prompts,this);
            promptsRecyler.setAdapter(promptAdapter);
        }

    }

    private void getEntriesBySearch(String searchString) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        journalEntryList.clear();
        entryAdapter.notifyDataSetChanged();

        // Split the search string by spaces
        String[] searchTerms = searchString.split("\\s+");

        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        // Create separate queries for each field and add them to the list of tasks
        for (String term : searchTerms) {
            tasks.add(db.collection("users/" + uid + "/Entries")
                    .whereArrayContains("emotions", term)
                    .get());

            tasks.add(db.collection("users/" + uid + "/Entries")
                    .whereArrayContains("title", term)
                    .get());
        }

        // Use Tasks.whenAllSuccess to combine the results of all tasks
        Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> results) {
                for (Object result : results) {
                    if (result instanceof QuerySnapshot) {
                        QuerySnapshot querySnapshot = (QuerySnapshot) result;
//                        processQuerySnapshot(querySnapshot);
                    }
                }
            }
        });
    }


// ...


private void getEntries() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        // Remove the existing entries before adding new ones
        if (!journalEntryList.isEmpty())
        {
            journalEntryList.clear();
            entryAdapter.notifyDataSetChanged();
        }


        // Add a snapshot listener to retrieve entries

        listenerRegistration = db.collection("users/" + uid + "/Entries")
                .addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen error", e);
                            return;
                        }

                        if (querySnapshot != null) {
                            for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                                if (change.getType() == DocumentChange.Type.ADDED) {
                                    QueryDocumentSnapshot document = change.getDocument();
                                    JournalEntry je = new JournalEntry();
                                    je.setId(document.getId());
                                    je.setTitle(document.getData().get("title").toString());
                                    je.setBody(String.valueOf(document.getData().get("body")));
                                    je.setSubtitle(document.getData().get("subtitle").toString());
                                    je.setDateCreated(document.getData().get("dateCreated").toString());
                                    List<Map<String, Object>> musicDataList = (List<Map<String, Object>>) document.getData().get("music");

                                    if (musicDataList != null) {
                                        List<Music> musicList = new ArrayList<>();

                                        for ( Map<String, Object> musicData : musicDataList) {
                                            Music music = new Music();

                                            // Assuming you have fields like "title", "artist", "imageUrl", and "musicUrl" in each Music object
                                            music.setImageResource((String) musicData.get("imageResource"));

                                            musicList.add(music);
                                        }

                                        je.setMusic(musicList);
                                    } else {
                                        je.setMusic(new ArrayList<>()); // Set an empty list if "music" field is null or empty
                                    }

                                    List<String> imageDataList = (List<String>) document.getData().get("images");

                                    if (imageDataList != null) {
                                        List<String> imageList = new ArrayList<>();

                                        for ( String imageD : imageDataList) {
//                                            Music music = new Music();

                                            // Assuming you have fields like "title", "artist", "imageUrl", and "musicUrl" in each Music object
//                                            music.setImageResource((String) imageD.get("imageResource"));

                                            imageList.add(imageD);
                                        }

                                        je.setImages(imageList);
                                    } else {
                                        je.setImages(new ArrayList<>()); // Set an empty list if "music" field is null or empty
                                    }

                                    journalEntryList.add(je);
                                    entryAdapter.notifyItemInserted(journalEntryList.size() - 1);
                                    Log.d(TAG, "Document added: " + document.getData());
                                }
                            }

                            homeRecyclerView.smoothScrollToPosition(0);
                        }

                        String source = querySnapshot.getMetadata().isFromCache() ? "local cache" : "server";
                        Log.d(TAG, "Data fetched from " + source);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Remove the listener to avoid multiple active listeners
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }


    }

    private void generateSuggestions()
    {
        String promptText=new ApiHandler().generateText(
                "AIzaSyB5UooCRfiIKJP_xbcmtl-d4k-46t_JH6A",
                "Generate one " +
                        "random healthy journaling prompt other than 'What are you grateful for today' only one result different " +
                        "every time make it is just one sentence " +
                        "nothing extra(no special characters only a-z and '?' no nothing, " +
                        "just a simple and complete sentence) " +
                        "make it different from the last one");

        if(promptText==null)
        {
            prompts.add("What did you do recently?");
        }
        else
        {
            prompts.clear();
            promptAdapter.notifyItemRemoved(0);
            prompts.add(promptText);

        }

        promptAdapter.notifyItemInserted(prompts.size()-1);

    }

// ...


    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_INT && resultCode == RESULT_OK) {
            // Clear the list only if it's not null
//            getEntries();
            generateSuggestions();

            if (data.getStringExtra("artist") != null) {
                Intent intent = new Intent(getApplicationContext(), createJournalEntry.class);
                intent.putExtra("image", data.getStringExtra("image"));
                intent.putExtra("url", data.getStringExtra("url"));
                intent.putExtra("artist", data.getStringExtra("artist"));
                intent.putExtra("title", data.getStringExtra("title"));
                startActivityForResult(intent, REQUEST_CODE_ADD_INT);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(journalEntryList!=null){
            if(journalEntryList.size()>1) {
                journalEntryList.clear();
                entryAdapter.notifyDataSetChanged();
            }
        }
        getEntries();
        generateSuggestions();
        entryAdapter.notifyDataSetChanged();
    }
    public void startActivitywithID(String ID)
    {

        Intent intent = new Intent(this, UpdateView.class);
        intent.putExtra("id",ID);
            HomeActivity.this.startActivity(intent);
        }


}