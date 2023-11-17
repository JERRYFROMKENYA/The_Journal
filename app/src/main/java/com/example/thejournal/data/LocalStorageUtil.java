package com.example.thejournal.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LocalStorageUtil {

    private static final String TAG = "LocalStorageUtil";

    public static String getLocalImagePath(Context context, String imageUrl) {
        // Use a hash function (e.g., MD5) to generate a unique key from the image URL
        // This key will be used as the filename for caching
        String filename = HashUtils.md5(imageUrl) + ".png";
        return context.getFilesDir() + File.separator + filename;
    }

    public static void saveBitmap(Context context, String filename, Bitmap bitmap) {
        // Ensure the directory exists
        File directory = new File(context.getFilesDir().toString());
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filePath = directory + File.separator + filename;

        try (FileOutputStream out = new FileOutputStream(filePath)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Log.i(TAG, "Bitmap saved successfully: " + filePath);
        } catch (IOException e) {
            Log.e(TAG, "Error saving bitmap: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Bitmap loadBitmap(Context context, String filename) {
        // You may want to use a more appropriate location and file naming scheme
        // For demonstration purposes, we use the image URL as the filename
        // This is just for illustration; you should use a more secure and efficient method

        String filePath = context.getFilesDir() + File.separator + filename;
        return BitmapFactory.decodeFile(filePath);
    }
}
