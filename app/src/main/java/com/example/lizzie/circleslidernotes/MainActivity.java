package com.example.lizzie.circleslidernotes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import ru.bullyboo.*;
import ru.bullyboo.view.CircleSeekBar;

public class MainActivity extends AppCompatActivity {

    CircleSeekBar mCircleSeekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCircleSeekBar = (CircleSeekBar) findViewById(R.id.CircleSeekBar);
        mCircleSeekBar.setMaxValue(88);
    }
}
