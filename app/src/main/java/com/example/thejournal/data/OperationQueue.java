package com.example.thejournal.data;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Queue;

public class OperationQueue {
    private static final String QUEUE_PREFS = "OperationQueuePrefs";
    private static final String QUEUE_KEY = "OperationQueueKey";

    private static OperationQueue instance;
    private Queue<Runnable> queue;
    private boolean isProcessing;
    private Handler handler;
    private Context appContext; // Added to store the application context

    private OperationQueue(Context context) {
        queue = new LinkedList<>();
        isProcessing = false;
        appContext = context.getApplicationContext(); // Store the application context

        HandlerThread handlerThread = new HandlerThread("OperationQueueThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    public static synchronized OperationQueue getInstance(Context context) {
        if (instance == null) {
            instance = new OperationQueue(context);
        }
        return instance;
    }

    public synchronized void addOperation(Runnable operation) {
        queue.offer(operation);
        saveQueueToPrefs();
        processQueue();
    }

    private synchronized void processQueue() {
        if (isNetworkAvailable() && !isProcessing && !queue.isEmpty()) {
            isProcessing = true;
            Runnable operation = queue.poll();
            handler.post(operation);
        }
    }

    public synchronized void operationComplete() {
        isProcessing = false;
        saveQueueToPrefs();
        processQueue();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void saveQueueToPrefs() {
        Gson gson = new Gson();
        String json = gson.toJson(queue);
        appContext.getSharedPreferences(QUEUE_PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(QUEUE_KEY, json)
                .apply();
    }

    private void loadQueueFromPrefs() {
        Gson gson = new Gson();
        String json = appContext.getSharedPreferences(QUEUE_PREFS, Context.MODE_PRIVATE)
                .getString(QUEUE_KEY, "");
        if (!json.isEmpty()) {
            Type type = new TypeToken<Queue<Runnable>>() {}.getType();
            queue = gson.fromJson(json, type);
        }
    }
}
