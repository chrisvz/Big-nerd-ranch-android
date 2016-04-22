package com.bignerdranch.android.beatbox;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 4/22/2016.
 */
public class BeatBox {

    private static final String TAG = "BeatBox";
    private static final String SOUNDS_FOLDER = "sample_sounds";
    private List<Sound> mSounds = new ArrayList<>();

    private AssetManager mAssets;

    public BeatBox(Context context){
        mAssets = context.getAssets();
        loadSounds();
    }

    private void loadSounds() {
        String[] soundsNames;
        try{
            soundsNames = mAssets.list(SOUNDS_FOLDER);
            Log.i(TAG,"Found "+soundsNames.length+ " sounds");
        }catch (IOException ioe){
            Log.e(TAG,"Could not list assets",ioe);
             return;
        }

        for(String filename: soundsNames) {
            String assetPath = SOUNDS_FOLDER + "/"+filename;
            Sound sound = new Sound(assetPath);
            mSounds.add(sound);
        }
    }

    public List<Sound> getSounds() {
        return mSounds;
    }
}
