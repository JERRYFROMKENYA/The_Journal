package com.example.thejournal.ui.SpotiySearch;

import static com.example.thejournal.data.SpotifySearchData.convertJsonToMusicList;
import static com.example.thejournal.data.SpotifySearchData.searchSpotify;
import static com.example.thejournal.ui.createentry.createJournalEntry.getMusicList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
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
    ImageView MusicSelect, musicBack;

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotifysearch);
        musicList = new ArrayList<>();

        searchbar = findViewById(R.id.inputMusicSearch);

        weblayout = findViewById(R.id.playerlayout);
        weblayout.setVisibility(View.GONE);
        webView = findViewById(R.id.webPlayer);
        MusicSelect = findViewById(R.id.MusicSelect);
        musicBack = findViewById(R.id.musicBack);
        musicBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        musicRecycler = findViewById(R.id.musicrecycler);
        musicRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        spotifyAdapter = new SpotifyAdapter(this, musicList, webView, weblayout, MusicSelect, this);
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
                if (searchbar.getText().toString().trim().isEmpty()) {
                } else {
                    // Move the network operations to a background thread
                    new SearchTask().execute(editable.toString());
                }
            }
        });
    }

    private class SearchTask extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... params) {
            String input = params[0];
            try {
                return searchSpotify(input);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            // Update UI on the main thread with the search results
            musicList.clear();
            musicList.addAll(convertJsonToMusicList(result));
            spotifyAdapter.notifyDataSetChanged();
        }
    }

    public void finishWithIntent(Music music) {
        Intent intent = getIntent();
        intent.putExtra("image", music.getImageResource());
        intent.putExtra("url", music.getMusicUrl());
        intent.putExtra("artist", music.getArtist());
        intent.putExtra("title", music.getTitle());
        setResult(RESULT_OK, intent);
        finish();
    }
}
