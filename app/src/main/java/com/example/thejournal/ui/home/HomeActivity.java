package com.example.thejournal.ui.home;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
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
import com.example.thejournal.models.JournalEntry;
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
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    public  Bitmap getBitmapFromURL(String src) {
        try {
//            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }
//    User.UserData ud;

    public static final int REQUEST_CODE_ADD_INT=1;
    private RecyclerView homeRecyclerView, promptsRecyler;
    private List<JournalEntry> journalEntryList;
    private List<String> prompts;
    private EntryAdapter entryAdapter;
    private PromptAdapter promptAdapter;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid=user.getUid();
//            User currentUser=new User(user.getUid());
            ImageView homeFloatingButton= findViewById(R.id.homeFloatingButton);
            com.makeramen.roundedimageview.RoundedImageView profilePhoto=findViewById(R.id.profilePicture);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(uid);
            Source source = Source.CACHE;
// Get the document, forcing the SDK to use the offline cache
            docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Log.d(TAG, "Cached document data: " + document.getData());
                        profilePhoto.setImageBitmap
                                (getBitmapFromURL(
                                        document.getData()
                                                .get("imageUrl")
                                                .toString()));
                    } else {
                        Log.d(TAG, "Go online" );

                    }
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
//                    new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            );
            promptsRecyler=findViewById(R.id.journalPromptHomeRecyclerView);
            promptsRecyler.setLayoutManager(
                    new LinearLayoutManager(getApplicationContext())
            );
            getEntries();
            journalEntryList=new ArrayList<>();
            entryAdapter=new EntryAdapter(journalEntryList);
            homeRecyclerView.setAdapter(entryAdapter);
            prompts=new ArrayList<>();
            promptAdapter=new PromptAdapter(prompts);
            promptsRecyler.setAdapter(promptAdapter);
            generateSuggestions();




        }


    }

    // ...

    private void getEntries() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        db.collection("users/" + uid + "/Entries")
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
                                    je.setTitle(document.getData().get("title").toString());
                                    je.setSubtitle(document.getData().get("subtitle").toString());
                                    je.setDateCreated(document.getData().get("dateCreated").toString());
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

    private void generateSuggestions()
    {
        String promptText=new ApiHandler().generateText(
                "AIzaSyB5UooCRfiIKJP_xbcmtl-d4k-46t_JH6A",
                "Generate one " +
                        "random healthy journaling prompt only one result different " +
                        "every time make it is just one sentence nothing extra");

        if(promptText=="null")
        {
            prompts.add("What did you do recently?");
        }
        else
        {
            prompts.add(promptText);
        }

        promptAdapter.notifyItemInserted(prompts.size()-1);

    }

// ...


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_INT && resultCode==RESULT_OK)
        {
            getEntries();
            generateSuggestions();
        }
    }
}