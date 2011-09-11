
package com.offbye.chinatvguide.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;

public class AssetUtil {
    public static Bitmap getImageFromAssetFile(Context context, String fileName) {
        Bitmap image = null;
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            Log.d("AssertUtil", e.getMessage());
        }
        return image;
    }

}
