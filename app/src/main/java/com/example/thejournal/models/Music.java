package com.example.thejournal.models;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Music {
    String artist;
    String title;

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getImageResource() {
        return ImageResource;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    String ImageResource;

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageResource(String imageResource) {
        ImageResource = imageResource;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    String musicUrl;
    public List<Music> convertJsonToMusicList(String jsonString) {
        List<Music> musicList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Music music = new Music();
                music.setArtist(jsonObject.getString("artist"));
                music.setTitle(jsonObject.getString("name"));
                music.setImageResource(jsonObject.getString("image"));

                // Create the musicUrl by combining the type and id
                String type = jsonObject.getString("type");
                String id = jsonObject.getString("id");
                music.setMusicUrl("https://open.spotify.com/embed/" + type + "/" + id);

                musicList.add(music);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return musicList;
    }

}
