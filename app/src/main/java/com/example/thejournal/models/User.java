package com.example.thejournal.models;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class User {
    public class UserData{
        public String getUsername() {
            return username;
        }

        public String getBorn() {
            return born;
        }

        public String getUid() {
            return uid;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        private String username;
        private String born;
        private String uid;
        private String imageUrl;
        public Bitmap getImage()
        {
//        Log.e("src",(String) imageUrl);
            return getBitmapFromURL(this.imageUrl);

        }

    }

    public Map<String, Object> userData=new HashMap<String,Object>();
    private String username;
    private UserData udata;
    private String born;
    private String uid;

    public  Bitmap getBitmapFromURL(String src) {
        try {
//            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public Bitmap getImage()
    {
//        Log.e("src",(String) imageUrl);
        return getBitmapFromURL(userData.get("imageUrl").toString());

    }


    private String imageUrl;

    public Map<String, Object> getUserData() {
        return userData;
    }

    public String getUsername() {
        return username;
    }

    public String getBorn() {
        return born;
    }

    public String getUid() {
        return uid;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBorn(String born) {
        this.born = born;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUserData(Map<String, Object> userData) {
        this.userData = userData;
    }



    @SuppressLint("RestrictedApi")
    public User(String uid)
    {

        this.uid=uid;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(uid);
        Source source = Source.CACHE;
// Get the document, forcing the SDK to use the offline cache
//        DocumentReference docRef = db.collection("cities").document("BJ");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserData ud = documentSnapshot.toObject(UserData.class);
            }
        });
        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d(TAG, "Cached document data: " + document.getData());
                    userData.putAll(document.getData());
                    setBorn(document.getData().get("born").toString());
                    setImageUrl(document.getData().get("imageUrl").toString());
                    setUsername(document.getData().get("username").toString());
                } else {

                }
            }
        });

        Log.d(TAG,userData.toString());


    }
}
