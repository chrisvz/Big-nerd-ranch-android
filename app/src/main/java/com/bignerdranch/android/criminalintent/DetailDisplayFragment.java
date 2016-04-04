package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;
import java.util.UUID;

/**
 * Created by Chris on 3/30/2016.
 */
public class DetailDisplayFragment extends Fragment {

    private ImageView imageView;
    private Crime crime;
    private UUID uuid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmet_detail_display,container,false);
        imageView = (ImageView)v.findViewById(R.id.fullImage_imageView);
        loadZoomedImage(uuid);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uuid =(UUID)getArguments().getSerializable("UUID");

        CrimeLab crimeLab = CrimeLab.get(getActivity());
        crime = crimeLab.getCrime(uuid);
    }

    public static DetailDisplayFragment newInstance(UUID uuid) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("UUID",uuid);
        DetailDisplayFragment detailDisplayFragment = new DetailDisplayFragment();
        detailDisplayFragment.setArguments(bundle);
        return detailDisplayFragment;
    }


    public void loadZoomedImage(UUID crimeId) {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        Crime crime = crimeLab.getCrime(crimeId);
        File path =crimeLab.getPhotoFile(crime);


        Glide
                .with(getActivity())
                .load(path)
                 .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .listener(new RequestListener<File, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        e.printStackTrace();
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        if (target.getRequest().isComplete())
//                            Log.d("DEBUG", "done bitch");
//                        return false;
//                    }
//                })

                .into(imageView)
        .getSize(new SizeReadyCallback() {
            @Override
            public void onSizeReady(int width, int height) {
                Log.d("DEBUG",width +""+height);
            }
        });

        ;

    }

}


