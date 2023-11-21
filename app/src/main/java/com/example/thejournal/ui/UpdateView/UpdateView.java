package com.example.thejournal.ui.UpdateView;

import static androidx.fragment.app.FragmentManager.TAG;

import static com.example.thejournal.ui.home.HomeActivity.EDIT_MODE;
import static com.example.thejournal.ui.home.HomeActivity.REQUEST_CODE_ADD_INT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.example.thejournal.R;
import com.example.thejournal.adapters.ImageAdapter;
import com.example.thejournal.adapters.MusicAdapter;
import com.example.thejournal.models.JournalEntry;
import com.example.thejournal.models.Music;
import com.example.thejournal.ui.SpotiySearch.SpotifySearch;
import com.example.thejournal.ui.createentry.createJournalEntry;
import com.example.thejournal.ui.home.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UpdateView extends AppCompatActivity {

    private static final int IMAGE_PICKER_REQUEST = 8;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    EditText inputEntryTitleUpdateView, inputSubtitleEntryUpdateView, inputEntryUpdateView;
    ImageView bookmarkUpdateView, deleteButton,imageDoneUpdateView, musicSelectorUpdateView, imageSelectorUpdateView, imageEditToggle;
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
        EDIT_MODE = false;
        currentEntry = new JournalEntry();

        intent = getIntent();
        if(intent.getStringExtra("id")==null){finish();}

        ImageView imageBackUpdateView = findViewById(R.id.imageBackUpdateView);
        imageDoneUpdateView = findViewById(R.id.imageSaveUpdateView);
        inputEntryUpdateView = findViewById(R.id.inputEntryUpdateView);
        musicSelectorUpdateView = findViewById(R.id.imageAddMusicUpdateView);
        imageSelectorUpdateView = findViewById(R.id.entryimageAddImageUpdateView);
        deleteButton=findViewById(R.id.deleteEntry);
        imageListUpdateView = new ArrayList<>();
        quickActions=findViewById(R.id.layoutQuickActionsUV);
        imageEditToggle=findViewById(R.id.imageEditUpdateView);
        musicListUpdateView = new ArrayList<>();
        musicRecyclerUpdateView = findViewById(R.id.musicCreateRecyclerUpdateView);
        bookmarkUpdateView=findViewById(R.id.bookmarkUpdateView);
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

        musicSelectorUpdateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), SpotifySearch.class),
                        REQUEST_CODE_ADD_INT);

            }
        });
        imageSelectorUpdateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(UpdateView.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_STORAGE_PERMISSION);
                }
                else{
                    SetImage();
                }

            }
        });


        imageBackUpdateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity.EDIT_MODE=false;
                onBackPressed();
            }
        });


        getJournalEntrybyID(intent.getStringExtra("id"));
        //Edit Toggle
        imageEditToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditToggle();

            }
        });
        bookmarkUpdateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bookmarked){
                    bookmarked=false;
                    int bookmark_add = getResources().getIdentifier("com.example.thejournal:drawable/ic_bookmark_add", null, null);
                    bookmarkUpdateView.setImageResource(bookmark_add);
                }else
                {
                    bookmarked=true;
                    int bookmark_remove = getResources().getIdentifier("com.example.thejournal:drawable/baseline_bookmark_remove_24", null, null);
                    bookmarkUpdateView.setImageResource(bookmark_remove);
                }
            }
        });

        if (!EDIT_MODE) {
            inputEntryTitleUpdateView.setEnabled(false);
            inputSubtitleEntryUpdateView.setEnabled(false);
            inputEntryUpdateView.setEnabled(false);
            quickActions.setVisibility(View.GONE);
            imageDoneUpdateView.setVisibility(View.GONE);
        }
    }

    public void setEditToggle()
    {

        if (EDIT_MODE) {
            inputEntryTitleUpdateView.setEnabled(false);
            inputSubtitleEntryUpdateView.setEnabled(false);
            inputEntryUpdateView.setEnabled(false);
            quickActions.setVisibility(View.GONE);
            imageDoneUpdateView.setVisibility(View.GONE);
            EDIT_MODE=false;
            return;
        } else {
            EDIT_MODE=true;
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

                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            System.out.println(doc.getId());
                            db.collection("users/"+currentUser.getUid()+"/Entries").document(doc.getId())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @SuppressLint("RestrictedApi")
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @SuppressLint("RestrictedApi")
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error deleting document", e);
                                        }
                                    });
                        }
                    });

                    inputEntryUpdateView.setText(String.valueOf(doc.get("body")));
                    System.out.println("Body is " + doc.get("title"));
                    currentEntry.setTitle(String.valueOf(doc.get("title")));
                    inputEntryTitleUpdateView.setText(String.valueOf(doc.get("title")));
                    inputSubtitleEntryUpdateView.setText(String.valueOf(doc.get("subtitle")));
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

                    imageDoneUpdateView.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    System.out.println(doc.getId());
                                    DocumentReference ref = db.collection("users/" + currentUser.getUid() + "/Entries").document(id);

                                    ref.update("body", String.valueOf(inputEntryUpdateView.getText()));
                                    ref.update("music",musicListUpdateView);
                                    ref.update("title",String.valueOf(inputEntryTitleUpdateView.getText()));
                                    ref.update("subtitle",String.valueOf(inputSubtitleEntryUpdateView.getText()));
                                    if(bookmarked)
                                    {
                                        ref.update("bookmarked",bookmarked);
                                    }else
                                    {
                                        ref.update("bookmarked",!bookmarked);
                                    }
                                    ref.update("images",imageListUpdateView);
                                    ref.update("dateCreated",new SimpleDateFormat(
                                                    "EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()
                                            ).format(new Date())
                                    ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            EDIT_MODE=false;
                                            finish();
                                        }
                                    });
                                }
                            }
                    );


                } else {
                    // Handle the case where the document does not exist
                    // You might want to show an error message or handle it based on your app's logic
                }

            }
        });
    }
    public void SetImage()
    {
        Intent i=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager())!=null)
        {
            startActivityForResult(i,IMAGE_PICKER_REQUEST);
        }
    }


    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IMAGE_PICKER_REQUEST:
                if (resultCode == RESULT_OK && data != null) {
                    // Get the image data and display it in the ImageView
                    Uri imageUri = data.getData();
                    String imagePath = String.valueOf(imageUri);
                    imageListUpdateView.add(imagePath);
                    imageAdapter.notifyDataSetChanged();
                }
                break;

            case REQUEST_CODE_ADD_INT:
                if (resultCode == RESULT_OK && data != null) {
                    if (data.getStringExtra("artist") != null) {
                        Music m = new Music();
                        m.setArtist(data.getStringExtra("artist").trim());
                        m.setImageResource(data.getStringExtra("image").trim());
                        m.setTitle(data.getStringExtra("title").trim());
                        m.setMusicUrl(data.getStringExtra("url").trim());
                        musicListUpdateView.add(m);
                        musicAdapter.notifyDataSetChanged();
                    }
                }
                break;

            default:
                break;



        }

        Log.d(TAG, "Image List: " + imageListUpdateView);
    }

}
