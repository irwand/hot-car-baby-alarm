package org.southcrest.hotcarbabyalarm;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.bmxgates.logger.BluetoothSerial;

public class Global {
    public static int state;  // 0: baby off, 1: baby on
    public static BluetoothSerial bluetoothSerial;
    public static BroadcastReceiver disconnectReceiver;
    public static Intent myIntent;
    public static PendingIntent pendingIntent;
    public static NotificationCompat.Builder notificationBuilder;
    public static NotificationManagerCompat notificationManager;
}
