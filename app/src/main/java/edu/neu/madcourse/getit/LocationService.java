package edu.neu.madcourse.getit;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;
import static edu.neu.madcourse.getit.App.CHANNEL_ID;

public class LocationService extends Service {
//    private final IBinder binder = new MyLocalBinder();
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, TestLocationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Alarm")
                .setContentText("You reached the location.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();

//        new DownloadFilesTask().execute();

        startForeground(1, notification);
        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY;
    }
//    @SuppressLint("StaticFieldLeak")
//    private class DownloadFilesTask extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            getLocation();
//            return null;
//        }
//
//    }

}
