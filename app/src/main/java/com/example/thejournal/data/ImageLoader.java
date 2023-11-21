package com.example.thejournal.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class ImageLoader {

    private static final String TAG = "ImageLoader";

    private static ImageLoader instance;

    private ImageLoader() {
        // Private constructor to enforce singleton pattern
    }

    public static ImageLoader getInstance() {
        if (instance == null) {
            instance = new ImageLoader();
        }
        return instance;
    }

    public void loadImage(Context context, String imageUrl, ImageView imageView) {
        // Check if the image is cached locally
        if (isImageCachedLocally(context, imageUrl)) {
            // Load the image from the local cache
            loadCachedImage(context, imageUrl, imageView);
        } else {
            // Image is not cached, download and cache it
            downloadAndCacheImage(context, imageUrl, imageView);
        }
    }

    private void loadCachedImage(Context context, String imageUrl, ImageView imageView) {
        Picasso.get()
                .load(LocalStorageUtil.getLocalImagePath(context, imageUrl))
                .into(imageView);
    }

    private void downloadAndCacheImage(Context context, String imageUrl, ImageView imageView) {
        Picasso.get()
                .load(imageUrl)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        // Save the image to local storage
                        LocalStorageUtil.
                                saveBitmap(context, HashUtils.md5(imageUrl) + ".png", bitmap);

                        // Display the image in the ImageView
                        imageView.setImageBitmap(bitmap);
                        Log.i(TAG, "Image loaded successfully: " + imageUrl);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        Log.e(TAG, "Failed to download image: " + e.getMessage());
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        // Do nothing here
                    }
                });
    }

    private boolean isImageCachedLocally(Context context, String imageUrl) {
        // Implement logic to check if the image is already cached locally
        // You can use SharedPreferences, SQLite, or any other local storage mechanism
        // For simplicity, let's assume you use SharedPreferences for now
        // This is just for demonstration purposes

        // Check if the image URL is present in SharedPreferences
        return isImageCachedInSharedPreferences(context, imageUrl);
    }

    private void saveImageToSharedPreferences(Context context, String imageUrl) {
        // Implement logic to save the image URL to SharedPreferences
        // For simplicity, let's use a simple key-value pair where the key is the image URL
        // and the value is a boolean indicating whether the image is cached
        // This is just for demonstration purposes
        context.getSharedPreferences("ImageCache", Context.MODE_PRIVATE)
                .edit()
                .putBoolean(imageUrl, true)
                .apply();
    }

    private boolean isImageCachedInSharedPreferences(Context context, String imageUrl) {
        // Implement logic to check if the image URL is present in SharedPreferences
        // This is just for demonstration purposes
        return context.getSharedPreferences("ImageCache", Context.MODE_PRIVATE)
                .getBoolean(imageUrl, false);
    }
    public static void loadImageNoCache(Context context, String imageUrl, ImageView imageView) {
        Picasso.get().load(imageUrl).into(imageView);
    }
}

