package edu.neu.madcourse.getit.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import edu.neu.madcourse.getit.R;
import edu.neu.madcourse.getit.TestActivity;
import edu.neu.madcourse.getit.YourGroupsActivity;
import edu.neu.madcourse.getit.callbacks.FCMServiceCallBacks;
import edu.neu.madcourse.getit.callbacks.GroupServiceCallbacks;
import edu.neu.madcourse.getit.callbacks.UserServiceCallbacks;
import edu.neu.madcourse.getit.models.Group;
import edu.neu.madcourse.getit.models.User;

public class FCMService extends FirebaseMessagingService {
    private static final String REFRESHED_TOKEN = "Received refresh token for device ->";

    private static final String TAG = FCMService.class.getSimpleName();
    private static final String SERVER_KEY = "key=AAAAWJBb2Jo:APA91bEiEqCeJUbSXd0lKQPyTgys3qtrP-7ZBdqLr_PkMHH54mjxsum9nFi-P-S771_TuiRmv_puv5dx3Zz_1tBTXUWv5-ubQD0RCXlbdevSJJuemNjePu5Mi95TGUej0MRWIu3YnuzO";

    private static final String CHANNEL_ID  = "GETIT_CHANNEL_ID";
    private static final String CHANNEL_NAME  = "GETIT";
    private static final String CHANNEL_DESCRIPTION  = "GETIT NOTIFICATION CHANNEL";

    GroupService groupService = new GroupService();

    private FirebaseAuth fAuth;
    private UserService userService = new UserService();

    @Override
    public void onNewToken(String token) {
        Log.d(REFRESHED_TOKEN, "Refreshed token: " + token);

        fAuth = FirebaseAuth.getInstance();
        String userDocId = fAuth.getCurrentUser().getUid();

        userService.updateUserDeviceToken(userDocId, token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage);
        }

    }

    private void showNotification(RemoteMessage remoteMessage) {

        Intent intent = new Intent(this, YourGroupsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification notification;
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel
            notificationChannel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(this,CHANNEL_ID);
        }
        else {
            builder = new NotificationCompat.Builder(this);
        }
        notification = builder.setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setSmallIcon(R.mipmap.ic_launcher_grocery_round)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(0,notification);

    }

    /**
     * Pushes a notification to a given device-- in particular, this device,
     * because that's what the instanceID token is defined to be.
     */
    private void sendMessageToDevice(String targetToken, String notificationText) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "GetIt!!");
            jNotification.put("body", notificationText);
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");

            jdata.put("title","data title");
            jdata.put("content","data content");
            jPayload.put("to", targetToken); // CLIENT_REGISTRATION_TOKEN);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data",jdata);


            /***
             * The Payload object is now populated.
             * Send it to Firebase to send the message to the appropriate recipient.
             */
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "run: " + resp);
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

    public void sendNewGroupMemberNotification(String groupCode, String newGroupMemberName, FCMServiceCallBacks.sendNewGroupMemberNotificationCallback callback) {
        groupService.getGroupByGroupCode(groupCode, new GroupServiceCallbacks.GetGroupByGroupCodeCallback() {
            @Override
            public void onComplete(Group group) {
                String notificationText = newGroupMemberName + " has joined your group " + group.getGroup_name();
                for(String userId : group.getUsers()) {
                    userService.getUserByUserId(userId, new UserServiceCallbacks.GetUserByUserIdTaskCallback() {
                        @Override
                        public void onComplete(User user) {
                            String token = user.getFirebase_token();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    sendMessageToDevice(token, notificationText);
                                }
                            }).start();

                        }
                    });
                }
                callback.onComplete();
            }
        });
    }

    public void sendUserGettingItemNotification(String itemName, String userRequestedId, String userGettingItName, FCMServiceCallBacks.sendNewGroupMemberNotificationCallback callback) {
        userService.getUserByUserId(userRequestedId, new UserServiceCallbacks.GetUserByUserIdTaskCallback() {
            @Override
            public void onComplete(User user) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String notificationText = userGettingItName + " is getting " + itemName;
                        sendMessageToDevice(user.getFirebase_token(), notificationText);
                    }
                }).start();
                callback.onComplete();
            }
        });
    }
}
