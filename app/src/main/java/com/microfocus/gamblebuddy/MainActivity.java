package com.microfocus.gamblebuddy;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;
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
    private int selectedDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        selectedDevice = intent.getIntExtra("BT",0);
        new BTAsyncTask().execute();
        setContentView(R.layout.activity_main);

        buttons = new Button[] {(Button)findViewById(R.id.redButton),(Button)findViewById(R.id.greenButton),(Button)findViewById(R.id.blueButton)};
        colors = new int[] {getResources().getColor(R.color.red),getResources().getColor(R.color.green),getResources().getColor(R.color.blue)};

    }

    private void resetButtons(int index)
    {
        for (int i=0; i<buttons.length;i++)
        {
            if (i!=index)
            {
                Button btn = buttons[i];
                btn.setBackgroundColor(getResources().getColor(R.color.white));
                btn.setTextColor(colors[i]);
            }
        }
    }


    public void selectButton(View view) {
        Button btn = (Button)view;
        int tag = Integer.parseInt(btn.getTag().toString());
        if (selected != tag)
        {
            resetButtons(tag);
            btn.setBackgroundColor(btn.getCurrentTextColor());
            btn.setTextColor(getResources().getColor(R.color.white));
            selected = tag;
        }


    }

    public void Gamble(View view) {
        if (selected != -1)
        {
            new GambleAsyncTask().execute();
        }
        else
        {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("No Color Selected");
            alertDialog.setMessage("You must select color in order to place bet!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

    }

    ProgressDialog progressDialog;
    int winner;

    private class GambleAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            super.onPreExecute();
        }

        protected Void doInBackground(Void... args) {
            try {
                OutputStream mmout = mmSocket.getOutputStream();
                byte[] toSend = {(byte) selected};//(selected & 0xFF)};
                mmout.write(toSend);

                InputStream mmin = mmSocket.getInputStream();
                winner = mmin.read();

            } catch (Exception e) {

            }
            return null;
        }

        protected void onPostExecute(Void result) {
            ImageView img = (ImageView) findViewById(R.id.result);
            if (winner == selected) {
                img.setImageResource(R.drawable.winner);
            } else {
                img.setImageResource(R.drawable.loser);
            }
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            //move activity
            super.onPostExecute(result);
        }
    }

    private class BTAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        protected Void doInBackground(Void... args) {
            BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();
            bt = (BluetoothDevice) pairedDevices.toArray()[selectedDevice];

            try {
                mmSocket = bt.createInsecureRfcommSocketToServiceRecord(BTMODULEUUID);
                mmSocket.connect();
            }
            catch (Exception e)
            {
                finish();
            }
            return null;
        }

        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
        }
    }
}
