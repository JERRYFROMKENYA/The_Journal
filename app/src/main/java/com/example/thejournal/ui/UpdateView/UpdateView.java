package com.example.thejournal.ui.UpdateView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.example.thejournal.R;
import com.example.thejournal.adapters.ImageAdapter;
import com.example.thejournal.adapters.MusicAdapter;
import com.example.thejournal.models.JournalEntry;
import com.example.thejournal.models.Music;
import com.example.thejournal.ui.home.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateView extends AppCompatActivity {

    private static final int IMAGE_PICKER_REQUEST = 8;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    EditText inputEntryTitleUpdateView, inputSubtitleEntryUpdateView, inputEntryUpdateView;
    ImageView bookmarkUpdateView, imageDoneUpdateView, musicSelectorUpdateView, imageSelectorUpdateView, imageEditToggle;
    boolean bookmarked = false;
    TextView textDateTimeUpdateView, generateImageUpdateView;
    Intent intent;
    static List<Music> musicListUpdateView;
    MusicAdapter musicAdapterUpdateView;
    ImageAdapter imageAdapterUpdateView;

    public static List<String> getImageListUpdateView() {
        return imageListUpdateView;
    }

    static List<String> imageListUpdateView;
    RecyclerView musicRecyclerUpdateView, imageRecyclerUpdateView;
    JournalEntry currentEntry;
    ImageAdapter imageAdapter;
    MusicAdapter musicAdapter;
    LinearLayout quickActions;

    public static List<Music> getMusicListUpdateView() {
        return musicListUpdateView;
    }


    @SuppressLint({"RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewupdate);
        HomeActivity.EDIT_MODE = false;
        currentEntry = new JournalEntry();

        intent = getIntent();
        if(intent.getStringExtra("id")==null){finish();}

        ImageView imageBackUpdateView = findViewById(R.id.imageBackUpdateView);
        imageDoneUpdateView = findViewById(R.id.imageSaveUpdateView);
        inputEntryUpdateView = findViewById(R.id.inputEntryUpdateView);
        musicSelectorUpdateView = findViewById(R.id.imageAddMusicUpdateView);
        imageSelectorUpdateView = findViewById(R.id.entryimageAddImageUpdateView);
        imageListUpdateView = new ArrayList<>();
        quickActions=findViewById(R.id.layoutQuickActionsUV);
        imageEditToggle=findViewById(R.id.imageEditUpdateView);
        musicListUpdateView = new ArrayList<>();
        musicRecyclerUpdateView = findViewById(R.id.musicCreateRecyclerUpdateView);

        musicAdapter = new MusicAdapter(this,musicListUpdateView);
        musicRecyclerUpdateView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        musicRecyclerUpdateView.setAdapter(musicAdapter);
        imageRecyclerUpdateView = findViewById(R.id.imageCreateRecyclerUpdateView);

        imageAdapter = new ImageAdapter(this,imageListUpdateView);
        imageRecyclerUpdateView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        imageRecyclerUpdateView.setAdapter(imageAdapter);
        inputEntryTitleUpdateView = findViewById(R.id.inputEntryTitleUpdateView);
        inputSubtitleEntryUpdateView = findViewById(R.id.inputEntrySubtitleUpdateView);
        textDateTimeUpdateView = findViewById(R.id.textDateAndTimeUpdateView);
        generateImageUpdateView = findViewById(R.id.textGenerateUpdateView);


        getJournalEntrybyID(intent.getStringExtra("id"));
        //Edit Toggle
        imageEditToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditToggle();

            }
        });

        if (!HomeActivity.EDIT_MODE) {
            inputEntryTitleUpdateView.setEnabled(false);
            inputSubtitleEntryUpdateView.setEnabled(false);
            inputEntryUpdateView.setEnabled(false);
            quickActions.setVisibility(View.GONE);
            imageDoneUpdateView.setVisibility(View.GONE);
        }
    }

    public void setEditToggle()
    {

        if (HomeActivity.EDIT_MODE) {
            inputEntryTitleUpdateView.setEnabled(false);
            inputSubtitleEntryUpdateView.setEnabled(false);
            inputEntryUpdateView.setEnabled(false);
            quickActions.setVisibility(View.GONE);
            imageDoneUpdateView.setVisibility(View.GONE);
            HomeActivity.EDIT_MODE=false;
            return;
        } else {
            HomeActivity.EDIT_MODE=true;
            inputEntryTitleUpdateView.setEnabled(true);
            inputSubtitleEntryUpdateView.setEnabled(true);
            inputEntryUpdateView.setEnabled(true);
            quickActions.setVisibility(View.VISIBLE);
            imageDoneUpdateView.setVisibility(View.VISIBLE);
            imageEditToggle.setVisibility(View.GONE);
            imageAdapter.notifyDataSetChanged();
            musicAdapter.notifyDataSetChanged();

        }

    }
    public void getJournalEntrybyID(String id) {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users/" + currentUser.getUid() + "/Entries").document(id);


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
//                    Map<String, Object> doc = documentSnapshot.getData();

                    inputEntryUpdateView.setText(String.valueOf(doc.get("body")));
                    System.out.println("Body is " + doc.get("title"));
                    currentEntry.setTitle(String.valueOf(doc.get("title")));
                    inputEntryTitleUpdateView.setText(String.valueOf(doc.get("title")));
//                setBookmarked(Boolean.getBoolean(doc.get("bookmarked")));
//                    currentEntry.setSubtitle();
                    inputSubtitleEntryUpdateView.setText(String.valueOf(doc.get("subtitle")));

//                    currentEntry.setDateCreated();
                    textDateTimeUpdateView.setText(String.valueOf(doc.get("dateCreated")));
                    currentEntry.setEmotion(String.valueOf(doc.get("emotion")));

                    List<Map<String, Object>> musicDataList =
                            (List<Map<String, Object>>) doc.get("music");

                    if (musicDataList != null) {
                        List<Music> musicList = new ArrayList<>();

                        for (Map<String, Object> musicData : musicDataList) {
                            Music music = new Music();

                            // Assuming you have fields like "title", "artist", "imageUrl", and "musicUrl" in each Music object
                            music.setTitle((String) musicData.get("title"));
                            music.setArtist((String) musicData.get("artist"));
                            music.setImageResource((String) musicData.get("imageResource"));
                            music.setMusicUrl((String) musicData.get("musicUrl"));

                            musicList.add(music);
                        }
musicListUpdateView.addAll(musicList);
                        musicAdapter.notifyDataSetChanged();
                        currentEntry.setMusic(musicList);
                    } else {
                        currentEntry.setMusic(new ArrayList<>());
                        // Set an empty list if "music" field is null or empty
                    }

                    List<String> imageDataList = (List<String>) doc.get("images");

                    if (imageDataList != null) {
                        imageListUpdateView.addAll(imageDataList);
                        imageAdapter.notifyDataSetChanged();
//                        currentEntry.setImages(imageDataList);
                    } else {
                        currentEntry.setImages(new ArrayList<>());
                        // Set an empty list if "images" field is null or empty
                    }


                } else {
                    // Handle the case where the document does not exist
                    // You might want to show an error message or handle it based on your app's logic
                }
//                inputEntryUpdateView.setText(currentEntry.getTitle());
//                System.out.println("new sout"+currentEntry.getTitle());

            }
        });
    }

}
