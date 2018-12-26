package com.microfocus.gamblebuddy;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    // Helpful Constants
    private final int NR_OF_SIMULTANEOUS_SOUNDS = 1;
    private final float LEFT_VOLUME = 1.0f;
    private final float RIGHT_VOLUME = 1.0f;
    private final int NO_LOOP = 0;
    private final int PRIORITY = 0;
    private final float NORMAL_PLAY_RATE = 1.0f;

    private SoundPool mSoundPool;
    private int winSoundId;
    private int loseSoundId;

    private int[] _results = new int[]{R.drawable.red,R.drawable.green,R.drawable.blue};
private int selected = -1;
private Button[] buttons ;
private int[] colors;
private BluetoothDevice bt;
private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothSocket mmSocket;
    private int selectedDevice;
    private Button gambleButton;
    ProgressDialog progressDialog;
    int winner;
    private ImageView resultText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        selectedDevice = intent.getIntExtra("BT",0);
        new BTAsyncTask().execute();
        setContentView(R.layout.activity_main);

        buttons = new Button[] {(Button)findViewById(R.id.redButton),(Button)findViewById(R.id.greenButton),(Button)findViewById(R.id.blueButton)};
        colors = new int[] {getResources().getColor(R.color.red),getResources().getColor(R.color.green),getResources().getColor(R.color.blue)};

        resultText = (ImageView)findViewById(R.id.resultText);

        resultText.setVisibility(View.GONE);
        gambleButton = (Button)findViewById(R.id.gampleButton);
        gambleButton.setEnabled(true);
        gambleButton.setClickable(true);
        gambleButton.setAlpha(1);

        mSoundPool = new SoundPool(NR_OF_SIMULTANEOUS_SOUNDS,AudioManager.STREAM_MUSIC,0);
        winSoundId = mSoundPool.load(getApplicationContext(),R.raw.winner,1);
        loseSoundId = mSoundPool.load(getApplicationContext(),R.raw.loser,1);
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
            gambleButton.setEnabled(false);
            gambleButton.setClickable(false);
            gambleButton.setAlpha(0.5f);
            resultText.setVisibility(View.GONE);
            final ImageView img = (ImageView) findViewById(R.id.result);
            img.setImageResource(R.drawable.unknown);
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


    /*
        private void Reset()
        {
            final ImageView img = (ImageView) findViewById(R.id.result);
            img.setImageResource(R.drawable.red);
            gambleButton.setEnabled(true);
            gambleButton.setClickable(true);
            gambleButton.setAlpha(1);
        }

        private void showWinnerDialog()
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Reset();
                }
            });

            final int Layout = winner==selected ? _alertDialogs[winner]: _alertDialogs[winner+3];

            final AlertDialog dialog = builder.create();
            LayoutInflater inflater = getLayoutInflater();
            View dialogLayout = inflater.inflate(Layout, null);
            dialog.setView(dialogLayout);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            final int Image = winner==selected ? _results[winner] : _results[winner+3];

            dialog.show();



            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface d) {
                    ImageView image = (ImageView) dialog.findViewById(R.id.goProDialogImage);
                    Bitmap icon = BitmapFactory.decodeResource(MainActivity.this.getResources(),
                            Image);
                    float imageWidthInPX = (float)image.getWidth();

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
                            Math.round(imageWidthInPX * (float)icon.getHeight() / (float)icon.getWidth()));
                    image.setLayoutParams(layoutParams);


                }
            });
        }
    */
    private class GambleAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Spin in Progress...");
            progressDialog.show();  /*
            try
            {
                Animation rotate = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate_image);
                rotate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        final ImageView img = (ImageView) findViewById(R.id.result);
                        Animation rotate120 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate_120);
                        Animation rotate240 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate_220);

                        rotate120.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                img.setImageResource(R.drawable.blue);
                                try {
                                    sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                showWinnerDialog();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        rotate240.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                img.setImageResource(R.drawable.green);

                                try {
                                    sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                showWinnerDialog();

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        switch (winner)
                        {
                            case 0 : img.setImageResource(R.drawable.red);
                                     showWinnerDialog();
                                     try {
                                        sleep(1000);
                                     } catch (InterruptedException e) {
                                        e.printStackTrace();
                                     }
                                     break;
                            case 1 : img.startAnimation(rotate240); break;
                            case 2 : img.startAnimation(rotate120); break;
                        }

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
// Start animating the image
                final ImageView splash = (ImageView) findViewById(R.id.result);
                //   Animation rotate125 = AnimationUtils.loadAnimation(this,R.anim.rotate_image125);
                splash.startAnimation(rotate);

            } catch (Exception e)
            {

            }  */
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
            final ImageView img = (ImageView) findViewById(R.id.result);
            img.setImageResource(_results[winner]);
            if (progressDialog.isShowing()) progressDialog.dismiss();
            if (winner == selected) {
                resultText.setImageResource(R.drawable.winner22);
            } else {
                resultText.setImageResource(R.drawable.loser22);
            }
            resultText.setVisibility(View.VISIBLE);
            gambleButton.setAlpha(1);
            gambleButton.setEnabled(true);
            gambleButton.setClickable(true);
            Animation pulse = AnimationUtils.loadAnimation(MainActivity.this, R.anim.pulse);
            resultText.startAnimation(pulse);

            if (winner == selected) {
                mSoundPool.play(winSoundId,LEFT_VOLUME,RIGHT_VOLUME,PRIORITY,NO_LOOP,NORMAL_PLAY_RATE);
            } else {
                mSoundPool.play(loseSoundId,LEFT_VOLUME,RIGHT_VOLUME,PRIORITY,NO_LOOP,NORMAL_PLAY_RATE);
            }




            //move activity
            super.onPostExecute(result);
        }
    }

    private class BTAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();
            bt = (BluetoothDevice) pairedDevices.toArray()[selectedDevice];

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Connecting to Device " + bt.getName() + "\nPlease Wait");
            progressDialog.show();
        }

        protected Void doInBackground(Void... args) {


            try {
                mmSocket = bt.createInsecureRfcommSocketToServiceRecord(BTMODULEUUID);
                mmSocket.connect();
                Thread.sleep(1500);
            }
            catch (Exception e)
            {
                finish();
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            super.onPostExecute(result);
        }
    }
}
