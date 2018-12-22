package org.southcrest.hotcarbabyalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bmxgates.logger.BluetoothSerial;

public class MainActivity extends AppCompatActivity {
    private int state;  // 0: baby off, 1: baby on
    private BluetoothSerial bluetoothSerial;
    private BroadcastReceiver disconnectReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //MessageHandler is call when bytes are read from the serial input
        this.bluetoothSerial = new BluetoothSerial(this, new BluetoothSerial.MessageHandler() {
            @Override
            public int read(int bufferSize, byte[] buffer) {
                final String mystr = new String(buffer);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = findViewById(R.id.textView);
                        if (mystr.startsWith("1")) {
                            textView.setText("Baby on");
                            state = 1;
                        } else {
                            textView.setText("Baby off");
                            state = 0;
                        }
                    }
                });
                return bufferSize;
            }
        }, "HC");

        disconnectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TextView textView = findViewById(R.id.textView);
                textView.setText("Disconnected");
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(
                disconnectReceiver, new IntentFilter(BluetoothSerial.BLUETOOTH_DISCONNECTED));
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
        this.bluetoothSerial.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // avoid leaking stuff
        this.bluetoothSerial.onPause();
    }
}
