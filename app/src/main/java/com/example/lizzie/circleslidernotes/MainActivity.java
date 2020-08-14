package com.example.lizzie.circleslidernotes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.billthefarmer.mididriver.MidiDriver;
import org.w3c.dom.Text;

import java.math.BigInteger;

import ru.bullyboo.view.CircleSeekBar;

public class MainActivity extends AppCompatActivity implements MidiDriver.OnMidiStartListener, View.OnTouchListener{

    private CircleSeekBar mCircleSeekBar;
    private Button mPlayButton;
    private byte mCurrentNote, mNoteOn = (byte) 0x90, mNoteOff = (byte) 0x80, mMaxVelocity = (byte) 0x7F, mMinVelocity = (byte) 0x7F;
    private MidiDriver mMidiDriver;
    private int mNumOfNotes = 88;
    private byte[] event;
    private byte[] notes = new byte[mNumOfNotes];
    private String[] noteNameMatches = new String[mNumOfNotes];
    private String[] noteNames = new String[12];
    private int[] config;
    private int shift = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCircleSeekBar = findViewById(R.id.CircleSeekBar);
        mPlayButton = findViewById(R.id.playButton);
        mMidiDriver = new MidiDriver();

        mPlayButton.setOnTouchListener(this);
        mMidiDriver.setOnMidiStartListener(this);
        mCircleSeekBar.setOnValueChangedListener(new CircleSeekBar.OnValueChangedListener(){
            public void onValueChanged(int i){
                mPlayButton.setText(noteNameMatches[mCircleSeekBar.getValue()]);
            }
        });
        mCircleSeekBar.setMaxValue(mNumOfNotes - 1);

        noteNames[0] = "A";
        noteNames[1] = "Bb";
        noteNames[2] = "B";
        noteNames[3] = "C";
        noteNames[4] = "C#";
        noteNames[5] = "D";
        noteNames[6] = "Eb";
        noteNames[7] = "E";
        noteNames[8] = "F";
        noteNames[9] = "F#";
        noteNames[10] = "G";
        noteNames[11] = "G#";

        int count = 0;
        for(int i = 0; i < notes.length; i++){
            int num = i + shift;
            int hex = Integer.parseInt("" + num);
            notes[i] = (byte) hex;
            noteNameMatches[i] = noteNames[count];
            count++;
            if(count == 12){
                count = 0;
            }
        }

        mCurrentNote = notes[27];
        mCircleSeekBar.setValue(27);
        mPlayButton.setText(noteNameMatches[27]);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMidiDriver.start();

        // Get the configuration.
        config = mMidiDriver.config();

        // Print out the details.
        Log.d(this.getClass().getName(), "maxVoices: " + config[0]);
        Log.d(this.getClass().getName(), "numChannels: " + config[1]);
        Log.d(this.getClass().getName(), "sampleRate: " + config[2]);
        Log.d(this.getClass().getName(), "mixBufferSize: " + config[3]);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMidiDriver.stop();
    }

    @Override
    public void onMidiStart() {
        Log.d(this.getClass().getName(), "onMidiStart()");
    }

    private void playNote() {

        // Construct a note ON message for the middle C at maximum velocity on channel 1:
        event = new byte[3];
        event[0] = mNoteOn;  // 0x90 = note On, 0x00 = channel 1
        event[1] = mCurrentNote;  // 0x3C = middle C
        event[2] = mMaxVelocity;  // 0x7F = the maximum velocity (127)

        // Internally this just calls write() and can be considered obsoleted:
        //midiDriver.queueEvent(event);

        // Send the MIDI event to the synthesizer.
        mMidiDriver.write(event);

    }

    private void stopNote() {

        // Construct a note OFF message for the middle C at minimum velocity on channel 1:
        event = new byte[3];
        event[0] = mNoteOff;  // 0x80 = note Off, 0x00 = channel 1
        event[1] = mCurrentNote;  // 0x3C = middle C
        event[2] = mMinVelocity;  // 0x00 = the minimum velocity (0)

        // Send the MIDI event to the synthesizer.
        mMidiDriver.write(event);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Log.d(this.getClass().getName(), "Motion event: " + event);

        if (v.getId() == R.id.playButton) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.d(this.getClass().getName(), "MotionEvent.ACTION_DOWN");
                mCurrentNote = notes[mCircleSeekBar.getValue()];
                playNote();
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Log.d(this.getClass().getName(), "MotionEvent.ACTION_UP");
                mCurrentNote = notes[mCircleSeekBar.getValue()];
                stopNote();
            }
        }

        return false;
    }

}
