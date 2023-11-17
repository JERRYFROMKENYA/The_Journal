package com.example.thejournal.data;

import com.example.thejournal.models.Music;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SpotifySearchData {

    private static final String CLIENT_ID = "9aa8d81a57624c87b392bf242c872224";
    private static final String CLIENT_SECRET = "79e962f233ab409bbf78ee72baf08066";

    public static JSONArray searchSpotify(String input) throws JSONException {
        JSONArray resultsArray = new JSONArray();

        // API Access Token
        String accessToken = getAccessToken();

        // Get Req using keyword
        String queryVariable = input + "&type=track";
        JSONArray results = getSearchResults(accessToken, queryVariable);

        for (int i = 0; i < results.length(); i++) {
            JSONObject resultObject = results.getJSONObject(i);

            JSONObject simplifiedResult = new JSONObject();
            simplifiedResult.put("image", resultObject.getJSONObject("album").getJSONArray("images").getJSONObject(1).getString("url"));
            simplifiedResult.put("name", resultObject.getString("name"));
            simplifiedResult.put("type", resultObject.getString("type"));
            simplifiedResult.put("id", resultObject.getString("id"));
            simplifiedResult.put("artist", resultObject.getJSONArray("artists").getJSONObject(0).getString("name"));

            resultsArray.put(simplifiedResult);
        }

        return resultsArray;
    }
    public static List<Music> convertJsonToMusicList(JSONArray json) {
        List<Music> musicList = new ArrayList<>();

        try {
            JSONArray jsonArray = json;

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


    private static String getAccessToken() {
        try {
            URL url = new URL("https://accounts.spotify.com/api/token");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            String authParams = "grant_type=client_credentials&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET;

            connection.getOutputStream().write(authParams.getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getString("access_token");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private static JSONArray getSearchResults(String accessToken, String queryVariable) {
        try {
            URL url = new URL("https://api.spotify.com/v1/search?q=" + queryVariable+"&market=KE&limit=20");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getJSONObject("tracks").getJSONArray("items");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

//    public static void main(String[] args) throws JSONException {
//        String input = "Ken Carson";
//        JSONArray result = searchSpotify(input);
//        System.out.println(result.toString());
//    }
}
