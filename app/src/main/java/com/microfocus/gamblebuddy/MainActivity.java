package com.microfocus.gamblebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

private int selected = -1;
private Button[] buttons ;
private int[] colors;
private String bt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        bt = intent.getStringExtra("BT"); //if it's a string you stored.

        setContentView(R.layout.activity_main);

        buttons = new Button[] {(Button)findViewById(R.id.greenButton),(Button)findViewById(R.id.redButton)};
        colors = new int[] {getResources().getColor(R.color.green),getResources().getColor(R.color.red)};

        TextView tv = (TextView)findViewById(R.id.device);
        tv.setText("Connected To " + bt);

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
}
