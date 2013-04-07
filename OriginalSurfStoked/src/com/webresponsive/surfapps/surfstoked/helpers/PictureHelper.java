package com.webresponsive.surfapps.surfstoked.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

/**
 * Picture Helper class.
 * 
 * Contains methods to create String picture files from bitmap images.
 */
public class PictureHelper
{
    public static final String PRE_PICTURE_NAME = "SurfStoked";

    public static final String POST_PICTURE_NAME = ".jpg";

    private static final float PICTURE_MAX_SIZE = 600;

    /**
     * Creating picture files from a list of bitmaps.
     * 
     * Stores the pictures on the memory card.
     */
    public static String[] createPictureFiles(ArrayList<Bitmap> images)
    {
        String[] fileNames = new String[images.size()];
        String fileName;
        FileOutputStream out;
        try
        {
            for (int i = 0; i < images.size(); i++)
            {
                fileName = Environment.getExternalStorageDirectory() + File.separator + PRE_PICTURE_NAME + (i + 1) + POST_PICTURE_NAME;
                out = new FileOutputStream(fileName);

                Bitmap scaledBitmap = scaleBitmap(images.get(i));
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);

                fileNames[i] = fileName;
            }
        } catch (Exception e)
        {
            fileNames = new String[] {};
            e.printStackTrace();
        }

        return fileNames;
    }

    /**
     * Resize a bitmap to the maximum width/height.
     */
    private static Bitmap scaleBitmap(Bitmap bitmap)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleBitmap = 1;
        float maxSize = PICTURE_MAX_SIZE;
        if (width > height)
        {
            scaleBitmap = (float) maxSize / width;
        } else
        {
            scaleBitmap = (float) maxSize / height;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scaleBitmap, scaleBitmap);

        // recreate the new Bitmap and set it back
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        return resizedBitmap;
    }
    
    /**
     * Create a bitmap from the file given.
     * 
     * This is used when returning from taking a picture with the camera.
     */
    public static Bitmap decodeFile(File file)
    {
        try
        {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, o);

            final int REQUIRED_SIZE = 800;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true)
            {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            return BitmapFactory.decodeStream(new FileInputStream(file), null, o2);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
