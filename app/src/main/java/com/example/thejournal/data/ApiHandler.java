package com.example.thejournal.data;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApiHandler {

    public static String generateText(String apiKey, String promptText) {
        try {
            // API endpoint
            String apiUrl = "https://generativelanguage.googleapis.com" +
                    "/v1beta2/models/text-bison-001:generateText?key=" + apiKey;

            // JSON payload
            String jsonInputString = "{\"prompt\": {\"text\": \"" + promptText + "\"}}";

            // Create URL object
            URL url = new URL(apiUrl);

            // Open a connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            urlConnection.setRequestMethod("POST");

            // Set the Content-Type header
            urlConnection.setRequestProperty("Content-Type", "application/json");

            // Enable input/output streams
            urlConnection.setDoOutput(true);

            // Write the JSON payload to the output stream
            try (DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream())) {
                outputStream.writeBytes(jsonInputString);
                outputStream.flush();
            }

            // Check the HTTP response code
            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line).append("\n");
                    }

                    // Parse JSON response and extract "output" value
                    return parseJsonResponse(response.toString());
                }
            } else {
                // Handle HTTP error
                System.out.println("HTTP error: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle other exceptions
            return null;
        }
    }

    private static String parseJsonResponse(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray candidatesArray = jsonObject.getJSONArray("candidates");
            if (candidatesArray.length() > 0) {
                JSONObject firstCandidate = candidatesArray.getJSONObject(0);
                return firstCandidate.getString("output");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
