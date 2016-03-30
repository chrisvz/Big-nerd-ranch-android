package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.UUID;

/**
 * Created by Chris on 3/30/2016.
 */
public class DetailDisplayActivity extends SingleFragmentActivity {

    private UUID uuid;


    public static Intent getIntent(Context context,UUID uuid) {
        Intent intent = new Intent(context,DetailDisplayActivity.class);
        intent.putExtra("UUID",uuid);
        return intent;
    }

    @Override
    protected Fragment createFragment() {

        uuid = (UUID)getIntent().getSerializableExtra("UUID");
        return DetailDisplayFragment.newInstance(uuid);
    }
}
