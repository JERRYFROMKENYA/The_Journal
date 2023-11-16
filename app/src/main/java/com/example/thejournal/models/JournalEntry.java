package com.example.thejournal.models;
import java.util.concurrent.ExecutionException;
import java.util.regex.*;
import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JournalEntry {
    private String id;

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String[] getImages() {
        return images;
    }

    public String getBody() {
        return body;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public String[] getMusic() {
        return music;
    }

    public String[] getLocations() {
        return locations;
    }

    public String getEmotion() {
        return Emotion;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setMusic(String[] music) {
        this.music = music;
    }

    public void setLocations(String[] locations) {
        this.locations = locations;
    }

    public void setEmotion(String emotion) {
        Emotion = emotion;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    private String uid;
    private String title;
    private String subtitle;
    private String[] images;

    private String body;
    private String dateCreated;
    private String music[];
    private String locations[];
    private String Emotion;
    private boolean bookmarked;
    private int countMatches(String input, String regex, int flags) {
        Matcher matcher = Pattern.compile(regex, flags).matcher(input);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
    public  String analyzeMood(String paragraph) {
        int happyPoints = countMatches(paragraph, "\\b(?:happy|joyful|excited|content|delighted|pleased|elated|glad|cheerful|upbeat|ecstatic|satisfied|grateful|joyous|merry|radiant|smiling|festive|optimistic|thrilled|overjoyed|blessed|lively|jovial|blissful|eager|vibrant|pleasurable|sunny|exuberant|exhilarated|buoyant|jubilant|gleeful|animated|carefree|enthusiastic|heartwarming|spirited|hopeful|chipper|elated|positivity|enjoyable|pleasant|uplifting|ecstasy|sensational|sunny|festivity)\\b", Pattern.CASE_INSENSITIVE);
        int sadPoints = countMatches(paragraph, "\\b(?:sad|unhappy|depress|disappoint|misery|gloom|tearful|downcast|heartbroken|despondent|regret|somber|melanchol|forlorn|woeful|blue|dismal|crestfallen|unpleasant|bitter|pessimist|trouble|weary|despair|anguish|dreary|downhearted|lugubrious|sorrows|deject|downtrodden|unfortunate|grim|plaintive|moros|wretched|desolate|sulky|dishearten|dispirit|oppressiv|glum|downhearted)\\b", Pattern.CASE_INSENSITIVE);
        int angryPoints = countMatches(paragraph, "\\b(?:angry|irritated|frustrated|annoyed|enraged|furious|indignant|exasperated|outraged|incensed|irate|hostile|agitated|livid|vexed|resentful|bitter|mad|infuriated|upset|aggressive|displeased|disgruntled|cross|crabby|snappy|testy|peevish|bothered|offended|pissed off)\\b", Pattern.CASE_INSENSITIVE);
        int surprisedPoints = countMatches(paragraph, "\\b(?:surprised|amazed|astonished|shocked|stunned|startled|bewildered|dumbfounded|flabbergasted)\\b", Pattern.CASE_INSENSITIVE);
        int calmPoints = countMatches(paragraph, "\\b(?:calm|relaxed|peaceful|serene|tranquil|composed|untroubled|placid|soothing|mellow|easygoing|undisturbed|quiet|gentle|unperturbed)\\b", Pattern.CASE_INSENSITIVE);
        int disgustedPoints = countMatches(paragraph, "\\b(?:disgusted|revolted|repulsed|nauseated|abhorred|appalled|offended)\\b", Pattern.CASE_INSENSITIVE);
        int confusedPoints = countMatches(paragraph, "\\b(?:confused|baffled|perplexed|bewildered|puzzled|uncertain|doubtful)\\b", Pattern.CASE_INSENSITIVE);
        int hopefulPoints = countMatches(paragraph, "\\b(?:hopeful|optimistic|confident|encouraged|positive)\\b", Pattern.CASE_INSENSITIVE);

        int totalPoints = happyPoints - sadPoints + calmPoints - angryPoints + surprisedPoints + disgustedPoints + confusedPoints + hopefulPoints;

        if (totalPoints > 0) {
            return "Happy";
        } else if (totalPoints < 0) {
            return "Sad";
        } else if (calmPoints > angryPoints) {
            return "Calm";
        } else if (surprisedPoints > 0) {
            return "Surprised";
        } else if (disgustedPoints > 0) {
            return "Disgusted";
        } else if (confusedPoints > 0) {
            return "Confused";
        } else if (hopefulPoints > 0) {
            return "Hopeful";
        } else {
            return "Neutral";
        }
    }
    public JournalEntry(){
//        this.uid=UID;
//        this.title=TITLE;
//        this.subtitle=SUBTITLE;
//        this.images=IMAGES;
//        this.body=BODY;
//        this.music=MUSIC;
//        this.locations=LOCATIONS;
//        dateCreated=new Date();


    }
    public boolean EntryUpload()
    {
        this.Emotion = analyzeMood(title + " " + subtitle + " " + body);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> journalData = new HashMap<>();
        journalData.put("uid", uid);
        journalData.put("title", title);
        journalData.put("subtitle", subtitle);
        journalData.put("images", images);
        journalData.put("body", body);
        journalData.put("dateCreated", dateCreated);
        journalData.put("music", music);
        journalData.put("location", locations);
        journalData.put("emotion", Emotion);

        final boolean[] uploadSuccess = {false};

        Task<DocumentReference> task = db.collection("users/" + uid + "/Entries")
                .add(journalData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        uploadSuccess[0] = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        uploadSuccess[0] = false;
                    }
                });

        // Block the thread until the task is complete
        try {
            Tasks.await(task);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return uploadSuccess[0];


    }
}
