package com.example.thejournal.firebase;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

class ImageUpload {
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    private void setImageUrl(String u) {
        imageUrl=u;

    }

    public ImageUpload(String pathString, Uri imageU) {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child(pathString);
        StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = imageRef.putFile(imageU)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the URL of the uploaded image
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        setImageUrl(uri.toString());

                    });
                });


//        return imageUrl;

    }
}