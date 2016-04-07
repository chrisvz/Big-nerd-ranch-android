package com.bignerdranch.android.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Chris on 3/28/2016.
 */
public class PictureUtils {



    public static Bitmap getScaledBitmap(String path,Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaledBitmap(path,size.x,size.y);
    }


    public static Bitmap getScaledBitmap(String path,int destWidth,int destHeight){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options = new BitmapFactory.Options();

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > destHeight || width > destWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > destHeight
                    && (halfWidth / inSampleSize) > destWidth) {
                inSampleSize *= 2;
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //Read in and create the final bitmap
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path,options);
    }
}
