package org.southcrest.hotcarbabyalarm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bmxgates.logger.BluetoothSerial;

public class MainActivity extends AppCompatActivity {
    private BluetoothSerial bluetoothSerial;

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
                        } else {
                            textView.setText("Baby off");
                        }
                    }
                });
                return bufferSize;
            }
        }, "HC");
    }

    protected void onResume() {
        super.onResume();

        //onResume calls connect, it is safe
        //to call connect even when already connected
        this.bluetoothSerial.onResume();
    }

    protected void onPause() {
        super.onPause();
        // avoid leaking stuff
        this.bluetoothSerial.onPause();
    }
}
