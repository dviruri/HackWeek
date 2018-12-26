package com.microfocus.gamblebuddy;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new SplashAsyncTask().execute();
    }

    private class SplashAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        protected Void doInBackground(Void... args) {


            try {
                Thread.sleep(2000);
            }
            catch (Exception e)
            {
                finish();
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            Intent myIntent = new Intent(SplashActivity.this, BTActivity.class);
            SplashActivity.this.startActivity(myIntent);
        }
    }
}
