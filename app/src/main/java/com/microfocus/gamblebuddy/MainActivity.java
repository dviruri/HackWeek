package com.microfocus.gamblebuddy;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

private int selected = -1;
private Button[] buttons ;
private int[] colors;
private BluetoothDevice bt;
private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothSocket mmSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int index = intent.getIntExtra("BT",0);
        BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();
        bt = (BluetoothDevice) pairedDevices.toArray()[index];

        try {
            mmSocket = bt.createInsecureRfcommSocketToServiceRecord(BTMODULEUUID);
            mmSocket.connect();
        }
        catch (Exception e)
        {
            finish();
        }

        setContentView(R.layout.activity_main);

        buttons = new Button[] {(Button)findViewById(R.id.greenButton),(Button)findViewById(R.id.redButton)};
        colors = new int[] {getResources().getColor(R.color.green),getResources().getColor(R.color.red)};

        TextView tv = (TextView)findViewById(R.id.device);
        tv.setText("Connected To " + bt.getName());

    }

    private void resetButton(int index)
    {
        Button btn = buttons[index];
        btn.setBackgroundColor(getResources().getColor(R.color.white));
        btn.setTextColor(colors[index]);

    }


    public void selectButton(View view) {
        Button btn = (Button)view;
        int tag = Integer.parseInt(btn.getTag().toString());
        if (selected != tag)
        {
            resetButton((tag-1)*-1);
            btn.setBackgroundColor(btn.getCurrentTextColor());
            btn.setTextColor(getResources().getColor(R.color.white));
            selected = tag;
        }


    }

    public void Gamble(View view) {
        try {
            OutputStream mmout=mmSocket.getOutputStream();
            byte[] toSend = {(byte)selected};//(selected & 0xFF)};
            mmout.write(toSend);
        }
        catch (Exception e)
        {

        }

    }
}
