package com.example.thejournal.ui.createentry;

import static androidx.fragment.app.FragmentManager.TAG;

import static com.example.thejournal.ui.home.HomeActivity.EDIT_MODE;
import static com.example.thejournal.ui.home.HomeActivity.REQUEST_CODE_ADD_INT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thejournal.R;
import com.example.thejournal.adapters.ImageAdapter;
import com.example.thejournal.adapters.MediaAdapter;
import com.example.thejournal.adapters.MusicAdapter;
import com.example.thejournal.data.ApiHandler;
import com.example.thejournal.models.JournalEntry;
import com.example.thejournal.models.Music;
import com.example.thejournal.ui.SpotiySearch.SpotifySearch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class createJournalEntry extends AppCompatActivity {
    private static final int IMAGE_PICKER_REQUEST = 8;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    EditText inputEntryTitle, inputSubtitleEntry, inputEntry;
    ImageView bookmark, imageDone, musicSelector, imageSelector;
    boolean bookmarked = false;
    TextView textDateTime, generateImage;
    Intent intent;
    static List<Music> musicList;
    MusicAdapter musicAdapter;
    ImageAdapter imageAdapter;

    public static List<String> getImageList() {
        return imageList;
    }

    static List<String> imageList;
    RecyclerView musicRecycler, imageRecycler;

    public static List<Music> getMusicList() {
        return musicList;
    }


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EDIT_MODE = true;
        setContentView(R.layout.activity_create_journal_entry);
        intent = getIntent();
        ImageView imageBack = findViewById(R.id.imageBack);
        imageDone = findViewById(R.id.imageSave);
        inputEntry = findViewById(R.id.inputEntry);
        musicSelector = findViewById(R.id.imageAddMusic);
        imageSelector=findViewById(R.id.entryimageAddImage);
        imageList = new ArrayList<>();
        musicList = new ArrayList<>();
        musicRecycler=findViewById(R.id.musicCreateRecycler);
        imageRecycler=findViewById(R.id.imageCreateRecycler);
        inputEntryTitle = findViewById(R.id.inputEntryTitle);
        inputSubtitleEntry = findViewById(R.id.inputEntrySubtitle);
        textDateTime = findViewById(R.id.textDateAndTime);
        generateImage = findViewById(R.id.textGenerate);


//        mediaAdapter = new MediaAdapter(getApplicationContext(), musicList, imageList);
        musicRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

musicAdapter=new MusicAdapter(getApplicationContext(),musicList);
musicRecycler.setAdapter(musicAdapter);
imageRecycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

imageAdapter=new ImageAdapter(getApplicationContext(),imageList);
imageRecycler.setAdapter(imageAdapter);

        if (intent.getStringExtra("artist") != null) {
            Log.d(TAG, Objects.requireNonNull(intent.getStringExtra("title")));
            Music m = new Music();
            m.setArtist(intent.getStringExtra("artist").toString().trim());
            m.setImageResource(intent.getStringExtra("image").toString().trim());
            m.setTitle(intent.getStringExtra("title").toString().trim());
            m.setMusicUrl(intent.getStringExtra("url").toString().trim());
            musicList.add(m);
            musicAdapter.notifyDataSetChanged();
        }
        if (intent.getStringExtra("EntryTitle") != null) {
            inputEntryTitle.setText(intent.getStringExtra("EntryTitle"));
        }
        musicSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), SpotifySearch.class),
                        REQUEST_CODE_ADD_INT);

            }
        });

        imageSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
               {
                   ActivityCompat.requestPermissions(createJournalEntry.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_STORAGE_PERMISSION);
               }
               else{
                   SetImage();
               }

            }
        });


        generateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateSuggestions();

            }
        });
        textDateTime.setText(

                new SimpleDateFormat(
                        "EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()
                ).format(new Date())
        );
        bookmark = findViewById(R.id.bookmark);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imageDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Music");

                SaveEntry();
            }
        });
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookmarked) {
                    bookmarked = false;
                    int bookmark_add = getResources().getIdentifier("com.example.thejournal:drawable/ic_bookmark_add", null, null);
                    bookmark.setImageResource(bookmark_add);

                } else if (!bookmarked) {

                    bookmarked = true;
                    int bookmark_remove = getResources().getIdentifier("com.example.thejournal:drawable/baseline_bookmark_remove_24", null, null);
                    bookmark.setImageResource(bookmark_remove);
//                    bookmark.setImageDrawable(Drawable.createFromPath("@drawable/baseline_bookmark_remove_24"));

                }

            }
        });
    }

    private void SaveEntry() {
        EDIT_MODE = false;
        if (inputEntryTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Journal Entry " +
                    "Title Can't be empty", Toast.LENGTH_SHORT).show();
            return;
        } else if (inputSubtitleEntry.getText().toString().trim().isEmpty() &&
                inputEntry.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please Give This Entry " +
                    "Some Content", Toast.LENGTH_SHORT).show();

        } else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final JournalEntry entry = new JournalEntry();
            entry.setUid(user.getUid());
            entry.setTitle(inputEntryTitle.getText().toString());
            entry.setSubtitle(inputSubtitleEntry.getText().toString());
            entry.setBody(inputEntry.getText().toString());
            entry.setDateCreated(textDateTime.getText().toString());
            entry.setBookmarked(bookmarked);

            entry.setImages(imageList);
            entry.setMusic(musicList);
            System.out.println(String.valueOf(musicList) + imageList);
            entry.EntryUpload(new JournalEntry.OnUploadListener() {
                @Override
                public void onUploadSuccess() {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onUploadFailure(Exception e) {
                    // Handle failure
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void generateSuggestions() {
        if (inputSubtitleEntry.getText().toString().trim().isEmpty() && inputEntry.getText().toString().trim().isEmpty()) {
            String promptText = new ApiHandler().generateText(
                    "AIzaSyB5UooCRfiIKJP_xbcmtl-d4k-46t_JH6A",
                    "Generate one " +
                            "random healthy journaling prompt other than 'What are you grateful for today' only one result different " +
                            "every time make it is just one sentence " +
                            "nothing extra(no special characters only a-z and '?' no nothing, " +
                            "just a simple and complete sentence) " +
                            "make it different from the last one");
            if (promptText == null) {
                inputEntryTitle.setText("What do you want remember about today?");
            } else {
                inputEntryTitle.setText(promptText);
            }

        } else {
            Toast.makeText(this, "Looks Like you're already writing about something", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
        {
SetImage();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear the static list when the activity is destroyed
        musicList.clear();
        imageList.clear();

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if (resultCode == RESULT_OK) {
            // Check which request it is
            if (Objects.requireNonNull(data).getStringExtra("artist") != null) {
                Music m = new Music();
                m.setArtist(data.getStringExtra("artist").trim());
                m.setImageResource(data.getStringExtra("image").trim());
                m.setTitle(data.getStringExtra("title").trim());
                m.setMusicUrl(data.getStringExtra("url").trim());
                musicList.add(m);
                musicAdapter.notifyDataSetChanged();
            }
            Log.d(TAG, "Image List: " + imageList);
        }
    }

    public void SetImage()
    {
        Intent i=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
                    imageList.add(imagePath);
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
                        musicList.add(m);
                        musicAdapter.notifyDataSetChanged();
                    }
                }
                break;

            default:
                break;



}

        Log.d(TAG, "Image List: " + imageList);
    }



}