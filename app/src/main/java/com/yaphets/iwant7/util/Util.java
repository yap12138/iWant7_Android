package com.yaphets.iwant7.util;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Yaphets on 2017/8/31.
 */
public class Util {
    public static int BlockSize;

    public static int Edge_Up;
    public static int Edge_Down;
    public static int Edge_Left;
    public static int Edge_Right;

    public static final int Per_Speed = 40;
    public static final int Ful_Speed = 320;

    public static final Object Lock = new Object();

    public static String getNumCanonicalPath(String num) {
        return "number" + num + ".png";
    }

    public static String getScoreCanonicalPath(int score) {
        return "changedaddscores" + score + ".png";
    }

    public static int[] getResourceWidthHeight(Resources resources, int res){
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, res, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth,options.outHeight};
    }

    public static int[] getAssetsWidthHeight(AssetManager assetManager, String path){
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        InputStream inStream = null;
        try {
            inStream = assetManager.open(path);
            Bitmap bitmap = BitmapFactory.decodeStream(inStream, null, options); // 此时返回的bitmap为null
        } catch (IOException e) {
            e.printStackTrace();
        }
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth,options.outHeight};
    }

    public static Bitmap getAssetsPic(AssetManager assetManager, String path) {
        InputStream inStream = null;
        try {
            inStream = assetManager.open(path);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Bitmap bitmap = BitmapFactory.decodeStream(inStream);
            return bitmap;
        }
    }
}
