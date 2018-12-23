package org.southcrest.hotcarbabyalarm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bmxgates.logger.BluetoothSerial;
import org.southcrest.hotcarbabyalarm.Global;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //MessageHandler is call when bytes are read from the serial input
        Global.bluetoothSerial = new BluetoothSerial(this, new BluetoothSerial.MessageHandler() {
            @Override
            public int read(int bufferSize, byte[] buffer) {
                final String mystr = new String(buffer);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = findViewById(R.id.textView);
                        if (mystr.startsWith("1")) {
                            textView.setText("Baby on");
                            Global.state = 1;
                        } else {
                            textView.setText("Baby off");
                            Global.state = 0;
                        }
                    }
                });
                return bufferSize;
            }
        }, "HC");

        Global.myIntent = new Intent(this, MainActivity.class);
        Global.pendingIntent = PendingIntent.getActivity(this, 0, Global.myIntent, 0);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] v = {500,1000};
        Global.notificationBuilder = new NotificationCompat.Builder(MainActivity.this)
                .setContentTitle("Child on Seat")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(Global.pendingIntent)
                .setContentText("Child left on seat!")
                .setAutoCancel(true)
                .setSound(uri)
                .setVibrate(v);

        Global.notificationManager = NotificationManagerCompat.from(this);

        Global.disconnectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TextView textView = findViewById(R.id.textView);
                textView.setText("Disconnected");
                if (Global.state == 1) {
                    Global.notificationManager.notify(1, Global.notificationBuilder.build());
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(
                Global.disconnectReceiver, new IntentFilter(BluetoothSerial.BLUETOOTH_DISCONNECTED));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //onResume calls connect, it is safe
        //to call connect even when already connected
        Global.bluetoothSerial.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // avoid leaking stuff
        Global.bluetoothSerial.onPause();
    }
}
