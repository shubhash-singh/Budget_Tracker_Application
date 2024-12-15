package com.example.budgettracker.NotificationSystem;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddNotification {

    private final FirebaseFirestore db;
    private static final String COLLECTION_NAME = "notifications";

    // Constructor
    public AddNotification() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Add a notification to Firestore.
     *
     * @param userId    The ID of the user.
     * @param message   The notification message.
     * @param title     The notification title.
     * @param roomId    The room ID (if applicable).
     * @param callback  A callback to handle success or failure.
     */
    public void addNotification(
            String userId,
            String message,
            String title,
            String roomId,
            NotificationCallback callback) {

        // Validate inputs
        if (userId == null || userId.isEmpty()) {
            callback.onFailure("User ID cannot be null or empty");
            return;
        }
        if (message == null || message.isEmpty()) {
            callback.onFailure("Message cannot be null or empty");
            return;
        }
        if (title == null || title.isEmpty()) {
            callback.onFailure("Title cannot be null or empty");
            return;
        }

        // Prepare the notification data
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userId", userId);
        notificationData.put("title", title);
        notificationData.put("message", message);
        notificationData.put("created_at", new Date());
        notificationData.put("roomId", roomId);

        // Add to Firestore
        db.collection(COLLECTION_NAME)
                .add(notificationData)
                .addOnSuccessListener(documentReference ->
                        callback.onSuccess(" added successfully")
                )
                .addOnFailureListener(e ->
                        callback.onFailure("Failed: " + e.getMessage())
                );
    }

    // Define the callback interface
    public interface NotificationCallback {
        void onSuccess(String message);
        void onFailure(String message);
    }
}
