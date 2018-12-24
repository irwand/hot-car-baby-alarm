package org.southcrest.hotcarbabyalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.bmxgates.logger.BluetoothSerial;


class SerialHandler implements BluetoothSerial.MessageHandler {
    private BabyMonitor babyMonitor;

    public SerialHandler(BabyMonitor bm) {
        babyMonitor = bm;
    }

    @Override
    public int read(int bufferSize, byte[] buffer) {
        final String text = new String(buffer);
        babyMonitor.onSerialRead(text);
        return bufferSize;
    }
}

class DisconnectReceiver extends BroadcastReceiver {
    private BabyMonitor babyMonitor;

    public DisconnectReceiver(BabyMonitor bm) {
        babyMonitor = bm;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        babyMonitor.onDisconnect();
    }
}

public class BabyMonitor extends Service {

    private static int NOTIFICATION_ID = 1234;
    private Boolean babyOnSeat;
    private Boolean monitorStarted;
    private BluetoothSerial bluetoothSerial;
    private BroadcastReceiver disconnectReceiver;

    private Notification getStateNotification(String title, String text) {
        Intent notificationIntent = new Intent(this, BabyMonitor.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        return new Notification.Builder(this)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .setTicker(text)
                        .build();

    }

    private void updateNotificationText(String title, String text) {
        Notification notification = getStateNotification(title, text);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void onSerialRead(String text) {
        if (text.startsWith("1")) {
            babyOnSeat = true;
            updateNotificationText("Baby on", "Baby on");
        } else {
            babyOnSeat = false;
            updateNotificationText("Baby off", "Baby off");
        }
    }

    public void onDisconnect() {
        if (babyOnSeat) {
            Intent myIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, myIntent, 0);

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            long[] v = {500,1000};
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(BabyMonitor.this)
                    .setContentTitle("Child on Seat")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setContentText("Child left on seat!")
                    .setAutoCancel(true)
                    .setSound(uri)
                    .setVibrate(v);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(1, notificationBuilder.build());
        }
        stopSelf();
    }

    @Override
    public void onCreate() {
        monitorStarted = false;
        babyOnSeat = false;
        bluetoothSerial = new BluetoothSerial(this, new SerialHandler(this), "HC");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (monitorStarted) {
            // If we get killed, after returning from here, restart
            return START_STICKY;
        }

        Toast.makeText(this, "Baby monitor starting", Toast.LENGTH_SHORT).show();

        startForeground(this.NOTIFICATION_ID, getStateNotification("Baby Monitor", "Connecting1..."));

        bluetoothSerial.onResume();

        disconnectReceiver = new DisconnectReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                disconnectReceiver, new IntentFilter(BluetoothSerial.BLUETOOTH_DISCONNECTED));

        monitorStarted = true;

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Baby Monitor done", Toast.LENGTH_SHORT).show();
        bluetoothSerial.onPause();
        bluetoothSerial.close();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(disconnectReceiver);
        monitorStarted = false;
    }
}
