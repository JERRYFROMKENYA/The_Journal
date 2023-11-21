package com.example.thejournal.models;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.*;
import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.thejournal.models.Music;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
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

    public List<String> getImages() {
        return images;
    }

    public String getBody() {
        return body;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public List<Music> getMusic() {
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

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setMusic(List<Music> music) {
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
    private List<String> images;

    private String body;
    private String dateCreated;
    private List<Music> music;
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

    public String analyzeMood(String paragraph) {
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

    public JournalEntry() {
//        this.uid=UID;
//        this.title=TITLE;
//        this.subtitle=SUBTITLE;
//        this.images=IMAGES;
//        this.body=BODY;
//        this.music=MUSIC;
//        this.locations=LOCATIONS;
//        dateCreated=new Date();
    }

    public void uploadImages(List<String> imagePaths, final OnUploadImagesListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Create a reference to the location where you want to store the images
        StorageReference imagesRef = storageRef.child("users/" + uid + "/images/");

        List<String> uploadedImageUrls = new ArrayList<>();

        // Upload each image
        for (String imagePath : imagePaths) {
            // Create a reference to the image file
            Uri file = Uri.parse(imagePath);

            // Create a reference to store the image in a unique name (you may customize this)
            StorageReference imageRef = imagesRef.child(file.getLastPathSegment());

            // Upload the file and metadata
            UploadTask uploadTask = imageRef.putFile(file);

            // Register observers to listen for when the upload is done or if it fails
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image uploaded successfully
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            System.out.println("This is" + imageUrl);
                            // Add the URL to the list
                            uploadedImageUrls.add(imageUrl);

                            // Check if all images are uploaded

                                // All images uploaded successfully

                                listener.onUploadImagesSuccess(uploadedImageUrls);


                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle unsuccessful uploads
                    listener.onUploadImagesFailure(e);
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                }
            });
        }
    }



    public void EntryUpload(final OnUploadListener listener) {
        CompletableFuture.runAsync(() -> {
            // Call the uploadImages function and upload the document
            this.Emotion = analyzeMood(title + " " + subtitle + " " + body);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> journalData = new HashMap<>();

            if (!images.isEmpty()) {
                this.uploadImages(images, new JournalEntry.OnUploadImagesListener() {
                    @Override
                    public void onUploadImagesSuccess(List<String> imageUrls) {
                        System.out.println("upload success"+ imageUrls);
                        journalData.put("uid", uid);
                        journalData.put("title", title);
                        journalData.put("subtitle", subtitle);
                        journalData.put("body", body);
                        journalData.put("dateCreated", dateCreated);
                        journalData.put("music", music);
                        journalData.put("location", locations);
                        journalData.put("emotion", Emotion);
                        journalData.put("images", imageUrls);
                        db.collection("users/" + uid + "/Entries")
                                .add(journalData)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @SuppressLint("RestrictedApi")
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                        listener.onUploadSuccess();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @SuppressLint("RestrictedApi")
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                        listener.onUploadFailure(e);
                                    }
                                });





                    }

                    @Override
                    public void onUploadImagesFailure(Exception e) {

                    }
                });
            }
            else {
                journalData.put("uid", uid);
                journalData.put("title", title);
                journalData.put("subtitle", subtitle);
                journalData.put("body", body);
                journalData.put("dateCreated", dateCreated);
                if(music==null)
                {
                    journalData.put("music", new ArrayList<>());
                }
                else
                {
                    journalData.put("music", music);
                }

                journalData.put("images",new ArrayList<>());
                journalData.put("location", locations);
                journalData.put("emotion", Emotion);
                db.collection("users/" + uid + "/Entries")
                        .add(journalData)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @SuppressLint("RestrictedApi")
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                listener.onUploadSuccess();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @SuppressLint("RestrictedApi")
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                                listener.onUploadFailure(e);
                            }
                        });
            }




        });
    }



    public interface OnUploadListener {
        void onUploadSuccess();

        void onUploadFailure(Exception e);
    }

    public interface OnUploadImagesListener {
        void onUploadImagesSuccess(List<String> imageUrls);

        void onUploadImagesFailure(Exception e);
    }
}

