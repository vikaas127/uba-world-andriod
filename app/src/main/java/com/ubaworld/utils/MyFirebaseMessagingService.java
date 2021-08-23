package com.ubaworld.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ubaworld.R;
import com.ubaworld.activity.Dashboard;
import com.ubaworld.activity.thread.ThreadActivity;

import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    String commentId;
    String commentType;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Log.e(TAG, "TOKEN: " + refreshedToken);
        Utils.saveToUserDefaults(this, Constants.KEY_FIREBASE_TOKEN, refreshedToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getTitle());
            Log.e("msg", "Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.e("msg", "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Log.e("msg", "Message Data: " + remoteMessage.getData().toString());
            Log.e("msg", "id " + remoteMessage.getData().get("comment_id"));
            try {
                Map<String, String> params = remoteMessage.getData();
                JSONObject object = new JSONObject(params);
                commentId=remoteMessage.getData().get("comment_id");
                commentType=remoteMessage.getData().get("comment_type");
                Log.e("JSON_OBJECT", object.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            sendNotification(remoteMessage.getNotification().getTitle());
        }
    }

    private void sendNotification(String body) {
        String channelId = "NotificationManager_00";
        String channelName = "NotificationManager";

        Intent intent = new Intent(this, Dashboard.class);
        intent.putExtra("comment_id",Integer.parseInt(commentId));
        intent.putExtra("comment_type",Integer.parseInt(commentType));
        intent.putExtra("isFromMessage", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setContentIntent(pendingIntent);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.drawable.ic_notification);
        } else {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }

        notificationManager.notify(Integer.parseInt(commentId), mBuilder.build());
    }

}
