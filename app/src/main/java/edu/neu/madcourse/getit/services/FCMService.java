package edu.neu.madcourse.getit.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

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
import java.util.List;
import java.util.Scanner;

import edu.neu.madcourse.getit.GroupView;
import edu.neu.madcourse.getit.R;
import edu.neu.madcourse.getit.TestActivity;
import edu.neu.madcourse.getit.callbacks.GroupServiceCallbacks;
import edu.neu.madcourse.getit.callbacks.UserServiceCallbacks;
import edu.neu.madcourse.getit.models.Group;
import edu.neu.madcourse.getit.models.User;

public class FCMService extends FirebaseMessagingService {
    private static final String REFRESHED_TOKEN = " Received refresh token for device ->";

    private static final String TAG = FCMService.class.getSimpleName();

    private static final String SERVER_KEY = "key=AAAAWJBb2Jo:APA91bEiEqCeJUbSXd0lKQPyTgys3qtrP-7ZBdqLr_PkMHH54mjxsum9nFi-P-S771_TuiRmv_puv5dx3Zz_1tBTXUWv5-ubQD0RCXlbdevSJJuemNjePu5Mi95TGUej0MRWIu3YnuzO";

    private static final String CHANNEL_ID  = "CHANNEL_ID";
    private static final String CHANNEL_NAME  = "CHANNEL_NAME";
    private static final String CHANNEL_DESCRIPTION  = "CHANNEL_DESCRIPTION";

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
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]
        if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage);
        }

    }

    private void showNotification(RemoteMessage remoteMessage) {

        Intent intent = new Intent(this, TestActivity.class);
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
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(0,notification);

    }


    /**
     * Pushes a notification to a given device-- in particular, this device,
     * because that's what the instanceID token is defined to be.
     */
    private void sendMessageToDevice(String targetToken) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "Message Title");
            jNotification.put("body", "Message body ");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            /*
            // We can add more details into the notification if we want.
            // We happen to be ignoring them for this demo.
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            */
            jdata.put("title","data title");
            jdata.put("content","data content");

            /***
             * The Notification object is now populated.
             * Next, build the Payload that we send to the server.
             */

            // If sending to a single client
            jPayload.put("to", targetToken); // CLIENT_REGISTRATION_TOKEN);

            /*
            // If sending to multiple clients (must be more than 1 and less than 1000)
            JSONArray ja = new JSONArray();
            ja.put(CLIENT_REGISTRATION_TOKEN);
            // Add Other client tokens
            ja.put(FirebaseInstanceId.getInstance().getToken());
            jPayload.put("registration_ids", ja);
            */

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
                    Toast.makeText(getApplicationContext(),resp,Toast.LENGTH_LONG).show();
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

    public void sendNewGroupMemberNotification(String groupCode) {
        groupService.getGroupByGroupCode(groupCode, new GroupServiceCallbacks.GetGroupByGroupCodeCallback() {
            @Override
            public void onComplete(Group group) {
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++1111");
                for(String userId : group.getUsers()) {
                    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++2222");
                    userService.getUserByUserId(userId, new UserServiceCallbacks.GetUserByUserNameTaskCallback() {
                        @Override
                        public void onComplete(User user) {
                            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++ User token: " + user.getFirebase_token());
                            String token = user.getFirebase_token();
                            sendMessageToDevice(token);
                        }
                    });
                }
            }
        });
    }

}
