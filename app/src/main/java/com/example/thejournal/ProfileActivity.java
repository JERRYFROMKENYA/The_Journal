package com.example.thejournal;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.thejournal.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
//import com.google.firebase.firestore.auth.User;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class User {
     private String uid, username, imageURL, age;

     public User(@Nullable String uid, String username, String imageURL, String age) {
         this.uid = uid;
         this.username = username;
         this.age = age;
         this.imageURL = imageURL;

     }
 }
public class ProfileActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText ageEditText;
    private Button submitButton;
    private ImageView imageView;
    private Button imagePickerButton;
    String uid, email, username, age;
    Uri imageUriforUpload;

    private FirebaseFirestore db;

    private static final int IMAGE_PICKER_REQUEST = 1;
    private Uri bitmapToUriConverter(Bitmap bitmap) {
        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
        // Initializing a new file
        // The bellow line return a directory in internal storage
        File file = wrapper.getDir("Images", MODE_PRIVATE);
        // Create a file to save the image
        file = new File(file, "profile.jpg");

        try {
            // Compress the bitmap and write it to the file
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Return the saved uri
        return Uri.parse(file.getAbsolutePath());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profileactivity);
        Intent intent = getIntent();
         uid = intent.getStringExtra("uid");
         email = intent.getStringExtra("email");





        db = FirebaseFirestore.getInstance();

        usernameEditText = findViewById(R.id.usernameEditText);
        ageEditText = findViewById(R.id.ageEditText);
        submitButton = findViewById(R.id.submitButton);
        imageView = findViewById(R.id.imageView);
        imagePickerButton = findViewById(R.id.imagePickerButton);
        TextView userEmail=findViewById(R.id.userEmail);
        userEmail.setText(email);
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        imagePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_PICKER_REQUEST);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString().trim();
                String age = ageEditText.getText().toString().trim();

                // Call the function to update the user's profile data in Firebase

                updateProfileData(username, age);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                // Get the image data and display it in the ImageView
                try {
                    Uri imageUri = data.getData();
                    imageUriforUpload=imageUri;
                    System.out.println(imageUriforUpload);

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void updateProfileData(String username, String age) {
        // Access a Cloud Firestore instance
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Update the user's display name in Firebase Auth
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                // You can also set other profile information here such as photo URL if available
                .build();

        // Update the user's profile data in Firebase Auth
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                        StorageReference imageRef = storageRef.child("images/" + user.getUid() + "/profile.jpg");
                        StorageTask<UploadTask.TaskSnapshot> taskSnapshotStorageTask = imageRef.putFile(imageUriforUpload)
                                .addOnSuccessListener(taskSnapshot -> {
                                    // Get the URL of the uploaded image
                                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                                String imageUrl = uri.toString();

                                                // Create a new user object with the provided data
//                                        User userData = new User(uid,username,age,imageUrl);
                                                // Create a new user with a first and last name
                                                Map<String, Object> userData = new HashMap<>();
                                                userData.put("uid", uid);
                                                userData.put("username", username);
                                                userData.put("born", age);
                                                userData.put("imageUrl", imageUrl);

// Add a new document with a generated ID
                                                db.collection("users")
                                                        .add(userData)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @SuppressLint("RestrictedApi")
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                                Intent myIntent = new Intent(ProfileActivity.this, MainActivity.class);
                                                                myIntent.putExtra("key", user.getEmail()); //Optional parameters
                                                                ProfileActivity.this.startActivity(myIntent);
                                                                finish();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @SuppressLint("RestrictedApi")
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error adding document", e);
                                                            }
                                                        });

                                                // Update the user's data in Firestore
//                                        db.collection("users")
//                                                .document(user.getUid())
//                                                .set(userData)
//                                                .addOnSuccessListener(documentReference -> {
//                                                    Toast.makeText(ProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
//                                                    // You can perform additional actions here, like navigating the user to the next screen
//                                                })
//                                                .addOnFailureListener(e -> {
//                                                    Toast.makeText(ProfileActivity.this, "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                });
                                    });
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(ProfileActivity.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });


                    } else {
                        Toast.makeText(ProfileActivity.this, "Error updating profile: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    }

