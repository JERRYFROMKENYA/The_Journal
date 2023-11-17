package com.example.thejournal.ui.SpotiySearch;

import static com.example.thejournal.data.SpotifySearchData.convertJsonToMusicList;
import static com.example.thejournal.data.SpotifySearchData.searchSpotify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.thejournal.R;
import com.example.thejournal.adapters.SpotifyAdapter;
import com.example.thejournal.models.Music;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class SpotifySearch extends AppCompatActivity {
    RecyclerView musicRecycler;
    List<Music> musicList;
    EditText searchbar;
    SpotifyAdapter spotifyAdapter;
    ConstraintLayout weblayout;
    ImageView MusicSelect , musicBack;

    WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotifysearch);
    musicList=new ArrayList<>();

        searchbar = findViewById(R.id.inputMusicSearch);

weblayout=findViewById(R.id.playerlayout);
weblayout.setVisibility(View.GONE);
webView=findViewById(R.id.webPlayer);
MusicSelect=findViewById(R.id.MusicSelect);
musicBack=findViewById(R.id.musicBack);
        musicBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        musicRecycler = findViewById(R.id.musicrecycler);
        musicRecycler.setLayoutManager(
        new LinearLayoutManager(getApplicationContext())
);

        spotifyAdapter = new SpotifyAdapter(this,musicList,
                webView,weblayout,MusicSelect,this);
        musicRecycler.setAdapter(spotifyAdapter);

        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // This method is called to notify you that characters within `start` and `start + before` are about to be replaced with new text with length `count`.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {


                // This method is called to notify you that somewhere within `start` and `start + before`, `count` character(s) has been replaced.
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(searchbar.getText().toString().trim().isEmpty())
                {

                }
                else {
                    displayResults();
                }
                // This method is called to notify you that the characters within `Editable` have been changed.
                String newText = editable.toString();
                // Do something with the updated text
                // e.g., perform a search, update a list, etc.
            }
        });
    }
    private void displayResults()
    {
        String input=searchbar.getText().toString();
//                String input = "Ken Carson";
        JSONArray result = null;
        try {
            result = searchSpotify(input);
            musicList.clear();
            musicList.addAll(convertJsonToMusicList(result));
            spotifyAdapter.notifyDataSetChanged();


//                    Log.d(TAG, "Data fetched from Spotify:" + result);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        System.out.println(result.toString());
    }

    public void finishWithIntent(Music music)
    {
        Intent intent=new Intent();
        intent.putExtra("image",music.getImageResource());
        intent.putExtra("url", music.getMusicUrl());
        intent.putExtra("artist",music.getArtist());
        intent.putExtra("title",music.getTitle());
        setResult(RESULT_OK,intent);
        finish();
    }
}
